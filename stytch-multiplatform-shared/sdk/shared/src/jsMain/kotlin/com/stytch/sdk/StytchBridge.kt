package com.stytch.sdk

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

    public fun getTelemetryId(): String

    public fun configureCaptcha(siteKey: String): Unit

    public fun getCAPTCHAToken(): String
}
