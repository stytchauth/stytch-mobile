package com.stytch.sdk.b2b.oauth

import com.stytch.sdk.b2b.B2BClientTest
import com.stytch.sdk.b2b.StytchB2BAuthenticationStateManager
import com.stytch.sdk.b2b.networking.models.IB2BOAuthAuthenticateParameters
import com.stytch.sdk.b2b.networking.models.IB2BOAuthDiscoveryAuthenticateParameters
import com.stytch.sdk.data.EndpointOptions
import com.stytch.sdk.data.PKCECodePair
import com.stytch.sdk.data.PublicTokenInfo
import com.stytch.sdk.data.StytchDataResponse
import com.stytch.sdk.oauth.B2BOAuthDiscoveryStartParameters
import com.stytch.sdk.oauth.B2BOAuthStartParameters
import com.stytch.sdk.oauth.IOAuthProvider
import com.stytch.sdk.oauth.OAuthException
import com.stytch.sdk.oauth.OAuthResult
import com.stytch.sdk.pkce.MissingPKCEException
import com.stytch.sdk.pkce.PKCEClient
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
internal class B2BOAuthClientImplTest : B2BClientTest() {
    private val pkceClient = mockk<PKCEClient>(relaxed = true)
    private val sessionManager = mockk<StytchB2BAuthenticationStateManager>(relaxed = true)
    private val oauthProvider = mockk<IOAuthProvider>(relaxed = true)
    private val fakePair = PKCECodePair(challenge = "test-challenge", verifier = "test-verifier")

    private fun makeClient(
        isTestToken: Boolean = true,
        cnameDomain: () -> String? = { null },
        defaultSessionDuration: Int = 30,
    ) = B2BOAuthClientImpl(
        dispatchers = dispatchers,
        networkingClient = networkingClient,
        pkceClient = pkceClient,
        sessionManager = sessionManager,
        oauthProvider = oauthProvider,
        publicTokenInfo = PublicTokenInfo(publicToken = "public-token-test-xxx", isTestToken = isTestToken),
        endpointOptions = EndpointOptions(),
        cnameDomain = cnameDomain,
        defaultSessionDuration = defaultSessionDuration,
    )

    // --- authenticate ---

    @Test
    fun `authenticate retrieves PKCE and calls b2BOAuthAuthenticate with verifier`() =
        runTest(testDispatcher) {
            coEvery { pkceClient.retrieve() } returns fakePair
            every { sessionManager.intermediateSessionToken } returns null
            coEvery { api.b2BOAuthAuthenticate(any()) } returns StytchDataResponse(mockk(relaxed = true))

            makeClient().authenticate(mockk<IB2BOAuthAuthenticateParameters>(relaxed = true))

            coVerify { api.b2BOAuthAuthenticate(match { it.pkceCodeVerifier == "test-verifier" }) }
            coVerify { pkceClient.revoke() }
        }

    @Test
    fun `authenticate throws MissingPKCEException when no code pair stored`() =
        runTest(testDispatcher) {
            coEvery { pkceClient.retrieve() } returns null

            assertFailsWith<MissingPKCEException> {
                makeClient().authenticate(mockk<IB2BOAuthAuthenticateParameters>(relaxed = true))
            }
        }

    @Test
    fun `authenticate passes intermediateSessionToken from sessionManager`() =
        runTest(testDispatcher) {
            coEvery { pkceClient.retrieve() } returns fakePair
            every { sessionManager.intermediateSessionToken } returns "ist-token"
            coEvery { api.b2BOAuthAuthenticate(any()) } returns StytchDataResponse(mockk(relaxed = true))

            makeClient().authenticate(mockk<IB2BOAuthAuthenticateParameters>(relaxed = true))

            coVerify {
                api.b2BOAuthAuthenticate(
                    match { it.pkceCodeVerifier == "test-verifier" && it.intermediateSessionToken == "ist-token" },
                )
            }
        }

    // --- provider.start: browser flow routing ---

