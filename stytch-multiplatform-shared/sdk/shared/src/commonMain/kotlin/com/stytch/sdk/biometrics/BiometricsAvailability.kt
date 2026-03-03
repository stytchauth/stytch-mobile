package com.stytch.sdk.biometrics

import kotlin.js.JsExport

@JsExport
public sealed class BiometricsAvailability(
    public val name: String,
    public open val reason: String? = null,
    public open val code: Int? = null,
) {
    public data object Available : BiometricsAvailability(AVAILABLE)

    public data object AlreadyRegistered : BiometricsAvailability(ALREADY_REGISTERED)

    public data object RegistrationRevoked : BiometricsAvailability(REGISTRATION_REVOKED)

    public data class Unavailable(
        public override val reason: String?,
        public override val code: Int? = null,
    ) : BiometricsAvailability(UNAVAILABLE, reason, code)

    public companion object {
        private const val AVAILABLE = "Available"
        private const val ALREADY_REGISTERED = "Already Registered"
        private const val REGISTRATION_REVOKED = "Registration Revoked"
        private const val UNAVAILABLE = "Unavailable"

        public fun fromString(
            name: String,
            reason: String? = null,
            code: Int? = null,
        ): BiometricsAvailability {
            if (name == AVAILABLE) return Available
            if (name == ALREADY_REGISTERED) return AlreadyRegistered
            if (name == REGISTRATION_REVOKED) return RegistrationRevoked
            if (name == UNAVAILABLE) return Unavailable(reason, code)
            throw InvalidBiometricAvailabilityError("$name is not a valid availability status")
        }
    }
}
