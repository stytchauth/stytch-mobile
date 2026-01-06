package com.stytch.sdk.data

public data class StytchNetworkError(
    override val message: String?,
    override val cause: Throwable? = null,
) : Exception(message ?: "Network error occurred.", cause)
