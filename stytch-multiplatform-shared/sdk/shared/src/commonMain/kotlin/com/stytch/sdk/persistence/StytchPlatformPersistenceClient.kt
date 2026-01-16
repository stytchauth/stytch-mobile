package com.stytch.sdk.persistence

public expect class StytchPlatformPersistenceClient {
    public fun save(
        key: String,
        data: String,
    )

    public fun get(key: String): String?

    public fun remove(key: String)
}

public const val STYTCH_PERSISTENCE_FILE_NAME: String = "STYTCH_PERSISTED_DATA"
