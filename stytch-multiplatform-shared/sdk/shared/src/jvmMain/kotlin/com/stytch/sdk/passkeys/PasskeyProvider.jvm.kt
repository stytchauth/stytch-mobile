package com.stytch.sdk.passkeys

public actual class PasskeyProvider {
    public actual val isSupported: Boolean = false

    public actual suspend fun createPublicKeyCredential(
        json: String,
        preferImmediatelyAvailableCredentials: Boolean,
    ): String = throw PasskeysUnsupportedError()

    public actual suspend fun getPublicKeyCredential(
        json: String,
        preferImmediatelyAvailableCredentials: Boolean,
    ): String = throw PasskeysUnsupportedError()
}
