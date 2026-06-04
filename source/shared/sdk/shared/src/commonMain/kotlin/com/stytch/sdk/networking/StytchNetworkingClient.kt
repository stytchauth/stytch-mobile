package com.stytch.sdk.networking

import com.stytch.sdk.StytchAuthenticationStateManager
import com.stytch.sdk.data.BootstrapResponse
import com.stytch.sdk.data.DFPConfiguration
import com.stytch.sdk.data.DFPProtectedAuthMode
import com.stytch.sdk.data.StytchAPIError
import com.stytch.sdk.data.StytchClientConfigurationInternal
import com.stytch.sdk.data.StytchDataResponse
import com.stytch.sdk.data.StytchDispatchers
import de.jensklingenberg.ktorfit.Ktorfit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.math.max
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
    private val heartbeatMutex = Mutex()

    public val ktorfit: Ktorfit

    internal val sharedAPI: SharedAPI

    private val httpClient =
        getStytchHttpClient(
            configuration = configuration,
            getSessionToken = { sessionManager.currentSessionToken },
            getDfpConfiguration = { dfpConfiguration },
        )

    private val heartbeatScope = CoroutineScope(dispatchers.ioDispatcher + SupervisorJob())

    public fun startSessionUpdateJob(delay: Long) {
        // This is a little more complicated than the existing android/iOS logic
        // previously, we only triggered the auto update every ~= 3 minutes, which could result
        // in sessions persisting locally for up to 3 minutes after expiration. Not a security issue, because
        // the API enforced sessions, but weird UX. Now, we force the check AT LEAST every three minutes,
        // but potentially sooner, if the session expires BEFORE three minutes from now. Does that make sense?
        heartbeatScope.launch {
            heartbeatMutex.withLock {
                sessionUpdateJob?.cancel()
                sessionUpdateJob =
                    heartbeatScope.launch {
                        try {
                            delay(delay)
                            val nextSessionExpiration =
                                stytchNetworkRequestWithRetryAndBackoff(
                                    block = ::updateSessionAndReturnExpiration,
                                )
                            val timeUntilSessionExpires = (nextSessionExpiration - Clock.System.now()).inWholeMilliseconds
                            // prevent negative delays
                            val delay = max(0L, min(timeUntilSessionExpires, HEARTBEAT_INTERVAL_MS))
                            startSessionUpdateJob(delay)
                        } catch (e: Exception) {
                            if (e is StytchAPIError) {
                                if (e.isUnrecoverableError()) {
                                    sessionManager.revoke()
                                }
                            }
                            // if it's not a network/API error, and we exhausted our retries, cancel the updating job. The next time a
                            // (successful) network response completes, it will re-create the heartbeat
                            heartbeatMutex.withLock {
                                sessionUpdateJob?.cancel()
                                sessionUpdateJob = null
                            }
                        }
                    }
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
                .baseUrl("https://$domain/")
                .httpClient(httpClient)
                .build()
        sharedAPI = ktorfit.createSharedAPI()
    }

    private var dfpConfiguration: DFPConfiguration = DFPConfiguration()

    public suspend fun refreshBootStrapData(): Result<BootstrapResponse> =
        try {
            // Add retry with backoff to boostrap requests
            val bootstrapResponse =
                stytchNetworkRequestWithRetryAndBackoff {
                    sharedAPI.getBootstrapData(configuration.tokenInfo.publicToken)
                }
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
            Result.success(bootstrapResponse.data)
        } catch (e: Exception) {
            Result.failure(e)
        }

    public companion object {
        public const val HEARTBEAT_INTERVAL_MS: Long = 3 * 60 * 1000L // 3 minutes
    }
}
