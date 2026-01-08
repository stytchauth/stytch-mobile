package com.stytch.sdk.data

public actual class StytchClientConfiguration(
    internal val publicToken: String,
    internal val endpointOptions: EndpointOptions = EndpointOptions(),
) {
    // TODO: DeviceInfo for JVM
    public actual fun toInternal(): StytchClientConfigurationInternal =
        StytchClientConfigurationInternal(
            publicToken = publicToken,
            endpointOptions = endpointOptions,
            deviceInfo = DeviceInfo("", "", "", "", "", ""),
        )
}
