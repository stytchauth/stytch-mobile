package com.stytch.sdk.oauth

import com.stytch.sdk.data.StytchError

public class OAuthFailedException(
    public val resultCode: Int,
    public override val message: String,
    public override val cause: Throwable? = null,
) : StytchError()
