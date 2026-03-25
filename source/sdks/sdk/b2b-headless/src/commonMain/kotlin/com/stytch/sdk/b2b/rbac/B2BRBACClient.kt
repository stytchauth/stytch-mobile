package com.stytch.sdk.b2b.rbac

import com.stytch.sdk.StytchApi
import com.stytch.sdk.b2b.StytchB2BAuthenticationStateManager
import com.stytch.sdk.data.RBACPolicy
import com.stytch.sdk.data.StytchDispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException
import kotlin.js.JsExport

/** Role-based access control (RBAC) authorization checks for the current member. */
@StytchApi
@JsExport
public interface B2BRBACClient {
    /**
     * Checks whether the logged-in member is authorized to perform [action] on [resourceId] using
     * the locally cached RBAC policy. No network request is made; returns `false` if no policy is
     * cached yet.
     *
     * **Kotlin:**
     * ```kotlin
     * val allowed = StytchB2B.rbac.isAuthorizedSync(
     *     resourceId = "documents",
     *     action = "read",
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let allowed = StytchB2B.rbac.isAuthorizedSync(resourceId: "documents", action: "read")
     * ```
     *
     * **React Native:**
     * ```js
     * const allowed = StytchB2B.rbac.isAuthorizedSync("documents", "read")
     * ```
     *
     * @param resourceId The ID of the resource to check authorization for.
     * @param action The action to check (e.g. `"read"`, `"write"`, `"delete"`).
     *
     * @return `true` if the member is authorized; `false` otherwise or if no policy is cached.
     */
    public fun isAuthorizedSync(
        resourceId: String,
        action: String,
    ): Boolean

    /**
     * Refreshes the RBAC policy from the server, then checks whether the logged-in member is
     * authorized to perform [action] on [resourceId].
     *
     * **Kotlin:**
     * ```kotlin
     * val allowed = StytchB2B.rbac.isAuthorized(
     *     resourceId = "documents",
     *     action = "read",
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let allowed = try await StytchB2B.rbac.isAuthorized(resourceId: "documents", action: "read")
     * ```
     *
     * **React Native:**
     * ```js
     * const allowed = await StytchB2B.rbac.isAuthorized("documents", "read")
     * ```
     *
     * @param resourceId The ID of the resource to check authorization for.
     * @param action The action to check (e.g. `"read"`, `"write"`, `"delete"`).
     *
     * @return `true` if the member is authorized; `false` otherwise.
     *
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(CancellationException::class)
    public suspend fun isAuthorized(
        resourceId: String,
        action: String,
    ): Boolean

    /**
     * Refreshes the RBAC policy from the server, then returns a map of all permissions for the
     * logged-in member in the form `Map<resourceId, Map<action, Boolean>>`.
     *
     * **Kotlin:**
     * ```kotlin
     * val permissions = StytchB2B.rbac.allPermissions()
     * ```
     *
     * **iOS:**
     * ```swift
     * let permissions = try await StytchB2B.rbac.allPermissions()
     * ```
     *
     * **React Native:**
     * ```js
     * const permissions = await StytchB2B.rbac.allPermissions()
     * ```
     *
     * @return A map of `resourceId` to `Map<action, Boolean>` representing all permissions.
     *
     * @throws [CancellationException] if the coroutine is cancelled.
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
