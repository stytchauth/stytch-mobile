package com.stytch.sdk.b2b.discovery

import com.stytch.sdk.StytchApi
import com.stytch.sdk.b2b.StytchB2BAuthenticationStateManager
import com.stytch.sdk.b2b.networking.B2BNetworkingClient
import com.stytch.sdk.b2b.networking.models.B2BDiscoveryIntermediateSessionsExchangeResponse
import com.stytch.sdk.b2b.networking.models.B2BDiscoveryOrganizationsCreateResponse
import com.stytch.sdk.b2b.networking.models.B2BDiscoveryOrganizationsResponse
import com.stytch.sdk.b2b.networking.models.B2BDiscoveryPasswordResetResponse
import com.stytch.sdk.b2b.networking.models.B2BDiscoveryPasswordResetStartResponse
import com.stytch.sdk.b2b.networking.models.B2BPasswordDiscoveryAuthenticateResponse
import com.stytch.sdk.b2b.networking.models.IB2BDiscoveryIntermediateSessionsExchangeParameters
import com.stytch.sdk.b2b.networking.models.IB2BDiscoveryOrganizationsCreateParameters
import com.stytch.sdk.b2b.networking.models.IB2BDiscoveryOrganizationsParameters
import com.stytch.sdk.b2b.networking.models.IB2BDiscoveryPasswordResetParameters
import com.stytch.sdk.b2b.networking.models.IB2BDiscoveryPasswordResetStartParameters
import com.stytch.sdk.b2b.networking.models.IB2BPasswordDiscoveryAuthenticateParameters
import com.stytch.sdk.b2b.networking.models.toNetworkModel
import com.stytch.sdk.data.StytchDispatchers
import com.stytch.sdk.data.StytchError
import com.stytch.sdk.pkce.MissingPKCEException
import com.stytch.sdk.pkce.PKCEClient
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException
import kotlin.js.JsExport

/**
 * Discovery flow methods for listing and joining organizations.
 * Used after obtaining an intermediate session token via magic link, OTP, OAuth, or SSO discovery.
 */
@StytchApi
@JsExport
public interface B2BDiscoveryClient {
    /** Methods for listing and creating organizations during the discovery flow. */
    public val organizations: B2BDiscoveryOrganizationsClient

    /** Methods for exchanging an intermediate session token into a full member session. */
    public val intermediateSessions: B2BDiscoveryIntermediateSessionsClient

    /** Password-based authentication and reset within the discovery flow. */
    public val passwords: B2BDiscoveryPasswordsClient
}

/** Organization discovery methods. */
@StytchApi
@JsExport
public interface B2BDiscoveryOrganizationsClient {
    /** Lists all organizations discoverable for the current intermediate session token. */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun list(request: IB2BDiscoveryOrganizationsParameters): B2BDiscoveryOrganizationsResponse

    /** Creates a new organization and establishes a member session within it. */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun create(request: IB2BDiscoveryOrganizationsCreateParameters): B2BDiscoveryOrganizationsCreateResponse
}

/** Intermediate session exchange — converts a discovery token into a full member session. */
@StytchApi
@JsExport
public interface B2BDiscoveryIntermediateSessionsClient {
    /** Exchanges an intermediate session token for a full member session in the selected organization. */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun exchange(
        request: IB2BDiscoveryIntermediateSessionsExchangeParameters,
    ): B2BDiscoveryIntermediateSessionsExchangeResponse
}

/** Password authentication and reset within the discovery flow (before an org is selected). */
@StytchApi
@JsExport
public interface B2BDiscoveryPasswordsClient {
    /** Authenticates an email/password credential during the discovery flow. */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun authenticate(request: IB2BPasswordDiscoveryAuthenticateParameters): B2BPasswordDiscoveryAuthenticateResponse

    /** Initiates a password reset during the discovery flow. */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun resetStart(request: IB2BDiscoveryPasswordResetStartParameters): B2BDiscoveryPasswordResetStartResponse

