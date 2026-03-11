package com.stytch.sdk.b2b.magicLinks

import com.stytch.sdk.b2b.B2BClientTest
import com.stytch.sdk.b2b.StytchB2BAuthenticationStateManager
import com.stytch.sdk.b2b.networking.models.IB2BMagicLinksAuthenticateParameters
import com.stytch.sdk.b2b.networking.models.IB2BMagicLinksDiscoveryAuthenticateParameters
import com.stytch.sdk.b2b.networking.models.IB2BMagicLinksDiscoveryEmailSendParameters
import com.stytch.sdk.b2b.networking.models.IB2BMagicLinksInviteParameters
import com.stytch.sdk.b2b.networking.models.IB2BMagicLinksLoginOrSignupParameters
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
internal class B2BMagicLinksClientImplTest : B2BClientTest() {
    private val pkceClient = mockk<PKCEClient>(relaxed = true)
    private val sessionManager = mockk<StytchB2BAuthenticationStateManager>(relaxed = true)
    private val fakePair = PKCECodePair(challenge = "test-challenge", verifier = "test-verifier")

    private val client = B2BMagicLinksClientImpl(dispatchers, networkingClient, pkceClient, sessionManager)

    // --- authenticate ---

    @Test
    fun `authenticate retrieves PKCE and calls b2BMagicLinksAuthenticate with verifier`() =
        runTest(testDispatcher) {
            coEvery { pkceClient.retrieve() } returns fakePair
            every { sessionManager.intermediateSessionToken } returns null
            coEvery { api.b2BMagicLinksAuthenticate(any()) } returns StytchDataResponse(mockk(relaxed = true))

            client.authenticate(mockk<IB2BMagicLinksAuthenticateParameters>(relaxed = true))

            coVerify { api.b2BMagicLinksAuthenticate(match { it.pkceCodeVerifier == "test-verifier" }) }
            coVerify { pkceClient.revoke() }
        }

    @Test
    fun `authenticate throws MissingPKCEException when no code pair stored`() =
        runTest(testDispatcher) {
            coEvery { pkceClient.retrieve() } returns null

            assertFailsWith<MissingPKCEException> {
                client.authenticate(mockk<IB2BMagicLinksAuthenticateParameters>(relaxed = true))
            }
        }

    @Test
    fun `authenticate passes intermediateSessionToken from sessionManager`() =
        runTest(testDispatcher) {
            coEvery { pkceClient.retrieve() } returns fakePair
            every { sessionManager.intermediateSessionToken } returns "ist-token"
            coEvery { api.b2BMagicLinksAuthenticate(any()) } returns StytchDataResponse(mockk(relaxed = true))

            client.authenticate(mockk<IB2BMagicLinksAuthenticateParameters>(relaxed = true))

            coVerify {
                api.b2BMagicLinksAuthenticate(
                    match { it.pkceCodeVerifier == "test-verifier" && it.intermediateSessionToken == "ist-token" },
                )
            }
        }

    // --- email.loginOrSignup ---

    @Test
    fun `email loginOrSignup creates PKCE and calls b2BMagicLinksLoginOrSignup`() =
        runTest(testDispatcher) {
            coEvery { pkceClient.create() } returns fakePair
            coEvery { api.b2BMagicLinksLoginOrSignup(any()) } returns StytchDataResponse(mockk(relaxed = true))

            client.email.loginOrSignup(mockk<IB2BMagicLinksLoginOrSignupParameters>(relaxed = true))

            coVerify { api.b2BMagicLinksLoginOrSignup(match { it.pkceCodeChallenge == "test-challenge" }) }
        }

    // --- email.invite ---

    @Test
    fun `email invite calls b2BMagicLinksInvite`() =
        runTest(testDispatcher) {
            coEvery { api.b2BMagicLinksInvite(any()) } returns StytchDataResponse(mockk(relaxed = true))

            client.email.invite(mockk<IB2BMagicLinksInviteParameters>(relaxed = true))

            coVerify { api.b2BMagicLinksInvite(any()) }
        }

    // --- discovery.emailSend ---

    @Test
    fun `discovery emailSend creates PKCE and calls b2BMagicLinksDiscoveryEmailSend`() =
        runTest(testDispatcher) {
            coEvery { pkceClient.create() } returns fakePair
            coEvery { api.b2BMagicLinksDiscoveryEmailSend(any()) } returns StytchDataResponse(mockk(relaxed = true))

            client.discovery.emailSend(mockk<IB2BMagicLinksDiscoveryEmailSendParameters>(relaxed = true))

            coVerify { api.b2BMagicLinksDiscoveryEmailSend(match { it.pkceCodeChallenge == "test-challenge" }) }
        }

    // --- discovery.authenticate ---

    @Test
    fun `discovery authenticate retrieves PKCE and calls b2BMagicLinksDiscoveryAuthenticate with verifier`() =
        runTest(testDispatcher) {
            coEvery { pkceClient.retrieve() } returns fakePair
            coEvery { api.b2BMagicLinksDiscoveryAuthenticate(any()) } returns StytchDataResponse(mockk(relaxed = true))

            client.discovery.authenticate(mockk<IB2BMagicLinksDiscoveryAuthenticateParameters>(relaxed = true))

            coVerify { api.b2BMagicLinksDiscoveryAuthenticate(match { it.pkceCodeVerifier == "test-verifier" }) }
            coVerify { pkceClient.revoke() }
        }

    @Test
    fun `discovery authenticate throws MissingPKCEException when no code pair stored`() =
        runTest(testDispatcher) {
            coEvery { pkceClient.retrieve() } returns null

            assertFailsWith<MissingPKCEException> {
                client.discovery.authenticate(mockk<IB2BMagicLinksDiscoveryAuthenticateParameters>(relaxed = true))
            }
        }
}
