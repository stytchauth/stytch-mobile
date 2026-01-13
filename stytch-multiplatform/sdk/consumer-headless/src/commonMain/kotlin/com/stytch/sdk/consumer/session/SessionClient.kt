package com.stytch.sdk.consumer.session

import com.stytch.sdk.consumer.networking.ConsumerNetworkingClient
import com.stytch.sdk.consumer.networking.SessionsAuthenticateRequest
import com.stytch.sdk.consumer.networking.SessionsAuthenticateResponse
import com.stytch.sdk.consumer.networking.SessionsRevokeResponse
import com.stytch.sdk.data.StytchResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
@JsName("SessionClient")
public interface SessionClient {
    public suspend fun authenticate(request: SessionsAuthenticateRequest): StytchResult<SessionsAuthenticateResponse>

    public suspend fun revoke(): StytchResult<SessionsRevokeResponse>
}

internal class SessionImpl(
    private val networkingClient: ConsumerNetworkingClient,
) : SessionClient {
    override suspend fun authenticate(request: SessionsAuthenticateRequest): StytchResult<SessionsAuthenticateResponse> =
        withContext(Dispatchers.Default) {
            networkingClient.request {
                networkingClient.api.sessionsAuthenticate(request)
            }
        }

    override suspend fun revoke(): StytchResult<SessionsRevokeResponse> =
        withContext(Dispatchers.Default) {
            networkingClient.request {
                networkingClient.api.sessionsRevoke()
            }
        }
}
