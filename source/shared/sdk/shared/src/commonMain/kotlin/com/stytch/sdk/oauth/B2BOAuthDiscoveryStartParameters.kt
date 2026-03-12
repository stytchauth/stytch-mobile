package com.stytch.sdk.oauth

public expect class B2BOAuthDiscoveryStartParameters {
    public val discoveryRedirectUrl: String?
    public val customScopes: List<String>?
    public val providerParams: Map<String, String>?

    public fun toOAuthStartParameters(): OAuthStartParameters
}
