package com.stytch.sdk.encryption

import com.stytch.sdk.StytchBridge
import com.stytch.sdk.data.Ed25519KeyPair
import io.ktor.util.decodeBase64Bytes
import io.ktor.util.encodeBase64

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

    public actual fun generateCodeVerifier(): ByteArray {
        TODO("Not yet implemented")
    }

    public actual fun generateCodeChallenge(codeVerifier: ByteArray): ByteArray {
        TODO("Not yet implemented")
    }

    public actual fun signEd25519(
        key: ByteArray,
        data: ByteArray,
    ): ByteArray {
        TODO()
    }

    public actual fun generateEd25519KeyPair(): Ed25519KeyPair {
        TODO()
    }

    public actual fun deriveEd25519PublicKeyFromPrivateKeyBytes(privateKeyBytes: ByteArray): ByteArray {
        TODO()
    }
}
