package com.stytch.sdk.encryption

import com.stytch.sdk.data.Ed25519KeyPair
import org.bouncycastle.crypto.Signer
import org.bouncycastle.crypto.generators.Ed25519KeyPairGenerator
import org.bouncycastle.crypto.params.Ed25519KeyGenerationParameters
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters
import org.bouncycastle.crypto.signers.Ed25519Signer
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

public actual class StytchEncryptionClient(
    private val password: String,
) {
    private val secretKey: SecretKey = getOrCreateSecretKey()
    internal var keystoreFailedOnInitialization: Boolean = false

    public actual fun encrypt(data: ByteArray): ByteArray {
        val cipher =
            Cipher.getInstance(CIPHER_TRANSFORMATION).apply {
                init(Cipher.ENCRYPT_MODE, secretKey)
            }
        val iv = cipher.iv
        val ciphertext = cipher.doFinal(data)
        return iv + ciphertext
    }

    public actual fun decrypt(data: ByteArray): ByteArray {
        val iv = data.sliceArray(0 until GCM_IV_LENGTH)
        val ciphertext = data.sliceArray(GCM_IV_LENGTH until data.size)
        val spec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
        val cipher =
            Cipher.getInstance(CIPHER_TRANSFORMATION).apply {
                init(Cipher.DECRYPT_MODE, secretKey, spec)
            }
        return cipher.doFinal(ciphertext)
    }

    public actual fun deleteKey() {
        try {
            val keyStore = KeyStore.getInstance(JAVA_KEY_STORE).apply { load(null) }
            if (keyStore.containsAlias(STYTCH_MASTER_KEY_ALIAS)) {
                keyStore.deleteEntry(STYTCH_MASTER_KEY_ALIAS)
            }
        } catch (_: Exception) {
        }
    }

    private fun getOrCreateSecretKey(): SecretKey {
        val keystorePassword = password.toCharArray()
        val keyStoreFile = File(KEY_STORE_PATH)
        val keyStore = KeyStore.getInstance(JAVA_KEY_STORE)
        val keyStoreInputStream = if (keyStoreFile.exists()) FileInputStream(keyStoreFile) else null
        try {
            keyStore.load(keyStoreInputStream, keystorePassword)
            if (keyStore.containsAlias(STYTCH_MASTER_KEY_ALIAS)) {
                return keyStore.getKey(STYTCH_MASTER_KEY_ALIAS, keystorePassword) as SecretKey
            }
        } catch (_: KeyStoreException) {
            // assume key is borked and try again (only once)
            keyStore.deleteEntry(STYTCH_MASTER_KEY_ALIAS)
            if (!keystoreFailedOnInitialization) {
                keystoreFailedOnInitialization = true
                return getOrCreateSecretKey()
            }
        }
        val keyGenerator = KeyGenerator.getInstance(ALGORITHM)
        val secretKey = keyGenerator.generateKey()
        val secretKeyEntry = KeyStore.SecretKeyEntry(secretKey)
        keyStore.setEntry(STYTCH_MASTER_KEY_ALIAS, secretKeyEntry, KeyStore.PasswordProtection(keystorePassword))
        FileOutputStream(KEY_STORE_PATH).use { keyStoreOutputStream ->
            keyStore.store(keyStoreOutputStream, keystorePassword)
        }
        return secretKey
    }

    private companion object {
        private const val KEY_STORE_PATH = "com.stytch.mobile.keystore.p12"
        private const val JAVA_KEY_STORE = "PKCS12"
        private const val ALGORITHM = "AES"
        private const val BLOCK_MODE = "GCM"
        private const val PADDING = "NoPadding"
        private const val CIPHER_TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"
        private const val GCM_TAG_LENGTH = 128
        private const val GCM_IV_LENGTH = 12
    }

    public actual fun generateCodeVerifier(): ByteArray {
        val randomBytes = ByteArray(32)
        SecureRandom().nextBytes(randomBytes)
        return randomBytes
    }

    public actual fun generateCodeChallenge(codeVerifier: ByteArray): ByteArray {
        val md = MessageDigest.getInstance("SHA-256")
        return md.digest(codeVerifier)
    }

    public actual fun signEd25519(
        key: ByteArray,
        data: ByteArray,
    ): ByteArray {
        val signer: Signer = Ed25519Signer()
        val privateKey = Ed25519PrivateKeyParameters(key)
        signer.init(true, privateKey)
        signer.update(data, 0, data.size)
        return signer.generateSignature()
    }

    public actual fun generateEd25519KeyPair(): Ed25519KeyPair {
        val gen = Ed25519KeyPairGenerator()
        gen.init(Ed25519KeyGenerationParameters(SecureRandom()))
        val keyPair = gen.generateKeyPair()
        val publicKey = keyPair.public as Ed25519PublicKeyParameters
        val privateKey = keyPair.private as Ed25519PrivateKeyParameters
        return Ed25519KeyPair(
            publicKey = publicKey.encoded,
            privateKey = privateKey.encoded,
        )
    }

    public actual fun deriveEd25519PublicKeyFromPrivateKeyBytes(privateKeyBytes: ByteArray): ByteArray {
        val privateKeyRebuild = Ed25519PrivateKeyParameters(privateKeyBytes, 0)
        val publicKeyRebuild = privateKeyRebuild.generatePublicKey()
        return publicKeyRebuild.encoded
    }
}
