package com.stytch.sdk.b2b.organizations

import com.stytch.sdk.StytchApi
import com.stytch.sdk.b2b.networking.B2BNetworkingClient
import com.stytch.sdk.b2b.networking.models.B2BOrganizationsDeleteResponse
import com.stytch.sdk.b2b.networking.models.B2BOrganizationsGetResponse
import com.stytch.sdk.b2b.networking.models.B2BOrganizationsUpdateResponse
import com.stytch.sdk.b2b.networking.models.IB2BOrganizationsUpdateParameters
import com.stytch.sdk.b2b.networking.models.toNetworkModel
import com.stytch.sdk.data.StytchDispatchers
import com.stytch.sdk.data.StytchError
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException
import kotlin.js.JsExport

/** Organization management methods for the current member's organization. */
@StytchApi
@JsExport
public interface B2BOrganizationsClient {
    /**
     * Fetches the current member's organization and its settings.
     * Calls the `GET /sdk/v1/b2b/organizations/me` endpoint. Requires an active session.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchB2B.organizations.get()
     * ```
     *
     * **iOS:**
     * ```swift
     * let response = try await StytchB2B.organizations.get()
     * ```
     *
     * **React Native:**
     * ```js
     * StytchB2B.organizations.get()
     * ```
     *
     * @return [B2BOrganizationsGetResponse] containing the organization object.
     *
     * @throws [StytchError] if the request fails or no active session exists.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun get(): B2BOrganizationsGetResponse

    /**
     * Updates settings on the current member's organization.
     * Calls the `PUT /sdk/v1/b2b/organizations/me` endpoint. Requires an active session and
     * appropriate RBAC permissions.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchB2B.organizations.update(
     *     B2BOrganizationsUpdateParameters(organizationName = "Acme Corp")
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = B2BOrganizationsUpdateParameters(organizationName: "Acme Corp")
     * let response = try await StytchB2B.organizations.update(params)
     * ```
     *
     * **React Native:**
     * ```js
     * StytchB2B.organizations.update({ organizationName: "Acme Corp" })
     * ```
     *
     * @param request - [IB2BOrganizationsUpdateParameters]
     *   - `organizationName?` — Updated display name for the organization.
     *   - `organizationSlug?` — Updated URL slug for the organization.
     *   - `organizationLogoUrl?` — Updated logo URL.
     *   - `ssoDefaultConnectionId?` — Default SSO connection ID.
     *   - `ssoJitProvisioning?` — JIT provisioning policy for SSO (`"ALL_ALLOWED"`, `"RESTRICTED"`, `"NOT_ALLOWED"`).
     *   - `ssoJitProvisioningAllowedConnections?` — List of SSO connection IDs allowed for JIT provisioning.
     *   - `emailAllowedDomains?` — Allowed email domains for the organization.
     *   - `emailJitProvisioning?` — JIT provisioning policy for email-based auth.
     *   - `emailInvites?` — Email invite policy (`"ALL_ALLOWED"`, `"RESTRICTED"`, `"NOT_ALLOWED"`).
     *   - `authMethods?` — Authentication methods policy.
     *   - `allowedAuthMethods?` — List of allowed auth methods.
     *   - `mfaPolicy?` — MFA requirement policy.
     *   - `mfaMethods?` — MFA methods policy.
     *   - `allowedMfaMethods?` — List of allowed MFA methods.
     *   - Additional organization-level configuration fields.
     *
     * @return [B2BOrganizationsUpdateResponse] containing the updated organization.
     *
     * @throws [StytchError] if the request fails or the caller lacks permission.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun update(request: IB2BOrganizationsUpdateParameters): B2BOrganizationsUpdateResponse

    /**
     * Permanently deletes the current member's organization and all associated data.
     * Calls the `DELETE /sdk/v1/b2b/organizations/me` endpoint. Requires an active session and
     * appropriate RBAC permissions. This action is irreversible.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchB2B.organizations.delete()
     * ```
     *
     * **iOS:**
     * ```swift
     * let response = try await StytchB2B.organizations.delete()
     * ```
     *
     * **React Native:**
     * ```js
     * StytchB2B.organizations.delete()
     * ```
     *
     * @return [B2BOrganizationsDeleteResponse] confirming the organization was deleted.
     *
     * @throws [StytchError] if the request fails or the caller lacks permission.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun delete(): B2BOrganizationsDeleteResponse
}

internal class B2BOrganizationsClientImpl(
    private val dispatchers: StytchDispatchers,
    private val networkingClient: B2BNetworkingClient,
) : B2BOrganizationsClient {
    @Throws(StytchError::class, CancellationException::class)
    override suspend fun get(): B2BOrganizationsGetResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request { networkingClient.api.b2BOrganizationsGet() }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun update(request: IB2BOrganizationsUpdateParameters): B2BOrganizationsUpdateResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request { networkingClient.api.b2BOrganizationsUpdate(request.toNetworkModel()) }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun delete(): B2BOrganizationsDeleteResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request { networkingClient.api.b2BOrganizationsDelete() }
        }
}
