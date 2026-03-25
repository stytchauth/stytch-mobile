package com.stytch.sdk.b2b.members

import com.stytch.sdk.StytchApi
import com.stytch.sdk.b2b.networking.B2BNetworkingClient
import com.stytch.sdk.b2b.networking.models.B2BGetMeResponse
import com.stytch.sdk.b2b.networking.models.IOrganizationsAdminMemberStartEmailUpdateParameters
import com.stytch.sdk.b2b.networking.models.IOrganizationsAdminMemberUnlinkRetiredEmailParameters
import com.stytch.sdk.b2b.networking.models.IOrganizationsAdminMemberUpdateParameters
import com.stytch.sdk.b2b.networking.models.IOrganizationsMemberCreateParameters
import com.stytch.sdk.b2b.networking.models.IOrganizationsMemberSearchParameters
import com.stytch.sdk.b2b.networking.models.IOrganizationsMemberStartEmailUpdateParameters
import com.stytch.sdk.b2b.networking.models.IOrganizationsMemberUnlinkRetiredEmailParameters
import com.stytch.sdk.b2b.networking.models.IOrganizationsMemberUpdateParameters
import com.stytch.sdk.b2b.networking.models.OrganizationsAdminMemberDeleteMFAPhoneNumberResponse
import com.stytch.sdk.b2b.networking.models.OrganizationsAdminMemberDeleteMFATOTPResponse
import com.stytch.sdk.b2b.networking.models.OrganizationsAdminMemberDeletePasswordResponse
import com.stytch.sdk.b2b.networking.models.OrganizationsAdminMemberDeleteResponse
import com.stytch.sdk.b2b.networking.models.OrganizationsAdminMemberReactivateResponse
import com.stytch.sdk.b2b.networking.models.OrganizationsAdminMemberStartEmailUpdateResponse
import com.stytch.sdk.b2b.networking.models.OrganizationsAdminMemberUnlinkRetiredEmailResponse
import com.stytch.sdk.b2b.networking.models.OrganizationsAdminMemberUpdateResponse
import com.stytch.sdk.b2b.networking.models.OrganizationsMemberCreateResponse
import com.stytch.sdk.b2b.networking.models.OrganizationsMemberDeleteMFAPhoneNumberResponse
import com.stytch.sdk.b2b.networking.models.OrganizationsMemberDeleteMFATOTPResponse
import com.stytch.sdk.b2b.networking.models.OrganizationsMemberSearchResponse
import com.stytch.sdk.b2b.networking.models.OrganizationsMemberStartEmailUpdateResponse
import com.stytch.sdk.b2b.networking.models.OrganizationsMemberUnlinkRetiredEmailResponse
import com.stytch.sdk.b2b.networking.models.OrganizationsMemberUpdateResponse
import com.stytch.sdk.b2b.networking.models.toNetworkModel
import com.stytch.sdk.data.StytchDispatchers
import com.stytch.sdk.data.StytchError
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException
import kotlin.js.JsExport

/** B2B member management methods for the authenticated member and organization members. */
@StytchApi
@JsExport
public interface B2BMembersClient {
    /** Admin-level member management methods requiring elevated permissions. */
    public val admin: B2BMembersAdminClient

