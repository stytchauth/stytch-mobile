package com.stytch.sdk.oauth

import platform.AuthenticationServices.ASWebAuthenticationPresentationContextProvidingProtocol

public actual class B2BOAuthStartParameters(
    public actual val loginRedirectUrl: String? = null,
    public actual val signupRedirectUrl: String? = null,
    public actual val organizationId: String? = null,
    public actual val organizationSlug: String? = null,
    public actual val customScopes: List<String>? = null,
    public actual val providerParams: Map<String, String>? = null,
    public actual val sessionDurationMinutes: Int? = null,
    public val oauthPresentationContextProvider: ASWebAuthenticationPresentationContextProvidingProtocol? = null,
) {
    public constructor() : this(null, null, null, null, null, null, null, null)

    public constructor(
        loginRedirectUrl: String,
        signupRedirectUrl: String,
    ) : this(loginRedirectUrl, signupRedirectUrl, null, null, null, null, null, null)

    public constructor(
        loginRedirectUrl: String,
        signupRedirectUrl: String,
        organizationId: String,
    ) : this(loginRedirectUrl, signupRedirectUrl, organizationId, null, null, null, null, null)

    public constructor(
        loginRedirectUrl: String,
        signupRedirectUrl: String,
        organizationId: String,
        sessionDurationMinutes: Int,
    ) : this(loginRedirectUrl, signupRedirectUrl, organizationId, null, null, null, sessionDurationMinutes, null)

    public actual fun toOAuthStartParameters(): OAuthStartParameters =
        OAuthStartParameters(oauthPresentationContextProvider = oauthPresentationContextProvider)
}
