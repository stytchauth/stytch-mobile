package com.stytch.sdk.persistence

import java.util.prefs.Preferences

public actual class StytchPlatformPersistenceClient(
    applicationClass: Class<*>,
    persistenceFileName: String,
) {
    private val preferences = Preferences.userNodeForPackage(applicationClass).node(persistenceFileName)

    public actual fun reset() {
        preferences.clear()
    }

    public actual fun saveData(
        key: String,
        data: String,
    ) {
        preferences.put(key, data)
    }

    public actual fun getData(key: String): String? = preferences.get(key, null)

    public actual fun removeData(key: String) {
        preferences.remove(key)
    }
}
