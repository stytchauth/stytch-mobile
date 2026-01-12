package com.stytch.sdk.persistence

public expect class StytchPlatformPersistenceClient {
    public suspend fun save(
        key: String,
        data: String,
    ): Boolean

    public suspend fun get(key: String): String?

    public suspend fun remove(key: String): Boolean
}
