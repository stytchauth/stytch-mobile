package com.stytch.sdk.consumer.oauth

import com.stytch.sdk.StytchApi
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
import com.stytch.sdk.data.StytchError
import com.stytch.sdk.oauth.IOAuthProvider
import com.stytch.sdk.oauth.OAuthException
import com.stytch.sdk.oauth.OAuthProviderType
import com.stytch.sdk.oauth.OAuthResult
import com.stytch.sdk.oauth.OAuthStartParameters
import com.stytch.sdk.pkce.MissingPKCEException
import com.stytch.sdk.pkce.PKCEClient
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException
import kotlin.js.JsExport

/** OAuth authentication methods for browser-based and native provider flows. */
@StytchApi
@JsExport
public interface OAuthClient {
    /**
     * Authenticates an OAuth token received from a deeplink after the browser-based OAuth flow completes.
     * Calls the `POST /sdk/v1/oauth/authenticate` endpoint. Retrieves the PKCE code verifier stored
     * during the corresponding [OAuthType.start] call.
     *
     * This method is called automatically by [OAuthType.start] when using the browser flow — you typically
     * only need to call it directly if you are handling the deeplink redirect yourself.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchConsumer.oauth.authenticate(
     *     OAuthAuthenticateParameters(token = "token", sessionDurationMinutes = 30)
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = OAuthAuthenticateParameters(token: "token", sessionDurationMinutes: 30)
     * let response = try await StytchConsumer.oauth.authenticate(params)
     * ```
     *
     * **React Native:**
     * ```js
     * StytchConsumer.oauth.authenticate({ token: "token", sessionDurationMinutes: 30 })
     * ```
     *
     * @param request - [IOAuthAuthenticateParameters]
     *   - `token` — The OAuth token extracted from the deeplink URL.
     *   - `sessionDurationMinutes` — Duration of the session to create, in minutes.
     *
     * @return [OAuthAuthenticateResponse] containing the authenticated session and user.
     *
     * @throws [StytchError] if the token is invalid or expired, or if no PKCE verifier is found in storage.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun authenticate(request: IOAuthAuthenticateParameters): OAuthAuthenticateResponse

    /**
     * Authenticates a Google ID token obtained via native Android Google Sign-In.
     * Calls the `POST /sdk/v1/oauth/google/id_token/authenticate` endpoint.
     *
     * This method is called automatically by [OAuthClient.google] `start()` on Android — you typically
     * only need to call it directly if you are supplying the ID token yourself.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchConsumer.oauth.authenticateGoogleIdToken(
     *     OAuthGoogleIDTokenAuthenticateParameters(
     *         idToken = "google-id-token",
     *         sessionDurationMinutes = 30,
     *     )
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = OAuthGoogleIDTokenAuthenticateParameters(
     *     idToken: "google-id-token",
     *     sessionDurationMinutes: 30
     * )
     * let response = try await StytchConsumer.oauth.authenticateGoogleIdToken(params)
     * ```
     *
     * **React Native:**
     * ```js
     * StytchConsumer.oauth.authenticateGoogleIdToken({ idToken: "google-id-token", sessionDurationMinutes: 30 })
     * ```
     *
     * @param request - [IOAuthGoogleIDTokenAuthenticateParameters]
     *   - `idToken` — The Google ID token from native Google Sign-In.
     *   - `sessionDurationMinutes` — Duration of the session to create, in minutes.
     *   - `nonce?` — The nonce used when requesting the ID token, for replay protection.
     *   - `oauthAttachToken?` — An OAuth attach token to link this provider to an existing session.
     *
     * @return [OAuthGoogleIDTokenAuthenticateResponse] containing the authenticated session and user.
     *
     * @throws [StytchError] if the ID token is invalid or expired.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun authenticateGoogleIdToken(request: IOAuthGoogleIDTokenAuthenticateParameters): OAuthGoogleIDTokenAuthenticateResponse

    /**
     * Authenticates an Apple ID token obtained via native iOS Sign In with Apple.
     * Calls the `POST /sdk/v1/oauth/apple/id_token/authenticate` endpoint.
     *
     * This method is called automatically by [OAuthClient.apple] `start()` on iOS — you typically
     * only need to call it directly if you are supplying the ID token yourself.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchConsumer.oauth.authenticateAppleIdToken(
     *     OAuthAppleIDTokenAuthenticateParameters(
     *         idToken = "apple-id-token",
     *         sessionDurationMinutes = 30,
     *     )
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = OAuthAppleIDTokenAuthenticateParameters(
     *     idToken: "apple-id-token",
     *     sessionDurationMinutes: 30
     * )
     * let response = try await StytchConsumer.oauth.authenticateAppleIdToken(params)
     * ```
     *
     * **React Native:**
     * ```js
     * StytchConsumer.oauth.authenticateAppleIdToken({ idToken: "apple-id-token", sessionDurationMinutes: 30 })
     * ```
     *
     * @param request - [IOAuthAppleIDTokenAuthenticateParameters]
     *   - `idToken` — The Apple ID token from Sign In with Apple.
     *   - `sessionDurationMinutes` — Duration of the session to create, in minutes.
     *   - `name?` — The user's name as provided by Apple on first sign-in (only returned once by Apple).
     *   - `nonce?` — The nonce used when requesting the ID token, for replay protection.
     *   - `oauthAttachToken?` — An OAuth attach token to link this provider to an existing session.
     *
     * @return [OAuthGoogleIDTokenAuthenticateResponse] containing the authenticated session and user.
     *
     * @throws [StytchError] if the ID token is invalid or expired.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun authenticateAppleIdToken(request: IOAuthAppleIDTokenAuthenticateParameters): OAuthGoogleIDTokenAuthenticateResponse

    /**
     * Creates an OAuth attach token that can be used to link an OAuth provider to an existing
     * authenticated session. Calls the `POST /sdk/v1/oauth/attach` endpoint. Pass the returned
     * attach token as `oauthAttachToken` in a subsequent [OAuthType.start] call.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchConsumer.oauth.attach(
     *     OAuthAttachParameters(provider = "google")
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = OAuthAttachParameters(provider: "google")
     * let response = try await StytchConsumer.oauth.attach(params)
     * ```
     *
     * **React Native:**
     * ```js
     * StytchConsumer.oauth.attach({ provider: "google" })
     * ```
     *
     * @param request - [IOAuthAttachParameters]
     *   - `provider` — The OAuth provider identifier (e.g. `"google"`, `"apple"`, `"github"`).
     *
     * @return [OAuthAttachResponse] containing the `oauthAttachToken` to pass to [OAuthType.start].
     *
     * @throws [StytchError] if the request fails or no active session exists.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun attach(request: IOAuthAttachParameters): OAuthAttachResponse

    /** Apple OAuth. */
    public val apple: OAuthType

