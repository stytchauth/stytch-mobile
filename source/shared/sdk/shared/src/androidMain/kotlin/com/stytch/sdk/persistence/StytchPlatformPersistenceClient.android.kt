package com.stytch.sdk.persistence

import android.content.Context
import android.content.SharedPreferences

public actual class StytchPlatformPersistenceClient(
    public val context: Context,
) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(STYTCH_PERSISTENCE_FILE_NAME, Context.MODE_PRIVATE)

    public actual fun saveData(
        key: String,
        data: String,
    ) {
        with(sharedPreferences.edit()) {
            putString(key, data)
            apply()
        }
    }

    public actual fun getData(key: String): String? = sharedPreferences.getString(key, null)

    public actual fun removeData(key: String) {
        with(sharedPreferences.edit()) {
            remove(key)
            apply()
        }
    }

    public actual fun reset() {
        with(sharedPreferences.edit()) {
            clear()
            apply()
        }
    }
}
