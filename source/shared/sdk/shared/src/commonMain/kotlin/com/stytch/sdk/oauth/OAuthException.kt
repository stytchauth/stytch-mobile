package com.stytch.sdk.oauth

import com.stytch.sdk.data.StytchError

public class OAuthException(
    public override val cause: Throwable?,
) : StytchError()
