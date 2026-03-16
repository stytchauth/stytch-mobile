package com.stytch.sdk.migrations

import com.stytch.sdk.data.KMPPlatformType
import com.stytch.sdk.data.StytchDispatchers
import com.stytch.sdk.data.Vertical
import com.stytch.sdk.persistence.StytchPlatformPersistenceClient

public actual class LegacyTokenReader : ILegacyTokenReader {
    actual override suspend fun getExistingSessionData(
        publicToken: String,
        platformPersistenceClient: StytchPlatformPersistenceClient,
        dispatchers: StytchDispatchers,
        platform: KMPPlatformType,
        vertical: Vertical,
    ): PersistedLegacySessionData? = null
}
