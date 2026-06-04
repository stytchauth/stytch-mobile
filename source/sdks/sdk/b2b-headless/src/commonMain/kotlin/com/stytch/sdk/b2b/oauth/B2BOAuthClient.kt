package com.stytch.sdk.b2b.oauth

import com.stytch.sdk.StytchApi
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

/** B2B OAuth authentication methods for supported identity providers. */
@StytchApi
@JsExport
public interface B2BOAuthClient {
    /** Google OAuth provider client. */
    public val google: B2BOAuthProviderClient

    /** Microsoft OAuth provider client. */
    public val microsoft: B2BOAuthProviderClient

    /** HubSpot OAuth provider client. */
    public val hubspot: B2BOAuthProviderClient

    /** Slack OAuth provider client. */
    public val slack: B2BOAuthProviderClient

    /** GitHub OAuth provider client. */
    public val github: B2BOAuthProviderClient

    /** Cross-org OAuth discovery methods for enumerating organizations before authentication. */
    public val discovery: B2BOAuthDiscoveryClient

    /**
     * Authenticates an OAuth token received via deeplink after a browser-based OAuth flow completes,
     * establishing a member session. Calls the `POST /sdk/v1/b2b/oauth/authenticate` endpoint.
     * Retrieves the PKCE code verifier stored during the [B2BOAuthProviderClient.start] call, and
     * automatically includes the intermediate session token if one is present.
     *
     * Use this method when handling deeplinks manually; prefer [B2BOAuthProviderClient.start] for
     * the end-to-end flow.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchB2B.oauth.authenticate(
     *     B2BOAuthAuthenticateParameters(
     *         oauthToken = "token",
     *         sessionDurationMinutes = 30,
     *     )
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = B2BOAuthAuthenticateParameters(oauthToken: "token", sessionDurationMinutes: 30)
     * let response = try await StytchB2B.oauth.authenticate(params)
     * ```
     *
     * **React Native:**
     * ```js
     * StytchB2B.oauth.authenticate({ oauthToken: "token", sessionDurationMinutes: 30 })
     * ```
     *
     * @param request - [IB2BOAuthAuthenticateParameters]
     *   - `oauthToken` — The OAuth token extracted from the deeplink URL.
     *   - `sessionDurationMinutes?` — Duration of the session to create, in minutes.
     *   - `locale?` — Locale for any follow-up communications.
     *
     * @return [B2BOAuthAuthenticateResponse] containing the authenticated member session.
     *
     * @throws [StytchError] if the token is invalid, expired, or no PKCE verifier is found in storage.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun authenticate(request: IB2BOAuthAuthenticateParameters): B2BOAuthAuthenticateResponse
}

/** OAuth authentication and discovery methods for a single identity provider. */
@StytchApi
@JsExport
public interface B2BOAuthProviderClient {
    /** Discovery OAuth methods for listing organizations before a session is established. */
    public val discovery: B2BOAuthProviderDiscoveryClient

