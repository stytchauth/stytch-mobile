package com.stytch.sdk.biometrics

import com.stytch.sdk.data.Ed25519KeyPair

public actual class BiometricsProvider {
    public actual suspend fun getAvailability(parameters: BiometricsParameters): BiometricsAvailability =
        BiometricsAvailability.Unavailable("Biometrics are not supported on this platform")

    public actual suspend fun register(parameters: BiometricsParameters): Ed25519KeyPair = throw BiometricsUnsupportedError()

    public actual suspend fun authenticate(parameters: BiometricsParameters): Ed25519KeyPair = throw BiometricsUnsupportedError()

    public actual suspend fun persistRegistration(
        registrationId: String,
        privateKeyData: String,
    ): Unit = throw BiometricsUnsupportedError()

    public actual suspend fun removeRegistration(): Unit = throw BiometricsUnsupportedError()
}
