package com.stytch.sdk.persistence

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSLibraryDirectory
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSURL
import platform.Foundation.NSURLIsExcludedFromBackupKey
import platform.Foundation.NSUserDefaults
import platform.Foundation.NSUserDomainMask

public actual class StytchPlatformPersistenceClient {
    private val userDefaults: NSUserDefaults = NSUserDefaults(STYTCH_PERSISTENCE_FILE_NAME)

    init {
        excludeFromBackup()
    }

    public actual fun saveData(
        key: String,
        data: String,
    ) {
        userDefaults.setObject(data, key)
    }

    public actual fun getData(key: String): String? = userDefaults.stringForKey(key)

    public actual fun removeData(key: String) {
        userDefaults.removeObjectForKey(key)
    }

    public actual fun reset() {
        userDefaults.dictionaryRepresentation().keys.forEach { key ->
            userDefaults.removeObjectForKey(key as String)
        }
        NSUserDefaults.standardUserDefaults.removePersistentDomainForName(STYTCH_PERSISTENCE_FILE_NAME)
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun excludeFromBackup() {
        val library = NSSearchPathForDirectoriesInDomains(NSLibraryDirectory, NSUserDomainMask, true).firstOrNull() as? String ?: return
        val prefsPath = "$library/Preferences/$STYTCH_PERSISTENCE_FILE_NAME.plist"
        val url = NSURL.fileURLWithPath(prefsPath)
        url.setResourceValue(value = true, forKey = NSURLIsExcludedFromBackupKey, error = null)
    }
}
