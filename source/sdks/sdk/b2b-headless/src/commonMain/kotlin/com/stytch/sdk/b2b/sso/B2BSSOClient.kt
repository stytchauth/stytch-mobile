package com.stytch.sdk.b2b.sso

import com.stytch.sdk.StytchApi
import com.stytch.sdk.b2b.StytchB2BAuthenticationStateManager
import com.stytch.sdk.b2b.networking.AuthenticatedResponse
import com.stytch.sdk.b2b.networking.B2BNetworkingClient
import com.stytch.sdk.b2b.networking.models.B2BCreateExternalConnectionResponse
import com.stytch.sdk.b2b.networking.models.B2BCreateOIDCConnectionResponse
import com.stytch.sdk.b2b.networking.models.B2BCreateSAMLConnectionResponse
import com.stytch.sdk.b2b.networking.models.B2BDeleteSAMLVerificationCertificateResponse
import com.stytch.sdk.b2b.networking.models.B2BDeleteSSOConnectionResponse
import com.stytch.sdk.b2b.networking.models.B2BGetSSOConnectionsResponse
import com.stytch.sdk.b2b.networking.models.B2BSSOAuthEnticateParameters
import com.stytch.sdk.b2b.networking.models.B2BSSOAuthEnticateResponse
import com.stytch.sdk.b2b.networking.models.B2BUpdateExternalConnectionResponse
import com.stytch.sdk.b2b.networking.models.B2BUpdateOIDCConnectionResponse
import com.stytch.sdk.b2b.networking.models.B2BUpdateSAMLConnectionByURLResponse
import com.stytch.sdk.b2b.networking.models.B2BUpdateSAMLConnectionResponse
import com.stytch.sdk.b2b.networking.models.IB2BCreateExternalConnectionParameters
import com.stytch.sdk.b2b.networking.models.IB2BCreateOIDCConnectionParameters
import com.stytch.sdk.b2b.networking.models.IB2BCreateSAMLConnectionParameters
import com.stytch.sdk.b2b.networking.models.IB2BSSOAuthEnticateParameters
import com.stytch.sdk.b2b.networking.models.IB2BUpdateExternalConnectionParameters
import com.stytch.sdk.b2b.networking.models.IB2BUpdateOIDCConnectionParameters
import com.stytch.sdk.b2b.networking.models.IB2BUpdateSAMLConnectionByURLParameters
import com.stytch.sdk.b2b.networking.models.IB2BUpdateSAMLConnectionParameters
import com.stytch.sdk.b2b.networking.models.toNetworkModel
import com.stytch.sdk.data.EndpointOptions
import com.stytch.sdk.data.PublicTokenInfo
import com.stytch.sdk.data.StytchDispatchers
import com.stytch.sdk.data.StytchError
import com.stytch.sdk.oauth.B2BSSOStartParameters
import com.stytch.sdk.oauth.IOAuthProvider
import com.stytch.sdk.oauth.OAuthException
import com.stytch.sdk.oauth.OAuthResult
import com.stytch.sdk.pkce.MissingPKCEException
import com.stytch.sdk.pkce.PKCEClient
import io.ktor.http.URLBuilder
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException
import kotlin.js.JsExport

/** Alias for [B2BSSOAuthEnticateResponse], correcting the typo in the generated type name. */
public typealias B2BSSOAuthenticateResponse = B2BSSOAuthEnticateResponse

/** B2B SSO (Single Sign-On) authentication methods via SAML, OIDC, and external connections. */
@StytchApi
@JsExport
public interface B2BSSOClient {
    /** SAML SSO connection management methods. */
    public val saml: B2BSSOSAMLClient

    /** OIDC SSO connection management methods. */
    public val oidc: B2BSSOOIDCClient

    /** External SSO connection management methods. */
    public val external: B2BSSOExternalClient

    /**
     * Initiates an SSO authentication flow by opening a browser to the SSO provider.
     * Automatically exchanges the resulting token for a member session on success.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun start(parameters: B2BSSOStartParameters): AuthenticatedResponse

    /** Authenticates an SSO token received via deeplink after the browser flow completes. */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun authenticate(request: IB2BSSOAuthEnticateParameters): B2BSSOAuthenticateResponse

    /** Returns all SSO connections configured for the organization. */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun getConnections(): B2BGetSSOConnectionsResponse

