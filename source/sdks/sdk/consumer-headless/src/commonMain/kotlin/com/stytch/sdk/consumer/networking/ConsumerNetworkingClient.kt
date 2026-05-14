package com.stytch.sdk.consumer.networking

import com.stytch.sdk.consumer.StytchConsumerAuthenticationStateManager
import com.stytch.sdk.consumer.networking.api.SdkExternalApi
import com.stytch.sdk.consumer.networking.models.ApiSessionV1Session
import com.stytch.sdk.consumer.networking.models.SessionsAuthenticateRequest
import com.stytch.sdk.data.StytchClientConfigurationInternal
import com.stytch.sdk.data.StytchDispatchers
import com.stytch.sdk.networking.StytchNetworkResponseMiddleware
import com.stytch.sdk.networking.StytchNetworkingClient
import io.ktor.client.plugins.ResponseException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlin.math.min
import kotlin.time.Clock
import kotlin.time.Instant

internal suspend fun checkAndHandleInitialSession(
    session: ApiSessionV1Session,
    now: Instant,
    onExpired: suspend () -> Unit,
    onValid: () -> Unit,
) {
    if ((session.expiresAt ?: Instant.DISTANT_PAST) < now) onExpired() else onValid()
}

internal class ConsumerNetworkingClient(
    private val configuration: StytchClientConfigurationInternal,
    private val dispatchers: StytchDispatchers,
    private val sessionManager: StytchConsumerAuthenticationStateManager,
    apiOverride: SdkExternalApi? = null,
) : StytchNetworkingClient(configuration, dispatchers, sessionManager) {
    internal val api: SdkExternalApi = apiOverride ?: ktorfit.create()
    private val networkingClientScope = CoroutineScope(dispatchers.ioDispatcher + SupervisorJob())

    init {
        networkingClientScope.launch {
            // Collect the first non-null session token emission, and trigger the heartbeat immediately
            sessionManager.sessionTokenFlow.filterNotNull().take(1).collect {
                startSessionUpdateJob(0L)
            }
        }
    }

    internal fun triggerSessionUpdateJobWithDelay(sessionExpiresAt: Instant) {
        val timeUntilSessionExpires = (sessionExpiresAt - Clock.System.now()).inWholeMilliseconds
        startSessionUpdateJob(min(timeUntilSessionExpires, HEARTBEAT_INTERVAL_MS))
    }

    override suspend fun updateSessionAndReturnExpiration(): Instant =
        try {
            val response = api.sessionsAuthenticate(SessionsAuthenticateRequest(configuration.defaultSessionDuration))
            middleware.onSuccess(response)
            return response.data.session.expiresAt ?: Instant.DISTANT_PAST
        } catch (e: ResponseException) {
            throw middleware.onError(e)
        }

    override val middleware: StytchNetworkResponseMiddleware =
        ConsumerNetworkingClientMiddleware(
            sessionManager = sessionManager,
            onSessionAuthenticated = ::triggerSessionUpdateJobWithDelay,
        )
}
