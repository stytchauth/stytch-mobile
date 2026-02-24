package com.stytch.sdk.consumer.oauth

import com.stytch.sdk.consumer.networking.ConsumerNetworkingClient
import com.stytch.sdk.consumer.networking.models.IOAuthAttachParameters
import com.stytch.sdk.consumer.networking.models.IOAuthAuthenticateParameters
import com.stytch.sdk.consumer.networking.models.OAuthAttachResponse
import com.stytch.sdk.consumer.networking.models.OAuthAuthenticateResponse
import com.stytch.sdk.consumer.networking.models.toNetworkModel
import com.stytch.sdk.data.EndpointOptions
import com.stytch.sdk.data.PublicTokenInfo
import com.stytch.sdk.data.StytchDispatchers
import com.stytch.sdk.oauth.OAuthProvider
import com.stytch.sdk.oauth.OAuthStartParameters
import com.stytch.sdk.pkce.PKCEClient
import io.ktor.http.URLBuilder
import kotlinx.coroutines.withContext
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.js.JsExport

@JsExport
public interface OAuthClient {
    public suspend fun authenticate(request: IOAuthAuthenticateParameters): OAuthAuthenticateResponse

    public suspend fun attach(request: IOAuthAttachParameters): OAuthAttachResponse

    public val apple: OAuthType

    public val amazon: OAuthType

    public val bitbucket: OAuthType

    public val coinbase: OAuthType

    public val discord: OAuthType

    public val facebook: OAuthType

    public val figma: OAuthType

    public val github: OAuthType

    public val gitlab: OAuthType

    public val google: OAuthType

    public val linkedin: OAuthType

    public val microsoft: OAuthType

    public val salesforce: OAuthType

    public val slack: OAuthType

    public val snapchat: OAuthType

    public val tiktok: OAuthType

    public val twitch: OAuthType

    public val twitter: OAuthType

    public val yahoo: OAuthType
}

@JsExport
public interface OAuthType {
    public suspend fun start(startParameters: OAuthStartParameters): String?
}

internal class OAuthClientImpl(
    private val publicTokenInfo: PublicTokenInfo,
    private val endpointOptions: EndpointOptions,
    private val cnameDomain: () -> String?,
    private val dispatchers: StytchDispatchers,
    private val networkingClient: ConsumerNetworkingClient,
    private val pkceClient: PKCEClient,
    private val oauthProvider: OAuthProvider,
) : OAuthClient {
    override suspend fun authenticate(request: IOAuthAuthenticateParameters): OAuthAuthenticateResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                val codePair = pkceClient.retrieve() ?: throw IllegalStateException("PKCE is missing")
                networkingClient.api.oAuthAuthenticate(request.toNetworkModel(codeVerifier = codePair.verifier))
            }
        }

    override suspend fun attach(request: IOAuthAttachParameters): OAuthAttachResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.oAuthAttach(request.toNetworkModel())
            }
        }

    override val apple: OAuthType = OAuthTypeImpl("apple", ::start)
    override val amazon: OAuthType = OAuthTypeImpl("amazon", ::start)
    override val bitbucket: OAuthType = OAuthTypeImpl("bitbucket", ::start)
    override val coinbase: OAuthType = OAuthTypeImpl("coinbase", ::start)
    override val discord: OAuthType = OAuthTypeImpl("discord", ::start)
    override val facebook: OAuthType = OAuthTypeImpl("facebook", ::start)
    override val figma: OAuthType = OAuthTypeImpl("figma", ::start)
    override val github: OAuthType = OAuthTypeImpl("github", ::start)
    override val gitlab: OAuthType = OAuthTypeImpl("gitlab", ::start)
    override val google: OAuthType = OAuthTypeImpl("google", ::start)
    override val linkedin: OAuthType = OAuthTypeImpl("linkedin", ::start)
    override val microsoft: OAuthType = OAuthTypeImpl("microsoft", ::start)
    override val salesforce: OAuthType = OAuthTypeImpl("salesforce", ::start)
    override val slack: OAuthType = OAuthTypeImpl("slack", ::start)
    override val snapchat: OAuthType = OAuthTypeImpl("snapchat", ::start)
    override val tiktok: OAuthType = OAuthTypeImpl("tiktok", ::start)
    override val twitch: OAuthType = OAuthTypeImpl("twitch", ::start)
    override val twitter: OAuthType = OAuthTypeImpl("twitter", ::start)
    override val yahoo: OAuthType = OAuthTypeImpl("yahoo", ::start)

    private suspend fun start(
        providerName: String,
        startParameters: OAuthStartParameters,
    ) = withContext(dispatchers.ioDispatcher) {
        val codePair = pkceClient.create()
        val host = "https://${cnameDomain() ?: if (publicTokenInfo.isTestToken) endpointOptions.testDomain else endpointOptions.liveDomain}/v1/"
        val baseUrl = "${host}public/oauth/$providerName/start"
        val parameters =
            mutableMapOf(
                "public_token" to publicTokenInfo.publicToken,
                "code_challenge" to codePair.challenge,
                "login_redirect_url" to startParameters.loginRedirectUrl,
                "signup_redirect_url" to startParameters.signupRedirectUrl,
                "custom_scopes" to startParameters.customScopes?.joinToString(" "),
                "oauth_attach_token" to startParameters.oauthAttachToken,
            )
        startParameters.providerParams?.entries?.forEach { (key, value) ->
            parameters["provider_$key"] = value
        }
        val uri = URLBuilder(baseUrl)
        parameters.forEach { (key, value) ->
            if (value?.isNotEmpty() == true) {
                uri.parameters.append(key, value)
            }
        }
        oauthProvider.getOAuthTokenFromUrl(uri.buildString())
    }
}

internal class OAuthTypeImpl(
    private val providerName: String,
    private val handler: suspend (String, OAuthStartParameters) -> String?,
) : OAuthType {
    override suspend fun start(startParameters: OAuthStartParameters): String? = handler(providerName, startParameters)
}
