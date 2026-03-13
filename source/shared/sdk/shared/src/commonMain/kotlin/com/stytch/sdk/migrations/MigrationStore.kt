package com.stytch.sdk.migrations

import com.stytch.sdk.persistence.StytchPlatformPersistenceClient
import kotlinx.serialization.json.Json

/**
 * Tracks which migrations have been applied for a given SDK namespace.
 *
 * Records are stored as unencrypted JSON via [StytchPlatformPersistenceClient] — migration
 * metadata (IDs and timestamps) is not sensitive, and using the encrypted client would
 * create ordering problems if encryption setup were ever part of a migration.
 */
internal class MigrationStore(
    namespace: String,
    private val platformPersistenceClient: StytchPlatformPersistenceClient,
) {
    private val key = "stytch_${namespace}_migrations"

    fun getAppliedIds(): Set<Int> {
        val json = platformPersistenceClient.getData(key) ?: return emptySet()
        return try {
            Json.decodeFromString<List<MigrationRecord>>(json).map { it.id }.toSet()
        } catch (_: Exception) {
            emptySet()
        }
    }

    fun record(record: MigrationRecord) {
        val current =
            try {
                val json = platformPersistenceClient.getData(key) ?: "[]"
                Json.decodeFromString<List<MigrationRecord>>(json).toMutableList()
            } catch (_: Exception) {
                mutableListOf()
            }
        current.add(record)
        platformPersistenceClient.saveData(key, Json.encodeToString(current))
    }
}
