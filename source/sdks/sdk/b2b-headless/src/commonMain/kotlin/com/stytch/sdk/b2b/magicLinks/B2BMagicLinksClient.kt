package com.stytch.sdk.b2b.magicLinks

import com.stytch.sdk.StytchApi
import com.stytch.sdk.b2b.StytchB2BAuthenticationStateManager
import com.stytch.sdk.b2b.networking.B2BNetworkingClient
import com.stytch.sdk.b2b.networking.models.B2BMagicLinksAuthenticateResponse
import com.stytch.sdk.b2b.networking.models.B2BMagicLinksDiscoveryAuthenticateResponse
import com.stytch.sdk.b2b.networking.models.B2BMagicLinksDiscoveryEmailSendResponse
import com.stytch.sdk.b2b.networking.models.B2BMagicLinksInviteResponse
import com.stytch.sdk.b2b.networking.models.B2BMagicLinksLoginOrSignupResponse
import com.stytch.sdk.b2b.networking.models.IB2BMagicLinksAuthenticateParameters
import com.stytch.sdk.b2b.networking.models.IB2BMagicLinksDiscoveryAuthenticateParameters
import com.stytch.sdk.b2b.networking.models.IB2BMagicLinksDiscoveryEmailSendParameters
import com.stytch.sdk.b2b.networking.models.IB2BMagicLinksInviteParameters
import com.stytch.sdk.b2b.networking.models.IB2BMagicLinksLoginOrSignupParameters
import com.stytch.sdk.b2b.networking.models.toNetworkModel
import com.stytch.sdk.data.StytchDispatchers
import com.stytch.sdk.data.StytchError
import com.stytch.sdk.pkce.MissingPKCEException
import com.stytch.sdk.pkce.PKCEClient
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException
import kotlin.js.JsExport

@StytchApi
@JsExport
public interface B2BMagicLinksClient {
    public val email: B2BEmailMagicLinksClient
    public val discovery: B2BMagicLinksDiscoveryClient

    @Throws(StytchError::class, CancellationException::class)
    public suspend fun authenticate(request: IB2BMagicLinksAuthenticateParameters): B2BMagicLinksAuthenticateResponse
}

@StytchApi
@JsExport
public interface B2BEmailMagicLinksClient {
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun loginOrSignup(request: IB2BMagicLinksLoginOrSignupParameters): B2BMagicLinksLoginOrSignupResponse

    @Throws(StytchError::class, CancellationException::class)
    public suspend fun invite(request: IB2BMagicLinksInviteParameters): B2BMagicLinksInviteResponse
}

@StytchApi
@JsExport
public interface B2BMagicLinksDiscoveryClient {
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun emailSend(request: IB2BMagicLinksDiscoveryEmailSendParameters): B2BMagicLinksDiscoveryEmailSendResponse

    @Throws(StytchError::class, CancellationException::class)
    public suspend fun authenticate(request: IB2BMagicLinksDiscoveryAuthenticateParameters): B2BMagicLinksDiscoveryAuthenticateResponse
}

internal class B2BMagicLinksClientImpl(
    private val dispatchers: StytchDispatchers,
    private val networkingClient: B2BNetworkingClient,
    private val pkceClient: PKCEClient,
    private val sessionManager: StytchB2BAuthenticationStateManager,
) : B2BMagicLinksClient {
    override val email: B2BEmailMagicLinksClient = B2BEmailMagicLinksClientImpl(dispatchers, networkingClient, pkceClient)
    override val discovery: B2BMagicLinksDiscoveryClient = B2BMagicLinksDiscoveryClientImpl(dispatchers, networkingClient, pkceClient)

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun authenticate(request: IB2BMagicLinksAuthenticateParameters): B2BMagicLinksAuthenticateResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                val codePair = pkceClient.retrieve() ?: throw MissingPKCEException()
                networkingClient.api
                    .b2BMagicLinksAuthenticate(
                        request.toNetworkModel(
                            pkceCodeVerifier = codePair.verifier,
                            intermediateSessionToken = sessionManager.intermediateSessionToken,
                        ),
                    ).also {
                        pkceClient.revoke()
                    }
            }
        }
}

internal class B2BEmailMagicLinksClientImpl(
    private val dispatchers: StytchDispatchers,
    private val networkingClient: B2BNetworkingClient,
    private val pkceClient: PKCEClient,
) : B2BEmailMagicLinksClient {
    @Throws(StytchError::class, CancellationException::class)
    override suspend fun loginOrSignup(request: IB2BMagicLinksLoginOrSignupParameters): B2BMagicLinksLoginOrSignupResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                val codePair = pkceClient.create()
                networkingClient.api.b2BMagicLinksLoginOrSignup(request.toNetworkModel(pkceCodeChallenge = codePair.challenge))
            }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun invite(request: IB2BMagicLinksInviteParameters): B2BMagicLinksInviteResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.b2BMagicLinksInvite(request.toNetworkModel())
            }
        }
}

internal class B2BMagicLinksDiscoveryClientImpl(
    private val dispatchers: StytchDispatchers,
    private val networkingClient: B2BNetworkingClient,
    private val pkceClient: PKCEClient,
) : B2BMagicLinksDiscoveryClient {
    @Throws(StytchError::class, CancellationException::class)
    override suspend fun emailSend(request: IB2BMagicLinksDiscoveryEmailSendParameters): B2BMagicLinksDiscoveryEmailSendResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                val codePair = pkceClient.create()
                networkingClient.api.b2BMagicLinksDiscoveryEmailSend(request.toNetworkModel(pkceCodeChallenge = codePair.challenge))
            }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun authenticate(request: IB2BMagicLinksDiscoveryAuthenticateParameters): B2BMagicLinksDiscoveryAuthenticateResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                val codePair = pkceClient.retrieve() ?: throw MissingPKCEException()
                networkingClient.api.b2BMagicLinksDiscoveryAuthenticate(request.toNetworkModel(pkceCodeVerifier = codePair.verifier)).also {
                    pkceClient.revoke()
                }
            }
        }
}
