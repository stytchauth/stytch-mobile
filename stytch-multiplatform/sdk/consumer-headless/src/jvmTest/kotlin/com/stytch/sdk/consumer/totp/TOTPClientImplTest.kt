package com.stytch.sdk.consumer.totp

import com.stytch.sdk.consumer.networking.ConsumerNetworkingClient
import com.stytch.sdk.consumer.networking.api.SdkExternalApi
import com.stytch.sdk.consumer.networking.models.TOTPsAuthenticateParameters
import com.stytch.sdk.consumer.networking.models.TOTPsAuthenticateRequest
import com.stytch.sdk.consumer.networking.models.TOTPsCreateParameters
import com.stytch.sdk.consumer.networking.models.TOTPsCreateRequest
import com.stytch.sdk.consumer.networking.models.TOTPsRecoverParameters
import com.stytch.sdk.consumer.networking.models.TOTPsRecoverRequest
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
class TOTPClientImplTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private val dispatchers = StytchDispatchers(ioDispatcher = testDispatcher, mainDispatcher = testDispatcher)

    private val api = mockk<SdkExternalApi>(relaxed = true)
    private val networkingClient = mockk<ConsumerNetworkingClient>(relaxed = true)
    private val client = TOTPClientImpl(dispatchers, networkingClient)

    @BeforeTest
    fun setup() {
        every { networkingClient.api } returns api
        coEvery { networkingClient.request<Any>(any()) } coAnswers {
            @Suppress("UNCHECKED_CAST")
            (firstArg<suspend () -> StytchDataResponse<*>>())().data!!
        }
    }

    @Test
    fun `create calls tOTPsCreate with correct network model`() = runTest(testDispatcher) {
        val params = TOTPsCreateParameters(expirationMinutes = 5)
        coEvery { api.tOTPsCreate(any()) } returns StytchDataResponse(mockk(relaxed = true))

        client.create(params)

        coVerify { api.tOTPsCreate(TOTPsCreateRequest(expirationMinutes = 5)) }
    }

    @Test
    fun `authenticate calls tOTPsAuthenticate with correct network model`() = runTest(testDispatcher) {
        val params = TOTPsAuthenticateParameters(totpCode = "123456", sessionDurationMinutes = 30)
        coEvery { api.tOTPsAuthenticate(any()) } returns StytchDataResponse(mockk(relaxed = true))

        client.authenticate(params)

        coVerify {
            api.tOTPsAuthenticate(
                TOTPsAuthenticateRequest(totpCode = "123456", sessionDurationMinutes = 30),
            )
        }
    }

    @Test
    fun `recover calls tOTPsRecover with correct network model`() = runTest(testDispatcher) {
        val params = TOTPsRecoverParameters(recoveryCode = "abc-def", sessionDurationMinutes = 60)
        coEvery { api.tOTPsRecover(any()) } returns StytchDataResponse(mockk(relaxed = true))

        client.recover(params)

        coVerify {
            api.tOTPsRecover(
                TOTPsRecoverRequest(recoveryCode = "abc-def", sessionDurationMinutes = 60),
            )
        }
    }

    @Test
    fun `recoveryCodes calls tOTPsGetRecoveryCodes`() = runTest(testDispatcher) {
        coEvery { api.tOTPsGetRecoveryCodes() } returns StytchDataResponse(mockk(relaxed = true))

        client.recoveryCodes()

        coVerify { api.tOTPsGetRecoveryCodes() }
    }
}
