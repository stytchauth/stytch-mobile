package com.stytch.sdk.biometrics

public class BiometricPromptData(
    public val reason: String = "Authenticate with biometrics",
    public val fallbackTitle: String? = null,
    public val cancelTitle: String? = null,
)
