# iOS Quickstart

This guide walks you through adding Stytch authentication to an iOS app using `StytchConsumerSDK` or `StytchB2BSDK`. Both are distributed as a Swift Package from [`stytchauth/stytch-ios`](https://github.com/stytchauth/stytch-ios) and require **iOS 15.0+**.

---

## 1. Install the SDK

**In Xcode:**

1. Go to **File → Add Package Dependencies...**
2. Enter: `https://github.com/stytchauth/stytch-ios`
3. Select version **1.0.0** or later
4. Add the product that matches your project type:
   - `StytchConsumerSDK` — Consumer (B2C) apps
   - `StytchB2BSDK` — B2B apps (organizations/members)

**In `Package.swift`:**

```swift
dependencies: [
    .package(url: "https://github.com/stytchauth/stytch-ios", from: "1.0.0"),
],
targets: [
    .target(
        name: "YourTarget",
        dependencies: [
            .product(name: "StytchConsumerSDK", package: "stytch-ios"),
            // or: .product(name: "StytchB2BSDK", package: "stytch-ios"),
        ]
    ),
]
```

**Required linker flag:** In your target's **Build Settings**, add `-ObjC` to **Other Linker Flags**.

---

## 2. Initialize the Client

Create the client once — at app startup or at the entry point of your auth flow. The client is a singleton; calling `createStytchConsumer` again returns the same instance.

```swift
import StytchConsumerSDK

// Create once and store — e.g., in your App or a top-level singleton
let stytch = createStytchConsumer(
    configuration: .init(
        publicToken: "public-token-live-xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx"
    )
)
```

For B2B apps, import `StytchB2BSDK` and use `createStytchB2B(configuration:)` instead.

Your public token is in the [Stytch Dashboard](https://stytch.com/dashboard/api-keys). Make sure you've also enabled the auth methods you want to use under **SDK Configuration**.

---

## 3. Observe Authentication State

The SDK exposes an `authenticationStateFlow` — an async sequence that emits whenever authentication state changes. Use `for await` to observe it:

```swift
Task {
    for await state in stytch.authenticationStateFlow {
        switch onEnum(of: state) {
        case .authenticated(let s):
            let user = s.user
            let session = s.session
            let sessionToken = s.sessionToken
            // Navigate to your authenticated UI
        case .unauthenticated:
            // Show your login UI
        case .loading:
            // SDK is restoring a persisted session — show a splash screen
        }
    }
}
```

To read the current state synchronously (e.g., during a navigation check), use `stytch.authenticationStateFlow.value`.

---

## 4. Auth Methods

All SDK methods are `async throws`. Call them from a `Task` or an `async` context and handle errors with `do/catch`. Errors are thrown as `StytchError`.

### SMS OTP

```swift
import StytchConsumerSDK

// Step 1: Send OTP
var methodId: String = ""

Task {
    do {
        let params: OTPsSMSLoginOrCreateParameters = .init(phoneNumber: "+15551234567")
        let response = try await stytch.otp.sms.loginOrCreate(request: params)
        methodId = response.methodId
    } catch {
        // Handle error
    }
}

// Step 2: Verify the code
Task {
    do {
        let params: OTPsAuthenticateParameters = .init(
            token: userEnteredCode,
            methodId: methodId,
            sessionDurationMinutes: 30
        )
        try await stytch.otp.authenticate(request: params)
    } catch {
        // Handle error
    }
}
```

The SDK also supports email OTP (`stytch.otp.email.loginOrCreate(request:)`) and WhatsApp OTP (`stytch.otp.whatsapp.loginOrCreate(request:)`).

### Email Magic Links

Magic links require deeplink handling — see [Step 5](#5-handle-deeplinks).

```swift
Task {
    do {
        let params: MagicLinksByEmailLoginOrCreateParameters = .init(
            email: "user@example.com",
            loginMagicLinkUrl: "myapp://auth",
            signupMagicLinkUrl: "myapp://auth"
        )
        try await stytch.magicLinks.email.loginOrCreate(request: params)
        // Tell the user to check their inbox
    } catch {
        // Handle error
    }
}
```

### OAuth (Browser-Based)

OAuth requires deeplink handling — see [Step 5](#5-handle-deeplinks).

`start()` opens an `ASWebAuthenticationSession`, completes the OAuth flow, and returns the authenticated session — all in one call:

```swift
import StytchConsumerSDK

Task {
    do {
        let params: OAuthStartParameters = .init(
            loginRedirectUrl: "myapp://oauth",
            signupRedirectUrl: "myapp://oauth",
            sessionDurationMinutes: 30,
            oauthPresentationContextProvider: self  // UIViewController or window scene
        )
        let response = try await stytch.oauth.google.start(startParameters: params)
    } catch {
        // Handle error
    }
}
```

Replace `.google` with any supported provider: `.apple`, `.github`, `.microsoft`, `.facebook`, `.amazon`, `.slack`, and more.

For Sign In With Apple, no `oauthPresentationContextProvider` is needed — Apple's native sheet is presented automatically:

```swift
let params: OAuthStartParameters = .init()
let response = try await stytch.oauth.apple.start(startParameters: params)
```

### Passwords

```swift
// Check password strength before creating or resetting
let strengthParams: PasswordsStrengthCheckParameters = .init(email: "user@example.com", password: "mypassword")
let strengthCheck = try await stytch.passwords.strengthCheck(request: strengthParams)

// Create a new password user
let createParams: PasswordsCreateParameters = .init(email: "user@example.com", password: "mypassword")
try await stytch.passwords.create(request: createParams)

// Authenticate with email + password
let authParams: PasswordsAuthenticateParameters = .init(email: "user@example.com", password: "mypassword")
try await stytch.passwords.authenticate(request: authParams)
```

Password reset by email follows the same deeplink pattern as magic links — see below.

---

## 5. Handle Deeplinks

Magic links and password reset emails redirect back to your app via a URL scheme.

**1. Register a redirect URL** in the [Stytch Dashboard](https://stytch.com/dashboard/redirect-urls). Use a scheme like `myapp://auth`.

**2. Add a URL scheme** to your target in Xcode: go to your target's **Info** tab, expand **URL Types**, click **+**, and enter your scheme (e.g., `myapp`) in the **URL Schemes** field.

**3. Receive the URL** in SwiftUI or UIKit:

```swift
// SwiftUI — add to your root view
.onOpenURL { url in
    handleDeeplink(url: url)
}

// UIKit — in your SceneDelegate
func scene(_ scene: UIScene, openURLContexts URLContexts: Set<UIOpenURLContext>) {
    guard let url = URLContexts.first?.url else { return }
    handleDeeplink(url: url)
}
```

**4. Authenticate the deeplink:**

```swift
func handleDeeplink(url: URL) {
    Task {
        do {
            let result = try await stytch.authenticate(url: url.absoluteString, sessionDurationMinutes: 30)
            switch onEnum(of: result) {
            case .authenticated:
                // User is now logged in
                break
            case .manualHandlingRequired(let status):
                // Password reset token — prompt for a new password,
                // then call stytch.passwords.resetByEmail(...)
                let resetToken = status.token
            case .unknownDeeplink:
                // Not a Stytch deeplink
                break
            }
        } catch {
            // Handle error
        }
    }
}
```

---

## 6. Session Management

Sessions are automatically persisted across app launches and validated on startup.

```swift
// Manually validate the current session (optionally extend it)
let params: SessionsAuthenticateParameters = .init(sessionDurationMinutes: 30)
try await stytch.session.authenticate(request: params)

// Sign out
try await stytch.session.revoke()
```
