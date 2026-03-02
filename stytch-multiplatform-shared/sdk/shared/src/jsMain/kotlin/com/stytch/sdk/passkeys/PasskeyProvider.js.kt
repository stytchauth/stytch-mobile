package com.stytch.sdk.passkeys

import com.stytch.sdk.StytchBridge
import com.stytch.sdk.data.StytchDispatchers
import kotlinx.coroutines.await

public actual class PasskeyProvider : IPasskeyProvider {
    public actual val isSupported: Boolean = true

    public actual suspend fun createPublicKeyCredential(
        parameters: PasskeysParameters,
        dispatchers: StytchDispatchers,
        json: String,
    ): String =
        StytchBridge
            .createPublicKeyCredential(
                domain = parameters.domain,
                preferImmediatelyAvailableCredentials = parameters.preferImmediatelyAvailableCredentials,
                json = json,
                sessionDurationMinutes = parameters.sessionDurationMinutes,
            ).await()

    public actual suspend fun getPublicKeyCredential(
        parameters: PasskeysParameters,
        dispatchers: StytchDispatchers,
        json: String,
    ): String =
        StytchBridge
            .getPublicKeyCredential(
                domain = parameters.domain,
                preferImmediatelyAvailableCredentials = parameters.preferImmediatelyAvailableCredentials,
                json = json,
                sessionDurationMinutes = parameters.sessionDurationMinutes,
            ).await()
}
