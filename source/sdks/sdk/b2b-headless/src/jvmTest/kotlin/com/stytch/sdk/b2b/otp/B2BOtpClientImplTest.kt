package com.stytch.sdk.b2b.otp

import com.stytch.sdk.b2b.B2BClientTest
import com.stytch.sdk.b2b.StytchB2BAuthenticationStateManager
import com.stytch.sdk.b2b.networking.models.IB2BOTPsEmailAuthenticateParameters
import com.stytch.sdk.b2b.networking.models.IB2BOTPsEmailDiscoveryAuthenticateParameters
import com.stytch.sdk.b2b.networking.models.IB2BOTPsEmailDiscoverySendParameters
import com.stytch.sdk.b2b.networking.models.IB2BOTPsEmailLoginOrSignupParameters
import com.stytch.sdk.b2b.networking.models.IB2BOTPsSMSAuthenticateParameters
import com.stytch.sdk.b2b.networking.models.IB2BOTPsSMSSendParameters
import com.stytch.sdk.data.StytchDataResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class B2BOtpClientImplTest : B2BClientTest() {
    private val sessionManager = mockk<StytchB2BAuthenticationStateManager>(relaxed = true)
    private val client = B2BOtpClientImpl(dispatchers, networkingClient, sessionManager)

    // --- sms ---

    @Test
    fun `sms send calls b2BOTPsSMSSend with intermediateSessionToken`() =
        runTest(testDispatcher) {
            every { sessionManager.intermediateSessionToken } returns "ist-token"
            coEvery { api.b2BOTPsSMSSend(any()) } returns StytchDataResponse(mockk(relaxed = true))

            client.sms.send(mockk<IB2BOTPsSMSSendParameters>(relaxed = true))

            coVerify { api.b2BOTPsSMSSend(match { it.intermediateSessionToken == "ist-token" }) }
        }

    @Test
    fun `sms authenticate calls b2BOTPsSMSAuthenticate with intermediateSessionToken`() =
        runTest(testDispatcher) {
            every { sessionManager.intermediateSessionToken } returns "ist-token"
            coEvery { api.b2BOTPsSMSAuthenticate(any()) } returns StytchDataResponse(mockk(relaxed = true))

            client.sms.authenticate(mockk<IB2BOTPsSMSAuthenticateParameters>(relaxed = true))

            coVerify { api.b2BOTPsSMSAuthenticate(match { it.intermediateSessionToken == "ist-token" }) }
        }

    // --- email ---

    @Test
    fun `email loginOrSignup calls b2BOTPsEmailLoginOrSignup`() =
        runTest(testDispatcher) {
            coEvery { api.b2BOTPsEmailLoginOrSignup(any()) } returns StytchDataResponse(mockk(relaxed = true))

            client.email.loginOrSignup(mockk<IB2BOTPsEmailLoginOrSignupParameters>(relaxed = true))

            coVerify { api.b2BOTPsEmailLoginOrSignup(any()) }
        }

    @Test
    fun `email authenticate calls b2BOTPsEmailAuthenticate with intermediateSessionToken`() =
        runTest(testDispatcher) {
            every { sessionManager.intermediateSessionToken } returns "ist-token"
            coEvery { api.b2BOTPsEmailAuthenticate(any()) } returns StytchDataResponse(mockk(relaxed = true))

            client.email.authenticate(mockk<IB2BOTPsEmailAuthenticateParameters>(relaxed = true))

            coVerify { api.b2BOTPsEmailAuthenticate(match { it.intermediateSessionToken == "ist-token" }) }
        }

    // --- email.discovery ---

    @Test
    fun `email discovery send calls b2BOTPsEmailDiscoverySend`() =
        runTest(testDispatcher) {
            coEvery { api.b2BOTPsEmailDiscoverySend(any()) } returns StytchDataResponse(mockk(relaxed = true))

            client.email.discovery.send(mockk<IB2BOTPsEmailDiscoverySendParameters>(relaxed = true))

            coVerify { api.b2BOTPsEmailDiscoverySend(any()) }
        }

    @Test
    fun `email discovery authenticate calls b2BOTPsEmailDiscoveryAuthenticate`() =
        runTest(testDispatcher) {
            coEvery { api.b2BOTPsEmailDiscoveryAuthenticate(any()) } returns StytchDataResponse(mockk(relaxed = true))

            client.email.discovery.authenticate(mockk<IB2BOTPsEmailDiscoveryAuthenticateParameters>(relaxed = true))

            coVerify { api.b2BOTPsEmailDiscoveryAuthenticate(any()) }
        }
}
