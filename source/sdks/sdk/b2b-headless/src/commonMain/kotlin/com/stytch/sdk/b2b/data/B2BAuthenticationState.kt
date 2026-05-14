package com.stytch.sdk.b2b.data

import com.stytch.sdk.b2b.networking.models.ApiB2bSessionV1MemberSession
import com.stytch.sdk.b2b.networking.models.ApiOrganizationV1Member
import com.stytch.sdk.b2b.networking.models.ApiOrganizationV1Organization
import com.stytch.sdk.data.AuthenticationState
import kotlin.js.JsExport
import kotlin.js.JsName

/** Represents the current authentication state of a B2B member. */
@JsExport
@JsName("B2BAuthenticationState")
public sealed class B2BAuthenticationState : AuthenticationState {
    /** The initial state before the SDK has determined the authentication status. */
    public class Loading : B2BAuthenticationState()

    /** The member is not authenticated. */
    public class Unauthenticated : B2BAuthenticationState()

    /** The member is authenticated. */
    public class Authenticated(
        /** The authenticated member. */
        public val member: ApiOrganizationV1Member,
        /** The active member session. */
        public val memberSession: ApiB2bSessionV1MemberSession,
        /** The organization the member belongs to. */
        public val organization: ApiOrganizationV1Organization,
        /** The opaque session token. */
        public val sessionToken: String,
        /** The session JWT. */
        public val sessionJwt: String,
    ) : B2BAuthenticationState()

    /** An error during startup has occurred */
    public class Error(
        /** The underlying error that was thrown */
        public val exception: Throwable,
    ) : B2BAuthenticationState()
}
