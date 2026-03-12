package com.stytch.sdk.biometrics

import kotlinx.serialization.Serializable

@Serializable
public sealed class BiometricsAvailability {
    @Serializable public data object Available : BiometricsAvailability()

    @Serializable public data object AlreadyRegistered : BiometricsAvailability()

    @Serializable public data object RegistrationRevoked : BiometricsAvailability()

    @Serializable public data class Unavailable(
        public val reason: String?,
        public val code: Int? = null,
    ) : BiometricsAvailability()
}
