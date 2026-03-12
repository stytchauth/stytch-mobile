package com.stytch.sdk.data

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class GetPublicTokenInfoTest {
    @Test
    fun `returns isTestToken true for a test token`() {
        val info = getPublicTokenInfo("public-token-test-00000000-0000-0000-0000-000000000000")
        assertTrue(info.isTestToken)
        assertEquals("public-token-test-00000000-0000-0000-0000-000000000000", info.publicToken)
    }

    @Test
    fun `returns isTestToken false for a live token`() {
        val info = getPublicTokenInfo("public-token-live-00000000-0000-0000-0000-000000000000")
        assertFalse(info.isTestToken)
        assertEquals("public-token-live-00000000-0000-0000-0000-000000000000", info.publicToken)
    }

    @Test
    fun `throws for a token missing the test or live segment`() {
        assertFailsWith<IllegalArgumentException> {
            getPublicTokenInfo("public-token-00000000-0000-0000-0000-000000000000")
        }
    }

    @Test
    fun `throws for a completely invalid token`() {
        assertFailsWith<IllegalArgumentException> {
            getPublicTokenInfo("not-a-valid-token")
        }
    }

    @Test
    fun `throws for a token with an invalid UUID`() {
        assertFailsWith<IllegalArgumentException> {
            getPublicTokenInfo("public-token-test-not-a-uuid")
        }
    }
}
