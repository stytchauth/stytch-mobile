package com.stytch.sdk.encryption

import com.stytch.sdk.data.Ed25519KeyPair
import kotlin.js.JsExport

@JsExport
public expect class StytchEncryptionClient {
    public fun encrypt(data: ByteArray): ByteArray

    public fun decrypt(data: ByteArray): ByteArray

    public fun deleteKey()

    public fun generateCodeVerifier(): ByteArray

    public fun generateCodeChallenge(codeVerifier: ByteArray): ByteArray

    public fun signEd25519(
        key: ByteArray,
        data: ByteArray,
    ): ByteArray

    public fun generateEd25519KeyPair(): Ed25519KeyPair

    public fun deriveEd25519PublicKeyFromPrivateKeyBytes(privateKeyBytes: ByteArray): ByteArray
}

internal const val STYTCH_MASTER_KEY_ALIAS = "StytchMobileMasterKey"
