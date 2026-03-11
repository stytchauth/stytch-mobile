package com.stytch.sdk.oauth

import platform.AuthenticationServices.ASWebAuthenticationPresentationContextProvidingProtocol

public actual class B2BSSOStartParameters(
    public actual val connectionId: String,
    public actual val loginRedirectUrl: String? = null,
    public actual val signupRedirectUrl: String? = null,
    public actual val sessionDurationMinutes: Int? = null,
    public val oauthPresentationContextProvider: ASWebAuthenticationPresentationContextProvidingProtocol? = null,
) {
    public actual fun toOAuthStartParameters(): OAuthStartParameters =
        OAuthStartParameters(oauthPresentationContextProvider = oauthPresentationContextProvider)
}
