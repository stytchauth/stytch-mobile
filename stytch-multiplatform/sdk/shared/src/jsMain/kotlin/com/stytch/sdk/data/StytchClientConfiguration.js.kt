package com.stytch.sdk.data

@JsExport
@JsName("StytchClientConfiguration")
public actual class StytchClientConfiguration(
    publicToken: String,
    public actual val endpointOptions: EndpointOptions = EndpointOptions(),
) {
    public actual val tokenInfo: PublicTokenInfo = getPublicTokenInfo(publicToken)
    internal actual val deviceInfo: DeviceInfo
        get() = TODO("Not yet implemented")
    internal actual val appSessionId: String
        get() = TODO("Not yet implemented")
    internal actual val timezone: String
        get() = TODO("Not yet implemented")
}
