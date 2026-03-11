package com.stytch.sdk.consumer.dfp

import com.stytch.sdk.data.StytchDispatchers
import com.stytch.sdk.dfp.DFPNotConfiguredError
import com.stytch.sdk.dfp.DFPProvider
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@OptIn(ExperimentalCoroutinesApi::class)
internal class DFPClientImplTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private val dispatchers = StytchDispatchers(ioDispatcher = testDispatcher, mainDispatcher = testDispatcher)

    @Test
    fun `getTelemetryId returns value from provider`() =
        runTest(testDispatcher) {
            val provider = mockk<DFPProvider>()
            coEvery { provider.getTelemetryId() } returns "telemetry-id-123"
            val client = DFPClientImpl(dispatchers, provider)

            val result = client.getTelemetryId()

            assertEquals("telemetry-id-123", result)
        }

    @Test
    fun `getTelemetryId throws DFPNotConfiguredError when provider is null`() =
        runTest(testDispatcher) {
            val client = DFPClientImpl(dispatchers, dfpProvider = null)

            assertFailsWith<DFPNotConfiguredError> {
                client.getTelemetryId()
            }
        }
}
