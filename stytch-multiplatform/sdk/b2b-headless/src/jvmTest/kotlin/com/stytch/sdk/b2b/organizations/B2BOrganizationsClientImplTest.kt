package com.stytch.sdk.b2b.organizations

import com.stytch.sdk.b2b.B2BClientTest
import com.stytch.sdk.b2b.networking.models.IB2BOrganizationsUpdateParameters
import com.stytch.sdk.data.StytchDataResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class B2BOrganizationsClientImplTest : B2BClientTest() {
    private val client = B2BOrganizationsClientImpl(dispatchers, networkingClient)

    @Test
    fun `get calls b2BOrganizationsGet`() =
        runTest(testDispatcher) {
            coEvery { api.b2BOrganizationsGet() } returns StytchDataResponse(mockk(relaxed = true))

            client.get()

            coVerify { api.b2BOrganizationsGet() }
        }

    @Test
    fun `update calls b2BOrganizationsUpdate`() =
        runTest(testDispatcher) {
            coEvery { api.b2BOrganizationsUpdate(any()) } returns StytchDataResponse(mockk(relaxed = true))

            client.update(mockk<IB2BOrganizationsUpdateParameters>(relaxed = true))

            coVerify { api.b2BOrganizationsUpdate(any()) }
        }

    @Test
    fun `delete calls b2BOrganizationsDelete`() =
        runTest(testDispatcher) {
            coEvery { api.b2BOrganizationsDelete() } returns StytchDataResponse(mockk(relaxed = true))

            client.delete()

            coVerify { api.b2BOrganizationsDelete() }
        }
}
