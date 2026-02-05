package com.stytch.sdk.dfp

public interface CAPTCHAProvider {
    public suspend fun getCAPTCHAToken(): String

    public val isConfigured: Boolean

    public suspend fun initialize(siteKey: String)
}
