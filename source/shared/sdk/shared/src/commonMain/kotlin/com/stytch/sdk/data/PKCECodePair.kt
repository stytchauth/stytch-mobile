package com.stytch.sdk.data

public data class PKCECodePair(
    public val challenge: String,
    public val verifier: String,
)
