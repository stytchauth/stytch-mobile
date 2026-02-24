package com.stytch.sdk.passkeys

public expect class PasskeyProvider {
    public val isSupported: Boolean

    public suspend fun createPublicKeyCredential(
        json: String,
        preferImmediatelyAvailableCredentials: Boolean,
    ): String

    public suspend fun getPublicKeyCredential(
        json: String,
        preferImmediatelyAvailableCredentials: Boolean,
    ): String
}
