package com.stytch.sdk.consumer.session

import com.stytch.sdk.StytchApi
import com.stytch.sdk.consumer.networking.ConsumerNetworkingClient
import com.stytch.sdk.consumer.networking.models.ISessionsAttestParameters
import com.stytch.sdk.consumer.networking.models.ISessionsAuthenticateParameters
import com.stytch.sdk.consumer.networking.models.SessionsAttestResponse
import com.stytch.sdk.consumer.networking.models.SessionsAuthenticateResponse
import com.stytch.sdk.consumer.networking.models.SessionsRevokeResponse
import com.stytch.sdk.consumer.networking.models.toNetworkModel
import com.stytch.sdk.data.StytchDispatchers
import com.stytch.sdk.data.StytchError
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException
import kotlin.js.JsExport

/** Session management methods. */
@StytchApi
@JsExport
public interface SessionClient {
    /**
     * Validates the current session token against the Stytch backend and optionally extends the session.
     * Calls the `POST /sdk/v1/sessions/authenticate` endpoint.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchConsumer.sessions.authenticate(
     *     SessionsAuthenticateParameters(sessionDurationMinutes = 30)
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = SessionsAuthenticateParameters(sessionDurationMinutes: 30)
     * let response = try await StytchConsumer.sessions.authenticate(params)
     * ```
     *
     * **React Native:**
     * ```js
     * StytchConsumer.sessions.authenticate({ sessionDurationMinutes: 30 })
     * ```
     *
     * @param request - [ISessionsAuthenticateParameters]
     *   - `sessionDurationMinutes?` — If provided, extends the session by this many minutes from the current time.
     *
     * @return [SessionsAuthenticateResponse] containing the validated session and user.
     *
     * @throws [StytchError] if the session token is invalid or expired.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun authenticate(request: ISessionsAuthenticateParameters): SessionsAuthenticateResponse

    /**
     * Revokes the current session, signing the user out on the backend and clearing local session state.
     * Calls the `POST /sdk/v1/sessions/revoke` endpoint.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchConsumer.sessions.revoke()
     * ```
     *
     * **iOS:**
     * ```swift
     * let response = try await StytchConsumer.sessions.revoke()
     * ```
     *
     * **React Native:**
     * ```js
     * StytchConsumer.sessions.revoke()
     * ```
     *
     * @return [SessionsRevokeResponse] confirming the session was revoked.
     *
     * @throws [StytchError] if the request fails.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun revoke(): SessionsRevokeResponse

    /**
     * Attests the current session using a Stytch Trusted Auth Token. Calls the `POST /sdk/v1/sessions/attest` endpoint.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchConsumer.sessions.attest(
     *     SessionsAttestParameters(
     *         profileId = "profile-id",
     *         token = "integrity-token",
     *     )
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = SessionsAttestParameters(profileId: "profile-id", token: "integrity-token")
     * let response = try await StytchConsumer.sessions.attest(params)
     * ```
     *
     * **React Native:**
     * ```js
     * StytchConsumer.sessions.attest({ profileId: "profile-id", token: "integrity-token" })
     * ```
     *
     * @param request - [ISessionsAttestParameters]
     *   - `profileId` — The device profile ID from the integrity provider.
     *   - `token` — The Stytch Trusted Auth Token
     *   - `sessionDurationMinutes?` — If provided, extends the session by this many minutes.
     *
     * @return [SessionsAttestResponse] containing the updated session.
     *
     * @throws [StytchError] if the attestation token is invalid.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun attest(request: ISessionsAttestParameters): SessionsAttestResponse
}

internal class SessionImpl(
    private val dispatchers: StytchDispatchers,
    private val networkingClient: ConsumerNetworkingClient,
) : SessionClient {
    @Throws(StytchError::class, CancellationException::class)
    override suspend fun authenticate(request: ISessionsAuthenticateParameters): SessionsAuthenticateResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.sessionsAuthenticate(request.toNetworkModel())
            }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun revoke(): SessionsRevokeResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.sessionsRevoke()
            }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun attest(request: ISessionsAttestParameters): SessionsAttestResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request { networkingClient.api.sessionsAttest(request.toNetworkModel()) }
        }
}
