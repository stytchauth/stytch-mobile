package com.stytch.sdk.migrations

import com.stytch.sdk.data.KMPPlatformType

public actual class LegacyTokenReader actual constructor() : ILegacyTokenReader {
    actual override suspend fun getExistingToken(platform: KMPPlatformType): String? {
        TODO("Not yet implemented")
    }
}
