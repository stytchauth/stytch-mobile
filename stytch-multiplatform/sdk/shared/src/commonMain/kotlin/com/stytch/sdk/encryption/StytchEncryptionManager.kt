package com.stytch.sdk.encryption

public expect class StytchEncryptionManager() {
    public suspend fun encrypt(data: ByteArray): ByteArray

    public suspend fun decrypt(data: ByteArray): ByteArray

    public suspend fun deleteKey()
}

internal const val STYTCH_MASTER_KEY_ALIAS = "StytchMobileMasterKey"
