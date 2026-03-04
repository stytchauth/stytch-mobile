package com.stytch.sdk.consumer

import com.stytch.sdk.consumer.data.ConsumerTokenType
import com.stytch.sdk.consumer.data.DeeplinkAuthenticationStatus
import com.stytch.sdk.consumer.magicLinks.MagicLinksClient
import com.stytch.sdk.consumer.networking.models.MagicLinksAuthenticateResponse
import com.stytch.sdk.consumer.networking.models.OAuthAuthenticateResponse
import com.stytch.sdk.consumer.oauth.OAuthClient
import com.stytch.sdk.data.DeviceInfo
import com.stytch.sdk.data.EndpointOptions
import com.stytch.sdk.data.KMPPlatformType
import com.stytch.sdk.data.StytchClientConfigurationInternal
import com.stytch.sdk.encryption.StytchEncryptionClient
import com.stytch.sdk.persistence.StytchPersistenceClient
import com.stytch.sdk.persistence.StytchPlatformPersistenceClient
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
internal class DefaultStytchConsumerTest {
    private val testDispatcher = UnconfinedTestDispatcher()

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

    private fun makeConsumer() = DefaultStytchConsumer(config)

    // --- parseDeeplink ---

    @Test
    fun `parseDeeplink returns null when token param is absent`() {
        val result = makeConsumer().parseDeeplink("https://example.com?stytch_token_type=magic_links")
        assertNull(result)
    }

    @Test
    fun `parseDeeplink returns DeeplinkToken with MAGIC_LINKS type and token`() {
        val result = makeConsumer().parseDeeplink("https://example.com?stytch_token_type=magic_links&token=abc123")
        assertNotNull(result)
        assertEquals(ConsumerTokenType.MAGIC_LINKS, result.type)
        assertEquals("abc123", result.token)
    }

    @Test
    fun `parseDeeplink returns DeeplinkToken with OAUTH type and token`() {
        val result = makeConsumer().parseDeeplink("https://example.com?stytch_token_type=oauth&token=abc123")
        assertNotNull(result)
        assertEquals(ConsumerTokenType.OAUTH, result.type)
        assertEquals("abc123", result.token)
    }

    @Test
    fun `parseDeeplink returns DeeplinkToken with RESET_PASSWORD type and token`() {
        val result = makeConsumer().parseDeeplink("https://example.com?stytch_token_type=reset_password&token=abc123")
        assertNotNull(result)
        assertEquals(ConsumerTokenType.RESET_PASSWORD, result.type)
        assertEquals("abc123", result.token)
    }

    @Test
    fun `parseDeeplink returns DeeplinkToken with UNKNOWN type for unrecognized token type`() {
        val result = makeConsumer().parseDeeplink("https://example.com?stytch_token_type=something_else&token=abc123")
        assertNotNull(result)
        assertEquals(ConsumerTokenType.UNKNOWN, result.type)
        assertEquals("abc123", result.token)
    }

    @Test
    fun `parseDeeplink returns DeeplinkToken with UNKNOWN type when stytch_token_type param is absent`() {
        val result = makeConsumer().parseDeeplink("https://example.com?token=abc123")
        assertNotNull(result)
        assertEquals(ConsumerTokenType.UNKNOWN, result.type)
        assertEquals("abc123", result.token)
    }

    // --- getPKCECodePair ---

    @Test
    fun `getPKCECodePair returns null when nothing is stored`() =
        runTest {
            assertNull(makeConsumer().getPKCECodePair())
        }

    @Test
    fun `getPKCECodePair returns stored PKCECodePair`() =
        runTest(testDispatcher) {
            // Save via a real persistence client so values are stored in the correct encoded format
            val persistenceClient = StytchPersistenceClient(testDispatcher, encryptionClient, platformClient)
            persistenceClient.save("PKCE_CODE_CHALLENGE", "test-challenge")
            persistenceClient.save("PKCE_CODE_VERIFIER", "test-verifier")

            val result = makeConsumer().getPKCECodePair()
            assertNotNull(result)
            assertEquals("test-challenge", result.challenge)
            assertEquals("test-verifier", result.verifier)
        }

