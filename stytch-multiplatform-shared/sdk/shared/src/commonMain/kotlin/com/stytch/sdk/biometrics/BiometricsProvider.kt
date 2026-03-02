package com.stytch.sdk.biometrics

import com.stytch.sdk.data.Ed25519KeyPair

public interface IBiometricsProvider {
    public suspend fun getAvailability(parameters: BiometricsParameters): BiometricsAvailability

    public suspend fun register(parameters: BiometricsParameters): Ed25519KeyPair

    public suspend fun authenticate(parameters: BiometricsParameters): Ed25519KeyPair

    public suspend fun persistRegistration(
        registrationId: String,
        privateKeyData: String,
    )

    public suspend fun removeRegistration()
}

public expect class BiometricsProvider : IBiometricsProvider {
    public suspend fun getAvailability(parameters: BiometricsParameters): BiometricsAvailability

    public suspend fun register(parameters: BiometricsParameters): Ed25519KeyPair

    public suspend fun authenticate(parameters: BiometricsParameters): Ed25519KeyPair

    public suspend fun persistRegistration(
        registrationId: String,
        privateKeyData: String,
    )

    public suspend fun removeRegistration()
}

internal const val BIOMETRIC_KEY_NAME = "stytch_biometric_key"
internal const val BIOMETRIC_REGISTRATION_ID_KEY = "BIOMETRIC_REGISTRATION_ID"
internal const val BIOMETRIC_REGISTRATION_PRIVATE_KEY_KEY = "BIOMETRIC_REGISTRATION_PRIVATE_KEY"
