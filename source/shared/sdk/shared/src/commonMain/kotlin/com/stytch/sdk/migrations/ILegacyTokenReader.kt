package com.stytch.sdk.migrations

import com.stytch.sdk.data.KMPPlatformType

public interface ILegacyTokenReader {
    public suspend fun getExistingToken(platform: KMPPlatformType): String?
}

public expect class LegacyTokenReader() : ILegacyTokenReader {
    override suspend fun getExistingToken(platform: KMPPlatformType): String?
}
