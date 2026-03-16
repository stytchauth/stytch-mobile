# Plan: Callback Extension APIs

## What We're Building

Every `suspend fun` on a public client interface gets a non-suspend twin. The twin takes the
same parameters, plus `onSuccess` / `onFailure` lambdas, and returns a `Job` the caller can
cancel. A coroutine is launched internally using `Dispatchers.Default`.

```kotlin
// Existing (suspend)
suspend fun authenticate(request: IOTPsAuthenticateParameters): OTPsAuthenticateResponse

// Generated (callback)
fun authenticate(
    request: IOTPsAuthenticateParameters,
    onSuccess: (OTPsAuthenticateResponse) -> Unit,
    onFailure: (Throwable) -> Unit,
): Job
```

`onSuccess`/`onFailure` is preferred over `Result<T>` because these extensions are primarily
for Java callers — the majority of Kotlin developers will use the suspend APIs directly.

All callbacks fire on `Dispatchers.Default`. No `CoroutineScope` parameter is exposed; callers
control lifecycle via the returned `Job`.

---

## Targets

**Platforms:** Android and JVM only. JS already has Promise-based suspend interop, and iOS
already has SKIE-generated `async`/`await` wrappers. Shipping callbacks to those platforms
adds dead code and build complexity.

**Which interfaces:** Any interface annotated with a new `@StytchApi` marker annotation. This
decouples callback generation from `@JsExport`, which already marks data/model interfaces
that should not get callbacks. The annotation-application pass is small (roughly 10-15
interfaces across consumer + B2B) and done once.

**Nested sub-clients:** `SmsOtpClient`, `EmailMagicLinksClient`, and similar sub-client
interfaces will be annotated and processed identically. No special casing needed.

**Top-level clients:** `StytchConsumer` and `StytchB2B` have their own suspend methods
(`authenticate`, `getPKCECodePair`) and will be annotated too.

---

## Code Generation

### New annotation

A `@StytchApi` annotation lives in a new file in `commonMain` of `consumer-headless` (and
`b2b-headless`):

```kotlin
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class StytchApi
```

It is source-retained because the processor only needs it at KSP time.

### New KSP processor

`StytchCallbackProcessor` is added to `buildSrc` alongside the existing `StytchDtoProcessor`.
It uses the same KSP + KotlinPoet stack, but instead of writing through KSP's `CodeGenerator`
(which forces output to `build/generated/ksp/`), it writes directly to a file path via
`FileSpec.writeTo(File)`. The output path is passed as a KSP processor option from the
build file:

```kotlin
// consumer-headless/build.gradle.kts
ksp {
    arg("callbackOutputDir", layout.buildDirectory.dir("generated/callbacks").get().asFile.absolutePath)
}
```

The processor:
1. Finds every `KSClassDeclaration` annotated with `@StytchApi`.
2. For each `suspend fun` declared on that interface, emits one extension function.
3. Groups extensions by source interface into one generated file per interface:
   `<InterfaceName>Callbacks.kt` in the same package as the interface.
4. Registers a second `SymbolProcessorProvider` (`StytchCallbackProcessorProvider`) in
   `buildSrc`'s service file alongside the existing provider.

### Generated output shape

```kotlin
// build/generated/callbacks/.../consumer/otp/OtpClientCallbacks.kt
package com.stytch.sdk.consumer.otp

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

public fun OtpClient.authenticate(
    request: IOTPsAuthenticateParameters,
    onSuccess: (OTPsAuthenticateResponse) -> Unit,
    onFailure: (Throwable) -> Unit,
): Job = CoroutineScope(Dispatchers.Default).launch {
    try {
        onSuccess(authenticate(request))
    } catch (t: Throwable) {
        onFailure(t)
    }
}
```

`build/generated/callbacks/` is **not** added to `consumer-headless`'s own source sets. The
base artifact never compiles the callback files.

---

## Separate Artifact Architecture

### The core challenge

