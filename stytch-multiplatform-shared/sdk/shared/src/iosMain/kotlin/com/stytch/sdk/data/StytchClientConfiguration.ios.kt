package com.stytch.sdk.data

import com.stytch.sdk.biometrics.BiometricsProvider
import com.stytch.sdk.dfp.CAPTCHAProviderImpl
import com.stytch.sdk.dfp.DFPProviderImpl
import com.stytch.sdk.encryption.StytchEncryptionClient
import com.stytch.sdk.oauth.OAuthProvider
import com.stytch.sdk.passkeys.PasskeyProvider
import com.stytch.sdk.persistence.StytchPlatformPersistenceClient
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.datetime.TimeZone
import platform.Foundation.NSBundle
import platform.Foundation.NSProcessInfo
import platform.UIKit.UIDevice
import platform.UIKit.UIScreen
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

public actual class StytchClientConfiguration(
    internal val publicToken: String,
    internal val endpointOptions: EndpointOptions = EndpointOptions(),
    internal val defaultSessionDuration: Int? = null,
) {
    // Manual secondary constructor for iOS DX improvement
    public constructor(publicToken: String) : this(publicToken, EndpointOptions())

    @OptIn(ExperimentalForeignApi::class)
    public actual fun toInternal(): StytchClientConfigurationInternal {
        val encryptionClient = StytchEncryptionClient()
        val platformPersistenceClient = StytchPlatformPersistenceClient()
        return StytchClientConfigurationInternal(
            publicToken = publicToken,
            endpointOptions = endpointOptions,
            defaultSessionDuration = defaultSessionDuration ?: DEFAULT_SESSION_DURATION_MINUTES,
            deviceInfo =
                DeviceInfo(
                    applicationPackageName = NSBundle.mainBundle.bundleIdentifier ?: "unknown_bundle_id",
                    applicationVersion = NSBundle.mainBundle.getVersion(),
                    osName = UIDevice.currentDevice.systemName,
                    osVersion = NSProcessInfo.processInfo.operatingSystemVersionString,
                    deviceName = UIDevice.currentDevice.model.lowercase(),
                    screenSize = UIScreen.mainScreen.bounds.useContents { "(${size.width},${size.height})" },
                ),
            platformPersistenceClient = platformPersistenceClient,
            platform = KMPPlatformType.IOS,
            encryptionClient = encryptionClient,
            dfpProvider = DFPProviderImpl(publicToken = publicToken, dfppaDomain = endpointOptions.dfppaDomain),
            captchaProvider = CAPTCHAProviderImpl(),
            passkeyProvider = PasskeyProvider(),
            biometricsProvider = BiometricsProvider(encryptionClient, platformPersistenceClient),
            oAuthProvider = OAuthProvider(),
        )
    }
}

private fun NSBundle.getVersion(): String =
    infoDictionary
        ?.let {
            // First, try to get the user-visible version, fallback to the internal identifier
            it["CFBundleShortVersionString"] ?: it["CFBundleVersion"]
        }.toString()
