package com.stytch.sdk.b2b.oauth

import com.stytch.sdk.b2b.StytchB2BAuthenticationStateManager
import com.stytch.sdk.b2b.networking.AuthenticatedResponse
import com.stytch.sdk.b2b.networking.B2BNetworkingClient
import com.stytch.sdk.b2b.networking.models.B2BOAuthAuthenticateParameters
import com.stytch.sdk.b2b.networking.models.B2BOAuthAuthenticateResponse
import com.stytch.sdk.b2b.networking.models.B2BOAuthDiscoveryAuthenticateParameters
import com.stytch.sdk.b2b.networking.models.B2BOAuthDiscoveryAuthenticateResponse
import com.stytch.sdk.b2b.networking.models.IB2BOAuthAuthenticateParameters
import com.stytch.sdk.b2b.networking.models.IB2BOAuthDiscoveryAuthenticateParameters
import com.stytch.sdk.b2b.networking.models.toNetworkModel
import com.stytch.sdk.data.EndpointOptions
import com.stytch.sdk.data.PublicTokenInfo
import com.stytch.sdk.data.StytchDispatchers
import com.stytch.sdk.data.StytchError
import com.stytch.sdk.oauth.B2BOAuthDiscoveryStartParameters
import com.stytch.sdk.oauth.B2BOAuthStartParameters
import com.stytch.sdk.oauth.IOAuthProvider
import com.stytch.sdk.oauth.OAuthException
import com.stytch.sdk.oauth.OAuthResult
import com.stytch.sdk.pkce.MissingPKCEException
import com.stytch.sdk.pkce.PKCEClient
import io.ktor.http.URLBuilder
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException
import kotlin.js.JsExport
import com.stytch.sdk.StytchApi

@StytchApi
@JsExport
public interface B2BOAuthClient {
    public val google: B2BOAuthProviderClient
    public val microsoft: B2BOAuthProviderClient
    public val hubspot: B2BOAuthProviderClient
    public val slack: B2BOAuthProviderClient
    public val github: B2BOAuthProviderClient
    public val discovery: B2BOAuthDiscoveryClient

    @Throws(StytchError::class, CancellationException::class)
    public suspend fun authenticate(request: IB2BOAuthAuthenticateParameters): B2BOAuthAuthenticateResponse
}

@StytchApi
@JsExport
public interface B2BOAuthProviderClient {
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun start(parameters: B2BOAuthStartParameters): AuthenticatedResponse

    public val discovery: B2BOAuthProviderDiscoveryClient
}

@StytchApi
@JsExport
public interface B2BOAuthProviderDiscoveryClient {
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun start(parameters: B2BOAuthDiscoveryStartParameters): B2BOAuthDiscoveryAuthenticateResponse
}

@StytchApi
@JsExport
public interface B2BOAuthDiscoveryClient {
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun authenticate(request: IB2BOAuthDiscoveryAuthenticateParameters): B2BOAuthDiscoveryAuthenticateResponse
}

