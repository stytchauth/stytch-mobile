package com.stytch.sdk

import com.stytch.sdk.data.DeviceInfo
import kotlin.js.Promise

public external object StytchBridge {
    public val device: DeviceInfoBridge

    public val persistence: PersistenceBridge
}

public external interface DeviceInfoBridge {
    public fun getInfo(): DeviceInfo
}

public external interface PersistenceBridge {
    public fun saveData(
        key: String,
        data: String,
    ): Promise<Unit>

    public fun getData(key: String): Promise<String?>

    public fun removeData(key: String): Promise<Unit>
}
