package com.stytch.sdk.utils

import com.stytch.sdk.biometrics.BiometricsAvailability
import com.stytch.sdk.data.Ed25519KeyPair
import com.stytch.sdk.migrations.PersistedLegacySessionData
import com.stytch.sdk.oauth.OAuthProviderType
import com.stytch.sdk.oauth.OAuthResult
import kotlinx.serialization.json.Json

public class JsonSerDeHelper {
    @Throws(Exception::class)
    public fun encodeEd25519KeyPair(data: Ed25519KeyPair): String = Json.encodeToString(Ed25519KeyPair.serializer(), data)

    @Throws(Exception::class)
    public fun encodeBiometricsAvailability(data: BiometricsAvailability): String =
        Json.encodeToString(BiometricsAvailability.serializer(), data)

    @Throws(Exception::class)
    public fun encodeOAuthResult(data: OAuthResult): String = Json.encodeToString(OAuthResult.serializer(), data)

    @Throws(Exception::class)
    public fun decodeOAuthProviderType(data: String): OAuthProviderType = Json.decodeFromString(OAuthProviderType.serializer(), data)

    @Throws(Exception::class)
    public fun encodePersistedLegacySessionData(data: PersistedLegacySessionData): String =
        Json.encodeToString(PersistedLegacySessionData.serializer(), data)
}
