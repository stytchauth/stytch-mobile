package com.stytch.sdk.encryption

import com.stytch.sdk.StytchBridge
import io.ktor.util.decodeBase64Bytes
import io.ktor.util.encodeBase64
import kotlinx.coroutines.await

@JsExport
public actual class StytchEncryptionClient {
    // RN doesn't allow passing ByteArray (Uint8Array in JS) across the bridge, so we need to encode/decode to string
    // This feels... fragile. I know I'm gonna get confused about bytes/strings and where at some point 🙃
    public actual fun encrypt(data: ByteArray): ByteArray {
        val result = StytchBridge.encryptData(data.encodeBase64())
        return result.decodeBase64Bytes()
    }

    public actual fun decrypt(data: ByteArray): ByteArray {
        val result = StytchBridge.decryptData(data.encodeBase64())
        return result.decodeBase64Bytes()
    }

    public actual fun deleteKey() {
        StytchBridge.deleteKey()
    }
}
