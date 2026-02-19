package com.stytch.sdk.encryption

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import io.ktor.util.decodeBase64Bytes
import io.ktor.util.encodeBase64
import java.security.KeyStore
import java.security.MessageDigest
import java.security.SecureRandom
import java.security.spec.RSAKeyGenParameterSpec
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

public actual class StytchEncryptionClient {
    private val secretKey: SecretKey = getOrCreateSecretKey()

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
            val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
            if (keyStore.containsAlias(STYTCH_MASTER_KEY_ALIAS)) {
                keyStore.deleteEntry(STYTCH_MASTER_KEY_ALIAS)
            }
        } catch (_: Exception) {
        }
    }

    private fun getOrCreateSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
        if (keyStore.containsAlias(STYTCH_MASTER_KEY_ALIAS)) {
            return keyStore.getKey(STYTCH_MASTER_KEY_ALIAS, null) as SecretKey
        }
        val keyGenerator = KeyGenerator.getInstance(ALGORITHM, ANDROID_KEYSTORE)
        val algorithmParameterSpec = RSAKeyGenParameterSpec(KEY_SIZE, RSAKeyGenParameterSpec.F4)
        val builder =
            KeyGenParameterSpec
                .Builder(
                    STYTCH_MASTER_KEY_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
                ).setAlgorithmParameterSpec(algorithmParameterSpec)
                .setBlockModes(BLOCK_MODE)
                .setEncryptionPaddings(PADDING)
                .setKeySize(algorithmParameterSpec.keysize)
        keyGenerator.init(builder.build())
        return keyGenerator.generateKey()
    }

    private companion object {
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
        private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_GCM
        private const val PADDING = KeyProperties.ENCRYPTION_PADDING_NONE
        private const val CIPHER_TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"
        private const val KEY_SIZE = 256
        private const val GCM_TAG_LENGTH = 128
        private const val GCM_IV_LENGTH = 12
    }

    public actual fun generateCodeVerifier(): String {
        val randomBytes = ByteArray(32)
        SecureRandom().nextBytes(randomBytes)
        return randomBytes.encodeBase64()
    }

    public actual fun generateCodeChallenge(codeVerifier: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(codeVerifier.decodeBase64Bytes())
        return digest.fold("") { str, byte -> str + "%02x".format(byte) }.encodeBase64()
    }
}
