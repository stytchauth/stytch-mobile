package com.stytch.sdk.b2b.otp

import com.stytch.sdk.b2b.StytchB2BAuthenticationStateManager
import com.stytch.sdk.b2b.networking.B2BNetworkingClient
import com.stytch.sdk.b2b.networking.models.B2BOTPsEmailAuthenticateResponse
import com.stytch.sdk.b2b.networking.models.B2BOTPsEmailDiscoveryAuthenticateResponse
import com.stytch.sdk.b2b.networking.models.B2BOTPsEmailDiscoverySendResponse
import com.stytch.sdk.b2b.networking.models.B2BOTPsEmailLoginOrSignupResponse
import com.stytch.sdk.b2b.networking.models.B2BOTPsSMSAuthenticateResponse
import com.stytch.sdk.b2b.networking.models.B2BOTPsSMSSendResponse
import com.stytch.sdk.b2b.networking.models.IB2BOTPsEmailAuthenticateParameters
import com.stytch.sdk.b2b.networking.models.IB2BOTPsEmailDiscoveryAuthenticateParameters
import com.stytch.sdk.b2b.networking.models.IB2BOTPsEmailDiscoverySendParameters
import com.stytch.sdk.b2b.networking.models.IB2BOTPsEmailLoginOrSignupParameters
import com.stytch.sdk.b2b.networking.models.IB2BOTPsSMSAuthenticateParameters
import com.stytch.sdk.b2b.networking.models.IB2BOTPsSMSSendParameters
import com.stytch.sdk.b2b.networking.models.toNetworkModel
import com.stytch.sdk.data.StytchDispatchers
import com.stytch.sdk.data.StytchError
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException
import kotlin.js.JsExport
import com.stytch.sdk.StytchApi

@StytchApi
@JsExport
public interface B2BOtpClient {
    public val sms: B2BSmsOtpClient
    public val email: B2BEmailOtpClient
}

@StytchApi
@JsExport
public interface B2BSmsOtpClient {
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun send(request: IB2BOTPsSMSSendParameters): B2BOTPsSMSSendResponse

    @Throws(StytchError::class, CancellationException::class)
    public suspend fun authenticate(request: IB2BOTPsSMSAuthenticateParameters): B2BOTPsSMSAuthenticateResponse
}

@StytchApi
@JsExport
public interface B2BEmailOtpClient {
    public val discovery: B2BEmailOtpDiscoveryClient

    @Throws(StytchError::class, CancellationException::class)
    public suspend fun loginOrSignup(request: IB2BOTPsEmailLoginOrSignupParameters): B2BOTPsEmailLoginOrSignupResponse

    @Throws(StytchError::class, CancellationException::class)
    public suspend fun authenticate(request: IB2BOTPsEmailAuthenticateParameters): B2BOTPsEmailAuthenticateResponse
}

@StytchApi
@JsExport
public interface B2BEmailOtpDiscoveryClient {
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun send(request: IB2BOTPsEmailDiscoverySendParameters): B2BOTPsEmailDiscoverySendResponse

    @Throws(StytchError::class, CancellationException::class)
    public suspend fun authenticate(request: IB2BOTPsEmailDiscoveryAuthenticateParameters): B2BOTPsEmailDiscoveryAuthenticateResponse
}

internal class B2BOtpClientImpl(
    private val dispatchers: StytchDispatchers,
    private val networkingClient: B2BNetworkingClient,
    private val sessionManager: StytchB2BAuthenticationStateManager,
) : B2BOtpClient {
    override val sms: B2BSmsOtpClient = B2BSmsOtpClientImpl(dispatchers, networkingClient, sessionManager)
    override val email: B2BEmailOtpClient = B2BEmailOtpClientImpl(dispatchers, networkingClient, sessionManager)
}

internal class B2BSmsOtpClientImpl(
    private val dispatchers: StytchDispatchers,
    private val networkingClient: B2BNetworkingClient,
    private val sessionManager: StytchB2BAuthenticationStateManager,
) : B2BSmsOtpClient {
    @Throws(StytchError::class, CancellationException::class)
    override suspend fun send(request: IB2BOTPsSMSSendParameters): B2BOTPsSMSSendResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.b2BOTPsSMSSend(
                    request.toNetworkModel(intermediateSessionToken = sessionManager.intermediateSessionToken),
                )
            }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun authenticate(request: IB2BOTPsSMSAuthenticateParameters): B2BOTPsSMSAuthenticateResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.b2BOTPsSMSAuthenticate(
                    request.toNetworkModel(intermediateSessionToken = sessionManager.intermediateSessionToken),
                )
            }
        }
}

internal class B2BEmailOtpClientImpl(
    private val dispatchers: StytchDispatchers,
    private val networkingClient: B2BNetworkingClient,
    private val sessionManager: StytchB2BAuthenticationStateManager,
) : B2BEmailOtpClient {
    override val discovery: B2BEmailOtpDiscoveryClient = B2BEmailOtpDiscoveryClientImpl(dispatchers, networkingClient)

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun loginOrSignup(request: IB2BOTPsEmailLoginOrSignupParameters): B2BOTPsEmailLoginOrSignupResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.b2BOTPsEmailLoginOrSignup(request.toNetworkModel())
            }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun authenticate(request: IB2BOTPsEmailAuthenticateParameters): B2BOTPsEmailAuthenticateResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.b2BOTPsEmailAuthenticate(
                    request.toNetworkModel(intermediateSessionToken = sessionManager.intermediateSessionToken),
                )
            }
        }
}

internal class B2BEmailOtpDiscoveryClientImpl(
    private val dispatchers: StytchDispatchers,
    private val networkingClient: B2BNetworkingClient,
) : B2BEmailOtpDiscoveryClient {
    @Throws(StytchError::class, CancellationException::class)
    override suspend fun send(request: IB2BOTPsEmailDiscoverySendParameters): B2BOTPsEmailDiscoverySendResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.b2BOTPsEmailDiscoverySend(request.toNetworkModel())
            }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun authenticate(request: IB2BOTPsEmailDiscoveryAuthenticateParameters): B2BOTPsEmailDiscoveryAuthenticateResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.b2BOTPsEmailDiscoveryAuthenticate(request.toNetworkModel())
            }
        }
}
