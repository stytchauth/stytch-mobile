package com.stytch.sdk.migrations

import com.stytch.sdk.StytchBridge
import com.stytch.sdk.data.KMPPlatformType
import com.stytch.sdk.data.StytchDispatchers
import com.stytch.sdk.data.Vertical
import com.stytch.sdk.persistence.StytchPlatformPersistenceClient
import kotlinx.coroutines.await
import kotlinx.serialization.json.Json

public actual class LegacyTokenReader : ILegacyTokenReader {
    actual override suspend fun getExistingToken(
        publicToken: String,
        platformPersistenceClient: StytchPlatformPersistenceClient,
        dispatchers: StytchDispatchers,
        platform: KMPPlatformType,
        vertical: Vertical,
    ): String? = StytchBridge.getLegacyToken(vertical = Json.encodeToString(vertical)).await()
}
