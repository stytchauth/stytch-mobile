package com.stytch.sdk.consumer

import com.stytch.sdk.consumer.networking.ConsumerNetworkingClient
import com.stytch.sdk.consumer.networking.api.SdkExternalApi
import com.stytch.sdk.data.StytchDataResponse
import com.stytch.sdk.data.StytchDispatchers
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlin.test.BeforeTest

/**
 * Base class for consumer client unit tests.
 *
 * Provides a pre-wired [networkingClient] mock whose [ConsumerNetworkingClient.request] executes
 * its lambda so that API calls flow through and can be verified with [coVerify]. The internal
 * [ConsumerNetworkingClient.api] getter is stubbed in [setupNetworkingClient] (a [@BeforeTest]
 * method) rather than inside the mockk{} block, because Kotlin mangles internal member names at
 * the JVM level and MockK would not match the getter otherwise.
 */
@OptIn(ExperimentalCoroutinesApi::class)
internal abstract class ConsumerClientTest {
    val testDispatcher = UnconfinedTestDispatcher()
    val dispatchers = StytchDispatchers(ioDispatcher = testDispatcher, mainDispatcher = testDispatcher)

    val api = mockk<SdkExternalApi>(relaxed = true)
    val networkingClient = mockk<ConsumerNetworkingClient>(relaxed = true)

    @BeforeTest
    fun setupNetworkingClient() {
        every { networkingClient.api } returns api
        coEvery { networkingClient.request<Any>(any()) } coAnswers {
            @Suppress("UNCHECKED_CAST")
            (firstArg<suspend () -> StytchDataResponse<*>>())().data!!
        }
    }
}
