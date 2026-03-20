package com.stytch.sdk.encryption

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.security.MessageDigest
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

@RunWith(AndroidJUnit4::class)
internal class StytchEncryptionClientInstrumentedTest {
    private lateinit var client: StytchEncryptionClient

    @Before
    fun setUp() {
        client = StytchEncryptionClient()
    }

    @After
    fun tearDown() {
        client.deleteKey()
    }

    // --- encrypt / decrypt ---

    @Test
    fun encrypt_and_decrypt_round_trips_arbitrary_bytes() {
        val original = "hello, stytch!".toByteArray()
        val decrypted = client.decrypt(client.encrypt(original))
        assertEquals(original.toList(), decrypted.toList())
    }

    @Test
    fun encrypted_output_differs_from_plaintext() {
        val original = "hello, stytch!".toByteArray()
        assertNotEquals(original.toList(), client.encrypt(original).toList())
    }

    @Test
    fun two_encryptions_of_the_same_plaintext_produce_different_ciphertext() {
        val original = "hello, stytch!".toByteArray()
        assertNotEquals(client.encrypt(original).toList(), client.encrypt(original).toList())
    }

    @Test
    fun encrypt_round_trips_empty_byte_array() {
        val original = ByteArray(0)
        assertEquals(original.toList(), client.decrypt(client.encrypt(original)).toList())
    }

    // --- generateCodeVerifier / generateCodeChallenge ---

    @Test
    fun generateCodeVerifier_returns_32_bytes() {
        assertEquals(32, client.generateCodeVerifier().size)
    }

    @Test
    fun generateCodeVerifier_produces_different_values_on_successive_calls() {
        assertNotEquals(client.generateCodeVerifier().toList(), client.generateCodeVerifier().toList())
    }

    @Test
    fun generateCodeChallenge_returns_SHA256_of_the_verifier() {
        val verifier = client.generateCodeVerifier()
        val expected = MessageDigest.getInstance("SHA-256").digest(verifier)
        assertEquals(expected.toList(), client.generateCodeChallenge(verifier).toList())
    }

    // --- Ed25519 key generation ---

    @Test
    fun generateEd25519KeyPair_returns_32_byte_public_and_private_keys() {
        val pair = client.generateEd25519KeyPair()
        assertEquals(32, pair.publicKey.size)
        assertEquals(32, pair.privateKey.size)
    }

    @Test
    fun generateEd25519KeyPair_produces_unique_keys_on_successive_calls() {
        val pair1 = client.generateEd25519KeyPair()
        val pair2 = client.generateEd25519KeyPair()
        assertNotEquals(pair1.privateKey.toList(), pair2.privateKey.toList())
        assertNotEquals(pair1.publicKey.toList(), pair2.publicKey.toList())
    }

    @Test
    fun deriveEd25519PublicKeyFromPrivateKeyBytes_matches_the_generated_public_key() {
        val pair = client.generateEd25519KeyPair()
        val derived = client.deriveEd25519PublicKeyFromPrivateKeyBytes(pair.privateKey)
        assertEquals(pair.publicKey.toList(), derived.toList())
    }

    // --- signEd25519 ---

    @Test
    fun signEd25519_produces_a_64_byte_signature() {
        val pair = client.generateEd25519KeyPair()
        val sig = client.signEd25519(pair.privateKey, "test data".toByteArray())
        assertEquals(64, sig.size)
    }

    @Test
    fun signEd25519_produces_different_signatures_for_different_data() {
        val pair = client.generateEd25519KeyPair()
        val sig1 = client.signEd25519(pair.privateKey, "data one".toByteArray())
        val sig2 = client.signEd25519(pair.privateKey, "data two".toByteArray())
        assertNotEquals(sig1.toList(), sig2.toList())
    }

    // --- deleteKey ---

    @Test
    fun deleteKey_does_not_throw() {
        client.deleteKey()
    }

    @Test
    fun encrypt_still_works_after_deleteKey() {
        client.deleteKey()
        val fresh = StytchEncryptionClient()
        val original = "after delete".toByteArray()
        assertEquals(original.toList(), fresh.decrypt(fresh.encrypt(original)).toList())
        fresh.deleteKey()
    }
}
