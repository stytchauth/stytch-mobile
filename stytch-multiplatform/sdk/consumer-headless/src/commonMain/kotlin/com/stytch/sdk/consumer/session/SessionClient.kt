package com.stytch.sdk.consumer.session

import com.stytch.sdk.consumer.networking.ConsumerNetworkingClient
import com.stytch.sdk.consumer.networking.models.ISessionsAttestParameters
import com.stytch.sdk.consumer.networking.models.ISessionsAuthenticateParameters
import com.stytch.sdk.consumer.networking.models.SessionsAttestResponse
import com.stytch.sdk.consumer.networking.models.SessionsAuthenticateResponse
import com.stytch.sdk.consumer.networking.models.SessionsRevokeResponse
import com.stytch.sdk.consumer.networking.models.toNetworkModel
import com.stytch.sdk.data.StytchDispatchers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.js.JsExport

@JsExport
public interface SessionClient {
    public suspend fun authenticate(request: ISessionsAuthenticateParameters): SessionsAuthenticateResponse

    public suspend fun revoke(): SessionsRevokeResponse

    public suspend fun attest(request: ISessionsAttestParameters): SessionsAttestResponse
}

internal class SessionImpl(
    private val dispatchers: StytchDispatchers,
    private val networkingClient: ConsumerNetworkingClient,
) : SessionClient {
    override suspend fun authenticate(request: ISessionsAuthenticateParameters): SessionsAuthenticateResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.sessionsAuthenticate(request.toNetworkModel())
            }
        }

    override suspend fun revoke(): SessionsRevokeResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.sessionsRevoke()
            }
        }

    override suspend fun attest(request: ISessionsAttestParameters): SessionsAttestResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request { networkingClient.api.sessionsAttest(request.toNetworkModel()) }
        }
}
