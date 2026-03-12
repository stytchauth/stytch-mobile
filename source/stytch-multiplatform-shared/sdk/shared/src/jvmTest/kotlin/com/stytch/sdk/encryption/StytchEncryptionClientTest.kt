package com.stytch.sdk.encryption

import java.io.File
import java.security.MessageDigest
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

internal class StytchEncryptionClientTest {
    private val client = StytchEncryptionClient("test-password")

    @AfterTest
    fun cleanup() {
        client.deleteKey()
        File("com.stytch.mobile.keystore.p12").delete()
    }

    // --- encrypt / decrypt ---

    @Test
    fun `encrypt and decrypt round-trips arbitrary bytes`() {
        val original = "hello, stytch!".toByteArray()
        val decrypted = client.decrypt(client.encrypt(original))
        assertEquals(original.toList(), decrypted.toList())
    }

    @Test
    fun `encrypted output differs from plaintext`() {
        val original = "hello, stytch!".toByteArray()
        assertNotEquals(original.toList(), client.encrypt(original).toList())
    }

    @Test
    fun `two encryptions of the same plaintext produce different ciphertext (random IV)`() {
        val original = "hello, stytch!".toByteArray()
        assertNotEquals(client.encrypt(original).toList(), client.encrypt(original).toList())
    }

    @Test
    fun `encrypt round-trips empty byte array`() {
        val original = ByteArray(0)
        assertEquals(original.toList(), client.decrypt(client.encrypt(original)).toList())
    }

    // --- generateCodeVerifier / generateCodeChallenge ---

    @Test
    fun `generateCodeVerifier returns 32 bytes`() {
        assertEquals(32, client.generateCodeVerifier().size)
    }

    @Test
    fun `generateCodeVerifier produces different values on successive calls`() {
        assertNotEquals(client.generateCodeVerifier().toList(), client.generateCodeVerifier().toList())
    }

    @Test
    fun `generateCodeChallenge returns SHA-256 of the verifier`() {
        val verifier = client.generateCodeVerifier()
        val expected = MessageDigest.getInstance("SHA-256").digest(verifier)
        assertEquals(expected.toList(), client.generateCodeChallenge(verifier).toList())
    }

    // --- Ed25519 key generation ---

    @Test
    fun `generateEd25519KeyPair returns 32-byte public and private keys`() {
        val pair = client.generateEd25519KeyPair()
        assertEquals(32, pair.publicKey.size)
        assertEquals(32, pair.privateKey.size)
    }

    @Test
    fun `generateEd25519KeyPair produces unique keys on successive calls`() {
        val pair1 = client.generateEd25519KeyPair()
        val pair2 = client.generateEd25519KeyPair()
        assertNotEquals(pair1.privateKey.toList(), pair2.privateKey.toList())
        assertNotEquals(pair1.publicKey.toList(), pair2.publicKey.toList())
    }

    @Test
    fun `deriveEd25519PublicKeyFromPrivateKeyBytes matches the generated public key`() {
        val pair = client.generateEd25519KeyPair()
        val derived = client.deriveEd25519PublicKeyFromPrivateKeyBytes(pair.privateKey)
        assertEquals(pair.publicKey.toList(), derived.toList())
    }

    // --- signEd25519 ---

    @Test
    fun `signEd25519 produces a 64-byte signature`() {
        val pair = client.generateEd25519KeyPair()
        val sig = client.signEd25519(pair.privateKey, "test data".toByteArray())
        assertEquals(64, sig.size)
    }

    @Test
    fun `signEd25519 produces different signatures for different data`() {
        val pair = client.generateEd25519KeyPair()
        val sig1 = client.signEd25519(pair.privateKey, "data one".toByteArray())
        val sig2 = client.signEd25519(pair.privateKey, "data two".toByteArray())
        assertNotEquals(sig1.toList(), sig2.toList())
    }

    // --- deleteKey ---

    @Test
    fun `deleteKey does not throw`() {
        client.deleteKey()
    }

    @Test
    fun `encrypt still works after deleteKey (creates a new key)`() {
        client.deleteKey()
        val fresh = StytchEncryptionClient("test-password")
        val original = "after delete".toByteArray()
        assertEquals(original.toList(), fresh.decrypt(fresh.encrypt(original)).toList())
        File("com.stytch.mobile.keystore.p12").delete()
    }
}
