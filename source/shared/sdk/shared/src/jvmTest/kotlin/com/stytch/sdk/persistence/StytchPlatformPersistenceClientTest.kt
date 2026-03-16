package com.stytch.sdk.persistence

import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

// Uses a dedicated class reference so the test gets its own isolated prefs node,
// separate from any production data or other test suites.
internal class StytchPlatformPersistenceClientTest {
    private val client = StytchPlatformPersistenceClient(StytchPlatformPersistenceClientTest::class.java)

    @BeforeTest
    fun setUp() {
        client.reset()
    }

    @AfterTest
    fun tearDown() {
        client.reset()
    }

    @Test
    fun `saveData and getData round-trip a value`() {
        client.saveData("key", "value")
        assertEquals("value", client.getData("key"))
    }

    @Test
    fun `getData returns null for a key that was never written`() {
        assertNull(client.getData("nonexistent-key"))
    }

    @Test
    fun `saveData overwrites an existing value`() {
        client.saveData("key", "first")
        client.saveData("key", "second")
        assertEquals("second", client.getData("key"))
    }

    @Test
    fun `removeData deletes the key`() {
        client.saveData("key", "value")
        client.removeData("key")
        assertNull(client.getData("key"))
    }

    @Test
    fun `removeData on a nonexistent key does not throw`() {
        client.removeData("nonexistent-key")
    }

    @Test
    fun `reset clears all stored keys`() {
        client.saveData("a", "1")
        client.saveData("b", "2")
        client.saveData("c", "3")
        client.reset()
        assertNull(client.getData("a"))
        assertNull(client.getData("b"))
        assertNull(client.getData("c"))
    }

    @Test
    fun `multiple keys are stored independently`() {
        client.saveData("x", "foo")
        client.saveData("y", "bar")
        assertEquals("foo", client.getData("x"))
        assertEquals("bar", client.getData("y"))
    }
}
