package com.stytch.sdk.consumer.biometrics

import com.stytch.sdk.data.StytchError

/** Thrown when attempting biometric authentication but no registration exists for this device. */
public class NoBiometricsRegistered : StytchError()
