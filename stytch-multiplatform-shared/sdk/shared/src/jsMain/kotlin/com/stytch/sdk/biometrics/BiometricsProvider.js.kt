package com.stytch.sdk.biometrics

import com.stytch.sdk.data.Ed25519KeyPair

public actual class BiometricsProvider {
    public actual suspend fun getAvailability(parameters: BiometricsParameters): BiometricsAvailability {
        TODO()
    }

    public actual suspend fun register(parameters: BiometricsParameters): Ed25519KeyPair {
        TODO()
    }

    public actual suspend fun authenticate(parameters: BiometricsParameters): Ed25519KeyPair {
        TODO()
    }

    public actual suspend fun persistRegistration(
        registrationId: String,
        privateKeyData: String,
    ) {
        TODO()
    }

    public actual suspend fun removeRegistration() {
        TODO()
    }
}
