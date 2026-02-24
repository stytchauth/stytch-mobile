package com.stytch.sdk.oauth

import com.stytch.sdk.data.StytchError

public class UnexpectedCredentialType(
    public val credentialType: String,
) : StytchError()