internal class B2BOAuthClientImpl(
    private val dispatchers: StytchDispatchers,
    private val networkingClient: B2BNetworkingClient,
    private val pkceClient: PKCEClient,
    private val sessionManager: StytchB2BAuthenticationStateManager,
    private val oauthProvider: IOAuthProvider,
    private val publicTokenInfo: PublicTokenInfo,
    private val endpointOptions: EndpointOptions,
    private val cnameDomain: () -> String?,
    private val defaultSessionDuration: Int,
) : B2BOAuthClient {
    override val discovery = B2BOAuthDiscoveryClientImpl(dispatchers, networkingClient, pkceClient)

    override val google = providerClient("google")
    override val microsoft = providerClient("microsoft")
    override val hubspot = providerClient("hubspot")
    override val slack = providerClient("slack")
    override val github = providerClient("github")

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun authenticate(request: IB2BOAuthAuthenticateParameters): B2BOAuthAuthenticateResponse =
        withContext(dispatchers.ioDispatcher) {
            val codePair = pkceClient.retrieve() ?: throw MissingPKCEException()
            networkingClient
                .request {
                    networkingClient.api.b2BOAuthAuthenticate(
                        request.toNetworkModel(
                            pkceCodeVerifier = codePair.verifier,
                            intermediateSessionToken = sessionManager.intermediateSessionToken,
                        ),
                    )
                }.also { pkceClient.revoke() }
        }

    private fun providerClient(providerName: String) =
        B2BOAuthProviderClientImpl(
            handleStart = { params -> startOAuthFlow(providerName, params) },
            handleDiscoveryStart = { params -> startDiscoveryOAuthFlow(providerName, params) },
        )

    private suspend fun startOAuthFlow(
        providerName: String,
        parameters: B2BOAuthStartParameters,
    ): AuthenticatedResponse =
        withContext(dispatchers.ioDispatcher) {
            val domain = cnameDomain() ?: if (publicTokenInfo.isTestToken) endpointOptions.testDomain else endpointOptions.liveDomain
            val baseUrl = "https://$domain/b2b/public/oauth/$providerName/start"
            val codePair = pkceClient.create()
            val url = buildOAuthUrl(baseUrl, codePair.challenge, parameters)
            val result = oauthProvider.startBrowserFlow(url, parameters.toOAuthStartParameters(), dispatchers)
            when (result) {
                is OAuthResult.ClassicToken -> {
                    networkingClient
                        .request {
                            networkingClient.api.b2BOAuthAuthenticate(
                                B2BOAuthAuthenticateParameters(
                                    oauthToken = result.token,
                                    sessionDurationMinutes = parameters.sessionDurationMinutes ?: defaultSessionDuration,
                                ).toNetworkModel(
                                    pkceCodeVerifier = codePair.verifier,
                                    intermediateSessionToken = sessionManager.intermediateSessionToken,
                                ),
                            )
                        }.also { pkceClient.revoke() } as AuthenticatedResponse
                }

                is OAuthResult.Error -> {
                    throw OAuthException(RuntimeException(result.message))
                }

                else -> {
                    throw OAuthException(RuntimeException("Unexpected OAuth result type"))
                }
            }
        }

    private suspend fun startDiscoveryOAuthFlow(
        providerName: String,
        parameters: B2BOAuthDiscoveryStartParameters,
    ): B2BOAuthDiscoveryAuthenticateResponse =
        withContext(dispatchers.ioDispatcher) {
            val domain = cnameDomain() ?: if (publicTokenInfo.isTestToken) endpointOptions.testDomain else endpointOptions.liveDomain
            val baseUrl = "https://$domain/b2b/public/oauth/$providerName/discovery/start"
            val codePair = pkceClient.create()
            val url = buildDiscoveryOAuthUrl(baseUrl, codePair.challenge, parameters)
            val result = oauthProvider.startBrowserFlow(url, parameters.toOAuthStartParameters(), dispatchers)
            when (result) {
                is OAuthResult.ClassicToken -> {
                    networkingClient
                        .request {
                            networkingClient.api.b2BOAuthDiscoveryAuthenticate(
                                B2BOAuthDiscoveryAuthenticateParameters(
                                    discoveryOauthToken = result.token,
                                ).toNetworkModel(pkceCodeVerifier = codePair.verifier),
                            )
                        }.also { pkceClient.revoke() }
                }

                is OAuthResult.Error -> {
                    throw OAuthException(RuntimeException(result.message))
                }

                else -> {
                    throw OAuthException(RuntimeException("Unexpected OAuth result type"))
                }
            }
        }

    private fun buildOAuthUrl(
        baseUrl: String,
        challenge: String,
        parameters: B2BOAuthStartParameters,
    ): String {
        val params =
            mutableMapOf(
                "public_token" to publicTokenInfo.publicToken,
                "pkce_code_challenge" to challenge,
                "organization_id" to parameters.organizationId,
                "slug" to parameters.organizationSlug,
                "login_redirect_url" to parameters.loginRedirectUrl,
                "signup_redirect_url" to parameters.signupRedirectUrl,
                "custom_scopes" to parameters.customScopes?.joinToString(" "),
            )
        parameters.providerParams?.forEach { (key, value) -> params["provider_$key"] = value }
        val uri = URLBuilder(baseUrl)
        params.forEach { (key, value) -> if (value?.isNotEmpty() == true) uri.parameters.append(key, value) }
        return uri.build().toString()
    }

    private fun buildDiscoveryOAuthUrl(
        baseUrl: String,
        challenge: String,
        parameters: B2BOAuthDiscoveryStartParameters,
    ): String {
        val params =
            mutableMapOf(
                "public_token" to publicTokenInfo.publicToken,
                "pkce_code_challenge" to challenge,
                "discovery_redirect_url" to parameters.discoveryRedirectUrl,
                "custom_scopes" to parameters.customScopes?.joinToString(" "),
            )
        parameters.providerParams?.forEach { (key, value) -> params["provider_$key"] = value }
        val uri = URLBuilder(baseUrl)
        params.forEach { (key, value) -> if (value?.isNotEmpty() == true) uri.parameters.append(key, value) }
        return uri.build().toString()
    }
}

internal class B2BOAuthProviderClientImpl(
    private val handleStart: suspend (B2BOAuthStartParameters) -> AuthenticatedResponse,
    private val handleDiscoveryStart: suspend (B2BOAuthDiscoveryStartParameters) -> B2BOAuthDiscoveryAuthenticateResponse,
) : B2BOAuthProviderClient {
    override val discovery =
        object : B2BOAuthProviderDiscoveryClient {
            @Throws(StytchError::class, CancellationException::class)
            override suspend fun start(parameters: B2BOAuthDiscoveryStartParameters) = handleDiscoveryStart(parameters)
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun start(parameters: B2BOAuthStartParameters) = handleStart(parameters)
}

internal class B2BOAuthDiscoveryClientImpl(
    private val dispatchers: StytchDispatchers,
    private val networkingClient: B2BNetworkingClient,
    private val pkceClient: PKCEClient,
) : B2BOAuthDiscoveryClient {
    @Throws(StytchError::class, CancellationException::class)
    override suspend fun authenticate(request: IB2BOAuthDiscoveryAuthenticateParameters): B2BOAuthDiscoveryAuthenticateResponse =
        withContext(dispatchers.ioDispatcher) {
            val codePair = pkceClient.retrieve() ?: throw MissingPKCEException()
            networkingClient
                .request {
                    networkingClient.api.b2BOAuthDiscoveryAuthenticate(
                        request.toNetworkModel(pkceCodeVerifier = codePair.verifier),
                    )
                }.also { pkceClient.revoke() }
        }
}
