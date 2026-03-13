package com.stytch.sdk.migrations

import android.content.Context
import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeyTemplates
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import com.stytch.sdk.data.KMPPlatformType
import com.stytch.sdk.data.StytchDispatchers
import com.stytch.sdk.data.Vertical
import com.stytch.sdk.persistence.StytchPlatformPersistenceClient
import io.ktor.util.decodeBase64Bytes
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.security.KeyStore

public actual class LegacyTokenReader : ILegacyTokenReader {
    actual override suspend fun getExistingToken(
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
    ): String? =
        withContext(dispatcher) {
            // Native Consumer and B2B SDKs used the same configurations (keys, names, etc.) for encryption and persistence, so this is easy
            val keyStore: KeyStore = KeyStore.getInstance("AndroidKeyStore")
            keyStore.load(null)
            // This is the master key from the legacy SDKs
            val masterKeyUri = "android-keystore://stytch_master_key"
            if (!keyStore.containsAlias(masterKeyUri)) {
                // if it doesn't exist, there's nothing to do
                return@withContext null
            }
            val aead =
                try {
                    AndroidKeysetManager
                        .Builder()
                        .withSharedPref(context, "Stytch RSA 2048", "stytch_secured_pref")
                        .withKeyTemplate(KeyTemplates.get("AES256_GCM"))
                        .withMasterKeyUri(masterKeyUri)
                        .build()
                        .keysetHandle
                        .getPrimitive(Aead::class.java)
                } catch (_: Exception) {
                    // if we can't get the primitive, there's nothing to do
                    return@withContext null
                }
            // if we've got the original key, try to get the old token
            val sharedPreferences = context.getSharedPreferences("stytch_preferences", Context.MODE_PRIVATE)
            val encryptedToken = sharedPreferences.getString("session_token", null)
            if (encryptedToken.isNullOrEmpty()) return@withContext null
            // decode from base64
            val cipherText = encryptedToken.decodeBase64Bytes()
            // return the decrypted token
            aead.decrypt(cipherText, null).decodeToString()
        }

    private suspend fun getDecryptedSessionTokenFromLegacyReactNativeSDK(
        context: Context,
        vertical: Vertical,
        dispatcher: CoroutineDispatcher,
    ): String? =
        withContext(dispatcher) {
            TODO()
        }
}
