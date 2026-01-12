package com.stytch.sdk.encryption

import com.stytch.sdk.StytchEncryptionManagerSwift
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.allocArrayOf
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Foundation.NSData
import platform.Foundation.create
import platform.posix.memcpy
import kotlin.coroutines.resumeWithException

@OptIn(ExperimentalForeignApi::class)
public actual class StytchEncryptionClient {
    private val swiftEncryptionManager = StytchEncryptionManagerSwift.shared()
    private lateinit var encryptionKeyData: NSData

    init {
        runBlocking {
            encryptionKeyData = getEncryptionKeyData()
        }
    }

    private suspend fun getEncryptionKeyData(): NSData =
        suspendCancellableCoroutine { continuation ->
            if (::encryptionKeyData.isInitialized) {
                continuation.resume(encryptionKeyData) { _, _, _ -> }
                return@suspendCancellableCoroutine
            }
            swiftEncryptionManager.getEncryptionKeyWithName(STYTCH_MASTER_KEY_ALIAS) { data, error ->
                data?.let {
                    continuation.resume(data) { _, _, _ -> }
                } ?: continuation.resumeWithException(RuntimeException("Error getting encryption key data"))
            }
        }.also {
            encryptionKeyData = it
        }

    public actual suspend fun encrypt(data: ByteArray): ByteArray =
        suspendCancellableCoroutine { continuation ->
            swiftEncryptionManager.encryptDataWithPlainText(plainText = data.toNSData(), withKeyData = encryptionKeyData) { nsData, error ->
                nsData?.let {
                    continuation.resume(nsData.toByteArray()) { _, _, _ -> }
                } ?: continuation.resumeWithException(RuntimeException("Error during encryption"))
            }
        }

    public actual suspend fun decrypt(data: ByteArray): ByteArray =
        suspendCancellableCoroutine { continuation ->
            swiftEncryptionManager.decryptDataWithEncryptedData(
                encryptedData = data.toNSData(),
                withKeyData = encryptionKeyData,
            ) { nsData, error ->
                nsData?.let {
                    continuation.resume(nsData.toByteArray()) { _, _, _ -> }
                } ?: continuation.resumeWithException(RuntimeException("Error during decryption"))
            }
        }

    public actual suspend fun deleteKey() {
        TODO()
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun NSData.toByteArray(): ByteArray =
    ByteArray(this.length.toInt()).apply {
        usePinned {
            memcpy(it.addressOf(0), this@toByteArray.bytes, this@toByteArray.length)
        }
    }

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
private fun ByteArray.toNSData(): NSData =
    memScoped {
        NSData.create(
            bytes = allocArrayOf(this@toNSData),
            length = this@toNSData.size.toULong(),
        )
    }
