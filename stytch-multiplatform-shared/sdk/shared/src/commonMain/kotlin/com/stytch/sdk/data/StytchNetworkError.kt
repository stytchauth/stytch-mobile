package com.stytch.sdk.data

public class StytchNetworkError(
    public override val message: String?,
    public override val cause: Throwable? = null,
) : StytchError()
