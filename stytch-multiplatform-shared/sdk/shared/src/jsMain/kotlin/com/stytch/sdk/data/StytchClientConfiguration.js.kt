package com.stytch.sdk.data

import com.stytch.sdk.StytchBridge
import com.stytch.sdk.biometrics.BiometricsProvider
import com.stytch.sdk.dfp.CAPTCHAProviderImpl
import com.stytch.sdk.dfp.DFPProviderImpl
import com.stytch.sdk.encryption.StytchEncryptionClient
import com.stytch.sdk.oauth.OAuthProvider
import com.stytch.sdk.passkeys.PasskeyProvider
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
            dfpProvider = DFPProviderImpl(publicToken = publicToken, dfppaDomain = endpointOptions.dfppaDomain),
            captchaProvider = CAPTCHAProviderImpl(),
            passkeyProvider = PasskeyProvider(),
            biometricsProvider = BiometricsProvider(),
            oAuthProvider = OAuthProvider(),
        )
}
