package com.stytch.sdk.b2b.scim

import com.stytch.sdk.b2b.B2BClientTest
import com.stytch.sdk.b2b.networking.models.IB2BGetSCIMConnectionGroupsParameters
import com.stytch.sdk.b2b.networking.models.IB2BSCIMCreateConnectionParameters
import com.stytch.sdk.b2b.networking.models.IB2BSCIMUpdateConnectionParameters
import com.stytch.sdk.b2b.networking.models.ISCIMRotateTokenCancelParameters
import com.stytch.sdk.b2b.networking.models.ISCIMRotateTokenCompleteParameters
import com.stytch.sdk.b2b.networking.models.ISCIMRotateTokenStartParameters
import com.stytch.sdk.data.StytchDataResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class B2BSCIMClientImplTest : B2BClientTest() {
    private val client = B2BSCIMClientImpl(dispatchers, networkingClient)

    @Test
    fun `getConnection calls b2BGetSCIMConnection`() =
        runTest(testDispatcher) {
            coEvery { api.b2BGetSCIMConnection() } returns StytchDataResponse(mockk(relaxed = true))

            client.getConnection()

            coVerify { api.b2BGetSCIMConnection() }
        }

    @Test
    fun `getConnectionGroups calls b2BGetSCIMConnectionGroups`() =
        runTest(testDispatcher) {
            coEvery { api.b2BGetSCIMConnectionGroups(any()) } returns StytchDataResponse(mockk(relaxed = true))

            client.getConnectionGroups(mockk<IB2BGetSCIMConnectionGroupsParameters>(relaxed = true))

            coVerify { api.b2BGetSCIMConnectionGroups(any()) }
        }

    @Test
    fun `createConnection calls b2BSCIMCreateConnection`() =
        runTest(testDispatcher) {
            coEvery { api.b2BSCIMCreateConnection(any()) } returns StytchDataResponse(mockk(relaxed = true))

            client.createConnection(mockk<IB2BSCIMCreateConnectionParameters>(relaxed = true))

            coVerify { api.b2BSCIMCreateConnection(any()) }
        }

    @Test
    fun `deleteConnection calls b2BSCIMDeleteConnection with connectionId`() =
        runTest(testDispatcher) {
            coEvery { api.b2BSCIMDeleteConnection(any()) } returns StytchDataResponse(mockk(relaxed = true))

            client.deleteConnection("conn-123")

            coVerify { api.b2BSCIMDeleteConnection("conn-123") }
        }

    @Test
    fun `updateConnection calls b2BSCIMUpdateConnection with connectionId`() =
        runTest(testDispatcher) {
            coEvery { api.b2BSCIMUpdateConnection(any(), any()) } returns StytchDataResponse(mockk(relaxed = true))

            client.updateConnection("conn-123", mockk<IB2BSCIMUpdateConnectionParameters>(relaxed = true))

            coVerify { api.b2BSCIMUpdateConnection(eq("conn-123"), any()) }
        }

    @Test
    fun `rotateTokenStart calls sCIMRotateTokenStart`() =
        runTest(testDispatcher) {
            coEvery { api.sCIMRotateTokenStart(any()) } returns StytchDataResponse(mockk(relaxed = true))

            client.rotateTokenStart(mockk<ISCIMRotateTokenStartParameters>(relaxed = true))

            coVerify { api.sCIMRotateTokenStart(any()) }
        }

    @Test
    fun `rotateTokenComplete calls sCIMRotateTokenComplete`() =
        runTest(testDispatcher) {
            coEvery { api.sCIMRotateTokenComplete(any()) } returns StytchDataResponse(mockk(relaxed = true))

            client.rotateTokenComplete(mockk<ISCIMRotateTokenCompleteParameters>(relaxed = true))

            coVerify { api.sCIMRotateTokenComplete(any()) }
        }

    @Test
    fun `rotateTokenCancel calls sCIMRotateTokenCancel`() =
        runTest(testDispatcher) {
            coEvery { api.sCIMRotateTokenCancel(any()) } returns StytchDataResponse(mockk(relaxed = true))

            client.rotateTokenCancel(mockk<ISCIMRotateTokenCancelParameters>(relaxed = true))

            coVerify { api.sCIMRotateTokenCancel(any()) }
        }
}
