package com.stytch.sdk

import kotlin.js.Promise

public external object StytchBridge {
    public fun getDeviceInfo(): String

    public fun saveData(
        key: String,
        data: String,
    ): Unit

    public fun getData(key: String): String?

    public fun removeData(key: String): Unit

    public fun encryptData(data: String): String

    public fun decryptData(data: String): String

    public fun deleteKey(): Unit

    public fun resetPreferences(): Unit

    public fun configureDfp(
        publicToken: String,
        dfppaDomain: String,
    ): Unit

    public fun getTelemetryId(): Promise<String>

    public fun configureCaptcha(siteKey: String): Unit

    public fun getCAPTCHAToken(): Promise<String>

    public fun isCaptchaConfigured(): Boolean

    public fun generateCodeVerifier(): String

    public fun generateCodeChallenge(verifier: String): String

    public fun signEd25519(
        key: String,
        data: String,
    ): String

    public fun generateEd25519KeyPair(): List<String>

    public fun deriveEd25519PublicKeyFromPrivateKeyBytes(privateKeyBytes: String): String

    public fun getBiometricsAvailability(
        sessionDurationMinutes: Int,
        androidAllowDeviceCredentials: Boolean?,
        androidTitle: String?,
        androidSubTitle: String?,
        androidNegativeButtonText: String?,
        androidAllowFallbackToCleartext: Boolean?,
        iosReason: String?,
        iosFallbackTitle: String?,
        iosCancelTitle: String?,
    ): Promise<String>

    public fun registerBiometrics(
        sessionDurationMinutes: Int,
        androidAllowDeviceCredentials: Boolean?,
        androidTitle: String?,
        androidSubTitle: String?,
        androidNegativeButtonText: String?,
        androidAllowFallbackToCleartext: Boolean?,
        iosReason: String?,
        iosFallbackTitle: String?,
        iosCancelTitle: String?,
    ): Promise<String>

    public fun authenticateBiometrics(
        sessionDurationMinutes: Int,
        androidAllowDeviceCredentials: Boolean?,
        androidTitle: String?,
        androidSubTitle: String?,
        androidNegativeButtonText: String?,
        androidAllowFallbackToCleartext: Boolean?,
        iosReason: String?,
        iosFallbackTitle: String?,
        iosCancelTitle: String?,
    ): Promise<String>

    public fun persistBiometricRegistration(
        registrationId: String,
        privateKeyData: String,
    ): Promise<Unit>

    public fun removeBiometricRegistration(): Promise<Unit>

    public fun createPublicKeyCredential(
        domain: String,
        preferImmediatelyAvailableCredentials: Boolean,
        json: String,
        sessionDurationMinutes: Int?,
    ): Promise<String>

    public fun getPublicKeyCredential(
        domain: String,
        preferImmediatelyAvailableCredentials: Boolean,
        json: String,
        sessionDurationMinutes: Int?,
    ): Promise<String>

    public fun getOAuthToken(
        type: String,
        baseUrl: String,
        publicToken: String,
        loginRedirectUrl: String?,
        signupRedirectUrl: String?,
        customScopes: List<String>?,
        providerParams: String?,
        oauthAttachToken: String?,
        sessionDurationMinutes: Int?,
        googleCredentialConfiguration: String?,
    ): Promise<String>

    public fun startBrowserFlow(url: String): Promise<String>

    public fun getLegacyToken(vertical: String): Promise<String?>
}
