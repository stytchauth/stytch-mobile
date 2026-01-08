package com.stytch.sdk.data

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.Foundation.NSBundle
import platform.Foundation.NSProcessInfo
import platform.UIKit.UIDevice
import platform.UIKit.UIScreen

public actual class StytchClientConfiguration(
    public actual val publicToken: String,
    public actual val endpointOptions: EndpointOptions = EndpointOptions(),
) {
    public constructor(publicToken: String) : this(publicToken, EndpointOptions())

    public actual val isTestToken: Boolean

    @OptIn(ExperimentalForeignApi::class)
    internal actual val deviceInfo: DeviceInfo =
        DeviceInfo(
            applicationPackageName = NSBundle.mainBundle.bundleIdentifier ?: "unknown_bundle_id",
            applicationVersion = NSBundle.mainBundle.getVersion(),
            osName = UIDevice.currentDevice.systemName,
            osVersion = NSProcessInfo.processInfo.operatingSystemVersionString,
            deviceName = UIDevice.currentDevice.model.lowercase(),
            screenSize = UIScreen.mainScreen.bounds.useContents { "(${size.width},${size.height})" },
        )

    init {
        val matches = PUBLIC_TOKEN_REGEX.find(publicToken)
        require(matches != null) { "Invalid public token provided: $publicToken" }
        isTestToken = matches.groupValues[1] == "test"
    }
}

private fun NSBundle.getVersion(): String =
    infoDictionary
        ?.let {
            // First, try to get the user-visible version, fallback to the internal identifier
            it["CFBundleShortVersionString"] ?: it["CFBundleVersion"]
        }.toString()
