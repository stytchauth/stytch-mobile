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
    publicToken: String,
    public actual val endpointOptions: EndpointOptions = EndpointOptions(),
) {
    public constructor(publicToken: String) : this(publicToken, EndpointOptions())

    public actual val tokenInfo: PublicTokenInfo = getPublicTokenInfo(publicToken)

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

    @OptIn(ExperimentalUuidApi::class)
    internal actual val appSessionId: String = Uuid.generateV4().toString()
    internal actual val timezone: String = TimeZone.currentSystemDefault().id
}

private fun NSBundle.getVersion(): String =
    infoDictionary
        ?.let {
            // First, try to get the user-visible version, fallback to the internal identifier
            it["CFBundleShortVersionString"] ?: it["CFBundleVersion"]
        }.toString()
