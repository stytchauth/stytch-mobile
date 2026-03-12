package com.stytch.sdk.b2b.session

import com.stytch.sdk.b2b.B2BClientTest
import com.stytch.sdk.b2b.networking.models.IB2BSessionsAccessTokenExchangeParameters
import com.stytch.sdk.b2b.networking.models.IB2BSessionsAttestParameters
import com.stytch.sdk.b2b.networking.models.IB2BSessionsAuthenticateParameters
import com.stytch.sdk.b2b.networking.models.IB2BSessionsExchangeParameters
import com.stytch.sdk.data.StytchDataResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class B2BSessionsClientImplTest : B2BClientTest() {
    private val client = B2BSessionsClientImpl(dispatchers, networkingClient)

    @Test
    fun `authenticate calls b2BSessionsAuthenticate`() =
        runTest(testDispatcher) {
            coEvery { api.b2BSessionsAuthenticate(any()) } returns StytchDataResponse(mockk(relaxed = true))

            client.authenticate(mockk<IB2BSessionsAuthenticateParameters>(relaxed = true))

            coVerify { api.b2BSessionsAuthenticate(any()) }
        }

    @Test
    fun `revoke calls b2BSessionsRevoke`() =
        runTest(testDispatcher) {
            coEvery { api.b2BSessionsRevoke() } returns StytchDataResponse(mockk(relaxed = true))

            client.revoke()

            coVerify { api.b2BSessionsRevoke() }
        }

    @Test
    fun `exchange calls b2BSessionsExchange`() =
        runTest(testDispatcher) {
            coEvery { api.b2BSessionsExchange(any()) } returns StytchDataResponse(mockk(relaxed = true))

            client.exchange(mockk<IB2BSessionsExchangeParameters>(relaxed = true))

            coVerify { api.b2BSessionsExchange(any()) }
        }

    @Test
    fun `exchangeAccessToken calls b2BSessionsAccessTokenExchange`() =
        runTest(testDispatcher) {
            coEvery { api.b2BSessionsAccessTokenExchange(any()) } returns StytchDataResponse(mockk(relaxed = true))

            client.exchangeAccessToken(mockk<IB2BSessionsAccessTokenExchangeParameters>(relaxed = true))

            coVerify { api.b2BSessionsAccessTokenExchange(any()) }
        }

    @Test
    fun `attest calls b2BSessionsAttest`() =
        runTest(testDispatcher) {
            coEvery { api.b2BSessionsAttest(any()) } returns StytchDataResponse(mockk(relaxed = true))

            client.attest(mockk<IB2BSessionsAttestParameters>(relaxed = true))

            coVerify { api.b2BSessionsAttest(any()) }
        }
}
