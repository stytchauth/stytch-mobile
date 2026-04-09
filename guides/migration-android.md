# Migration Guide: stytch-android → stytch-mobile (Android)

This guide covers what changed when moving from the `com.stytch.sdk:sdk` artifact (the original Android-only SDK) to `com.stytch.sdk:consumer-headless` or `com.stytch.sdk:b2b-headless` (the new Kotlin Multiplatform SDK).

---

## What Changed at a Glance

| | Old SDK (`stytch-android`) | New SDK (`stytch-mobile`) |
|---|---|---|
| **Artifact** | `com.stytch.sdk:sdk` | `com.stytch.sdk:consumer-headless` or `b2b-headless` |
| **Kotlin** | 2.0.0 required | **2.3.0 or later required** |
| **Configuration** | `StytchClient.configure(context)` (reads `STYTCH_PUBLIC_TOKEN` from resources) | `createStytchConsumer(StytchClientConfiguration(context, publicToken))` (explicit) |
| **Error model** | `StytchResult.Success` / `StytchResult.Error` sealed class | `throws StytchError` |
| **Auth state** | `StytchClient.sessions.onChange` Flow + `getSync()` | `stytch.authenticationStateFlow` (`StateFlow<ConsumerAuthenticationState>`) |
| **Deeplinks** | `StytchClient.handle(uri, sessionDurationMinutes)` | `stytch.authenticate(url, sessionDurationMinutes)` |
| **OAuth result** | Two-step: `start()` + `authenticate()` (via activity result or `getTokenForProvider`) | One-step: `start()` returns the full auth response |
| **Google OneTap** | `StytchClient.oauth.googleOneTap.start(params)` | `stytch.oauth.google.start(params)` with `GoogleCredentialConfiguration` |
| **Callbacks** | Built into every method | Separate `-extensions` artifact |
| **Pre-built UI** | `StytchUI` | Not provided — bring your own UI |
| **Session migration** | — | **Automatic** — existing sessions are migrated on first launch |

---

## Kotlin Version

The new SDK requires **Kotlin 2.3.0 or later**. Update your project's Kotlin version before proceeding:

```kotlin
// build.gradle.kts (root)
plugins {
    kotlin("android") version "2.3.0" apply false
}
```

---

## Installation

```kotlin
// Before
dependencies {
    implementation("com.stytch.sdk:sdk:latest.release")
}

// After (Consumer)
dependencies {
    implementation("com.stytch.sdk:consumer-headless:1.0.0")
}

// After (B2B)
dependencies {
    implementation("com.stytch.sdk:b2b-headless:1.0.0")
}
```

---

## Configuration

### Before: auto-configured from a string resource

The old SDK read your public token automatically from a `STYTCH_PUBLIC_TOKEN` string resource and configured itself when you called `configure(context)`:

```xml
<!-- strings.xml -->
<string name="STYTCH_PUBLIC_TOKEN">public-token-live-...</string>
```

```kotlin
// Application.onCreate()
StytchClient.configure(applicationContext)
```

### After: explicit configuration

The new SDK requires you to pass the token explicitly. Remove the `STYTCH_PUBLIC_TOKEN` string resource and initialize the client directly:

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

Store the returned instance — it's a singleton, but you should hold a reference to it rather than relying on a global `StytchClient` object.

The `STYTCH_PUBLIC_TOKEN` string resource used to also auto-register OAuth redirect activities. With the new SDK, that's handled by the SDK's internal activity management — you only need to add your custom URL scheme's intent filter for magic links / password reset deeplinks.

---

## Error Handling

### Before: `StytchResult` sealed class

```kotlin
when (val result = StytchClient.otps.authenticate(params)) {
    is StytchResult.Success -> { /* use result.value */ }
    is StytchResult.Error -> { /* use result.exception */ }
}
```

### After: `throws StytchError`

```kotlin
try {
    val response = stytch.otp.authenticate(params)
    // use response directly
} catch (e: StytchError) {
    // handle error
}
```

This aligns with standard Kotlin coroutine error handling and removes the need to chain `when` blocks around every call.

---

## Method Naming

Most method names are the same, but a few have changed. Key differences:

