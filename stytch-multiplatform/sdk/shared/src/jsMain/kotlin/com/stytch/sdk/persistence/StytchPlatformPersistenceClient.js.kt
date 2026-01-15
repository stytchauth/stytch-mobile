package com.stytch.sdk.persistence

import com.stytch.sdk.StytchReactNativeBridge
import kotlinx.coroutines.await

@JsExport
public actual class StytchPlatformPersistenceClient(
    private val bridge: StytchReactNativeBridge,
) {
    public actual suspend fun save(
        key: String,
        data: String,
    ) {
        bridge.persistenceBridge.saveData(key, data).await()
    }

    public actual suspend fun get(key: String): String? = bridge.persistenceBridge.getData(key).await()

    public actual suspend fun remove(key: String) {
        bridge.persistenceBridge.removeData(key).await()
    }
}
