package com.stytch.sdk.migrations

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class MigrationRunnerTest {
    private val store = mockk<MigrationStore>(relaxed = true)

    private fun migration(
        id: Int,
        applicable: Boolean = true,
        result: MigrationResult = MigrationResult.Success,
    ): Migration =
        mockk {
            every { this@mockk.id } returns id
            coEvery { isApplicable() } returns applicable
            coEvery { run() } returns result
        }

    @Test
    fun `pending migration runs and is recorded as SUCCESS`() =
        runTest {
            coEvery { store.getAppliedIds() } returns emptySet()
            val m = migration(1)

            MigrationRunner(listOf(m), store).runPendingMigrations()

            coVerify { m.run() }
            verify { store.record(match { it.id == 1 && it.status == MigrationStatus.SUCCESS }) }
        }

    @Test
    fun `already-applied migration is skipped without calling isApplicable or run`() =
        runTest {
            coEvery { store.getAppliedIds() } returns setOf(1)
            val m = migration(1)

            MigrationRunner(listOf(m), store).runPendingMigrations()

            coVerify(exactly = 0) { m.isApplicable() }
            coVerify(exactly = 0) { m.run() }
            verify(exactly = 0) { store.record(any()) }
        }

    @Test
    fun `isApplicable returning false records SKIPPED without calling run`() =
        runTest {
            coEvery { store.getAppliedIds() } returns emptySet()
            val m = migration(1, applicable = false)

            MigrationRunner(listOf(m), store).runPendingMigrations()

            coVerify(exactly = 0) { m.run() }
            verify { store.record(match { it.id == 1 && it.status == MigrationStatus.SKIPPED }) }
        }

    @Test
    fun `run throwing records FAILED and continues to next migration`() =
        runTest {
            coEvery { store.getAppliedIds() } returns emptySet()
            val m1 =
                mockk<Migration> {
                    every { id } returns 1
                    coEvery { isApplicable() } returns true
                    coEvery { run() } throws RuntimeException("boom")
                }
            val m2 = migration(2)

            MigrationRunner(listOf(m1, m2), store).runPendingMigrations()

            verify { store.record(match { it.id == 1 && it.status == MigrationStatus.FAILED }) }
            coVerify { m2.run() }
            verify { store.record(match { it.id == 2 && it.status == MigrationStatus.SUCCESS }) }
        }

    @Test
    fun `run returning Failed records FAILED and continues to next migration`() =
        runTest {
            coEvery { store.getAppliedIds() } returns emptySet()
            val m1 = migration(1, result = MigrationResult.Failed(RuntimeException("failed")))
            val m2 = migration(2)

            MigrationRunner(listOf(m1, m2), store).runPendingMigrations()

            verify { store.record(match { it.id == 1 && it.status == MigrationStatus.FAILED }) }
            coVerify { m2.run() }
        }

    @Test
    fun `migrations run in ID order regardless of list order`() =
        runTest {
            coEvery { store.getAppliedIds() } returns emptySet()
            val m3 = migration(3)
            val m1 = migration(1)
            val m2 = migration(2)

            MigrationRunner(listOf(m3, m1, m2), store).runPendingMigrations()

            coVerifyOrder {
                m1.run()
                m2.run()
                m3.run()
            }
        }

    @Test
    fun `only pending migrations run when some are already applied`() =
        runTest {
            coEvery { store.getAppliedIds() } returns setOf(1, 3)
            val m1 = migration(1)
            val m2 = migration(2)
            val m3 = migration(3)

            MigrationRunner(listOf(m1, m2, m3), store).runPendingMigrations()

            coVerify(exactly = 0) { m1.run() }
            coVerify { m2.run() }
            coVerify(exactly = 0) { m3.run() }
        }
}
