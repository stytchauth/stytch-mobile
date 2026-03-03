package com.stytch.sdk.passkeys

import com.stytch.sdk.data.StytchDispatchers

public actual class PasskeyProvider : IPasskeyProvider {
    public actual override val isSupported: Boolean = false

    public actual override suspend fun createPublicKeyCredential(
        parameters: PasskeysParameters,
        dispatchers: StytchDispatchers,
        json: String,
    ): String = throw PasskeysUnsupportedError()

    public actual override suspend fun getPublicKeyCredential(
        parameters: PasskeysParameters,
        dispatchers: StytchDispatchers,
        json: String,
    ): String = throw PasskeysUnsupportedError()
}
