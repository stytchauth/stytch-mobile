package com.stytch.sdk.biometrics

@JsExport
public class IosBiometricOptions(
    public val reason: String = "Authenticate with biometrics",
    public val fallbackTitle: String? = null,
    public val cancelTitle: String? = null,
)
