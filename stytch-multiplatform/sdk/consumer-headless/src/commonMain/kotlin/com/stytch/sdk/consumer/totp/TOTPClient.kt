package com.stytch.sdk.consumer.totp

import com.stytch.sdk.consumer.StytchConsumerAuthenticationStateManager
import com.stytch.sdk.consumer.networking.ConsumerNetworkingClient
import com.stytch.sdk.consumer.networking.models.CryptoWalletsAuthenticateRequest
import com.stytch.sdk.consumer.networking.models.CryptoWalletsAuthenticateResponse
import com.stytch.sdk.consumer.networking.models.CryptoWalletsAuthenticateStartSecondaryRequest
import com.stytch.sdk.consumer.networking.models.ICryptoWalletsAuthenticateParameters
import com.stytch.sdk.consumer.networking.models.ITOTPsAuthenticateParameters
import com.stytch.sdk.consumer.networking.models.ITOTPsCreateParameters
import com.stytch.sdk.consumer.networking.models.ITOTPsRecoverParameters
import com.stytch.sdk.consumer.networking.models.TOTPsAuthenticateResponse
import com.stytch.sdk.consumer.networking.models.TOTPsCreateResponse
import com.stytch.sdk.consumer.networking.models.TOTPsGetRecoveryCodesResponse
import com.stytch.sdk.consumer.networking.models.TOTPsRecoverResponse
import com.stytch.sdk.consumer.networking.models.toNetworkModel
import com.stytch.sdk.data.StytchDispatchers
import kotlinx.coroutines.withContext
import kotlin.js.JsExport

@JsExport
public interface TOTPClient {
    public suspend fun create(request: ITOTPsCreateParameters): TOTPsCreateResponse

    public suspend fun authenticate(request: ITOTPsAuthenticateParameters): TOTPsAuthenticateResponse

    public suspend fun recover(request: ITOTPsRecoverParameters): TOTPsRecoverResponse

    public suspend fun recoveryCodes(): TOTPsGetRecoveryCodesResponse
}

internal class TOTPClientImpl(
    private val dispatchers: StytchDispatchers,
    private val networkingClient: ConsumerNetworkingClient,
) : TOTPClient {
    override suspend fun create(request: ITOTPsCreateParameters): TOTPsCreateResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.tOTPsCreate(request.toNetworkModel())
            }
        }

    override suspend fun authenticate(request: ITOTPsAuthenticateParameters): TOTPsAuthenticateResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.tOTPsAuthenticate(request.toNetworkModel())
            }
        }

    override suspend fun recover(request: ITOTPsRecoverParameters): TOTPsRecoverResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.tOTPsRecover(request.toNetworkModel())
            }
        }

    override suspend fun recoveryCodes() =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.tOTPsGetRecoveryCodes()
            }
        }
}
