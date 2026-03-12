package com.stytch.sdk.biometrics

import com.stytch.sdk.data.StytchError

public class InvalidBiometricAvailabilityError(
    override val message: String,
) : StytchError()