    /**
     * Returns the currently authenticated member and their organization.
     * Calls the `GET /sdk/v1/b2b/organizations/members/me` endpoint. Requires an active session.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchB2B.members.me()
     * ```
     *
     * **iOS:**
     * ```swift
     * let response = try await StytchB2B.members.me()
     * ```
     *
     * **React Native:**
     * ```js
     * StytchB2B.members.me()
     * ```
     *
     * @return [B2BGetMeResponse] containing the current member and organization objects.
     *
     * @throws [StytchError] if the request fails or no active session exists.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun me(): B2BGetMeResponse

    /**
     * Updates profile fields on the currently authenticated member.
     * Calls the `PUT /sdk/v1/b2b/organizations/members/update` endpoint. Requires an active session.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchB2B.members.update(
     *     OrganizationsMemberUpdateParameters(name = "Jane Doe")
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = OrganizationsMemberUpdateParameters(name: "Jane Doe")
     * let response = try await StytchB2B.members.update(params)
     * ```
     *
     * **React Native:**
     * ```js
     * StytchB2B.members.update({ name: "Jane Doe" })
     * ```
     *
     * @param request - [IOrganizationsMemberUpdateParameters]
     *   - `name?` — Updated display name for the member.
     *   - `mfaEnrolled?` — Whether MFA is enrolled for this member.
     *   - `mfaPhoneNumber?` — Updated MFA phone number.
     *   - `untrustedMetadata?` — Client-settable key-value metadata.
     *   - `defaultMfaMethod?` — The default MFA method for this member.
     *
     * @return [OrganizationsMemberUpdateResponse] containing the updated member object.
     *
     * @throws [StytchError] if the request fails.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun update(request: IOrganizationsMemberUpdateParameters): OrganizationsMemberUpdateResponse

    /**
     * Searches for members within the organization matching the provided criteria.
     * Calls the `POST /sdk/v1/b2b/organizations/me/members/search` endpoint. Requires an active
     * session and appropriate RBAC permissions.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchB2B.members.search(
     *     OrganizationsMemberSearchParameters(cursor = "")
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = OrganizationsMemberSearchParameters(cursor: "")
     * let response = try await StytchB2B.members.search(params)
     * ```
     *
     * **React Native:**
     * ```js
     * StytchB2B.members.search({ cursor: "" })
     * ```
     *
     * @param request - [IOrganizationsMemberSearchParameters]
     *   - `cursor` — Pagination cursor from a previous response; pass an empty string to start from the beginning.
     *   - `limit?` — Maximum number of members to return per page.
     *   - `query?` — Search query to filter results.
     *
     * @return [OrganizationsMemberSearchResponse] containing a page of matching members.
     *
     * @throws [StytchError] if the request fails.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun search(request: IOrganizationsMemberSearchParameters): OrganizationsMemberSearchResponse

    /**
     * Creates a new member in the organization.
     * Calls the `POST /sdk/v1/b2b/organizations/members` endpoint. Requires an active session and
     * appropriate RBAC permissions.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchB2B.members.create(
     *     OrganizationsMemberCreateParameters(
     *         emailAddress = "newmember@example.com",
     *         isBreakglass = false,
     *         createMemberAsPending = false,
     *         roles = listOf("member"),
     *     )
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = OrganizationsMemberCreateParameters(
     *     emailAddress: "newmember@example.com",
     *     isBreakglass: false,
     *     createMemberAsPending: false,
     *     roles: ["member"]
     * )
     * let response = try await StytchB2B.members.create(params)
     * ```
     *
     * **React Native:**
     * ```js
     * StytchB2B.members.create({
     *     emailAddress: "newmember@example.com",
     *     isBreakglass: false,
     *     createMemberAsPending: false,
     *     roles: ["member"],
     * })
     * ```
     *
     * @param request - [IOrganizationsMemberCreateParameters]
     *   - `emailAddress` — The email address of the new member.
     *   - `isBreakglass` — Whether this is a breakglass (emergency access) account.
     *   - `createMemberAsPending` — Whether to create the member in a pending state.
     *   - `roles` — List of RBAC role IDs to assign to the member.
     *   - `name?` — Display name for the new member.
     *   - `mfaEnrolled?` — Whether MFA is enrolled.
     *   - `mfaPhoneNumber?` — MFA phone number.
     *   - `untrustedMetadata?` — Client-settable key-value metadata.
     *
     * @return [OrganizationsMemberCreateResponse] containing the newly created member.
     *
     * @throws [StytchError] if the request fails or the email is already in use.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun create(request: IOrganizationsMemberCreateParameters): OrganizationsMemberCreateResponse

    /**
     * Removes the MFA phone number from the currently authenticated member's account.
     * Calls the `DELETE /sdk/v1/b2b/organizations/members/deletePhoneNumber` endpoint. Requires
     * an active session.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchB2B.members.deleteMFAPhoneNumber()
     * ```
     *
     * **iOS:**
     * ```swift
     * let response = try await StytchB2B.members.deleteMFAPhoneNumber()
     * ```
     *
     * **React Native:**
     * ```js
     * StytchB2B.members.deleteMFAPhoneNumber()
     * ```
     *
     * @return [OrganizationsMemberDeleteMFAPhoneNumberResponse] containing the updated member object.
     *
     * @throws [StytchError] if the request fails.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun deleteMFAPhoneNumber(): OrganizationsMemberDeleteMFAPhoneNumberResponse

    /**
     * Removes the TOTP authenticator registration from the currently authenticated member's account.
     * Calls the `DELETE /sdk/v1/b2b/organizations/members/deleteTOTP` endpoint. Requires an active
     * session.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchB2B.members.deleteMFATOTP()
     * ```
     *
     * **iOS:**
     * ```swift
     * let response = try await StytchB2B.members.deleteMFATOTP()
     * ```
     *
     * **React Native:**
     * ```js
     * StytchB2B.members.deleteMFATOTP()
     * ```
     *
     * @return [OrganizationsMemberDeleteMFATOTPResponse] containing the updated member object.
     *
     * @throws [StytchError] if the request fails.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun deleteMFATOTP(): OrganizationsMemberDeleteMFATOTPResponse

    /**
     * Initiates an email address update for the currently authenticated member by sending a
     * verification link to the new address. Calls the
     * `POST /sdk/v1/b2b/organizations/members/start_email_update` endpoint.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchB2B.members.startEmailUpdate(
     *     OrganizationsMemberStartEmailUpdateParameters(emailAddress = "newemail@example.com")
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = OrganizationsMemberStartEmailUpdateParameters(emailAddress: "newemail@example.com")
     * let response = try await StytchB2B.members.startEmailUpdate(params)
     * ```
     *
     * **React Native:**
     * ```js
     * StytchB2B.members.startEmailUpdate({ emailAddress: "newemail@example.com" })
     * ```
     *
     * @param request - [IOrganizationsMemberStartEmailUpdateParameters]
     *   - `emailAddress` — The new email address to update to.
     *   - `loginRedirectUrl?` — URL to redirect to after the email is verified.
     *   - `locale?` — Locale for the verification email.
     *   - `loginTemplateId?` — Custom email template ID.
     *   - `deliveryMethod?` — Delivery method for the verification (e.g. `"magic_link"`, `"otp"`).
     *
     * @return [OrganizationsMemberStartEmailUpdateResponse] confirming the verification was sent.
     *
     * @throws [StytchError] if the request fails.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun startEmailUpdate(
        request: IOrganizationsMemberStartEmailUpdateParameters,
    ): OrganizationsMemberStartEmailUpdateResponse

    /**
     * Removes a retired (previously used) email address from the currently authenticated member's
     * account. Calls the `POST /sdk/v1/b2b/organizations/members/unlink_retired_email` endpoint.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchB2B.members.unlinkRetiredEmail(
     *     OrganizationsMemberUnlinkRetiredEmailParameters(emailAddress = "old@example.com")
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = OrganizationsMemberUnlinkRetiredEmailParameters(emailAddress: "old@example.com")
     * let response = try await StytchB2B.members.unlinkRetiredEmail(params)
     * ```
     *
     * **React Native:**
     * ```js
     * StytchB2B.members.unlinkRetiredEmail({ emailAddress: "old@example.com" })
     * ```
     *
     * @param request - [IOrganizationsMemberUnlinkRetiredEmailParameters]
     *   - `emailId?` — The ID of the retired email to unlink.
     *   - `emailAddress?` — The address of the retired email to unlink.
     *
     * @return [OrganizationsMemberUnlinkRetiredEmailResponse] containing the updated member object.
     *
     * @throws [StytchError] if the request fails.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun unlinkRetiredEmail(
        request: IOrganizationsMemberUnlinkRetiredEmailParameters,
    ): OrganizationsMemberUnlinkRetiredEmailResponse
}

/** Admin-level operations on organization members, requiring appropriate RBAC permissions. */
@StytchApi
@JsExport
public interface B2BMembersAdminClient {
    /**
     * Updates profile fields on any member in the organization.
     * Calls the `PUT /sdk/v1/b2b/organizations/members/{member_id}` endpoint. Requires an active
     * session and appropriate RBAC permissions.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchB2B.members.admin.update(
     *     memberId = "member-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *     request = OrganizationsAdminMemberUpdateParameters(
     *         preserveExistingSessions = true,
     *         name = "Jane Doe",
     *     ),
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = OrganizationsAdminMemberUpdateParameters(preserveExistingSessions: true, name: "Jane Doe")
     * let response = try await StytchB2B.members.admin.update(
     *     memberId: "member-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *     request: params
     * )
     * ```
     *
     * **React Native:**
     * ```js
     * StytchB2B.members.admin.update(
     *     "member-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *     { preserveExistingSessions: true, name: "Jane Doe" }
     * )
     * ```
     *
     * @param memberId The unique ID of the member to update.
     * @param request - [IOrganizationsAdminMemberUpdateParameters]
     *   - `preserveExistingSessions` — Whether to keep existing sessions active after the update.
     *   - `name?` — Updated display name.
     *   - `mfaEnrolled?` — Whether MFA is enrolled.
     *   - `mfaPhoneNumber?` — Updated MFA phone number.
     *   - `untrustedMetadata?` — Client-settable key-value metadata.
     *   - `isBreakglass?` — Whether this is a breakglass account.
     *   - `roles?` — List of RBAC role IDs to assign.
     *   - `defaultMfaMethod?` — The default MFA method.
     *   - `emailAddress?` — Updated email address.
     *   - `unlinkEmail?` — Whether to unlink the current primary email.
     *
     * @return [OrganizationsAdminMemberUpdateResponse] containing the updated member object.
     *
     * @throws [StytchError] if the request fails or the caller lacks permission.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun update(
        memberId: String,
        request: IOrganizationsAdminMemberUpdateParameters,
    ): OrganizationsAdminMemberUpdateResponse

    /**
     * Permanently deletes the specified member from the organization.
     * Calls the `DELETE /sdk/v1/b2b/organizations/members/{member_id}` endpoint. Requires an active
     * session and appropriate RBAC permissions. This action is irreversible.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchB2B.members.admin.delete("member-test-d5a3b680-e8a3-40c0-b815-ab79986666d0")
     * ```
     *
     * **iOS:**
     * ```swift
     * let response = try await StytchB2B.members.admin.delete("member-test-d5a3b680-e8a3-40c0-b815-ab79986666d0")
     * ```
     *
     * **React Native:**
     * ```js
     * StytchB2B.members.admin.delete("member-test-d5a3b680-e8a3-40c0-b815-ab79986666d0")
     * ```
     *
     * @param memberId The unique ID of the member to delete.
     *
     * @return [OrganizationsAdminMemberDeleteResponse] confirming the member was deleted.
     *
     * @throws [StytchError] if the request fails or the caller lacks permission.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun delete(memberId: String): OrganizationsAdminMemberDeleteResponse

    /**
     * Removes the MFA phone number from the specified member's account.
     * Calls the `DELETE /sdk/v1/b2b/organizations/members/mfa_phone_numbers/{member_id}` endpoint.
     * Requires an active session and appropriate RBAC permissions.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchB2B.members.admin.deleteMFAPhoneNumber("member-test-d5a3b680-e8a3-40c0-b815-ab79986666d0")
     * ```
     *
     * **iOS:**
     * ```swift
     * let response = try await StytchB2B.members.admin.deleteMFAPhoneNumber("member-test-d5a3b680-e8a3-40c0-b815-ab79986666d0")
     * ```
     *
     * **React Native:**
     * ```js
     * StytchB2B.members.admin.deleteMFAPhoneNumber("member-test-d5a3b680-e8a3-40c0-b815-ab79986666d0")
     * ```
     *
     * @param memberId The unique ID of the member whose MFA phone number to remove.
     *
     * @return [OrganizationsAdminMemberDeleteMFAPhoneNumberResponse] containing the updated member object.
     *
     * @throws [StytchError] if the request fails or the caller lacks permission.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun deleteMFAPhoneNumber(memberId: String): OrganizationsAdminMemberDeleteMFAPhoneNumberResponse

    /**
     * Removes the TOTP authenticator registration from the specified member's account.
     * Calls the `DELETE /sdk/v1/b2b/organizations/members/totp/{member_id}` endpoint. Requires an
     * active session and appropriate RBAC permissions.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchB2B.members.admin.deleteMFATOTP("member-test-d5a3b680-e8a3-40c0-b815-ab79986666d0")
     * ```
     *
     * **iOS:**
     * ```swift
     * let response = try await StytchB2B.members.admin.deleteMFATOTP("member-test-d5a3b680-e8a3-40c0-b815-ab79986666d0")
     * ```
     *
     * **React Native:**
     * ```js
     * StytchB2B.members.admin.deleteMFATOTP("member-test-d5a3b680-e8a3-40c0-b815-ab79986666d0")
     * ```
     *
     * @param memberId The unique ID of the member whose TOTP registration to remove.
     *
     * @return [OrganizationsAdminMemberDeleteMFATOTPResponse] containing the updated member object.
     *
     * @throws [StytchError] if the request fails or the caller lacks permission.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun deleteMFATOTP(memberId: String): OrganizationsAdminMemberDeleteMFATOTPResponse

    /**
     * Deletes the specified password credential from the organization.
     * Calls the `DELETE /sdk/v1/b2b/organizations/members/passwords/{member_password_id}` endpoint.
     * Requires an active session and appropriate RBAC permissions.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchB2B.members.admin.deletePassword("member-password-test-d5a3b680-e8a3-40c0-b815-ab79986666d0")
     * ```
     *
     * **iOS:**
     * ```swift
     * let response = try await StytchB2B.members.admin.deletePassword("member-password-test-d5a3b680-e8a3-40c0-b815-ab79986666d0")
     * ```
     *
     * **React Native:**
     * ```js
     * StytchB2B.members.admin.deletePassword("member-password-test-d5a3b680-e8a3-40c0-b815-ab79986666d0")
     * ```
     *
     * @param memberPasswordId The unique ID of the password credential to delete.
     *
     * @return [OrganizationsAdminMemberDeletePasswordResponse] containing the updated member object.
     *
     * @throws [StytchError] if the request fails or the caller lacks permission.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun deletePassword(memberPasswordId: String): OrganizationsAdminMemberDeletePasswordResponse

    /**
     * Reactivates a previously deleted member in the organization, restoring their access.
     * Calls the `PUT /sdk/v1/b2b/organizations/members/{member_id}/reactivate` endpoint. Requires
     * an active session and appropriate RBAC permissions.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchB2B.members.admin.reactivate("member-test-d5a3b680-e8a3-40c0-b815-ab79986666d0")
     * ```
     *
     * **iOS:**
     * ```swift
     * let response = try await StytchB2B.members.admin.reactivate("member-test-d5a3b680-e8a3-40c0-b815-ab79986666d0")
     * ```
     *
     * **React Native:**
     * ```js
     * StytchB2B.members.admin.reactivate("member-test-d5a3b680-e8a3-40c0-b815-ab79986666d0")
     * ```
     *
     * @param memberId The unique ID of the member to reactivate.
     *
     * @return [OrganizationsAdminMemberReactivateResponse] containing the reactivated member object.
     *
     * @throws [StytchError] if the request fails or the caller lacks permission.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun reactivate(memberId: String): OrganizationsAdminMemberReactivateResponse

    /**
     * Initiates an email address update for the specified member by sending a verification link to
     * the new address. Calls the
     * `POST /sdk/v1/b2b/organizations/members/{member_id}/start_email_update` endpoint. Requires an
     * active session and appropriate RBAC permissions.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchB2B.members.admin.startEmailUpdate(
     *     memberId = "member-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *     request = OrganizationsAdminMemberStartEmailUpdateParameters(emailAddress = "newemail@example.com"),
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = OrganizationsAdminMemberStartEmailUpdateParameters(emailAddress: "newemail@example.com")
     * let response = try await StytchB2B.members.admin.startEmailUpdate(
     *     memberId: "member-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *     request: params
     * )
     * ```
     *
     * **React Native:**
     * ```js
     * StytchB2B.members.admin.startEmailUpdate(
     *     "member-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *     { emailAddress: "newemail@example.com" }
     * )
     * ```
     *
     * @param memberId The unique ID of the member whose email to update.
     * @param request - [IOrganizationsAdminMemberStartEmailUpdateParameters]
     *   - `emailAddress` — The new email address to update to.
     *   - `loginRedirectUrl?` — URL to redirect to after verification.
     *   - `locale?` — Locale for the verification email.
     *   - `loginTemplateId?` — Custom email template ID.
     *   - `deliveryMethod?` — Delivery method for the verification (e.g. `"magic_link"`, `"otp"`).
     *
     * @return [OrganizationsAdminMemberStartEmailUpdateResponse] confirming the verification was sent.
     *
     * @throws [StytchError] if the request fails or the caller lacks permission.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun startEmailUpdate(
        memberId: String,
        request: IOrganizationsAdminMemberStartEmailUpdateParameters,
    ): OrganizationsAdminMemberStartEmailUpdateResponse

    /**
     * Removes a retired (previously used) email address from the specified member's account.
     * Calls the `POST /sdk/v1/b2b/organizations/members/{member_id}/unlink_retired_email` endpoint.
     * Requires an active session and appropriate RBAC permissions.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchB2B.members.admin.unlinkRetiredEmail(
     *     memberId = "member-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *     request = OrganizationsAdminMemberUnlinkRetiredEmailParameters(emailAddress = "old@example.com"),
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = OrganizationsAdminMemberUnlinkRetiredEmailParameters(emailAddress: "old@example.com")
     * let response = try await StytchB2B.members.admin.unlinkRetiredEmail(
     *     memberId: "member-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *     request: params
     * )
     * ```
     *
     * **React Native:**
     * ```js
     * StytchB2B.members.admin.unlinkRetiredEmail(
     *     "member-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *     { emailAddress: "old@example.com" }
     * )
     * ```
     *
     * @param memberId The unique ID of the member whose retired email to remove.
     * @param request - [IOrganizationsAdminMemberUnlinkRetiredEmailParameters]
     *   - `emailId?` — The ID of the retired email to unlink.
     *   - `emailAddress?` — The address of the retired email to unlink.
     *
     * @return [OrganizationsAdminMemberUnlinkRetiredEmailResponse] containing the updated member object.
     *
     * @throws [StytchError] if the request fails or the caller lacks permission.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun unlinkRetiredEmail(
        memberId: String,
        request: IOrganizationsAdminMemberUnlinkRetiredEmailParameters,
    ): OrganizationsAdminMemberUnlinkRetiredEmailResponse
}

internal class B2BMembersClientImpl(
    private val dispatchers: StytchDispatchers,
    private val networkingClient: B2BNetworkingClient,
) : B2BMembersClient {
    override val admin: B2BMembersAdminClient = B2BMembersAdminClientImpl(dispatchers, networkingClient)

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun me(): B2BGetMeResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request { networkingClient.api.b2BGetMe() }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun update(request: IOrganizationsMemberUpdateParameters): OrganizationsMemberUpdateResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request { networkingClient.api.organizationsMemberUpdate(request.toNetworkModel()) }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun search(request: IOrganizationsMemberSearchParameters): OrganizationsMemberSearchResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request { networkingClient.api.organizationsMemberSearch(request.toNetworkModel()) }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun create(request: IOrganizationsMemberCreateParameters): OrganizationsMemberCreateResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request { networkingClient.api.organizationsMemberCreate(request.toNetworkModel()) }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun deleteMFAPhoneNumber(): OrganizationsMemberDeleteMFAPhoneNumberResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request { networkingClient.api.organizationsMemberDeleteMFAPhoneNumber() }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun deleteMFATOTP(): OrganizationsMemberDeleteMFATOTPResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request { networkingClient.api.organizationsMemberDeleteMFATOTP() }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun startEmailUpdate(
        request: IOrganizationsMemberStartEmailUpdateParameters,
    ): OrganizationsMemberStartEmailUpdateResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.organizationsMemberStartEmailUpdate(request.toNetworkModel())
            }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun unlinkRetiredEmail(
        request: IOrganizationsMemberUnlinkRetiredEmailParameters,
    ): OrganizationsMemberUnlinkRetiredEmailResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.organizationsMemberUnlinkRetiredEmail(request.toNetworkModel())
            }
        }
}

internal class B2BMembersAdminClientImpl(
    private val dispatchers: StytchDispatchers,
    private val networkingClient: B2BNetworkingClient,
) : B2BMembersAdminClient {
    @Throws(StytchError::class, CancellationException::class)
    override suspend fun update(
        memberId: String,
        request: IOrganizationsAdminMemberUpdateParameters,
    ): OrganizationsAdminMemberUpdateResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.organizationsAdminMemberUpdate(memberId, request.toNetworkModel())
            }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun delete(memberId: String): OrganizationsAdminMemberDeleteResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request { networkingClient.api.organizationsAdminMemberDelete(memberId) }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun deleteMFAPhoneNumber(memberId: String): OrganizationsAdminMemberDeleteMFAPhoneNumberResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request { networkingClient.api.organizationsAdminMemberDeleteMFAPhoneNumber(memberId) }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun deleteMFATOTP(memberId: String): OrganizationsAdminMemberDeleteMFATOTPResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request { networkingClient.api.organizationsAdminMemberDeleteMFATOTP(memberId) }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun deletePassword(memberPasswordId: String): OrganizationsAdminMemberDeletePasswordResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request { networkingClient.api.organizationsAdminMemberDeletePassword(memberPasswordId) }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun reactivate(memberId: String): OrganizationsAdminMemberReactivateResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request { networkingClient.api.organizationsAdminMemberReactivate(memberId, emptyMap<String, Any>()) }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun startEmailUpdate(
        memberId: String,
        request: IOrganizationsAdminMemberStartEmailUpdateParameters,
    ): OrganizationsAdminMemberStartEmailUpdateResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.organizationsAdminMemberStartEmailUpdate(memberId, request.toNetworkModel())
            }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun unlinkRetiredEmail(
        memberId: String,
        request: IOrganizationsAdminMemberUnlinkRetiredEmailParameters,
    ): OrganizationsAdminMemberUnlinkRetiredEmailResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.organizationsAdminMemberUnlinkRetiredEmail(memberId, request.toNetworkModel())
            }
        }
}
