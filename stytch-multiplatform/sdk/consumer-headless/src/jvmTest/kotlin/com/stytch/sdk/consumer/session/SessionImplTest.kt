package com.stytch.sdk.consumer.session

import com.stytch.sdk.consumer.ConsumerClientTest
import com.stytch.sdk.consumer.networking.models.SessionsAttestParameters
import com.stytch.sdk.consumer.networking.models.SessionsAttestRequest
import com.stytch.sdk.consumer.networking.models.SessionsAuthenticateParameters
import com.stytch.sdk.consumer.networking.models.SessionsAuthenticateRequest
import com.stytch.sdk.data.StytchDataResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class SessionImplTest : ConsumerClientTest() {
    private val client = SessionImpl(dispatchers, networkingClient)

    @Test
    fun `authenticate calls sessionsAuthenticate with correct network model`() = runTest(testDispatcher) {
        coEvery { api.sessionsAuthenticate(any()) } returns StytchDataResponse(mockk(relaxed = true))

        client.authenticate(SessionsAuthenticateParameters(sessionDurationMinutes = 30))

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
        coEvery { api.sessionsAttest(any()) } returns StytchDataResponse(mockk(relaxed = true))

        client.attest(SessionsAttestParameters(profileId = "profile-1", token = "tok", sessionDurationMinutes = 10))

        coVerify { api.sessionsAttest(SessionsAttestRequest(profileId = "profile-1", token = "tok", sessionDurationMinutes = 10)) }
    }
}
