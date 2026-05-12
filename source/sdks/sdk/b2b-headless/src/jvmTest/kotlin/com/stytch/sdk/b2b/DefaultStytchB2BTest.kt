package com.stytch.sdk.b2b

import com.stytch.sdk.b2b.data.B2BTokenType
import com.stytch.sdk.b2b.data.DeeplinkAuthenticationStatus
import com.stytch.sdk.b2b.magicLinks.B2BMagicLinksClient
import com.stytch.sdk.b2b.networking.models.B2BMagicLinksAuthenticateResponse
import com.stytch.sdk.b2b.networking.models.B2BOAuthAuthenticateResponse
import com.stytch.sdk.b2b.networking.models.B2BSSOAuthEnticateResponse
import com.stytch.sdk.b2b.oauth.B2BOAuthClient
import com.stytch.sdk.b2b.sso.B2BSSOClient
import com.stytch.sdk.data.DeviceInfo
import com.stytch.sdk.data.EndpointOptions
import com.stytch.sdk.data.KMPPlatformType
import com.stytch.sdk.data.StytchClientConfigurationInternal
import com.stytch.sdk.encryption.StytchEncryptionClient
import com.stytch.sdk.persistence.StytchPlatformPersistenceClient
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
internal class DefaultStytchB2BTest {
    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    private val encryptionClient =
        mockk<StytchEncryptionClient> {
            every { encrypt(any()) } answers { firstArg() }
            every { decrypt(any()) } answers { firstArg() }
        }
    private val platformStore = mutableMapOf<String, String>()
    private val platformClient =
        mockk<StytchPlatformPersistenceClient> {
            every { saveData(any(), any()) } answers { platformStore[firstArg()] = secondArg() }
            every { getData(any()) } answers { platformStore[firstArg()] }
            every { removeData(any()) } answers {
                platformStore.remove(firstArg())
                Unit
            }
        }

    private val config =
        StytchClientConfigurationInternal(
            publicToken = "public-token-test-00000000-0000-0000-0000-000000000000",
            endpointOptions = EndpointOptions(),
            defaultSessionDuration = 30,
            deviceInfo = DeviceInfo("pkg", "1.0", "JVM", "1.0", "Test", "0x0"),
            platformPersistenceClient = platformClient,
            platform = KMPPlatformType.JVM,
            encryptionClient = encryptionClient,
            passkeyProvider = mockk(relaxed = true),
            biometricsProvider = mockk(relaxed = true),
            oAuthProvider = mockk(relaxed = true),
        )

    private fun makeB2B() = DefaultStytchB2B(config)

    // --- parseDeeplink ---

    @Test
    fun `parseDeeplink returns null when token param is absent`() {
        val result = makeB2B().parseDeeplink("https://example.com?stytch_token_type=multi_tenant_magic_links")
        assertNull(result)
    }

    @Test
    fun `parseDeeplink returns DeeplinkToken with MULTI_TENANT_MAGIC_LINKS type`() {
        val result = makeB2B().parseDeeplink("https://example.com?stytch_token_type=multi_tenant_magic_links&token=tok123")
        assertNotNull(result)
        assertEquals(B2BTokenType.MULTI_TENANT_MAGIC_LINKS, result.type)
        assertEquals("tok123", result.token)
    }

    @Test
    fun `parseDeeplink returns DeeplinkToken with SSO type`() {
        val result = makeB2B().parseDeeplink("https://example.com?stytch_token_type=sso&token=tok123")
        assertNotNull(result)
        assertEquals(B2BTokenType.SSO, result.type)
    }

    @Test
    fun `parseDeeplink returns DeeplinkToken with OAUTH type`() {
        val result = makeB2B().parseDeeplink("https://example.com?stytch_token_type=oauth&token=tok123")
        assertNotNull(result)
        assertEquals(B2BTokenType.OAUTH, result.type)
    }

    @Test
    fun `parseDeeplink returns DeeplinkToken with DISCOVERY type`() {
        val result = makeB2B().parseDeeplink("https://example.com?stytch_token_type=discovery&token=tok123")
        assertNotNull(result)
        assertEquals(B2BTokenType.DISCOVERY, result.type)
    }

    @Test
    fun `parseDeeplink returns DeeplinkToken with DISCOVERY_OAUTH type`() {
        val result = makeB2B().parseDeeplink("https://example.com?stytch_token_type=discovery_oauth&token=tok123")
        assertNotNull(result)
        assertEquals(B2BTokenType.DISCOVERY_OAUTH, result.type)
    }

    @Test
    fun `parseDeeplink returns DeeplinkToken with MULTI_TENANT_PASSWORDS type`() {
        val result = makeB2B().parseDeeplink("https://example.com?stytch_token_type=multi_tenant_passwords&token=tok123")
        assertNotNull(result)
        assertEquals(B2BTokenType.MULTI_TENANT_PASSWORDS, result.type)
    }

    @Test
    fun `parseDeeplink returns DeeplinkToken with UNKNOWN type for unrecognized token type`() {
        val result = makeB2B().parseDeeplink("https://example.com?stytch_token_type=something_else&token=tok123")
        assertNotNull(result)
        assertEquals(B2BTokenType.UNKNOWN, result.type)
    }

    @Test
    fun `parseDeeplink returns DeeplinkToken with UNKNOWN type when stytch_token_type param is absent`() {
        val result = makeB2B().parseDeeplink("https://example.com?token=tok123")
        assertNotNull(result)
        assertEquals(B2BTokenType.UNKNOWN, result.type)
        assertEquals("tok123", result.token)
    }

    // --- authenticate ---

