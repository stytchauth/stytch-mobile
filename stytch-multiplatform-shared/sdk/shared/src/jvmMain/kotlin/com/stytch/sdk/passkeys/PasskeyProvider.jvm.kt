package com.stytch.sdk.passkeys

import com.stytch.sdk.data.StytchDispatchers

public actual class PasskeyProvider {
    public actual val isSupported: Boolean = false

    public actual suspend fun createPublicKeyCredential(
        parameters: PasskeysParameters,
        dispatchers: StytchDispatchers,
        json: String,
        preferImmediatelyAvailableCredentials: Boolean,
    ): String = throw PasskeysUnsupportedError()

    public actual suspend fun getPublicKeyCredential(
        parameters: PasskeysParameters,
        dispatchers: StytchDispatchers,
        json: String,
        preferImmediatelyAvailableCredentials: Boolean,
    ): String = throw PasskeysUnsupportedError()
}
