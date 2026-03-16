package com.stytch.sdk.oauth

public expect class OAuthStartParameters {
    public val loginRedirectUrl: String?
    public val signupRedirectUrl: String?
    public val customScopes: List<String>?
    public val providerParams: Map<String, String>?
    public val oauthAttachToken: String?
    public val sessionDurationMinutes: Int?
}
