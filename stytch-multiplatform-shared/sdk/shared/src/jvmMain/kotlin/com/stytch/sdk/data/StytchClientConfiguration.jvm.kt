package com.stytch.sdk.data

import com.stytch.sdk.persistence.StytchPlatformPersistenceClient
import java.awt.Toolkit

public actual class StytchClientConfiguration(
    internal val publicToken: String,
    internal val applicationClass: Class<*>,
    internal val applicationVersion: String,
    internal val endpointOptions: EndpointOptions = EndpointOptions(),
    public val defaultSessionDuration: Int? = null,
) {
    public actual fun toInternal(): StytchClientConfigurationInternal =
        StytchClientConfigurationInternal(
            publicToken = publicToken,
            endpointOptions = endpointOptions,
            defaultSessionDuration = defaultSessionDuration,
            deviceInfo = getDeviceInfo(),
            platformPersistenceClient = StytchPlatformPersistenceClient(applicationClass),
            platform = KMPPlatformType.JVM,
        )

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
