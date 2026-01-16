package com.stytch.sdk.data

import com.stytch.sdk.persistence.StytchPlatformPersistenceClient

public actual class StytchClientConfiguration(
    internal val publicToken: String,
    internal val endpointOptions: EndpointOptions = EndpointOptions(),
    public val defaultSessionDuration: Int? = null,
) {
    // TODO: DeviceInfo for JVM
    // TODO: PersistenceClient for JVM
    public actual fun toInternal(): StytchClientConfigurationInternal =
        StytchClientConfigurationInternal(
            publicToken = publicToken,
            endpointOptions = endpointOptions,
            defaultSessionDuration = defaultSessionDuration,
            deviceInfo = DeviceInfo("", "", "", "", "", ""),
            platformPersistenceClient = StytchPlatformPersistenceClient(),
            platform = KMPPlatformType.JVM,
        )
}
