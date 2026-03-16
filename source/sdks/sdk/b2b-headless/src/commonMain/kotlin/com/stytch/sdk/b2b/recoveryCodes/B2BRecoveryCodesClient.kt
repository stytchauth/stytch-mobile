package com.stytch.sdk.b2b.recoveryCodes

import com.stytch.sdk.b2b.StytchB2BAuthenticationStateManager
import com.stytch.sdk.b2b.networking.B2BNetworkingClient
import com.stytch.sdk.b2b.networking.models.B2BRecoveryCodesGetResponse
import com.stytch.sdk.b2b.networking.models.B2BRecoveryCodesRecoverResponse
import com.stytch.sdk.b2b.networking.models.B2BRecoveryCodesRotateResponse
import com.stytch.sdk.b2b.networking.models.IB2BRecoveryCodesRecoverParameters
import com.stytch.sdk.b2b.networking.models.IB2BRecoveryCodesRotateParameters
import com.stytch.sdk.b2b.networking.models.toNetworkModel
import com.stytch.sdk.data.StytchDispatchers
import com.stytch.sdk.data.StytchError
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException
import kotlin.js.JsExport

@JsExport
public interface B2BRecoveryCodesClient {
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun get(): B2BRecoveryCodesGetResponse

    @Throws(StytchError::class, CancellationException::class)
    public suspend fun recover(request: IB2BRecoveryCodesRecoverParameters): B2BRecoveryCodesRecoverResponse

    @Throws(StytchError::class, CancellationException::class)
    public suspend fun rotate(request: IB2BRecoveryCodesRotateParameters): B2BRecoveryCodesRotateResponse
}

internal class B2BRecoveryCodesClientImpl(
    private val dispatchers: StytchDispatchers,
    private val networkingClient: B2BNetworkingClient,
    private val sessionManager: StytchB2BAuthenticationStateManager,
) : B2BRecoveryCodesClient {
    @Throws(StytchError::class, CancellationException::class)
    override suspend fun get(): B2BRecoveryCodesGetResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request { networkingClient.api.b2BRecoveryCodesGet() }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun recover(request: IB2BRecoveryCodesRecoverParameters): B2BRecoveryCodesRecoverResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.b2BRecoveryCodesRecover(
                    request.toNetworkModel(intermediateSessionToken = sessionManager.intermediateSessionToken),
                )
            }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun rotate(request: IB2BRecoveryCodesRotateParameters): B2BRecoveryCodesRotateResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request { networkingClient.api.b2BRecoveryCodesRotate(request.toNetworkModel()) }
        }
}
