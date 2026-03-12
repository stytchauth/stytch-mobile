package com.stytch.sdk.dfp

public interface DFPProvider {
    public suspend fun getTelemetryId(): String
}
