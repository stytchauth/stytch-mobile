package com.stytch.mobile.bridge

import com.facebook.react.bridge.Promise
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

  override fun getDeviceInfo(promise: Promise) {
    promise.resolve(deviceInfo)
  }

  override fun saveData(key: String, data: String, promise: Promise) {
    try {
      promise.resolve(platformPersistenceClient.saveData(key, data))
    } catch (e: Exception) {
      promise.reject(e)
    }
  }

  override fun getData(key: String, promise: Promise) {
    try {
      promise.resolve(platformPersistenceClient.getData(key))
    } catch (e: Exception) {
      promise.reject(e)
    }
  }

  override fun removeData(key: String, promise: Promise) {
    try {
      promise.resolve(platformPersistenceClient.removeData(key))
    } catch (e: Exception) {
      promise.reject(e)
    }
  }

  override fun encryptData(data: String, promise: Promise) {
    try {
      val encrypted = encryptionClient.encrypt(data.decodeBase64Bytes())
      promise.resolve(encrypted.encodeBase64())
    } catch (e: Exception) {
      promise.reject(e)
    }
  }

  override fun decryptData(data: String, promise: Promise) {
    try {
      val decrypted = encryptionClient.decrypt(data.decodeBase64Bytes())
      promise.resolve(decrypted.encodeBase64())
    } catch (e: Exception) {
      promise.reject(e)
    }
  }

  override fun deleteKey(promise: Promise) {
    try {
      promise.resolve(encryptionClient.deleteKey())
    } catch (e: Exception) {
      promise.reject(e)
    }
  }


  companion object {
    const val NAME = "StytchBridge"
  }
}
