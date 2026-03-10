# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build

```bash
./build [platform]   # platform: android | ios | rn | shared (omit for all)
```

Full build order: `StytchSwiftUtils.xcframework` → publish `stytch-multiplatform-shared` to mavenLocal → build consumer SDK for target platform → copy artifacts to `stytch-ios/` and `examples/`.

- `shared` — builds only SwiftUtils + shared SDK (no consumer SDK)
- `android` / `ios` / `rn` — builds shared first, then the specified platform
- omit — builds everything

To test manually, run the relevant app in `examples/` after building.

## Tests

Tests live in `jvmTest` source sets in both projects (not `commonTest` — that runs on every platform).

- **Frameworks**: `kotlin.test`, `MockK`, `kotlinx-coroutines-test`
- **Mocking strategy**: Mock at `ConsumerNetworkingClient.api` (the Ktorfit interface), not at the full client level. `ConsumerClientTest` is the base class for consumer unit tests — it stubs `networkingClient.request` to execute its lambda so API calls flow through normally.
- Use MockK for `expect class` instances (providers, encryption client, persistence) since they can't be subclassed.
- **Scope**: Pure logic unit tests only. Platform `actual` implementations are assumed correct.

Run tests: `./gradlew :sdk:consumer-headless:jvmTest` (or `:sdk:shared:jvmTest` for shared).

## Two-Project Architecture

Two separate KMP Gradle projects — the split exists because React Native requires Kotlin ≤ 2.1.0 but exporting `suspend` functions to JS requires Kotlin ≥ 2.3.0:

| Project | Kotlin | Role |
|---|---|---|
| `stytch-multiplatform-shared/` | 2.1.0 | Platform abstractions: networking, encryption, persistence, biometrics/passkeys/OAuth providers, DFP/CAPTCHA |
| `stytch-multiplatform/` | 2.3.0 | Consumer SDK: all auth modules (OTP, OAuth, Passwords, Magic Links, TOTP, Passkeys, Biometrics, Sessions, User, Crypto) |

`stytch-multiplatform` depends on `stytch-multiplatform-shared` via mavenLocal. Build shared first.

Both target: Android, iOS (iosX64/iosArm64/iosSimulatorArm64), JVM, JavaScript.

## Hybrid Interface Pattern for Testability

`expect class` types cannot be subclassed, so interfaces with an `I` prefix are defined in `commonMain` to enable mocking:

```kotlin
// commonMain
interface IBiometricsProvider { ... }
expect class BiometricsProvider : IBiometricsProvider { ... }

// Consumer client depends on the interface
class BiometricsClientImpl(private val biometricsProvider: IBiometricsProvider)
```

Applied to: `IBiometricsProvider`, `IPasskeyProvider`, `IOAuthProvider`. Consumer clients and `StytchClientConfigurationInternal` reference the `I`-prefixed interfaces, not the concrete `expect class`.

## Key Source Locations

**Shared SDK** (`stytch-multiplatform-shared/sdk/shared/src/commonMain/kotlin/com/stytch/sdk/`):
```
biometrics/    — IBiometricsProvider interface + types
passkeys/      — IPasskeyProvider interface + types
oauth/         — IOAuthProvider interface + types
dfp/           — DFPProvider, CAPTCHAProvider, DFPPAInterceptor
encryption/    — StytchEncryptionClient (expect)
persistence/   — StytchPlatformPersistenceClient (expect), StytchPersistenceClient (typed wrapper)
pkce/          — PKCEClient
networking/    — StytchNetworkingClient base, HTTP client factory, SharedAPI (bootstrap)
data/          — StytchClientConfiguration (expect + actuals), StytchClientConfigurationInternal,
                 KMPPlatformType, DeviceInfo, StytchDispatchers, EndpointOptions, BootstrapResponse
StytchClient.kt                    — base marker interface
StytchAuthenticationStateManager.kt — base session state interface
```

Platform-specific `actual` implementations live in `androidMain/`, `iosMain/`, `jvmMain/`, `jsMain/` in the same project.

