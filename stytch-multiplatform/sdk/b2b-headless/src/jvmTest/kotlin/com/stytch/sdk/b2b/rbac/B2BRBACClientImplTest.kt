package com.stytch.sdk.b2b.rbac

import com.stytch.sdk.b2b.StytchB2BAuthenticationStateManager
import com.stytch.sdk.b2b.networking.models.ApiB2bSessionV1MemberSession
import com.stytch.sdk.data.RBACPermission
import com.stytch.sdk.data.RBACPolicy
import com.stytch.sdk.data.RBACPolicyResource
import com.stytch.sdk.data.RBACPolicyRole
import com.stytch.sdk.data.StytchDispatchers
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
internal class B2BRBACClientImplTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private val dispatchers = StytchDispatchers(ioDispatcher = testDispatcher, mainDispatcher = testDispatcher)
    private val sessionManager = mockk<StytchB2BAuthenticationStateManager>(relaxed = true)

    private val adminSession =
        mockk<ApiB2bSessionV1MemberSession>(relaxed = true) {
            every { roles } returns listOf("admin")
        }

    private val policy =
        RBACPolicy(
            roles =
                listOf(
                    RBACPolicyRole(
                        roleId = "admin",
                        roleDescription = "Administrator",
                        permissions =
                            listOf(
                                RBACPermission(resourceId = "documents", actions = listOf("read", "write")),
                            ),
                    ),
                ),
            resources =
                listOf(
                    RBACPolicyResource(
                        resourceId = "documents",
                        resourceDescription = "Documents",
                        actions = listOf("read", "write", "delete"),
                    ),
                ),
        )

    private fun makeClient(
        cachedPolicy: RBACPolicy? = null,
        refreshedPolicy: RBACPolicy? = null,
    ) = B2BRBACClientImpl(
        dispatchers = dispatchers,
        sessionManager = sessionManager,
        getRbacPolicy = { cachedPolicy },
        refreshAndGetRbacPolicy = { refreshedPolicy },
    )

    // --- isAuthorizedSync ---

    @Test
    fun `isAuthorizedSync returns true when member has permission (cached policy)`() {
        every { sessionManager.sessionFlow } returns MutableStateFlow(adminSession)
        val client = makeClient(cachedPolicy = policy)

        assertTrue(client.isAuthorizedSync("documents", "read"))
    }

    @Test
    fun `isAuthorizedSync returns false when action not permitted`() {
        every { sessionManager.sessionFlow } returns MutableStateFlow(adminSession)
        val client = makeClient(cachedPolicy = policy)

        assertFalse(client.isAuthorizedSync("documents", "delete"))
    }

    @Test
    fun `isAuthorizedSync returns false when policy is null`() {
        every { sessionManager.sessionFlow } returns MutableStateFlow(adminSession)
        val client = makeClient(cachedPolicy = null)

        assertFalse(client.isAuthorizedSync("documents", "read"))
    }

    @Test
    fun `isAuthorizedSync returns false when session has no roles`() {
        every { sessionManager.sessionFlow } returns MutableStateFlow(null)
        val client = makeClient(cachedPolicy = policy)

        assertFalse(client.isAuthorizedSync("documents", "read"))
    }

    // --- isAuthorized ---

    @Test
    fun `isAuthorized returns true after refreshing policy`() =
        runTest(testDispatcher) {
            every { sessionManager.sessionFlow } returns MutableStateFlow(adminSession)
            val client = makeClient(refreshedPolicy = policy)

            assertTrue(client.isAuthorized("documents", "read"))
        }

    @Test
    fun `isAuthorized returns false when refreshed policy is null`() =
        runTest(testDispatcher) {
            every { sessionManager.sessionFlow } returns MutableStateFlow(adminSession)
            val client = makeClient(refreshedPolicy = null)

            assertFalse(client.isAuthorized("documents", "read"))
        }

    // --- allPermissions ---

    @Test
    fun `allPermissions returns correct map after refreshing policy`() =
        runTest(testDispatcher) {
            every { sessionManager.sessionFlow } returns MutableStateFlow(adminSession)
            val client = makeClient(refreshedPolicy = policy)

            val perms = client.allPermissions()

            assertEquals(true, perms["documents"]?.get("read"))
            assertEquals(true, perms["documents"]?.get("write"))
            assertEquals(false, perms["documents"]?.get("delete"))
        }

    @Test
    fun `allPermissions returns empty map when refreshed policy is null`() =
        runTest(testDispatcher) {
            every { sessionManager.sessionFlow } returns MutableStateFlow(adminSession)
            val client = makeClient(refreshedPolicy = null)

            val perms = client.allPermissions()

            assertTrue(perms.isEmpty())
        }
}