    /** Deletes the specified SSO connection from the organization. */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun deleteConnection(connectionId: String): B2BDeleteSSOConnectionResponse
}

/** SAML SSO connection management methods. */
@StytchApi
@JsExport
public interface B2BSSOSAMLClient {
    /** Creates a new SAML SSO connection for the organization. */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun createConnection(request: IB2BCreateSAMLConnectionParameters): B2BCreateSAMLConnectionResponse

    /** Updates an existing SAML SSO connection. */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun updateConnection(
        connectionId: String,
        request: IB2BUpdateSAMLConnectionParameters,
    ): B2BUpdateSAMLConnectionResponse

    /** Updates a SAML SSO connection by fetching its metadata from the provided URL. */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun updateConnectionByUrl(
        connectionId: String,
        request: IB2BUpdateSAMLConnectionByURLParameters,
    ): B2BUpdateSAMLConnectionByURLResponse

    /** Deletes a verification certificate from the specified SAML SSO connection. */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun deleteVerificationCertificate(
        connectionId: String,
        certificateId: String,
    ): B2BDeleteSAMLVerificationCertificateResponse
}

/** OIDC SSO connection management methods. */
@StytchApi
@JsExport
public interface B2BSSOOIDCClient {
    /** Creates a new OIDC SSO connection for the organization. */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun createConnection(request: IB2BCreateOIDCConnectionParameters): B2BCreateOIDCConnectionResponse

    /** Updates an existing OIDC SSO connection. */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun updateConnection(
        connectionId: String,
        request: IB2BUpdateOIDCConnectionParameters,
    ): B2BUpdateOIDCConnectionResponse
}

/** External SSO connection management methods. */
@StytchApi
@JsExport
public interface B2BSSOExternalClient {
    /** Creates a new external SSO connection for the organization. */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun createConnection(request: IB2BCreateExternalConnectionParameters): B2BCreateExternalConnectionResponse

    /** Updates an existing external SSO connection. */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun updateConnection(
        connectionId: String,
        request: IB2BUpdateExternalConnectionParameters,
    ): B2BUpdateExternalConnectionResponse
}

