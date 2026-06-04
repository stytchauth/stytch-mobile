package com.stytch.sdk.biometrics

public interface IBiometricsProvider {
    public suspend fun getAvailability(parameters: BiometricsParameters): BiometricsAvailability

    public suspend fun createBiometricKey(parameters: BiometricsParameters): String

    public suspend fun retrieveBiometricKey(parameters: BiometricsParameters): String

    public suspend fun signWithBiometricKey(challenge: String): String

    public suspend fun persistRegistration(registrationId: String)

    public suspend fun removeRegistration()
}

public expect class BiometricsProvider : IBiometricsProvider {
    public override suspend fun getAvailability(parameters: BiometricsParameters): BiometricsAvailability

    public override suspend fun createBiometricKey(parameters: BiometricsParameters): String

    public override suspend fun retrieveBiometricKey(parameters: BiometricsParameters): String

    public override suspend fun signWithBiometricKey(challenge: String): String

    public override suspend fun persistRegistration(registrationId: String)

    public override suspend fun removeRegistration()
}

internal const val BIOMETRIC_KEY_NAME = "stytch_biometric_key"
internal const val BIOMETRIC_REGISTRATION_ID_KEY = "BIOMETRIC_REGISTRATION_ID"
internal const val BIOMETRIC_REGISTRATION_PRIVATE_KEY_KEY = "BIOMETRIC_REGISTRATION_PRIVATE_KEY"
