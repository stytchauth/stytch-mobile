package com.stytch.sdk.oauth

import com.stytch.sdk.data.PublicTokenInfo
import com.stytch.sdk.data.StytchDispatchers
import com.stytch.sdk.pkce.PKCEClient
import kotlin.js.JsExport

public expect class OAuthProvider {
    public val isSupported: Boolean

    public suspend fun getOAuthToken(
        parameters: OAuthStartParameters,
        pkceClient: PKCEClient,
        dispatchers: StytchDispatchers,
        type: OAuthProviderType,
        baseUrl: String,
        publicTokenInfo: PublicTokenInfo,
    ): OAuthResult
}

public enum class OAuthProviderType(
    public val hostName: String,
) {
    APPLE("apple"),
    AMAZON("amazon"),
    BITBUCKET("bitbucket"),
    COINBASE("coinbase"),
    DISCORD("discord"),
    FACEBOOK("facebook"),
    FIGMA("figma"),
    GITHUB("github"),
    GITLAB("gitlab"),
    GOOGLE("google"),
    LINKEDIN("linkedin"),
    MICROSOFT("microsoft"),
    SALESFORCE("salesforce"),
    SLACK("slack"),
    SNAPCHAT("snapchat"),
    TIKTOK("tiktok"),
    TWITCH("twitch"),
    TWITTER("twitter"),
    YAHOO("yahoo"),
}

@JsExport
public sealed class OAuthResult {
    public data class ClassicToken(
        val token: String,
    ) : OAuthResult()

    public data class IDToken(
        val token: String,
        val nonce: String,
        val name: String? = null,
    ) : OAuthResult()

    public data class Error(
        val error: Throwable,
    ) : OAuthResult()
}
