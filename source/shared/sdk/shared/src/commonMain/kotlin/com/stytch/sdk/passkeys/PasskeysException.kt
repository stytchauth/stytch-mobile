package com.stytch.sdk.passkeys

import com.stytch.sdk.data.StytchError

public class PasskeysException(
    override val cause: Throwable,
) : StytchError()