    @Test
    fun `authenticate returns UnknownDeeplink when token param is absent`() =
        runTest {
            val url = "https://example.com?stytch_token_type=multi_tenant_magic_links"
            val result = makeB2B().authenticate(url, null)
            assertIs<DeeplinkAuthenticationStatus.UnknownDeeplink>(result)
            assertEquals(url, result.url)
        }

    @Test
    fun `authenticate returns UnknownDeeplink when token type is UNKNOWN`() =
        runTest {
            val url = "https://example.com?stytch_token_type=unrecognized&token=tok123"
            val result = makeB2B().authenticate(url, null)
            assertIs<DeeplinkAuthenticationStatus.UnknownDeeplink>(result)
            assertEquals(url, result.url)
        }

    @Test
    fun `authenticate returns ManualHandlingRequired for multi_tenant_passwords token`() =
        runTest {
            val result = makeB2B().authenticate("https://example.com?stytch_token_type=multi_tenant_passwords&token=tok123", null)
            assertIs<DeeplinkAuthenticationStatus.ManualHandlingRequired>(result)
            assertEquals("tok123", result.token)
        }

    @Test
    fun `authenticate returns ManualHandlingRequired for discovery token`() =
        runTest {
            val result = makeB2B().authenticate("https://example.com?stytch_token_type=discovery&token=tok123", null)
            assertIs<DeeplinkAuthenticationStatus.ManualHandlingRequired>(result)
            assertEquals("tok123", result.token)
        }

    @Test
    fun `authenticate returns ManualHandlingRequired for discovery_oauth token`() =
        runTest {
            val result = makeB2B().authenticate("https://example.com?stytch_token_type=discovery_oauth&token=tok123", null)
            assertIs<DeeplinkAuthenticationStatus.ManualHandlingRequired>(result)
            assertEquals("tok123", result.token)
        }

    @Test
    fun `authenticate calls magicLinks authenticate with correct token and explicit session duration`() =
        runTest {
            val mockMagicLinks = mockk<B2BMagicLinksClient>(relaxed = true)
            coEvery { mockMagicLinks.authenticate(any()) } returns mockk<B2BMagicLinksAuthenticateResponse>(relaxed = true)

            val b2b = spyk(makeB2B()) { every { magicLinks } returns mockMagicLinks }

            b2b.authenticate("https://example.com?stytch_token_type=multi_tenant_magic_links&token=tok123", 60)

            coVerify {
                mockMagicLinks.authenticate(match { it.magicLinksToken == "tok123" && it.sessionDurationMinutes == 60 })
            }
        }

    @Test
    fun `authenticate falls back to defaultSessionDuration for magic_links when sessionDurationMinutes is null`() =
        runTest {
            val mockMagicLinks = mockk<B2BMagicLinksClient>(relaxed = true)
            coEvery { mockMagicLinks.authenticate(any()) } returns mockk<B2BMagicLinksAuthenticateResponse>(relaxed = true)

            val b2b = spyk(makeB2B()) { every { magicLinks } returns mockMagicLinks }

            b2b.authenticate("https://example.com?stytch_token_type=multi_tenant_magic_links&token=tok123", null)

            coVerify {
                mockMagicLinks.authenticate(match { it.magicLinksToken == "tok123" && it.sessionDurationMinutes == 30 })
            }
        }

    @Test
    fun `authenticate calls oauth authenticate with correct token and explicit session duration`() =
        runTest {
            val mockOAuth = mockk<B2BOAuthClient>(relaxed = true)
            coEvery { mockOAuth.authenticate(any()) } returns mockk<B2BOAuthAuthenticateResponse>(relaxed = true)

            val b2b = spyk(makeB2B()) { every { oauth } returns mockOAuth }

            b2b.authenticate("https://example.com?stytch_token_type=oauth&token=tok123", 60)

            coVerify {
                mockOAuth.authenticate(match { it.oauthToken == "tok123" && it.sessionDurationMinutes == 60 })
            }
        }

    @Test
    fun `authenticate falls back to defaultSessionDuration for oauth when sessionDurationMinutes is null`() =
        runTest {
            val mockOAuth = mockk<B2BOAuthClient>(relaxed = true)
            coEvery { mockOAuth.authenticate(any()) } returns mockk<B2BOAuthAuthenticateResponse>(relaxed = true)

            val b2b = spyk(makeB2B()) { every { oauth } returns mockOAuth }

            b2b.authenticate("https://example.com?stytch_token_type=oauth&token=tok123", null)

            coVerify {
                mockOAuth.authenticate(match { it.oauthToken == "tok123" && it.sessionDurationMinutes == 30 })
            }
        }

    @Test
    fun `authenticate calls sso authenticate with correct token and explicit session duration`() =
        runTest {
            val mockSSO = mockk<B2BSSOClient>(relaxed = true)
            coEvery { mockSSO.authenticate(any()) } returns mockk<B2BSSOAuthEnticateResponse>(relaxed = true)

            val b2b = spyk(makeB2B()) { every { sso } returns mockSSO }

            b2b.authenticate("https://example.com?stytch_token_type=sso&token=tok123", 60)

            coVerify {
                mockSSO.authenticate(match { it.ssoToken == "tok123" && it.sessionDurationMinutes == 60 })
            }
        }

    @Test
    fun `authenticate falls back to defaultSessionDuration for sso when sessionDurationMinutes is null`() =
        runTest {
            val mockSSO = mockk<B2BSSOClient>(relaxed = true)
            coEvery { mockSSO.authenticate(any()) } returns mockk<B2BSSOAuthEnticateResponse>(relaxed = true)

            val b2b = spyk(makeB2B()) { every { sso } returns mockSSO }

            b2b.authenticate("https://example.com?stytch_token_type=sso&token=tok123", null)

            coVerify {
                mockSSO.authenticate(match { it.ssoToken == "tok123" && it.sessionDurationMinutes == 30 })
            }
        }
}
