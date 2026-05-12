package com.stytch.sdk.consumer.data

import com.stytch.sdk.data.AuthenticationState
import kotlin.js.JsExport
import kotlin.js.JsName
import com.stytch.sdk.consumer.networking.models.ApiSessionV1Session as Session
import com.stytch.sdk.consumer.networking.models.ApiUserV1User as User

/** Represents the current authentication state of a consumer user. */
@JsExport
@JsName("ConsumerAuthenticationState")
public sealed class ConsumerAuthenticationState : AuthenticationState {
    /** The initial state before the SDK has determined the authentication status. */
    public class Loading : ConsumerAuthenticationState()

    /** The user is not authenticated. */
    public class Unauthenticated : ConsumerAuthenticationState()

    /** The user is authenticated. */
    public class Authenticated(
        /** The authenticated user. */
        public val user: User,
        /** The active session. */
        public val session: Session,
        /** The opaque session token. */
        public val sessionToken: String,
        /** The session JWT. */
        public val sessionJwt: String,
    ) : ConsumerAuthenticationState()

    public class Error(
        public val exception: Throwable,
    ) : ConsumerAuthenticationState()
}
