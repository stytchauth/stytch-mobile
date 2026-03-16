package com.stytch.sdk.passkeys

@JsExport
public actual class PasskeysParameters(
    public actual val domain: String,
    public actual val sessionDurationMinutes: Int? = null,
    public actual val preferImmediatelyAvailableCredentials: Boolean = true,
)
