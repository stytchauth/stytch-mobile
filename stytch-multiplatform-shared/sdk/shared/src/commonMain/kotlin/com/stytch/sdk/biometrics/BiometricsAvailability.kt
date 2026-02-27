package com.stytch.sdk.biometrics

import kotlin.js.JsExport

@JsExport
public sealed class BiometricsAvailability {
    public data object Available : BiometricsAvailability()

    public data object AlreadyRegistered : BiometricsAvailability()

    public data object RegistrationRevoked : BiometricsAvailability()

    public data class Unavailable(
        public val reason: String,
        public val code: Int? = null,
    ) : BiometricsAvailability()
}
