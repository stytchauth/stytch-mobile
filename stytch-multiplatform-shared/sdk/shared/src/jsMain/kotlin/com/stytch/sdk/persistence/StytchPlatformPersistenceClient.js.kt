package com.stytch.sdk.persistence

import com.stytch.sdk.StytchBridge
import kotlinx.coroutines.await

@JsExport
public actual class StytchPlatformPersistenceClient(
    private val bridge: StytchBridge,
) {
    public actual fun saveData(
        key: String,
        data: String,
    ) {
        bridge.saveData(key, data)
    }

    public actual fun getData(key: String): String? = bridge.getData(key)

    public actual fun removeData(key: String) {
        bridge.removeData(key)
    }
}
