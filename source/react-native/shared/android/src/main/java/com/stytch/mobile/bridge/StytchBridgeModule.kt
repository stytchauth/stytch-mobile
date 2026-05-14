package com.stytch.mobile.bridge

import android.app.Application
import androidx.activity.ComponentActivity
import androidx.fragment.app.FragmentActivity
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.WritableArray
import com.facebook.react.module.annotations.ReactModule
import com.stytch.sdk.biometrics.BiometricPromptData
import com.stytch.sdk.biometrics.BiometricsParameters
import com.stytch.sdk.biometrics.BiometricsProvider
import com.stytch.sdk.data.KMPPlatformType
import com.stytch.sdk.data.StytchDispatchers
import com.stytch.sdk.data.Vertical
import com.stytch.sdk.data.getDeviceInfo
import com.stytch.sdk.data.getPublicTokenInfo
import com.stytch.sdk.migrations.LegacyTokenReader
import com.stytch.sdk.dfp.CAPTCHAProviderImpl
import com.stytch.sdk.dfp.DFPProviderImpl
import com.stytch.sdk.encryption.StytchEncryptionClient
import com.stytch.sdk.data.GoogleCredentialConfiguration
import com.stytch.sdk.oauth.OAuthProvider
import com.stytch.sdk.oauth.OAuthStartParameters
import com.stytch.sdk.passkeys.PasskeyProvider
import com.stytch.sdk.passkeys.PasskeysParameters
import com.stytch.sdk.persistence.StytchPersistenceClient
import com.stytch.sdk.persistence.StytchPlatformPersistenceClient
import com.stytch.sdk.persistence.STYTCH_PERSISTENCE_FILE_NAME
import com.stytch.sdk.pkce.PKCEClient
import io.ktor.util.decodeBase64Bytes
import io.ktor.util.encodeBase64
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.coroutines.*

/**
 * This bridge module is just an RN-accessible, well, bridge, to the existing code in the "real" Stytch SDK,
 * which is a dependency of this React Native SDK.
 */
@ReactModule(name = StytchBridgeModule.NAME)

