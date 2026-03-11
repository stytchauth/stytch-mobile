package com.stytch.sdk.b2b.sso

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

public typealias B2BSSOAuthenticateResponse = B2BSSOAuthEnticateResponse

@JsExport
public interface B2BSSOClient {
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun start(parameters: B2BSSOStartParameters): AuthenticatedResponse

    @Throws(StytchError::class, CancellationException::class)
    public suspend fun authenticate(request: IB2BSSOAuthEnticateParameters): B2BSSOAuthenticateResponse

    @Throws(StytchError::class, CancellationException::class)
    public suspend fun getConnections(): B2BGetSSOConnectionsResponse

    @Throws(StytchError::class, CancellationException::class)
    public suspend fun deleteConnection(connectionId: String): B2BDeleteSSOConnectionResponse

    public val saml: B2BSSOSAMLClient
    public val oidc: B2BSSOOIDCClient
    public val external: B2BSSOExternalClient
}

@JsExport
public interface B2BSSOSAMLClient {
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun createConnection(request: IB2BCreateSAMLConnectionParameters): B2BCreateSAMLConnectionResponse

    @Throws(StytchError::class, CancellationException::class)
    public suspend fun updateConnection(
        connectionId: String,
        request: IB2BUpdateSAMLConnectionParameters,
    ): B2BUpdateSAMLConnectionResponse

    @Throws(StytchError::class, CancellationException::class)
    public suspend fun updateConnectionByUrl(
        connectionId: String,
        request: IB2BUpdateSAMLConnectionByURLParameters,
    ): B2BUpdateSAMLConnectionByURLResponse

    @Throws(StytchError::class, CancellationException::class)
    public suspend fun deleteVerificationCertificate(
        connectionId: String,
        certificateId: String,
    ): B2BDeleteSAMLVerificationCertificateResponse
}

@JsExport
public interface B2BSSOOIDCClient {
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun createConnection(request: IB2BCreateOIDCConnectionParameters): B2BCreateOIDCConnectionResponse

    @Throws(StytchError::class, CancellationException::class)
    public suspend fun updateConnection(
        connectionId: String,
        request: IB2BUpdateOIDCConnectionParameters,
    ): B2BUpdateOIDCConnectionResponse
}

@JsExport
public interface B2BSSOExternalClient {
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun createConnection(request: IB2BCreateExternalConnectionParameters): B2BCreateExternalConnectionResponse

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

    override suspend fun getConnections(): B2BGetSSOConnectionsResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request { networkingClient.api.b2BGetSSOConnections() }
        }

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
    override suspend fun createConnection(request: IB2BCreateSAMLConnectionParameters): B2BCreateSAMLConnectionResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request { networkingClient.api.b2BCreateSAMLConnection(request.toNetworkModel()) }
        }

    override suspend fun updateConnection(
        connectionId: String,
        request: IB2BUpdateSAMLConnectionParameters,
    ): B2BUpdateSAMLConnectionResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request { networkingClient.api.b2BUpdateSAMLConnection(connectionId, request.toNetworkModel()) }
        }

    override suspend fun updateConnectionByUrl(
        connectionId: String,
        request: IB2BUpdateSAMLConnectionByURLParameters,
    ): B2BUpdateSAMLConnectionByURLResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request { networkingClient.api.b2BUpdateSAMLConnectionByURL(connectionId, request.toNetworkModel()) }
        }

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
    override suspend fun createConnection(request: IB2BCreateOIDCConnectionParameters): B2BCreateOIDCConnectionResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request { networkingClient.api.b2BCreateOIDCConnection(request.toNetworkModel()) }
        }

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
    override suspend fun createConnection(request: IB2BCreateExternalConnectionParameters): B2BCreateExternalConnectionResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request { networkingClient.api.b2BCreateExternalConnection(request.toNetworkModel()) }
        }

    override suspend fun updateConnection(
        connectionId: String,
        request: IB2BUpdateExternalConnectionParameters,
    ): B2BUpdateExternalConnectionResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request { networkingClient.api.b2BUpdateExternalConnection(connectionId, request.toNetworkModel()) }
        }
}
