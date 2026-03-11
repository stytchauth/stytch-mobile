package com.stytch.sdk.oauth

public actual class B2BOAuthStartParameters(
    public actual val loginRedirectUrl: String? = null,
    public actual val signupRedirectUrl: String? = null,
    public actual val organizationId: String? = null,
    public actual val organizationSlug: String? = null,
    public actual val customScopes: List<String>? = null,
    public actual val providerParams: Map<String, String>? = null,
    public actual val sessionDurationMinutes: Int? = null,
) {
    public actual fun toOAuthStartParameters(): OAuthStartParameters = OAuthStartParameters()
}
