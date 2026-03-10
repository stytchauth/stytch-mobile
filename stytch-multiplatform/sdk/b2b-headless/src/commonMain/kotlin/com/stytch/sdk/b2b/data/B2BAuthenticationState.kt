package com.stytch.sdk.b2b.data

import com.stytch.sdk.data.AuthenticationState
import kotlin.js.JsExport
import kotlin.js.JsName
import com.stytch.sdk.b2b.networking.models.ApiB2bSessionV1MemberSession
import com.stytch.sdk.b2b.networking.models.ApiOrganizationV1Member
import com.stytch.sdk.b2b.networking.models.ApiOrganizationV1Organization

@JsExport
@JsName("B2BAuthenticationState")
public sealed class B2BAuthenticationState : AuthenticationState {
    public class Loading : B2BAuthenticationState()

    public class Unauthenticated : B2BAuthenticationState()

    public class Authenticated(
        public val member: ApiOrganizationV1Member,
        public val memberSession: ApiB2bSessionV1MemberSession,
        public val organization: ApiOrganizationV1Organization,
        public val sessionToken: String,
        public val sessionJwt: String,
    ) : B2BAuthenticationState()
}
