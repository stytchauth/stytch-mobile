package com.stytch.sdk

import com.stytch.sdk.data.DeviceInfo
import kotlin.js.Promise

public external class StytchReactNativeBridge {
    public val deviceInfoBridge: DeviceInfoBridge

    public val persistenceBridge: PersistenceBridge
}

public external interface DeviceInfoBridge {
    public fun getDeviceInfo(): DeviceInfo
}

public external interface PersistenceBridge {
    public fun saveData(
        key: String,
        data: String,
    ): Promise<Unit>

    public fun getData(key: String): Promise<String?>

    public fun removeData(key: String): Promise<Unit>
}
