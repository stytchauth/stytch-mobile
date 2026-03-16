package com.stytch.sdk.b2b.totp

import com.stytch.sdk.StytchApi
import com.stytch.sdk.b2b.StytchB2BAuthenticationStateManager
import com.stytch.sdk.b2b.networking.B2BNetworkingClient
import com.stytch.sdk.b2b.networking.models.B2BTOTPsAuthenticateResponse
import com.stytch.sdk.b2b.networking.models.B2BTOTPsCreateResponse
import com.stytch.sdk.b2b.networking.models.IB2BTOTPsAuthenticateParameters
import com.stytch.sdk.b2b.networking.models.IB2BTOTPsCreateParameters
import com.stytch.sdk.b2b.networking.models.toNetworkModel
import com.stytch.sdk.data.StytchDispatchers
import com.stytch.sdk.data.StytchError
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException
import kotlin.js.JsExport

@StytchApi
@JsExport
public interface B2BTOTPClient {
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun create(request: IB2BTOTPsCreateParameters): B2BTOTPsCreateResponse

    @Throws(StytchError::class, CancellationException::class)
    public suspend fun authenticate(request: IB2BTOTPsAuthenticateParameters): B2BTOTPsAuthenticateResponse
}

internal class B2BTOTPClientImpl(
    private val dispatchers: StytchDispatchers,
    private val networkingClient: B2BNetworkingClient,
    private val sessionManager: StytchB2BAuthenticationStateManager,
) : B2BTOTPClient {
    @Throws(StytchError::class, CancellationException::class)
    override suspend fun create(request: IB2BTOTPsCreateParameters): B2BTOTPsCreateResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.b2BTOTPsCreate(
                    request.toNetworkModel(intermediateSessionToken = sessionManager.intermediateSessionToken),
                )
            }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun authenticate(request: IB2BTOTPsAuthenticateParameters): B2BTOTPsAuthenticateResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.b2BTOTPsAuthenticate(
                    request.toNetworkModel(intermediateSessionToken = sessionManager.intermediateSessionToken),
                )
            }
        }
}
