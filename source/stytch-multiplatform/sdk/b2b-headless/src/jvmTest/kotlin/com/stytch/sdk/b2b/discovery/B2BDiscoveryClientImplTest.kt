package com.stytch.sdk.b2b.discovery

import com.stytch.sdk.b2b.B2BClientTest
import com.stytch.sdk.b2b.StytchB2BAuthenticationStateManager
import com.stytch.sdk.b2b.networking.models.IB2BDiscoveryIntermediateSessionsExchangeParameters
import com.stytch.sdk.b2b.networking.models.IB2BDiscoveryOrganizationsCreateParameters
import com.stytch.sdk.b2b.networking.models.IB2BDiscoveryOrganizationsParameters
import com.stytch.sdk.b2b.networking.models.IB2BDiscoveryPasswordResetParameters
import com.stytch.sdk.b2b.networking.models.IB2BDiscoveryPasswordResetStartParameters
import com.stytch.sdk.b2b.networking.models.IB2BPasswordDiscoveryAuthenticateParameters
import com.stytch.sdk.data.PKCECodePair
import com.stytch.sdk.data.StytchDataResponse
import com.stytch.sdk.pkce.MissingPKCEException
import com.stytch.sdk.pkce.PKCEClient
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFailsWith

@OptIn(ExperimentalCoroutinesApi::class)
internal class B2BDiscoveryClientImplTest : B2BClientTest() {
    private val pkceClient = mockk<PKCEClient>(relaxed = true)
    private val sessionManager = mockk<StytchB2BAuthenticationStateManager>(relaxed = true)
    private val fakePair = PKCECodePair(challenge = "test-challenge", verifier = "test-verifier")

    private val client = B2BDiscoveryClientImpl(dispatchers, networkingClient, pkceClient, sessionManager)

    // --- organizations.list ---

    @Test
    fun `organizations list calls b2BDiscoveryOrganizations with intermediateSessionToken`() =
        runTest(testDispatcher) {
            every { sessionManager.intermediateSessionToken } returns "ist-token"
            coEvery { api.b2BDiscoveryOrganizations(any()) } returns StytchDataResponse(mockk(relaxed = true))

            client.organizations.list(mockk<IB2BDiscoveryOrganizationsParameters>(relaxed = true))

            coVerify { api.b2BDiscoveryOrganizations(match { it.intermediateSessionToken == "ist-token" }) }
        }

    // --- organizations.create ---

    @Test
    fun `organizations create calls b2BDiscoveryOrganizationsCreate with intermediateSessionToken`() =
        runTest(testDispatcher) {
            every { sessionManager.intermediateSessionToken } returns "ist-token"
            coEvery { api.b2BDiscoveryOrganizationsCreate(any()) } returns StytchDataResponse(mockk(relaxed = true))

            client.organizations.create(mockk<IB2BDiscoveryOrganizationsCreateParameters>(relaxed = true))

            coVerify { api.b2BDiscoveryOrganizationsCreate(match { it.intermediateSessionToken == "ist-token" }) }
        }

    // --- intermediateSessions.exchange ---

    @Test
    fun `intermediateSessions exchange calls b2BDiscoveryIntermediateSessionsExchange with intermediateSessionToken`() =
        runTest(testDispatcher) {
            every { sessionManager.intermediateSessionToken } returns "ist-token"
            coEvery { api.b2BDiscoveryIntermediateSessionsExchange(any()) } returns StytchDataResponse(mockk(relaxed = true))

            client.intermediateSessions.exchange(mockk<IB2BDiscoveryIntermediateSessionsExchangeParameters>(relaxed = true))

            coVerify { api.b2BDiscoveryIntermediateSessionsExchange(match { it.intermediateSessionToken == "ist-token" }) }
        }

    // --- passwords.authenticate ---

    @Test
    fun `passwords authenticate calls b2BPasswordDiscoveryAuthenticate`() =
        runTest(testDispatcher) {
            coEvery { api.b2BPasswordDiscoveryAuthenticate(any()) } returns StytchDataResponse(mockk(relaxed = true))

            client.passwords.authenticate(mockk<IB2BPasswordDiscoveryAuthenticateParameters>(relaxed = true))

            coVerify { api.b2BPasswordDiscoveryAuthenticate(any()) }
        }

    // --- passwords.resetStart ---

    @Test
    fun `passwords resetStart creates PKCE and calls b2BDiscoveryPasswordResetStart`() =
        runTest(testDispatcher) {
            coEvery { pkceClient.create() } returns fakePair
            coEvery { api.b2BDiscoveryPasswordResetStart(any()) } returns StytchDataResponse(mockk(relaxed = true))

            client.passwords.resetStart(mockk<IB2BDiscoveryPasswordResetStartParameters>(relaxed = true))

            coVerify { api.b2BDiscoveryPasswordResetStart(match { it.pkceCodeChallenge == "test-challenge" }) }
        }

    // --- passwords.reset ---

    @Test
    fun `passwords reset retrieves PKCE and calls b2BDiscoveryPasswordReset with verifier`() =
        runTest(testDispatcher) {
            coEvery { pkceClient.retrieve() } returns fakePair
            coEvery { api.b2BDiscoveryPasswordReset(any()) } returns StytchDataResponse(mockk(relaxed = true))

            client.passwords.reset(mockk<IB2BDiscoveryPasswordResetParameters>(relaxed = true))

            coVerify { api.b2BDiscoveryPasswordReset(match { it.pkceCodeVerifier == "test-verifier" }) }
            coVerify { pkceClient.revoke() }
        }

    @Test
    fun `passwords reset throws MissingPKCEException when no code pair stored`() =
        runTest(testDispatcher) {
            coEvery { pkceClient.retrieve() } returns null

            assertFailsWith<MissingPKCEException> {
                client.passwords.reset(mockk<IB2BDiscoveryPasswordResetParameters>(relaxed = true))
            }
        }
}
