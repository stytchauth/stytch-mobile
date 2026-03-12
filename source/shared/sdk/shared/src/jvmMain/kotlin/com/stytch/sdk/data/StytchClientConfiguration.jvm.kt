package com.stytch.sdk.data

import com.stytch.sdk.biometrics.BiometricsProvider
import com.stytch.sdk.encryption.StytchEncryptionClient
import com.stytch.sdk.oauth.OAuthProvider
import com.stytch.sdk.passkeys.PasskeyProvider
import com.stytch.sdk.persistence.StytchPlatformPersistenceClient
import java.awt.Toolkit

public actual class StytchClientConfiguration(
    internal val publicToken: String,
    internal val applicationClass: Class<*>,
    internal val applicationVersion: String,
    internal val keystorePassword: String,
    internal val endpointOptions: EndpointOptions = EndpointOptions(),
    public val defaultSessionDuration: Int? = null,
) {
    public actual fun toInternal(): StytchClientConfigurationInternal {
        // create necessary clients
        val encryptionClient = StytchEncryptionClient(keystorePassword)
        val platformPersistenceClient = StytchPlatformPersistenceClient(applicationClass)
        // if it failed the first time (key corruption issues), nuke any previous preferences, because they are gone
        if (encryptionClient.keystoreFailedOnInitialization) {
            platformPersistenceClient.reset()
        }
        return StytchClientConfigurationInternal(
            publicToken = publicToken,
            endpointOptions = endpointOptions,
            defaultSessionDuration = defaultSessionDuration ?: DEFAULT_SESSION_DURATION_MINUTES,
            deviceInfo = getDeviceInfo(),
            platformPersistenceClient = platformPersistenceClient,
            platform = KMPPlatformType.JVM,
            encryptionClient = encryptionClient,
            passkeyProvider = PasskeyProvider(),
            biometricsProvider = BiometricsProvider(),
            oAuthProvider = OAuthProvider(),
        )
    }

    private fun getDeviceInfo(): DeviceInfo {
        val screenSize = Toolkit.getDefaultToolkit().screenSize
        return DeviceInfo(
            applicationPackageName = applicationClass.packageName,
            applicationVersion = applicationVersion,
            osName = System.getProperty("os.name"),
            osVersion = System.getProperty("os.version"),
            deviceName = "JVM",
            screenSize = "(${screenSize.width}x${screenSize.height})",
        )
    }
}
