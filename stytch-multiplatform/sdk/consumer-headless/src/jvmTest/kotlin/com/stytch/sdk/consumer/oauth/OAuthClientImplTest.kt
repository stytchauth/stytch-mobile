package com.stytch.sdk.consumer.oauth

import com.stytch.sdk.consumer.ConsumerClientTest
import com.stytch.sdk.consumer.networking.models.ApiUserV1Name
import com.stytch.sdk.consumer.networking.models.OAuthAttachParameters
import com.stytch.sdk.consumer.networking.models.OAuthAttachRequest
import com.stytch.sdk.consumer.networking.models.OAuthAuthenticateParameters
import com.stytch.sdk.consumer.networking.models.OAuthAuthenticateRequest
import com.stytch.sdk.consumer.networking.models.OAuthAppleIDTokenAuthenticateParameters
import com.stytch.sdk.consumer.networking.models.OAuthAppleIDTokenAuthenticateRequest
import com.stytch.sdk.consumer.networking.models.OAuthGoogleIDTokenAuthenticateParameters
import com.stytch.sdk.consumer.networking.models.OAuthGoogleIDTokenAuthenticateRequest
import com.stytch.sdk.data.EndpointOptions
import com.stytch.sdk.data.PKCECodePair
import com.stytch.sdk.data.PublicTokenInfo
import com.stytch.sdk.data.StytchDataResponse
import com.stytch.sdk.oauth.IOAuthProvider
import com.stytch.sdk.oauth.OAuthProviderType
import com.stytch.sdk.oauth.OAuthResult
import com.stytch.sdk.oauth.OAuthStartParameters
import com.stytch.sdk.pkce.PKCEClient
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@OptIn(ExperimentalCoroutinesApi::class)
internal class OAuthClientImplTest : ConsumerClientTest() {
    private val pkceClient = mockk<PKCEClient>(relaxed = true)
    private val oauthProvider = mockk<IOAuthProvider>(relaxed = true)

    private val fakePair = PKCECodePair(challenge = "test-challenge", verifier = "test-verifier")
    private val startParams = OAuthStartParameters(sessionDurationMinutes = 30)

    private fun makeClient(
        isTestToken: Boolean = true,
        cnameDomain: () -> String? = { null },
        defaultSessionDuration: Int = 5,
    ) = OAuthClientImpl(
        publicTokenInfo = PublicTokenInfo(publicToken = "public-token-test-xxx", isTestToken = isTestToken),
        endpointOptions = EndpointOptions(),
        cnameDomain = cnameDomain,
        dispatchers = dispatchers,
        networkingClient = networkingClient,
        pkceClient = pkceClient,
        oauthProvider = oauthProvider,
        defaultSessionDuration = defaultSessionDuration,
    )

    // --- authenticate ---

    @Test
    fun `authenticate retrieves PKCE and calls oAuthAuthenticate with code verifier`() = runTest(testDispatcher) {
        coEvery { pkceClient.retrieve() } returns fakePair
        coEvery { api.oAuthAuthenticate(any()) } returns StytchDataResponse(mockk(relaxed = true))

        makeClient().authenticate(OAuthAuthenticateParameters(token = "tok", sessionDurationMinutes = 30))

        coVerify {
            api.oAuthAuthenticate(
                OAuthAuthenticateRequest(token = "tok", sessionDurationMinutes = 30, codeVerifier = "test-verifier"),
            )
        }
    }

    @Test
    fun `authenticate throws IllegalStateException when PKCE is missing`() = runTest(testDispatcher) {
        coEvery { pkceClient.retrieve() } returns null

        assertFailsWith<IllegalStateException> {
            makeClient().authenticate(OAuthAuthenticateParameters(token = "tok", sessionDurationMinutes = 30))
        }
    }

    // --- authenticateGoogleIdToken ---

    @Test
    fun `authenticateGoogleIdToken calls oAuthGoogleIDTokenAuthenticate with correct model`() = runTest(testDispatcher) {
        coEvery { api.oAuthGoogleIDTokenAuthenticate(any()) } returns StytchDataResponse(mockk(relaxed = true))

        makeClient().authenticateGoogleIdToken(
            OAuthGoogleIDTokenAuthenticateParameters(idToken = "id-tok", sessionDurationMinutes = 30, nonce = "nonce123"),
        )

        coVerify {
            api.oAuthGoogleIDTokenAuthenticate(
                OAuthGoogleIDTokenAuthenticateRequest(idToken = "id-tok", sessionDurationMinutes = 30, nonce = "nonce123"),
            )
        }
    }

    // --- authenticateAppleIdToken ---

