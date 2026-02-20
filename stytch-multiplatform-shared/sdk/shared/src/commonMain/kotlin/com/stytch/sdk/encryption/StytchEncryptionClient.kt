package com.stytch.sdk.encryption

import kotlin.js.JsExport

@JsExport
public expect class StytchEncryptionClient {
    public fun encrypt(data: ByteArray): ByteArray

    public fun decrypt(data: ByteArray): ByteArray

    public fun deleteKey()

    public fun generateCodeVerifier(): ByteArray

    public fun generateCodeChallenge(codeVerifier: ByteArray): ByteArray
}

internal const val STYTCH_MASTER_KEY_ALIAS = "StytchMobileMasterKey"
