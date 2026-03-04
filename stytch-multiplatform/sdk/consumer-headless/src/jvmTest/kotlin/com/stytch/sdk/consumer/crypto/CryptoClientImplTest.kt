package com.stytch.sdk.consumer.crypto

import com.stytch.sdk.consumer.ConsumerClientTest
import com.stytch.sdk.consumer.StytchConsumerAuthenticationStateManager
import com.stytch.sdk.consumer.networking.models.CryptoWalletsAuthenticateParameters
import com.stytch.sdk.consumer.networking.models.CryptoWalletsAuthenticateRequest
import com.stytch.sdk.consumer.networking.models.CryptoWalletsAuthenticateStartPrimaryResponse
import com.stytch.sdk.consumer.networking.models.CryptoWalletsAuthenticateStartSecondaryRequest
import com.stytch.sdk.consumer.networking.models.CryptoWalletsAuthenticateStartSecondaryResponse
import com.stytch.sdk.data.StytchDataResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
internal class CryptoClientImplTest : ConsumerClientTest() {
    private val sessionManager = mockk<StytchConsumerAuthenticationStateManager>(relaxed = true)
    private val client = CryptoClientImpl(dispatchers, networkingClient, sessionManager)

    private val params = CryptoWalletsAuthenticateParameters(
        cryptoWalletAddress = "0xabc123",
        cryptoWalletType = "ethereum",
        sessionDurationMinutes = 30,
    )
    private val startRequest = CryptoWalletsAuthenticateStartSecondaryRequest(
        cryptoWalletType = "ethereum",
        cryptoWalletAddress = "0xabc123",
    )

    @Test
    fun `authenticate calls primary start, threads challenge to signChallenge, passes signature to authenticate`() =
        runTest(testDispatcher) {
            every { sessionManager.currentSessionToken } returns null

            val primaryStartResponse = mockk<CryptoWalletsAuthenticateStartPrimaryResponse>()
            every { primaryStartResponse.challenge } returns "test-challenge"
            coEvery { api.cryptoWalletsAuthenticateStartPrimary(any()) } returns StytchDataResponse(primaryStartResponse)
            coEvery { api.cryptoWalletsAuthenticate(any()) } returns StytchDataResponse(mockk(relaxed = true))

            val capturedChallenges = mutableListOf<String>()
            client.authenticate(params) { challenge ->
                capturedChallenges += challenge
                "signed-$challenge"
            }

            coVerify { api.cryptoWalletsAuthenticateStartPrimary(startRequest) }
            assertEquals(listOf("test-challenge"), capturedChallenges)
            coVerify {
                api.cryptoWalletsAuthenticate(
                    CryptoWalletsAuthenticateRequest(
                        cryptoWalletAddress = "0xabc123",
                        cryptoWalletType = "ethereum",
                        signature = "signed-test-challenge",
                        sessionDurationMinutes = 30,
                    ),
                )
            }
            coVerify(exactly = 0) { api.cryptoWalletsAuthenticateStartSecondary(any()) }
        }

    @Test
    fun `authenticate calls secondary start when session token is present`() = runTest(testDispatcher) {
        every { sessionManager.currentSessionToken } returns "session-tok"

        val secondaryStartResponse = mockk<CryptoWalletsAuthenticateStartSecondaryResponse>()
        every { secondaryStartResponse.challenge } returns "test-challenge"
        coEvery { api.cryptoWalletsAuthenticateStartSecondary(any()) } returns StytchDataResponse(secondaryStartResponse)
        coEvery { api.cryptoWalletsAuthenticate(any()) } returns StytchDataResponse(mockk(relaxed = true))

        client.authenticate(params) { "sig" }

        coVerify { api.cryptoWalletsAuthenticateStartSecondary(startRequest) }
        coVerify(exactly = 0) { api.cryptoWalletsAuthenticateStartPrimary(any()) }
    }
}
