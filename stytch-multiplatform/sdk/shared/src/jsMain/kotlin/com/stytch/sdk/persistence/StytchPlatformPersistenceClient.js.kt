package com.stytch.sdk.persistence

import com.stytch.sdk.StytchBridge
import kotlinx.coroutines.await

@JsExport
public actual class StytchPlatformPersistenceClient(
    private val bridge: StytchBridge,
) {
    public actual fun save(
        key: String,
        data: String,
    ) {
        bridge.saveData(key, data)
    }

    public actual fun get(key: String): String? = bridge.getData(key)

    public actual fun remove(key: String) {
        bridge.removeData(key)
    }
}
