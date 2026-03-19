package com.stytch.sdk.b2b.networking

import com.stytch.sdk.consumer.networking.models.ApiB2bMfaV1MfaRequired

/**
 * Common optional fields present on B2B API responses.
 *
 * These fields are present when additional authentication steps are required before a session
 * can be established (e.g. MFA enrollment or org selection during discovery).
 */
public interface B2BResponse {
    /** Present when the user must complete an MFA step before the session is fully established. */
    public val mfaRequired: ApiB2bMfaV1MfaRequired?
        get() = null

    /**
     * A short-lived token that carries the partially-authenticated identity across steps
     * (e.g. from SSO/OAuth discovery to organization selection).
     */
    public val intermediateSessionToken: String?
        get() = null
}
