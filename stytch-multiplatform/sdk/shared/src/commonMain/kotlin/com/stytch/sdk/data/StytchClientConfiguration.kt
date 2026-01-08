package com.stytch.sdk.data

import kotlin.js.JsExport

public expect class StytchClientConfiguration {
    public val endpointOptions: EndpointOptions
    public val tokenInfo: PublicTokenInfo
    internal val deviceInfo: DeviceInfo
    internal val appSessionId: String
    internal val timezone: String
}

@JsExport
public data class PublicTokenInfo(
    val publicToken: String,
    val isTestToken: Boolean,
)

internal val PUBLIC_TOKEN_REGEX = Regex("^public-token-(test|live)-[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")

internal fun getPublicTokenInfo(publicToken: String): PublicTokenInfo {
    val matches = PUBLIC_TOKEN_REGEX.find(publicToken)
    require(matches != null) { "Invalid public token provided: $publicToken" }
    return PublicTokenInfo(
        publicToken = publicToken,
        isTestToken = matches.groupValues[1] == "test",
    )
}
