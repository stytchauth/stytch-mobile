package com.stytch.sdk.b2b.session

import com.stytch.sdk.StytchApi
import com.stytch.sdk.b2b.networking.B2BNetworkingClient
import com.stytch.sdk.b2b.networking.models.B2BSessionsAccessTokenExchangeResponse
import com.stytch.sdk.b2b.networking.models.B2BSessionsAttestResponse
import com.stytch.sdk.b2b.networking.models.B2BSessionsAuthenticateResponse
import com.stytch.sdk.b2b.networking.models.B2BSessionsExchangeResponse
import com.stytch.sdk.b2b.networking.models.B2BSessionsRevokeResponse
import com.stytch.sdk.b2b.networking.models.IB2BSessionsAccessTokenExchangeParameters
import com.stytch.sdk.b2b.networking.models.IB2BSessionsAttestParameters
import com.stytch.sdk.b2b.networking.models.IB2BSessionsAuthenticateParameters
import com.stytch.sdk.b2b.networking.models.IB2BSessionsExchangeParameters
import com.stytch.sdk.b2b.networking.models.toNetworkModel
import com.stytch.sdk.data.StytchDispatchers
import com.stytch.sdk.data.StytchError
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException
import kotlin.js.JsExport

/** B2B session management methods. */
@StytchApi
@JsExport
public interface B2BSessionsClient {
    /**
     * Validates the current member session token against the Stytch backend and optionally extends
     * the session. Calls the `POST /sdk/v1/b2b/sessions/authenticate` endpoint.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchB2B.sessions.authenticate(
     *     B2BSessionsAuthenticateParameters(sessionDurationMinutes = 30)
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = B2BSessionsAuthenticateParameters(sessionDurationMinutes: 30)
     * let response = try await StytchB2B.sessions.authenticate(params)
     * ```
     *
     * **React Native:**
     * ```js
     * StytchB2B.sessions.authenticate({ sessionDurationMinutes: 30 })
     * ```
     *
     * @param request - [IB2BSessionsAuthenticateParameters]
     *   - `sessionDurationMinutes?` — If provided, extends the session by this many minutes from now.
     *
     * @return [B2BSessionsAuthenticateResponse] containing the validated member session.
     *
     * @throws [StytchError] if the session token is invalid or expired.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun authenticate(request: IB2BSessionsAuthenticateParameters): B2BSessionsAuthenticateResponse

    /**
     * Revokes the current member session, signing the member out on the backend and clearing local
     * session state. Calls the `POST /sdk/v1/b2b/sessions/revoke` endpoint.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchB2B.sessions.revoke()
     * ```
     *
     * **iOS:**
     * ```swift
     * let response = try await StytchB2B.sessions.revoke()
     * ```
     *
     * **React Native:**
     * ```js
     * StytchB2B.sessions.revoke()
     * ```
     *
     * @return [B2BSessionsRevokeResponse] confirming the session was revoked.
     *
     * @throws [StytchError] if the request fails.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun revoke(): B2BSessionsRevokeResponse

    /**
     * Exchanges the current member session for a session in a different organization.
     * Calls the `POST /sdk/v1/b2b/sessions/exchange` endpoint. The member must belong to the
     * target organization.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchB2B.sessions.exchange(
     *     B2BSessionsExchangeParameters(
     *         organizationId = "org-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *         sessionDurationMinutes = 30,
     *     )
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = B2BSessionsExchangeParameters(
     *     organizationId: "org-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *     sessionDurationMinutes: 30
     * )
     * let response = try await StytchB2B.sessions.exchange(params)
     * ```
     *
     * **React Native:**
     * ```js
     * StytchB2B.sessions.exchange({
     *     organizationId: "org-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *     sessionDurationMinutes: 30,
     * })
     * ```
     *
     * @param request - [IB2BSessionsExchangeParameters]
     *   - `organizationId` — The ID of the organization to exchange into.
     *   - `sessionDurationMinutes` — Duration of the new session, in minutes.
     *   - `locale?` — Locale for any follow-up communications.
     *
     * @return [B2BSessionsExchangeResponse] containing the new member session for the target organization.
     *
     * @throws [StytchError] if the current session is invalid or the member is not in the target organization.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun exchange(request: IB2BSessionsExchangeParameters): B2BSessionsExchangeResponse

    /**
     * Exchanges the current member session for an OAuth access token for a connected provider.
     * Calls the `POST /sdk/v1/b2b/sessions/exchange_access_token` endpoint.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchB2B.sessions.exchangeAccessToken(
     *     B2BSessionsAccessTokenExchangeParameters(
     *         accessToken = "access-token",
     *         sessionDurationMinutes = 30,
     *     )
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = B2BSessionsAccessTokenExchangeParameters(
     *     accessToken: "access-token",
     *     sessionDurationMinutes: 30
     * )
     * let response = try await StytchB2B.sessions.exchangeAccessToken(params)
     * ```
     *
     * **React Native:**
     * ```js
     * StytchB2B.sessions.exchangeAccessToken({
     *     accessToken: "access-token",
     *     sessionDurationMinutes: 30,
     * })
     * ```
     *
     * @param request - [IB2BSessionsAccessTokenExchangeParameters]
     *   - `accessToken` — The OAuth access token to exchange.
     *   - `sessionDurationMinutes` — Duration of the resulting session, in minutes.
     *
     * @return [B2BSessionsAccessTokenExchangeResponse] containing the authenticated member session.
     *
     * @throws [StytchError] if the access token is invalid or the exchange fails.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun exchangeAccessToken(request: IB2BSessionsAccessTokenExchangeParameters): B2BSessionsAccessTokenExchangeResponse

    /**
     * Attests the current member session using a Stytch Trusted Auth Token. Calls the `POST /sdk/v1/b2b/sessions/attest`
     * endpoint.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchB2B.sessions.attest(
     *     B2BSessionsAttestParameters(
     *         profileId = "profile-id",
     *         token = "integrity-token",
     *     )
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = B2BSessionsAttestParameters(profileId: "profile-id", token: "integrity-token")
     * let response = try await StytchB2B.sessions.attest(params)
     * ```
     *
     * **React Native:**
     * ```js
     * StytchB2B.sessions.attest({ profileId: "profile-id", token: "integrity-token" })
     * ```
     *
     * @param request - [IB2BSessionsAttestParameters]
     *   - `profileId` — The device profile ID from the integrity provider.
     *   - `token` — The Stytch Trusted Auth Token.
     *   - `organizationId?` — The ID of the organization to attest for; defaults to the current session's org.
     *   - `sessionDurationMinutes?` — If provided, extends the session by this many minutes.
     *
     * @return [B2BSessionsAttestResponse] containing the updated member session.
     *
     * @throws [StytchError] if the attestation token is invalid.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun attest(request: IB2BSessionsAttestParameters): B2BSessionsAttestResponse
}

internal class B2BSessionsClientImpl(
    private val dispatchers: StytchDispatchers,
    private val networkingClient: B2BNetworkingClient,
) : B2BSessionsClient {
    @Throws(StytchError::class, CancellationException::class)
    override suspend fun authenticate(request: IB2BSessionsAuthenticateParameters): B2BSessionsAuthenticateResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.b2BSessionsAuthenticate(request.toNetworkModel())
            }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun revoke(): B2BSessionsRevokeResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.b2BSessionsRevoke()
            }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun exchange(request: IB2BSessionsExchangeParameters): B2BSessionsExchangeResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.b2BSessionsExchange(request.toNetworkModel())
            }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun exchangeAccessToken(request: IB2BSessionsAccessTokenExchangeParameters): B2BSessionsAccessTokenExchangeResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.b2BSessionsAccessTokenExchange(request.toNetworkModel())
            }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun attest(request: IB2BSessionsAttestParameters): B2BSessionsAttestResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.b2BSessionsAttest(request.toNetworkModel())
            }
        }
}
