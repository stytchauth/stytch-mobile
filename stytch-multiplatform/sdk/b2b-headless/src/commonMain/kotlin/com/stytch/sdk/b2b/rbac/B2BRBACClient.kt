package com.stytch.sdk.b2b.rbac

import com.stytch.sdk.b2b.StytchB2BAuthenticationStateManager
import com.stytch.sdk.data.RBACPolicy
import com.stytch.sdk.data.StytchDispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException
import kotlin.js.JsExport

@JsExport
public interface B2BRBACClient {
    /**
     * Checks whether the logged-in member is authorized to perform [action] on [resourceId] using
     * the locally cached RBAC policy. No network request is made.
     */
    public fun isAuthorizedSync(
        resourceId: String,
        action: String,
    ): Boolean

    /**
     * Refreshes the RBAC policy from the server, then checks whether the logged-in member is
     * authorized to perform [action] on [resourceId].
     */
    @Throws(CancellationException::class)
    public suspend fun isAuthorized(
        resourceId: String,
        action: String,
    ): Boolean

    /**
     * Refreshes the RBAC policy from the server, then returns a map of all permissions for the
     * logged-in member in the form `Map<resourceId, Map<action, Boolean>>`.
     */
    @Throws(CancellationException::class)
    public suspend fun allPermissions(): Map<String, Map<String, Boolean>>
}

internal class B2BRBACClientImpl(
    private val dispatchers: StytchDispatchers,
    private val sessionManager: StytchB2BAuthenticationStateManager,
    private val getRbacPolicy: () -> RBACPolicy?,
    private val refreshAndGetRbacPolicy: suspend () -> RBACPolicy?,
) : B2BRBACClient {
    private fun memberRoles(): List<String> = sessionManager.sessionFlow.value?.roles ?: emptyList()

    override fun isAuthorizedSync(
        resourceId: String,
        action: String,
    ): Boolean = getRbacPolicy()?.callerIsAuthorized(memberRoles(), resourceId, action) ?: false

    override suspend fun isAuthorized(
        resourceId: String,
        action: String,
    ): Boolean =
        withContext(dispatchers.ioDispatcher) {
            refreshAndGetRbacPolicy()?.callerIsAuthorized(memberRoles(), resourceId, action) ?: false
        }

    override suspend fun allPermissions(): Map<String, Map<String, Boolean>> =
        withContext(dispatchers.ioDispatcher) {
            refreshAndGetRbacPolicy()?.allPermissionsForCaller(memberRoles()) ?: emptyMap()
        }
}
