package com.stytch.sdk.b2b.data

import com.stytch.sdk.b2b.networking.AuthenticatedResponse
import kotlin.js.JsExport

/** The result of processing a Stytch B2B deeplink via [com.stytch.sdk.b2b.StytchB2B.authenticate]. */
@JsExport
public sealed class DeeplinkAuthenticationStatus {
    /** The deeplink was successfully authenticated. */
    public class Authenticated(
        /** The authentication response from the Stytch API. */
        public val response: AuthenticatedResponse,
    ) : DeeplinkAuthenticationStatus()

    /**
     * The deeplink contains a token that requires manual handling by the caller.
     * For example, a password reset token or a discovery/discovery-OAuth token.
     */
    public class ManualHandlingRequired(
        /** The raw token extracted from the deeplink. */
        public val token: String,
    ) : DeeplinkAuthenticationStatus()

    /** The URL was not recognized as a Stytch B2B deeplink. */
    public class UnknownDeeplink(
        /** The original URL that was passed to authenticate. */
        public val url: String,
    ) : DeeplinkAuthenticationStatus()
}
