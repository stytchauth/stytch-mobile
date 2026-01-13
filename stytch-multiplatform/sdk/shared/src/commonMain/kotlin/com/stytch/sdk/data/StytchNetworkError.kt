package com.stytch.sdk.data

public class StytchNetworkError(
    override val message: String?,
    override val cause: Throwable? = null,
) : Exception(message ?: "Network error occurred.", cause)
