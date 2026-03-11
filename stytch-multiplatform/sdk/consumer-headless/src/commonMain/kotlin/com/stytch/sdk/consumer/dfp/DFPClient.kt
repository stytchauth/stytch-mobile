package com.stytch.sdk.consumer.dfp

import com.stytch.sdk.data.StytchDispatchers
import com.stytch.sdk.dfp.DFPProvider
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException
import kotlin.js.JsExport

@JsExport
public interface DFPClient {
    /**
     * Fetches a DFP telemetry ID for use in backend lookup calls.
     * Returns an empty string if DFP is not configured.
     */
    @Throws(CancellationException::class)
    public suspend fun getTelemetryId(): String
}

internal class DFPClientImpl(
    private val dispatchers: StytchDispatchers,
    private val dfpProvider: DFPProvider?,
) : DFPClient {
    override suspend fun getTelemetryId(): String =
        withContext(dispatchers.ioDispatcher) {
            dfpProvider?.getTelemetryId() ?: ""
        }
}
