package com.stytch.sdk.biometrics

public actual class BiometricsProvider {
    public actual val isSupported: Boolean
        get() {
            TODO()
        }

    public actual fun getAvailability(): BiometricsAvailability {
        TODO()
    }

    public actual fun register(): BiometricsKeyPair {
        TODO()
    }

    public actual fun authenticate(): BiometricsKeyPair {
        TODO()
    }
}
