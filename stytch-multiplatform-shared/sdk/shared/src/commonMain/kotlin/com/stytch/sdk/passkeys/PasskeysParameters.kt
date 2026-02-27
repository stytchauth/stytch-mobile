package com.stytch.sdk.passkeys

public expect class PasskeysParameters {
    public val domain: String
    public val sessionDurationMinutes: Int?
    public val preferImmediatelyAvailableCredentials: Boolean
}
