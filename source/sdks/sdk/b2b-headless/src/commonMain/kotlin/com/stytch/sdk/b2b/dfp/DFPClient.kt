package com.stytch.sdk.b2b.dfp

import com.stytch.sdk.StytchApi
import com.stytch.sdk.data.StytchDispatchers
import com.stytch.sdk.data.StytchError
import com.stytch.sdk.dfp.DFPNotConfiguredError
import com.stytch.sdk.dfp.DFPProvider
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException
import kotlin.js.JsExport

/** Device fingerprinting (DFP) integration. Provides a telemetry ID for Stytch's fraud prevention layer. */
@StytchApi
@JsExport
public interface DFPClient {
    /**
     * Fetches a DFP telemetry ID from the local device fingerprinting provider. The ID is passed to your
     * backend for use in Stytch lookup calls.
     *
     * **Kotlin:**
     * ```kotlin
     * val telemetryId = StytchB2B.dfp.getTelemetryId()
     * ```
     *
     * **iOS:**
     * ```swift
     * let telemetryId = try await StytchB2B.dfp.getTelemetryId()
     * ```
     *
     * **React Native:**
     * ```js
     * const telemetryId = await StytchB2B.dfp.getTelemetryId()
     * ```
     *
     * @return The DFP telemetry ID string.
     *
     * @throws [DFPNotConfiguredError] if DFP was not configured during SDK initialization.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun getTelemetryId(): String
}

internal class DFPClientImpl(
    private val dispatchers: StytchDispatchers,
    private val dfpProvider: DFPProvider?,
) : DFPClient {
    @Throws(StytchError::class, CancellationException::class)
    override suspend fun getTelemetryId(): String =
        withContext(dispatchers.ioDispatcher) {
            dfpProvider?.getTelemetryId() ?: throw DFPNotConfiguredError()
        }
}
