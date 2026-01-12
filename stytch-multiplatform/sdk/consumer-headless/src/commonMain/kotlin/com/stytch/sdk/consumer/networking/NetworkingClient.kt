package com.stytch.sdk.consumer.networking

import com.stytch.sdk.consumer.StytchConsumerSessionManager
import com.stytch.sdk.data.SDK_URL_PATH
import com.stytch.sdk.data.StytchAPIError
import com.stytch.sdk.data.StytchClientConfigurationInternal
import com.stytch.sdk.data.StytchDataResponse
import com.stytch.sdk.data.StytchDispatchers
import com.stytch.sdk.data.StytchNetworkError
import com.stytch.sdk.networking.StytchNetworkResponseMiddleware
import com.stytch.sdk.networking.getStytchNetworkingClient
import com.stytch.sdk.networking.stytchNetworkRequest
import com.stytch.sdk.networking.stytchNetworkRequestWithRetryAndBackoff
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Clock

internal class NetworkingClient(
    private val configuration: StytchClientConfigurationInternal,
    private val dispatchers: StytchDispatchers,
    private val sessionManager: StytchConsumerSessionManager,
) {
    internal val api: API

    init {
        val endpointOptions = configuration.endpointOptions
        val domain = if (configuration.tokenInfo.isTestToken) endpointOptions.testDomain else endpointOptions.liveDomain
        val ktorfit =
            Ktorfit
                .Builder()
                .baseUrl("https://$domain/$SDK_URL_PATH")
                .httpClient(getStytchNetworkingClient(configuration, sessionManager::getCurrentSessionToken))
                .build()
        api = ktorfit.createAPI()

        CoroutineScope(dispatchers.ioDispatcher).launch {
            sessionManager.session.collect { session ->
                if (session != null) {
                    if (session.expiresAt < Clock.System.now()) {
                        sessionManager.revoke()
                    } else {
                        startSessionUpdateJob()
                    }
                }
            }
        }
    }

    private val middlewares: StytchNetworkResponseMiddleware =
        object : StytchNetworkResponseMiddleware {
            override suspend fun <T> onSuccess(data: T) {
                if (data is AuthenticatedResponse) {
                    sessionManager.update(data)
                    startSessionUpdateJob()
                }
            }

            override suspend fun onError(response: HttpResponse): Exception =
                try {
                    val exception = response.body<StytchAPIError>()
                    if (exception.isUnrecoverableError()) {
                        sessionManager.revoke()
                    }
                    exception
                } catch (_: Exception) {
                    throw StytchNetworkError(response.bodyAsText())
                }
        }

    private var sessionUpdateJob: Job? = null

    private fun startSessionUpdateJob() {
        sessionUpdateJob?.cancel()
        sessionUpdateJob =
            CoroutineScope(dispatchers.ioDispatcher).launch {
                try {
                    stytchNetworkRequestWithRetryAndBackoff(
                        block = {
                            api.sessionsAuthenticate(SessionsAuthenticateRequest(configuration.defaultSessionDuration))
                        },
                        onSuccess = sessionManager::update,
                    )
                    delay(HEARTBEAT_INTERVAL_MS)
                    startSessionUpdateJob()
                } catch (e: Exception) {
                    if (e is ResponseException) {
                        if (e.response.body<StytchAPIError>().isUnrecoverableError()) {
                            sessionManager.revoke()
                        }
                    }
                    // TODO: else, something went wrong
                }
            }
    }

    internal suspend fun <T> request(block: suspend (API) -> StytchDataResponse<T>) =
        stytchNetworkRequest(middlewares) {
            block(api)
        }

    private companion object {
        private const val HEARTBEAT_INTERVAL_MS = 3 * 60 * 1000L // 3 minutes
    }
}
