package com.stytch.sdk.data

public expect class StytchClientConfiguration {
    public val publicToken: String
    public val endpointOptions: EndpointOptions
    public val isTestToken: Boolean
    internal val deviceInfo: DeviceInfo
}

internal val PUBLIC_TOKEN_REGEX = Regex("^public-token-(test|live)-[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")
