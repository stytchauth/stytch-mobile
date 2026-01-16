package com.stytch.sdk

import com.stytch.sdk.data.DeviceInfo
import kotlin.js.Promise

public external object StytchBridge {
    public fun getDeviceInfo(): DeviceInfo

    public fun saveData(
        key: String,
        data: String,
    ): Unit

    public fun getData(key: String): String?

    public fun removeData(key: String): Unit

    public fun encryptData(data: String): String

    public fun decryptData(data: String): String

    public fun deleteKey(): Unit
}
