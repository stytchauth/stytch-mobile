package com.stytch.sdk.migrations

import com.stytch.sdk.StytchEncryptionManagerSwift
import com.stytch.sdk.data.KMPPlatformType
import com.stytch.sdk.data.ReactNativeSessionState
import com.stytch.sdk.data.StytchDispatchers
import com.stytch.sdk.data.Vertical
import com.stytch.sdk.persistence.StytchPlatformPersistenceClient
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import platform.Foundation.NSUserDefaults

@OptIn(ExperimentalForeignApi::class)
public actual class LegacyTokenReader : ILegacyTokenReader {
    private val jsonSerializer =
        Json {
            ignoreUnknownKeys = true
            isLenient = true
        }
    private val swiftEncryptionManager = StytchEncryptionManagerSwift.shared()

    actual override suspend fun getExistingToken(
        publicToken: String,
        platformPersistenceClient: StytchPlatformPersistenceClient,
        dispatchers: StytchDispatchers,
        platform: KMPPlatformType,
    ): String? =
        when (platform) {
            KMPPlatformType.IOS -> {
                getDecryptedSessionTokenFromLegacyIosSDK(dispatcher = dispatchers.ioDispatcher)
            }

            KMPPlatformType.REACTNATIVE -> {
                getDecryptedSessionTokenFromLegacyReactNativeSDK(dispatcher = dispatchers.ioDispatcher, publicToken = publicToken)
            }

            else -> {
                throw InvalidPlatformForLegacyTokenMigration(targetPlatform = platform, actualPlatform = KMPPlatformType.IOS)
            }
        }

    private suspend fun getDecryptedSessionTokenFromLegacyIosSDK(dispatcher: CoroutineDispatcher): String? =
        withContext(dispatcher) {
            val keyData = swiftEncryptionManager.getLegacyNativeEncryptionKey() ?: return@withContext null
            val userDefaults = NSUserDefaults(suiteName = "StytchEncryptedUserDefaults")
            // `stytch_session` is the session_token; the naming is admittedly confusing
            val encryptedSessionToken = userDefaults.dataForKey("stytch_session") ?: return@withContext null
            swiftEncryptionManager.decryptDataFromLegacyInstallWithEncryptedData(
                encryptedData = encryptedSessionToken,
                keyData = keyData,
            )
        }

    private suspend fun getDecryptedSessionTokenFromLegacyReactNativeSDK(
        dispatcher: CoroutineDispatcher,
        publicToken: String,
    ): String? =
        withContext(dispatcher) {
            // first, check if a key exists
            val keyData = swiftEncryptionManager.getLegacyReactNativeEncryptionKey() ?: return@withContext null
            val userDefaults = NSUserDefaults(suiteName = "StytchPersistence")
            // The double "stytch_" is intentional, NOT a typo
            val sessionStateKey = "stytch_stytch_sdk_state_$publicToken"
            val encryptedSessionStateData = userDefaults.dataForKey(sessionStateKey) ?: return@withContext null
            val sessionStateString =
                swiftEncryptionManager.decryptDataFromLegacyInstallWithEncryptedData(
                    encryptedData = encryptedSessionStateData,
                    keyData = keyData,
                ) ?: return@withContext null
            val sessionState: ReactNativeSessionState = jsonSerializer.decodeFromString(sessionStateString)
            sessionState.sessionToken
        }
}
