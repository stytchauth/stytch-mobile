package com.stytch.sdk.data

import com.stytch.sdk.StytchReactNativeBridge
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
    private val reactNativeBridge: StytchReactNativeBridge = StytchReactNativeBridge()

    public actual fun toInternal(): StytchClientConfigurationInternal =
        StytchClientConfigurationInternal(
            publicToken = publicToken,
            endpointOptions = endpointOptions,
            defaultSessionDuration = defaultSessionDuration,
            deviceInfo = reactNativeBridge.deviceInfoBridge.getDeviceInfo(),
            platformPersistenceClient = StytchPlatformPersistenceClient(reactNativeBridge),
            platform = KMPPlatformType.REACTNATIVE,
        )
}
