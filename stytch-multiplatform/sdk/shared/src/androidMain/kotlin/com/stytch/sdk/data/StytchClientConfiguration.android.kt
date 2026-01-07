package com.stytch.sdk.data

import android.content.Context
import android.os.Build

public actual class StytchClientConfiguration(
    internal val context: Context,
    public actual val publicToken: String,
    public actual val endpointOptions: EndpointOptions = EndpointOptions(),
) {
    public actual val isTestToken: Boolean
    internal actual val deviceInfo: DeviceInfo = context.getDeviceInfo()

    init {
        val matches = PUBLIC_TOKEN_REGEX.find(publicToken)
        require(matches != null) { "Invalid public token provided: $publicToken" }
        isTestToken = matches.groupValues[1] == "test"
    }
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
