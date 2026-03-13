package com.stytch.sdk.migrations

import android.content.Context
import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeyTemplates
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import com.stytch.sdk.data.KMPPlatformType
import com.stytch.sdk.data.ReactNativeSessionState
import com.stytch.sdk.data.StytchDispatchers
import com.stytch.sdk.data.Vertical
import com.stytch.sdk.persistence.StytchPlatformPersistenceClient
import io.ktor.util.decodeBase64Bytes
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.security.KeyStore

public actual class LegacyTokenReader : ILegacyTokenReader {
    private val jsonSerializer =
        Json {
            ignoreUnknownKeys = true
            isLenient = true
        }

    actual override suspend fun getExistingToken(
        publicToken: String,
        platformPersistenceClient: StytchPlatformPersistenceClient,
        dispatchers: StytchDispatchers,
        platform: KMPPlatformType,
        vertical: Vertical,
    ): String? =
        when (platform) {
            KMPPlatformType.ANDROID -> {
                getDecryptedSessionTokenFromLegacyAndroidSDK(
                    context = platformPersistenceClient.context,
                    vertical = vertical,
                    dispatcher = dispatchers.ioDispatcher,
                )
            }

            KMPPlatformType.REACTNATIVE -> {
                getDecryptedSessionTokenFromLegacyReactNativeSDK(
                    context = platformPersistenceClient.context,
                    publicToken = publicToken,
                    vertical = vertical,
                    dispatcher = dispatchers.ioDispatcher,
                )
            }

            else -> {
                throw InvalidPlatformForLegacyTokenMigration(targetPlatform = platform, actualPlatform = KMPPlatformType.ANDROID)
            }
        }

    private suspend fun getDecryptedSessionTokenFromLegacyAndroidSDK(
        context: Context,
        vertical: Vertical,
        dispatcher: CoroutineDispatcher,
    ): String? {
        val keysetName = "Stytch RSA 2048"
        val aead = getEncryptionPrimitive(context, keysetName, dispatcher) ?: return null
        // Consumer and B2B (on native) used the same key names for persistence, so this is easy
        // First, get the old token
        val sharedPreferences = context.getSharedPreferences("stytch_preferences", Context.MODE_PRIVATE)
        val encryptedToken = sharedPreferences.getString("session_token", null)
        if (encryptedToken.isNullOrEmpty()) return null
        // Then, base64 decode it
        val cipherText = encryptedToken.decodeBase64Bytes()
        // Finally, return the decrypted token
        return aead.decrypt(cipherText, null).decodeToString()
    }

    private suspend fun getDecryptedSessionTokenFromLegacyReactNativeSDK(
        context: Context,
        publicToken: String,
        vertical: Vertical,
        dispatcher: CoroutineDispatcher,
    ): String? {
        val keysetName = ""
        val aead = getEncryptionPrimitive(context, keysetName, dispatcher) ?: return null
        // RN used the same shared preferences file for the keys and the content
        val sharedPreferences = context.getSharedPreferences("stytch_secured_pref", Context.MODE_PRIVATE)
        // RN saved the whole session object, not just the token, so decode the whole thing, and extract just the sessionToken
        val sessionStateKey = "stytch_sdk_state_$publicToken"
        val sessionStateStringEncryptedAndEncoded = sharedPreferences.getString(sessionStateKey, null)
        if (sessionStateStringEncryptedAndEncoded.isNullOrEmpty()) return null
        val sessionStateStringEncrypted = sessionStateStringEncryptedAndEncoded.decodeBase64Bytes()
        val sessionStateString = aead.decrypt(sessionStateStringEncrypted, null).decodeToString()
        val sessionState: ReactNativeSessionState = jsonSerializer.decodeFromString(sessionStateString)
        return sessionState.sessionToken
    }

    private suspend fun getEncryptionPrimitive(
        context: Context,
        keysetName: String,
        dispatcher: CoroutineDispatcher,
    ): Aead? =
        withContext(dispatcher) {
            // Native and RN are almost identical in how the encryption primitives are set up, only differing in the keysetName
            val keyStore: KeyStore = KeyStore.getInstance("AndroidKeyStore")
            keyStore.load(null)
            val masterKeyUri = "android-keystore://stytch_master_key"
            if (!keyStore.containsAlias(masterKeyUri)) {
                // if it doesn't exist, there's nothing to do
                return@withContext null
            }
            return@withContext try {
                AndroidKeysetManager
                    .Builder()
                    .withSharedPref(context, keysetName, "stytch_secured_pref")
                    .withKeyTemplate(KeyTemplates.get("AES256_GCM"))
                    .withMasterKeyUri(masterKeyUri)
                    .build()
                    .keysetHandle
                    .getPrimitive(Aead::class.java)
            } catch (_: Exception) {
                // if we can't get the primitive, there's nothing to do
                null
            }
        }
}
