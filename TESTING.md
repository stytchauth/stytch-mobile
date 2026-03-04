# Testing Plan

## Scope

Unit tests only. No integration or network tests. We are testing shared business logic — the platform-specific `actual` implementations are assumed correct for now.

---

## Target: `jvmTest` only

Both projects have tests in the `jvmTest` source set. This is the right call because:

- All the logic under test lives in `commonMain`, which is fully accessible from `jvmTest`
- `commonTest` would require running tests on every configured platform (Android, iOS, JS, JVM), which is slow and unnecessary for pure unit tests
- Our mocking strategy depends on JVM-only tools (see below)
- JVM tests run fast, locally, with no device or simulator required

The trade-off — that `jvmMain` `actual` implementations of `expect class`es are not exercised — is acceptable for now.

---

## Frameworks

### `kotlin.test`
Standard KMP test library. Provides `@Test`, assertions (`assertEquals`, `assertFailsWith`, etc.), and integrates with JUnit on JVM.

### `MockK` (`io.mockk:mockk`)
The standard mocking library for Kotlin on JVM. We need it specifically because the platform abstractions (`BiometricsProvider`, `PasskeyProvider`, `OAuthProvider`, `StytchEncryptionClient`, `StytchPlatformPersistenceClient`) are `expect class` — not interfaces — so they cannot be subclassed or manually faked. MockK can mock concrete and final Kotlin classes using bytecode manipulation, which solves this problem cleanly.

### `kotlinx-coroutines-test`
Provides `TestCoroutineScope`, `UnconfinedTestDispatcher`, and `runTest`. Required for testing `suspend` functions and `StateFlow` behavior deterministically. The `StytchDispatchers` wrapper makes it easy to inject test dispatchers in place of real `IO` and `Main` dispatchers.

---

## Mocking Strategy

