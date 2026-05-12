package com.stytch.sdk.b2b

import com.stytch.sdk.StytchAuthenticationStateManager
import com.stytch.sdk.b2b.data.B2BAuthenticationState
import com.stytch.sdk.b2b.networking.AuthenticatedResponse
import com.stytch.sdk.b2b.networking.B2BResponse
import com.stytch.sdk.b2b.networking.models.ApiB2bSessionV1MemberSession
import com.stytch.sdk.b2b.networking.models.ApiOrganizationV1Member
import com.stytch.sdk.b2b.networking.models.ApiOrganizationV1Organization
import com.stytch.sdk.data.StytchDispatchers
import com.stytch.sdk.persistence.StytchPersistenceClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlin.concurrent.Volatile
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant

internal class StytchB2BAuthenticationStateManager(
    private val dispatchers: StytchDispatchers,
    private val persistenceClient: StytchPersistenceClient,
) : StytchAuthenticationStateManager {
    internal val sessionFlow: MutableStateFlow<ApiB2bSessionV1MemberSession?> = MutableStateFlow(null)
    internal val memberFlow: MutableStateFlow<ApiOrganizationV1Member?> = MutableStateFlow(null)
    internal val organizationFlow: MutableStateFlow<ApiOrganizationV1Organization?> = MutableStateFlow(null)
    internal var sessionTokenFlow: MutableStateFlow<String?> = MutableStateFlow(null)
    internal var sessionJwtFlow: MutableStateFlow<String?> = MutableStateFlow(null)
    internal var intermediateSessionTokenFlow: MutableStateFlow<String?> = MutableStateFlow(null)

    @Volatile
    internal var istExpiration: Instant? = null

    private val loadingStateFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)
    internal val exceptionFlow: MutableStateFlow<Throwable?> = MutableStateFlow(null)
    private val stateScope = CoroutineScope(dispatchers.mainDispatcher + SupervisorJob())

    @OptIn(ExperimentalCoroutinesApi::class)
    override val authenticationStateFlow: StateFlow<B2BAuthenticationState> =
        exceptionFlow
            .combine(loadingStateFlow) { throwable, isLoaded ->
                if (throwable != null) {
                    flowOf(B2BAuthenticationState.Error(throwable))
                } else if (!isLoaded) {
                    flowOf(B2BAuthenticationState.Loading())
                } else {
                    combine(
                        sessionFlow,
                        memberFlow,
                        organizationFlow,
                        sessionTokenFlow,
                        sessionJwtFlow,
                    ) { memberSession, member, organization, sessionToken, sessionJwt ->
                        if (memberSession != null && member != null && organization != null && sessionToken != null && sessionJwt != null) {
                            B2BAuthenticationState.Authenticated(member, memberSession, organization, sessionToken, sessionJwt)
                        } else {
                            B2BAuthenticationState.Unauthenticated()
                        }
                    }
                }
            }.flatMapLatest { it }
            .stateIn(stateScope, SharingStarted.Eagerly, B2BAuthenticationState.Loading())

    override val currentSessionToken: String?
        get() = sessionTokenFlow.value

    val intermediateSessionToken: String?
        get() =
            if (istExpiration?.let { it < Clock.System.now() } ?: false) {
                return null
            } else {
                return intermediateSessionTokenFlow.value
            }

    override suspend fun <T> update(response: T) {
        if (response is AuthenticatedResponse) {
            coroutineScope {
                memberFlow.value = response.member
                sessionFlow.value = response.memberSession
                organizationFlow.value = response.organization
                sessionTokenFlow.value = response.sessionToken
                sessionJwtFlow.value = response.sessionJwt
                listOf(
                    async(dispatchers.ioDispatcher) { persistenceClient.save(MEMBER_IDENTIFIER, response.member) },
                    async(dispatchers.ioDispatcher) { persistenceClient.save(SESSION_IDENTIFIER, response.memberSession) },
                    async(dispatchers.ioDispatcher) { persistenceClient.save(ORGANIZATION_IDENTIFIER, response.organization) },
                    async(dispatchers.ioDispatcher) { persistenceClient.save(SESSION_TOKEN_IDENTIFIER, response.sessionToken) },
                    async(dispatchers.ioDispatcher) { persistenceClient.save(SESSION_JWT_IDENTIFIER, response.sessionJwt) },
                ).awaitAll()
            }
        }
    }

    suspend fun potentiallyUpdateIST(response: B2BResponse) {
        coroutineScope {
            intermediateSessionTokenFlow.value = response.intermediateSessionToken
            istExpiration =
                if (response.intermediateSessionToken?.isNotEmpty() == true) {
                    Clock.System.now() + 10.minutes
                } else {
                    null
                }
            listOf(
                async(dispatchers.ioDispatcher) { persistenceClient.save(IST_IDENTIFIER, response.intermediateSessionToken) },
                async(dispatchers.ioDispatcher) { persistenceClient.save(IST_EXPIRATION, istExpiration) },
            ).awaitAll()
        }
    }

    override suspend fun revoke() {
        coroutineScope {
            memberFlow.value = null
            sessionFlow.value = null
            organizationFlow.value = null
            sessionTokenFlow.value = null
            sessionJwtFlow.value = null
            intermediateSessionTokenFlow.value = null
            istExpiration = null
            listOf(
                async(dispatchers.ioDispatcher) { persistenceClient.remove(MEMBER_IDENTIFIER) },
                async(dispatchers.ioDispatcher) { persistenceClient.remove(SESSION_IDENTIFIER) },
                async(dispatchers.ioDispatcher) { persistenceClient.remove(ORGANIZATION_IDENTIFIER) },
                async(dispatchers.ioDispatcher) { persistenceClient.remove(SESSION_TOKEN_IDENTIFIER) },
                async(dispatchers.ioDispatcher) { persistenceClient.remove(SESSION_JWT_IDENTIFIER) },
                async(dispatchers.ioDispatcher) { persistenceClient.remove(IST_IDENTIFIER) },
                async(dispatchers.ioDispatcher) { persistenceClient.remove(IST_EXPIRATION) },
            ).awaitAll()
        }
    }

    suspend fun hydrate() {
        coroutineScope {
            listOf(
                async(dispatchers.ioDispatcher) { memberFlow.value = persistenceClient.get(MEMBER_IDENTIFIER, null) },
                async(dispatchers.ioDispatcher) { sessionFlow.value = persistenceClient.get(SESSION_IDENTIFIER, null) },
                async(dispatchers.ioDispatcher) { organizationFlow.value = persistenceClient.get(ORGANIZATION_IDENTIFIER, null) },
                async(dispatchers.ioDispatcher) { sessionTokenFlow.value = persistenceClient.get(SESSION_TOKEN_IDENTIFIER, null) },
                async(dispatchers.ioDispatcher) { sessionJwtFlow.value = persistenceClient.get(SESSION_JWT_IDENTIFIER, null) },
                async(dispatchers.ioDispatcher) { intermediateSessionTokenFlow.value = persistenceClient.get(IST_IDENTIFIER, null) },
                async(dispatchers.ioDispatcher) { istExpiration = persistenceClient.get(IST_EXPIRATION, null) },
            ).awaitAll()
            loadingStateFlow.value = true
        }
    }

    internal companion object {
        internal const val SESSION_IDENTIFIER = "stytch_session"
        internal const val SESSION_TOKEN_IDENTIFIER = "stytch_session_token"
        private const val SESSION_JWT_IDENTIFIER = "stytch_session_jwt"
        private const val MEMBER_IDENTIFIER = "stytch_member"
        private const val ORGANIZATION_IDENTIFIER = "stytch_organization"
        private const val IST_IDENTIFIER = "stytch_ist_identifier"
        private const val IST_EXPIRATION = "stytch_ist_expiration"
    }
}
