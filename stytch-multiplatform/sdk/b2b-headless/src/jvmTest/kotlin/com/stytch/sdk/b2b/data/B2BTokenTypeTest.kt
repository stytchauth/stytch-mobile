package com.stytch.sdk.b2b.data

import kotlin.test.Test
import kotlin.test.assertEquals

internal class B2BTokenTypeTest {
    @Test
    fun `fromString maps multi_tenant_magic_links`() {
        assertEquals(B2BTokenType.MULTI_TENANT_MAGIC_LINKS, B2BTokenType.fromString("multi_tenant_magic_links"))
    }

    @Test
    fun `fromString maps multi_tenant_passwords`() {
        assertEquals(B2BTokenType.MULTI_TENANT_PASSWORDS, B2BTokenType.fromString("multi_tenant_passwords"))
    }

    @Test
    fun `fromString maps discovery`() {
        assertEquals(B2BTokenType.DISCOVERY, B2BTokenType.fromString("discovery"))
    }

    @Test
    fun `fromString maps sso`() {
        assertEquals(B2BTokenType.SSO, B2BTokenType.fromString("sso"))
    }

    @Test
    fun `fromString maps oauth`() {
        assertEquals(B2BTokenType.OAUTH, B2BTokenType.fromString("oauth"))
    }

    @Test
    fun `fromString maps discovery_oauth`() {
        assertEquals(B2BTokenType.DISCOVERY_OAUTH, B2BTokenType.fromString("discovery_oauth"))
    }

    @Test
    fun `fromString is case-insensitive`() {
        assertEquals(B2BTokenType.MULTI_TENANT_MAGIC_LINKS, B2BTokenType.fromString("MULTI_TENANT_MAGIC_LINKS"))
        assertEquals(B2BTokenType.SSO, B2BTokenType.fromString("SSO"))
        assertEquals(B2BTokenType.OAUTH, B2BTokenType.fromString("OAuth"))
    }

    @Test
    fun `fromString returns UNKNOWN for null`() {
        assertEquals(B2BTokenType.UNKNOWN, B2BTokenType.fromString(null))
    }

    @Test
    fun `fromString returns UNKNOWN for unrecognized string`() {
        assertEquals(B2BTokenType.UNKNOWN, B2BTokenType.fromString("something_else"))
    }
}