    @Test
    fun `google start with ClassicToken calls b2BOAuthAuthenticate`() =
        runTest(testDispatcher) {
            coEvery { pkceClient.create() } returns fakePair
            coEvery { pkceClient.retrieve() } returns fakePair
            coEvery { oauthProvider.startBrowserFlow(any(), any(), any()) } returns OAuthResult.ClassicToken("oauth-tok")
            coEvery { api.b2BOAuthAuthenticate(any()) } returns StytchDataResponse(mockk(relaxed = true))

            makeClient().google.start(B2BOAuthStartParameters())

            coVerify { api.b2BOAuthAuthenticate(any()) }
        }

    @Test
    fun `google start with Error result throws OAuthException`() =
        runTest(testDispatcher) {
            coEvery { pkceClient.create() } returns fakePair
            coEvery { oauthProvider.startBrowserFlow(any(), any(), any()) } returns OAuthResult.Error("OAuth failed")

            val thrown = assertFailsWith<OAuthException> { makeClient().google.start(B2BOAuthStartParameters()) }
            assertEquals("OAuth failed", thrown.cause?.message)
        }

    @Test
    fun `google start with unexpected OAuthResult type throws OAuthException`() =
        runTest(testDispatcher) {
            coEvery { pkceClient.create() } returns fakePair
            // IDToken is an unexpected result type for B2B flows
            coEvery { oauthProvider.startBrowserFlow(any(), any(), any()) } returns
                OAuthResult.IDToken(token = "id-tok", nonce = "nonce")

            assertFailsWith<OAuthException> { makeClient().google.start(B2BOAuthStartParameters()) }
        }

    // --- provider.start: URL construction ---

    @Test
    fun `google start uses CNAME domain when provided`() =
        runTest(testDispatcher) {
            var capturedUrl = ""
            coEvery { pkceClient.create() } returns fakePair
            coEvery { pkceClient.retrieve() } returns fakePair
            coEvery { oauthProvider.startBrowserFlow(any(), any(), any()) } coAnswers {
                capturedUrl = firstArg()
                OAuthResult.ClassicToken("tok")
            }
            coEvery { api.b2BOAuthAuthenticate(any()) } returns StytchDataResponse(mockk(relaxed = true))

            makeClient(cnameDomain = { "custom.example.com" }).google.start(B2BOAuthStartParameters())

            assertTrue(capturedUrl.startsWith("https://custom.example.com/b2b/public/oauth/google/start"))
        }

    @Test
    fun `google start uses test domain when no CNAME and token is a test token`() =
        runTest(testDispatcher) {
            var capturedUrl = ""
            coEvery { pkceClient.create() } returns fakePair
            coEvery { pkceClient.retrieve() } returns fakePair
            coEvery { oauthProvider.startBrowserFlow(any(), any(), any()) } coAnswers {
                capturedUrl = firstArg()
                OAuthResult.ClassicToken("tok")
            }
            coEvery { api.b2BOAuthAuthenticate(any()) } returns StytchDataResponse(mockk(relaxed = true))

            makeClient(isTestToken = true).google.start(B2BOAuthStartParameters())

            assertTrue(capturedUrl.startsWith("https://test.stytch.com/b2b/public/oauth/google/start"))
        }

    @Test
    fun `google start uses live domain when no CNAME and token is a live token`() =
        runTest(testDispatcher) {
            var capturedUrl = ""
            coEvery { pkceClient.create() } returns fakePair
            coEvery { pkceClient.retrieve() } returns fakePair
            coEvery { oauthProvider.startBrowserFlow(any(), any(), any()) } coAnswers {
                capturedUrl = firstArg()
                OAuthResult.ClassicToken("tok")
            }
            coEvery { api.b2BOAuthAuthenticate(any()) } returns StytchDataResponse(mockk(relaxed = true))

            makeClient(isTestToken = false).google.start(B2BOAuthStartParameters())

            assertTrue(capturedUrl.startsWith("https://api.stytch.com/b2b/public/oauth/google/start"))
        }

    @Test
    fun `google start URL includes pkce_code_challenge`() =
        runTest(testDispatcher) {
            var capturedUrl = ""
            coEvery { pkceClient.create() } returns fakePair
            coEvery { pkceClient.retrieve() } returns fakePair
            coEvery { oauthProvider.startBrowserFlow(any(), any(), any()) } coAnswers {
                capturedUrl = firstArg()
                OAuthResult.ClassicToken("tok")
            }
            coEvery { api.b2BOAuthAuthenticate(any()) } returns StytchDataResponse(mockk(relaxed = true))

            makeClient().google.start(B2BOAuthStartParameters())

            assertTrue(capturedUrl.contains("pkce_code_challenge=test-challenge"))
        }

