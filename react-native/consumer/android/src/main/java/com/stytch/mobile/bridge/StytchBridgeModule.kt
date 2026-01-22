package com.stytch.mobile.bridge

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.module.annotations.ReactModule
import com.stytch.sdk.data.getDeviceInfo
import com.stytch.sdk.encryption.StytchEncryptionClient
import com.stytch.sdk.persistence.StytchPersistenceClient
import com.stytch.sdk.persistence.StytchPlatformPersistenceClient
import io.ktor.util.decodeBase64Bytes
import io.ktor.util.encodeBase64
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import okio.ByteString.Companion.decodeBase64
import kotlinx.serialization.json.Json

/**
 * This bridge module is just an RN-accessible, well, bridge, to the existing code in the "real" Stytch SDK,
 * which is a dependency of this React Native SDK.
 */
@ReactModule(name = StytchBridgeModule.NAME)

class StytchBridgeModule(reactContext: ReactApplicationContext) :
  NativeStytchBridgeSpec(reactContext) {
  private val encryptionClient: StytchEncryptionClient = StytchEncryptionClient()
  private val platformPersistenceClient: StytchPlatformPersistenceClient = StytchPlatformPersistenceClient(reactContext)
  private val deviceInfo = reactContext.getDeviceInfo()

  override fun getName(): String {
    return NAME
  }

  override fun getDeviceInfo(): String {
    return Json.encodeToString(deviceInfo)
  }

  override fun saveData(key: String, data: String) {
    platformPersistenceClient.saveData(key, data)
  }

  override fun getData(key: String): String? = platformPersistenceClient.getData(key)

  override fun removeData(key: String) {
    platformPersistenceClient.removeData(key)
  }

  override fun encryptData(data: String): String {
    val encrypted = encryptionClient.encrypt(data.decodeBase64Bytes())
    return encrypted.encodeBase64()
  }

  override fun decryptData(data: String): String {
    val decrypted = encryptionClient.decrypt(data.decodeBase64Bytes())
    return decrypted.encodeBase64()
  }

  override fun deleteKey() {
   encryptionClient.deleteKey()
  }

  override fun resetPreferences() {
   platformPersistenceClient.reset()
  }


  companion object {
    const val NAME = "StytchBridge"
  }
}
