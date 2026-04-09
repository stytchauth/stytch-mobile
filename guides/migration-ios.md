# Migration Guide: stytch-ios → stytch-mobile (iOS)

This guide covers what changed when moving from `StytchCore` (the original iOS-only SDK) to `StytchConsumerSDK` or `StytchB2BSDK` (the new Kotlin Multiplatform-backed SDK).

---

## What Changed at a Glance

| | Old SDK (`stytch-ios`) | New SDK (`stytch-mobile`) |
|---|---|---|
| **Package** | `StytchCore` / `StytchUI` from `stytchauth/stytch-ios` | `StytchConsumerSDK` / `StytchB2BSDK` from `stytchauth/stytch-ios` |
| **Client** | `StytchClient` (global static) | `createStytchConsumer(configuration:)` (instance-based) |
| **Configuration** | `StytchClient.configure(configuration:)` | `createStytchConsumer(configuration:)` — configure and create in one step |
| **Auth state** | `StytchClient.sessions.onSessionChange` (Combine publisher) | `stytch.authenticationStateFlow` (async sequence) |
| **Concurrency** | `async/await`, Combine publishers, and completion handlers (Sourcery-generated) | `async/await` |
| **Deeplinks** | `StytchClient.handle(url:sessionDurationMinutes:)` | `stytch.authenticate(url:sessionDurationMinutes:)` |
| **OAuth result** | Two-step: `start()` returns `(token, url)`, then call `authenticate(token:)` | One-step: `start()` returns the full auth response |
| **Pre-built UI** | `StytchUI` / `StytchUIClient` | Not provided — bring your own UI |
| **Session migration** | — | **Automatic** — existing sessions are migrated on first launch |

---

## Installation

The Swift Package URL is the same (`stytchauth/stytch-ios`), but the product names have changed.

### Before

```
// Xcode: Add Package → https://github.com/stytchauth/stytch-ios
// Product: StytchCore   (or StytchUI)
import StytchCore
```

### After

Remove `StytchCore` and `StytchUI` from your target's frameworks and add the new product:

```
// Product: StytchConsumerSDK   (Consumer / B2C apps)
// Product: StytchB2BSDK        (B2B apps)
import StytchConsumerSDK
// or:
import StytchB2BSDK
```

In `Package.swift`:

```swift
// Before
.product(name: "StytchCore", package: "stytch-ios"),

// After
.product(name: "StytchConsumerSDK", package: "stytch-ios"),
// or:
.product(name: "StytchB2BSDK", package: "stytch-ios"),
```

The `-ObjC` linker flag is still required. In your target's **Build Settings**, confirm that **Other Linker Flags** contains `-ObjC`.

---

## Configuration and Client Initialization

### Before: global static + separate configure call

```swift
import StytchCore

// Configure first (typically in AppDelegate or App.init)
StytchClient.configure(configuration: .init(publicToken: "public-token-live-..."))

// Then use the global static anywhere
let response = try await StytchClient.otps.send(parameters: params)
```

### After: factory function returns an instance

```swift
import StytchConsumerSDK

// Create once and store — App.init, a dependency container, or a top-level let
let stytch = createStytchConsumer(
    configuration: .init(publicToken: "public-token-live-xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx")
)

// Then use the instance
let response = try await stytch.otp.sms.loginOrCreate(request: params)
```

The instance is a singleton internally — `createStytchConsumer` called again with the same token returns the same object. Storing it as a `let` constant or passing it through your dependency injection makes the dependency explicit rather than relying on a global.

---

## Authentication State and Session Access

This is the most significant behavioral change. The old SDK spread session and user state across multiple Combine publishers. The new SDK gives you a single async sequence.

### Before: Combine publishers

```swift
import Combine
import StytchCore

var subscriptions = Set<AnyCancellable>()

// Session changes
StytchClient.sessions.onSessionChange
    .receive(on: DispatchQueue.main)
    .sink { sessionInfo in
        switch sessionInfo {
        case let .available(session, lastValidatedAt):
            print("Active session: \(session.expiresAt)")
        case .unavailable:
            print("No session")
        }
    }
    .store(in: &subscriptions)

// Synchronous getters
let session = StytchClient.sessions.session
let sessionToken = StytchClient.sessions.sessionToken
let user = StytchClient.user.getSync()
```

