package com.stytch.sdk.data

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
) {
    // Manual secondary constructor for iOS DX improvement
    public constructor(publicToken: String) : this(publicToken, EndpointOptions())

    @OptIn(ExperimentalForeignApi::class)
    public actual fun toInternal(): StytchClientConfigurationInternal =
        StytchClientConfigurationInternal(
            publicToken = publicToken,
            endpointOptions = endpointOptions,
            deviceInfo =
                DeviceInfo(
                    applicationPackageName = NSBundle.mainBundle.bundleIdentifier ?: "unknown_bundle_id",
                    applicationVersion = NSBundle.mainBundle.getVersion(),
                    osName = UIDevice.currentDevice.systemName,
                    osVersion = NSProcessInfo.processInfo.operatingSystemVersionString,
                    deviceName = UIDevice.currentDevice.model.lowercase(),
                    screenSize = UIScreen.mainScreen.bounds.useContents { "(${size.width},${size.height})" },
                ),
        )
}

private fun NSBundle.getVersion(): String =
    infoDictionary
        ?.let {
            // First, try to get the user-visible version, fallback to the internal identifier
            it["CFBundleShortVersionString"] ?: it["CFBundleVersion"]
        }.toString()
