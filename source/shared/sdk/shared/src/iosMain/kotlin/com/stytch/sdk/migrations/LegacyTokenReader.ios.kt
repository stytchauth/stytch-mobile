package com.stytch.sdk.migrations

import com.stytch.sdk.data.KMPPlatformType
import com.stytch.sdk.data.StytchDispatchers
import com.stytch.sdk.data.Vertical
import com.stytch.sdk.persistence.StytchPlatformPersistenceClient

public actual class LegacyTokenReader : ILegacyTokenReader {
    actual override suspend fun getExistingToken(
        publicToken: String,
        platformPersistenceClient: StytchPlatformPersistenceClient,
        dispatchers: StytchDispatchers,
        platform: KMPPlatformType,
        vertical: Vertical,
    ): String? =
        when (platform) {
            KMPPlatformType.IOS -> getDecryptedSessionTokenFromLegacyIosSDK(vertical)
            KMPPlatformType.REACTNATIVE -> getDecryptedSessionTokenFromLegacyReactNativeSDK(vertical)
            else -> throw InvalidPlatformForLegacyTokenMigration(targetPlatform = platform, actualPlatform = KMPPlatformType.IOS)
        }

    private suspend fun getDecryptedSessionTokenFromLegacyIosSDK(vertical: Vertical): String? {
        TODO()
    }

    private suspend fun getDecryptedSessionTokenFromLegacyReactNativeSDK(vertical: Vertical): String? {
        TODO()
    }
}
