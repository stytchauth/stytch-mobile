package com.stytch.sdk.passkeys

import com.stytch.sdk.data.StytchDispatchers

public interface IPasskeyProvider {
    public val isSupported: Boolean

    public suspend fun createPublicKeyCredential(
        parameters: PasskeysParameters,
        dispatchers: StytchDispatchers,
        json: String,
    ): String

    public suspend fun getPublicKeyCredential(
        parameters: PasskeysParameters,
        dispatchers: StytchDispatchers,
        json: String,
    ): String
}

public expect class PasskeyProvider : IPasskeyProvider {
    public val isSupported: Boolean

    public suspend fun createPublicKeyCredential(
        parameters: PasskeysParameters,
        dispatchers: StytchDispatchers,
        json: String,
    ): String

    public suspend fun getPublicKeyCredential(
        parameters: PasskeysParameters,
        dispatchers: StytchDispatchers,
        json: String,
    ): String
}
