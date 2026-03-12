package com.stytch.sdk.passkeys

public actual class PasskeysParameters(
    public actual val domain: String,
    public actual val sessionDurationMinutes: Int? = null,
    public actual val preferImmediatelyAvailableCredentials: Boolean = true,
) {
    public constructor(domain: String, sessionDurationMinutes: Int) : this(domain, sessionDurationMinutes, true)
    public constructor(
        domain: String,
        preferImmediatelyAvailableCredentials: Boolean,
    ) : this(domain, null, preferImmediatelyAvailableCredentials)
    public constructor(domain: String) : this(domain, null, true)
}
