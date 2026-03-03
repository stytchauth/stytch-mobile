package com.stytch.sdk.consumer.session

import com.stytch.sdk.consumer.networking.ConsumerNetworkingClient
import com.stytch.sdk.consumer.networking.api.SdkExternalApi
import com.stytch.sdk.consumer.networking.models.SessionsAttestParameters
import com.stytch.sdk.consumer.networking.models.SessionsAttestRequest
import com.stytch.sdk.consumer.networking.models.SessionsAuthenticateParameters
import com.stytch.sdk.consumer.networking.models.SessionsAuthenticateRequest
import com.stytch.sdk.data.StytchDataResponse
import com.stytch.sdk.data.StytchDispatchers
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SessionImplTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private val dispatchers = StytchDispatchers(ioDispatcher = testDispatcher, mainDispatcher = testDispatcher)

    private val api = mockk<SdkExternalApi>(relaxed = true)
    private val networkingClient = mockk<ConsumerNetworkingClient>(relaxed = true)
    private val client = SessionImpl(dispatchers, networkingClient)

    @BeforeTest
    fun setup() {
        // api is an internal val — must be stubbed outside the mockk{} block to match the
        // mangled JVM getter name correctly
        every { networkingClient.api } returns api
        // Execute the lambda so we can verify which api method was called
        coEvery { networkingClient.request<Any>(any()) } coAnswers {
            @Suppress("UNCHECKED_CAST")
            (firstArg<suspend () -> StytchDataResponse<*>>())().data!!
        }
    }

    @Test
    fun `authenticate calls sessionsAuthenticate with correct network model`() = runTest(testDispatcher) {
        val params = SessionsAuthenticateParameters(sessionDurationMinutes = 30)
        coEvery { api.sessionsAuthenticate(any()) } returns StytchDataResponse(mockk(relaxed = true))

        client.authenticate(params)

        coVerify { api.sessionsAuthenticate(SessionsAuthenticateRequest(sessionDurationMinutes = 30)) }
    }

    @Test
    fun `revoke calls sessionsRevoke`() = runTest(testDispatcher) {
        coEvery { api.sessionsRevoke() } returns StytchDataResponse(mockk(relaxed = true))

        client.revoke()

        coVerify { api.sessionsRevoke() }
    }

    @Test
    fun `attest calls sessionsAttest with correct network model`() = runTest(testDispatcher) {
        val params = SessionsAttestParameters(profileId = "profile-1", token = "tok", sessionDurationMinutes = 10)
        coEvery { api.sessionsAttest(any()) } returns StytchDataResponse(mockk(relaxed = true))

        client.attest(params)

        coVerify { api.sessionsAttest(SessionsAttestRequest(profileId = "profile-1", token = "tok", sessionDurationMinutes = 10)) }
    }
}
