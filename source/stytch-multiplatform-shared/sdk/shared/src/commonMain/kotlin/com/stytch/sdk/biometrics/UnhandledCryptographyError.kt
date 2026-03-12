package com.stytch.sdk.biometrics

import com.stytch.sdk.data.StytchError

public class UnhandledCryptographyError(
    override val cause: Throwable,
) : StytchError()