**Consumer SDK** (`stytch-multiplatform/sdk/consumer-headless/src/commonMain/kotlin/com/stytch/sdk/consumer/`):
```
StytchConsumer.kt          — public interface + createStytchConsumer() factory + DefaultStytchConsumer
StytchConsumerAuthenticationStateManager.kt — session/user state management
biometrics/                — BiometricsClientImpl
passkeys/                  — PasskeysClientImpl
oauth/                     — OAuthClientImpl
passwords/                 — PasswordsClientImpl
otp/, totp/, magicLinks/   — auth method clients
session/, user/, crypto/   — session/user/crypto clients
networking/                — ConsumerNetworkingClient (extends StytchNetworkingClient), SdkExternalApi
```

All consumer code is `commonMain` only — no platform source sets except `StytchDispatcherFactory.{platform}.kt`.

## iOS Distribution (`stytch-ios/`)

A Swift Package at `stytch-ios/Package.swift` wraps the three xcframeworks for native iOS consumption:
- `StytchConsumerSDK.xcframework` — from `stytch-multiplatform`
- `StytchSharedSDK.xcframework` — from `stytch-multiplatform-shared`
- `StytchSwiftUtils.xcframework` — from `StytchSwiftUtils/`

The build script copies all three here. The package product is `StytchConsumerSDK` (target `StytchConsumerTarget`). The `Sources/StytchConsumerTarget/dummy.swift` stub is required by SPM.

## React Native Architecture

`react-native/consumer/` is the full RN npm package (`@stytch/react-native-consumer`):

```
src/
  NativeStytchBridge.ts   — TurboModule TypeScript spec
  contexts.tsx            — React contexts (StytchContext, StytchUserContext, etc.)
  hooks.tsx               — useStytch, useStytchUser, useStytchSession, etc.
  providers.tsx           — withStytch, StytchProvider, etc.
ios/
  StytchBridge.mm/.h      — iOS TurboModule implementation (ObjC, calls into shared xcframework)
android/
  StytchBridgeModule.kt   — Android TurboModule implementation
lib/
  consumer-headless.mjs   — KMP JS build output (copied here by ./build rn)
dist/                     — TypeScript compiled output (yarn build)
```

**Data flow**: JS code imports from `lib/consumer-headless.mjs` (KMP-compiled). The KMP JS `actual` classes call methods on a `StytchBridge` global JS object. `NativeStytchBridge.ts` exposes the native TurboModule as that global, bridging to `StytchBridge.mm` (iOS) or `StytchBridgeModule.kt` (Android). All complex types are encoded/decoded as JSON strings across the bridge.

The RN native bridges depend on the shared xcframework (iOS) / mavenLocal artifact (Android) from `stytch-multiplatform-shared`, not on `stytch-multiplatform`.

The demo app at `examples/rn/` uses `yarn add file:../../react-native/consumer` (updated automatically by `./build rn`).

## Code Generation Pipeline

1. **OpenAPI spec** (`src/commonMain/resources/openapi.yml`) → `openapi` plugin → Ktorfit HTTP interfaces + serializable network models
2. **`@NetworkModel` annotation** → custom KSP processor → public-facing DTO classes + `toNetworkModel()` extension functions
3. **`buildconfig` plugin** → `BuildConfig` with SDK name/version for User-Agent headers
4. **SKIE** → generates Swift-friendly wrappers around Kotlin/Native APIs (async/await, sealed classes, flows)

> **IMPORTANT:** When running any Gradle command in either project, include `--rerun-tasks` to ensure KSP/code-generation tasks run. Without it, Gradle may skip them as up-to-date.

## Known Issues / Notes
- `StytchSwiftUtils.xcframework` bridges Swift-only APIs (e.g., CryptoKit) to Kotlin via C interop — `StytchEncryptionClient` on iOS calls `StytchEncryptionManagerSwift` from this framework
- `ASAuthorizationController.delegate` is a weak ObjC ref — `OAuthProvider` stores active SIWA delegate/controller as instance properties to survive React Native app-inactive lifecycle events
- The next major piece of work is the B2B SDK — same structure as consumer, different API endpoints, no new native code needed (reuses all shared providers)