### After: async sequence

```swift
import StytchConsumerSDK

// Observe state changes
Task {
    for await state in stytch.authenticationStateFlow {
        switch onEnum(of: state) {
        case .authenticated(let s):
            let user = s.user
            let session = s.session
            let sessionToken = s.sessionToken
            let sessionJwt = s.sessionJwt
            // Update your UI
        case .unauthenticated:
            // Show login UI
        case .loading:
            // Restoring persisted session
        }
    }
}

// Synchronous access (the StateFlow always has a current value)
let currentState = stytch.authenticationStateFlow.value
```

If you were using Combine elsewhere in your app and want to bridge the async sequence back to a publisher, you can wrap it with `AsyncStream` or use the callback-based observer:

```swift
// Callback-based alternative (useful for bridging to Combine)
let cleanup = stytch.authenticationStateObserver { state in
    // called on every state change
}
// Call cleanup.stop() to unsubscribe
```

---

## API Method Changes

Most method names are consistent, but the namespace and parameter type names have changed. The old SDK namespaced under `StytchClient` with nested type aliases (e.g., `StytchClient.OTP.Parameters`); the new SDK uses generated parameter types from the OpenAPI spec.

### OTP

```swift
// Before
let params = StytchClient.OTP.Parameters(deliveryMethod: .sms(phoneNumber: "+15551234567"))
let response = try await StytchClient.otps.send(parameters: params)
let authParams = StytchClient.OTP.AuthenticateParameters(code: code, methodId: methodId)
let authResponse = try await StytchClient.otps.authenticate(parameters: authParams)

// After
let sendParams: OTPsSMSLoginOrCreateParameters = .init(phoneNumber: "+15551234567")
let sendResponse = try await stytch.otp.sms.loginOrCreate(request: sendParams)

let authParams: OTPsAuthenticateParameters = .init(token: code, methodId: methodId, sessionDurationMinutes: 30)
let authResponse = try await stytch.otp.authenticate(request: authParams)
```

Note: the old SDK had a single `otps.send()` that accepted the delivery method as an enum. The new SDK splits by channel: `otp.sms`, `otp.email`, `otp.whatsapp`.

### Email Magic Links

```swift
// Before
let params = StytchClient.MagicLinks.Email.Parameters(
    email: "user@example.com",
    loginMagicLinkUrl: URL(string: "myapp://auth"),
    signupMagicLinkUrl: URL(string: "myapp://auth")
)
try await StytchClient.magicLinks.email.loginOrCreate(parameters: params)

// After
let params: MagicLinksByEmailLoginOrCreateParameters = .init(
    email: "user@example.com",
    loginMagicLinkUrl: "myapp://auth",
    signupMagicLinkUrl: "myapp://auth"
)
try await stytch.magicLinks.email.loginOrCreate(request: params)
```

Redirect URLs are now `String` rather than `URL?`.

### Passwords

```swift
// Before
try await StytchClient.passwords.authenticate(
    parameters: .init(email: "user@example.com", password: "pw", sessionDurationMinutes: 30)
)

// After
let params: PasswordsAuthenticateParameters = .init(email: "user@example.com", password: "pw", sessionDurationMinutes: 30)
try await stytch.passwords.authenticate(request: params)
```

### Session Management

```swift
// Before
try await StytchClient.sessions.authenticate(
    parameters: Sessions.AuthenticateParameters(sessionDurationMinutes: Minutes(rawValue: 30))
)
try await StytchClient.sessions.revoke(parameters: Sessions.RevokeParameters())

// After
let params: SessionsAuthenticateParameters = .init(sessionDurationMinutes: 30)
try await stytch.session.authenticate(request: params)
try await stytch.session.revoke()
```

`sessionDurationMinutes` is now a plain `Int` — the `Minutes` wrapper is gone.

---

## Deeplinks

### Before

```swift
switch try await StytchClient.handle(url: url, sessionDurationMinutes: 5) {
case let .handled(response):
    switch response {
    case let .auth(r): print(r.session)
    case let .oauth(r): print(r.session)
    }
case .notHandled:
    break
case let .manualHandlingRequired(tokenType, token, _):
    // e.g., password reset
    break
}
```

### After

