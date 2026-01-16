package com.stytch.sdk.persistence

import platform.Foundation.NSUserDefaults

public actual class StytchPlatformPersistenceClient {
    private val userDefaults: NSUserDefaults = NSUserDefaults(STYTCH_PERSISTENCE_FILE_NAME)

    public actual fun saveData(
        key: String,
        data: String,
    ) {
        userDefaults.setObject(data, key)
    }

    public actual fun getData(key: String): String? = userDefaults.stringForKey(key)

    public actual fun removeData(key: String) {
        userDefaults.removeObjectForKey(key)
    }
}
