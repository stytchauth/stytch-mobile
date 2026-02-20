package com.stytch.sdk.persistence

import com.stytch.sdk.encryption.StytchEncryptionClient
import io.ktor.util.decodeBase64Bytes
import io.ktor.util.encodeBase64
import io.ktor.utils.io.core.toByteArray
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

public class StytchPersistenceClient(
    public val dispatcher: CoroutineDispatcher,
    public val encryptionClient: StytchEncryptionClient,
    public val platformPersistenceClient: StytchPlatformPersistenceClient,
) {
    public inline fun <reified T> save(
        key: String,
        data: T?,
    ): Unit =
        data?.let { plaintext ->
            val plaintextAsString = Json.encodeToString(plaintext)
            val encrypted = encryptionClient.encrypt(plaintextAsString.toByteArray())
            val encoded = encrypted.encodeBase64()
            platformPersistenceClient.saveData(key, encoded)
        } ?: remove(key)

    public inline fun <reified T> get(
        key: String,
        default: T?,
    ): T? =
        platformPersistenceClient.getData(key)?.let { encoded ->
            try {
                val decoded = encoded.decodeBase64Bytes()
                val decrypted = encryptionClient.decrypt(decoded).decodeToString()
                Json.decodeFromString<T>(decrypted)
            } catch (_: Exception) {
                // malformed data, nuke it
                remove(key)
                null
            }
        } ?: default

    public fun remove(key: String): Unit = platformPersistenceClient.removeData(key)
}
