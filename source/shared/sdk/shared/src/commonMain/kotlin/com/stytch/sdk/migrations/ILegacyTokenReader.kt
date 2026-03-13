package com.stytch.sdk.migrations

import com.stytch.sdk.data.KMPPlatformType
import com.stytch.sdk.data.StytchDispatchers
import com.stytch.sdk.data.StytchError
import com.stytch.sdk.data.Vertical
import com.stytch.sdk.persistence.StytchPlatformPersistenceClient

public interface ILegacyTokenReader {
    public suspend fun getExistingToken(
        publicToken: String,
        platformPersistenceClient: StytchPlatformPersistenceClient,
        dispatchers: StytchDispatchers,
        platform: KMPPlatformType,
        vertical: Vertical,
    ): String?
}

public expect class LegacyTokenReader : ILegacyTokenReader {
    override suspend fun getExistingToken(
        publicToken: String,
        platformPersistenceClient: StytchPlatformPersistenceClient,
        dispatchers: StytchDispatchers,
        platform: KMPPlatformType,
        vertical: Vertical,
    ): String?
}

public class InvalidPlatformForLegacyTokenMigration(
    targetPlatform: KMPPlatformType,
    actualPlatform: KMPPlatformType,
    override val message: String = "${targetPlatform.name} is not valid for migration on ${actualPlatform.name}",
) : StytchError()
