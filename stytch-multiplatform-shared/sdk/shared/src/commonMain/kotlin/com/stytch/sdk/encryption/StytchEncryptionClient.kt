package com.stytch.sdk.encryption

public expect class StytchEncryptionClient {
    public fun encrypt(data: ByteArray): ByteArray

    public fun decrypt(data: ByteArray): ByteArray

    public fun deleteKey()
}

internal const val STYTCH_MASTER_KEY_ALIAS = "StytchMobileMasterKey"
