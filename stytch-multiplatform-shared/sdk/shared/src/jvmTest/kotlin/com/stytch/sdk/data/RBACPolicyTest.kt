package com.stytch.sdk.data

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

private fun makePolicy(): RBACPolicy {
    val role = RBACPolicyRole(
        roleId = "admin",
        roleDescription = "Administrator",
        permissions = listOf(
            RBACPermission(resourceId = "documents", actions = listOf("read", "write")),
            RBACPermission(resourceId = "users", actions = listOf("*")),
        ),
    )
    val resource1 = RBACPolicyResource(
        resourceId = "documents",
        resourceDescription = "Documents",
        actions = listOf("read", "write", "delete"),
    )
    val resource2 = RBACPolicyResource(
        resourceId = "users",
        resourceDescription = "Users",
        actions = listOf("read", "write", "delete"),
    )
    return RBACPolicy(roles = listOf(role), resources = listOf(resource1, resource2))
}

internal class RBACPolicyCallerIsAuthorizedTest {
    private val policy = makePolicy()

    @Test
    fun `returns true for an exact action match`() {
        assertTrue(policy.callerIsAuthorized(listOf("admin"), "documents", "read"))
    }

    @Test
    fun `returns true for a wildcard action`() {
        assertTrue(policy.callerIsAuthorized(listOf("admin"), "users", "delete"))
    }

    @Test
    fun `returns false when the role does not exist`() {
        assertFalse(policy.callerIsAuthorized(listOf("guest"), "documents", "read"))
    }

    @Test
    fun `returns false when the action is not permitted`() {
        assertFalse(policy.callerIsAuthorized(listOf("admin"), "documents", "delete"))
    }

    @Test
    fun `returns false when the resource does not match`() {
        assertFalse(policy.callerIsAuthorized(listOf("admin"), "settings", "read"))
    }

    @Test
    fun `returns false when member roles list is empty`() {
        assertFalse(policy.callerIsAuthorized(emptyList(), "documents", "read"))
    }
}

internal class RBACPolicyAllPermissionsForCallerTest {
    private val policy = makePolicy()

    @Test
    fun `returns correct map for admin role`() {
        val perms = policy.allPermissionsForCaller(listOf("admin"))

        // documents: read + write granted, delete not
        assertEquals(true, perms["documents"]?.get("read"))
        assertEquals(true, perms["documents"]?.get("write"))
        assertEquals(false, perms["documents"]?.get("delete"))

        // users: wildcard so everything granted
        assertEquals(true, perms["users"]?.get("read"))
        assertEquals(true, perms["users"]?.get("write"))
        assertEquals(true, perms["users"]?.get("delete"))
    }

    @Test
    fun `returns all false for unknown role`() {
        val perms = policy.allPermissionsForCaller(listOf("guest"))

        assertEquals(false, perms["documents"]?.get("read"))
        assertEquals(false, perms["documents"]?.get("write"))
        assertEquals(false, perms["users"]?.get("read"))
    }

    @Test
    fun `returns map covering all resources even when no roles match`() {
        val perms = policy.allPermissionsForCaller(emptyList())
        assertTrue(perms.containsKey("documents"))
        assertTrue(perms.containsKey("users"))
    }
}
