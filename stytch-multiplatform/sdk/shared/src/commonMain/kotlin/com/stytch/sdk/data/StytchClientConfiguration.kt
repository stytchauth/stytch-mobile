package com.stytch.sdk.data

import com.stytch.sdk.persistence.StytchPlatformPersistenceClient
import kotlinx.datetime.TimeZone
import kotlin.js.JsExport
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

public expect class StytchClientConfiguration {
    public fun toInternal(): StytchClientConfigurationInternal
}

@JsExport
public data class PublicTokenInfo(
    val publicToken: String,
    val isTestToken: Boolean,
)

private val PUBLIC_TOKEN_REGEX = Regex("^public-token-(test|live)-[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")

internal fun getPublicTokenInfo(publicToken: String): PublicTokenInfo {
    val matches = PUBLIC_TOKEN_REGEX.find(publicToken)
    require(matches != null) { "Invalid public token provided: $publicToken" }
    return PublicTokenInfo(
        publicToken = publicToken,
        isTestToken = matches.groupValues[1] == "test",
    )
}

@JsExport
public class StytchClientConfigurationInternal(
    publicToken: String,
    public val endpointOptions: EndpointOptions,
    internal val deviceInfo: DeviceInfo,
    public val tokenInfo: PublicTokenInfo = getPublicTokenInfo(publicToken),
    @OptIn(ExperimentalUuidApi::class)
    internal val appSessionId: String = Uuid.generateV4().toString(),
    internal val timezone: String = TimeZone.currentSystemDefault().id,
    public val platformPersistenceClient: StytchPlatformPersistenceClient,
)
