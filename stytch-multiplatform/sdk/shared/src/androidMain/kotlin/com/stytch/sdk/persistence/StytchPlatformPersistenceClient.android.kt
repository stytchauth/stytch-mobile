package com.stytch.sdk.persistence

import android.content.Context
import android.content.SharedPreferences

public actual class StytchPlatformPersistenceClient(
    context: Context,
) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(STYTCH_PERSISTENCE_FILE_NAME, Context.MODE_PRIVATE)

    public actual suspend fun save(
        key: String,
        data: String,
    ) {
        with(sharedPreferences.edit()) {
            putString(key, data)
            apply()
        }
    }

    public actual suspend fun get(key: String): String? = sharedPreferences.getString(key, null)

    public actual suspend fun remove(key: String) {
        with(sharedPreferences.edit()) {
            remove(key)
            apply()
        }
    }
}
