package com.stytch.sdk.migrations

import com.stytch.sdk.StytchBridge
import com.stytch.sdk.data.KMPPlatformType
import kotlinx.coroutines.await

public actual class LegacyTokenReader actual constructor() : ILegacyTokenReader {
    actual override suspend fun getExistingToken(platform: KMPPlatformType): String? = StytchBridge.getLegacyToken().await()
}
