# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build

```bash
./build [platform]   # platform: android | ios | rn (omit for all)
```

This script builds `StytchSwiftUtils.xcframework`, publishes `stytch-multiplatform-shared` to mavenLocal, then builds the consumer SDK for the target platform and copies artifacts to `examples/`.

To test manually, run the relevant app in `examples/` after building.

## Tests

No tests exist yet. The plan is:

- **Target**: `jvmTest` source sets in both projects (not `commonTest` — that would run on every platform)
- **Frameworks**: `kotlin.test`, `MockK`, `kotlinx-coroutines-test`
- **Mocking strategy**: Mock at the `ConsumerNetworkingClient` level; do not test real HTTP. Use MockK for `expect class` instances (providers, encryption client, persistence) since they can't be subclassed.
- **Scope**: Pure logic unit tests only. Platform `actual` implementations are assumed correct for now.

## Two-Project Architecture

The repo contains two separate KMP Gradle projects — this split exists because React Native requires Kotlin ≤ 2.1.0 but exporting `suspend` functions to JS requires Kotlin ≥ 2.3.0:

| Project | Kotlin | Role |
|---|---|---|
| `stytch-multiplatform-shared/` | 2.1.0 | Platform abstractions: networking, encryption, persistence, biometrics/passkeys/OAuth providers |
| `stytch-multiplatform/` | 2.3.0 | Consumer SDK: all auth modules (OTP, OAuth, Passwords, Magic Links, TOTP, Passkeys, Biometrics, Sessions, User) |

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

**Shared SDK** (`stytch-multiplatform-shared`):
```
sdk/shared/src/commonMain/kotlin/com/stytch/sdk/
  biometrics/    — IBiometricsProvider interface + types
  passkeys/      — IPasskeyProvider interface + types
  oauth/         — IOAuthProvider interface + types
  encryption/    — StytchEncryptionClient (expect)
  persistence/   — StytchPlatformPersistenceClient (expect), StytchPersistenceClient (typed wrapper)
  networking/    — StytchNetworkingClient base, HTTP client factory, auth plugin
  data/          — StytchClientConfiguration (expect), DeviceInfo, StytchDispatchers, AuthenticationState
```

Platform-specific `actual` implementations live in `androidMain/`, `iosMain/`, `jvmMain/`, `jsMain/` in the same project.

**Consumer SDK** (`stytch-multiplatform`):
```
sdk/consumer-headless/src/commonMain/kotlin/com/stytch/sdk/consumer/
  StytchConsumer.kt          — entry point: createStytchConsumer()
  biometrics/                — BiometricsClientImpl
  passkeys/                  — PasskeysClientImpl
  oauth/                     — OAuthClientImpl
  passwords/                 — PasswordsClientImpl
  otp/, totp/, magicLinks/   — auth method clients
  session/, user/, crypto/   — session/user/crypto clients
  networking/                — ConsumerNetworkingClient (extends StytchNetworkingClient)
```

All consumer code is `commonMain` only — no platform source sets here.

## Code Generation Pipeline

1. **OpenAPI spec** (`src/commonMain/resources/openapi.yml`) → Ktorfit generates Ktor HTTP interfaces + serializable models
2. **`@NetworkModel` annotation** → custom KSP processor generates public-facing DTO classes + `toNetworkModel()` extension functions
3. **`buildconfig` plugin** → generates `BuildConfig` with SDK name/version for User-Agent headers

> **IMPORTANT:** When running any Gradle command in `stytch-multiplatform-shared`, you MUST include the `--rerun-tasks` flag to ensure all KSP/code-generation tasks execute. Without it, Gradle may skip them as up-to-date.

## React Native Architecture

JS `actual` classes call methods on a `StytchBridge` JS object (declared as Kotlin `external`). The RN TurboModule implements this bridge, routing calls across the native bridge to platform-specific methods. The RN artifact depends on the published Android/iOS artifacts from `stytch-multiplatform-shared`.

## Known Issues / Big TODOs
- `StytchSwiftUtils.xcframework` bridges Swift-only APIs (e.g., CryptoKit) to Kotlin via C interop — `StytchEncryptionClient` on iOS calls `StytchEncryptionManagerSwift` from this framework
