package com.stytch.sdk.oauth

import platform.AuthenticationServices.ASWebAuthenticationPresentationContextProvidingProtocol

public actual class OAuthStartParameters(
    public val presentationContextProvider: ASWebAuthenticationPresentationContextProvidingProtocol? = null,
    public actual val loginRedirectUrl: String? = null,
    public actual val signupRedirectUrl: String? = null,
    public actual val customScopes: List<String>? = null,
    public actual val providerParams: Map<String, String>? = null,
    public actual val oauthAttachToken: String? = null,
    public actual val sessionDurationMinutes: Int? = null,
)
