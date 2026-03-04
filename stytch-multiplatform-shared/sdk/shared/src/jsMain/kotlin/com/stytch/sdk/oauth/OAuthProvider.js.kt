package com.stytch.sdk.oauth

import com.stytch.sdk.StytchBridge
import com.stytch.sdk.data.GoogleCredentialConfiguration
import com.stytch.sdk.data.PublicTokenInfo
import com.stytch.sdk.data.StytchDispatchers
import com.stytch.sdk.pkce.PKCEClient
import kotlinx.coroutines.await
import kotlinx.serialization.json.Json

public actual class OAuthProvider(
    private val googleCredentialConfiguration: GoogleCredentialConfiguration? = null,
) : IOAuthProvider {
    public actual override val isSupported: Boolean = true

    public actual override suspend fun getOAuthToken(
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
                    type = Json.encodeToString(type),
                    baseUrl = baseUrl,
                    publicToken = publicTokenInfo.publicToken,
                    googleCredentialConfiguration = Json.encodeToString(googleCredentialConfiguration),
                ).await()
        return Json.decodeFromString(result)
    }
}
