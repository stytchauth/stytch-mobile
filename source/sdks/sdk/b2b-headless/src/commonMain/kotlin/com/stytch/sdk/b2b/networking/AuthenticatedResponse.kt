package com.stytch.sdk.b2b.networking

import com.stytch.sdk.b2b.networking.models.ApiB2bSessionV1MemberSession
import com.stytch.sdk.b2b.networking.models.ApiOrganizationV1Member
import com.stytch.sdk.b2b.networking.models.ApiOrganizationV1Organization

public interface AuthenticatedResponse {
    public val member: ApiOrganizationV1Member
    public val memberSession: ApiB2bSessionV1MemberSession
    public val organization: ApiOrganizationV1Organization
    public val sessionToken: String
    public val sessionJwt: String
}
