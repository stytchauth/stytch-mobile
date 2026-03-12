package com.stytch.sdk.passkeys

import kotlinx.serialization.Serializable

@Serializable
public data class PasskeysRegisterResponse(
    public val challenge: String,
    public val user: PasskeysUser,
)

@Serializable
public data class PasskeysUser(
    public val displayName: String,
    public val id: String,
)
