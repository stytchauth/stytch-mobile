package com.stytch.sdk.migrations

import kotlin.time.Clock

public class MigrationRunner(
    private val migrations: List<Migration>,
    private val store: MigrationStore,
) {
    public suspend fun runPendingMigrations() {
        val appliedIds = store.getAppliedIds()

        for (migration in migrations.sortedBy { it.id }) {
            if (migration.id in appliedIds) continue

            val result =
                try {
                    if (!migration.isApplicable()) {
                        MigrationResult.Skipped("not applicable")
                    } else {
                        migration.run()
                    }
                } catch (e: Exception) {
                    MigrationResult.Failed(e)
                }

            store.record(
                MigrationRecord(
                    id = migration.id,
                    appliedAt = Clock.System.now().toEpochMilliseconds(),
                    status =
                        when (result) {
                            is MigrationResult.Success -> MigrationStatus.SUCCESS
                            is MigrationResult.Skipped -> MigrationStatus.SKIPPED
                            is MigrationResult.Failed -> MigrationStatus.FAILED
                        },
                ),
            )
        }
    }
}