    /** Amazon OAuth. */
    public val amazon: OAuthType

    /** Bitbucket OAuth. */
    public val bitbucket: OAuthType

    /** Coinbase OAuth. */
    public val coinbase: OAuthType

    /** Discord OAuth. */
    public val discord: OAuthType

    /** Facebook OAuth. */
    public val facebook: OAuthType

    /** Figma OAuth. */
    public val figma: OAuthType

    /** GitHub OAuth. */
    public val github: OAuthType

    /** GitLab OAuth. */
    public val gitlab: OAuthType

    /** Google OAuth. */
    public val google: OAuthType

    /** LinkedIn OAuth. */
    public val linkedin: OAuthType

    /** Microsoft OAuth. */
    public val microsoft: OAuthType

    /** Salesforce OAuth. */
    public val salesforce: OAuthType

    /** Slack OAuth. */
    public val slack: OAuthType

    /** Snapchat OAuth. */
    public val snapchat: OAuthType

    /** TikTok OAuth. */
    public val tiktok: OAuthType

    /** Twitch OAuth. */
    public val twitch: OAuthType

    /** Twitter/X OAuth. */
    public val twitter: OAuthType

    /** Yahoo OAuth. */
    public val yahoo: OAuthType
}

/** An individual OAuth provider integration. */
@StytchApi
@JsExport
public interface OAuthType {
    /**
     * Initiates the browser-based OAuth flow for this provider and authenticates the result,
     * establishing a session. On Android, opens a Custom Tab; on iOS, uses `ASWebAuthenticationSession`.
     * Internally calls [OAuthClient.authenticate], [OAuthClient.authenticateGoogleIdToken], or
     * [OAuthClient.authenticateAppleIdToken] depending on the provider and platform response.
     *
     * **Kotlin (Android):**
     * ```kotlin
     * StytchConsumer.oauth.google.start(
     *     OAuthStartParameters(
     *         activity = activity,
     *         loginRedirectUrl = "myapp://oauth",
     *         signupRedirectUrl = "myapp://oauth",
     *         sessionDurationMinutes = 30,
     *     )
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = OAuthStartParameters(
     *     loginRedirectUrl: "myapp://oauth",
     *     signupRedirectUrl: "myapp://oauth",
     *     sessionDurationMinutes: 30,
     *     oauthPresentationContextProvider: self
     * )
     * let response = try await StytchConsumer.oauth.google.start(params)
     * ```
     *
     * **React Native:**
     * ```js
     * StytchConsumer.oauth.google.start({
     *     loginRedirectUrl: "myapp://oauth",
     *     signupRedirectUrl: "myapp://oauth",
     *     sessionDurationMinutes: 30,
     * })
     * ```
     *
     * @param startParameters - [OAuthStartParameters]
     *   - `loginRedirectUrl?` — Deep link URL to redirect existing users to after authorization.
     *   - `signupRedirectUrl?` — Deep link URL to redirect new users to after authorization.
     *   - `customScopes?` — Additional OAuth scopes to request from the provider.
     *   - `providerParams?` — Extra provider-specific query parameters to include in the authorization URL.
     *   - `oauthAttachToken?` — Attach token to link this provider to an existing session.
     *   - `sessionDurationMinutes?` — Duration of the session to create, in minutes.
     *   - *(Android only)* `activity` — The `Activity` used to launch the Custom Tab.
     *   - *(iOS only)* `applePresentationContextProvider` — Presentation context for Sign In with Apple sheets.
     *   - *(iOS only)* `oauthPresentationContextProvider` — Presentation context for the web authentication session.
     *
     * @return [AuthenticatedResponse] containing the authenticated session and user.
     *
     * @throws [StytchError] if the OAuth flow fails or the resulting token cannot be authenticated.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
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
    @Throws(StytchError::class, CancellationException::class)
    override suspend fun authenticate(request: IOAuthAuthenticateParameters): OAuthAuthenticateResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                val codePair = pkceClient.retrieve() ?: throw MissingPKCEException()
                val response = networkingClient.api.oAuthAuthenticate(request.toNetworkModel(codeVerifier = codePair.verifier))
                pkceClient.revoke()
                response
            }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun authenticateGoogleIdToken(
        request: IOAuthGoogleIDTokenAuthenticateParameters,
    ): OAuthGoogleIDTokenAuthenticateResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.oAuthGoogleIDTokenAuthenticate(request.toNetworkModel())
            }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun authenticateAppleIdToken(
        request: IOAuthAppleIDTokenAuthenticateParameters,
    ): OAuthGoogleIDTokenAuthenticateResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.oAuthAppleIDTokenAuthenticate(request.toNetworkModel())
            }
        }

    @Throws(StytchError::class, CancellationException::class)
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
    ): AuthenticatedResponse =
        withContext(dispatchers.ioDispatcher) {
            val host =
                "https://${cnameDomain() ?: if (publicTokenInfo.isTestToken) endpointOptions.testDomain else endpointOptions.liveDomain}/v1/"
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
                    throw OAuthException(RuntimeException(response.message))
                }
            }
        }
}

internal class OAuthTypeImpl(
    private val provider: OAuthProviderType,
    private val handler: suspend (OAuthProviderType, OAuthStartParameters) -> AuthenticatedResponse,
) : OAuthType {
    @Throws(StytchError::class, CancellationException::class)
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
