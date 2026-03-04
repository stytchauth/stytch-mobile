package com.stytch.sdk.consumer.networking

import com.stytch.sdk.consumer.StytchConsumerAuthenticationStateManager
import com.stytch.sdk.consumer.networking.api.SdkExternalApi
import com.stytch.sdk.consumer.networking.models.SessionsAuthenticateRequest
import com.stytch.sdk.data.StytchClientConfigurationInternal
import com.stytch.sdk.data.StytchDispatchers
import com.stytch.sdk.networking.StytchNetworkResponseMiddleware
import com.stytch.sdk.networking.StytchNetworkingClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.Instant

internal class ConsumerNetworkingClient(
    private val configuration: StytchClientConfigurationInternal,
    private val dispatchers: StytchDispatchers,
    private val sessionManager: StytchConsumerAuthenticationStateManager,
) : StytchNetworkingClient(configuration, dispatchers, sessionManager) {
    internal val api: SdkExternalApi = ktorfit.create()

    init {
        CoroutineScope(dispatchers.ioDispatcher).launch {
            // Collect the first non-null session emission (on start) and revoke or refresh as necessary
            sessionManager.sessionFlow.firstOrNull { it != null }?.let { session ->
                if ((session.expiresAt ?: Instant.DISTANT_PAST) < Clock.System.now()) {
                    sessionManager.revoke()
                } else {
                    startSessionUpdateJob()
                }
            }
        }
    }

    override suspend fun updateSessionAndReturnExpiration(): Instant {
        val response = api.sessionsAuthenticate(SessionsAuthenticateRequest(configuration.defaultSessionDuration))
        return response.data.session.expiresAt ?: Instant.DISTANT_PAST
    }

    override val middleware: StytchNetworkResponseMiddleware =
        ConsumerNetworkingClientMiddleware(
            sessionManager = sessionManager,
            onSessionAuthenticated = ::startSessionUpdateJob,
        )
}
