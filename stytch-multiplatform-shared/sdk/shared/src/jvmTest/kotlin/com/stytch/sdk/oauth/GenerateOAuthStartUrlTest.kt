package com.stytch.sdk.oauth

import com.stytch.sdk.data.PKCECodePair
import com.stytch.sdk.data.PublicTokenInfo
import com.stytch.sdk.pkce.PKCEClient
import io.ktor.http.Url
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

private val fakePair = PKCECodePair(challenge = "test-challenge", verifier = "test-verifier")
private val publicTokenInfo = PublicTokenInfo(publicToken = "public-token-test-xxx", isTestToken = true)

internal class GenerateOAuthStartUrlTest {
    private val pkceClient = mockk<PKCEClient>()

    private suspend fun generate(
        baseUrl: String = "https://test.stytch.com/v1/public/oauth/google/start",
        parameters: OAuthStartParameters = OAuthStartParameters(),
    ): Url {
        coEvery { pkceClient.create() } returns fakePair
        val result = generateOAuthStartUrl(
            baseUrl = baseUrl,
            publicTokenInfo = publicTokenInfo,
            parameters = parameters,
            pkceClient = pkceClient,
        )
        return Url(result)
    }

    @Test
    fun `includes public_token and code_challenge`() = runTest {
        val url = generate()
        assertEquals("public-token-test-xxx", url.parameters["public_token"])
        assertEquals("test-challenge", url.parameters["code_challenge"])
    }

    @Test
    fun `preserves the base URL path`() = runTest {
        val url = generate(baseUrl = "https://test.stytch.com/v1/public/oauth/google/start")
        assertEquals("https", url.protocol.name)
        assertEquals("test.stytch.com", url.host)
        assertEquals("/v1/public/oauth/google/start", url.encodedPath)
    }

    @Test
    fun `includes loginRedirectUrl and signupRedirectUrl when provided`() = runTest {
        val url = generate(
            parameters = OAuthStartParameters(
                loginRedirectUrl = "myapp://login",
                signupRedirectUrl = "myapp://signup",
            ),
        )
        assertEquals("myapp://login", url.parameters["login_redirect_url"])
        assertEquals("myapp://signup", url.parameters["signup_redirect_url"])
    }

    @Test
    fun `omits null parameters`() = runTest {
        val url = generate(
            parameters = OAuthStartParameters(
                loginRedirectUrl = null,
                signupRedirectUrl = null,
                oauthAttachToken = null,
            ),
        )
        assertNull(url.parameters["login_redirect_url"])
        assertNull(url.parameters["signup_redirect_url"])
        assertNull(url.parameters["oauth_attach_token"])
    }

    @Test
    fun `joins customScopes with a space`() = runTest {
        val url = generate(
            parameters = OAuthStartParameters(customScopes = listOf("openid", "email", "profile")),
        )
        assertEquals("openid email profile", url.parameters["custom_scopes"])
    }

    @Test
    fun `prefixes providerParams keys with provider_`() = runTest {
        val url = generate(
            parameters = OAuthStartParameters(
                providerParams = mapOf("hd" to "example.com", "prompt" to "consent"),
            ),
        )
        assertEquals("example.com", url.parameters["provider_hd"])
        assertEquals("consent", url.parameters["provider_prompt"])
    }

    @Test
    fun `omits empty string values`() = runTest {
        val url = generate(
            parameters = OAuthStartParameters(loginRedirectUrl = ""),
        )
        assertNull(url.parameters["login_redirect_url"])
    }
}
