package com.stytch.sdk.oauth

public expect class OAuthProvider {
    public val isSupported: Boolean

    public suspend fun getOAuthTokenFromUrl(url: String): String?
}