- **`expect class` instances** (providers, encryption client, platform persistence) → MockK
- **`ConsumerNetworkingClient`** → MockK (mock at this level; don't test real HTTP)
- **`SdkExternalApi`** (the Ktorfit-generated interface) → MockK, when we need to verify specific API calls within a client's logic
- **`StytchPersistenceClient`** → MockK or an in-memory fake, depending on the test

---

## Test Plan

Tests are ordered from simplest/most foundational to most complex. Each wave builds on the previous. Complete each wave before moving to the next.

---

### Wave 1 — Shared Utilities (`stytch-multiplatform-shared`)

These have no dependency on networking and are the lowest-level building blocks. Test them first.

#### `StytchPersistenceClient`

Mock: `StytchEncryptionClient` (expect class), `StytchPlatformPersistenceClient` (expect class)

- `save` serializes value to JSON, encrypts, base64-encodes, and writes to platform client
- `save(null)` removes the key from platform client
- `get` retrieves, base64-decodes, decrypts, and deserializes correctly (roundtrip with `save`)
- `get` silently returns `null`/default and removes the key when base64 decode fails
- `get` silently returns `null`/default and removes the key when decryption fails
- `get` silently returns `null`/default and removes the key when deserialization fails
- `remove` delegates to platform client

#### `PKCEClient`

Mock: `StytchEncryptionClient` (expect class), `StytchPersistenceClient` (mock or in-memory)

- `create` generates a code verifier, derives a challenge, persists both, and returns the pair
- Challenge encoding: hex string produced from raw bytes matches expected format, then base64-encoded
- `retrieve` returns `CodePair` when both challenge and verifier are persisted
- `retrieve` returns `null` when verifier is missing
- `retrieve` returns `null` when challenge is missing
- `revoke` removes both persisted keys

---

### Wave 2 — State Management (`stytch-multiplatform`)

This is foundational because nearly every consumer client reads `currentSessionToken` from the state manager and several call `update()` or `revoke()`.

#### `StytchConsumerAuthenticationStateManager`

Mock: `StytchPersistenceClient`, `StytchDispatchers` (inject `UnconfinedTestDispatcher`)

- `authenticationStateFlow` emits `Loading` before initialization completes
- `authenticationStateFlow` emits `Authenticated` when all four values (user, session, token, jwt) are present
- `authenticationStateFlow` emits `Unauthenticated` when any of the four values is absent
- `update` with an `AuthenticatedResponse` sets all four flows and persists all four values
- `update` with a non-`AuthenticatedResponse` type does not mutate state
- `revoke` clears all four flows and removes all four persisted keys
- `currentSessionToken` reflects the current `sessionTokenFlow` value
- Init block loads all four values from persistence and sets loading state to `true` when complete

---

### Wave 3 — Simple Consumer Clients (direct delegation)

These clients contain minimal logic beyond dispatching to the API. They establish the testing pattern for mocking `ConsumerNetworkingClient` and verifying parameter transformation.

#### `SessionImpl`

Mock: `ConsumerNetworkingClient`, `StytchDispatchers`

- `authenticate` calls `sessionsAuthenticate` with the correct network model
- `revoke` calls `sessionsRevoke` with the correct network model
- `attest` calls `sessionsAttest` with the correct network model

#### `TOTPClientImpl`

Mock: `ConsumerNetworkingClient`, `StytchDispatchers`

- `create` calls `tOTPsCreate` with the correct network model
- `authenticate` calls `tOTPsAuthenticate` with the correct network model
- `recover` calls `tOTPsRecover` with the correct network model
- `recoveryCodes` calls `tOTPsGetRecoveryCodes`

---

### Wave 4 — Session-Branching Clients

These clients check `currentSessionToken` to decide between primary and secondary API endpoints. Test both branches.

#### `OtpImpl`

Mock: `ConsumerNetworkingClient`, `StytchConsumerAuthenticationStateManager`, `StytchDispatchers`

- `authenticate` calls the correct API endpoint with transformed parameters
- `sms.loginOrCreate` calls the SMS login/create endpoint
- `sms.send` calls the SMS primary send endpoint when no session token is present
- `sms.send` calls the SMS secondary send endpoint when a session token is present
- Same two-branch tests for `email.send` and `whatsapp.send`

#### `MagicLinksImpl`

Mock: `ConsumerNetworkingClient`, `PKCEClient`, `StytchConsumerAuthenticationStateManager`, `StytchDispatchers`

- `authenticate` retrieves the PKCE code pair and calls the API with the code verifier
- `authenticate` throws when no PKCE code pair is found
- `email.loginOrCreate` creates a PKCE code pair and passes the challenge to the API
- `email.send` creates a PKCE code pair and calls the primary endpoint when no session token
- `email.send` creates a PKCE code pair and calls the secondary endpoint when a session token exists

#### `PasswordsClientImpl`

Mock: `ConsumerNetworkingClient`, `PKCEClient`, `StytchDispatchers`

- `authenticate` calls the correct endpoint with transformed parameters
- `create` calls the correct endpoint
- `resetByEmailStart` creates a PKCE code pair and passes the challenge to the API
- `resetByEmail` retrieves the PKCE code pair and passes the verifier to the API
- `resetByEmail` throws when no PKCE code pair is found
- `resetByExistingPassword` calls the correct endpoint
- `resetBySession` calls the correct endpoint
- `strengthCheck` calls the correct endpoint

---

### Wave 5 — Sealed Class Routing & Callback Logic

#### `UserClientImpl`

Mock: `ConsumerNetworkingClient`, `StytchDispatchers`

- `getUser` calls `getMe` and returns the response
- `update` calls the correct endpoint with transformed parameters
- `deleteFactor(AuthenticationFactor.TOTP)` calls the TOTP deletion endpoint
- `deleteFactor(AuthenticationFactor.Biometric)` calls the biometric deletion endpoint
- `deleteFactor(AuthenticationFactor.CryptoWallet)` calls the crypto wallet deletion endpoint
- `deleteFactor(AuthenticationFactor.Email)` calls the email deletion endpoint
- `deleteFactor(AuthenticationFactor.OAuth)` calls the OAuth deletion endpoint
- `deleteFactor(AuthenticationFactor.PhoneNumber)` calls the phone number deletion endpoint
- `deleteFactor(AuthenticationFactor.WebAuthn)` calls the WebAuthn deletion endpoint
- Each `deleteFactor` call returns a `DeleteFactorResponse` (response type is unified correctly)

#### `CryptoClientImpl`

Mock: `ConsumerNetworkingClient`, `StytchConsumerAuthenticationStateManager`, `StytchDispatchers`

- `authenticate` calls the primary start endpoint when no session token is present
- `authenticate` calls the secondary start endpoint when a session token is present
- `authenticate` extracts the challenge from the start response and passes it to `signChallenge`
- `signChallenge` is invoked on the main dispatcher
- The signature returned by `signChallenge` is passed correctly to the authenticate API call

---

### Wave 6 — Provider-Dependent Clients

These require mocking `expect class` providers via MockK, which is more involved.

#### `PasskeysClientImpl`

Mock: `ConsumerNetworkingClient`, `IPasskeyProvider`, `StytchConsumerAuthenticationStateManager`, `StytchDispatchers`

- `isSupported` reflects the provider's value
- `register` throws `PasskeysUnsupportedError` when the provider reports unsupported
- `register` calls `webAuthnRegisterStart`, passes response to provider, then calls `webAuthnRegister`
- `authenticate` throws `PasskeysUnsupportedError` when the provider reports unsupported
- `authenticate` calls the primary start endpoint when no session token is present
- `authenticate` calls the secondary start endpoint when a session token is present
- `update` throws `PasskeysUnsupportedError` when the provider reports unsupported
- `update` calls the correct API endpoint with transformed parameters

#### `BiometricsClientImpl`

Mock: `ConsumerNetworkingClient`, `IBiometricsProvider`, `StytchEncryptionClient`, `StytchConsumerAuthenticationStateManager`, `StytchDispatchers`

- `getAvailability` delegates to provider
- `register` throws when biometrics availability is `Unavailable`
- `register` throws when biometrics availability is `NotEnrolled`
- `register` throws when no session token is present
- `register` generates a key pair, calls `biometricsRegisterStart`, signs the challenge, calls `biometricsRegister`
- `register` persists the encrypted private key on success
- `authenticate` throws when no existing biometric registration is found
- `authenticate` retrieves the key pair, signs the challenge, calls the authenticate API
- `removeRegistration` delegates to provider

---

### Wave 7 — Complex Orchestration

#### `OAuthClientImpl`

Mock: `ConsumerNetworkingClient`, `PKCEClient`, `IOAuthProvider`, `StytchDispatchers`

- `authenticate` retrieves the PKCE code verifier and passes it to the API
- `authenticate` throws when no PKCE code pair is found
- `authenticateWithIdToken` (Google) calls the Google ID token endpoint
- `authenticateWithIdToken` (Apple) calls the Apple ID token endpoint
- `start` constructs the OAuth URL using the CNAME domain when provided
- `start` constructs the OAuth URL using the test/live domain when no CNAME is provided
- `start` with a `ClassicToken` result calls `authenticate`
- `start` with a Google `IDToken` result calls `authenticateGoogleIdToken`
- `start` with an Apple `IDToken` result calls `authenticateAppleIdToken`
- `start` propagates errors from the provider
- Name parsing: 3+ parts → first, middle (joined), last
- Name parsing: 2 parts → first, last
- Name parsing: 1 part → first only
- Session duration fallback: uses provided value, then `defaultSessionDuration`, then hardcoded `5`

#### `ConsumerNetworkingClient`

Mock: `SdkExternalApi`, `StytchConsumerAuthenticationStateManager`, `StytchDispatchers`

- Middleware `onSuccess` with an `AuthenticatedResponse` calls `update` on the state manager
- Middleware `onSuccess` with a `SessionsRevokeResponse` calls `revoke` on the state manager
- Middleware `onSuccess` with any other type is a no-op
- Middleware `onError` parses a valid `StytchAPIError` body and returns it
- Middleware `onError` with an unrecoverable error calls `revoke` on the state manager
- Middleware `onError` returns a `StytchNetworkError` fallback when the body cannot be parsed
- Init: when the first emitted session is expired, `revoke` is called on the state manager
- Init: when the first emitted session is valid, the session refresh job is started
