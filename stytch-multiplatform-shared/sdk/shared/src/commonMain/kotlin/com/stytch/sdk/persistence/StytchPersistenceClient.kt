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
    public suspend inline fun <reified T> save(
        key: String,
        data: T?,
    ): Unit =
        withContext(dispatcher) {
            data?.let { plaintext ->
                val plaintextAsString = Json.encodeToString(plaintext)
                val encrypted = encryptionClient.encrypt(plaintextAsString.toByteArray())
                val encoded = encrypted.encodeBase64()
                platformPersistenceClient.save(key, encoded)
            } ?: remove(key)
        }

    public suspend inline fun <reified T> get(
        key: String,
        default: T?,
    ): T? =
        withContext(dispatcher) {
            return@withContext platformPersistenceClient.get(key)?.let { encoded ->
                val decoded = encoded.decodeBase64Bytes()
                val decrypted = encryptionClient.decrypt(decoded).decodeToString()
                Json.decodeFromString<T>(decrypted)
            } ?: default
        }

    public suspend fun remove(key: String): Unit =
        withContext(dispatcher) {
            platformPersistenceClient.remove(key)
        }
}
