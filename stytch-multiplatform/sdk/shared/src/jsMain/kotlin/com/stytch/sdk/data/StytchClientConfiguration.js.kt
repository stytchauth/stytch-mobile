package com.stytch.sdk.data

@JsExport
@JsName("StytchClientConfiguration")
public actual class StytchClientConfiguration(
    internal val publicToken: String,
    internal val endpointOptions: EndpointOptions = EndpointOptions(),
) {
    // TODO: DeviceInfo for JS
    public actual fun toInternal(): StytchClientConfigurationInternal =
        StytchClientConfigurationInternal(
            publicToken = publicToken,
            endpointOptions = endpointOptions,
            deviceInfo = DeviceInfo("", "", "", "", "", ""),
        )
}
