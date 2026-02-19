package com.stytch.sdk.consumer.session

import com.stytch.sdk.consumer.networking.ConsumerNetworkingClient
import com.stytch.sdk.consumer.networking.models.ISessionsAuthenticateParameters
import com.stytch.sdk.consumer.networking.models.SessionsAuthenticateResponse
import com.stytch.sdk.consumer.networking.models.SessionsRevokeResponse
import com.stytch.sdk.consumer.networking.models.toNetworkModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.js.JsExport

@JsExport
public interface SessionClient {
    public suspend fun authenticate(request: ISessionsAuthenticateParameters): SessionsAuthenticateResponse

    public suspend fun revoke(): SessionsRevokeResponse
}

internal class SessionImpl(
    private val networkingClient: ConsumerNetworkingClient,
) : SessionClient {
    override suspend fun authenticate(request: ISessionsAuthenticateParameters): SessionsAuthenticateResponse =
        withContext(Dispatchers.Default) {
            networkingClient.request {
                networkingClient.api.sessionsAuthenticate(request.toNetworkModel())
            }
        }

    override suspend fun revoke(): SessionsRevokeResponse =
        withContext(Dispatchers.Default) {
            networkingClient.request {
                networkingClient.api.sessionsRevoke()
            }
        }
}
