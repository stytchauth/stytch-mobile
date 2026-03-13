package com.stytch.sdk.b2b.migrations

import com.stytch.sdk.b2b.StytchB2BAuthenticationStateManager.Companion.SESSION_IDENTIFIER
import com.stytch.sdk.b2b.StytchB2BAuthenticationStateManager.Companion.SESSION_TOKEN_IDENTIFIER
import com.stytch.sdk.b2b.networking.models.ApiB2bSessionV1MemberSession
import com.stytch.sdk.data.KMPPlatformType
import com.stytch.sdk.data.StytchDispatchers
import com.stytch.sdk.data.StytchError
import com.stytch.sdk.data.Vertical
import com.stytch.sdk.migrations.ILegacyTokenReader
import com.stytch.sdk.migrations.Migration
import com.stytch.sdk.migrations.MigrationResult
import com.stytch.sdk.migrations.PersistedLegacySessionData
import com.stytch.sdk.persistence.StytchPersistenceClient
import kotlinx.serialization.json.Json

internal class LegacyTokenMigration(
    private val publicToken: String,
    private val platform: KMPPlatformType,
    private val tokenReader: ILegacyTokenReader,
    private val persistenceClient: StytchPersistenceClient,
    private val dispatchers: StytchDispatchers,
    override val id: Int = 1,
) : Migration {
    private var persistedLegacySessionData: PersistedLegacySessionData? = null

    override suspend fun isApplicable(): Boolean =
        try {
            persistedLegacySessionData =
                tokenReader.getExistingSessionData(
                    publicToken = publicToken,
                    platform = platform,
                    platformPersistenceClient = persistenceClient.platformPersistenceClient,
                    dispatchers = dispatchers,
                    vertical = Vertical.B2B,
                )
            return persistedLegacySessionData != null
        } catch (_: StytchError) {
            // If something went wrong getting the previous token data, consider it unrecoverable, and therefore not applicable
            false
        }

    override suspend fun run(): MigrationResult {
        val data = persistedLegacySessionData ?: return MigrationResult.Skipped("No persisted data recovered")
        persistenceClient.save(SESSION_TOKEN_IDENTIFIER, data.token)
        // we can't be sure that the persisted session data from the legacy SDKs contains a FULL session as defined by the OpenAPI spec,
        // so rehydrating the session is optimistic, not guaranteed. This is fine, because the token is the only thing truly necessary
        try {
            data.sessionDataString?.let {
                val session: ApiB2bSessionV1MemberSession? = Json.decodeFromString(it)
                persistenceClient.save(SESSION_IDENTIFIER, session)
            }
        } catch (_: Exception) {
            // intentional NOOP
        }
        return MigrationResult.Success
    }
}
