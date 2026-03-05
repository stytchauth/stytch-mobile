package com.stytch.sdk.oauth

import platform.AuthenticationServices.ASAuthorizationControllerPresentationContextProvidingProtocol
import platform.AuthenticationServices.ASWebAuthenticationPresentationContextProvidingProtocol

public actual class OAuthStartParameters(
    public actual val loginRedirectUrl: String? = null,
    public actual val signupRedirectUrl: String? = null,
    public actual val customScopes: List<String>? = null,
    public actual val providerParams: Map<String, String>? = null,
    public actual val oauthAttachToken: String? = null,
    public actual val sessionDurationMinutes: Int? = null,
    public val applePresentationContextProvider: ASAuthorizationControllerPresentationContextProvidingProtocol? = null,
    public val oauthPresentationContextProvider: ASWebAuthenticationPresentationContextProvidingProtocol? = null,
) {
    public constructor(sessionDurationMinutes: Int) : this(null, null, null, null, null, sessionDurationMinutes, null, null)
    public constructor(
        loginRedirectUrl: String,
    ) : this(loginRedirectUrl, null, null, null, null, null, null, null)

    public constructor(
        loginRedirectUrl: String,
        signupRedirectUrl: String,
    ) : this(loginRedirectUrl, signupRedirectUrl, null, null, null, null, null, null)

    public constructor(
        loginRedirectUrl: String,
        signupRedirectUrl: String,
        sessionDurationMinutes: Int,
    ) : this(loginRedirectUrl, signupRedirectUrl, null, null, null, sessionDurationMinutes, null, null)

    public constructor(
        loginRedirectUrl: String,
        signupRedirectUrl: String,
        customScopes: List<String>,
    ) : this(loginRedirectUrl, signupRedirectUrl, customScopes, null, null, null, null, null)

    public constructor(
        loginRedirectUrl: String,
        signupRedirectUrl: String,
        customScopes: List<String>,
        providerParams: Map<String, String>,
    ) : this(loginRedirectUrl, signupRedirectUrl, customScopes, providerParams, null, null, null, null)

    public constructor(
        loginRedirectUrl: String,
        signupRedirectUrl: String,
        customScopes: List<String>,
        providerParams: Map<String, String>,
        oauthAttachToken: String,
    ) : this(loginRedirectUrl, signupRedirectUrl, customScopes, providerParams, oauthAttachToken, null, null, null)

    public constructor(
        loginRedirectUrl: String,
        signupRedirectUrl: String,
        customScopes: List<String>,
        providerParams: Map<String, String>,
        oauthAttachToken: String,
        sessionDurationMinutes: Int,
    ) : this(loginRedirectUrl, signupRedirectUrl, customScopes, providerParams, oauthAttachToken, sessionDurationMinutes, null, null)
}
