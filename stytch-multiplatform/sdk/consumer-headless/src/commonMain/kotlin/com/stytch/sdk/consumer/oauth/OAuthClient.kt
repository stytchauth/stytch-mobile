package com.stytch.sdk.consumer.oauth

import com.stytch.sdk.consumer.networking.AuthenticatedResponse
import com.stytch.sdk.consumer.networking.ConsumerNetworkingClient
import com.stytch.sdk.consumer.networking.models.ApiUserV1Name
import com.stytch.sdk.consumer.networking.models.IOAuthAppleIDTokenAuthenticateParameters
import com.stytch.sdk.consumer.networking.models.IOAuthAttachParameters
import com.stytch.sdk.consumer.networking.models.IOAuthAuthenticateParameters
import com.stytch.sdk.consumer.networking.models.IOAuthGoogleIDTokenAuthenticateParameters
import com.stytch.sdk.consumer.networking.models.OAuthAppleIDTokenAuthenticateParameters
import com.stytch.sdk.consumer.networking.models.OAuthAttachResponse
import com.stytch.sdk.consumer.networking.models.OAuthAuthenticateParameters
import com.stytch.sdk.consumer.networking.models.OAuthAuthenticateResponse
import com.stytch.sdk.consumer.networking.models.OAuthGoogleIDTokenAuthenticateParameters
import com.stytch.sdk.consumer.networking.models.OAuthGoogleIDTokenAuthenticateResponse
import com.stytch.sdk.consumer.networking.models.toNetworkModel
import com.stytch.sdk.data.EndpointOptions
import com.stytch.sdk.data.PublicTokenInfo
import com.stytch.sdk.data.StytchDispatchers
import com.stytch.sdk.oauth.IOAuthProvider
import com.stytch.sdk.oauth.OAuthProviderType
import com.stytch.sdk.oauth.OAuthResult
import com.stytch.sdk.oauth.OAuthStartParameters
import com.stytch.sdk.pkce.PKCEClient
import kotlinx.coroutines.withContext
import kotlin.js.JsExport

@JsExport
public interface OAuthClient {
    public suspend fun authenticate(request: IOAuthAuthenticateParameters): OAuthAuthenticateResponse

    public suspend fun authenticateGoogleIdToken(request: IOAuthGoogleIDTokenAuthenticateParameters): OAuthGoogleIDTokenAuthenticateResponse

    public suspend fun authenticateAppleIdToken(request: IOAuthAppleIDTokenAuthenticateParameters): OAuthGoogleIDTokenAuthenticateResponse

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
    public suspend fun start(startParameters: OAuthStartParameters): AuthenticatedResponse
}

