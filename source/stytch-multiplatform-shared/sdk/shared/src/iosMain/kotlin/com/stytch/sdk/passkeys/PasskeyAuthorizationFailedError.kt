package com.stytch.sdk.passkeys

import com.stytch.sdk.data.StytchError

public class PasskeyAuthorizationFailedError(
    public override val message: String,
) : StytchError()
