package com.stytch.sdk.consumer.crypto

import com.stytch.sdk.consumer.StytchConsumerAuthenticationStateManager
import com.stytch.sdk.consumer.networking.ConsumerNetworkingClient
import com.stytch.sdk.consumer.networking.models.CryptoWalletsAuthenticateRequest
import com.stytch.sdk.consumer.networking.models.CryptoWalletsAuthenticateResponse
import com.stytch.sdk.consumer.networking.models.CryptoWalletsAuthenticateStartSecondaryRequest
import com.stytch.sdk.consumer.networking.models.ICryptoWalletsAuthenticateParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.js.JsExport

@JsExport
public interface CryptoClient {
    public suspend fun authenticate(
        request: ICryptoWalletsAuthenticateParameters,
        signChallenge: suspend (String) -> String,
    ): CryptoWalletsAuthenticateResponse
}

internal class CryptoClientImpl(
    private val networkingClient: ConsumerNetworkingClient,
    private val authenticationStateManager: StytchConsumerAuthenticationStateManager,
) : CryptoClient {
    override suspend fun authenticate(
        request: ICryptoWalletsAuthenticateParameters,
        signChallenge: suspend (String) -> String,
    ): CryptoWalletsAuthenticateResponse =
        withContext(Dispatchers.Default) {
            networkingClient.request {
                val challenge =
                    if (authenticationStateManager.currentSessionToken.isNullOrEmpty()) {
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
                        signature = signChallenge(challenge),
                        sessionDurationMinutes = request.sessionDurationMinutes,
                    ),
                )
            }
        }
}
