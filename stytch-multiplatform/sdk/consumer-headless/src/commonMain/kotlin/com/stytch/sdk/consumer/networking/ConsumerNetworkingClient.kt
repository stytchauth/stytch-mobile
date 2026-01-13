package com.stytch.sdk.consumer.networking

import com.stytch.sdk.consumer.StytchConsumerSessionManager
import com.stytch.sdk.data.StytchAPIError
import com.stytch.sdk.data.StytchClientConfigurationInternal
import com.stytch.sdk.data.StytchDispatchers
import com.stytch.sdk.data.StytchNetworkError
import com.stytch.sdk.networking.StytchNetworkResponseMiddleware
import com.stytch.sdk.networking.StytchNetworkingClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlin.time.Clock

internal class ConsumerNetworkingClient(
    private val configuration: StytchClientConfigurationInternal,
    private val dispatchers: StytchDispatchers,
    private val sessionManager: StytchConsumerSessionManager,
) : StytchNetworkingClient(configuration, dispatchers, sessionManager) {
    internal val api: API = ktorfit.createAPI()

    init {
        CoroutineScope(dispatchers.ioDispatcher).launch {
            // Collect the first non-null session emission (on start) and revoke or refresh as necessary
            sessionManager.session.firstOrNull { it != null }?.let { session ->
                println("JORDAN >>> Got a session from the manager, do something with it")
                if (session.expiresAt < Clock.System.now()) {
                    println("JORDAN >>> Expired, revoke it")
                    sessionManager.revoke()
                } else {
                    println("JORDAN >>> valid, refresh it")
                    sessionUpdater<SessionsAuthenticateResponse>()
                }
            }
        }
    }

    override suspend fun <T> sessionUpdater() = api.sessionsAuthenticate(SessionsAuthenticateRequest(configuration.defaultSessionDuration))

    override val middleware: StytchNetworkResponseMiddleware =
        object : StytchNetworkResponseMiddleware {
            override suspend fun <T> onSuccess(data: T) {
                if (data is AuthenticatedResponse) {
                    sessionManager.update(data)
                    startSessionUpdateJob<SessionsAuthenticateResponse>()
                }
            }

            override suspend fun onError(exception: ResponseException): Exception =
                try {
                    val error = exception.response.body<StytchAPIError>()
                    if (error.isUnrecoverableError()) {
                        sessionManager.revoke()
                    }
                    error
                } catch (_: Exception) {
                    throw StytchNetworkError(exception.response.bodyAsText())
                }
        }
}
