package com.stytch.sdk.passkeys

import kotlinx.serialization.Serializable

@Serializable
public data class PasskeysStartResponse(
    public val challenge: String,
    public val displayName: String,
    public val userId: String,
)
