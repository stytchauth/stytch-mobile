package com.stytch.sdk.oauth

import platform.AuthenticationServices.ASWebAuthenticationPresentationContextProvidingProtocol

public actual class B2BSSOStartParameters(
    public actual val connectionId: String,
    public actual val loginRedirectUrl: String? = null,
    public actual val signupRedirectUrl: String? = null,
    public actual val sessionDurationMinutes: Int? = null,
    public val oauthPresentationContextProvider: ASWebAuthenticationPresentationContextProvidingProtocol? = null,
) {
    public constructor(connectionId: String) : this(connectionId, null, null, null, null)

    public constructor(
        connectionId: String,
        loginRedirectUrl: String,
        signupRedirectUrl: String,
    ) : this(connectionId, loginRedirectUrl, signupRedirectUrl, null, null)

    public constructor(
        connectionId: String,
        loginRedirectUrl: String,
        signupRedirectUrl: String,
        sessionDurationMinutes: Int,
    ) : this(connectionId, loginRedirectUrl, signupRedirectUrl, sessionDurationMinutes, null)

    public actual fun toOAuthStartParameters(): OAuthStartParameters =
        OAuthStartParameters(oauthPresentationContextProvider = oauthPresentationContextProvider)
}