| Old | New |
|---|---|
| `StytchClient.otps.sms.loginOrCreate(OTP.SmsOTP.Parameters(...))` | `stytch.otp.sms.loginOrCreate(OTPsSMSLoginOrCreateParameters(...))` |
| `StytchClient.otps.authenticate(OTP.AuthParameters(...))` | `stytch.otp.authenticate(OTPsAuthenticateParameters(...))` |
| `StytchClient.magicLinks.email.loginOrCreate(EmailMagicLinks.Parameters(...))` | `stytch.magicLinks.email.loginOrCreate(MagicLinksByEmailLoginOrCreateParameters(...))` |
| `StytchClient.magicLinks.authenticate(MagicLinks.AuthParameters(...))` | `stytch.magicLinks.authenticate(MagicLinksAuthenticateParameters(...))` |
| `StytchClient.passwords.authenticate(Passwords.AuthParameters(...))` | `stytch.passwords.authenticate(PasswordsAuthenticateParameters(...))` |
| `StytchClient.sessions.authenticate(Sessions.AuthParams())` | `stytch.session.authenticate(SessionsAuthenticateParameters(...))` |
| `StytchClient.sessions.revoke(Sessions.RevokeParams())` | `stytch.session.revoke()` |
| `StytchClient.user.get()` | `stytch.user.get()` |

The parameter types are generated from the OpenAPI spec. When in doubt, let IDE autocomplete guide you — the shape (field names, types) is consistent with the old SDK's parameters.

---

## Authentication State and Session Access

### Before: multiple sources of truth

The old SDK had several ways to observe and access session/user data:

```kotlin
// Observe session changes
StytchClient.sessions.onChange.collect { info ->
    when (info) {
        is StytchObjectInfo.Available -> { /* info.value is SessionData */ }
        is StytchObjectInfo.Unavailable -> { /* logged out */ }
    }
}

// Synchronous getters
val session = StytchClient.sessions.getSync()
val user = StytchClient.user.getSyncUser()
val sessionToken = StytchClient.sessions.sessionToken
val sessionJwt = StytchClient.sessions.sessionJwt
```

### After: one `StateFlow`

The new SDK consolidates everything into a single `StateFlow`:

```kotlin
// Observe changes
stytch.authenticationStateFlow.collect { state ->
    when (state) {
        is ConsumerAuthenticationState.Authenticated -> {
            val user = state.user
            val session = state.session
            val sessionToken = state.sessionToken
            val sessionJwt = state.sessionJwt
        }
        is ConsumerAuthenticationState.Unauthenticated -> { /* logged out */ }
        is ConsumerAuthenticationState.Loading -> { /* restoring session */ }
    }
}

// Synchronous access (StateFlow always has a current value)
val currentState = stytch.authenticationStateFlow.value
```

---

## Deeplinks

### Before

```kotlin
// Manual parsing
val (tokenType, token) = StytchClient.parseDeeplink(uri)

// Automatic handling
val result = StytchClient.handle(uri = uri, sessionDurationMinutes = 30)
when (result) {
    is DeeplinkHandledStatus.Handled -> { /* nested result.response */ }
    is DeeplinkHandledStatus.NotHandled -> { }
    is DeeplinkHandledStatus.ManualHandlingRequired -> { /* token type + value */ }
}
```

### After

```kotlin
when (val status = stytch.authenticate(url = uri.toString(), sessionDurationMinutes = 30)) {
    is DeeplinkAuthenticationStatus.Authenticated -> { /* user is logged in */ }
    is DeeplinkAuthenticationStatus.ManualHandlingRequired -> {
        // Password reset — store status.token and prompt for new password
    }
    is DeeplinkAuthenticationStatus.UnknownDeeplink -> { /* not ours */ }
}
```

The new `authenticate()` takes a `String` URL rather than a `URI` object. Call `uri.toString()` if you have a `URI` from the intent.

If you need the token type directly without authenticating:

```kotlin
val deeplinkToken = stytch.parseDeeplink(url = uri.toString())
// deeplinkToken?.type, deeplinkToken?.token
```

---

## OAuth

The OAuth flow has been significantly simplified. The old SDK had two patterns: an activity-result callback approach, and a `getTokenForProvider()` approach. Both required two steps (start + authenticate). The new SDK collapses this into a single `start()` call that manages the browser, handles the redirect, and returns the fully authenticated response.

### Before (activity result callback pattern)

