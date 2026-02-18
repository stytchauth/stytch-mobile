package com.stytch.sdk.consumer

import com.stytch.sdk.StytchAuthenticationStateManager
import com.stytch.sdk.consumer.data.ConsumerAuthenticationState
import com.stytch.sdk.consumer.networking.AuthenticatedResponse
import com.stytch.sdk.data.StytchDispatchers
import com.stytch.sdk.persistence.StytchPersistenceClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.stytch.sdk.consumer.networking.models.ApiSessionV1Session as Session
import com.stytch.sdk.consumer.networking.models.ApiUserV1User as User

internal class StytchConsumerAuthenticationStateManager(
    private val dispatchers: StytchDispatchers,
    private val persistenceClient: StytchPersistenceClient,
) : StytchAuthenticationStateManager {
    internal val sessionFlow: MutableStateFlow<Session?> = MutableStateFlow(null)
    internal val userFlow: MutableStateFlow<User?> = MutableStateFlow(null)
    internal var sessionTokenFlow: MutableStateFlow<String?> = MutableStateFlow(null)
    internal var sessionJwtFlow: MutableStateFlow<String?> = MutableStateFlow(null)

    private val loadingStateFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)

    override val authenticationStateFlow: StateFlow<ConsumerAuthenticationState> =
        combine(loadingStateFlow, sessionFlow, userFlow, sessionTokenFlow, sessionJwtFlow) { isLoaded, session, user, token, jwt ->
            if (!isLoaded) return@combine ConsumerAuthenticationState.Loading()
            if (session != null && user != null && token != null && jwt != null) {
                return@combine ConsumerAuthenticationState.Authenticated(user, session, token, jwt)
            }
            ConsumerAuthenticationState.Unauthenticated()
        }.stateIn(CoroutineScope(dispatchers.mainDispatcher), SharingStarted.WhileSubscribed(5000L), ConsumerAuthenticationState.Loading())

    override val currentSessionToken: String?
        get() = sessionTokenFlow.value

    override suspend fun <T> update(response: T) {
        if (response is AuthenticatedResponse) {
            coroutineScope {
                userFlow.value = response.user
                sessionFlow.value = response.session
                sessionTokenFlow.value = response.sessionToken
                sessionJwtFlow.value = response.sessionJwt
                listOf(
                    async(dispatchers.ioDispatcher) { persistenceClient.save(USER_IDENTIFIER, response.user) },
                    async(dispatchers.ioDispatcher) { persistenceClient.save(SESSION_IDENTIFIER, response.session) },
                    async(dispatchers.ioDispatcher) { persistenceClient.save(SESSION_TOKEN_IDENTIFIER, response.sessionToken) },
                    async(dispatchers.ioDispatcher) { persistenceClient.save(SESSION_JWT_IDENTIFIER, response.sessionJwt) },
                ).awaitAll()
            }
        }
    }

    override suspend fun revoke() {
        coroutineScope {
            userFlow.value = null
            sessionFlow.value = null
            sessionTokenFlow.value = null
            sessionJwtFlow.value = null
            listOf(
                async(dispatchers.ioDispatcher) { persistenceClient.remove(USER_IDENTIFIER) },
                async(dispatchers.ioDispatcher) { persistenceClient.remove(SESSION_IDENTIFIER) },
                async(dispatchers.ioDispatcher) { persistenceClient.remove(SESSION_TOKEN_IDENTIFIER) },
                async(dispatchers.ioDispatcher) { persistenceClient.remove(SESSION_JWT_IDENTIFIER) },
            ).awaitAll()
        }
    }

    init {
        CoroutineScope(dispatchers.ioDispatcher).launch {
            listOf(
                async(dispatchers.ioDispatcher) { userFlow.value = persistenceClient.get(USER_IDENTIFIER, null) },
                async(dispatchers.ioDispatcher) { sessionFlow.value = persistenceClient.get(SESSION_IDENTIFIER, null) },
                async(dispatchers.ioDispatcher) { sessionTokenFlow.value = persistenceClient.get(SESSION_TOKEN_IDENTIFIER, null) },
                async(dispatchers.ioDispatcher) { sessionJwtFlow.value = persistenceClient.get(SESSION_JWT_IDENTIFIER, null) },
            ).awaitAll()
            loadingStateFlow.value = true
        }
    }

    private companion object {
        private const val SESSION_IDENTIFIER = "stytch_session"
        private const val SESSION_TOKEN_IDENTIFIER = "stytch_session_token"
        private const val SESSION_JWT_IDENTIFIER = "stytch_session_jwt"
        private const val USER_IDENTIFIER = "stytch_user"
    }
}
