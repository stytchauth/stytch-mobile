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

    private val fakeMember =
        ApiOrganizationV1Member(
            organizationId = "org-id",
            memberId = "member-id",
            emailAddress = "member@example.com",
            status = "active",
            name = "Test Member",
            ssoRegistrations = emptyList(),
            isBreakglass = false,
            memberPasswordId = "pwd-id",
            oauthRegistrations = emptyList(),
            emailAddressVerified = true,
            mfaPhoneNumberVerified = false,
            isAdmin = false,
            totpRegistrationId = "",
            retiredEmailAddresses = emptyList(),
            isLocked = false,
            mfaEnrolled = false,
            mfaPhoneNumber = "",
            defaultMfaMethod = "",
            roles = emptyList(),
        )
    private val fakeSession =
        ApiB2bSessionV1MemberSession(
            memberSessionId = "session-id",
            memberId = "member-id",
            startedAt = Clock.System.now(),
            lastAccessedAt = Clock.System.now(),
            expiresAt = Clock.System.now(),
            authenticationFactors = emptyList(),
            organizationId = "org-id",
            roles = emptyList(),
            organizationSlug = "test-org",
        )
    private val fakeOrganization =
        ApiOrganizationV1Organization(
            organizationId = "org-id",
            organizationName = "Test Org",
            organizationLogoUrl = "",
            organizationSlug = "test-org",
            ssoJitProvisioning = "NOT_ALLOWED",
            ssoJitProvisioningAllowedConnections = emptyList(),
            ssoActiveConnections = emptyList(),
            emailAllowedDomains = emptyList(),
            emailJitProvisioning = "NOT_ALLOWED",
            emailInvites = "ALL_ALLOWED",
            authMethods = "ALL_ALLOWED",
            allowedAuthMethods = emptyList(),
            mfaPolicy = "OPTIONAL",
            rbacEmailImplicitRoleAssignments = emptyList(),
            mfaMethods = "ALL_ALLOWED",
            allowedMfaMethods = emptyList(),
            oauthTenantJitProvisioning = "NOT_ALLOWED",
            claimedEmailDomains = emptyList(),
            firstPartyConnectedAppsAllowedType = "ALL_ALLOWED",
            allowedFirstPartyConnectedApps = emptyList(),
            thirdPartyConnectedAppsAllowedType = "ALL_ALLOWED",
            allowedThirdPartyConnectedApps = emptyList(),
            customRoles = emptyList(),
        )
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

            // Launch hydrate but don't advance — hydrate suspends on blockingDispatcher, so loadingState stays false
            launch { manager.hydrate() }

            assertIs<B2BAuthenticationState.Loading>(states.first())

            job.cancel()
        }

    @Test
    fun `authenticationStateFlow emits Unauthenticated after init with no persisted data`() =
        runTest(testDispatcher) {
            val manager = createManager()
            manager.hydrate()
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
            manager.hydrate()
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
            manager.hydrate()
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
            verify { platformClient.saveData(eq("stytch_organization"), any()) }
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
            verify { platformClient.removeData("stytch_organization") }
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
            seedManager.hydrate()
            seedManager.update(fakeResponse)
            advanceUntilIdle()

            val manager = createManager()
            manager.hydrate()
            advanceUntilIdle()

            val states = mutableListOf<B2BAuthenticationState>()
            val job = launch { manager.authenticationStateFlow.collect { states.add(it) } }
            advanceUntilIdle()

            assertIs<B2BAuthenticationState.Authenticated>(states.last())
            job.cancel()
        }
}
