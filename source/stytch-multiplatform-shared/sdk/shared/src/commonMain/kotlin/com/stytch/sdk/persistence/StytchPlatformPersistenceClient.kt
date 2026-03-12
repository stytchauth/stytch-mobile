package com.stytch.sdk.persistence

public expect class StytchPlatformPersistenceClient {
    public fun saveData(
        key: String,
        data: String,
    )

    public fun getData(key: String): String?

    public fun removeData(key: String)

    public fun reset()
}

public const val STYTCH_PERSISTENCE_FILE_NAME: String = "STYTCH_PERSISTED_DATA"