internal class OAuthClientImpl(
    private val publicTokenInfo: PublicTokenInfo,
    private val endpointOptions: EndpointOptions,
    private val cnameDomain: () -> String?,
    private val dispatchers: StytchDispatchers,
    private val networkingClient: ConsumerNetworkingClient,
    private val pkceClient: PKCEClient,
    private val oauthProvider: IOAuthProvider,
    private val defaultSessionDuration: Int,
) : OAuthClient {
    override suspend fun authenticate(request: IOAuthAuthenticateParameters): OAuthAuthenticateResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                val codePair = pkceClient.retrieve() ?: throw IllegalStateException("PKCE is missing")
                networkingClient.api.oAuthAuthenticate(request.toNetworkModel(codeVerifier = codePair.verifier))
            }
        }

    override suspend fun authenticateGoogleIdToken(
        request: IOAuthGoogleIDTokenAuthenticateParameters,
    ): OAuthGoogleIDTokenAuthenticateResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.oAuthGoogleIDTokenAuthenticate(request.toNetworkModel())
            }
        }

    override suspend fun authenticateAppleIdToken(
        request: IOAuthAppleIDTokenAuthenticateParameters,
    ): OAuthGoogleIDTokenAuthenticateResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.oAuthAppleIDTokenAuthenticate(request.toNetworkModel())
            }
        }

    override suspend fun attach(request: IOAuthAttachParameters): OAuthAttachResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.oAuthAttach(request.toNetworkModel())
            }
        }

    override val apple: OAuthType = OAuthTypeImpl(OAuthProviderType.APPLE, ::start)
    override val amazon: OAuthType = OAuthTypeImpl(OAuthProviderType.AMAZON, ::start)
    override val bitbucket: OAuthType = OAuthTypeImpl(OAuthProviderType.BITBUCKET, ::start)
    override val coinbase: OAuthType = OAuthTypeImpl(OAuthProviderType.COINBASE, ::start)
    override val discord: OAuthType = OAuthTypeImpl(OAuthProviderType.DISCORD, ::start)
    override val facebook: OAuthType = OAuthTypeImpl(OAuthProviderType.FACEBOOK, ::start)
    override val figma: OAuthType = OAuthTypeImpl(OAuthProviderType.FIGMA, ::start)
    override val github: OAuthType = OAuthTypeImpl(OAuthProviderType.GITHUB, ::start)
    override val gitlab: OAuthType = OAuthTypeImpl(OAuthProviderType.GITLAB, ::start)
    override val google: OAuthType = OAuthTypeImpl(OAuthProviderType.GOOGLE, ::start)
    override val linkedin: OAuthType = OAuthTypeImpl(OAuthProviderType.LINKEDIN, ::start)
    override val microsoft: OAuthType = OAuthTypeImpl(OAuthProviderType.MICROSOFT, ::start)
    override val salesforce: OAuthType = OAuthTypeImpl(OAuthProviderType.SALESFORCE, ::start)
    override val slack: OAuthType = OAuthTypeImpl(OAuthProviderType.SLACK, ::start)
    override val snapchat: OAuthType = OAuthTypeImpl(OAuthProviderType.SNAPCHAT, ::start)
    override val tiktok: OAuthType = OAuthTypeImpl(OAuthProviderType.TIKTOK, ::start)
    override val twitch: OAuthType = OAuthTypeImpl(OAuthProviderType.TWITCH, ::start)
    override val twitter: OAuthType = OAuthTypeImpl(OAuthProviderType.TWITTER, ::start)
    override val yahoo: OAuthType = OAuthTypeImpl(OAuthProviderType.YAHOO, ::start)

    private suspend fun start(
        provider: OAuthProviderType,
        parameters: OAuthStartParameters,
    ) = withContext(dispatchers.ioDispatcher) {
        val host = "https://${cnameDomain() ?: if (publicTokenInfo.isTestToken) endpointOptions.testDomain else endpointOptions.liveDomain}/v1/"
        val baseUrl = "${host}public/oauth/${provider.hostName}/start"
        val response =
            oauthProvider.getOAuthToken(
                pkceClient = pkceClient,
                dispatchers = dispatchers,
                type = provider,
                parameters = parameters,
                baseUrl = baseUrl,
                publicTokenInfo = publicTokenInfo,
            )
        return@withContext when (response) {
            is OAuthResult.ClassicToken -> {
                authenticate(
                    OAuthAuthenticateParameters(
                        token = response.token,
                        sessionDurationMinutes = parameters.sessionDurationMinutes ?: defaultSessionDuration,
                    ),
                )
            }

            is OAuthResult.IDToken -> {
                // IDTokens only come back for Google (on Android) or Apple (on iOS)
                if (provider == OAuthProviderType.GOOGLE) {
                    authenticateGoogleIdToken(
                        OAuthGoogleIDTokenAuthenticateParameters(
                            idToken = response.token,
                            nonce = response.nonce,
                            sessionDurationMinutes = parameters.sessionDurationMinutes ?: defaultSessionDuration,
                            oauthAttachToken = parameters.oauthAttachToken,
                        ),
                    )
                } else {
                    authenticateAppleIdToken(
                        OAuthAppleIDTokenAuthenticateParameters(
                            idToken = response.token,
                            nonce = response.nonce,
                            name = response.name?.toApiUserV1Name(),
                            sessionDurationMinutes = parameters.sessionDurationMinutes ?: defaultSessionDuration,
                            oauthAttachToken = parameters.oauthAttachToken,
                        ),
                    )
                }
            }

            is OAuthResult.Error -> {
                throw response.error
            }
        }
    }
}

internal class OAuthTypeImpl(
    private val provider: OAuthProviderType,
    private val handler: suspend (OAuthProviderType, OAuthStartParameters) -> AuthenticatedResponse,
) : OAuthType {
    override suspend fun start(startParameters: OAuthStartParameters): AuthenticatedResponse = handler(provider, startParameters)
}

private fun String.toApiUserV1Name(): ApiUserV1Name {
    val nameParts = split(" ")
    return when {
        nameParts.size >= 3 -> {
            ApiUserV1Name(
                firstName = nameParts[0],
                middleName = nameParts[1],
                lastName = nameParts.drop(2).joinToString(" "),
            )
        }

        nameParts.size == 2 -> {
            ApiUserV1Name(firstName = nameParts[0], lastName = nameParts[1])
        }

        else -> {
            ApiUserV1Name(this)
        }
    }
}
