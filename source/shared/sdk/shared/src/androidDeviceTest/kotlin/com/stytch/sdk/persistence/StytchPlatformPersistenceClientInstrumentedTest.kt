package com.stytch.sdk.persistence

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals
import kotlin.test.assertNull

@RunWith(AndroidJUnit4::class)
internal class StytchPlatformPersistenceClientInstrumentedTest {
    private lateinit var client: StytchPlatformPersistenceClient

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        client = StytchPlatformPersistenceClient(context)
        client.reset()
    }

    @After
    fun tearDown() {
        client.reset()
    }

    @Test
    fun saveData_and_getData_round_trip_a_value() {
        client.saveData("key", "value")
        assertEquals("value", client.getData("key"))
    }

    @Test
    fun getData_returns_null_for_a_key_that_was_never_written() {
        assertNull(client.getData("nonexistent-key"))
    }

    @Test
    fun saveData_overwrites_an_existing_value() {
        client.saveData("key", "first")
        client.saveData("key", "second")
        assertEquals("second", client.getData("key"))
    }

    @Test
    fun removeData_deletes_the_key() {
        client.saveData("key", "value")
        client.removeData("key")
        assertNull(client.getData("key"))
    }

    @Test
    fun removeData_on_a_nonexistent_key_does_not_throw() {
        client.removeData("nonexistent-key")
    }

    @Test
    fun reset_clears_all_stored_keys() {
        client.saveData("a", "1")
        client.saveData("b", "2")
        client.saveData("c", "3")
        client.reset()
        assertNull(client.getData("a"))
        assertNull(client.getData("b"))
        assertNull(client.getData("c"))
    }

    @Test
    fun multiple_keys_are_stored_independently() {
        client.saveData("x", "foo")
        client.saveData("y", "bar")
        assertEquals("foo", client.getData("x"))
        assertEquals("bar", client.getData("y"))
    }
}
