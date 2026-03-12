package com.stytch.sdk.biometrics

import com.stytch.sdk.data.StytchError

public class BiometricAuthenticationFailed(
    override val message: String,
) : StytchError()
