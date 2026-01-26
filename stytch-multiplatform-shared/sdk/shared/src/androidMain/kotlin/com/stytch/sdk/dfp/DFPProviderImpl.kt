package com.stytch.sdk.dfp

import android.content.Context
import com.stytch.dfp.DFP
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

internal class DFPProviderImpl(
    context: Context,
    publicToken: String,
    dfppaDomain: String,
) : DFPProvider {
    override suspend fun getTelemetryId(): String =
        suspendCancellableCoroutine { cont ->
            dfp?.getTelemetryId { telemetryId ->
                cont.resume(telemetryId)
            } ?: run {
                cont.resume(STATIC_UNABLE_TO_LOAD_BINARY)
            }
        }

    private var dfp: DFP? =
        try {
            DFP(context = context, publicToken = publicToken, submissionUrl = dfppaDomain)
        } catch (_: UnsatisfiedLinkError) {
            null
        } catch (_: NoClassDefFoundError) {
            null
        }

    private companion object {
        const val STATIC_UNABLE_TO_LOAD_BINARY = "9b595d97-f845-4de6-b0ef-014905bc92dc"
    }
}
