package com.stytch.sdk.data

import android.content.Context
import android.os.Build
import kotlinx.datetime.TimeZone
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

public actual class StytchClientConfiguration(
    internal val context: Context,
    internal val publicToken: String,
    internal val endpointOptions: EndpointOptions = EndpointOptions(),
) {
    public actual fun toInternal(): StytchClientConfigurationInternal =
        StytchClientConfigurationInternal(
            publicToken = publicToken,
            endpointOptions = endpointOptions,
            deviceInfo = context.getDeviceInfo(),
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
