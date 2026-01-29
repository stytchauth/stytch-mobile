package com.stytch.sdk.data

import com.stytch.sdk.StytchBridge
import com.stytch.sdk.encryption.StytchEncryptionClient
import com.stytch.sdk.persistence.StytchPlatformPersistenceClient
import kotlinx.serialization.json.Json

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
            deviceInfo = Json.decodeFromString<DeviceInfo>(StytchBridge.getDeviceInfo()),
            platformPersistenceClient = StytchPlatformPersistenceClient(StytchBridge),
            platform = KMPPlatformType.REACTNATIVE,
            encryptionClient = StytchEncryptionClient(),
        )
}
