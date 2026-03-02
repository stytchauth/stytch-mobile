package com.stytch.sdk.oauth

import com.stytch.sdk.data.PublicTokenInfo
import com.stytch.sdk.data.StytchDispatchers
import com.stytch.sdk.pkce.PKCEClient
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
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

@JsExport
@Serializable
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
@Serializable
public sealed class OAuthResult {
    @Serializable public data class ClassicToken(
        val token: String,
    ) : OAuthResult()

    @Serializable public data class IDToken(
        val token: String,
        val nonce: String,
        val name: String? = null,
    ) : OAuthResult()

    @Serializable public data class Error(
        val error: @Contextual Throwable,
    ) : OAuthResult()
}
