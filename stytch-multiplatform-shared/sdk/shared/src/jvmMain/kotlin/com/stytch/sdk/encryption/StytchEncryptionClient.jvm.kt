package com.stytch.sdk.encryption

import java.security.KeyStore
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
            val keyStore = KeyStore.getInstance(JAVA_KEY_STORE).apply { load(null) }
            if (keyStore.containsAlias(STYTCH_MASTER_KEY_ALIAS)) {
                keyStore.deleteEntry(STYTCH_MASTER_KEY_ALIAS)
            }
        } catch (_: Exception) {
        }
    }

    private fun getOrCreateSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance(JAVA_KEY_STORE).apply { load(null, null) }
        if (keyStore.containsAlias(STYTCH_MASTER_KEY_ALIAS)) {
            println("JORDAN >>>> reusing key")
            return keyStore.getKey(STYTCH_MASTER_KEY_ALIAS, null) as SecretKey
        }
        println("JORDAN >>>> creating NEW key")
        val keyGenerator = KeyGenerator.getInstance(ALGORITHM)
        println("JORDAN >>>> using algorithm")
        return keyGenerator.generateKey()
    }

    private companion object {
        private const val JAVA_KEY_STORE = "PKCS12"
        private const val ALGORITHM = "AES"
        private const val BLOCK_MODE = "GCM"
        private const val PADDING = "NoPadding"
        private const val CIPHER_TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"
        private const val KEY_SIZE = 256
        private const val GCM_TAG_LENGTH = 128
        private const val GCM_IV_LENGTH = 12
    }
}
