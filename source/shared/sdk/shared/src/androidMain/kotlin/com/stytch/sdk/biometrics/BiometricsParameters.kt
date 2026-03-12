package com.stytch.sdk.biometrics

import androidx.fragment.app.FragmentActivity

public actual class BiometricsParameters(
    public val context: FragmentActivity,
    public val allowDeviceCredentials: Boolean = false,
    public actual val sessionDurationMinutes: Int,
    public val promptData: BiometricPromptData? = null,
    public val allowFallbackToCleartext: Boolean = false,
)
