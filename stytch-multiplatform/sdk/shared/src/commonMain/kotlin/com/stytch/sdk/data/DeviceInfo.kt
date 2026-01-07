package com.stytch.sdk.data

import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
@JsName("DeviceInfo")
public data class DeviceInfo(
    val applicationPackageName: String,
    val applicationVersion: String,
    val osName: String,
    val osVersion: String,
    val deviceName: String,
    val screenSize: String,
)
