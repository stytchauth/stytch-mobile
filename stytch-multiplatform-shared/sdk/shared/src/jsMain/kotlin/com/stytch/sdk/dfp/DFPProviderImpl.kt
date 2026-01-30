package com.stytch.sdk.dfp

import com.stytch.sdk.StytchBridge
import kotlinx.coroutines.await

internal class DFPProviderImpl(
    publicToken: String,
    dfppaDomain: String,
) : DFPProvider {
    override suspend fun getTelemetryId(): String = StytchBridge.getTelemetryId().await()

    init {
        StytchBridge.configureDfp(publicToken, dfppaDomain)
    }
}
