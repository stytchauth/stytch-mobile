package com.stytch.sdk.b2b.members

import com.stytch.sdk.b2b.B2BClientTest
import com.stytch.sdk.b2b.networking.models.IOrganizationsAdminMemberStartEmailUpdateParameters
import com.stytch.sdk.b2b.networking.models.IOrganizationsAdminMemberUnlinkRetiredEmailParameters
import com.stytch.sdk.b2b.networking.models.IOrganizationsAdminMemberUpdateParameters
import com.stytch.sdk.b2b.networking.models.IOrganizationsMemberCreateParameters
import com.stytch.sdk.b2b.networking.models.IOrganizationsMemberSearchParameters
import com.stytch.sdk.b2b.networking.models.IOrganizationsMemberStartEmailUpdateParameters
import com.stytch.sdk.b2b.networking.models.IOrganizationsMemberUnlinkRetiredEmailParameters
import com.stytch.sdk.b2b.networking.models.IOrganizationsMemberUpdateParameters
import com.stytch.sdk.data.StytchDataResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class B2BMembersClientImplTest : B2BClientTest() {
    private val client = B2BMembersClientImpl(dispatchers, networkingClient)

    // --- me ---

    @Test
    fun `me calls b2BGetMe`() =
        runTest(testDispatcher) {
            coEvery { api.b2BGetMe() } returns StytchDataResponse(mockk(relaxed = true))

            client.me()

            coVerify { api.b2BGetMe() }
        }

    // --- update ---

    @Test
    fun `update calls organizationsMemberUpdate`() =
        runTest(testDispatcher) {
            coEvery { api.organizationsMemberUpdate(any()) } returns StytchDataResponse(mockk(relaxed = true))

            client.update(mockk<IOrganizationsMemberUpdateParameters>(relaxed = true))

            coVerify { api.organizationsMemberUpdate(any()) }
        }

    // --- search ---

    @Test
    fun `search calls organizationsMemberSearch`() =
        runTest(testDispatcher) {
            coEvery { api.organizationsMemberSearch(any()) } returns StytchDataResponse(mockk(relaxed = true))

            client.search(mockk<IOrganizationsMemberSearchParameters>(relaxed = true))

            coVerify { api.organizationsMemberSearch(any()) }
        }

    // --- create ---

    @Test
    fun `create calls organizationsMemberCreate`() =
        runTest(testDispatcher) {
            coEvery { api.organizationsMemberCreate(any()) } returns StytchDataResponse(mockk(relaxed = true))

            client.create(mockk<IOrganizationsMemberCreateParameters>(relaxed = true))

            coVerify { api.organizationsMemberCreate(any()) }
        }

    // --- deleteMFAPhoneNumber / deleteMFATOTP ---

    @Test
    fun `deleteMFAPhoneNumber calls organizationsMemberDeleteMFAPhoneNumber`() =
        runTest(testDispatcher) {
            coEvery { api.organizationsMemberDeleteMFAPhoneNumber() } returns StytchDataResponse(mockk(relaxed = true))

            client.deleteMFAPhoneNumber()

            coVerify { api.organizationsMemberDeleteMFAPhoneNumber() }
        }

    @Test
    fun `deleteMFATOTP calls organizationsMemberDeleteMFATOTP`() =
        runTest(testDispatcher) {
            coEvery { api.organizationsMemberDeleteMFATOTP() } returns StytchDataResponse(mockk(relaxed = true))

            client.deleteMFATOTP()

            coVerify { api.organizationsMemberDeleteMFATOTP() }
        }

    // --- startEmailUpdate / unlinkRetiredEmail ---

    @Test
    fun `startEmailUpdate calls organizationsMemberStartEmailUpdate`() =
        runTest(testDispatcher) {
            coEvery { api.organizationsMemberStartEmailUpdate(any()) } returns StytchDataResponse(mockk(relaxed = true))

            client.startEmailUpdate(mockk<IOrganizationsMemberStartEmailUpdateParameters>(relaxed = true))

            coVerify { api.organizationsMemberStartEmailUpdate(any()) }
        }

    @Test
    fun `unlinkRetiredEmail calls organizationsMemberUnlinkRetiredEmail`() =
        runTest(testDispatcher) {
            coEvery { api.organizationsMemberUnlinkRetiredEmail(any()) } returns StytchDataResponse(mockk(relaxed = true))

            client.unlinkRetiredEmail(mockk<IOrganizationsMemberUnlinkRetiredEmailParameters>(relaxed = true))

            coVerify { api.organizationsMemberUnlinkRetiredEmail(any()) }
        }

    // --- admin ---

    @Test
    fun `admin update calls organizationsAdminMemberUpdate with memberId`() =
        runTest(testDispatcher) {
            coEvery { api.organizationsAdminMemberUpdate(any(), any()) } returns StytchDataResponse(mockk(relaxed = true))

            client.admin.update("member-123", mockk<IOrganizationsAdminMemberUpdateParameters>(relaxed = true))

            coVerify { api.organizationsAdminMemberUpdate(eq("member-123"), any()) }
        }

    @Test
    fun `admin delete calls organizationsAdminMemberDelete with memberId`() =
        runTest(testDispatcher) {
            coEvery { api.organizationsAdminMemberDelete(any()) } returns StytchDataResponse(mockk(relaxed = true))

            client.admin.delete("member-123")

            coVerify { api.organizationsAdminMemberDelete("member-123") }
        }

    @Test
    fun `admin deleteMFAPhoneNumber calls organizationsAdminMemberDeleteMFAPhoneNumber with memberId`() =
        runTest(testDispatcher) {
            coEvery { api.organizationsAdminMemberDeleteMFAPhoneNumber(any()) } returns StytchDataResponse(mockk(relaxed = true))

            client.admin.deleteMFAPhoneNumber("member-123")

            coVerify { api.organizationsAdminMemberDeleteMFAPhoneNumber("member-123") }
        }

    @Test
    fun `admin deleteMFATOTP calls organizationsAdminMemberDeleteMFATOTP with memberId`() =
        runTest(testDispatcher) {
            coEvery { api.organizationsAdminMemberDeleteMFATOTP(any()) } returns StytchDataResponse(mockk(relaxed = true))

            client.admin.deleteMFATOTP("member-123")

            coVerify { api.organizationsAdminMemberDeleteMFATOTP("member-123") }
        }

    @Test
    fun `admin deletePassword calls organizationsAdminMemberDeletePassword with memberPasswordId`() =
        runTest(testDispatcher) {
            coEvery { api.organizationsAdminMemberDeletePassword(any()) } returns StytchDataResponse(mockk(relaxed = true))

            client.admin.deletePassword("pwd-123")

            coVerify { api.organizationsAdminMemberDeletePassword("pwd-123") }
        }

    @Test
    fun `admin reactivate calls organizationsAdminMemberReactivate with memberId`() =
        runTest(testDispatcher) {
            coEvery { api.organizationsAdminMemberReactivate(any(), any()) } returns StytchDataResponse(mockk(relaxed = true))

            client.admin.reactivate("member-123")

            coVerify { api.organizationsAdminMemberReactivate(eq("member-123"), any()) }
        }

    @Test
    fun `admin startEmailUpdate calls organizationsAdminMemberStartEmailUpdate with memberId`() =
        runTest(testDispatcher) {
            coEvery { api.organizationsAdminMemberStartEmailUpdate(any(), any()) } returns StytchDataResponse(mockk(relaxed = true))

            client.admin.startEmailUpdate("member-123", mockk<IOrganizationsAdminMemberStartEmailUpdateParameters>(relaxed = true))

            coVerify { api.organizationsAdminMemberStartEmailUpdate(eq("member-123"), any()) }
        }

    @Test
    fun `admin unlinkRetiredEmail calls organizationsAdminMemberUnlinkRetiredEmail with memberId`() =
        runTest(testDispatcher) {
            coEvery { api.organizationsAdminMemberUnlinkRetiredEmail(any(), any()) } returns StytchDataResponse(mockk(relaxed = true))

            client.admin.unlinkRetiredEmail("member-123", mockk<IOrganizationsAdminMemberUnlinkRetiredEmailParameters>(relaxed = true))

            coVerify { api.organizationsAdminMemberUnlinkRetiredEmail(eq("member-123"), any()) }
        }
}
