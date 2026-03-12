package com.stytch.sdk.b2b.sso

import com.stytch.sdk.b2b.B2BClientTest
import com.stytch.sdk.b2b.StytchB2BAuthenticationStateManager
import com.stytch.sdk.b2b.networking.models.IB2BCreateExternalConnectionParameters
import com.stytch.sdk.b2b.networking.models.IB2BCreateOIDCConnectionParameters
import com.stytch.sdk.b2b.networking.models.IB2BCreateSAMLConnectionParameters
import com.stytch.sdk.b2b.networking.models.IB2BSSOAuthEnticateParameters
import com.stytch.sdk.b2b.networking.models.IB2BUpdateExternalConnectionParameters
import com.stytch.sdk.b2b.networking.models.IB2BUpdateOIDCConnectionParameters
import com.stytch.sdk.b2b.networking.models.IB2BUpdateSAMLConnectionByURLParameters
import com.stytch.sdk.b2b.networking.models.IB2BUpdateSAMLConnectionParameters
import com.stytch.sdk.data.EndpointOptions
import com.stytch.sdk.data.PKCECodePair
import com.stytch.sdk.data.PublicTokenInfo
import com.stytch.sdk.data.StytchDataResponse
import com.stytch.sdk.oauth.B2BSSOStartParameters
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
internal class B2BSSOClientImplTest : B2BClientTest() {
    private val pkceClient = mockk<PKCEClient>(relaxed = true)
    private val sessionManager = mockk<StytchB2BAuthenticationStateManager>(relaxed = true)
    private val oauthProvider = mockk<IOAuthProvider>(relaxed = true)
    private val fakePair = PKCECodePair(challenge = "test-challenge", verifier = "test-verifier")

