package com.stytch.sdk.biometrics

@JsExport
public class AndroidBiometricOptions(
    public val allowDeviceCredentials: Boolean = false,
    public val title: String? = null,
    public val subTitle: String? = null,
    public val negativeButtonText: String? = null,
    public val allowFallbackToCleartext: Boolean = false,
)
