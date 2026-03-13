package com.stytch.sdk.consumer.migrations

import com.stytch.sdk.consumer.StytchConsumerAuthenticationStateManager.Companion.SESSION_TOKEN_IDENTIFIER
import com.stytch.sdk.data.KMPPlatformType
import com.stytch.sdk.data.StytchError
import com.stytch.sdk.data.Vertical
import com.stytch.sdk.migrations.ILegacyTokenReader
import com.stytch.sdk.migrations.Migration
import com.stytch.sdk.migrations.MigrationResult
import com.stytch.sdk.persistence.StytchPersistenceClient

internal class LegacyTokenMigration(
    private val platform: KMPPlatformType,
    private val tokenReader: ILegacyTokenReader,
    private val persistenceClient: StytchPersistenceClient,
    override val id: Int = 1,
) : Migration {
    private var decryptedTokenFromPreviousInstall: String? = null

    override suspend fun isApplicable(): Boolean =
        try {
            decryptedTokenFromPreviousInstall =
                tokenReader.getExistingToken(
                    platformPersistenceClient = persistenceClient.platformPersistenceClient,
                    platform = platform,
                    vertical = Vertical.CONSUMER,
                )
            return decryptedTokenFromPreviousInstall != null
        } catch (_: StytchError) {
            // If something went wrong getting the previous token data, consider it unrecoverable, and therefore not applicable
            false
        }

    override suspend fun run(): MigrationResult {
        // ALl we need to do is persist the OLD token (with new encryption). After the migration runs, the session token will be retrieved,
        // the session validation will run and populate (or nuke, as appropriate) the other persisted session data
        persistenceClient.save(SESSION_TOKEN_IDENTIFIER, decryptedTokenFromPreviousInstall)
        return MigrationResult.Success
    }
}
