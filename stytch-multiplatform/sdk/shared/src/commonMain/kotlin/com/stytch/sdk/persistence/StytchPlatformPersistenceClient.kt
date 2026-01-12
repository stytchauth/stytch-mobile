package com.stytch.sdk.persistence

public expect class StytchPlatformPersistenceClient {
    public suspend fun save(
        key: String,
        data: String,
    )

    public suspend fun get(key: String): String?

    public suspend fun remove(key: String)
}

public const val STYTCH_PERSISTENCE_FILE_NAME: String = "STYTCH_PERSISTED_DATA"
