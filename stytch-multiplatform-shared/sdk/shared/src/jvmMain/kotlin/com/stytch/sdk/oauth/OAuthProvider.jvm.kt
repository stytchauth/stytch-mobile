package com.stytch.sdk.oauth

import com.stytch.sdk.data.PublicTokenInfo
import com.stytch.sdk.data.StytchDispatchers
import com.stytch.sdk.pkce.PKCEClient

public actual class OAuthProvider : IOAuthProvider {
    public actual override val isSupported: Boolean = false

    public actual override suspend fun getOAuthToken(
        parameters: OAuthStartParameters,
        pkceClient: PKCEClient,
        dispatchers: StytchDispatchers,
        type: OAuthProviderType,
        baseUrl: String,
        publicTokenInfo: PublicTokenInfo,
    ): OAuthResult = throw OAuthUnsupportedError()

    public actual override suspend fun startBrowserFlow(
        url: String,
        parameters: OAuthStartParameters,
        dispatchers: StytchDispatchers,
    ): OAuthResult = throw OAuthUnsupportedError()
}