    private fun makeClient(
        isTestToken: Boolean = true,
        cnameDomain: () -> String? = { null },
        defaultSessionDuration: Int = 30,
    ) = B2BSSOClientImpl(
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

    // --- start ---

    @Test
    fun `start with ClassicToken calls b2BSSOAuthEnticate`() =
        runTest(testDispatcher) {
            coEvery { pkceClient.create() } returns fakePair
            coEvery { pkceClient.retrieve() } returns fakePair
            coEvery { oauthProvider.startBrowserFlow(any(), any(), any()) } returns OAuthResult.ClassicToken("sso-tok")
            coEvery { api.b2BSSOAuthEnticate(any()) } returns StytchDataResponse(mockk(relaxed = true))

            makeClient().start(B2BSSOStartParameters(connectionId = "conn-123"))

            coVerify { api.b2BSSOAuthEnticate(any()) }
            coVerify { pkceClient.revoke() }
        }

    @Test
    fun `start with Error result throws OAuthException`() =
        runTest(testDispatcher) {
            coEvery { pkceClient.create() } returns fakePair
            coEvery { oauthProvider.startBrowserFlow(any(), any(), any()) } returns OAuthResult.Error("SSO failed")

            val thrown =
                assertFailsWith<OAuthException> {
                    makeClient().start(B2BSSOStartParameters(connectionId = "conn-123"))
                }
            assertEquals("SSO failed", thrown.cause?.message)
        }

    @Test
    fun `start with unexpected OAuthResult type throws OAuthException`() =
        runTest(testDispatcher) {
            coEvery { pkceClient.create() } returns fakePair
            coEvery { oauthProvider.startBrowserFlow(any(), any(), any()) } returns
                OAuthResult.IDToken(token = "id-tok", nonce = "nonce")

            assertFailsWith<OAuthException> {
                makeClient().start(B2BSSOStartParameters(connectionId = "conn-123"))
            }
        }

    // --- start: URL construction ---

    @Test
    fun `start URL includes pkce_code_challenge and connection_id`() =
        runTest(testDispatcher) {
            var capturedUrl = ""
            coEvery { pkceClient.create() } returns fakePair
            coEvery { pkceClient.retrieve() } returns fakePair
            coEvery { oauthProvider.startBrowserFlow(any(), any(), any()) } coAnswers {
                capturedUrl = firstArg()
                OAuthResult.ClassicToken("tok")
            }
            coEvery { api.b2BSSOAuthEnticate(any()) } returns StytchDataResponse(mockk(relaxed = true))

            makeClient().start(B2BSSOStartParameters(connectionId = "conn-123"))

            assertTrue(capturedUrl.contains("pkce_code_challenge=test-challenge"))
            assertTrue(capturedUrl.contains("connection_id=conn-123"))
        }

    @Test
    fun `start uses test domain when no CNAME and token is a test token`() =
        runTest(testDispatcher) {
            var capturedUrl = ""
            coEvery { pkceClient.create() } returns fakePair
            coEvery { pkceClient.retrieve() } returns fakePair
            coEvery { oauthProvider.startBrowserFlow(any(), any(), any()) } coAnswers {
                capturedUrl = firstArg()
                OAuthResult.ClassicToken("tok")
            }
            coEvery { api.b2BSSOAuthEnticate(any()) } returns StytchDataResponse(mockk(relaxed = true))

            makeClient(isTestToken = true).start(B2BSSOStartParameters(connectionId = "conn-123"))

            assertTrue(capturedUrl.startsWith("https://test.stytch.com/b2b/public/sso/start"))
        }

    @Test
    fun `start falls back to defaultSessionDuration when params sessionDurationMinutes is null`() =
        runTest(testDispatcher) {
            coEvery { pkceClient.create() } returns fakePair
            coEvery { pkceClient.retrieve() } returns fakePair
            coEvery { oauthProvider.startBrowserFlow(any(), any(), any()) } returns OAuthResult.ClassicToken("tok")
            coEvery { api.b2BSSOAuthEnticate(any()) } returns StytchDataResponse(mockk(relaxed = true))

            makeClient(defaultSessionDuration = 15).start(B2BSSOStartParameters(connectionId = "conn", sessionDurationMinutes = null))

            coVerify { api.b2BSSOAuthEnticate(match { it.sessionDurationMinutes == 15 }) }
        }

    // --- authenticate ---

    @Test
    fun `authenticate retrieves PKCE and calls b2BSSOAuthEnticate with verifier`() =
        runTest(testDispatcher) {
            coEvery { pkceClient.retrieve() } returns fakePair
            every { sessionManager.intermediateSessionToken } returns null
            coEvery { api.b2BSSOAuthEnticate(any()) } returns StytchDataResponse(mockk(relaxed = true))

            makeClient().authenticate(mockk<IB2BSSOAuthEnticateParameters>(relaxed = true))

            coVerify { api.b2BSSOAuthEnticate(match { it.pkceCodeVerifier == "test-verifier" }) }
            coVerify { pkceClient.revoke() }
        }

    @Test
    fun `authenticate throws MissingPKCEException when no code pair stored`() =
        runTest(testDispatcher) {
            coEvery { pkceClient.retrieve() } returns null

            assertFailsWith<MissingPKCEException> {
                makeClient().authenticate(mockk<IB2BSSOAuthEnticateParameters>(relaxed = true))
            }
        }

    // --- getConnections / deleteConnection ---

    @Test
    fun `getConnections calls b2BGetSSOConnections`() =
        runTest(testDispatcher) {
            coEvery { api.b2BGetSSOConnections() } returns StytchDataResponse(mockk(relaxed = true))

            makeClient().getConnections()

            coVerify { api.b2BGetSSOConnections() }
        }

    @Test
    fun `deleteConnection calls b2BDeleteSSOConnection with connectionId`() =
        runTest(testDispatcher) {
            coEvery { api.b2BDeleteSSOConnection(any()) } returns StytchDataResponse(mockk(relaxed = true))

            makeClient().deleteConnection("conn-123")

            coVerify { api.b2BDeleteSSOConnection("conn-123") }
        }

    // --- saml ---

    @Test
    fun `saml createConnection calls b2BCreateSAMLConnection`() =
        runTest(testDispatcher) {
            coEvery { api.b2BCreateSAMLConnection(any()) } returns StytchDataResponse(mockk(relaxed = true))

            makeClient().saml.createConnection(mockk<IB2BCreateSAMLConnectionParameters>(relaxed = true))

            coVerify { api.b2BCreateSAMLConnection(any()) }
        }

    @Test
    fun `saml updateConnection calls b2BUpdateSAMLConnection with connectionId`() =
        runTest(testDispatcher) {
            coEvery { api.b2BUpdateSAMLConnection(any(), any()) } returns StytchDataResponse(mockk(relaxed = true))

            makeClient().saml.updateConnection("conn-123", mockk<IB2BUpdateSAMLConnectionParameters>(relaxed = true))

            coVerify { api.b2BUpdateSAMLConnection(eq("conn-123"), any()) }
        }

    @Test
    fun `saml updateConnectionByUrl calls b2BUpdateSAMLConnectionByURL with connectionId`() =
        runTest(testDispatcher) {
            coEvery { api.b2BUpdateSAMLConnectionByURL(any(), any()) } returns StytchDataResponse(mockk(relaxed = true))

            makeClient().saml.updateConnectionByUrl("conn-123", mockk<IB2BUpdateSAMLConnectionByURLParameters>(relaxed = true))

            coVerify { api.b2BUpdateSAMLConnectionByURL(eq("conn-123"), any()) }
        }

    @Test
    fun `saml deleteVerificationCertificate calls b2BDeleteSAMLVerificationCertificate`() =
        runTest(testDispatcher) {
            coEvery { api.b2BDeleteSAMLVerificationCertificate(any(), any()) } returns StytchDataResponse(mockk(relaxed = true))

            makeClient().saml.deleteVerificationCertificate("conn-123", "cert-456")

            coVerify { api.b2BDeleteSAMLVerificationCertificate("conn-123", "cert-456") }
        }

    // --- oidc ---

    @Test
    fun `oidc createConnection calls b2BCreateOIDCConnection`() =
        runTest(testDispatcher) {
            coEvery { api.b2BCreateOIDCConnection(any()) } returns StytchDataResponse(mockk(relaxed = true))

            makeClient().oidc.createConnection(mockk<IB2BCreateOIDCConnectionParameters>(relaxed = true))

            coVerify { api.b2BCreateOIDCConnection(any()) }
        }

    @Test
    fun `oidc updateConnection calls b2BUpdateOIDCConnection with connectionId`() =
        runTest(testDispatcher) {
            coEvery { api.b2BUpdateOIDCConnection(any(), any()) } returns StytchDataResponse(mockk(relaxed = true))

            makeClient().oidc.updateConnection("conn-123", mockk<IB2BUpdateOIDCConnectionParameters>(relaxed = true))

            coVerify { api.b2BUpdateOIDCConnection(eq("conn-123"), any()) }
        }

    // --- external ---

    @Test
    fun `external createConnection calls b2BCreateExternalConnection`() =
        runTest(testDispatcher) {
            coEvery { api.b2BCreateExternalConnection(any()) } returns StytchDataResponse(mockk(relaxed = true))

            makeClient().external.createConnection(mockk<IB2BCreateExternalConnectionParameters>(relaxed = true))

            coVerify { api.b2BCreateExternalConnection(any()) }
        }

    @Test
    fun `external updateConnection calls b2BUpdateExternalConnection with connectionId`() =
        runTest(testDispatcher) {
            coEvery { api.b2BUpdateExternalConnection(any(), any()) } returns StytchDataResponse(mockk(relaxed = true))

            makeClient().external.updateConnection("conn-123", mockk<IB2BUpdateExternalConnectionParameters>(relaxed = true))

            coVerify { api.b2BUpdateExternalConnection(eq("conn-123"), any()) }
        }
}
