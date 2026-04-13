# Android Quickstart

This guide walks you through adding Stytch authentication to an Android app using `consumer-headless` or `b2b-headless`. Both SDKs are distributed via Maven Central and require **Kotlin 2.3.0 or later**.

---

## 1. Install the SDK

Make sure `mavenCentral()` is in your repository configuration:

```kotlin
// settings.gradle.kts
dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}
```

Add the dependency that matches your project type:

```kotlin
// Consumer (B2C apps)
dependencies {
    implementation("com.stytch.sdk:consumer-headless:1.0.0")
}

// B2B (organizations/members)
dependencies {
    implementation("com.stytch.sdk:b2b-headless:1.0.0")
}
```

If you prefer callback-style APIs over coroutines, see [Callback Extensions](#callback-extensions) at the end of this guide.

---

## 2. Initialize the Client

Create the client once — in your `Application` class or at the entry point of your auth flow. The client is a singleton; calling `createStytchConsumer` again returns the same instance.

```kotlin
import com.stytch.sdk.consumer.createStytchConsumer
import com.stytch.sdk.data.StytchClientConfiguration

class App : Application() {
    val stytch by lazy {
        createStytchConsumer(
            StytchClientConfiguration(
                context = applicationContext,
                publicToken = "public-token-live-xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx",
            )
        )
    }
}
```

For B2B apps, replace `createStytchConsumer` with `createStytchB2B` and import from `com.stytch.sdk.b2b`.

Your public token is in the [Stytch Dashboard](https://stytch.com/dashboard/api-keys). Make sure you've also enabled the auth methods you want to use under **SDK Configuration**.

---

## 3. Observe Authentication State

The SDK exposes a `StateFlow` that emits whenever the authentication state changes. Collect it from a `ViewModel` or `lifecycleScope`:

```kotlin
import com.stytch.sdk.consumer.data.ConsumerAuthenticationState

lifecycleScope.launch {
    app.stytch.authenticationStateFlow.collect { state ->
        when (state) {
            is ConsumerAuthenticationState.Authenticated -> {
                val user = state.user
                val session = state.session
                val sessionToken = state.sessionToken
                // Navigate to your authenticated UI
            }
            is ConsumerAuthenticationState.Unauthenticated -> {
                // Show your login UI
            }
            is ConsumerAuthenticationState.Loading -> {
                // SDK is restoring a persisted session — show a splash screen
            }
        }
    }
}
```

To read the current state synchronously (e.g., during a navigation check), use `stytch.authenticationStateFlow.value`.

---

## 4. Auth Methods

All SDK methods are `suspend` functions. Call them from a coroutine scope and handle errors with `try/catch`. Errors are thrown as `StytchError`.

### SMS OTP

```kotlin
import com.stytch.sdk.consumer.networking.models.OTPsSMSLoginOrCreateParameters
import com.stytch.sdk.consumer.networking.models.OTPsAuthenticateParameters

// Step 1: Send OTP
var methodId: String? = null

viewModelScope.launch {
    try {
        val response = stytch.otp.sms.loginOrCreate(
            OTPsSMSLoginOrCreateParameters(phoneNumber = "+15551234567")
        )
        methodId = response.methodId
    } catch (e: StytchError) {
        // Handle error
    }
}

// Step 2: Verify the code
viewModelScope.launch {
    try {
        stytch.otp.authenticate(
            OTPsAuthenticateParameters(
                token = userEnteredCode,
                methodId = methodId!!,
                sessionDurationMinutes = 30,
            )
        )
    } catch (e: StytchError) {
        // Handle error
    }
}
```

The SDK also supports email OTP (`stytch.otp.email.loginOrCreate(...)`) and WhatsApp OTP (`stytch.otp.whatsapp.loginOrCreate(...)`).

### Email Magic Links

Magic links require deeplink handling — see [Step 5](#5-handle-deeplinks).

```kotlin
import com.stytch.sdk.consumer.networking.models.MagicLinksByEmailLoginOrCreateParameters

viewModelScope.launch {
    try {
        stytch.magicLinks.email.loginOrCreate(
            MagicLinksByEmailLoginOrCreateParameters(
                email = "user@example.com",
                loginMagicLinkUrl = "myapp://auth",
                signupMagicLinkUrl = "myapp://auth",
            )
        )
        // Tell the user to check their inbox
    } catch (e: StytchError) {
        // Handle error
    }
}
```

### OAuth (Browser-Based)

OAuth requires deeplink handling — see [Step 5](#5-handle-deeplinks).

```kotlin
import com.stytch.sdk.oauth.OAuthStartParameters

viewModelScope.launch {
    try {
        // `start()` opens a Custom Tab, completes the OAuth flow,
        // and returns the authenticated session in one call.
        val response = stytch.oauth.google.start(
            OAuthStartParameters(
                activity = activity,
                loginRedirectUrl = "myapp://oauth",
                signupRedirectUrl = "myapp://oauth",
                sessionDurationMinutes = 30,
            )
        )
    } catch (e: StytchError) {
        // Handle error
    }
}
```

Replace `.google` with any supported provider: `.apple`, `.github`, `.microsoft`, `.facebook`, `.amazon`, `.slack`, and more.

### Google Credential Manager (Native)

To use Google's native credential manager dialog instead of a browser, pass a `GoogleCredentialConfiguration` when initializing:

```kotlin
import com.stytch.sdk.data.GoogleCredentialConfiguration

createStytchConsumer(
    StytchClientConfiguration(
        context = applicationContext,
        publicToken = "public-token-live-...",
        googleCredentialConfiguration = GoogleCredentialConfiguration(
            googleClientId = "YOUR_ANDROID_GOOGLE_CLIENT_ID",
        ),
    )
)
```

Then call `stytch.oauth.google.start(...)` as shown above — the SDK will automatically use Credential Manager when this configuration is present.

#### Note: Google Credential Manager is only supported in the consumer SDK. Google Credential Manager is not supported in the B2B SDK.

### Passwords

```kotlin
import com.stytch.sdk.consumer.networking.models.PasswordsAuthenticateParameters
import com.stytch.sdk.consumer.networking.models.PasswordsCreateParameters
import com.stytch.sdk.consumer.networking.models.PasswordsStrengthCheckParameters

// Check password strength before creating or resetting
val strengthCheck = stytch.passwords.strengthCheck(
    PasswordsStrengthCheckParameters(email = "user@example.com", password = "mypassword")
)

// Create a new password user
stytch.passwords.create(
    PasswordsCreateParameters(email = "user@example.com", password = "mypassword")
)

// Authenticate with email + password
stytch.passwords.authenticate(
    PasswordsAuthenticateParameters(email = "user@example.com", password = "mypassword")
)
```

Password reset by email follows the same deeplink pattern as magic links — see below.

---

## 5. Handle Deeplinks

Magic links and password reset emails redirect back to your app via a custom URL scheme.

**1. Register a redirect URL** in the [Stytch Dashboard](https://stytch.com/dashboard/redirect-urls). Use a scheme like `myapp://auth`.

**2. Add an intent filter** to your activity in `AndroidManifest.xml`:

```xml
<activity ...>
    <intent-filter>
        <action android:name="android.intent.action.VIEW" />
        <category android:name="android.intent.category.DEFAULT" />
        <category android:name="android.intent.category.BROWSABLE" />
        <data android:scheme="myapp" android:host="auth" />
    </intent-filter>
</activity>
```

**3. Handle the intent** in your activity:

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    if (intent.action == Intent.ACTION_VIEW) {
        intent.data?.toString()?.let { url ->
            viewModel.handleDeeplink(url)
        }
    }
}
```

**4. Authenticate the deeplink** in your ViewModel:

```kotlin
import com.stytch.sdk.consumer.data.DeeplinkAuthenticationStatus

fun handleDeeplink(url: String) {
    viewModelScope.launch {
        try {
            when (val status = stytch.authenticate(url, sessionDurationMinutes = 30)) {
                is DeeplinkAuthenticationStatus.Authenticated -> {
                    // User is now logged in
                }
                is DeeplinkAuthenticationStatus.ManualHandlingRequired -> {
                    // Password reset token — prompt the user for a new password,
                    // then call stytch.passwords.resetByEmail(token, newPassword)
                    val resetToken = status.token
                }
                is DeeplinkAuthenticationStatus.UnknownDeeplink -> {
                    // Not a Stytch deeplink
                }
            }
        } catch (e: StytchError) {
            // Handle error
        }
    }
}
```

---

## 6. Session Management

Sessions are automatically persisted across app launches and validated on startup.

```kotlin
// Manually authenticate (validate) the current session
stytch.session.authenticate(
    SessionsAuthenticateParameters(sessionDurationMinutes = 30)
)

// Sign out
stytch.session.revoke()
```

---

## Callback Extensions

If your project uses callback-style APIs or calls the SDK from Java, swap the base artifact for the extensions variant — it re-exports the base SDK, so no other changes are needed:

```kotlin
// Before
implementation("com.stytch.sdk:consumer-headless:1.0.0")

// After
implementation("com.stytch.sdk:consumer-headless-extensions:1.0.0")

// Same for B2B
implementation("com.stytch.sdk:b2b-headless-extensions:1.0.0")
```

Every `suspend` method then gains an `onSuccess`/`onFailure` overload that returns a cancellable `Job`:

```kotlin
val job = stytch.otp.authenticate(
    request = OTPsAuthenticateParameters(token = code, methodId = methodId, sessionDurationMinutes = 30),
    onSuccess = { response -> /* handle success */ },
    onFailure = { error -> /* handle error */ },
)
```
