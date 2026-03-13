package com.stytch.sdk.consumer.migrations

import com.stytch.sdk.data.StytchError
import com.stytch.sdk.migrations.ILegacyTokenReader
import com.stytch.sdk.migrations.Migration
import com.stytch.sdk.migrations.MigrationResult

internal class LegacyTokenMigration(
    private val tokenReader: ILegacyTokenReader,
    override val id: Int = 1,
) : Migration {
    private var existingDecryptedToken: String? = null

    override suspend fun isApplicable(): Boolean =
        try {
            existingDecryptedToken = tokenReader.getExistingToken()
        } catch (e: StytchError) {
            false
        }

    override suspend fun run(): MigrationResult {
        TODO("Not yet implemented")
    }
}
