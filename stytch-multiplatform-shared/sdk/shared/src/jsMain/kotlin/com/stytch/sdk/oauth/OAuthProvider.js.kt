package com.stytch.sdk.oauth

import com.stytch.sdk.StytchBridge
import com.stytch.sdk.data.PublicTokenInfo
import com.stytch.sdk.data.StytchDispatchers
import com.stytch.sdk.pkce.PKCEClient
import kotlinx.coroutines.await
import kotlinx.serialization.json.Json

public actual class OAuthProvider {
    public actual val isSupported: Boolean = true

    public actual suspend fun getOAuthToken(
        parameters: OAuthStartParameters,
        pkceClient: PKCEClient,
        dispatchers: StytchDispatchers,
        type: OAuthProviderType,
        baseUrl: String,
        publicTokenInfo: PublicTokenInfo,
    ): OAuthResult {
        val result =
            StytchBridge
                .getOAuthToken(
                    loginRedirectUrl = parameters.loginRedirectUrl,
                    signupRedirectUrl = parameters.signupRedirectUrl,
                    customScopes = parameters.customScopes,
                    providerParams = parameters.providerParams?.map { "${it.key}=${it.value}" }?.joinToString("&"),
                    oauthAttachToken = parameters.oauthAttachToken,
                    sessionDurationMinutes = parameters.sessionDurationMinutes,
                    type = type.hostName,
                    baseUrl = baseUrl,
                    publicToken = publicTokenInfo.publicToken,
                ).await()
        return Json.decodeFromString(result)
    }
}
