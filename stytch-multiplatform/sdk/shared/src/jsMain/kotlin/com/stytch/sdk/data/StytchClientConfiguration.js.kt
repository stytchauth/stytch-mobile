package com.stytch.sdk.data

import com.stytch.sdk.persistence.StytchPlatformPersistenceClient

@JsExport
@JsName("StytchClientConfiguration")
public actual class StytchClientConfiguration(
    internal val publicToken: String,
    internal val endpointOptions: EndpointOptions = EndpointOptions(),
    internal val defaultSessionDuration: Int? = null,
) {
    // TODO: DeviceInfo for JS
    // TODO: PersistenceClient for JS
    public actual fun toInternal(): StytchClientConfigurationInternal =
        StytchClientConfigurationInternal(
            publicToken = publicToken,
            endpointOptions = endpointOptions,
            defaultSessionDuration = defaultSessionDuration,
            deviceInfo = DeviceInfo("", "", "", "", "", ""),
            platformPersistenceClient = StytchPlatformPersistenceClient(),
            platform = KMPPlatformType.REACTNATIVE,
        )
}
