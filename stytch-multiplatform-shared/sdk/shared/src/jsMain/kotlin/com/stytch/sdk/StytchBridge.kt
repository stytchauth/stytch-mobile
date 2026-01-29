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
}
