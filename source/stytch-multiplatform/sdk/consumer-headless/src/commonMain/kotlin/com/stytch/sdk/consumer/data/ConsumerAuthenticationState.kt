package com.stytch.sdk.consumer.data

import com.stytch.sdk.data.AuthenticationState
import kotlin.js.JsExport
import kotlin.js.JsName
import com.stytch.sdk.consumer.networking.models.ApiSessionV1Session as Session
import com.stytch.sdk.consumer.networking.models.ApiUserV1User as User

@JsExport
@JsName("ConsumerAuthenticationState")
public sealed class ConsumerAuthenticationState : AuthenticationState {
    public class Loading : ConsumerAuthenticationState()

    public class Unauthenticated : ConsumerAuthenticationState()

    public class Authenticated(
        public val user: User,
        public val session: Session,
        public val sessionToken: String,
        public val sessionJwt: String,
    ) : ConsumerAuthenticationState()
}
