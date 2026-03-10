package com.stytch.sdk.passkeys

import kotlinx.serialization.Serializable

@Serializable
public data class PasskeysAuthenticateResponse(
    public val challenge: String,
    public val userId: String? = null,
)
