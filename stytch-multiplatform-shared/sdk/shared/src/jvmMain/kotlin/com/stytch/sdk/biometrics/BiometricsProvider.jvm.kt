package com.stytch.sdk.biometrics

public actual class BiometricsProvider {
    public actual val isSupported: Boolean = false

    public actual fun getAvailability(): BiometricsAvailability =
        BiometricsAvailability.Unavailable("Biometrics are unsupported on this platform")

    public actual fun register(): BiometricsKeyPair = throw BiometricsUnsupportedError()

    public actual fun authenticate(): BiometricsKeyPair = throw BiometricsUnsupportedError()
}
