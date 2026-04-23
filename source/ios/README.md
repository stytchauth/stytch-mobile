# Stytch iOS SDK

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://github.com/stytchauth/stytch-mobile/blob/main/LICENSE)
![iOS](https://img.shields.io/badge/iOS-15.0%2B-000000?logo=apple&logoColor=white)

Headless authentication SDK for iOS, built on a shared [Kotlin Multiplatform](https://www.jetbrains.com/kotlin-multiplatform/) core. Bring your own UI — Stytch handles the auth.

---

## Requirements

- iOS **15.0** or later
- Xcode 15 or later

---

## Consumer vs. B2B: What's Right for You?

| | Consumer SDK | B2B SDK |
|---|---|---|
| **Use case** | B2C apps — end users authenticate directly | B2B SaaS — members authenticate within Organizations |
| **Auth methods** | OTP, magic links, passwords, OAuth, passkeys, biometrics, TOTP, crypto wallets | OTP, magic links, passwords, OAuth, SSO (SAML/OIDC), TOTP |
| **Additional features** | Session + user management, DFP | Organizations, members, RBAC, SCIM, discovery flows, recovery codes |
| **Package** | `StytchConsumerSDK` | `StytchB2BSDK` |

If you're building a consumer-facing app, you want the **Consumer SDK**. If you're building a B2B SaaS product where your customers belong to organizations, you want the **B2B SDK**. For a deeper comparison, see the [docs](https://stytch.com/docs).

---

## Installation

**In Xcode:**

1. Go to **File → Add Package Dependencies...**
2. Enter the repository URL: `https://github.com/stytchauth/stytch-ios-sdk`
3. Select version **1.0.0** or later
4. Add the product you need to your target:
   - `StytchConsumerSDK` — Consumer apps
   - `StytchB2BSDK` — B2B apps

**In `Package.swift`:**

```swift
dependencies: [
    .package(url: "https://github.com/stytchauth/stytch-ios-sdk", from: "1.0.0"),
],
targets: [
    .target(
        name: "YourTarget",
        dependencies: [
            // Pick one:
            .product(name: "StytchConsumerSDK", package: "stytch-ios-sdk"),
            // .product(name: "StytchB2BSDK", package: "stytch-ios-sdk"),
        ]
    ),
]
```

---

## Quick Start

The examples below use the **Consumer SDK** with an SMS OTP login flow. The **B2B SDK** follows the same pattern — swap in `StytchB2BSDK` and use the equivalent B2B endpoints.

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

---

## Support

If you have questions, found a bug or want help troubleshooting, join us in [Slack](https://stytch.com/docs/resources/support/overview) or email [support@stytch.com](mailto:support@stytch.com).

If you've found a security vulnerability, please follow our [responsible disclosure instructions](https://stytch.com/docs/resources/security-and-trust/security#:~:text=Responsible%20disclosure%20program).
