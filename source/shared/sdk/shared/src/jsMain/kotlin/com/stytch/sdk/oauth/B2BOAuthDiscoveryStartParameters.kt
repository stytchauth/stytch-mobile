package com.stytch.sdk.oauth

@JsExport
public actual class B2BOAuthDiscoveryStartParameters(
    public actual val discoveryRedirectUrl: String? = null,
    public actual val customScopes: List<String>? = null,
    public actual val providerParams: Map<String, String>? = null,
) {
    public actual fun toOAuthStartParameters(): OAuthStartParameters = OAuthStartParameters()
}