```swift
let result = try await stytch.authenticate(url: url.absoluteString, sessionDurationMinutes: 30)
switch onEnum(of: result) {
case .authenticated:
    // User is now logged in
case .manualHandlingRequired(let status):
    // Password reset — store status.token, prompt for new password,
    // then call stytch.passwords.resetByEmail(...)
    let resetToken = status.token
case .unknownDeeplink:
    // Not a Stytch deeplink
}
```

The new `authenticate()` takes a `String` rather than a `URL`. Pass `url.absoluteString` from your `onOpenURL` or `openURLContexts` handler.

---

## OAuth

The OAuth flow has been simplified. The old SDK returned an intermediate `(token, url)` tuple from `start()` that you then passed to `authenticate()`. The new SDK completes the entire flow — browser session, redirect handling, and token exchange — inside a single `start()` call.

### Before

```swift
// Third-party (e.g., Google)
let configuration = StytchClient.OAuth.ThirdParty.WebAuthenticationConfiguration(
    loginRedirectUrl: URL(string: "myapp://login"),
    signupRedirectUrl: URL(string: "myapp://signup")
)
let (token, url) = try await StytchClient.oauth.google.start(configuration: configuration)
let response = try await StytchClient.oauth.authenticate(
    parameters: .init(token: token)
)

// Sign In With Apple
let response = try await StytchClient.oauth.apple.start(parameters: .init())
```

### After

```swift
// Third-party (e.g., Google) — start() returns the full auth response
let params: OAuthStartParameters = .init(
    loginRedirectUrl: "myapp://login",
    signupRedirectUrl: "myapp://signup",
    sessionDurationMinutes: 30,
    oauthPresentationContextProvider: self  // ASPresentationAnchor provider
)
let response = try await stytch.oauth.google.start(startParameters: params)

// Sign In With Apple — same interface, no separate authenticate() call needed
let appleParams: OAuthStartParameters = .init()
let response = try await stytch.oauth.apple.start(startParameters: appleParams)
```

Redirect URLs are now `String` rather than `URL?`. The `oauthPresentationContextProvider` is the replacement for `WebAuthenticationConfiguration.presentationContextProvider`.

---

## Concurrency: Combine and Completion Handlers

The old SDK used [Sourcery](https://github.com/krzysztofzablocki/Sourcery) to generate Combine and completion-handler variants of every async method. The new SDK does not generate these — it exposes `async/await` only.

If your codebase relies heavily on Combine at call sites, you can bridge with:

```swift
// Wrapping an async call in a Future (one-shot)
let publisher = Future<OTPsAuthenticateResponse, Error> { promise in
    Task {
        do {
            let response = try await stytch.otp.authenticate(request: params)
            promise(.success(response))
        } catch {
            promise(.failure(error))
        }
    }
}
```

For continuous state observation (previously `onSessionChange`), use the `authenticationStateObserver` callback or bridge `authenticationStateFlow` with `AsyncStream`:

```swift
let stream = AsyncStream(ConsumerAuthenticationState.self) { continuation in
    let cleanup = stytch.authenticationStateObserver { state in
        continuation.yield(state)
    }
    continuation.onTermination = { _ in cleanup.stop() }
}

// Bridge to a Publisher
let publisher = stream.publisher
```

---

## B2B SDK

If you used `StytchB2BClient` in the old SDK, the migration follows the same pattern. Import `StytchB2BSDK` and use `createStytchB2B(configuration:)`:

```swift
import StytchB2BSDK

let stytch = createStytchB2B(
    configuration: .init(publicToken: "public-token-live-...")
)
```

The B2B auth state uses `B2BAuthenticationState` with `.authenticated` (carrying `member`, `memberSession`, `organization`), `.unauthenticated`, and `.loading` cases.

---

## Pre-Built UI

`StytchUI` and `StytchUIClient` are not available in the new SDK. The new SDK is intentionally headless — you have complete control over every pixel of your authentication experience, with no constraints on layout, styling, navigation, or branding. Your existing screens remain yours; you just wire them to the SDK methods directly.

---

## Automatic Session Migration

There's nothing you need to do. On first launch after upgrading, the new SDK automatically reads and decrypts your users' existing sessions from the old SDK's `UserDefaults` store (`StytchEncryptedUserDefaults` suite) and migrates them into the new SDK's storage format. Users who were logged in will remain logged in.
