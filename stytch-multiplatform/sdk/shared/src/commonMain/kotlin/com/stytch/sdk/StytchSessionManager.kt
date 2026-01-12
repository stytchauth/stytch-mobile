package com.stytch.sdk

public interface StytchSessionManager {
    public suspend fun getCurrentSessionToken(): String?

    public suspend fun <T> update(response: T)

    public suspend fun revoke()
}
