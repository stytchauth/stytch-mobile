package com.stytch.sdk.data

public class StytchSDKError(
    public override val message: String?,
    public override val cause: Throwable,
) : StytchError()
