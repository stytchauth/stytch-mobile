# Testing

All tests live in `jvmTest` source sets. Run them with:

```bash
# Shared SDK
./gradlew -p stytch-multiplatform-shared :sdk:shared:jvmTest --rerun-tasks

# Consumer SDK
./gradlew :sdk:consumer-headless:jvmTest --rerun-tasks

# B2B SDK
./gradlew :sdk:b2b-headless:jvmTest --rerun-tasks
```

**Frameworks**: `kotlin.test`, MockK, `kotlinx-coroutines-test`

---

## Test counts

| Project | Tests | Files |
|---|---|---|
| `stytch-multiplatform-shared` | 62 | 7 |
| `consumer-headless` | 119 | 17 |
| `b2b-headless` | 163 | 19 |
| **Total** | **344** | **43** |

---

## stytch-multiplatform-shared

| Test file | Tests | What it covers |
|---|---|---|
| `data/GetPublicTokenInfoTest` | 5 | `getPublicTokenInfo()` token parsing + test/live classification |
| `data/RBACPolicyTest` | 9 | RBAC policy evaluation (role matching, resource/action checks) |
| `dfp/DFPPAInterceptorTest` | 16 | DFP/PA request interception logic |
| `networking/StytchNetworkRequestTest` | 6 | `request { }` success/error/rethrow paths |
| `oauth/GenerateOAuthStartUrlTest` | 7 | OAuth start URL construction (CNAME, test/live domain, params) |
| `persistence/StytchPersistenceClientTest` | 8 | Typed save/get/remove round-trips |
| `pkce/PKCEClientTest` | 11 | PKCE create/retrieve/revoke, expiry, encoding |

### Known gaps — shared SDK

- **`StytchNetworkingClient` base class** — session heartbeat scheduling (`startSessionUpdateJob`, delay calculations) not directly tested; this logic is exercised indirectly through `ConsumerNetworkingClientTest` and `B2BNetworkingClientTest`.
- **`StytchEncryptionClient`, `OAuthProvider`, `BiometricsProvider`, `PasskeyProvider`** — `expect class` platform implementations; not unit-tested (platform actuals assumed correct per project policy).
- **`SharedAPI` (bootstrap endpoint)** — network-dependent; no unit test.

---

## consumer-headless

| Test file | Tests | What it covers |
|---|---|---|
| `DefaultStytchConsumerTest` | 15 | `parseDeeplink` (all token types), `authenticate` routing + session duration fallback, `getPKCECodePair` |
| `StytchConsumerAuthenticationStateManagerTest` | 12 | All four flows, `update`/`revoke`/`currentSessionToken`, persistence round-trips, init from persistence |
| `data/ConsumerTokenTypeTest` | 6 | `fromString` for all types, case-insensitivity, null, unknown |
| `networking/CheckAndHandleInitialSessionTest` | 3 | Session expiry routing (past/future/null `expiresAt`) |
| `networking/ConsumerNetworkingClientTest` | 2 | `updateSessionAndReturnExpiration` with and without `expiresAt` |
| `networking/ConsumerNetworkingClientMiddlewareTest` | 6 | `onSuccess` (Authenticated/Revoke/other), `onError` (API/network error) |
| `magicLinks/MagicLinksImplTest` | 5 | Authenticate PKCE/MissingPKCE, email send (primary/secondary session token path), email loginOrCreate PKCE |
| `otp/OtpImplTest` | 10 | Authenticate; SMS/email/WhatsApp send (primary/secondary) + loginOrCreate |
| `oauth/OAuthClientImplTest` | 17 | Authenticate PKCE/MissingPKCE, start flow routing (ClassicToken/Error/IDToken), URL construction, session duration |
| `passwords/PasswordsClientImplTest` | 8 | Email reset PKCE flow + MissingPKCE, existing password reset, session reset |
| `session/SessionImplTest` | 3 | Authenticate, revoke, attest |
| `totp/TOTPClientImplTest` | 4 | Create/authenticate with and without session token injection |
| `user/UserClientImplTest` | 9 | get/update/delete user; add/delete biometric/crypto/phone/TOTP factor |
| `crypto/CryptoClientImplTest` | 2 | `authenticateWallet`, `registerWallet` |
| `biometrics/BiometricsClientImplTest` | 9 | Register/authenticate/isAvailable, BiometricsAlreadyEnrolled error, platform error propagation |
| `passkeys/PasskeysClientImplTest` | 8 | Register/authenticate, MissingPasskeyException, PasskeysUnsupportedError |
| `StytchConsumerAuthenticationStateManagerTest` | 12 | (same row as above — listed at top) |

### Known gaps — consumer-headless

