package com.stytch.sdk.consumer.crypto

import com.stytch.sdk.consumer.StytchConsumerAuthenticationStateManager
import com.stytch.sdk.consumer.networking.ConsumerNetworkingClient
import com.stytch.sdk.consumer.networking.models.CryptoWalletsAuthenticateRequest
import com.stytch.sdk.consumer.networking.models.CryptoWalletsAuthenticateResponse
import com.stytch.sdk.consumer.networking.models.CryptoWalletsAuthenticateStartSecondaryRequest
import com.stytch.sdk.consumer.networking.models.ICryptoWalletsAuthenticateParameters
import com.stytch.sdk.data.StytchDispatchers
import com.stytch.sdk.data.StytchError
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException
import kotlin.js.JsExport
import com.stytch.sdk.StytchApi

@StytchApi
@JsExport
public interface CryptoClient {
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun authenticate(
        request: ICryptoWalletsAuthenticateParameters,
        signChallenge: suspend (String) -> String,
    ): CryptoWalletsAuthenticateResponse
}

internal class CryptoClientImpl(
    private val dispatchers: StytchDispatchers,
    private val networkingClient: ConsumerNetworkingClient,
    private val sessionManager: StytchConsumerAuthenticationStateManager,
) : CryptoClient {
    @Throws(StytchError::class, CancellationException::class)
    override suspend fun authenticate(
        request: ICryptoWalletsAuthenticateParameters,
        signChallenge: suspend (String) -> String,
    ): CryptoWalletsAuthenticateResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                val challenge =
                    if (sessionManager.currentSessionToken.isNullOrEmpty()) {
                        networkingClient.api
                            .cryptoWalletsAuthenticateStartPrimary(
                                CryptoWalletsAuthenticateStartSecondaryRequest(
                                    cryptoWalletType = request.cryptoWalletType,
                                    cryptoWalletAddress = request.cryptoWalletAddress,
                                ),
                            ).data.challenge
                    } else {
                        networkingClient.api
                            .cryptoWalletsAuthenticateStartSecondary(
                                CryptoWalletsAuthenticateStartSecondaryRequest(
                                    cryptoWalletType = request.cryptoWalletType,
                                    cryptoWalletAddress = request.cryptoWalletAddress,
                                ),
                            ).data.challenge
                    }
                networkingClient.api.cryptoWalletsAuthenticate(
                    CryptoWalletsAuthenticateRequest(
                        cryptoWalletType = request.cryptoWalletType,
                        cryptoWalletAddress = request.cryptoWalletAddress,
                        signature =
                            withContext(dispatchers.mainDispatcher) {
                                signChallenge(challenge)
                            },
                        sessionDurationMinutes = request.sessionDurationMinutes,
                    ),
                )
            }
        }
}
