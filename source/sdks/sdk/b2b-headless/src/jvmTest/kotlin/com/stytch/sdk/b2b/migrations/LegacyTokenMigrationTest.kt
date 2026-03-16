package com.stytch.sdk.b2b.migrations

import com.stytch.sdk.b2b.StytchB2BAuthenticationStateManager.Companion.SESSION_IDENTIFIER
import com.stytch.sdk.b2b.StytchB2BAuthenticationStateManager.Companion.SESSION_TOKEN_IDENTIFIER
import com.stytch.sdk.data.KMPPlatformType
import com.stytch.sdk.data.StytchDispatchers
import com.stytch.sdk.encryption.StytchEncryptionClient
import com.stytch.sdk.migrations.ILegacyTokenReader
import com.stytch.sdk.migrations.MigrationResult
import com.stytch.sdk.migrations.PersistedLegacySessionData
import com.stytch.sdk.persistence.StytchPersistenceClient
import com.stytch.sdk.persistence.StytchPlatformPersistenceClient
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
internal class LegacyTokenMigrationTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private val dispatchers = StytchDispatchers(ioDispatcher = testDispatcher, mainDispatcher = testDispatcher)

    private val inMemoryStore = mutableMapOf<String, String>()
    private val platformClient =
        mockk<StytchPlatformPersistenceClient> {
            every { saveData(any(), any()) } answers { inMemoryStore[firstArg()] = secondArg() }
            every { getData(any()) } answers { inMemoryStore[firstArg()] }
            every { removeData(any()) } answers {
                inMemoryStore.remove(firstArg())
                Unit
            }
        }
    private val encryptionClient =
        mockk<StytchEncryptionClient> {
            every { encrypt(any()) } answers { firstArg() }
            every { decrypt(any()) } answers { firstArg() }
        }
    private val persistenceClient = StytchPersistenceClient(testDispatcher, encryptionClient, platformClient)

    private val tokenReader = mockk<ILegacyTokenReader>()

    private fun migration() =
        LegacyTokenMigration(
            publicToken = "public-token",
            platform = KMPPlatformType.IOS,
            tokenReader = tokenReader,
            persistenceClient = persistenceClient,
            dispatchers = dispatchers,
        )

    @BeforeTest
    fun setUp() {
        inMemoryStore.clear()
    }

    // --- isApplicable ---

    @Test
    fun `isApplicable returns true when token reader returns data`() =
        runTest(testDispatcher) {
            coEvery { tokenReader.getExistingSessionData(any(), any(), any(), any(), any()) } returns
                PersistedLegacySessionData(token = "tok", sessionDataString = null)

            val result = migration().isApplicable()

            assert(result)
        }

    @Test
    fun `isApplicable returns false when token reader returns null`() =
        runTest(testDispatcher) {
            coEvery { tokenReader.getExistingSessionData(any(), any(), any(), any(), any()) } returns null

            val result = migration().isApplicable()

            assert(!result)
        }

    @Test
    fun `isApplicable returns false when token reader throws`() =
        runTest(testDispatcher) {
            coEvery { tokenReader.getExistingSessionData(any(), any(), any(), any(), any()) } throws
                RuntimeException("read error")

            val result = migration().isApplicable()

            assert(!result)
        }

    // --- run ---

    @Test
    fun `run returns Skipped when called without isApplicable`() =
        runTest(testDispatcher) {
            val result = migration().run()

            assertIs<MigrationResult.Skipped>(result)
        }

    @Test
    fun `run saves session token and returns Success`() =
        runTest(testDispatcher) {
            coEvery { tokenReader.getExistingSessionData(any(), any(), any(), any(), any()) } returns
                PersistedLegacySessionData(token = "session-tok", sessionDataString = null)

            val m = migration()
            m.isApplicable()
            val result = m.run()

            assertIs<MigrationResult.Success>(result)
            verify { platformClient.saveData(SESSION_TOKEN_IDENTIFIER, any()) }
        }

    @Test
    fun `run saves only the token when session data string is null`() =
        runTest(testDispatcher) {
            coEvery { tokenReader.getExistingSessionData(any(), any(), any(), any(), any()) } returns
                PersistedLegacySessionData(token = "session-tok", sessionDataString = null)

            val m = migration()
            m.isApplicable()
            m.run()

            verify { platformClient.saveData(SESSION_TOKEN_IDENTIFIER, any()) }
            verify(exactly = 0) { platformClient.saveData(SESSION_IDENTIFIER, any()) }
        }

    @Test
    fun `run returns Success and saves token even when session data string is malformed JSON`() =
        runTest(testDispatcher) {
            coEvery { tokenReader.getExistingSessionData(any(), any(), any(), any(), any()) } returns
                PersistedLegacySessionData(token = "session-tok", sessionDataString = "not-valid-json{{{")

            val m = migration()
            m.isApplicable()
            val result = m.run()

            assertIs<MigrationResult.Success>(result)
            verify { platformClient.saveData(SESSION_TOKEN_IDENTIFIER, any()) }
        }
}
