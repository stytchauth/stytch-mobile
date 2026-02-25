package com.stytch.sdk.oauth

public actual class OAuthStartParameters(
    public actual val loginRedirectUrl: String? = null,
    public actual val signupRedirectUrl: String? = null,
    public actual val customScopes: List<String>? = null,
    public actual val providerParams: Map<String, String>? = null,
    public actual val oauthAttachToken: String? = null,
)
