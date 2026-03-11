package com.stytch.sdk.b2b.totp

import com.stytch.sdk.b2b.B2BClientTest
import com.stytch.sdk.b2b.networking.models.IB2BTOTPsAuthenticateParameters
import com.stytch.sdk.b2b.networking.models.IB2BTOTPsCreateParameters
import com.stytch.sdk.data.StytchDataResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class B2BTOTPClientImplTest : B2BClientTest() {
    private val sessionManager = mockk<com.stytch.sdk.b2b.StytchB2BAuthenticationStateManager>(relaxed = true)
    private val client = B2BTOTPClientImpl(dispatchers, networkingClient, sessionManager)

    @Test
    fun `create calls b2BTOTPsCreate with intermediateSessionToken from sessionManager`() =
        runTest(testDispatcher) {
            every { sessionManager.intermediateSessionToken } returns "ist-token"
            coEvery { api.b2BTOTPsCreate(any()) } returns StytchDataResponse(mockk(relaxed = true))

            client.create(mockk<IB2BTOTPsCreateParameters>(relaxed = true))

            coVerify { api.b2BTOTPsCreate(match { it.intermediateSessionToken == "ist-token" }) }
        }

    @Test
    fun `authenticate calls b2BTOTPsAuthenticate with intermediateSessionToken from sessionManager`() =
        runTest(testDispatcher) {
            every { sessionManager.intermediateSessionToken } returns "ist-token"
            coEvery { api.b2BTOTPsAuthenticate(any()) } returns StytchDataResponse(mockk(relaxed = true))

            client.authenticate(mockk<IB2BTOTPsAuthenticateParameters>(relaxed = true))

            coVerify { api.b2BTOTPsAuthenticate(match { it.intermediateSessionToken == "ist-token" }) }
        }

    @Test
    fun `create passes null intermediateSessionToken when sessionManager returns null`() =
        runTest(testDispatcher) {
            every { sessionManager.intermediateSessionToken } returns null
            coEvery { api.b2BTOTPsCreate(any()) } returns StytchDataResponse(mockk(relaxed = true))

            client.create(mockk<IB2BTOTPsCreateParameters>(relaxed = true))

            coVerify { api.b2BTOTPsCreate(match { it.intermediateSessionToken == null }) }
        }
}
