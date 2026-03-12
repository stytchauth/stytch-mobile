package com.stytch.sdk.biometrics

@JsExport
public actual class BiometricsParameters(
    public actual val sessionDurationMinutes: Int,
    public val androidBiometricOptions: AndroidBiometricOptions = AndroidBiometricOptions(),
    public val iosBiometricOptions: IosBiometricOptions = IosBiometricOptions(),
)
