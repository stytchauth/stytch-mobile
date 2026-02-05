package com.stytch.sdk.networking

import com.stytch.sdk.StytchAuthenticationStateManager
import com.stytch.sdk.data.DFPConfiguration
import com.stytch.sdk.data.DFPProtectedAuthMode
import com.stytch.sdk.data.SDK_URL_PATH
import com.stytch.sdk.data.StytchAPIError
import com.stytch.sdk.data.StytchClientConfigurationInternal
import com.stytch.sdk.data.StytchDataResponse
import com.stytch.sdk.data.StytchDispatchers
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.min
import kotlin.time.Clock
import kotlin.time.Instant

public abstract class StytchNetworkingClient(
    private val configuration: StytchClientConfigurationInternal,
    private val dispatchers: StytchDispatchers,
    private val sessionManager: StytchAuthenticationStateManager,
) {
    public abstract val middleware: StytchNetworkResponseMiddleware

    public abstract suspend fun updateSessionAndReturnExpiration(): Instant

    private var sessionUpdateJob: Job? = null

    public val ktorfit: Ktorfit

    internal val sharedAPI: SharedAPI

    private val httpClient =
        getStytchHttpClient(
            configuration = configuration,
            getSessionToken = { sessionManager.currentSessionToken },
            getDfpConfiguration = { dfpConfiguration },
        )

    public fun startSessionUpdateJob() {
        // This is a little more complicated than the existing android/iOS logic
        // previously, we only triggered the auto update every ~= 3 minutes, which could result
        // in sessions persisting locally for up to 3 minutes after expiration. Not a security issue, because
        // the API enforced sessions, but weird UX. Now, we force the check AT LEAST every three minutes,
        // but potentially sooner, if the session expires BEFORE three minutes from now. Does that make sense?
        sessionUpdateJob?.cancel()
        sessionUpdateJob =
            CoroutineScope(dispatchers.ioDispatcher).launch {
                try {
                    val nextSessionExpiration =
                        stytchNetworkRequestWithRetryAndBackoff(
                            block = ::updateSessionAndReturnExpiration,
                        )
                    val timeUntilSessionExpires = (nextSessionExpiration - Clock.System.now()).inWholeMilliseconds
                    val delay = min(timeUntilSessionExpires, HEARTBEAT_INTERVAL_MS)
                    delay(delay)
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

    public suspend fun <T> request(block: suspend () -> StytchDataResponse<T>): T =
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
                .httpClient(httpClient)
                .build()
        sharedAPI = ktorfit.createSharedAPI()
    }

    private var dfpConfiguration: DFPConfiguration = DFPConfiguration()

    public suspend fun refreshBootStrapData() {
        try {
            val bootstrapResponse = sharedAPI.getBootstrapData(configuration.tokenInfo.publicToken)
            dfpConfiguration =
                DFPConfiguration(
                    dfpProtectedAuthEnabled = bootstrapResponse.data.dfpProtectedAuthEnabled,
                    dfpProtectedAuthMode =
                        bootstrapResponse.data.dfpProtectedAuthMode
                            ?: DFPProtectedAuthMode.OBSERVATION,
                )
            bootstrapResponse.data.captchaSettings.siteKey.let { siteKey ->
                if (siteKey.isNotBlank()) {
                    configuration.captchaProvider?.initialize(siteKey)
                }
            }
        } catch (e: Exception) {
            // TODO: Logging for failed bootstrap response
        }
    }

    private companion object {
        private const val HEARTBEAT_INTERVAL_MS = 3 * 60 * 1000L // 3 minutes
    }
}
