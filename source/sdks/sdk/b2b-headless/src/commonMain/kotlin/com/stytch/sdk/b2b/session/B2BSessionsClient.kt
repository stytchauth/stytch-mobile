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
    /** Validates the current session token and optionally extends the session expiry. */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun authenticate(request: IB2BSessionsAuthenticateParameters): B2BSessionsAuthenticateResponse

    /** Revokes the current session, signing the member out. */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun revoke(): B2BSessionsRevokeResponse

    /** Exchanges the current session for a session in a different organization. */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun exchange(request: IB2BSessionsExchangeParameters): B2BSessionsExchangeResponse

    /** Exchanges the current session for an OAuth access token for a connected provider. */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun exchangeAccessToken(request: IB2BSessionsAccessTokenExchangeParameters): B2BSessionsAccessTokenExchangeResponse

    /** Attests the current session using a device integrity token. */
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
