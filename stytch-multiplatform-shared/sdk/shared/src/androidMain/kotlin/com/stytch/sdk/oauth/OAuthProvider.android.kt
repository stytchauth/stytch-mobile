package com.stytch.sdk.oauth

public actual class OAuthProvider {
    public actual val isSupported: Boolean = true

    public actual suspend fun getOAuthTokenFromUrl(url: String): String? {
        TODO()
    }
}
