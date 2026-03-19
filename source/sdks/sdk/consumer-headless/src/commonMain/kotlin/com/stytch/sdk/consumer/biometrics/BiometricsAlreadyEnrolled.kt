package com.stytch.sdk.consumer.biometrics

import com.stytch.sdk.data.StytchError

/** Thrown when attempting to register biometrics that are already enrolled for this device and user. */
public class BiometricsAlreadyEnrolled : StytchError()
