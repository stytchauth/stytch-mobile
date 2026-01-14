package com.stytch.sdk.data

import android.content.Context
import android.os.Build
import com.stytch.sdk.persistence.StytchPlatformPersistenceClient
import kotlinx.datetime.TimeZone
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

public actual class StytchClientConfiguration(
    internal val context: Context,
    internal val publicToken: String,
    internal val endpointOptions: EndpointOptions = EndpointOptions(),
    internal val defaultSessionDuration: Int? = null,
) {
    public actual fun toInternal(): StytchClientConfigurationInternal =
        StytchClientConfigurationInternal(
            publicToken = publicToken,
            endpointOptions = endpointOptions,
            defaultSessionDuration = defaultSessionDuration,
            deviceInfo = context.getDeviceInfo(),
            platformPersistenceClient = StytchPlatformPersistenceClient(context),
            platform = KMPPlatformType.ANDROID,
        )
}

private fun Context.getDeviceInfo(): DeviceInfo {
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
