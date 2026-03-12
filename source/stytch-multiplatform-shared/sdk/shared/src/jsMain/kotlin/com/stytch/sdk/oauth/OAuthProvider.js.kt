package com.stytch.sdk.oauth

import com.stytch.sdk.StytchBridge
import com.stytch.sdk.data.GoogleCredentialConfiguration
import com.stytch.sdk.data.PublicTokenInfo
import com.stytch.sdk.data.StytchDispatchers
import com.stytch.sdk.pkce.PKCEClient
import kotlinx.coroutines.await
import kotlinx.serialization.json.Json

public actual class OAuthProvider(
    private val packageName: String,
    private val googleCredentialConfiguration: GoogleCredentialConfiguration? = null,
) : IOAuthProvider {
    public actual override val isSupported: Boolean = true

    public actual override suspend fun startBrowserFlow(
        url: String,
        parameters: OAuthStartParameters,
        dispatchers: StytchDispatchers,
    ): OAuthResult =
        try {
            val result = StytchBridge.startBrowserFlow(url).await()
            Json.decodeFromString(result)
        } catch (e: Throwable) {
            OAuthResult.Error(e.message ?: e.toString())
        }

    public actual override suspend fun getOAuthToken(
        parameters: OAuthStartParameters,
        pkceClient: PKCEClient,
        dispatchers: StytchDispatchers,
        type: OAuthProviderType,
        baseUrl: String,
        publicTokenInfo: PublicTokenInfo,
    ): OAuthResult =
        try {
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
                        googleCredentialConfiguration =
                            googleCredentialConfiguration?.let {
                                """{"googleClientId":"${it.googleClientId}","autoSelectEnabled":${it.autoSelectEnabled}}"""
                            },
                    ).await()
            Json.decodeFromString(result)
        } catch (e: Throwable) {
            e.printStackTrace()
            OAuthResult.Error(e.message ?: e.toString())
        }
}
