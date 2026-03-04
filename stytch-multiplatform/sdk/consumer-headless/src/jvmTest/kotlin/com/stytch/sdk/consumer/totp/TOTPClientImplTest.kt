package com.stytch.sdk.consumer.totp

import com.stytch.sdk.consumer.ConsumerClientTest
import com.stytch.sdk.consumer.networking.models.TOTPsAuthenticateParameters
import com.stytch.sdk.consumer.networking.models.TOTPsAuthenticateRequest
import com.stytch.sdk.consumer.networking.models.TOTPsCreateParameters
import com.stytch.sdk.consumer.networking.models.TOTPsCreateRequest
import com.stytch.sdk.consumer.networking.models.TOTPsRecoverParameters
import com.stytch.sdk.consumer.networking.models.TOTPsRecoverRequest
import com.stytch.sdk.data.StytchDataResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class TOTPClientImplTest : ConsumerClientTest() {
    private val client = TOTPClientImpl(dispatchers, networkingClient)

    @Test
    fun `create calls tOTPsCreate with correct network model`() = runTest(testDispatcher) {
        coEvery { api.tOTPsCreate(any()) } returns StytchDataResponse(mockk(relaxed = true))

        client.create(TOTPsCreateParameters(expirationMinutes = 5))

        coVerify { api.tOTPsCreate(TOTPsCreateRequest(expirationMinutes = 5)) }
    }

    @Test
    fun `authenticate calls tOTPsAuthenticate with correct network model`() = runTest(testDispatcher) {
        coEvery { api.tOTPsAuthenticate(any()) } returns StytchDataResponse(mockk(relaxed = true))

        client.authenticate(TOTPsAuthenticateParameters(totpCode = "123456", sessionDurationMinutes = 30))

        coVerify { api.tOTPsAuthenticate(TOTPsAuthenticateRequest(totpCode = "123456", sessionDurationMinutes = 30)) }
    }

    @Test
    fun `recover calls tOTPsRecover with correct network model`() = runTest(testDispatcher) {
        coEvery { api.tOTPsRecover(any()) } returns StytchDataResponse(mockk(relaxed = true))

        client.recover(TOTPsRecoverParameters(recoveryCode = "abc-def", sessionDurationMinutes = 60))

        coVerify { api.tOTPsRecover(TOTPsRecoverRequest(recoveryCode = "abc-def", sessionDurationMinutes = 60)) }
    }

    @Test
    fun `recoveryCodes calls tOTPsGetRecoveryCodes`() = runTest(testDispatcher) {
        coEvery { api.tOTPsGetRecoveryCodes() } returns StytchDataResponse(mockk(relaxed = true))

        client.recoveryCodes()

        coVerify { api.tOTPsGetRecoveryCodes() }
    }
}
