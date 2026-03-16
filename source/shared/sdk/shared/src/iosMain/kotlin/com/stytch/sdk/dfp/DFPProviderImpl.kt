package com.stytch.sdk.dfp

import com.stytch.sdk.StytchDFPProvider
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

@OptIn(ExperimentalForeignApi::class)
public class DFPProviderImpl(
    publicToken: String,
    dfppaDomain: String,
) : DFPProvider {
    private val provider = StytchDFPProvider.shared()

    init {
        provider.configureWithPublicToken(publicToken, dfppaDomain)
    }

    override suspend fun getTelemetryId(): String =
        withContext(Dispatchers.Main) {
            suspendCancellableCoroutine { continuation ->
                provider.getTelemetryIdWithCompletionHandler {
                    continuation.resume(it ?: STATIC_UNABLE_TO_LOAD_BINARY)
                }
            }
        }

    private companion object {
        const val STATIC_UNABLE_TO_LOAD_BINARY = "9b595d97-f845-4de6-b0ef-014905bc92dc"
    }
}
