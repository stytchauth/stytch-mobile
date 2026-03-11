package com.stytch.sdk.b2b

import com.stytch.sdk.b2b.data.B2BAuthenticationState
import com.stytch.sdk.b2b.networking.AuthenticatedResponse
import com.stytch.sdk.b2b.networking.B2BResponse
import com.stytch.sdk.b2b.networking.models.ApiB2bSessionV1MemberSession
import com.stytch.sdk.b2b.networking.models.ApiOrganizationV1Member
import com.stytch.sdk.b2b.networking.models.ApiOrganizationV1Organization
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
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes

@OptIn(ExperimentalCoroutinesApi::class)
class StytchB2BAuthenticationStateManagerTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private val dispatchers = StytchDispatchers(ioDispatcher = testDispatcher, mainDispatcher = testDispatcher)

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

    private fun createManager() = StytchB2BAuthenticationStateManager(dispatchers, persistenceClient)

    private val fakeMember = mockk<ApiOrganizationV1Member>(relaxed = true)
    private val fakeSession = mockk<ApiB2bSessionV1MemberSession>(relaxed = true)
    private val fakeOrganization = mockk<ApiOrganizationV1Organization>(relaxed = true)
    private val fakeToken = "fake-session-token"
    private val fakeJwt = "fake-session-jwt"

    private val fakeResponse =
        object : AuthenticatedResponse {
            override val member = fakeMember
            override val memberSession = fakeSession
            override val organization = fakeOrganization
            override val sessionToken = fakeToken
            override val sessionJwt = fakeJwt
        }

    // --- authenticationStateFlow state combinations ---

    @Test
    fun `authenticationStateFlow emits Loading before init completes`() =
        runTest(testDispatcher) {
            val blockingDispatcher = kotlinx.coroutines.test.StandardTestDispatcher(testScheduler)
            val blockingDispatchers = StytchDispatchers(ioDispatcher = blockingDispatcher, mainDispatcher = blockingDispatcher)
            val manager = StytchB2BAuthenticationStateManager(blockingDispatchers, persistenceClient)

            val states = mutableListOf<B2BAuthenticationState>()
            val job = launch { manager.authenticationStateFlow.collect { states.add(it) } }

            assertIs<B2BAuthenticationState.Loading>(states.first())

            job.cancel()
        }

    @Test
    fun `authenticationStateFlow emits Unauthenticated after init with no persisted data`() =
        runTest(testDispatcher) {
            val manager = createManager()
            val states = mutableListOf<B2BAuthenticationState>()
            val job = launch { manager.authenticationStateFlow.collect { states.add(it) } }
            advanceUntilIdle()

            assertIs<B2BAuthenticationState.Unauthenticated>(states.last())
            job.cancel()
        }

    @Test
    fun `authenticationStateFlow emits Authenticated when all five values are set`() =
        runTest(testDispatcher) {
            val manager = createManager()
            val states = mutableListOf<B2BAuthenticationState>()
            val job = launch { manager.authenticationStateFlow.collect { states.add(it) } }
            advanceUntilIdle()

            manager.update(fakeResponse)
            advanceUntilIdle()

            val last = states.last()
            assertIs<B2BAuthenticationState.Authenticated>(last)
            assertEquals(fakeMember, last.member)
            assertEquals(fakeSession, last.memberSession)
            assertEquals(fakeOrganization, last.organization)
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

            val states = mutableListOf<B2BAuthenticationState>()
            val job = launch { manager.authenticationStateFlow.collect { states.add(it) } }
            manager.revoke()
            advanceUntilIdle()

            assertIs<B2BAuthenticationState.Unauthenticated>(states.last())
            job.cancel()
        }

    // --- update ---

    @Test
    fun `update with AuthenticatedResponse sets all five flow values`() =
        runTest(testDispatcher) {
            val manager = createManager()
            manager.update(fakeResponse)

            assertEquals(fakeMember, manager.memberFlow.value)
            assertEquals(fakeSession, manager.sessionFlow.value)
            assertEquals(fakeOrganization, manager.organizationFlow.value)
            assertEquals(fakeToken, manager.sessionTokenFlow.value)
            assertEquals(fakeJwt, manager.sessionJwtFlow.value)
        }

    @Test
    fun `update with AuthenticatedResponse persists all five values`() =
        runTest(testDispatcher) {
            val manager = createManager()
            manager.update(fakeResponse)
            advanceUntilIdle()

            verify { platformClient.saveData(eq("stytch_member"), any()) }
            verify { platformClient.saveData(eq("stytch_session"), any()) }
            verify { platformClient.saveData(eq("stytch_session_token"), any()) }
            verify { platformClient.saveData(eq("stytch_session_jwt"), any()) }
        }

    @Test
    fun `update with non-AuthenticatedResponse does not modify flows`() =
        runTest(testDispatcher) {
            val manager = createManager()
            manager.update("some-non-authenticated-response")

            assertNull(manager.memberFlow.value)
            assertNull(manager.sessionFlow.value)
            assertNull(manager.organizationFlow.value)
            assertNull(manager.sessionTokenFlow.value)
            assertNull(manager.sessionJwtFlow.value)
        }

    // --- revoke ---

    @Test
    fun `revoke clears all flows including IST`() =
        runTest(testDispatcher) {
            val manager = createManager()
            manager.update(fakeResponse)
            manager.potentiallyUpdateIST(
                object : B2BResponse {
                    override val intermediateSessionToken = "ist-tok"
                },
            )
            manager.revoke()

            assertNull(manager.memberFlow.value)
            assertNull(manager.sessionFlow.value)
            assertNull(manager.organizationFlow.value)
            assertNull(manager.sessionTokenFlow.value)
            assertNull(manager.sessionJwtFlow.value)
            assertNull(manager.intermediateSessionTokenFlow.value)
            assertNull(manager.istExpiration)
        }

    @Test
    fun `revoke removes all keys from persistence including IST keys`() =
        runTest(testDispatcher) {
            val manager = createManager()
            manager.update(fakeResponse)
            advanceUntilIdle()
            manager.revoke()
            advanceUntilIdle()

            verify { platformClient.removeData("stytch_member") }
            verify { platformClient.removeData("stytch_session") }
            verify { platformClient.removeData("stytch_session_token") }
            verify { platformClient.removeData("stytch_session_jwt") }
            verify { platformClient.removeData("stytch_ist_identifier") }
            verify { platformClient.removeData("stytch_ist_expiration") }
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

    // --- potentiallyUpdateIST ---

    @Test
    fun `potentiallyUpdateIST sets intermediateSessionTokenFlow and expiration when token is non-empty`() =
        runTest(testDispatcher) {
            val manager = createManager()
            manager.potentiallyUpdateIST(
                object : B2BResponse {
                    override val intermediateSessionToken = "ist-tok"
                },
            )

            assertEquals("ist-tok", manager.intermediateSessionTokenFlow.value)
            assertNotNull(manager.istExpiration)
        }

    @Test
    fun `potentiallyUpdateIST clears expiration when token is null`() =
        runTest(testDispatcher) {
            val manager = createManager()
            manager.potentiallyUpdateIST(
                object : B2BResponse {
                    override val intermediateSessionToken = "ist-tok"
                },
            )
            manager.potentiallyUpdateIST(
                object : B2BResponse {
                    override val intermediateSessionToken = null
                },
            )

            assertNull(manager.intermediateSessionTokenFlow.value)
            assertNull(manager.istExpiration)
        }

    @Test
    fun `potentiallyUpdateIST persists IST and expiration`() =
        runTest(testDispatcher) {
            val manager = createManager()
            manager.potentiallyUpdateIST(
                object : B2BResponse {
                    override val intermediateSessionToken = "ist-tok"
                },
            )
            advanceUntilIdle()

            verify { platformClient.saveData(eq("stytch_ist_identifier"), any()) }
            verify { platformClient.saveData(eq("stytch_ist_expiration"), any()) }
        }

    // --- intermediateSessionToken computed property ---

    @Test
    fun `intermediateSessionToken returns value when not expired`() =
        runTest(testDispatcher) {
            val manager = createManager()
            manager.potentiallyUpdateIST(
                object : B2BResponse {
                    override val intermediateSessionToken = "ist-tok"
                },
            )

            assertEquals("ist-tok", manager.intermediateSessionToken)
        }

    @Test
    fun `intermediateSessionToken returns null when expiration is in the past`() =
        runTest(testDispatcher) {
            val manager = createManager()
            manager.potentiallyUpdateIST(
                object : B2BResponse {
                    override val intermediateSessionToken = "ist-tok"
                },
            )
            // Manually backdate the expiration to the past
            manager.istExpiration = Clock.System.now() - 1.minutes

            assertNull(manager.intermediateSessionToken)
        }

    // --- init: loading from persistence ---

    @Test
    fun `init loads all values from persistence and emits Authenticated`() =
        runTest(testDispatcher) {
            val seedManager = createManager()
            seedManager.update(fakeResponse)
            advanceUntilIdle()

            val manager = createManager()
            advanceUntilIdle()

            val states = mutableListOf<B2BAuthenticationState>()
            val job = launch { manager.authenticationStateFlow.collect { states.add(it) } }
            advanceUntilIdle()

            assertIs<B2BAuthenticationState.Authenticated>(states.last())
            job.cancel()
        }
}
