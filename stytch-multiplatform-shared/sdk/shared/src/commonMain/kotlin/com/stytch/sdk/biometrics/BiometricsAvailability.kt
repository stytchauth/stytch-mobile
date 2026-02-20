package com.stytch.sdk.biometrics

public sealed interface BiometricsAvailability {
    public data object Available : BiometricsAvailability

    public data object AlreadyRegistered : BiometricsAvailability

    public data object RegistrationRevoked : BiometricsAvailability

    public data class Unavailable(
        public val reason: String,
    ) : BiometricsAvailability
}
