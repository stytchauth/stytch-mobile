package com.stytch.sdk.pkce

import com.stytch.sdk.encryption.StytchEncryptionClient
import com.stytch.sdk.persistence.StytchPersistenceClient
import com.stytch.sdk.persistence.StytchPlatformPersistenceClient
import com.stytch.sdk.utils.stytchUrlEncode
import io.ktor.util.encodeBase64
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class PKCEClientTest {
    private val dispatcher = UnconfinedTestDispatcher()

    // StytchPersistenceClient.save/get/remove are inline, so MockK cannot intercept them.
    // We use a real StytchPersistenceClient backed by a mocked StytchPlatformPersistenceClient
    // (an expect class) and identity encryption so we can observe platform-level storage.
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

    private val persistenceClient = StytchPersistenceClient(dispatcher, encryptionClient, platformClient)
    private val client = PKCEClient(encryptionClient, persistenceClient)

    private val fakeVerifierBytes = byteArrayOf(1, 2, 3, 4)
    private val fakeChallengeBytes = byteArrayOf(0x0A, 0x1B, 0x2C)

    private val expectedVerifier = fakeVerifierBytes.encodeBase64().stytchUrlEncode()
    private val expectedChallenge = fakeChallengeBytes.encodeBase64().stytchUrlEncode()

    private fun setupEncryption() {
        every { encryptionClient.generateCodeVerifier() } returns fakeVerifierBytes
        every { encryptionClient.generateCodeChallenge(expectedVerifier.encodeToByteArray()) } returns fakeChallengeBytes
    }

    // --- create ---

    @Test
    fun `create returns PKCECodePair with correctly encoded verifier`() =
        runTest(dispatcher) {
            setupEncryption()

            val pair = client.create()

            assertEquals(expectedVerifier, pair.verifier)
        }

    @Test
    fun `create returns PKCECodePair with hex-then-base64 encoded challenge`() =
        runTest(dispatcher) {
            setupEncryption()

            val pair = client.create()

            assertEquals(expectedChallenge, pair.challenge)
        }

    @Test
    fun `create persists challenge and verifier to platform storage`() =
        runTest(dispatcher) {
            setupEncryption()

            client.create()

            // Verify both keys landed in platform storage (via real StytchPersistenceClient)
            verify { platformClient.saveData(eq("PKCE_CODE_CHALLENGE"), any()) }
            verify { platformClient.saveData(eq("PKCE_CODE_VERIFIER"), any()) }
        }

    @Test
    fun `create passes verifier bytes to generateCodeChallenge`() =
        runTest(dispatcher) {
            setupEncryption()

            client.create()

            verify { encryptionClient.generateCodeChallenge(expectedVerifier.encodeToByteArray()) }
        }

    // --- challenge encoding ---

    @Test
    fun `challenge is base64url encoded`() =
        runTest(dispatcher) {
            val challengeBytes = byteArrayOf(0x0F, 0x00)
            every { encryptionClient.generateCodeVerifier() } returns fakeVerifierBytes
            every { encryptionClient.generateCodeChallenge(expectedVerifier.encodeToByteArray()) } returns challengeBytes

            val pair = client.create()

            assertEquals(challengeBytes.encodeBase64().stytchUrlEncode(), pair.challenge)
        }

    // --- retrieve ---

    @Test
    fun `retrieve returns PKCECodePair when both keys are persisted`() =
        runTest(dispatcher) {
            setupEncryption()
            client.create()

            val pair = client.retrieve()

            assertNotNull(pair)
            assertEquals(expectedChallenge, pair.challenge)
            assertEquals(expectedVerifier, pair.verifier)
        }

    @Test
    fun `retrieve returns null when challenge is missing`() =
        runTest(dispatcher) {
            setupEncryption()
            client.create()
            platformStore.remove("PKCE_CODE_CHALLENGE")

            assertNull(client.retrieve())
        }

    @Test
    fun `retrieve returns null when verifier is missing`() =
        runTest(dispatcher) {
            setupEncryption()
            client.create()
            platformStore.remove("PKCE_CODE_VERIFIER")

            assertNull(client.retrieve())
        }

    @Test
    fun `retrieve returns null when nothing has been created`() =
        runTest(dispatcher) {
            assertNull(client.retrieve())
        }

    // --- revoke ---

    @Test
    fun `revoke removes both keys from platform storage`() =
        runTest(dispatcher) {
            setupEncryption()
            client.create()

            client.revoke()

            verify { platformClient.removeData("PKCE_CODE_CHALLENGE") }
            verify { platformClient.removeData("PKCE_CODE_VERIFIER") }
        }

    @Test
    fun `retrieve returns null after revoke`() =
        runTest(dispatcher) {
            setupEncryption()
            client.create()
            client.revoke()

            assertNull(client.retrieve())
        }
}
