package com.stytch.sdk

import com.stytch.sdk.data.DeviceInfo
import kotlin.js.Promise

public external object StytchBridge {
    public fun getDeviceInfo(): DeviceInfo

    public fun saveData(
        key: String,
        data: String,
    ): Promise<Unit>

    public fun getData(key: String): Promise<String?>

    public fun removeData(key: String): Promise<Unit>

    public fun encryptData(data: String): Promise<String>

    public fun decryptData(data: String): Promise<String>

    public fun deleteKey(): Promise<Unit>
}
