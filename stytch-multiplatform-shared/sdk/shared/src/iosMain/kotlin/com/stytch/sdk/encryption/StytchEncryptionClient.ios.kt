package com.stytch.sdk.encryption

import com.stytch.sdk.StytchEncryptionManagerSwift
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.allocArrayOf
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.create
import platform.posix.memcpy

@OptIn(ExperimentalForeignApi::class)
public actual class StytchEncryptionClient {
    private val swiftEncryptionManager = StytchEncryptionManagerSwift.shared()
    private var encryptionKeyData: NSData = swiftEncryptionManager.getEncryptionKeyWithName(STYTCH_MASTER_KEY_ALIAS)

    public actual fun encrypt(data: ByteArray): ByteArray =
        swiftEncryptionManager.encryptDataWithPlainText(plainText = data.toNSData(), withKeyData = encryptionKeyData).toByteArray()

    public actual fun decrypt(data: ByteArray): ByteArray =
        swiftEncryptionManager
            .decryptDataWithEncryptedData(
                encryptedData = data.toNSData(),
                withKeyData = encryptionKeyData,
            ).toByteArray()

    public actual fun deleteKey() {
        swiftEncryptionManager.deleteEncryptionKeyWithName(STYTCH_MASTER_KEY_ALIAS)
    }

    public actual fun generateCodeVerifier(): ByteArray {
        TODO("Not yet implemented")
    }

    public actual fun generateCodeChallenge(codeVerifier: ByteArray): ByteArray {
        TODO("Not yet implemented")
    }
}

@OptIn(ExperimentalForeignApi::class)
public fun NSData?.toByteArray(): ByteArray =
    this?.let {
        ByteArray(it.length.toInt()).apply {
            usePinned {
                memcpy(it.addressOf(0), this@toByteArray.bytes, this@toByteArray.length)
            }
        }
    } ?: run {
        // TODO: Log errors encrypting/decrypting data
        byteArrayOf()
    }

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
public fun ByteArray.toNSData(): NSData =
    memScoped {
        NSData.create(
            bytes = allocArrayOf(this@toNSData),
            length = this@toNSData.size.toULong(),
        )
    }
