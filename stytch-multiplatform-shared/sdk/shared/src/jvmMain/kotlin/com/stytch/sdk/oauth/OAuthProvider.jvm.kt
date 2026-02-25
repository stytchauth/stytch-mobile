package com.stytch.sdk.oauth

public actual class OAuthProvider {
    public actual val isSupported: Boolean = false

    public actual suspend fun getOAuthTokenFromUrl(url: String): String? = throw OAuthUnsupportedError()
}
