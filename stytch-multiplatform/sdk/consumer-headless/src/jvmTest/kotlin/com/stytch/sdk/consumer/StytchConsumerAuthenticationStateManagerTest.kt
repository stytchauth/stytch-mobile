package com.stytch.sdk.consumer

import com.stytch.sdk.consumer.data.ConsumerAuthenticationState
import com.stytch.sdk.consumer.networking.AuthenticatedResponse
import com.stytch.sdk.consumer.networking.models.ApiSessionV1Session
import com.stytch.sdk.consumer.networking.models.ApiUserV1User
import com.stytch.sdk.data.StytchDispatchers
import com.stytch.sdk.encryption.StytchEncryptionClient
import com.stytch.sdk.persistence.StytchPersistenceClient
import com.stytch.sdk.persistence.StytchPlatformPersistenceClient
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class StytchConsumerAuthenticationStateManagerTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private val dispatchers = StytchDispatchers(ioDispatcher = testDispatcher, mainDispatcher = testDispatcher)

    // StytchPersistenceClient.save/get/remove are inline — use a real instance
    // backed by identity crypto and in-memory storage.
    private val encryptionClient =
        mockk<StytchEncryptionClient> {
            every { encrypt(any()) } answers { firstArg() }
            every { decrypt(any()) } answers { firstArg() }
        }
    private val platformStore = mutableMapOf<String, String>()
    private val platformClient =
        mockk<StytchPlatformPersistenceClient> {
            every { saveData(any(), any()) } answers { platformStore[firstArg()] = secondArg() }
            every { getData(any()) } answers { platformStore[firstArg()] }
            every { removeData(any()) } answers {
                platformStore.remove(firstArg())
                Unit
            }
        }
    private val persistenceClient = StytchPersistenceClient(testDispatcher, encryptionClient, platformClient)

    private fun createManager() = StytchConsumerAuthenticationStateManager(dispatchers, persistenceClient)

    // Minimal serializable fixtures
    private val fakeUser =
        ApiUserV1User(
            userId = "user-id",
            emails = emptyList(),
            status = "active",
            phoneNumbers = emptyList(),
            webauthnRegistrations = emptyList(),
            providers = emptyList(),
            totps = emptyList(),
            cryptoWallets = emptyList(),
            biometricRegistrations = emptyList(),
            isLocked = false,
            roles = emptyList(),
        )
    private val fakeSession =
        ApiSessionV1Session(
            sessionId = "session-id",
            userId = "user-id",
            authenticationFactors = emptyList(),
            roles = emptyList(),
        )
    private val fakeToken = "fake-session-token"
    private val fakeJwt = "fake-session-jwt"

    private val fakeResponse =
        object : AuthenticatedResponse {
            override val user = fakeUser
            override val session = fakeSession
            override val sessionToken = fakeToken
            override val sessionJwt = fakeJwt
        }

    // --- authenticationStateFlow state combinations ---

    @Test
    fun `authenticationStateFlow emits Loading before init completes`() =
        runTest(testDispatcher) {
            // Use a dispatcher that doesn't run eagerly so we can observe Loading
            val blockingDispatcher = kotlinx.coroutines.test.StandardTestDispatcher(testScheduler)
            val blockingDispatchers = StytchDispatchers(ioDispatcher = blockingDispatcher, mainDispatcher = blockingDispatcher)
            val manager = StytchConsumerAuthenticationStateManager(blockingDispatchers, persistenceClient)

            val states = mutableListOf<ConsumerAuthenticationState>()
            val job = launch { manager.authenticationStateFlow.collect { states.add(it) } }

            // Before advancing, init coroutine hasn't completed — should see Loading
            assertIs<ConsumerAuthenticationState.Loading>(states.first())

            job.cancel()
        }

    @Test
    fun `authenticationStateFlow emits Unauthenticated after init with no persisted data`() =
        runTest(testDispatcher) {
            val manager = createManager()
            val states = mutableListOf<ConsumerAuthenticationState>()
            val job = launch { manager.authenticationStateFlow.collect { states.add(it) } }
            advanceUntilIdle()

            assertIs<ConsumerAuthenticationState.Unauthenticated>(states.last())
            job.cancel()
        }

    @Test
    fun `authenticationStateFlow emits Authenticated when all four values are set`() =
        runTest(testDispatcher) {
            val manager = createManager()
            val states = mutableListOf<ConsumerAuthenticationState>()
            val job = launch { manager.authenticationStateFlow.collect { states.add(it) } }
            advanceUntilIdle()

            manager.update(fakeResponse)
            advanceUntilIdle()

            val last = states.last()
            assertIs<ConsumerAuthenticationState.Authenticated>(last)
            assertEquals(fakeUser, last.user)
            assertEquals(fakeSession, last.session)
            assertEquals(fakeToken, last.sessionToken)
            assertEquals(fakeJwt, last.sessionJwt)
            job.cancel()
        }

    @Test
    fun `authenticationStateFlow emits Unauthenticated after revoke`() =
        runTest(testDispatcher) {
            val manager = createManager()
            manager.update(fakeResponse)
            advanceUntilIdle()

            val states = mutableListOf<ConsumerAuthenticationState>()
            val job = launch { manager.authenticationStateFlow.collect { states.add(it) } }
            manager.revoke()
            advanceUntilIdle()

            assertIs<ConsumerAuthenticationState.Unauthenticated>(states.last())
            job.cancel()
        }

    // --- update ---

    @Test
    fun `update with AuthenticatedResponse sets all four flow values`() =
        runTest(testDispatcher) {
            val manager = createManager()
            manager.update(fakeResponse)

            assertEquals(fakeUser, manager.userFlow.value)
            assertEquals(fakeSession, manager.sessionFlow.value)
            assertEquals(fakeToken, manager.sessionTokenFlow.value)
            assertEquals(fakeJwt, manager.sessionJwtFlow.value)
        }

    @Test
    fun `update with AuthenticatedResponse persists all four values`() =
        runTest(testDispatcher) {
            val manager = createManager()
            manager.update(fakeResponse)
            advanceUntilIdle()

            verify { platformClient.saveData(eq("stytch_user"), any()) }
            verify { platformClient.saveData(eq("stytch_session"), any()) }
            verify { platformClient.saveData(eq("stytch_session_token"), any()) }
            verify { platformClient.saveData(eq("stytch_session_jwt"), any()) }
        }

    @Test
    fun `update with non-AuthenticatedResponse does not modify flows`() =
        runTest(testDispatcher) {
            val manager = createManager()
            manager.update("some-non-authenticated-response")

            assertNull(manager.userFlow.value)
            assertNull(manager.sessionFlow.value)
            assertNull(manager.sessionTokenFlow.value)
            assertNull(manager.sessionJwtFlow.value)
        }

    // --- revoke ---

    @Test
    fun `revoke clears all four flow values`() =
        runTest(testDispatcher) {
            val manager = createManager()
            manager.update(fakeResponse)
            manager.revoke()

            assertNull(manager.userFlow.value)
            assertNull(manager.sessionFlow.value)
            assertNull(manager.sessionTokenFlow.value)
            assertNull(manager.sessionJwtFlow.value)
        }

    @Test
    fun `revoke removes all four keys from persistence`() =
        runTest(testDispatcher) {
            val manager = createManager()
            manager.update(fakeResponse)
            advanceUntilIdle()
            manager.revoke()
            advanceUntilIdle()

            verify { platformClient.removeData("stytch_user") }
            verify { platformClient.removeData("stytch_session") }
            verify { platformClient.removeData("stytch_session_token") }
            verify { platformClient.removeData("stytch_session_jwt") }
        }

    // --- currentSessionToken ---

    @Test
    fun `currentSessionToken reflects sessionTokenFlow value`() =
        runTest(testDispatcher) {
            val manager = createManager()
            assertNull(manager.currentSessionToken)

            manager.update(fakeResponse)
            assertEquals(fakeToken, manager.currentSessionToken)
        }

    @Test
    fun `currentSessionToken is null after revoke`() =
        runTest(testDispatcher) {
            val manager = createManager()
            manager.update(fakeResponse)
            manager.revoke()

            assertNull(manager.currentSessionToken)
        }

    // --- init: loading from persistence ---

    @Test
    fun `init loads all four values from persistence and emits Authenticated`() =
        runTest(testDispatcher) {
            // Pre-populate persistence with a full session by updating then re-creating the manager
            val seedManager = createManager()
            seedManager.update(fakeResponse)
            advanceUntilIdle()

            // New manager should load from persistence and produce Authenticated state
            val manager = createManager()
            advanceUntilIdle()

            val states = mutableListOf<ConsumerAuthenticationState>()
            val job = launch { manager.authenticationStateFlow.collect { states.add(it) } }
            advanceUntilIdle()

            assertIs<ConsumerAuthenticationState.Authenticated>(states.last())
            job.cancel()
        }
}
