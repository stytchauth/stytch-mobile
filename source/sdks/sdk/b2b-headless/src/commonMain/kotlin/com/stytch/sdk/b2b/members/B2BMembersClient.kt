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

    /** Returns the currently authenticated member and their organization. */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun me(): B2BGetMeResponse

    /** Updates profile fields on the currently authenticated member. */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun update(request: IOrganizationsMemberUpdateParameters): OrganizationsMemberUpdateResponse

    /** Searches for members within the organization matching the provided criteria. */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun search(request: IOrganizationsMemberSearchParameters): OrganizationsMemberSearchResponse

    /** Creates a new member in the organization. */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun create(request: IOrganizationsMemberCreateParameters): OrganizationsMemberCreateResponse

    /** Removes the MFA phone number from the currently authenticated member. */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun deleteMFAPhoneNumber(): OrganizationsMemberDeleteMFAPhoneNumberResponse

    /** Removes the TOTP authenticator registration from the currently authenticated member. */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun deleteMFATOTP(): OrganizationsMemberDeleteMFATOTPResponse

    /** Initiates an email address update for the currently authenticated member. */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun startEmailUpdate(
        request: IOrganizationsMemberStartEmailUpdateParameters,
    ): OrganizationsMemberStartEmailUpdateResponse

    /** Removes a retired email address from the currently authenticated member. */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun unlinkRetiredEmail(
        request: IOrganizationsMemberUnlinkRetiredEmailParameters,
    ): OrganizationsMemberUnlinkRetiredEmailResponse
}

/** Admin-level operations on organization members, requiring appropriate RBAC permissions. */
@StytchApi
@JsExport
public interface B2BMembersAdminClient {
    /** Updates profile fields on any member in the organization. */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun update(
        memberId: String,
        request: IOrganizationsAdminMemberUpdateParameters,
    ): OrganizationsAdminMemberUpdateResponse

    /** Permanently deletes the specified member from the organization. */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun delete(memberId: String): OrganizationsAdminMemberDeleteResponse

    /** Removes the MFA phone number from the specified member. */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun deleteMFAPhoneNumber(memberId: String): OrganizationsAdminMemberDeleteMFAPhoneNumberResponse

    /** Removes the TOTP authenticator registration from the specified member. */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun deleteMFATOTP(memberId: String): OrganizationsAdminMemberDeleteMFATOTPResponse

    /** Deletes the specified password credential from the organization. */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun deletePassword(memberPasswordId: String): OrganizationsAdminMemberDeletePasswordResponse

    /** Reactivates a previously deleted member in the organization. */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun reactivate(memberId: String): OrganizationsAdminMemberReactivateResponse

    /** Initiates an email address update for the specified member. */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun startEmailUpdate(
        memberId: String,
        request: IOrganizationsAdminMemberStartEmailUpdateParameters,
    ): OrganizationsAdminMemberStartEmailUpdateResponse

    /** Removes a retired email address from the specified member. */
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
