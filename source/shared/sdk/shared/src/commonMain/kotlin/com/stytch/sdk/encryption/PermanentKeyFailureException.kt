package com.stytch.sdk.encryption

import com.stytch.sdk.data.StytchError

public data class PermanentKeyFailureException(
    override val cause: Throwable?,
) : StytchError()