```kotlin
// In your Activity
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    if (requestCode == STYTCH_OAUTH_REQUEST_CODE) {
        data?.data?.let { url ->
            val token = url.getQueryParameter("token")
            viewModelScope.launch {
                StytchClient.oauth.authenticate(
                    OAuth.ThirdParty.AuthenticateParameters(token = token, sessionDurationMinutes = 30)
                )
            }
        }
    }
}

// In your ViewModel
StytchClient.oauth.github.start(
    OAuth.ThirdParty.StartParameters(
        context = activity,
        oAuthRequestIdentifier = STYTCH_OAUTH_REQUEST_CODE,
        loginRedirectUrl = "myapp://oauth?type=login",
        signupRedirectUrl = "myapp://oauth?type=signup",
    )
)
```

### Before (direct token capture pattern)

```kotlin
// Register receiver in Activity
StytchClient.oauth.setOAuthReceiverActivity(this)

// In ViewModel
val token = StytchClient.oauth.github.getTokenForProvider(startParams)
val result = StytchClient.oauth.authenticate(authenticateParams)
```

### After

```kotlin
// One call, no activity result handler, no receiver registration needed
val response = stytch.oauth.github.start(
    OAuthStartParameters(
        activity = activity,
        loginRedirectUrl = "myapp://oauth",
        signupRedirectUrl = "myapp://oauth",
        sessionDurationMinutes = 30,
    )
)
```

No `oAuthRequestIdentifier`, no `setOAuthReceiverActivity()`, no separate `authenticate()` call.

### Google Credential Manager (formerly Google OneTap)

The old SDK exposed Google OneTap as a separate `googleOneTap` client. In the new SDK, native Google Credential Manager is used automatically when you provide `GoogleCredentialConfiguration` in the `StytchClientConfiguration`. The call site is identical to any other provider:

```kotlin
// Before
StytchClient.oauth.googleOneTap.start(
    OAuth.GoogleOneTap.StartParameters(context = activity, clientId = "YOUR_ANDROID_CLIENT_ID")
)

// After (step 1: configure at initialization time)
createStytchConsumer(
    StytchClientConfiguration(
        context = applicationContext,
        publicToken = "public-token-live-...",
        googleCredentialConfiguration = GoogleCredentialConfiguration(
            googleClientId = "YOUR_ANDROID_CLIENT_ID",
        ),
    )
)

// After (step 2: same call as any other provider)
stytch.oauth.google.start(
    OAuthStartParameters(activity = activity, sessionDurationMinutes = 30)
)
```

If no `GoogleCredentialConfiguration` is provided, or if Google Credential Manager fails on the users device, `oauth.google.start()` falls back to a browser-based OAuth flow.

---

## B2B SDK

If you used the B2B client in the old SDK (`StytchB2BClient`), the migration is the same pattern. Replace the artifact and swap `createStytchConsumer` for `createStytchB2B`:

```kotlin
import com.stytch.sdk.b2b.createStytchB2B

val stytch = createStytchB2B(
    StytchClientConfiguration(
        context = applicationContext,
        publicToken = "public-token-live-...",
    )
)
```

The B2B auth state uses `B2BAuthenticationState` with `.authenticated` (carrying `member`, `memberSession`, `organization`), `.unauthenticated`, and `.loading` cases.

---

## Callback Extensions

The old SDK generated callback overloads for every method automatically. The new SDK ships them in a separate artifact. If your codebase uses callbacks, swap the dependency:

```kotlin
// Before (callbacks were always available)
StytchClient.otps.authenticate(params) { result ->
    when (result) {
        is StytchResult.Success -> { }
        is StytchResult.Error -> { }
    }
}

// After (add the extensions artifact)
implementation("com.stytch.sdk:consumer-headless-extensions:1.0.0")

stytch.otp.authenticate(
    request = params,
    onSuccess = { response -> },
    onFailure = { error -> },
)
```

---

## Pre-Built UI

`StytchUI` is not available in the new SDK. The new SDK is intentionally headless — you have complete control over every pixel of your authentication experience, which means no constraints on layout, styling, navigation, or branding. Your existing screens remain yours; you just wire them to the SDK methods directly.

---

## Automatic Session Migration

There's nothing you need to do. On first launch after upgrading, the new SDK automatically reads and decrypts your users' existing sessions from the old SDK's `SharedPreferences` store and migrates them into the new SDK's storage format. Users who were logged in will remain logged in.