    @Test
    fun `authenticateAppleIdToken calls oAuthAppleIDTokenAuthenticate with correct model`() = runTest(testDispatcher) {
        coEvery { api.oAuthAppleIDTokenAuthenticate(any()) } returns StytchDataResponse(mockk(relaxed = true))

        makeClient().authenticateAppleIdToken(
            OAuthAppleIDTokenAuthenticateParameters(idToken = "id-tok", sessionDurationMinutes = 30, nonce = "nonce123"),
        )

        coVerify {
            api.oAuthAppleIDTokenAuthenticate(
                OAuthAppleIDTokenAuthenticateRequest(idToken = "id-tok", sessionDurationMinutes = 30, nonce = "nonce123"),
            )
        }
    }

    // --- attach ---

    @Test
    fun `attach calls oAuthAttach with correct model`() = runTest(testDispatcher) {
        coEvery { api.oAuthAttach(any()) } returns StytchDataResponse(mockk(relaxed = true))

        makeClient().attach(OAuthAttachParameters(provider = "google"))

        coVerify { api.oAuthAttach(OAuthAttachRequest(provider = "google")) }
    }

    // --- start: URL construction ---

    private fun stubStartForUrlTest(result: OAuthResult = OAuthResult.ClassicToken("tok")): String {
        var capturedUrl = ""
        coEvery { oauthProvider.getOAuthToken(any(), any(), any(), any(), any(), any()) } coAnswers {
            capturedUrl = arg(4)
            result
        }
        coEvery { pkceClient.retrieve() } returns fakePair
        coEvery { api.oAuthAuthenticate(any()) } returns StytchDataResponse(mockk(relaxed = true))
        return capturedUrl.also { /* returned after call */ }
    }

    @Test
    fun `start uses CNAME domain when provided`() = runTest(testDispatcher) {
        var capturedUrl = ""
        coEvery { oauthProvider.getOAuthToken(any(), any(), any(), any(), any(), any()) } coAnswers {
            capturedUrl = arg(4); OAuthResult.ClassicToken("tok")
        }
        coEvery { pkceClient.retrieve() } returns fakePair
        coEvery { api.oAuthAuthenticate(any()) } returns StytchDataResponse(mockk(relaxed = true))

        makeClient(cnameDomain = { "custom.example.com" }).apple.start(startParams)

        assertEquals("https://custom.example.com/v1/public/oauth/apple/start", capturedUrl)
    }

    @Test
    fun `start uses test domain when no CNAME and token is a test token`() = runTest(testDispatcher) {
        var capturedUrl = ""
        coEvery { oauthProvider.getOAuthToken(any(), any(), any(), any(), any(), any()) } coAnswers {
            capturedUrl = arg(4); OAuthResult.ClassicToken("tok")
        }
        coEvery { pkceClient.retrieve() } returns fakePair
        coEvery { api.oAuthAuthenticate(any()) } returns StytchDataResponse(mockk(relaxed = true))

        makeClient(isTestToken = true).apple.start(startParams)

        assertEquals("https://test.stytch.com/v1/public/oauth/apple/start", capturedUrl)
    }

    @Test
    fun `start uses live domain when no CNAME and token is a live token`() = runTest(testDispatcher) {
        var capturedUrl = ""
        coEvery { oauthProvider.getOAuthToken(any(), any(), any(), any(), any(), any()) } coAnswers {
            capturedUrl = arg(4); OAuthResult.ClassicToken("tok")
        }
        coEvery { pkceClient.retrieve() } returns fakePair
        coEvery { api.oAuthAuthenticate(any()) } returns StytchDataResponse(mockk(relaxed = true))

        makeClient(isTestToken = false).apple.start(startParams)

        assertEquals("https://api.stytch.com/v1/public/oauth/apple/start", capturedUrl)
    }

    // --- start: OAuthResult routing ---

    @Test
    fun `start with ClassicToken calls authenticate`() = runTest(testDispatcher) {
        coEvery { oauthProvider.getOAuthToken(any(), any(), any(), any(), any(), any()) } returns OAuthResult.ClassicToken("tok")
        coEvery { pkceClient.retrieve() } returns fakePair
        coEvery { api.oAuthAuthenticate(any()) } returns StytchDataResponse(mockk(relaxed = true))

        makeClient().apple.start(startParams)

        coVerify { api.oAuthAuthenticate(any()) }
    }