    @Test
    fun `google start falls back to defaultSessionDuration when params sessionDurationMinutes is null`() =
        runTest(testDispatcher) {
            coEvery { pkceClient.create() } returns fakePair
            coEvery { pkceClient.retrieve() } returns fakePair
            coEvery { oauthProvider.startBrowserFlow(any(), any(), any()) } returns OAuthResult.ClassicToken("tok")
            coEvery { api.b2BOAuthAuthenticate(any()) } returns StytchDataResponse(mockk(relaxed = true))

            makeClient(defaultSessionDuration = 15).google.start(B2BOAuthStartParameters(sessionDurationMinutes = null))

            coVerify { api.b2BOAuthAuthenticate(match { it.sessionDurationMinutes == 15 }) }
        }

    @Test
    fun `google start uses sessionDurationMinutes from parameters when provided`() =
        runTest(testDispatcher) {
            coEvery { pkceClient.create() } returns fakePair
            coEvery { pkceClient.retrieve() } returns fakePair
            coEvery { oauthProvider.startBrowserFlow(any(), any(), any()) } returns OAuthResult.ClassicToken("tok")
            coEvery { api.b2BOAuthAuthenticate(any()) } returns StytchDataResponse(mockk(relaxed = true))

            makeClient(defaultSessionDuration = 15).google.start(B2BOAuthStartParameters(sessionDurationMinutes = 60))

            coVerify { api.b2BOAuthAuthenticate(match { it.sessionDurationMinutes == 60 }) }
        }

    // --- provider.discovery.start ---

    @Test
    fun `google discovery start with ClassicToken calls b2BOAuthDiscoveryAuthenticate`() =
        runTest(testDispatcher) {
            coEvery { pkceClient.create() } returns fakePair
            coEvery { pkceClient.retrieve() } returns fakePair
            coEvery { oauthProvider.startBrowserFlow(any(), any(), any()) } returns OAuthResult.ClassicToken("disc-tok")
            coEvery { api.b2BOAuthDiscoveryAuthenticate(any()) } returns StytchDataResponse(mockk(relaxed = true))

            makeClient().google.discovery.start(B2BOAuthDiscoveryStartParameters())

            coVerify { api.b2BOAuthDiscoveryAuthenticate(any()) }
            coVerify { pkceClient.revoke() }
        }

    @Test
    fun `google discovery start with Error result throws OAuthException`() =
        runTest(testDispatcher) {
            coEvery { pkceClient.create() } returns fakePair
            coEvery { oauthProvider.startBrowserFlow(any(), any(), any()) } returns OAuthResult.Error("discovery failed")

            assertFailsWith<OAuthException> {
                makeClient().google.discovery.start(B2BOAuthDiscoveryStartParameters())
            }
        }

    // --- discovery.authenticate ---

    @Test
    fun `discovery authenticate retrieves PKCE and calls b2BOAuthDiscoveryAuthenticate with verifier`() =
        runTest(testDispatcher) {
            coEvery { pkceClient.retrieve() } returns fakePair
            coEvery { api.b2BOAuthDiscoveryAuthenticate(any()) } returns StytchDataResponse(mockk(relaxed = true))

            makeClient().discovery.authenticate(mockk<IB2BOAuthDiscoveryAuthenticateParameters>(relaxed = true))

            coVerify { api.b2BOAuthDiscoveryAuthenticate(match { it.pkceCodeVerifier == "test-verifier" }) }
            coVerify { pkceClient.revoke() }
        }

    @Test
    fun `discovery authenticate throws MissingPKCEException when no code pair stored`() =
        runTest(testDispatcher) {
            coEvery { pkceClient.retrieve() } returns null

            assertFailsWith<MissingPKCEException> {
                makeClient().discovery.authenticate(mockk<IB2BOAuthDiscoveryAuthenticateParameters>(relaxed = true))
            }
        }
}
