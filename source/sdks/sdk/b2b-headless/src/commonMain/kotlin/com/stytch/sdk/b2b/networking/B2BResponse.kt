package com.stytch.sdk.b2b.networking

import com.stytch.sdk.b2b.networking.models.ApiB2bMfaV1MfaRequired

// This contains some response fields that are common to B2B responses, but are optional, and there is no good way to represent them
public interface B2BResponse {
    public val mfaRequired: ApiB2bMfaV1MfaRequired?
        get() = null
    public val intermediateSessionToken: String?
        get() = null
}
