package com.stytch.sdk.b2b

import com.stytch.sdk.StytchAuthenticationStateManager
import com.stytch.sdk.b2b.data.B2BAuthenticationState
import com.stytch.sdk.b2b.networking.AuthenticatedResponse
import com.stytch.sdk.b2b.networking.models.ApiB2bSessionV1MemberSession
import com.stytch.sdk.b2b.networking.models.ApiOrganizationV1Member
import com.stytch.sdk.b2b.networking.models.ApiOrganizationV1Organization
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

internal class StytchB2BAuthenticationStateManager(
    private val dispatchers: StytchDispatchers,
    private val persistenceClient: StytchPersistenceClient,
) : StytchAuthenticationStateManager {
    internal val sessionFlow: MutableStateFlow<ApiB2bSessionV1MemberSession?> = MutableStateFlow(null)
    internal val memberFlow: MutableStateFlow<ApiOrganizationV1Member?> = MutableStateFlow(null)
    internal val organizationFlow: MutableStateFlow<ApiOrganizationV1Organization?> = MutableStateFlow(null)
    internal var sessionTokenFlow: MutableStateFlow<String?> = MutableStateFlow(null)
    internal var sessionJwtFlow: MutableStateFlow<String?> = MutableStateFlow(null)

    private val loadingStateFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)

    override val authenticationStateFlow: StateFlow<B2BAuthenticationState> =
        combine(
            sessionFlow,
            memberFlow,
            organizationFlow,
            sessionTokenFlow,
            sessionJwtFlow,
        ) { session, member, organization, token, jwt ->
            if (!loadingStateFlow.value) return@combine B2BAuthenticationState.Loading()
            if (session != null && member != null && organization != null && token != null && jwt != null) {
                return@combine B2BAuthenticationState.Authenticated(member, session, organization, token, jwt)
            }
            B2BAuthenticationState.Unauthenticated()
        }.stateIn(CoroutineScope(dispatchers.mainDispatcher), SharingStarted.WhileSubscribed(5000L), B2BAuthenticationState.Loading())

    override val currentSessionToken: String?
        get() = sessionTokenFlow.value

    override suspend fun <T> update(response: T) {
        if (response is AuthenticatedResponse) {
            coroutineScope {
                memberFlow.value = response.member
                sessionFlow.value = response.memberSession
                sessionTokenFlow.value = response.sessionToken
                sessionJwtFlow.value = response.sessionJwt
                listOf(
                    async(dispatchers.ioDispatcher) { persistenceClient.save(MEMBER_IDENTIFIER, response.member) },
                    async(dispatchers.ioDispatcher) { persistenceClient.save(SESSION_IDENTIFIER, response.memberSession) },
                    async(dispatchers.ioDispatcher) { persistenceClient.save(SESSION_TOKEN_IDENTIFIER, response.sessionToken) },
                    async(dispatchers.ioDispatcher) { persistenceClient.save(SESSION_JWT_IDENTIFIER, response.sessionJwt) },
                ).awaitAll()
            }
        }
    }

    override suspend fun revoke() {
        coroutineScope {
            memberFlow.value = null
            sessionFlow.value = null
            sessionTokenFlow.value = null
            sessionJwtFlow.value = null
            listOf(
                async(dispatchers.ioDispatcher) { persistenceClient.remove(MEMBER_IDENTIFIER) },
                async(dispatchers.ioDispatcher) { persistenceClient.remove(SESSION_IDENTIFIER) },
                async(dispatchers.ioDispatcher) { persistenceClient.remove(SESSION_TOKEN_IDENTIFIER) },
                async(dispatchers.ioDispatcher) { persistenceClient.remove(SESSION_JWT_IDENTIFIER) },
            ).awaitAll()
        }
    }

    init {
        CoroutineScope(dispatchers.ioDispatcher).launch {
            listOf(
                async(dispatchers.ioDispatcher) { memberFlow.value = persistenceClient.get(MEMBER_IDENTIFIER, null) },
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
        private const val MEMBER_IDENTIFIER = "stytch_member"
    }
}
