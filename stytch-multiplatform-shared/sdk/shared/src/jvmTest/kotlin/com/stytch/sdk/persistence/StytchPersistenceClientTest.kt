package com.stytch.sdk.persistence

import com.stytch.sdk.encryption.StytchEncryptionClient
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.Serializable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class StytchPersistenceClientTest {
    private val dispatcher = UnconfinedTestDispatcher()
    private val encryptionClient = mockk<StytchEncryptionClient>()

    // In-memory platform client so roundtrip tests don't need complex mock setup
    private val inMemoryStore = mutableMapOf<String, String>()
    private val platformClient = mockk<StytchPlatformPersistenceClient> {
        every { saveData(any(), any()) } answers { inMemoryStore[firstArg()] = secondArg() }
        every { getData(any()) } answers { inMemoryStore[firstArg()] }
        every { removeData(any()) } answers { inMemoryStore.remove(firstArg()); Unit }
    }

    private val client = StytchPersistenceClient(dispatcher, encryptionClient, platformClient)

    @Serializable
    private data class Payload(val value: String)

    // --- save ---

    @Test
    fun `save writes encrypted base64-encoded value to platform client`() = runTest(dispatcher) {
        every { encryptionClient.encrypt(any()) } answers { firstArg() }

        client.save("key", Payload("hello"))

        verify { platformClient.saveData("key", any()) }
    }

    @Test
    fun `save with null removes the key`() = runTest(dispatcher) {
        client.save<Payload>("key", null)

        verify { platformClient.removeData("key") }
        verify(exactly = 0) { platformClient.saveData(any(), any()) }
    }

    // --- get ---

    @Test
    fun `get returns default when key is absent`() = runTest(dispatcher) {
        val result = client.get<Payload>("missing", null)

        assertNull(result)
    }

    @Test
    fun `get returns default and removes key when decryption throws`() = runTest(dispatcher) {
        every { encryptionClient.encrypt(any()) } answers { firstArg() }
        every { encryptionClient.decrypt(any()) } throws RuntimeException("decryption failed")

        client.save("key", Payload("hello"))
        val result = client.get<Payload>("key", null)

        assertNull(result)
        verify { platformClient.removeData("key") }
    }

    @Test
    fun `get returns default and removes key when decrypted bytes are not valid JSON`() = runTest(dispatcher) {
        every { encryptionClient.encrypt(any()) } answers { firstArg() }
        every { encryptionClient.decrypt(any()) } returns "not-valid-json".toByteArray()

        client.save("key", Payload("hello"))
        val result = client.get<Payload>("key", null)

        assertNull(result)
        verify { platformClient.removeData("key") }
    }

    // --- remove ---

    @Test
    fun `remove delegates to platform client`() = runTest(dispatcher) {
        client.remove("key")

        verify { platformClient.removeData("key") }
    }

    // --- roundtrip ---

    @Test
    fun `save then get returns original value`() = runTest(dispatcher) {
        every { encryptionClient.encrypt(any()) } answers { firstArg() }
        every { encryptionClient.decrypt(any()) } answers { firstArg() }

        val original = Payload("test-value")
        client.save("k", original)

        assertEquals(original, client.get<Payload>("k", null))
    }

    @Test
    fun `save null after save removes key so get returns default`() = runTest(dispatcher) {
        every { encryptionClient.encrypt(any()) } answers { firstArg() }
        every { encryptionClient.decrypt(any()) } answers { firstArg() }

        client.save("k", Payload("value"))
        client.save<Payload>("k", null)

        assertNull(client.get<Payload>("k", null))
    }
}
