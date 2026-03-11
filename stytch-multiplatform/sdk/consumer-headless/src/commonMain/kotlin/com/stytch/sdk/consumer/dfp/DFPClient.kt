package com.stytch.sdk.consumer.dfp

import com.stytch.sdk.data.StytchDispatchers
import com.stytch.sdk.data.StytchError
import com.stytch.sdk.dfp.DFPNotConfiguredError
import com.stytch.sdk.dfp.DFPProvider
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException
import kotlin.js.JsExport

@JsExport
public interface DFPClient {
    /**
     * Fetches a DFP telemetry ID for use in backend lookup calls.
     * Throws [DFPNotConfiguredError] if DFP is not configured.
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
