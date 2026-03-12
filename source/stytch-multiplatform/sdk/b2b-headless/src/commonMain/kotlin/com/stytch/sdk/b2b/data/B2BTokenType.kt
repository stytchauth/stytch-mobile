package com.stytch.sdk.b2b.data

import kotlin.js.JsExport

@JsExport
public enum class B2BTokenType {
    MULTI_TENANT_MAGIC_LINKS,
    MULTI_TENANT_PASSWORDS,
    DISCOVERY,
    SSO,
    OAUTH,
    DISCOVERY_OAUTH,
    UNKNOWN,
    ;

    internal companion object {
        fun fromString(typeString: String?): B2BTokenType =
            try {
                B2BTokenType.valueOf(typeString?.uppercase()!!)
            } catch (_: Exception) {
                UNKNOWN
            }
    }
}
