package com.stytch.sdk.data

public actual class StytchClientConfiguration(
    public actual val publicToken: String,
    public actual val endpointOptions: EndpointOptions = EndpointOptions(),
) {
    public constructor(publicToken: String) : this(publicToken, EndpointOptions())

    public actual val isTestToken: Boolean
    internal actual val deviceInfo: DeviceInfo =
        DeviceInfo(
            applicationPackageName = "TODO()",
            applicationVersion = "TODO()",
            osName = "TODO()",
            osVersion = "TODO()",
            deviceName = "TODO()",
            screenSize = "TODO()",
        )

    init {
        val matches = PUBLIC_TOKEN_REGEX.find(publicToken)
        require(matches != null) { "Invalid public token provided: $publicToken" }
        isTestToken = matches.groupValues[1] == "test"
    }
}
