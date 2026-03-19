package com.stytch.sdk.consumer.totp

import com.stytch.sdk.StytchApi
import com.stytch.sdk.consumer.networking.ConsumerNetworkingClient
import com.stytch.sdk.consumer.networking.models.ITOTPsAuthenticateParameters
import com.stytch.sdk.consumer.networking.models.ITOTPsCreateParameters
import com.stytch.sdk.consumer.networking.models.ITOTPsRecoverParameters
import com.stytch.sdk.consumer.networking.models.TOTPsAuthenticateResponse
import com.stytch.sdk.consumer.networking.models.TOTPsCreateResponse
import com.stytch.sdk.consumer.networking.models.TOTPsGetRecoveryCodesResponse
import com.stytch.sdk.consumer.networking.models.TOTPsRecoverResponse
import com.stytch.sdk.consumer.networking.models.toNetworkModel
import com.stytch.sdk.data.StytchDispatchers
import com.stytch.sdk.data.StytchError
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException
import kotlin.js.JsExport

/** TOTP (time-based one-time passcode) authentication methods. */
@StytchApi
@JsExport
public interface TOTPClient {
    /** Creates a new TOTP instance for the current user, returning a secret and QR code URL. */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun create(request: ITOTPsCreateParameters): TOTPsCreateResponse

    /** Authenticates a TOTP code from the user's authenticator app. */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun authenticate(request: ITOTPsAuthenticateParameters): TOTPsAuthenticateResponse

    /** Authenticates using a TOTP recovery code instead of a time-based code. */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun recover(request: ITOTPsRecoverParameters): TOTPsRecoverResponse

    /** Retrieves the recovery codes for the current user's TOTP instance. */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun recoveryCodes(): TOTPsGetRecoveryCodesResponse
}

internal class TOTPClientImpl(
    private val dispatchers: StytchDispatchers,
    private val networkingClient: ConsumerNetworkingClient,
) : TOTPClient {
    @Throws(StytchError::class, CancellationException::class)
    override suspend fun create(request: ITOTPsCreateParameters): TOTPsCreateResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.tOTPsCreate(request.toNetworkModel())
            }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun authenticate(request: ITOTPsAuthenticateParameters): TOTPsAuthenticateResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.tOTPsAuthenticate(request.toNetworkModel())
            }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun recover(request: ITOTPsRecoverParameters): TOTPsRecoverResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.tOTPsRecover(request.toNetworkModel())
            }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun recoveryCodes() =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.tOTPsGetRecoveryCodes()
            }
        }
}
