package com.stytch.sdk.passkeys

import com.stytch.sdk.data.StytchError

public class InvalidPasskeyCredentialError(
    override val message: String = "The public key credential type was not of the expected type.",
) : StytchError()
