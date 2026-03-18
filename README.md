# Stytch Mobile SDKs

[![Maven Central](https://img.shields.io/maven-central/v/com.stytch.sdk/consumer-headless?label=Maven%20Central&color=blue)](https://central.sonatype.com/artifact/com.stytch.sdk/consumer-headless)
[![npm (consumer)](https://img.shields.io/npm/v/@stytch/react-native-consumer?label=%40stytch%2Freact-native-consumer&color=red)](https://www.npmjs.com/package/@stytch/react-native-consumer)
[![npm (b2b)](https://img.shields.io/npm/v/@stytch/react-native-b2b?label=%40stytch%2Freact-native-b2b&color=red)](https://www.npmjs.com/package/@stytch/react-native-b2b)
[![CI](https://github.com/stytchauth/stytch-mobile/actions/workflows/qc.yml/badge.svg)](https://github.com/stytchauth/stytch-mobile/actions/workflows/qc.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
![Android/KMP](https://img.shields.io/badge/Android%2FKMP-API%2024%2B-3DDC84?logo=android&logoColor=white)
![iOS](https://img.shields.io/badge/iOS-15.0%2B-000000?logo=apple&logoColor=white)
![React Native](https://img.shields.io/badge/React%20Native-0.80%2B-61DAFB?logo=react&logoColor=white)

Headless authentication SDKs for Android, iOS, and React Native, built on a shared [Kotlin Multiplatform](https://www.jetbrains.com/kotlin-multiplatform/) core. Bring your own UI — Stytch handles the auth.

---

## Consumer vs. B2B: What's Right for You?

| | Consumer SDK | B2B SDK |
|---|---|---|
| **Use case** | B2C apps — end users authenticate directly | B2B SaaS — members authenticate within Organizations |
| **Auth methods** | OTP, magic links, passwords, OAuth, passkeys, biometrics, TOTP, crypto wallets | OTP, magic links, passwords, OAuth, SSO (SAML/OIDC), TOTP |
| **Additional features** | Session + user management, DFP | Organizations, members, RBAC, SCIM, discovery flows, recovery codes |
| **Packages** | `consumer-headless` · `StytchConsumerSDK` · `@stytch/react-native-consumer` | `b2b-headless` · `StytchB2BSDK` · `@stytch/react-native-b2b` |

If you're building a consumer-facing app, you want the **Consumer SDK**. If you're building a B2B SaaS product where your customers belong to organizations, you want the **B2B SDK**. For a deeper comparison, see the [docs](https://stytch.com/docs).

---

## Supported Platforms

| Platform | Minimum Version | Consumer | B2B |
|---|---|---|---|
| Android | API 24 (Android 7.0) | `com.stytch.sdk:consumer-headless` | `com.stytch.sdk:b2b-headless` |
| iOS | 15.0 | `StytchConsumerSDK` (SPM) | `StytchB2BSDK` (SPM) |
| React Native | 0.80.x | `@stytch/react-native-consumer` | `@stytch/react-native-b2b` |
| JVM / Desktop | — | `com.stytch.sdk:consumer-headless` | `com.stytch.sdk:b2b-headless` |

> **JVM/Desktop** support is functional but limited — platform-specific features such as biometrics, OAuth, and passkeys are not available outside of the mobile platforms.

---

## Installation

<details>
<summary><strong>Android</strong></summary>

&nbsp;

The SDK requires **Kotlin 2.3.0 or later**.

Make sure `mavenCentral()` is listed in your repository configuration:

```kotlin
// settings.gradle.kts
dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}
```

Then add the dependency to your module's `build.gradle.kts`:

**Consumer**
```kotlin
dependencies {
    implementation("com.stytch.sdk:consumer-headless:1.0.0")
}
```

**B2B**
```kotlin
dependencies {
    implementation("com.stytch.sdk:b2b-headless:1.0.0")
}
```

Prefer callback-style APIs over coroutines? See [Callback Extensions](#callback-extensions-androidjvm) — you can swap the dependency there and get `onSuccess`/`onFailure` overloads for every method.

</details>

<details>
<summary><strong>iOS</strong></summary>

&nbsp;

The iOS SDK is distributed as a Swift Package from [`stytchauth/stytch-ios`](https://github.com/stytchauth/stytch-ios).

**In Xcode:**

1. Go to **File → Add Package Dependencies...**
2. Enter the repository URL: `https://github.com/stytchauth/stytch-ios`
3. Select version **1.0.0** or later
4. Add the product you need to your target:
   - `StytchConsumerSDK` — Consumer apps
   - `StytchB2BSDK` — B2B apps

**In `Package.swift`:**

```swift
dependencies: [
    .package(url: "https://github.com/stytchauth/stytch-ios", from: "1.0.0"),
],
targets: [
    .target(
        name: "YourTarget",
        dependencies: [
            // Pick one:
            .product(name: "StytchConsumerSDK", package: "stytch-ios"),
            // .product(name: "StytchB2BSDK", package: "stytch-ios"),
        ]
    ),
]
```

</details>

<details>
<summary><strong>React Native</strong></summary>

&nbsp;

The React Native packages require React Native's [New Architecture (TurboModules)](https://reactnative.dev/docs/new-architecture-intro) and React Native **0.80.x** or later.

**Consumer**
```sh
npm install @stytch/react-native-consumer
# or
yarn add @stytch/react-native-consumer
```

**B2B**
```sh
npm install @stytch/react-native-b2b
# or
yarn add @stytch/react-native-b2b
```

</details>

---

## Quick Start

The examples below use the **Consumer SDK** with an SMS OTP login flow. The **B2B SDK** follows the same pattern — swap in `createStytchB2B` / `StytchB2BSDK` / `@stytch/react-native-b2b` and use the equivalent B2B endpoints.

<details>
<summary><strong>Android</strong></summary>

&nbsp;

**1. Initialize the client**

Create the client once — in your `Application` class or at the entry point of your auth flow. The client is a singleton; calling `createStytchConsumer` again with the same token returns the existing instance.

```kotlin
import com.stytch.sdk.consumer.createStytchConsumer
import com.stytch.sdk.data.StytchClientConfiguration

val stytch = createStytchConsumer(
    StytchClientConfiguration(
        context = applicationContext,
        publicToken = "public-token-live-xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx",
    )
)
```

**2. Observe authentication state**

```kotlin
import com.stytch.sdk.consumer.data.ConsumerAuthenticationState

lifecycleScope.launch {
    stytch.authenticationStateFlow.collect { state ->
        when (state) {
            is ConsumerAuthenticationState.Authenticated -> { /* user is logged in */ }
            is ConsumerAuthenticationState.Unauthenticated -> { /* show login UI */ }
            is ConsumerAuthenticationState.Loading -> { /* session is being restored */ }
        }
    }
}
```

**3. Send and verify an SMS OTP**

```kotlin
import com.stytch.sdk.consumer.networking.models.OTPsAuthenticateParameters
import com.stytch.sdk.consumer.networking.models.OTPsSMSLoginOrCreateParameters

// Send OTP — creates the user if they don't exist yet
val sendResponse = stytch.otp.sms.loginOrCreate(
    OTPsSMSLoginOrCreateParameters(phoneNumber = "+15551234567")
)
val methodId = sendResponse.methodId

// Verify the code the user entered
val authResponse = stytch.otp.authenticate(
    OTPsAuthenticateParameters(
        token = userEnteredCode,
        methodId = methodId,
        sessionDurationMinutes = 30,
    )
)
```

All SDK methods are `suspend` functions — call them from a coroutine scope (`viewModelScope`, `lifecycleScope`, etc.). Errors are thrown as `StytchError`.

</details>

<details>
<summary><strong>iOS</strong></summary>

&nbsp;

**1. Initialize the client**

```swift
import StytchConsumerSDK

let stytch = createStytchConsumer(
    configuration: .init(
        publicToken: "public-token-live-xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx"
    )
)
```

**2. Observe authentication state**

```swift
Task {
    for await state in stytch.authenticationStateFlow {
        switch onEnum(of: state) {
        case .authenticated(let s):
            print("Logged in: \(s.user)")
        case .unauthenticated:
            print("Not logged in")
        case .loading:
            print("Loading...")
        }
    }
}
```

**3. Send and verify an SMS OTP**

```swift
// Send OTP — creates the user if they don't exist yet
let sendParams: OTPsSMSLoginOrCreateParameters = .init(phoneNumber: "+15551234567")
let sendResponse = try await stytch.otp.sms.loginOrCreate(request: sendParams)
let methodId = sendResponse.methodId

// Verify the code the user entered
let authParams: OTPsAuthenticateParameters = .init(
    token: userEnteredCode,
    methodId: methodId,
    sessionDurationMinutes: 30
)
let authResponse = try await stytch.otp.authenticate(request: authParams)
```

All SDK methods are `async throws`. Errors are thrown as `StytchError`.

</details>

<details>
<summary><strong>React Native</strong></summary>

&nbsp;

**1. Initialize the client and wrap your app**

```tsx
import {
    createStytchConsumer,
    StytchClientConfiguration,
    StytchProvider,
} from '@stytch/react-native-consumer';

const stytch = createStytchConsumer(
    new StytchClientConfiguration("public-token-live-xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx")
);

export default function App() {
    return (
        <StytchProvider stytch={stytch}>
            <YourNavigator />
        </StytchProvider>
    );
}
```

**2. Access the client and state in components**

```tsx
import { useStytch, useStytchUser, useStytchSession } from '@stytch/react-native-consumer';

function ProfileHeader() {
    const user = useStytchUser();
    const session = useStytchSession();
    return (
        <Text>{user ? `Logged in as ${user.emails[0]?.email}` : 'Not logged in'}</Text>
    );
}
```

**3. Send and verify an SMS OTP**

```tsx
import { useStytch } from '@stytch/react-native-consumer';

function SmsOtpScreen() {
    const stytch = useStytch();
    const [methodId, setMethodId] = useState<string | null>(null);

    const sendOtp = async (phoneNumber: string) => {
        const response = await stytch.otp.sms.loginOrCreate({ phoneNumber });
        setMethodId(response.methodId);
    };

    const verifyOtp = async (code: string) => {
        await stytch.otp.authenticate({
            token: code,
            methodId: methodId!,
            sessionDurationMinutes: 30,
        });
    };

    // ...
}
```

</details>

---

## Callback Extensions (Android/JVM)

By default, all SDK methods are Kotlin `suspend` functions. If your project uses callback-style APIs — or you're calling the SDK from Java — the `*-headless-extensions` artifacts provide `onSuccess`/`onFailure` overloads for every method, returning a cancellable `Job`:

```kotlin
// Coroutine style (base SDK):
val result = stytch.otp.authenticate(params)

// Callback style (extensions):
val job = stytch.otp.authenticate(
    request = params,
    onSuccess = { response -> /* handle success */ },
    onFailure = { error -> /* handle error */ },
)
```

To use the callback extensions, swap the base dependency for the extensions artifact — it re-exports the base module, so no other changes are needed:

```kotlin
// Before:
implementation("com.stytch.sdk:consumer-headless:1.0.0")

// After:
implementation("com.stytch.sdk:consumer-headless-extensions:1.0.0")

// Same for B2B:
implementation("com.stytch.sdk:b2b-headless-extensions:1.0.0")
```

Callback extensions are available on Android and JVM only. iOS uses native `async/await` and React Native handles async natively in JavaScript.

---

## Support

If you have questions, found a bug or want help troubleshooting, join us in [Slack](https://stytch.com/docs/resources/support/overview) or email [support@stytch.com](mailto:support@stytch.com).

If you've found a security vulnerability, please follow our [responsible disclosure instructions](https://stytch.com/docs/resources/security-and-trust/security#:~:text=Responsible%20disclosure%20program).

## Contributing

We welcome contributions! Please see our [Contributing Guide](CONTRIBUTING.md) for details on how to submit issues, pull requests, and contribute to the project.

## Development

See [DEVELOPMENT.md](DEVELOPMENT.md)

## Security

If you discover a security vulnerability, please report it to us at `security@stytch.com`. See our [Security Policy](SECURITY.md) for more details.

## Code of Conduct

Everyone interacting in Stytch codebases, issue trackers, chat rooms and mailing lists is expected to follow the [code of conduct](CODE_OF_CONDUCT.md).
