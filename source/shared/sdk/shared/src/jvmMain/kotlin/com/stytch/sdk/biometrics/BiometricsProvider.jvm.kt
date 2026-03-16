package com.stytch.sdk.biometrics

import com.stytch.sdk.data.Ed25519KeyPair

public actual class BiometricsProvider : IBiometricsProvider {
    public actual override suspend fun getAvailability(parameters: BiometricsParameters): BiometricsAvailability =
        BiometricsAvailability.Unavailable("Biometrics are not supported on this platform")

    public actual override suspend fun register(parameters: BiometricsParameters): Ed25519KeyPair = throw BiometricsUnsupportedError()

    public actual override suspend fun authenticate(parameters: BiometricsParameters): Ed25519KeyPair = throw BiometricsUnsupportedError()

    public actual override suspend fun persistRegistration(
        registrationId: String,
        privateKeyData: String,
    ): Unit = throw BiometricsUnsupportedError()

    public actual override suspend fun removeRegistration(): Unit = throw BiometricsUnsupportedError()
}
