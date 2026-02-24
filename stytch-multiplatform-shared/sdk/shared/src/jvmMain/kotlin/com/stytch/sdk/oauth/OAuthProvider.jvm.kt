package com.stytch.sdk.oauth

import com.stytch.sdk.data.StytchDispatchers
import com.stytch.sdk.pkce.PKCEClient

public actual class OAuthProvider {
    public actual val isSupported: Boolean = false

    public actual suspend fun getOAuthTokenFromUrl(
        pkceClient: PKCEClient,
        dispatchers: StytchDispatchers,
        type: OAuthProviderType,
        url: String,
    ): OAuthResult = throw OAuthUnsupportedError()
}
