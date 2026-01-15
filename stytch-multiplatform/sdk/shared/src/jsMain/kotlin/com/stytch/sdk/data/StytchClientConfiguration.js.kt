package com.stytch.sdk.data

import com.stytch.sdk.StytchBridge
import com.stytch.sdk.persistence.StytchPlatformPersistenceClient

@JsExport
@JsName("StytchClientConfiguration")
public actual class StytchClientConfiguration(
    internal val publicToken: String,
    internal val endpointOptions: EndpointOptions = EndpointOptions(),
    internal val defaultSessionDuration: Int? = null,
) {
    public actual fun toInternal(): StytchClientConfigurationInternal =
        StytchClientConfigurationInternal(
            publicToken = publicToken,
            endpointOptions = endpointOptions,
            defaultSessionDuration = defaultSessionDuration,
            deviceInfo = StytchBridge.device.getInfo(),
            platformPersistenceClient = StytchPlatformPersistenceClient(StytchBridge),
            platform = KMPPlatformType.REACTNATIVE,
        )
}