KSP processes source files in the current compilation unit. A standalone
`consumer-headless-extensions` module that depends on `consumer-headless` cannot use
`resolver.getSymbolsWithAnnotation("@StytchApi")` to find interfaces in the dependency —
those are compiled `.class` files, not source. This is why the processor runs inside the
base module but writes to a directory the base module does not compile.

### Module structure

The callback processor runs as part of `consumer-headless`'s existing KSP pass. It writes to:

```
source/sdks/sdk/consumer-headless/build/generated/callbacks/commonMain/kotlin/
```

A new `consumer-headless-extensions` module adds that directory as its only `commonMain`
source root and declares `consumer-headless` as an `api` dependency:

```kotlin
// sdk/consumer-headless-extensions/build.gradle.kts
commonMain {
    kotlin.srcDir(
        project(":sdk:consumer-headless").layout.buildDirectory.dir(
            "generated/callbacks/commonMain/kotlin"
        )
    )
    dependencies {
        api(project(":sdk:consumer-headless"))
        implementation(libs.kotlinx.coroutines.core)
    }
}
```

The extensions module targets Android and JVM only — no iOS frameworks, no JS output, no
SKIE. Build is simple. Same structure for `b2b-headless-extensions`.

Task ordering: the extensions module's compile tasks `dependsOn` the base module's
`kspCommonMainKotlinMetadata` task (the same pattern used in the base module for
`openApiGenerate`).

### Published artifacts

| Artifact | Contains | Targets |
|---|---|---|
| `com.stytch.sdk:consumer-headless` | All auth logic, suspend APIs | Android, iOS, JVM, JS |
| `com.stytch.sdk:consumer-headless-extensions` | Callback extensions only | Android, JVM |
| `com.stytch.sdk:b2b-headless` | B2B auth logic, suspend APIs | Android, iOS, JVM, JS |
| `com.stytch.sdk:b2b-headless-extensions` | B2B callback extensions | Android, JVM |

Developers using coroutines depend only on `*-headless`. Developers wanting callbacks add
`*-headless-extensions`, which transitively pulls in the base artifact.

---

## Scope of Work

### Phase 1 — Annotation + processor

1. Define `@StytchApi` annotation in `consumer-headless` (and `b2b-headless`) `commonMain`.
2. Apply `@StytchApi` to all public client interfaces in both SDKs (~10-15 interfaces).
3. Implement `StytchCallbackProcessor` + `StytchCallbackProcessorProvider` in `buildSrc`.
4. Wire the processor into both base module `build.gradle.kts` files with the
   `callbackOutputDir` KSP option; confirm the `generated/callbacks/` directory is populated
   correctly after a build.

### Phase 2 — Extensions modules

5. Add `:sdk:consumer-headless-extensions` and `:sdk:b2b-headless-extensions` to
   `source/sdks/settings.gradle.kts`.
6. Write `build.gradle.kts` for each (Android + JVM targets, srcDir pointing at base module's
   `generated/callbacks/`, `maven-publish`).
7. Confirm the extensions compile cleanly and the generated callback functions are callable
   from a test.

### Phase 3 — Tests + build plumbing

8. Add `jvmTest` smoke tests in the extensions modules: mock the underlying suspend method,
   call the callback overload, assert `onSuccess`/`onFailure` fires and the returned `Job`
   can be cancelled.
9. Update `./build` script to include the new modules in the Android and JVM build paths.
10. Update `README` / release notes with the new artifact coordinates.

---

## Notes / Constraints

- The `@StytchApi` annotation is source-retained, so it does not appear in compiled
  artifacts and adds zero runtime overhead.
- The extensions modules have no source files of their own — they are purely a compilation
  wrapper around generated output. If the processor produces no output (e.g., the annotation
  was not applied), the extensions module produces an empty artifact, which is harmless.
- `CancellationException` is a subclass of `Throwable`, so cancellation will route to
  `onFailure`. This is consistent with how the old SDK behaved and is acceptable; the `Job`
  return value gives callers a clean cancellation path that avoids the callback entirely.
- The extensions modules do not need SKIE, openapi generation, or the `@NetworkModel` KSP
  processor — only the callback processor output and `kotlinx-coroutines-core`.
