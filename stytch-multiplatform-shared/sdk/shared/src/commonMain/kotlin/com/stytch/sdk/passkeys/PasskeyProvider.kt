package com.stytch.sdk.passkeys

import com.stytch.sdk.data.StytchDispatchers
import com.stytch.sdk.data.StytchError
import kotlin.coroutines.cancellation.CancellationException

public interface IPasskeyProvider {
    public val isSupported: Boolean

    @Throws(StytchError::class, CancellationException::class)
    public suspend fun createPublicKeyCredential(
        parameters: PasskeysParameters,
        dispatchers: StytchDispatchers,
        json: String,
    ): String

    @Throws(StytchError::class, CancellationException::class)
    public suspend fun getPublicKeyCredential(
        parameters: PasskeysParameters,
        dispatchers: StytchDispatchers,
        json: String,
    ): String
}

public expect class PasskeyProvider : IPasskeyProvider {
    public override val isSupported: Boolean

    @Throws(StytchError::class, CancellationException::class)
    public override suspend fun createPublicKeyCredential(
        parameters: PasskeysParameters,
        dispatchers: StytchDispatchers,
        json: String,
    ): String

    @Throws(StytchError::class, CancellationException::class)
    public override suspend fun getPublicKeyCredential(
        parameters: PasskeysParameters,
        dispatchers: StytchDispatchers,
        json: String,
    ): String
}
