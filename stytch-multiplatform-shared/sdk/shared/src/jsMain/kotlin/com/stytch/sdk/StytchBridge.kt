package com.stytch.sdk

import com.stytch.sdk.data.Ed25519KeyPair
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
}
