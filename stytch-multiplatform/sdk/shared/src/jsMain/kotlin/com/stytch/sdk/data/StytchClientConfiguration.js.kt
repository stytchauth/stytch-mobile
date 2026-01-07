package com.stytch.sdk.data

@JsExport
@JsName("StytchClientConfiguration")
public actual class StytchClientConfiguration(
    public actual val publicToken: String,
    public actual val endpointOptions: EndpointOptions = EndpointOptions(),
) {
    public actual val isTestToken: Boolean
    internal actual val deviceInfo: DeviceInfo
        get() = TODO("Not yet implemented")

    init {
        val matches = PUBLIC_TOKEN_REGEX.find(publicToken)
        require(matches != null) { "Invalid public token provided: $publicToken" }
        isTestToken = matches.groupValues[1] == "test"
    }
}
