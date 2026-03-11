package com.stytch.sdk.oauth

public expect class B2BSSOStartParameters {
    public val connectionId: String
    public val loginRedirectUrl: String?
    public val signupRedirectUrl: String?
    public val sessionDurationMinutes: Int?

    public fun toOAuthStartParameters(): OAuthStartParameters
}
