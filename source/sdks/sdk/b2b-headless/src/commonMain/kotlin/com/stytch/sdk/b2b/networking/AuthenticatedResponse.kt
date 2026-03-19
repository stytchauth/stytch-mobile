package com.stytch.sdk.b2b.networking

import com.stytch.sdk.b2b.networking.models.ApiB2bSessionV1MemberSession
import com.stytch.sdk.b2b.networking.models.ApiOrganizationV1Member
import com.stytch.sdk.b2b.networking.models.ApiOrganizationV1Organization

/** Common fields returned in every successful B2B authentication response. */
public interface AuthenticatedResponse {
    /** The authenticated member. */
    public val member: ApiOrganizationV1Member

    /** The active member session. */
    public val memberSession: ApiB2bSessionV1MemberSession?

    /** The organization the member belongs to. */
    public val organization: ApiOrganizationV1Organization

    /** The opaque session token. */
    public val sessionToken: String

    /** The session JWT. */
    public val sessionJwt: String
}
