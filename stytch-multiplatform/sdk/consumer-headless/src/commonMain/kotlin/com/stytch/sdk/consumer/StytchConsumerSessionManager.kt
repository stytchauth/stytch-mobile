package com.stytch.sdk.consumer

import com.stytch.sdk.StytchSessionManager
import com.stytch.sdk.consumer.networking.AuthenticatedResponse
import com.stytch.sdk.consumer.networking.Session
import com.stytch.sdk.data.StytchDispatchers
import com.stytch.sdk.persistence.StytchPersistenceClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

internal class StytchConsumerSessionManager(
    private val dispatchers: StytchDispatchers,
    private val persistenceClient: StytchPersistenceClient,
) : StytchSessionManager {
    internal val session: MutableStateFlow<Session?> = MutableStateFlow(null)
    internal var sessionToken: String? = null

    override suspend fun getCurrentSessionToken(): String? = sessionToken

    override suspend fun <T> update(response: T) {
        if (response is AuthenticatedResponse) {
            sessionToken = response.sessionToken
            session.value = response.session
            coroutineScope {
                listOf(
                    async(dispatchers.ioDispatcher) { persistenceClient.save(SESSION_IDENTIFIER, response.session) },
                    async(dispatchers.ioDispatcher) { persistenceClient.save(SESSION_TOKEN_IDENTIFIER, response.sessionToken) },
                ).awaitAll()
            }
        }
    }

    override suspend fun revoke() {
        sessionToken = null
        session.value = null
        persistenceClient.remove(SESSION_IDENTIFIER)
        persistenceClient.remove(SESSION_TOKEN_IDENTIFIER)
    }

    init {
        CoroutineScope(dispatchers.ioDispatcher).launch {
            session.value = persistenceClient.get(SESSION_IDENTIFIER, null)
            sessionToken = persistenceClient.get(SESSION_TOKEN_IDENTIFIER, null)
        }
    }

    private companion object {
        private const val SESSION_IDENTIFIER = "stytch_session"
        private const val SESSION_TOKEN_IDENTIFIER = "stytch_session_token"
    }
}
