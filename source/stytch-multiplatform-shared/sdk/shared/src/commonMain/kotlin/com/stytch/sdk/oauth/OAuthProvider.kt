package com.stytch.sdk.oauth

import com.stytch.sdk.data.PublicTokenInfo
import com.stytch.sdk.data.StytchDispatchers
import com.stytch.sdk.pkce.PKCEClient
import kotlinx.serialization.Serializable

public interface IOAuthProvider {
    public val isSupported: Boolean

    public suspend fun getOAuthToken(
        parameters: OAuthStartParameters,
        pkceClient: PKCEClient,
        dispatchers: StytchDispatchers,
        type: OAuthProviderType,
        baseUrl: String,
        publicTokenInfo: PublicTokenInfo,
    ): OAuthResult

    public suspend fun startBrowserFlow(
        url: String,
        parameters: OAuthStartParameters,
        dispatchers: StytchDispatchers,
    ): OAuthResult
}

public expect class OAuthProvider : IOAuthProvider {
    public override val isSupported: Boolean

    public override suspend fun getOAuthToken(
        parameters: OAuthStartParameters,
        pkceClient: PKCEClient,
        dispatchers: StytchDispatchers,
        type: OAuthProviderType,
        baseUrl: String,
        publicTokenInfo: PublicTokenInfo,
    ): OAuthResult

    public override suspend fun startBrowserFlow(
        url: String,
        parameters: OAuthStartParameters,
        dispatchers: StytchDispatchers,
    ): OAuthResult
}

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
        val message: String,
    ) : OAuthResult()
}
