package com.stytch.sdk.migrations

public interface Migration {
    public val id: Int

    /**
     * A cheap pre-check run before [run]. If this returns false, the migration is recorded as
     * [MigrationResult.Skipped] and will not be attempted again on future launches.
     */
    public suspend fun isApplicable(): Boolean

    public suspend fun run(): MigrationResult
}
