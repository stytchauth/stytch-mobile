package com.stytch.sdk.b2b.data

import kotlin.js.JsExport

/** The type of token present in a Stytch B2B deeplink URL. */
@JsExport
public enum class B2BTokenType {
    /** A B2B magic link authentication token. */
    MULTI_TENANT_MAGIC_LINKS,

    /** A B2B password reset token. Must be handled manually by the caller via the passwords reset flow. */
    MULTI_TENANT_PASSWORDS,

    /** A discovery flow token used to list organizations for an email address. */
    DISCOVERY,

    /** An SSO (SAML or OIDC) authentication token. */
    SSO,

    /** An OAuth authentication token for an org-scoped flow. */
    OAUTH,

    /**
     * A discovery OAuth token. Caller must present org selection and then call
     * [com.stytch.sdk.b2b.oauth.B2BOAuthDiscoveryClient.authenticate].
     */
    DISCOVERY_OAUTH,

    /** An unrecognized or missing token type. */
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
