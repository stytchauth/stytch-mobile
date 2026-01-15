package com.stytch.sdk.persistence

import com.stytch.sdk.StytchBridge
import kotlinx.coroutines.await

@JsExport
public actual class StytchPlatformPersistenceClient(
    private val bridge: StytchBridge,
) {
    public actual suspend fun save(
        key: String,
        data: String,
    ) {
        bridge.saveData(key, data).await()
    }

    public actual suspend fun get(key: String): String? = bridge.getData(key).await()

    public actual suspend fun remove(key: String) {
        bridge.removeData(key).await()
    }
}
