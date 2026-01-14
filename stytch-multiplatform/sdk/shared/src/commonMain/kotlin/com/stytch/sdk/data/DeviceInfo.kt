package com.stytch.sdk.data

import kotlin.js.JsExport
import kotlin.js.JsName

public class DeviceInfo(
    public val applicationPackageName: String,
    public val applicationVersion: String,
    public val osName: String,
    public val osVersion: String,
    public val deviceName: String,
    public val screenSize: String,
)
