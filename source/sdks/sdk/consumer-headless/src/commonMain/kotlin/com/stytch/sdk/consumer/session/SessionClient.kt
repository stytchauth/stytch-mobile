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
    /** Validates the current session token and optionally extends the session expiry. */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun authenticate(request: ISessionsAuthenticateParameters): SessionsAuthenticateResponse

    /** Revokes the current session, signing the user out. */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun revoke(): SessionsRevokeResponse

    /** Attests the current session using a device integrity token. */
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
