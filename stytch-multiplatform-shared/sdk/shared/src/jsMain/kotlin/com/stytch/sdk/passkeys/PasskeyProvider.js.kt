package com.stytch.sdk.passkeys

import com.stytch.sdk.data.StytchDispatchers

public actual class PasskeyProvider {
    public actual val isSupported: Boolean = true

    public actual suspend fun createPublicKeyCredential(
        parameters: PasskeysParameters,
        dispatchers: StytchDispatchers,
        json: String,
    ): String {
        TODO("Not yet implemented")
    }

    public actual suspend fun getPublicKeyCredential(
        parameters: PasskeysParameters,
        dispatchers: StytchDispatchers,
        json: String,
    ): String {
        TODO("Not yet implemented")
    }
}