- **`dfp/DFPClientImpl`** — **no test file exists**. Simple two-path logic (`dfpProvider?.getTelemetryId() ?: throw DFPNotConfiguredError()`); the B2B version is tested in `b2b-headless/dfp/DFPClientImplTest` as a reference.
- **`DefaultStytchConsumer.authenticationStateObserver`** — the JS observer wrapper is untested (creates a `CoroutineScope` job that forwards `StateFlow` emissions; low test value).
- **`DefaultStytchConsumer` bootstrap caching** — `bootstrapResponse` (from `networkingClient.refreshBootStrapData()`) is not tested; integration-level concern.

---

## b2b-headless

| Test file | Tests | What it covers |
|---|---|---|
| `DefaultStytchB2BTest` | 20 | `parseDeeplink` (all 7 token types), `authenticate` routing for all 6 handled types + session duration fallback |
| `StytchB2BAuthenticationStateManagerTest` | 17 | All five flows + IST flow, `update`/`revoke` (incl. IST keys), `potentiallyUpdateIST`, IST expiration, `currentSessionToken`, persistence, init from persistence |
| `data/B2BTokenTypeTest` | 9 | `fromString` for all 7 types, case-insensitivity, null, unknown |
| `networking/B2BNetworkingClientMiddlewareTest` | 7 | `onSuccess` (Authenticated/Revoke/B2BResponse/other), `onError` (API error, unrecoverable revoke, network error) |
| `magicLinks/B2BMagicLinksClientImplTest` | 8 | Authenticate PKCE/MissingPKCE/IST/revoke, email loginOrSignup PKCE, email discovery send PKCE, discovery authenticate PKCE/MissingPKCE |
| `oauth/B2BOAuthClientImplTest` | 16 | Authenticate PKCE/MissingPKCE/IST, google start routing (ClassicToken/Error/IDToken), URL construction (CNAME/test/live domain, PKCE param), session duration, google discovery start, discovery authenticate |
| `sso/B2BSSOClientImplTest` | 18 | Start routing/URL params/domain selection/session duration, authenticate PKCE/IST/MissingPKCE, getConnections/deleteConnection, saml CRUD, oidc CRUD, external CRUD |
| `organizations/B2BOrganizationsClientImplTest` | 3 | get/update/delete |
| `passwords/B2BPasswordsClientImplTest` | 7 | Authenticate+IST, strengthCheck, email resetStart PKCE, email reset PKCE+IST+revoke, MissingPKCE, existingPassword reset, session reset |
| `discovery/B2BDiscoveryClientImplTest` | 7 | Organizations list/create+IST, intermediate sessions exchange+IST, passwords authenticate/resetStart PKCE/reset PKCE+revoke/MissingPKCE |
| `otp/B2BOtpClientImplTest` | 6 | SMS send/authenticate+IST, email loginOrSignup/authenticate+IST, email discovery send/authenticate |
| `totp/B2BTOTPClientImplTest` | 3 | create+IST, authenticate+IST, null IST passthrough |
| `recoveryCodes/B2BRecoveryCodesClientImplTest` | 3 | get, recover+IST, rotate |
| `members/B2BMembersClientImplTest` | 16 | get/delete/update/searchMembers; admin get/delete/update/reactivate/deletePassword/deletePhoneNumber/deleteTotp/deleteBiometric/deleteCrypto/deleteOAuth |
| `scim/B2BSCIMClientImplTest` | 8 | createConnection/updateConnection (with connectionId), deleteConnection, getConnection, rotateToken/startRotation/cancelRotation, listConnections |
| `rbac/B2BRBACClientImplTest` | 8 | `isAuthorizedSync` (cached policy), `isAuthorized` (refreshed policy), `allPermissions` — with real `RBACPolicy` fixture |
| `dfp/DFPClientImplTest` | 2 | `getTelemetryId` with and without provider |
| `session/B2BSessionsClientImplTest` | 5 | authenticate/revoke/exchange/accessTokenExchange/attest |

### Known gaps — b2b-headless

- **`B2BMagicLinksClientImpl.email.invite`** — `b2BMagicLinksInvite` is implemented but has no test case. Same simple passthrough shape as the other no-PKCE/no-IST endpoints.
- **`B2BNetworkingClient.checkAndHandleInitialSession`** — the analogous `CheckAndHandleInitialSessionTest` exists in consumer-headless but was never added for B2B. The B2B version differs slightly: `expiresAt` on `ApiB2bSessionV1MemberSession` is a non-nullable `Instant`, so there is no null case.
- **`B2BNetworkingClient.updateSessionAndReturnExpiration`** — tested in consumer as `ConsumerNetworkingClientTest`; the B2B equivalent was not added.
- **`DefaultStytchB2B.authenticationStateObserver`** — same JS observer wrapper as consumer; not tested.
- **`DefaultStytchB2B` bootstrap caching** — same as consumer; integration-level concern.
