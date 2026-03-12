package com.stytch.sdk.oauth

import com.stytch.sdk.data.PublicTokenInfo
import com.stytch.sdk.pkce.PKCEClient
import io.ktor.http.URLBuilder

internal suspend fun generateOAuthStartUrl(
    packageName: String,
    baseUrl: String,
    publicTokenInfo: PublicTokenInfo,
    parameters: OAuthStartParameters,
    pkceClient: PKCEClient,
): String {
    val codePair = pkceClient.create()
    val finalParameters =
        mutableMapOf(
            "public_token" to publicTokenInfo.publicToken,
            "code_challenge" to codePair.challenge,
            "login_redirect_url" to parameters.loginRedirectUrl?.takeIf { it.isNotEmpty() }?.let { "$packageName://oauth?url=$it" },
            "signup_redirect_url" to parameters.signupRedirectUrl?.takeIf { it.isNotEmpty() }?.let { "$packageName://oauth?url=$it" },
            "custom_scopes" to parameters.customScopes?.joinToString(" "),
            "oauth_attach_token" to parameters.oauthAttachToken,
        )
    parameters.providerParams?.entries?.forEach { (key, value) ->
        finalParameters["provider_$key"] = value
    }
    val uri = URLBuilder(baseUrl)
    finalParameters.forEach { (key, value) ->
        if (value?.isNotEmpty() == true) {
            uri.parameters.append(key, value)
        }
    }
    return uri.build().toString()
}
