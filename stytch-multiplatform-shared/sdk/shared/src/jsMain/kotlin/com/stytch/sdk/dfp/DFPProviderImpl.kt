package com.stytch.sdk.dfp

import com.stytch.sdk.StytchBridge

internal class DFPProviderImpl(
    publicToken: String,
    dfppaDomain: String,
) : DFPProvider {
    override suspend fun getTelemetryId(): String = StytchBridge.getTelemetryId()

    init {
        StytchBridge.configureDfp(publicToken, dfppaDomain)
    }
}
