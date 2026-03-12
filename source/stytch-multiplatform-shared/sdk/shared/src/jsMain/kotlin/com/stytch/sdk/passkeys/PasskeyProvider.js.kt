package com.stytch.sdk.passkeys

import com.stytch.sdk.StytchBridge
import com.stytch.sdk.data.StytchDispatchers
import kotlinx.coroutines.await

public actual class PasskeyProvider : IPasskeyProvider {
    public actual override val isSupported: Boolean = true

    public actual override suspend fun createPublicKeyCredential(
        parameters: PasskeysParameters,
        dispatchers: StytchDispatchers,
        json: String,
    ): String =
        try {
            StytchBridge
                .createPublicKeyCredential(
                    domain = parameters.domain,
                    preferImmediatelyAvailableCredentials = parameters.preferImmediatelyAvailableCredentials,
                    json = json,
                    sessionDurationMinutes = parameters.sessionDurationMinutes,
                ).await()
        } catch (e: Throwable) {
            throw PasskeysException(e)
        }

    public actual override suspend fun getPublicKeyCredential(
        parameters: PasskeysParameters,
        dispatchers: StytchDispatchers,
        json: String,
    ): String =
        try {
            StytchBridge
                .getPublicKeyCredential(
                    domain = parameters.domain,
                    preferImmediatelyAvailableCredentials = parameters.preferImmediatelyAvailableCredentials,
                    json = json,
                    sessionDurationMinutes = parameters.sessionDurationMinutes,
                ).await()
        } catch (e: Throwable) {
            throw PasskeysException(e)
        }
}
