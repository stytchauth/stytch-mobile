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

    public actual fun generateCodeVerifier(): ByteArray = StytchBridge.generateCodeVerifier().decodeBase64Bytes()

    public actual fun generateCodeChallenge(codeVerifier: ByteArray): ByteArray =
        StytchBridge.generateCodeChallenge(codeVerifier.encodeBase64()).decodeBase64Bytes()

    public actual fun signEd25519(
        key: ByteArray,
        data: ByteArray,
    ): ByteArray = StytchBridge.signEd25519(key.encodeBase64(), data.encodeBase64()).decodeBase64Bytes()

    public actual fun generateEd25519KeyPair(): Ed25519KeyPair {
        val result = StytchBridge.generateEd25519KeyPair()
        return Ed25519KeyPair(
            publicKey = result[0].decodeBase64Bytes(),
            privateKey = result[1].decodeBase64Bytes(),
            encryptedPrivateKey = result[2].decodeBase64Bytes(),
        )
    }

    public actual fun deriveEd25519PublicKeyFromPrivateKeyBytes(privateKeyBytes: ByteArray): ByteArray =
        StytchBridge.deriveEd25519PublicKeyFromPrivateKeyBytes(privateKeyBytes.encodeBase64()).decodeBase64Bytes()
}
