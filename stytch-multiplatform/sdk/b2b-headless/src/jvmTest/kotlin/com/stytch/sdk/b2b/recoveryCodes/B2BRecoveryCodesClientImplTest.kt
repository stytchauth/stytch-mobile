package com.stytch.sdk.b2b.recoveryCodes

import com.stytch.sdk.b2b.B2BClientTest
import com.stytch.sdk.b2b.networking.models.IB2BRecoveryCodesRecoverParameters
import com.stytch.sdk.b2b.networking.models.IB2BRecoveryCodesRotateParameters
import com.stytch.sdk.data.StytchDataResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class B2BRecoveryCodesClientImplTest : B2BClientTest() {
    private val sessionManager = mockk<com.stytch.sdk.b2b.StytchB2BAuthenticationStateManager>(relaxed = true)
    private val client = B2BRecoveryCodesClientImpl(dispatchers, networkingClient, sessionManager)

    @Test
    fun `get calls b2BRecoveryCodesGet`() =
        runTest(testDispatcher) {
            coEvery { api.b2BRecoveryCodesGet() } returns StytchDataResponse(mockk(relaxed = true))

            client.get()

            coVerify { api.b2BRecoveryCodesGet() }
        }

    @Test
    fun `recover calls b2BRecoveryCodesRecover with intermediateSessionToken from sessionManager`() =
        runTest(testDispatcher) {
            every { sessionManager.intermediateSessionToken } returns "ist-token"
            coEvery { api.b2BRecoveryCodesRecover(any()) } returns StytchDataResponse(mockk(relaxed = true))

            client.recover(mockk<IB2BRecoveryCodesRecoverParameters>(relaxed = true))

            coVerify { api.b2BRecoveryCodesRecover(match { it.intermediateSessionToken == "ist-token" }) }
        }

    @Test
    fun `rotate calls b2BRecoveryCodesRotate`() =
        runTest(testDispatcher) {
            coEvery { api.b2BRecoveryCodesRotate(any()) } returns StytchDataResponse(mockk(relaxed = true))

            client.rotate(mockk<IB2BRecoveryCodesRotateParameters>(relaxed = true))

            coVerify { api.b2BRecoveryCodesRotate(any()) }
        }
}
