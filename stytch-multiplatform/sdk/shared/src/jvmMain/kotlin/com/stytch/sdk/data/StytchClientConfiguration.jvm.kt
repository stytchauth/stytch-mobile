package com.stytch.sdk.data

import com.stytch.sdk.persistence.StytchPlatformPersistenceClient

public actual class StytchClientConfiguration(
    internal val publicToken: String,
    internal val endpointOptions: EndpointOptions = EndpointOptions(),
) {
    // TODO: DeviceInfo for JVM
    // TODO: PersistenceClient for JVM
    public actual fun toInternal(): StytchClientConfigurationInternal =
        StytchClientConfigurationInternal(
            publicToken = publicToken,
            endpointOptions = endpointOptions,
            deviceInfo = DeviceInfo("", "", "", "", "", ""),
            platformPersistenceClient = StytchPlatformPersistenceClient(),
        )
}
