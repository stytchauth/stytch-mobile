package com.stytch.sdk.b2b.otp

import com.stytch.sdk.StytchApi
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

/** B2B OTP (one-time passcode) authentication via SMS and email. */
@StytchApi
@JsExport
public interface B2BOtpClient {
    /** SMS OTP methods. */
    public val sms: B2BSmsOtpClient

    /** Email OTP methods. */
    public val email: B2BEmailOtpClient
}

/** B2B SMS OTP methods. */
@StytchApi
@JsExport
public interface B2BSmsOtpClient {
    /** Sends an OTP to the member's phone number for MFA verification. */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun send(request: IB2BOTPsSMSSendParameters): B2BOTPsSMSSendResponse

    /** Authenticates an SMS OTP code submitted by the member. */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun authenticate(request: IB2BOTPsSMSAuthenticateParameters): B2BOTPsSMSAuthenticateResponse
}

/** B2B email OTP methods. */
@StytchApi
@JsExport
public interface B2BEmailOtpClient {
    /** Email OTP discovery methods for listing organizations before a session is established. */
    public val discovery: B2BEmailOtpDiscoveryClient

    /** Sends an email OTP to the provided address for login or signup within the organization. */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun loginOrSignup(request: IB2BOTPsEmailLoginOrSignupParameters): B2BOTPsEmailLoginOrSignupResponse

    /** Authenticates an email OTP code submitted by the member. */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun authenticate(request: IB2BOTPsEmailAuthenticateParameters): B2BOTPsEmailAuthenticateResponse
}

/** Email OTP discovery methods for enumerating organizations before authentication. */
@StytchApi
@JsExport
public interface B2BEmailOtpDiscoveryClient {
    /** Sends a discovery email OTP to enumerate organizations for the given email address. */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun send(request: IB2BOTPsEmailDiscoverySendParameters): B2BOTPsEmailDiscoverySendResponse

    /** Authenticates the discovery email OTP, returning an intermediate session token and discovered organizations. */
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
