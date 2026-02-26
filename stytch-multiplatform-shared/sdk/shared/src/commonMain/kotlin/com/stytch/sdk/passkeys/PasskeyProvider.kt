package com.stytch.sdk.passkeys

import com.stytch.sdk.data.StytchDispatchers

public expect class PasskeyProvider {
    public val isSupported: Boolean

    public suspend fun createPublicKeyCredential(
        parameters: PasskeysParameters,
        dispatchers: StytchDispatchers,
        json: String,
        preferImmediatelyAvailableCredentials: Boolean,
    ): String

    public suspend fun getPublicKeyCredential(
        parameters: PasskeysParameters,
        dispatchers: StytchDispatchers,
        json: String,
        preferImmediatelyAvailableCredentials: Boolean,
    ): String
}