    /** Completes the password reset during the discovery flow. */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun reset(request: IB2BDiscoveryPasswordResetParameters): B2BDiscoveryPasswordResetResponse
}

internal class B2BDiscoveryClientImpl(
    private val dispatchers: StytchDispatchers,
    private val networkingClient: B2BNetworkingClient,
    private val pkceClient: PKCEClient,
    private val sessionManager: StytchB2BAuthenticationStateManager,
) : B2BDiscoveryClient {
    override val organizations: B2BDiscoveryOrganizationsClient =
        B2BDiscoveryOrganizationsClientImpl(dispatchers, networkingClient, sessionManager)
    override val intermediateSessions: B2BDiscoveryIntermediateSessionsClient =
        B2BDiscoveryIntermediateSessionsClientImpl(dispatchers, networkingClient, sessionManager)
    override val passwords: B2BDiscoveryPasswordsClient =
        B2BDiscoveryPasswordsClientImpl(dispatchers, networkingClient, pkceClient)
}

internal class B2BDiscoveryOrganizationsClientImpl(
    private val dispatchers: StytchDispatchers,
    private val networkingClient: B2BNetworkingClient,
    private val sessionManager: StytchB2BAuthenticationStateManager,
) : B2BDiscoveryOrganizationsClient {
    @Throws(StytchError::class, CancellationException::class)
    override suspend fun list(request: IB2BDiscoveryOrganizationsParameters): B2BDiscoveryOrganizationsResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.b2BDiscoveryOrganizations(
                    request.toNetworkModel(intermediateSessionToken = sessionManager.intermediateSessionToken),
                )
            }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun create(request: IB2BDiscoveryOrganizationsCreateParameters): B2BDiscoveryOrganizationsCreateResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.b2BDiscoveryOrganizationsCreate(
                    request.toNetworkModel(intermediateSessionToken = sessionManager.intermediateSessionToken ?: ""),
                )
            }
        }
}

internal class B2BDiscoveryIntermediateSessionsClientImpl(
    private val dispatchers: StytchDispatchers,
    private val networkingClient: B2BNetworkingClient,
    private val sessionManager: StytchB2BAuthenticationStateManager,
) : B2BDiscoveryIntermediateSessionsClient {
    @Throws(StytchError::class, CancellationException::class)
    override suspend fun exchange(
        request: IB2BDiscoveryIntermediateSessionsExchangeParameters,
    ): B2BDiscoveryIntermediateSessionsExchangeResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.b2BDiscoveryIntermediateSessionsExchange(
                    request.toNetworkModel(intermediateSessionToken = sessionManager.intermediateSessionToken ?: ""),
                )
            }
        }
}

internal class B2BDiscoveryPasswordsClientImpl(
    private val dispatchers: StytchDispatchers,
    private val networkingClient: B2BNetworkingClient,
    private val pkceClient: PKCEClient,
) : B2BDiscoveryPasswordsClient {
    @Throws(StytchError::class, CancellationException::class)
    override suspend fun authenticate(request: IB2BPasswordDiscoveryAuthenticateParameters): B2BPasswordDiscoveryAuthenticateResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.b2BPasswordDiscoveryAuthenticate(request.toNetworkModel())
            }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun resetStart(request: IB2BDiscoveryPasswordResetStartParameters): B2BDiscoveryPasswordResetStartResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                val codePair = pkceClient.create()
                networkingClient.api.b2BDiscoveryPasswordResetStart(
                    request.toNetworkModel(pkceCodeChallenge = codePair.challenge),
                )
            }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun reset(request: IB2BDiscoveryPasswordResetParameters): B2BDiscoveryPasswordResetResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                val codePair = pkceClient.retrieve() ?: throw MissingPKCEException()
                networkingClient.api
                    .b2BDiscoveryPasswordReset(
                        request.toNetworkModel(pkceCodeVerifier = codePair.verifier),
                    ).also { pkceClient.revoke() }
            }
        }
}
