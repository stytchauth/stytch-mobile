package com.stytch.sdk.persistence

import platform.Foundation.NSUserDefaults

public actual class StytchPlatformPersistenceClient {
    private val userDefaults: NSUserDefaults = NSUserDefaults(STYTCH_PERSISTENCE_FILE_NAME)

    public actual suspend fun save(
        key: String,
        data: String,
    ) {
        userDefaults.setObject(data, key)
    }

    public actual suspend fun get(key: String): String? = userDefaults.stringForKey(key)

    public actual suspend fun remove(key: String) {
        userDefaults.removeObjectForKey(key)
    }
}
