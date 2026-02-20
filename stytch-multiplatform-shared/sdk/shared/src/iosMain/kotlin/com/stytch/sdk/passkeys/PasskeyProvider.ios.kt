package com.stytch.sdk.passkeys

public actual class PasskeyProvider {
    public actual val isSupported: Boolean = true

    public actual suspend fun createPublicKeyCredential(
        json: String,
        preferImmediatelyAvailableCredentials: Boolean,
    ): String {
        TODO("Not yet implemented")
    }

    public actual suspend fun getPublicKeyCredential(
        json: String,
        preferImmediatelyAvailableCredentials: Boolean,
    ): String {
        TODO("Not yet implemented")
    }
}
