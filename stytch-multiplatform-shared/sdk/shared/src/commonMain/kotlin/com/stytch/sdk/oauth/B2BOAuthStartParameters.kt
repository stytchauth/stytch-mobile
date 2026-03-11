package com.stytch.sdk.oauth

public expect class B2BOAuthStartParameters {
    public val loginRedirectUrl: String?
    public val signupRedirectUrl: String?
    public val organizationId: String?
    public val organizationSlug: String?
    public val customScopes: List<String>?
    public val providerParams: Map<String, String>?
    public val sessionDurationMinutes: Int?

    public fun toOAuthStartParameters(): OAuthStartParameters
}
