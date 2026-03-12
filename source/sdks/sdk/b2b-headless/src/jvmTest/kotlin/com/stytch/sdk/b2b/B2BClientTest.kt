package com.stytch.sdk.b2b

import com.stytch.sdk.b2b.networking.B2BNetworkingClient
import com.stytch.sdk.b2b.networking.api.SdkExternalApi
import com.stytch.sdk.data.StytchDataResponse
import com.stytch.sdk.data.StytchDispatchers
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlin.test.BeforeTest

/**
 * Base class for B2B client unit tests.
 *
 * Mirrors the ConsumerClientTest pattern: [networkingClient.request] executes its lambda so
 * API calls flow through and can be verified with [coVerify]. The internal [api] getter is
 * stubbed in [setupNetworkingClient] (a [@BeforeTest] method) because Kotlin mangles internal
 * member names at the JVM level and MockK would not match the getter otherwise.
 */
@OptIn(ExperimentalCoroutinesApi::class)
internal abstract class B2BClientTest {
    val testDispatcher = UnconfinedTestDispatcher()
    val dispatchers = StytchDispatchers(ioDispatcher = testDispatcher, mainDispatcher = testDispatcher)

    val api = mockk<SdkExternalApi>(relaxed = true)
    val networkingClient = mockk<B2BNetworkingClient>(relaxed = true)

    @BeforeTest
    fun setupNetworkingClient() {
        every { networkingClient.api } returns api
        coEvery { networkingClient.request<Any>(any()) } coAnswers {
            @Suppress("UNCHECKED_CAST")
            (firstArg<suspend () -> StytchDataResponse<*>>())().data!!
        }
    }
}
