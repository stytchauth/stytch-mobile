package com.stytch.sdk.b2b.data

import com.stytch.sdk.b2b.networking.AuthenticatedResponse
import kotlin.js.JsExport

@JsExport
public sealed class DeeplinkAuthenticationStatus {
    public class Authenticated(
        public val response: AuthenticatedResponse,
    ) : DeeplinkAuthenticationStatus()

    public class ManualHandlingRequired(
        public val token: String,
    ) : DeeplinkAuthenticationStatus()

    public class UnknownDeeplink(
        public val url: String,
    ) : DeeplinkAuthenticationStatus()
}