internal class B2BSSOClientImpl(
    private val dispatchers: StytchDispatchers,
    private val networkingClient: B2BNetworkingClient,
    private val pkceClient: PKCEClient,
    private val sessionManager: StytchB2BAuthenticationStateManager,
    private val oauthProvider: IOAuthProvider,
    private val publicTokenInfo: PublicTokenInfo,
    private val endpointOptions: EndpointOptions,
    private val cnameDomain: () -> String?,
    private val defaultSessionDuration: Int,
) : B2BSSOClient {
    override val saml: B2BSSOSAMLClient = B2BSSOSAMLClientImpl(dispatchers, networkingClient)
    override val oidc: B2BSSOOIDCClient = B2BSSOOIDCClientImpl(dispatchers, networkingClient)
    override val external: B2BSSOExternalClient = B2BSSOExternalClientImpl(dispatchers, networkingClient)

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun start(parameters: B2BSSOStartParameters): AuthenticatedResponse =
        withContext(dispatchers.ioDispatcher) {
            val domain = cnameDomain() ?: if (publicTokenInfo.isTestToken) endpointOptions.testDomain else endpointOptions.liveDomain
            val baseUrl = "https://$domain/b2b/public/sso/start"
            val codePair = pkceClient.create()
            val url = buildSSOUrl(baseUrl, codePair.challenge, parameters)
            val result = oauthProvider.startBrowserFlow(url, parameters.toOAuthStartParameters(), dispatchers)
            when (result) {
                is OAuthResult.ClassicToken ->
                    networkingClient
                        .request {
                            networkingClient.api.b2BSSOAuthEnticate(
                                B2BSSOAuthEnticateParameters(
                                    ssoToken = result.token,
                                    sessionDurationMinutes = parameters.sessionDurationMinutes ?: defaultSessionDuration,
                                ).toNetworkModel(
                                    pkceCodeVerifier = codePair.verifier,
                                    intermediateSessionToken = sessionManager.intermediateSessionToken,
                                ),
                            )
                        }.also { pkceClient.revoke() } as AuthenticatedResponse
                is OAuthResult.Error -> throw OAuthException(RuntimeException(result.message))
                else -> throw OAuthException(RuntimeException("Unexpected OAuth result type"))
            }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun authenticate(request: IB2BSSOAuthEnticateParameters): B2BSSOAuthenticateResponse =
        withContext(dispatchers.ioDispatcher) {
            val codePair = pkceClient.retrieve() ?: throw MissingPKCEException()
            networkingClient
                .request {
                    networkingClient.api.b2BSSOAuthEnticate(
                        request.toNetworkModel(
                            pkceCodeVerifier = codePair.verifier,
                            intermediateSessionToken = sessionManager.intermediateSessionToken,
                        ),
                    )
                }.also { pkceClient.revoke() }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun getConnections(): B2BGetSSOConnectionsResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request { networkingClient.api.b2BGetSSOConnections() }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun deleteConnection(connectionId: String): B2BDeleteSSOConnectionResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request { networkingClient.api.b2BDeleteSSOConnection(connectionId) }
        }

    private fun buildSSOUrl(
        baseUrl: String,
        challenge: String,
        parameters: B2BSSOStartParameters,
    ): String {
        val params =
            mutableMapOf(
                "connection_id" to parameters.connectionId,
                "public_token" to publicTokenInfo.publicToken,
                "pkce_code_challenge" to challenge,
                "login_redirect_url" to parameters.loginRedirectUrl,
                "signup_redirect_url" to parameters.signupRedirectUrl,
            )
        val uri = URLBuilder(baseUrl)
        params.forEach { (key, value) -> if (value?.isNotEmpty() == true) uri.parameters.append(key, value) }
        return uri.build().toString()
    }
}

internal class B2BSSOSAMLClientImpl(
    private val dispatchers: StytchDispatchers,
    private val networkingClient: B2BNetworkingClient,
) : B2BSSOSAMLClient {
    @Throws(StytchError::class, CancellationException::class)
    override suspend fun createConnection(request: IB2BCreateSAMLConnectionParameters): B2BCreateSAMLConnectionResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request { networkingClient.api.b2BCreateSAMLConnection(request.toNetworkModel()) }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun updateConnection(
        connectionId: String,
        request: IB2BUpdateSAMLConnectionParameters,
    ): B2BUpdateSAMLConnectionResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request { networkingClient.api.b2BUpdateSAMLConnection(connectionId, request.toNetworkModel()) }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun updateConnectionByUrl(
        connectionId: String,
        request: IB2BUpdateSAMLConnectionByURLParameters,
    ): B2BUpdateSAMLConnectionByURLResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request { networkingClient.api.b2BUpdateSAMLConnectionByURL(connectionId, request.toNetworkModel()) }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun deleteVerificationCertificate(
        connectionId: String,
        certificateId: String,
    ): B2BDeleteSAMLVerificationCertificateResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request { networkingClient.api.b2BDeleteSAMLVerificationCertificate(connectionId, certificateId) }
        }
}

internal class B2BSSOOIDCClientImpl(
    private val dispatchers: StytchDispatchers,
    private val networkingClient: B2BNetworkingClient,
) : B2BSSOOIDCClient {
    @Throws(StytchError::class, CancellationException::class)
    override suspend fun createConnection(request: IB2BCreateOIDCConnectionParameters): B2BCreateOIDCConnectionResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request { networkingClient.api.b2BCreateOIDCConnection(request.toNetworkModel()) }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun updateConnection(
        connectionId: String,
        request: IB2BUpdateOIDCConnectionParameters,
    ): B2BUpdateOIDCConnectionResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request { networkingClient.api.b2BUpdateOIDCConnection(connectionId, request.toNetworkModel()) }
        }
}

internal class B2BSSOExternalClientImpl(
    private val dispatchers: StytchDispatchers,
    private val networkingClient: B2BNetworkingClient,
) : B2BSSOExternalClient {
    @Throws(StytchError::class, CancellationException::class)
    override suspend fun createConnection(request: IB2BCreateExternalConnectionParameters): B2BCreateExternalConnectionResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request { networkingClient.api.b2BCreateExternalConnection(request.toNetworkModel()) }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun updateConnection(
        connectionId: String,
        request: IB2BUpdateExternalConnectionParameters,
    ): B2BUpdateExternalConnectionResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request { networkingClient.api.b2BUpdateExternalConnection(connectionId, request.toNetworkModel()) }
        }
}