    // --- authenticate ---

    @Test
    fun `authenticate returns UnknownDeeplink when token param is absent`() =
        runTest {
            val result = makeConsumer().authenticate("https://example.com?stytch_token_type=magic_links", null)
            assertIs<DeeplinkAuthenticationStatus.UnknownDeeplink>(result)
        }

    @Test
    fun `authenticate returns UnknownDeeplink when token type is UNKNOWN`() =
        runTest {
            val result = makeConsumer().authenticate("https://example.com?stytch_token_type=unrecognized&token=abc123", null)
            assertIs<DeeplinkAuthenticationStatus.UnknownDeeplink>(result)
        }

    @Test
    fun `authenticate returns ManualHandlingRequired for reset_password token`() =
        runTest {
            val result = makeConsumer().authenticate("https://example.com?stytch_token_type=reset_password&token=abc123", null)
            assertIs<DeeplinkAuthenticationStatus.ManualHandlingRequired>(result)
        }

    @Test
    fun `authenticate calls magicLinks authenticate with correct token and explicit session duration`() =
        runTest {
            val mockMagicLinks = mockk<MagicLinksClient>(relaxed = true)
            coEvery { mockMagicLinks.authenticate(any()) } returns mockk<MagicLinksAuthenticateResponse>(relaxed = true)

            val consumer = spyk(makeConsumer()) { every { magicLinks } returns mockMagicLinks }

            consumer.authenticate("https://example.com?stytch_token_type=magic_links&token=tok123", 60)

            coVerify {
                mockMagicLinks.authenticate(match { it.token == "tok123" && it.sessionDurationMinutes == 60 })
            }
        }

    @Test
    fun `authenticate falls back to defaultSessionDuration for magic_links when sessionDurationMinutes is null`() =
        runTest {
            val mockMagicLinks = mockk<MagicLinksClient>(relaxed = true)
            coEvery { mockMagicLinks.authenticate(any()) } returns mockk<MagicLinksAuthenticateResponse>(relaxed = true)

            val consumer = spyk(makeConsumer()) { every { magicLinks } returns mockMagicLinks }

            consumer.authenticate("https://example.com?stytch_token_type=magic_links&token=tok123", null)

            coVerify {
                mockMagicLinks.authenticate(match { it.token == "tok123" && it.sessionDurationMinutes == 30 })
            }
        }

    @Test
    fun `authenticate calls oauth authenticate with correct token and explicit session duration`() =
        runTest {
            val mockOAuth = mockk<OAuthClient>(relaxed = true)
            coEvery { mockOAuth.authenticate(any()) } returns mockk<OAuthAuthenticateResponse>(relaxed = true)

            val consumer = spyk(makeConsumer()) { every { oauth } returns mockOAuth }

            consumer.authenticate("https://example.com?stytch_token_type=oauth&token=tok123", 60)

            coVerify {
                mockOAuth.authenticate(match { it.token == "tok123" && it.sessionDurationMinutes == 60 })
            }
        }

    @Test
    fun `authenticate falls back to defaultSessionDuration for oauth when sessionDurationMinutes is null`() =
        runTest {
            val mockOAuth = mockk<OAuthClient>(relaxed = true)
            coEvery { mockOAuth.authenticate(any()) } returns mockk<OAuthAuthenticateResponse>(relaxed = true)

            val consumer = spyk(makeConsumer()) { every { oauth } returns mockOAuth }

            consumer.authenticate("https://example.com?stytch_token_type=oauth&token=tok123", null)

            coVerify {
                mockOAuth.authenticate(match { it.token == "tok123" && it.sessionDurationMinutes == 30 })
            }
        }
}
