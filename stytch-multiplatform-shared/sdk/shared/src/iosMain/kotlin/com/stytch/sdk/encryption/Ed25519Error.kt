package com.stytch.sdk.encryption

import com.stytch.sdk.data.StytchError

public class Ed25519Error(
    public override val message: String,
) : StytchError()
