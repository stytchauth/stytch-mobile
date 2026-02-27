package com.stytch.mobile.bridge

import android.app.Application
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.module.annotations.ReactModule
import com.stytch.sdk.data.getDeviceInfo
import com.stytch.sdk.dfp.CAPTCHAProviderImpl
import com.stytch.sdk.dfp.DFPProviderImpl
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
import kotlinx.coroutines.*


/**
 * This bridge module is just an RN-accessible, well, bridge, to the existing code in the "real" Stytch SDK,
 * which is a dependency of this React Native SDK.
 */
@ReactModule(name = StytchBridgeModule.NAME)

class StytchBridgeModule(reactContext: ReactApplicationContext) :
  NativeStytchBridgeSpec(reactContext) {
  private val encryptionClient: StytchEncryptionClient = StytchEncryptionClient()
  private val platformPersistenceClient: StytchPlatformPersistenceClient = StytchPlatformPersistenceClient(reactContext)
  private val dfpProvider = DFPProviderImpl(reactContext)
  private val captchaProvider = CAPTCHAProviderImpl(reactContext.applicationContext as Application)
  private val deviceInfo = reactContext.getDeviceInfo()
  private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

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

  override fun configureDfp(publicToken: String, dfppaDomain: String) {
    dfpProvider.configureDfp(publicToken, dfppaDomain)
  }

  override fun getTelemetryId(promise: Promise) {
    scope.launch {
      promise.resolve(dfpProvider.getTelemetryId())
    }
  }

  override fun configureCaptcha(siteKey: String) {
    scope.launch {
      captchaProvider.initialize(siteKey)
    }
  }
  
  override fun getCAPTCHAToken(promise: Promise) {
    scope.launch {
      promise.resolve(captchaProvider.getCAPTCHAToken())
    }
  }

  override fun isCaptchaConfigured(): Boolean {
    return captchaProvider.isConfigured
  }

  override fun generateCodeVerifier(): String {
    return encryptionClient.generateCodeVerifier().encodeBase64()
  }

  override fun generateCodeChallenge(verifier: String): String {
    return encryptionClient.generateCodeChallenge(verifier.decodeBase64Bytes()).encodeBase64()
  }

  override fun signEd25519(key: String, data: String): String {
    return encryptionClient.signEd25519(key.decodeBase64Bytes(), data.decodeBase64Bytes()).encodeBase64()
  }

  override fun generateEd25519KeyPair(): List<String> {
    val result = encryptionClient.generateEd25519KeyPair()
    return listOf(result.publicKey.encodeBase64(), result.privateKey.encodeBase64(), result.encryptedPrivateKey.encodeBase64())
  }

  override fun deriveEd25519PublicKeyFromPrivateKeyBytes(privateKeyBytes: String): String {
    return encryptionClient.deriveEd25519PublicKeyFromPrivateKeyBytes(privateKeyBytes.decodeBase64Bytes()).encodeBase64()
  }

  companion object {
    const val NAME = "StytchBridge"
  }
}
