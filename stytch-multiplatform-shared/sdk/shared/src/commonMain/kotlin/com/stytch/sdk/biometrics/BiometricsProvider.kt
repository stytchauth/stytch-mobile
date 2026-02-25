package com.stytch.sdk.biometrics

import com.stytch.sdk.data.Ed25519KeyPair

public expect class BiometricsProvider {
    public suspend fun getAvailability(parameters: BiometricsParameters): BiometricsAvailability

    public suspend fun register(parameters: BiometricsParameters): Ed25519KeyPair

    public suspend fun authenticate(parameters: BiometricsParameters): Ed25519KeyPair

    public suspend fun persistRegistration(
        registrationId: String,
        privateKeyData: String,
    )

    public suspend fun removeRegistration()
}
