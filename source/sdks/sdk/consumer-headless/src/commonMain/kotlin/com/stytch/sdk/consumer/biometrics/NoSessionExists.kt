package com.stytch.sdk.consumer.biometrics

import com.stytch.sdk.data.StytchError

/** Thrown when a biometric operation requires an active session, but none is present. */
public class NoSessionExists : StytchError()
