package com.stytch.sdk.consumer.session

import com.stytch.sdk.consumer.networking.ConsumerNetworkingClient
import com.stytch.sdk.consumer.networking.models.SessionsAuthenticateRequest
import com.stytch.sdk.consumer.networking.models.SessionsAuthenticateResponse
import com.stytch.sdk.consumer.networking.models.SessionsRevokeResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
public interface SessionClient {
    public suspend fun authenticate(request: SessionsAuthenticateRequest): SessionsAuthenticateResponse

    public suspend fun revoke(): SessionsRevokeResponse
}

internal class SessionImpl(
    private val networkingClient: ConsumerNetworkingClient,
) : SessionClient {
    override suspend fun authenticate(request: SessionsAuthenticateRequest): SessionsAuthenticateResponse =
        withContext(Dispatchers.Default) {
            networkingClient.request {
                networkingClient.api.sessionsAuthenticate(request)
            }
        }

    override suspend fun revoke(): SessionsRevokeResponse =
        withContext(Dispatchers.Default) {
            networkingClient.request {
                networkingClient.api.sessionsRevoke()
            }
        }
}