    /**
     * Initiates an OAuth browser flow for the provider, scoped to the specified organization.
     * Opens a browser session at `https://{domain}/b2b/public/oauth/{provider}/start`, then
     * automatically exchanges the resulting token by calling `POST /sdk/v1/b2b/oauth/authenticate`,
     * establishing a member session on success.
     *
     * **Kotlin:**
     * ```kotlin
     * val response = StytchB2B.oauth.google.start(
     *     B2BOAuthStartParameters(
     *         organizationId = "org-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *         loginRedirectUrl = "myapp://callback",
     *         signupRedirectUrl = "myapp://callback",
     *         sessionDurationMinutes = 30,
     *     )
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = B2BOAuthStartParameters(
     *     organizationId: "org-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *     loginRedirectUrl: "myapp://callback",
     *     signupRedirectUrl: "myapp://callback",
     *     sessionDurationMinutes: 30
     * )
     * let response = try await StytchB2B.oauth.google.start(params)
     * ```
     *
     * **React Native:**
     * ```js
     * StytchB2B.oauth.google.start({
     *     organizationId: "org-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *     loginRedirectUrl: "myapp://callback",
     *     signupRedirectUrl: "myapp://callback",
     *     sessionDurationMinutes: 30,
     * })
     * ```
     *
     * @param parameters - [B2BOAuthStartParameters]
     *   - `organizationId?` — The ID of the organization to authenticate into.
     *   - `organizationSlug?` — The slug of the organization to authenticate into.
     *   - `loginRedirectUrl?` — URL to redirect to after a successful login.
     *   - `signupRedirectUrl?` — URL to redirect to after a successful sign-up.
     *   - `customScopes?` — Additional OAuth scopes to request from the provider.
     *   - `providerParams?` — Provider-specific query parameters to append to the OAuth URL.
     *   - `sessionDurationMinutes?` — Duration of the session to create, in minutes.
     *   - `activity?` *(Android only)* — The Android `Activity` used to launch the browser.
     *   - `oauthPresentationContextProvider?` *(iOS only)* — Presentation context for the `ASWebAuthenticationSession`.
     *
     * @return [AuthenticatedResponse] containing the authenticated member session.
     *
     * @throws [StytchError] if the OAuth flow fails or the token cannot be exchanged.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun start(parameters: B2BOAuthStartParameters): AuthenticatedResponse
}

/** Discovery OAuth start for a single provider — returns discovered organizations instead of a session. */
@StytchApi
@JsExport
public interface B2BOAuthProviderDiscoveryClient {
    /**
     * Initiates a discovery OAuth browser flow for the provider, allowing the user to enumerate
     * organizations before authenticating. Opens a browser session at
     * `https://{domain}/b2b/public/oauth/{provider}/discovery/start`, then automatically calls
     * `POST /sdk/v1/b2b/oauth/discovery/authenticate`, returning discovered organizations and an
     * intermediate session token.
     *
     * **Kotlin:**
     * ```kotlin
     * val response = StytchB2B.oauth.google.discovery.start(
     *     B2BOAuthDiscoveryStartParameters(
     *         discoveryRedirectUrl = "myapp://discovery",
     *     )
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = B2BOAuthDiscoveryStartParameters(discoveryRedirectUrl: "myapp://discovery")
     * let response = try await StytchB2B.oauth.google.discovery.start(params)
     * ```
     *
     * **React Native:**
     * ```js
     * StytchB2B.oauth.google.discovery.start({ discoveryRedirectUrl: "myapp://discovery" })
     * ```
     *
     * @param parameters - [B2BOAuthDiscoveryStartParameters]
     *   - `discoveryRedirectUrl?` — URL to redirect to after the discovery flow completes.
     *   - `customScopes?` — Additional OAuth scopes to request from the provider.
     *   - `providerParams?` — Provider-specific query parameters to append to the OAuth URL.
     *   - `activity?` *(Android only)* — The Android `Activity` used to launch the browser.
     *   - `oauthPresentationContextProvider?` *(iOS only)* — Presentation context for the `ASWebAuthenticationSession`.
     *
     * @return [B2BOAuthDiscoveryAuthenticateResponse] containing discovered organizations and an intermediate session token.
     *
     * @throws [StytchError] if the OAuth flow fails or the token cannot be exchanged.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun start(parameters: B2BOAuthDiscoveryStartParameters): B2BOAuthDiscoveryAuthenticateResponse
}

/** Cross-provider OAuth discovery authentication — exchanges a discovery token for an intermediate session. */
@StytchApi
@JsExport
public interface B2BOAuthDiscoveryClient {
    /**
     * Authenticates a discovery OAuth token received via deeplink after a browser-based discovery
     * OAuth flow completes. Calls the `POST /sdk/v1/b2b/oauth/discovery/authenticate` endpoint.
     * Retrieves the PKCE code verifier stored during the [B2BOAuthProviderDiscoveryClient.start] call.
     * Returns discovered organizations and an intermediate session token; call
     * [B2BDiscoveryIntermediateSessionsClient.exchange] to establish a full member session.
     *
     * Use this method when handling deeplinks manually; prefer [B2BOAuthProviderDiscoveryClient.start]
     * for the end-to-end flow.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchB2B.oauth.discovery.authenticate(
     *     B2BOAuthDiscoveryAuthenticateParameters(
     *         discoveryOauthToken = "token",
     *     )
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = B2BOAuthDiscoveryAuthenticateParameters(discoveryOauthToken: "token")
     * let response = try await StytchB2B.oauth.discovery.authenticate(params)
     * ```
     *
     * **React Native:**
     * ```js
     * StytchB2B.oauth.discovery.authenticate({ discoveryOauthToken: "token" })
     * ```
     *
     * @param request - [IB2BOAuthDiscoveryAuthenticateParameters]
     *   - `discoveryOauthToken` — The discovery OAuth token extracted from the deeplink URL.
     *
     * @return [B2BOAuthDiscoveryAuthenticateResponse] containing discovered organizations and an intermediate session token.
     *
     * @throws [StytchError] if the token is invalid, expired, or no PKCE verifier is found in storage.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
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
            try {
                val codePair = pkceClient.create()
                val url = buildOAuthUrl(baseUrl, codePair.challenge, parameters)
                when (val result = oauthProvider.startBrowserFlow(url, parameters.toOAuthStartParameters(), dispatchers)) {
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
            } finally {
                pkceClient.revoke()
            }
        }

    private suspend fun startDiscoveryOAuthFlow(
        providerName: String,
        parameters: B2BOAuthDiscoveryStartParameters,
    ): B2BOAuthDiscoveryAuthenticateResponse =
        withContext(dispatchers.ioDispatcher) {
            val domain = cnameDomain() ?: if (publicTokenInfo.isTestToken) endpointOptions.testDomain else endpointOptions.liveDomain
            val baseUrl = "https://$domain/b2b/public/oauth/$providerName/discovery/start"
            try {
                val codePair = pkceClient.create()
                val url = buildDiscoveryOAuthUrl(baseUrl, codePair.challenge, parameters)
                when (val result = oauthProvider.startBrowserFlow(url, parameters.toOAuthStartParameters(), dispatchers)) {
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
            } finally {
                pkceClient.revoke()
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
