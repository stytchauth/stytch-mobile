package com.stytch.sdk.networking

import com.stytch.sdk.StytchSessionManager
import com.stytch.sdk.data.SDK_URL_PATH
import com.stytch.sdk.data.StytchAPIError
import com.stytch.sdk.data.StytchClientConfigurationInternal
import com.stytch.sdk.data.StytchDataResponse
import com.stytch.sdk.data.StytchDispatchers
import com.stytch.sdk.data.StytchResult
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

public abstract class StytchNetworkingClient(
    configuration: StytchClientConfigurationInternal,
    private val dispatchers: StytchDispatchers,
    private val sessionManager: StytchSessionManager,
) {
    public abstract val middleware: StytchNetworkResponseMiddleware

    public abstract suspend fun <T> sessionUpdater(): StytchDataResponse<*>

    private var sessionUpdateJob: Job? = null

    public val ktorfit: Ktorfit

    public fun <T> startSessionUpdateJob() {
        sessionUpdateJob?.cancel()
        sessionUpdateJob =
            CoroutineScope(dispatchers.ioDispatcher).launch {
                try {
                    stytchNetworkRequestWithRetryAndBackoff(
                        block = { sessionUpdater<T>() },
                        onSuccess = sessionManager::update,
                    )
                    delay(HEARTBEAT_INTERVAL_MS)
                    startSessionUpdateJob<T>()
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

    public suspend fun <T> request(block: suspend () -> StytchDataResponse<T>): StytchResult<T> =
        stytchNetworkRequest(middleware) {
            block()
        }

    init {
        val endpointOptions = configuration.endpointOptions
        val domain = if (configuration.tokenInfo.isTestToken) endpointOptions.testDomain else endpointOptions.liveDomain
        ktorfit =
            Ktorfit
                .Builder()
                .baseUrl("https://$domain/$SDK_URL_PATH")
                .httpClient(getStytchHttpClient(configuration, sessionManager::getCurrentSessionToken))
                .build()
    }

    private companion object {
        private const val HEARTBEAT_INTERVAL_MS = 3 * 60 * 1000L // 3 minutes
    }
}
