package com.stytch.sdk.encryption

import com.stytch.sdk.data.StytchError

public class StytchEncryptionError(
    public override val message: String,
) : StytchError()
