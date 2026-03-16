package com.stytch.sdk.migrations

import com.stytch.sdk.StytchBridge
import com.stytch.sdk.data.KMPPlatformType
import com.stytch.sdk.data.StytchDispatchers
import com.stytch.sdk.data.Vertical
import com.stytch.sdk.persistence.StytchPlatformPersistenceClient
import kotlinx.coroutines.await
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

public actual class LegacyTokenReader : ILegacyTokenReader {
    actual override suspend fun getExistingSessionData(
        publicToken: String,
        platformPersistenceClient: StytchPlatformPersistenceClient,
        dispatchers: StytchDispatchers,
        platform: KMPPlatformType,
        vertical: Vertical,
    ): PersistedLegacySessionData? =
        StytchBridge.getLegacyToken(Json.encodeToString(vertical)).await()?.let {
            Json.decodeFromString<PersistedLegacySessionData>(it)
        }
}
