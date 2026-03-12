package com.stytch.sdk.oauth

import platform.AuthenticationServices.ASWebAuthenticationPresentationContextProvidingProtocol

public actual class B2BOAuthDiscoveryStartParameters(
    public actual val discoveryRedirectUrl: String? = null,
    public actual val customScopes: List<String>? = null,
    public actual val providerParams: Map<String, String>? = null,
    public val oauthPresentationContextProvider: ASWebAuthenticationPresentationContextProvidingProtocol? = null,
) {
    public actual fun toOAuthStartParameters(): OAuthStartParameters =
        OAuthStartParameters(oauthPresentationContextProvider = oauthPresentationContextProvider)
}
