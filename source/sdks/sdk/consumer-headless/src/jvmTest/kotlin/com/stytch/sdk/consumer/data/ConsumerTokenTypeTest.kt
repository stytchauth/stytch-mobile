package com.stytch.sdk.consumer.data

import kotlin.test.Test
import kotlin.test.assertEquals

internal class ConsumerTokenTypeTest {
    @Test
    fun `fromString maps magic_links`() {
        assertEquals(ConsumerTokenType.MAGIC_LINKS, ConsumerTokenType.fromString("magic_links"))
    }

    @Test
    fun `fromString maps oauth`() {
        assertEquals(ConsumerTokenType.OAUTH, ConsumerTokenType.fromString("oauth"))
    }

    @Test
    fun `fromString maps reset_password`() {
        assertEquals(ConsumerTokenType.RESET_PASSWORD, ConsumerTokenType.fromString("reset_password"))
    }

    @Test
    fun `fromString is case-insensitive`() {
        assertEquals(ConsumerTokenType.MAGIC_LINKS, ConsumerTokenType.fromString("MAGIC_LINKS"))
        assertEquals(ConsumerTokenType.OAUTH, ConsumerTokenType.fromString("OAUTH"))
    }

    @Test
    fun `fromString returns UNKNOWN for null`() {
        assertEquals(ConsumerTokenType.UNKNOWN, ConsumerTokenType.fromString(null))
    }

    @Test
    fun `fromString returns UNKNOWN for unrecognized string`() {
        assertEquals(ConsumerTokenType.UNKNOWN, ConsumerTokenType.fromString("something_else"))
    }
}
