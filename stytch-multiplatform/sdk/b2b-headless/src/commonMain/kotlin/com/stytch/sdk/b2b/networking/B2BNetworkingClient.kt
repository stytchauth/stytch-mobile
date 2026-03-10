package com.stytch.sdk.b2b.networking

import com.stytch.sdk.b2b.StytchB2BAuthenticationStateManager
import com.stytch.sdk.b2b.networking.api.SdkExternalApi
import com.stytch.sdk.b2b.networking.models.ApiB2bSessionV1MemberSession as MemberSession
import com.stytch.sdk.b2b.networking.models.ApiOrganizationV1Member as Member
import com.stytch.sdk.b2b.networking.models.ApiOrganizationV1Organization as Organization
import com.stytch.sdk.data.StytchClientConfigurationInternal
import com.stytch.sdk.data.StytchDispatchers
import com.stytch.sdk.networking.StytchNetworkResponseMiddleware
import com.stytch.sdk.networking.StytchNetworkingClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.firstOrNull
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

internal class B2BNetworkingClient(
    private val configuration: StytchClientConfigurationInternal,
    private val dispatchers: StytchDispatchers,
    private val sessionManager: StytchB2BAuthenticationStateManager,
    apiOverride: SdkExternalApi? = null,
) : StytchNetworkingClient(configuration, dispatchers, sessionManager) {
    internal val api: SdkExternalApi = apiOverride ?: ktorfit.create()

    init {
        CoroutineScope(dispatchers.ioDispatcher).launch {
            // Collect the first non-null session emission (on start) and revoke or refresh as necessary
            sessionManager.sessionFlow.firstOrNull { it != null }?.let { session ->
                checkAndHandleInitialSession(
                    session = session,
                    now = Clock.System.now(),
                    onExpired = { sessionManager.revoke() },
                    onValid = { triggerSessionUpdateJobWithDelay(session.expiresAt ?: Instant.DISTANT_PAST) },
                )
            }
        }
    }

    internal fun triggerSessionUpdateJobWithDelay(sessionExpiresAt: Instant) {
        val timeUntilSessionExpires = (sessionExpiresAt - Clock.System.now()).inWholeMilliseconds
        startSessionUpdateJob(min(timeUntilSessionExpires, HEARTBEAT_INTERVAL_MS))
    }

    override suspend fun updateSessionAndReturnExpiration(): Instant {
        val response = api.sessionsAuthenticate(SessionsAuthenticateRequest(configuration.defaultSessionDuration))
        return response.data.session.expiresAt ?: Instant.DISTANT_PAST
    }

    override val middleware: StytchNetworkResponseMiddleware =
        B2BNetworkingClientMiddleware(
            sessionManager = sessionManager,
            onSessionAuthenticated = ::triggerSessionUpdateJobWithDelay,
        )
}
