package com.stytch.sdk.biometrics

public actual class BiometricsParameters(
    public actual val sessionDurationMinutes: Int,
    public val promptData: BiometricPromptData = BiometricPromptData(),
)
