package com.stytch.sdk.consumer.crypto

import com.stytch.sdk.StytchApi
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

/** Crypto wallet authentication methods. */
@StytchApi
@JsExport
public interface CryptoClient {
    /**
     * Authenticates a crypto wallet by signing a server-issued challenge with the wallet's private key.
     * Performs a two-step flow: calls `POST /sdk/v1/crypto_wallets/authenticate/start/primary` (no session)
     * or `POST /sdk/v1/crypto_wallets/authenticate/start/secondary` (with session) to get a challenge
     * string, invokes [signChallenge] to sign it, then calls `POST /sdk/v1/crypto_wallets/authenticate`
     * to complete authentication.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchConsumer.crypto.authenticate(
     *     request = CryptoWalletsAuthenticateParameters(
     *         cryptoWalletAddress = "0xABC123...",
     *         cryptoWalletType = "ethereum",
     *         sessionDurationMinutes = 30,
     *     ),
     *     signChallenge = { challenge -> myWallet.signMessage(challenge) },
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = CryptoWalletsAuthenticateParameters(
     *     cryptoWalletAddress: "0xABC123...",
     *     cryptoWalletType: "ethereum",
     *     sessionDurationMinutes: 30
     * )
     * let response = try await StytchConsumer.crypto.authenticate(params) { challenge in
     *     return myWallet.signMessage(challenge)
     * }
     * ```
     *
     * **React Native:**
     * ```js
     * StytchConsumer.crypto.authenticate(
     *     { cryptoWalletAddress: "0xABC123...", cryptoWalletType: "ethereum", sessionDurationMinutes: 30 },
     *     (challenge) => myWallet.signMessage(challenge),
     * )
     * ```
     *
     * @param request - [ICryptoWalletsAuthenticateParameters]
     *   - `cryptoWalletAddress` — The public wallet address (e.g. an Ethereum address).
     *   - `cryptoWalletType` — The wallet type identifier (e.g. `"ethereum"`, `"solana"`).
     *   - `sessionDurationMinutes` — Duration of the session to create, in minutes.
     * @param signChallenge - A suspend function that receives the server-issued challenge string and
     *   returns the hex-encoded signature produced by the wallet's private key.
     *
     * @return [CryptoWalletsAuthenticateResponse] containing the authenticated session and user.
     *
     * @throws [StytchError] if the wallet address is not registered or the signature is invalid.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
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
