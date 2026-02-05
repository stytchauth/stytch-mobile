package com.stytch.sdk.data

import android.app.Application
import android.content.Context
import android.os.Build
import com.stytch.sdk.dfp.CAPTCHAProviderImpl
import com.stytch.sdk.dfp.DFPProviderImpl
import com.stytch.sdk.encryption.StytchEncryptionClient
import com.stytch.sdk.persistence.StytchPlatformPersistenceClient

public actual class StytchClientConfiguration(
    internal val context: Context,
    internal val publicToken: String,
    internal val endpointOptions: EndpointOptions = EndpointOptions(),
    internal val defaultSessionDuration: Int? = null,
) {
    public actual fun toInternal(): StytchClientConfigurationInternal {
        // DFP initialization is a two-step process SOLELY because it has to be on React Native :upsidedownface:
        val dfpProvider = DFPProviderImpl(context)
        dfpProvider.configureDfp(publicToken = publicToken, dfppaDomain = endpointOptions.dfppaDomain)
        return StytchClientConfigurationInternal(
            publicToken = publicToken,
            endpointOptions = endpointOptions,
            defaultSessionDuration = defaultSessionDuration,
            deviceInfo = context.getDeviceInfo(),
            platformPersistenceClient = StytchPlatformPersistenceClient(context),
            platform = KMPPlatformType.ANDROID,
            encryptionClient = StytchEncryptionClient(),
            dfpProvider = dfpProvider,
            captchaProvider = CAPTCHAProviderImpl(context.applicationContext as Application),
        )
    }
}

public fun Context.getDeviceInfo(): DeviceInfo {
    val applicationPackageName = applicationContext.packageName.toString()
    val applicationVersion =
        try {
            applicationContext
                .packageManager
                .getPackageInfo(applicationPackageName, 0)
                .versionName
                .toString()
        } catch (_: Exception) {
            ""
        }
    val width = resources.displayMetrics.widthPixels
    val height = resources.displayMetrics.heightPixels
    return DeviceInfo(
        applicationPackageName = applicationPackageName,
        applicationVersion = applicationVersion,
        osName = "Android",
        osVersion = Build.VERSION.SDK_INT.toString(),
        deviceName = Build.MODEL,
        screenSize = "($width,$height)",
    )
}
