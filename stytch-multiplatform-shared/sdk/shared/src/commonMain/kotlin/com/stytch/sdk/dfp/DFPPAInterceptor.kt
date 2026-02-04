package com.stytch.sdk.dfp

import com.stytch.sdk.data.DFPConfiguration
import com.stytch.sdk.data.DFPProtectedAuthMode
import com.stytch.sdk.data.StytchAPIError
import com.stytch.sdk.dfp.DFPPAEnabled
import de.jensklingenberg.ktorfit.annotations
import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.api.Send
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.TextContent
import io.ktor.utils.io.InternalAPI
import kotlinx.coroutines.CompletableJob
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject

private const val DFP_TELEMETRY_ID_KEY = "dfp_telemetry_id"
private const val CAPTCHA_TOKEN_KEY = "captcha_token"

@OptIn(InternalAPI::class)
internal val DFPPAInterceptor =
    createClientPlugin("DFPPAInterceptor", ::DFPPAInterceptorConfiguration) {
        val configuration = pluginConfig

        fun prepareRequest(request: HttpRequestBuilder): HttpRequestBuilder {
            val subRequest = HttpRequestBuilder().takeFrom(request)
            request.executionContext.invokeOnCompletion { cause ->
                val subRequestJob = subRequest.executionContext as CompletableJob
                if (cause == null) {
                    subRequestJob.complete()
                } else {
                    subRequestJob.completeExceptionally(cause)
                }
            }
            return subRequest
        }

        on(Send) { request ->
            if (!request.annotations.contains(DFPPAEnabled())) {
                // Not a DFPPA endpoint; do nothing
                return@on proceed(request)
            }
            // create a new request to operate on
            val newRequest = prepareRequest(request)
            // If, for some reason, the body isn't modifiable, return early
            var body = newRequest.body as? TextContent ?: return@on proceed(newRequest)

            // If DFP is disabled, add a CAPTCHA token (if configured), and carry on
            if (!configuration.getDfpConfiguration().dfpProtectedAuthEnabled) {
                newRequest.body = body.setCAPTCHAToken(configuration.captchaProvider)
                return@on proceed(newRequest)
            }

            // DFP is enabled.
            when (configuration.getDfpConfiguration().dfpProtectedAuthMode) {
                // In OBSERVATION mode, add a telemetry ID and a CAPTCHA token (if configured), and ignore the response
                DFPProtectedAuthMode.OBSERVATION -> {
                    newRequest.body =
                        body
                            .setTelemetryID(configuration.dfpProvider)
                            .setCAPTCHAToken(configuration.captchaProvider)
                    proceed(newRequest)
                }

                // In DECISIONING mode, add a telemetry ID, try the request, and retry with CAPTCHA if it 403s
                DFPProtectedAuthMode.DECISIONING -> {
                    newRequest.body = body.setTelemetryID(configuration.dfpProvider)
                    var call = proceed(newRequest)
                    if (call.response.body<StytchAPIError>().errorType == "captcha_required") {
                        val retryRequest = prepareRequest(request)
                        // add new tokens
                        retryRequest.body =
                            body
                                .setTelemetryID(configuration.dfpProvider)
                                .setCAPTCHAToken(configuration.captchaProvider)
                        // fire it off
                        call = proceed(retryRequest)
                    }
                    call
                }
            }
        }
    }

internal class DFPPAInterceptorConfiguration {
    var getDfpConfiguration: () -> DFPConfiguration = {
        DFPConfiguration()
    }
    var dfpProvider: DFPProvider? = null
    var captchaProvider: CAPTCHAProvider? = null
}

private suspend fun TextContent.setCAPTCHAToken(captchaProvider: CAPTCHAProvider?): TextContent {
    val properties = mutableMapOf<String, String?>()
    if (captchaProvider?.isConfigured == true) {
        properties[CAPTCHA_TOKEN_KEY] = captchaProvider.getCAPTCHAToken()
    }
    return addProperties(properties)
}

private suspend fun TextContent.setTelemetryID(dfpProvider: DFPProvider?): TextContent {
    val properties = mutableMapOf<String, String?>()
    properties[DFP_TELEMETRY_ID_KEY] = dfpProvider?.getTelemetryId()
    return addProperties(properties)
}

private fun TextContent.addProperties(properties: Map<String, String?>): TextContent =
    if (contentType.match(ContentType.Application.Json)) {
        val originalPayload = Json.parseToJsonElement(text).jsonObject
        val modifiedPayload = originalPayload.toMutableMap()
        properties.forEach { (key, value) ->
            modifiedPayload[key] = JsonPrimitive(value)
        }
        val newPayload = Json.encodeToString(modifiedPayload)
        TextContent(newPayload, ContentType.Application.Json)
    } else {
        this
    }
