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
            try {
                data?.let { plaintext ->
                    println("JORDAN >>> saving $key = $plaintext")
                    val plaintextAsString = Json.encodeToString(plaintext)
                    println("JORDAN >>> STRINGIFIED = $plaintextAsString")
                    val encrypted = encryptionClient.encrypt(plaintextAsString.toByteArray())
                    println("JORDAN >>> ENCRYPTED = $encrypted")
                    val encoded = encrypted.encodeBase64()
                    println("JORDAN >>> ENCODED = $encoded")
                    platformPersistenceClient.save(key, encoded)
                } ?: remove(key)
            } catch (error: Throwable) {
                println("JORDAN >>> ERROR: ${error.message}")
            }
        }

    public suspend inline fun <reified T> get(
        key: String,
        default: T?,
    ): T? =
        withContext(dispatcher) {
            println("JORDAN >>> GETTING $key = $default")
            return@withContext platformPersistenceClient.get(key)?.let { encoded ->
                println("JORDAN >>> GOT ENCODED = $encoded")
                val decoded = encoded.decodeBase64Bytes()
                println("JORDAN >>> GOT DECODED = $decoded")
                val decrypted = encryptionClient.decrypt(decoded).decodeToString()
                println("JORDAN >>> GOT DECRYPTED = $decrypted")
                val serialized = Json.decodeFromString<T>(decrypted)
                println("JORDAN >>> GOT SERIALIZED = $serialized")
                serialized
            } ?: default
        }

    public suspend fun remove(key: String): Unit =
        withContext(dispatcher) {
            platformPersistenceClient.remove(key)
        }
}