    @Test
    fun `start with Google IDToken calls authenticateGoogleIdToken`() = runTest(testDispatcher) {
        coEvery { oauthProvider.getOAuthToken(any(), any(), any(), any(), any(), any()) } returns
            OAuthResult.IDToken(token = "id-tok", nonce = "nonce123")
        coEvery { api.oAuthGoogleIDTokenAuthenticate(any()) } returns StytchDataResponse(mockk(relaxed = true))

        makeClient().google.start(startParams)

        coVerify { api.oAuthGoogleIDTokenAuthenticate(any()) }
        coVerify(exactly = 0) { api.oAuthAppleIDTokenAuthenticate(any()) }
    }

    @Test
    fun `start with non-Google IDToken calls authenticateAppleIdToken`() = runTest(testDispatcher) {
        coEvery { oauthProvider.getOAuthToken(any(), any(), any(), any(), any(), any()) } returns
            OAuthResult.IDToken(token = "id-tok", nonce = "nonce123")
        coEvery { api.oAuthAppleIDTokenAuthenticate(any()) } returns StytchDataResponse(mockk(relaxed = true))

        makeClient().apple.start(startParams)

        coVerify { api.oAuthAppleIDTokenAuthenticate(any()) }
        coVerify(exactly = 0) { api.oAuthGoogleIDTokenAuthenticate(any()) }
    }

    @Test
    fun `start with Error result propagates the error`() = runTest(testDispatcher) {
        val error = RuntimeException("OAuth failed")
        coEvery { oauthProvider.getOAuthToken(any(), any(), any(), any(), any(), any()) } returns OAuthResult.Error(error)

        val thrown = assertFailsWith<RuntimeException> { makeClient().apple.start(startParams) }
        assertEquals("OAuth failed", thrown.message)
    }

    // --- start: session duration fallback ---

    @Test
    fun `start uses sessionDurationMinutes from parameters when provided`() = runTest(testDispatcher) {
        coEvery { oauthProvider.getOAuthToken(any(), any(), any(), any(), any(), any()) } returns OAuthResult.ClassicToken("tok")
        coEvery { pkceClient.retrieve() } returns fakePair
        coEvery { api.oAuthAuthenticate(any()) } returns StytchDataResponse(mockk(relaxed = true))

        makeClient(defaultSessionDuration = 15).apple.start(OAuthStartParameters(sessionDurationMinutes = 30))

        coVerify { api.oAuthAuthenticate(match { it.sessionDurationMinutes == 30 }) }
    }

    @Test
    fun `start falls back to defaultSessionDuration when params sessionDurationMinutes is null`() = runTest(testDispatcher) {
        coEvery { oauthProvider.getOAuthToken(any(), any(), any(), any(), any(), any()) } returns OAuthResult.ClassicToken("tok")
        coEvery { pkceClient.retrieve() } returns fakePair
        coEvery { api.oAuthAuthenticate(any()) } returns StytchDataResponse(mockk(relaxed = true))

        makeClient(defaultSessionDuration = 15).apple.start(OAuthStartParameters(sessionDurationMinutes = null))

        coVerify { api.oAuthAuthenticate(match { it.sessionDurationMinutes == 15 }) }
    }

    // --- start: name parsing (via Apple IDToken) ---

    private fun stubAppleIdTokenStart(name: String?) {
        coEvery { oauthProvider.getOAuthToken(any(), any(), any(), any(), any(), any()) } returns
            OAuthResult.IDToken(token = "id-tok", nonce = "nonce", name = name)
        coEvery { api.oAuthAppleIDTokenAuthenticate(any()) } returns StytchDataResponse(mockk(relaxed = true))
    }

    @Test
    fun `name with 3 or more parts parses to firstName, middleName, and joined lastName`() = runTest(testDispatcher) {
        stubAppleIdTokenStart("John Middle Last")

        makeClient().apple.start(startParams)

        coVerify {
            api.oAuthAppleIDTokenAuthenticate(
                match { it.name == ApiUserV1Name(firstName = "John", middleName = "Middle", lastName = "Last") },
            )
        }
    }

    @Test
    fun `name with 2 parts parses to firstName and lastName`() = runTest(testDispatcher) {
        stubAppleIdTokenStart("John Last")

        makeClient().apple.start(startParams)

        coVerify {
            api.oAuthAppleIDTokenAuthenticate(
                match { it.name == ApiUserV1Name(firstName = "John", lastName = "Last") },
            )
        }
    }

    @Test
    fun `name with 1 part parses to firstName only`() = runTest(testDispatcher) {
        stubAppleIdTokenStart("John")

        makeClient().apple.start(startParams)

        coVerify {
            api.oAuthAppleIDTokenAuthenticate(
                match { it.name == ApiUserV1Name(firstName = "John") },
            )
        }
    }
}
