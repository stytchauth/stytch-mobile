package com.stytch.sdk.encryption

import com.stytch.sdk.StytchEncryptionManagerSwift
import com.stytch.sdk.data.Ed25519KeyPair
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.alloc
import kotlinx.cinterop.allocArrayOf
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.usePinned
import kotlinx.cinterop.value
import platform.Foundation.NSData
import platform.Foundation.NSError
import platform.Foundation.create
import platform.posix.memcpy

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
public actual class StytchEncryptionClient {
    private val swiftEncryptionManager = StytchEncryptionManagerSwift.shared()
    private var encryptionKeyData: NSData =
        memScoped {
            val error = alloc<ObjCObjectVar<NSError?>>()
            val key = swiftEncryptionManager.getEncryptionKeyWithName(STYTCH_MASTER_KEY_ALIAS, error.ptr)
            if (error.value != null || key == null) {
                throw StytchEncryptionError("Error getting master encryption key: ${error.value?.localizedDescription ?: "empty result"}")
            }
            key
        }

    public actual fun encrypt(data: ByteArray): ByteArray =
        memScoped {
            val error = alloc<ObjCObjectVar<NSError?>>()
            val result =
                swiftEncryptionManager
                    .encryptDataWithPlainText(
                        plainText = data.toNSData(),
                        withKeyData = encryptionKeyData,
                        error = error.ptr,
                    ).toByteArray()
            if (error.value != null) {
                throw StytchEncryptionError("Error encrypting data: ${error.value?.localizedDescription}")
            }
            result
        }

    public actual fun decrypt(data: ByteArray): ByteArray =
        memScoped {
            val error = alloc<ObjCObjectVar<NSError?>>()
            val result =
                swiftEncryptionManager
                    .decryptDataWithEncryptedData(
                        encryptedData = data.toNSData(),
                        withKeyData = encryptionKeyData,
                        error = error.ptr,
                    ).toByteArray()
            if (error.value != null) {
                throw StytchEncryptionError("Error decrypting data: ${error.value?.localizedDescription}")
            }
            result
        }

    public actual fun deleteKey() {
        memScoped {
            val error = alloc<ObjCObjectVar<NSError?>>()
            swiftEncryptionManager.deleteEncryptionKeyWithName(name = STYTCH_MASTER_KEY_ALIAS, error = error.ptr)
            if (error.value != null) {
                throw StytchEncryptionError("Error deleting master key: ${error.value?.localizedDescription}")
            }
        }
    }

    public fun createBiometricKey(
        name: String,
        data: ByteArray,
    ) {
        memScoped {
            val error = alloc<ObjCObjectVar<NSError?>>()
            swiftEncryptionManager.persistBiometricKeyDataWithName(name, data.toNSData(), error.ptr)
            if (error.value != null) {
                throw StytchEncryptionError("Error creating biometric key: ${error.value?.localizedDescription}")
            }
        }
    }

    public fun retrieveBiometricKey(name: String): ByteArray =
        memScoped {
            val error = alloc<ObjCObjectVar<NSError?>>()
            val key = swiftEncryptionManager.getBiometricKeyDataWithName(name, error.ptr)
            if (error.value != null) {
                throw StytchEncryptionError("Error retrieving biometric key: ${error.value?.localizedDescription}")
            }
            return key.toByteArray()
        }

    public fun deleteBiometricKey(name: String) {
        memScoped {
            val error = alloc<ObjCObjectVar<NSError?>>()
            swiftEncryptionManager.deleteEncryptionKeyWithName(name = name, error = error.ptr)
            if (error.value != null) {
                throw StytchEncryptionError("Error deleting biometric key: ${error.value?.localizedDescription}")
            }
        }
    }

    public actual fun generateCodeVerifier(): ByteArray = swiftEncryptionManager.generateCodeVerifier().toByteArray()

    public actual fun generateCodeChallenge(codeVerifier: ByteArray): ByteArray =
        swiftEncryptionManager.generateCodeChallengeWithChallenge(codeVerifier.toNSData()).toByteArray()

    public actual fun signEd25519(
        key: ByteArray,
        data: ByteArray,
    ): ByteArray =
        memScoped {
            val error = alloc<ObjCObjectVar<NSError?>>()
            val signature =
                swiftEncryptionManager
                    .signEd25519WithKey(key = key.toNSData(), challenge = data.toNSData(), error.ptr)
                    .toByteArray()
            if (error.value != null) {
                throw Ed25519Error("Error signing challenge: ${error.value?.localizedDescription}")
            }
            return signature
        }

    public actual fun generateEd25519KeyPair(): Ed25519KeyPair {
        val keypair = mutableMapOf<String, NSData>()
        swiftEncryptionManager.generateEd25519KeyPair().forEach {
            keypair[it.key as String] = it.value as NSData
        }
        return Ed25519KeyPair(
            publicKey = keypair["publicKey"]?.toByteArray() ?: throw Ed25519Error("Missing public key"),
            privateKey = keypair["privateKey"]?.toByteArray() ?: throw Ed25519Error("Missing private key"),
        )
    }

    public actual fun deriveEd25519PublicKeyFromPrivateKeyBytes(privateKeyBytes: ByteArray): ByteArray =
        memScoped {
            val error = alloc<ObjCObjectVar<NSError?>>()
            val publicKey =
                swiftEncryptionManager
                    .deriveEd25519PublicKeyFromPrivateKeyBytesWithPrivateKeyData(
                        privateKeyBytes.toNSData(),
                        error.ptr,
                    ).toByteArray()
            if (error.value != null) {
                throw Ed25519Error("Error deriving public key: ${error.value?.localizedDescription}")
            }
            return publicKey
        }
}

@OptIn(ExperimentalForeignApi::class)
public fun NSData?.toByteArray(): ByteArray =
    this?.let { data ->
        ByteArray(data.length.toInt()).apply {
            usePinned {
                memcpy(it.addressOf(0), this@toByteArray.bytes, this@toByteArray.length)
            }
        }
    } ?: byteArrayOf()

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
public fun ByteArray.toNSData(): NSData =
    memScoped {
        NSData.create(
            bytes = allocArrayOf(this@toNSData),
            length = this@toNSData.size.toULong(),
        )
    }
