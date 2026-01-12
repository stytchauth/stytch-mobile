package com.stytch.sdk.persistence

public actual class StytchPlatformPersistenceClient {
    public actual suspend fun save(
        key: String,
        data: String,
    ) {
        TODO("Not yet implemented")
    }

    public actual suspend fun get(key: String): String? {
        TODO("Not yet implemented")
    }

    public actual suspend fun remove(key: String) {
        TODO("Not yet implemented")
    }
}
