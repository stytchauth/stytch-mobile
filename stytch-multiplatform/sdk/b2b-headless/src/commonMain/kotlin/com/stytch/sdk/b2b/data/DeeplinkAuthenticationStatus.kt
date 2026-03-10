package com.stytch.sdk.b2b.data

import com.stytch.sdk.b2b.networking.AuthenticatedResponse
import kotlin.js.JsExport

@JsExport
public sealed class DeeplinkAuthenticationStatus {
    public class Authenticated(
        response: AuthenticatedResponse,
    ) : DeeplinkAuthenticationStatus()

    public class ManualHandlingRequired(
        token: String,
    ) : DeeplinkAuthenticationStatus()

    public class UnknownDeeplink(
        url: String,
    ) : DeeplinkAuthenticationStatus()
}