class StytchBridgeModule(reactContext: ReactApplicationContext) :
  NativeStytchBridgeSpec(reactContext) {
  private val encryptionClient: StytchEncryptionClient = StytchEncryptionClient()
  private val platformPersistenceClient: StytchPlatformPersistenceClient = StytchPlatformPersistenceClient(reactContext, STYTCH_PERSISTENCE_FILE_NAME)
  private val dfpProvider = DFPProviderImpl(reactContext)
  private val captchaProvider = CAPTCHAProviderImpl(reactContext.applicationContext as Application)
  private val deviceInfo = reactContext.getDeviceInfo()
  private val ioScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
  private val mainScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

  private val biometricsProvider = BiometricsProvider(encryptionClient, platformPersistenceClient)
  private val passkeysProvider = PasskeyProvider()
  private val persistenceClient = StytchPersistenceClient(Dispatchers.IO, encryptionClient, platformPersistenceClient)
  private val pkceClient = PKCEClient(encryptionClient, persistenceClient)

  private val dispatchers = StytchDispatchers(ioDispatcher = Dispatchers.IO, mainDispatcher = Dispatchers.Main)

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
    ioScope.launch {
      promise.resolve(dfpProvider.getTelemetryId())
    }
  }

  override fun configureCaptcha(siteKey: String) {
    ioScope.launch {
      captchaProvider.initialize(siteKey)
    }
  }
  
  override fun getCAPTCHAToken(promise: Promise) {
    ioScope.launch {
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

  override fun generateEd25519KeyPair(): WritableArray {
    val result = encryptionClient.generateEd25519KeyPair()
    val nativeArray = Arguments.createArray()
    nativeArray.pushString(result.publicKey.encodeBase64())
    nativeArray.pushString(result.privateKey.encodeBase64())
    return nativeArray
  }

  override fun deriveEd25519PublicKeyFromPrivateKeyBytes(privateKeyBytes: String): String {
    return encryptionClient.deriveEd25519PublicKeyFromPrivateKeyBytes(privateKeyBytes.decodeBase64Bytes()).encodeBase64()
  }

  override fun getBiometricsAvailability(
    sessionDurationMinutes: Double,
    androidAllowDeviceCredentials: Boolean?,
    androidTitle: String?,
    androidSubTitle: String?,
    androidNegativeButtonText: String?,
    androidAllowFallbackToCleartext: Boolean?,
    iosReason: String?,
    iosFallbackTitle: String?,
    iosCancelTitle: String?,
    promise: Promise,
  ) {
    mainScope.launch {
      runCatching {
        biometricsProvider.getAvailability(
          BiometricsParameters(
            context = reactApplicationContext.currentActivity!! as FragmentActivity,
            allowDeviceCredentials = androidAllowDeviceCredentials ?: false,
            sessionDurationMinutes = sessionDurationMinutes.toInt(),
            promptData = BiometricPromptData(
                title = androidTitle ?: "Biometric Authentication",
                subTitle = androidSubTitle ?: "Authenticate using your device biometrics",
                negativeButtonText = androidNegativeButtonText ?: "Cancel",
            ),
            allowFallbackToCleartext = androidAllowFallbackToCleartext ?: false,
          )
        )
      }
      .onSuccess { availability ->
        val asString = Json.encodeToString(availability)
        promise.resolve(asString)
      }
      .onFailure { exception ->
        promise.reject(exception)
      }
    }
  }

  override fun createBiometricKey(
    sessionDurationMinutes: Double,
    androidAllowDeviceCredentials: Boolean?,
    androidTitle: String?,
    androidSubTitle: String?,
    androidNegativeButtonText: String?,
    androidAllowFallbackToCleartext: Boolean?,
    iosReason: String?,
    iosFallbackTitle: String?,
    iosCancelTitle: String?,
    promise: Promise,
  ) {
    mainScope.launch {
      runCatching {
        biometricsProvider.createBiometricKey(
          BiometricsParameters(
            context = reactApplicationContext.currentActivity!! as FragmentActivity,
            allowDeviceCredentials = androidAllowDeviceCredentials ?: false,
            sessionDurationMinutes = sessionDurationMinutes.toInt(),
            promptData = BiometricPromptData(
              title = androidTitle ?: "Biometric Authentication",
              subTitle = androidSubTitle ?: "Authenticate using your device biometrics",
              negativeButtonText = androidNegativeButtonText ?: "Cancel",
            ),
            allowFallbackToCleartext = androidAllowFallbackToCleartext ?: false,
          )
        )
      }
      .onSuccess { publicKey ->
        promise.resolve(publicKey)
      }
      .onFailure { exception ->
        promise.reject(exception)
      }
    }
  }

  override fun retrieveBiometricKey(
    sessionDurationMinutes: Double,
    androidAllowDeviceCredentials: Boolean?,
    androidTitle: String?,
    androidSubTitle: String?,
    androidNegativeButtonText: String?,
    androidAllowFallbackToCleartext: Boolean?,
    iosReason: String?,
    iosFallbackTitle: String?,
    iosCancelTitle: String?,
    promise: Promise,
  ) {
    mainScope.launch {
      runCatching {
        biometricsProvider.retrieveBiometricKey(
          BiometricsParameters(
            context = reactApplicationContext.currentActivity!! as FragmentActivity,
            allowDeviceCredentials = androidAllowDeviceCredentials ?: false,
            sessionDurationMinutes = sessionDurationMinutes.toInt(),
            promptData = BiometricPromptData(
              title = androidTitle ?: "Biometric Authentication",
              subTitle = androidSubTitle ?: "Authenticate using your device biometrics",
              negativeButtonText = androidNegativeButtonText ?: "Cancel",
            ),
            allowFallbackToCleartext = androidAllowFallbackToCleartext ?: false,
          )
        )
      }
      .onSuccess { publicKey ->
        promise.resolve(publicKey)
      }
      .onFailure { exception ->
        promise.reject(exception)
      }
    }
  }

  override fun signWithBiometricKey(challenge: String, promise: Promise) {
    ioScope.launch {
      runCatching {
        biometricsProvider.signWithBiometricKey(challenge)
      }.onSuccess { signature ->
        promise.resolve(signature)
      }
      .onFailure { exception ->
        promise.reject(exception)
      }
    }
  }

  override fun persistBiometricRegistration(registrationId: String, promise: Promise) {
    ioScope.launch {
      runCatching {
        biometricsProvider.persistRegistration(registrationId)
      }
      .onSuccess {
        promise.resolve(null)
      }
      .onFailure { exception ->
        promise.reject(exception)
      }
    }
  }

  override fun removeBiometricRegistration(promise: Promise) {
    ioScope.launch {
      runCatching {
        biometricsProvider.removeRegistration()
      }
      .onSuccess {
        promise.resolve(null)
      }
      .onFailure { exception ->
        promise.reject(exception)
      }
    }
  }

  override fun createPublicKeyCredential(
    domain: String,
    preferImmediatelyAvailableCredentials: Boolean,
    json: String,
    sessionDurationMinutes: Double?,
    promise: Promise
  ) {
    ioScope.launch {
      runCatching {
        passkeysProvider.createPublicKeyCredential(
            parameters = PasskeysParameters(
                activity = reactApplicationContext.currentActivity!! as FragmentActivity,
                domain = domain,
                sessionDurationMinutes = sessionDurationMinutes?.toInt(),
                preferImmediatelyAvailableCredentials = preferImmediatelyAvailableCredentials,
            ),
            dispatchers = dispatchers,
            json = json,
        )
      }
        .onSuccess { credential ->
          promise.resolve(Json.encodeToString(credential))
        }
        .onFailure { exception ->
          promise.reject(exception)
        }
    }
  }

  override fun getPublicKeyCredential(
    domain: String,
    preferImmediatelyAvailableCredentials: Boolean,
    json: String,
    sessionDurationMinutes: Double?,
    promise: Promise
  ) {
    ioScope.launch {
      runCatching {
        passkeysProvider.getPublicKeyCredential(
          parameters = PasskeysParameters(
            activity = reactApplicationContext.currentActivity!! as FragmentActivity,
            domain = domain,
            sessionDurationMinutes = sessionDurationMinutes?.toInt(),
            preferImmediatelyAvailableCredentials = preferImmediatelyAvailableCredentials,
          ),
          dispatchers = dispatchers,
          json = json,
        )
      }
        .onSuccess { credential ->
          promise.resolve(Json.encodeToString(credential))
        }
        .onFailure { exception ->
          promise.reject(exception)
        }
    }
  }

  override fun getOAuthToken(
    type: String,
    baseUrl: String,
    publicToken: String,
    loginRedirectUrl: String?,
    signupRedirectUrl: String?,
    customScopes: ReadableArray?,
    providerParams: String?,
    oauthAttachToken: String?,
    sessionDurationMinutes: Double?,
    googleCredentialConfiguration: String?,
    promise: Promise
  ) {
    mainScope.launch {
      runCatching {
        val oauthProvider = OAuthProvider(
          application = reactApplicationContext.applicationContext as Application,
          packageName = deviceInfo.applicationPackageName,
          googleCredentialConfiguration = googleCredentialConfiguration?.let {
            val obj = Json.parseToJsonElement(it).jsonObject
            GoogleCredentialConfiguration(
              googleClientId = obj["googleClientId"]!!.jsonPrimitive.content,
              autoSelectEnabled = obj["autoSelectEnabled"]?.jsonPrimitive?.boolean ?: true,
            )
          }
        )
        val providerParamsMap = mutableMapOf<String, String>()
        providerParams?.split("&")?.forEach {
          val (key, value) = it.split("=")
          providerParamsMap[key] = value
        }
        val customScopesList = mutableListOf<String>()
        customScopes?.toArrayList()?.forEach {
          customScopesList.add(it as String)
        }
        oauthProvider.getOAuthToken(
            parameters = OAuthStartParameters(
              activity = reactApplicationContext.currentActivity!! as ComponentActivity,
              loginRedirectUrl = loginRedirectUrl,
              signupRedirectUrl = signupRedirectUrl,
              customScopes = customScopesList,
              providerParams = providerParamsMap,
              oauthAttachToken = oauthAttachToken,
              sessionDurationMinutes = sessionDurationMinutes?.toInt(),
            ),
            pkceClient = pkceClient,
            dispatchers = dispatchers,
            type = Json.decodeFromString(type),
            baseUrl = baseUrl,
            publicTokenInfo = getPublicTokenInfo(publicToken),
        )
      }
        .onSuccess { token ->
          val asString = Json.encodeToString(token)
          promise.resolve(asString)
        }
        .onFailure { exception ->
          promise.reject(exception)
        }
    }
  }

  override fun startBrowserFlow(
    url: String,
    promise: Promise,
  ) {
    mainScope.launch {
      runCatching {
        val oauthProvider = OAuthProvider(
          application = reactApplicationContext.applicationContext as Application,
          packageName = deviceInfo.applicationPackageName,
        )
        // The only parameter we need to send to startBrowserFlow() is the activity; all the real parameters were already encoded into the url
        val parameters = OAuthStartParameters(
            activity = reactApplicationContext.currentActivity!! as ComponentActivity,
        )
        oauthProvider.startBrowserFlow(url, parameters, dispatchers)
      }.onSuccess { result ->
          val asString = Json.encodeToString(result)
          promise.resolve(asString)
        }
        .onFailure { exception ->
          promise.reject(exception)
        }
    }
  }

  override fun getLegacySessionData(publicToken: String, vertical: String, promise: Promise) {
    ioScope.launch {
      runCatching {
        val tokenReader = LegacyTokenReader()
        val verticalEnum = Json.decodeFromString<Vertical>(vertical)
        tokenReader.getExistingSessionData(
          publicToken = publicToken,
          platformPersistenceClient = platformPersistenceClient,
          dispatchers = dispatchers,
          platform = KMPPlatformType.REACTNATIVE,
          vertical = verticalEnum,
        )
      }
      .onSuccess { data ->
        promise.resolve(data?.let { Json.encodeToString(it) })
      }
      .onFailure { exception ->
        promise.reject(exception)
      }
    }
  }

  companion object {
    const val NAME = "StytchBridge"
  }
}
