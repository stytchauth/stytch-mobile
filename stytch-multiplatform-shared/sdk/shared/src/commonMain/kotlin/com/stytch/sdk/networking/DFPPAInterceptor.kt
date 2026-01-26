package com.stytch.sdk.networking

import com.stytch.sdk.data.DFPConfiguration
import com.stytch.sdk.data.DFPProtectedAuthMode
import de.jensklingenberg.ktorfit.annotations
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.request.HttpRequestBuilder

internal val DFPPAInterceptor =
    createClientPlugin("DFPPAInterceptor", ::DFPPAInterceptorConfiguration) {
        val configuration = pluginConfig
        onRequest { request, content ->
            if (request.annotations.contains(DFPPAEnabled())) {
                if (!configuration.getDfpConfiguration().dfpProtectedAuthEnabled) {
                    return@onRequest handleDFPDisabled(request, content)
                }
                when (configuration.getDfpConfiguration().dfpProtectedAuthMode) {
                    DFPProtectedAuthMode.OBSERVATION -> handleDFPObservationMode(request, content)
                    DFPProtectedAuthMode.DECISIONING -> handleDFPDecisioningMode(request, content)
                }
            }
        }
    }

internal class DFPPAInterceptorConfiguration {
    var getDfpConfiguration: () -> DFPConfiguration = {
        DFPConfiguration()
    }
    val dfpTelemetryIdKey = "dfp_telemetry_id"
    val captchaTokenKey = "captcha_token"
}

internal fun handleDFPDisabled(
    request: HttpRequestBuilder,
    content: Any,
) {
    println("JORDAN >>>>> DFPDISABLED")
    // DISABLED = if captcha client is configured, add a captcha token, else do nothing
}

internal fun handleDFPObservationMode(
    request: HttpRequestBuilder,
    content: Any,
) {
    println("JORDAN >>>>> DFP OBSERVATION MODE")
    // OBSERVATION = Always DFP; CAPTCHA if configured
}

internal fun handleDFPDecisioningMode(
    request: HttpRequestBuilder,
    content: Any,
) {
    println("JORDAN >>>>> DFP DECISIONING MODE")
    // DECISIONING = add DFP Id, proceed; if request 403s, add a captcha token
}
