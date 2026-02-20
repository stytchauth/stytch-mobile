package com.stytch.sdk.biometrics

public expect class BiometricsProvider {
    public val isSupported: Boolean

    public fun getAvailability(): BiometricsAvailability

    public fun register(): BiometricsKeyPair

    public fun authenticate(): BiometricsKeyPair
}
