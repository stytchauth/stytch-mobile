package com.stytch.sdk.dfp

import com.stytch.sdk.data.DFPConfiguration
import com.stytch.sdk.data.DFPProtectedAuthMode
import com.stytch.sdk.data.StytchAPIError
import de.jensklingenberg.ktorfit.annotations
import io.ktor.client.call.HttpClientCall
import io.ktor.client.call.body
import io.ktor.client.plugins.api.Send
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.content.TextContent
import io.ktor.http.isSuccess
import io.ktor.utils.io.InternalAPI
import kotlinx.coroutines.CompletableJob
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject

private const val DFP_TELEMETRY_ID_KEY = "dfp_telemetry_id"
private const val CAPTCHA_TOKEN_KEY = "captcha_token"

internal val DFPPAInterceptor =
    createClientPlugin("DFPPAInterceptor", ::DFPPAInterceptorConfiguration) {
        val configuration = pluginConfig

        on(Send) { request ->
            return@on handlePotentialDFPPARequest(request, configuration, { proceed(it) }) {
                try {
                    it.body<StytchAPIError>()
                } catch (_: Exception) {
                    null
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

internal suspend fun TextContent.setCAPTCHAToken(captchaProvider: CAPTCHAProvider?): TextContent {
    val properties = mutableMapOf<String, String?>()
    if (captchaProvider?.isConfigured == true) {
        properties[CAPTCHA_TOKEN_KEY] = captchaProvider.getCAPTCHAToken()
    }
    return addProperties(properties)
}

internal suspend fun TextContent.setTelemetryID(dfpProvider: DFPProvider?): TextContent {
    val properties = mutableMapOf<String, String?>()
    properties[DFP_TELEMETRY_ID_KEY] = dfpProvider?.getTelemetryId()
    return addProperties(properties)
}

internal fun TextContent.addProperties(properties: Map<String, String?>): TextContent =
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

internal fun prepareRequest(request: HttpRequestBuilder): HttpRequestBuilder {
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

@OptIn(InternalAPI::class)
internal suspend fun handlePotentialDFPPARequest(
    request: HttpRequestBuilder,
    configuration: DFPPAInterceptorConfiguration,
    proceed: suspend (HttpRequestBuilder) -> HttpClientCall,
    parseResponseAsError: suspend (HttpResponse) -> StytchAPIError?,
): HttpClientCall {
    if (!request.annotations.contains(DFPPAEnabled())) {
        // Not a DFPPA endpoint; do nothing
        return proceed(request)
    }
    // create a new request to operate on
    val newRequest = prepareRequest(request)
    // If, for some reason, the body isn't modifiable, return early
    val body = newRequest.body as? TextContent ?: return proceed(newRequest)

    // If DFP is disabled, add a CAPTCHA token (if configured), and carry on
    if (!configuration.getDfpConfiguration().dfpProtectedAuthEnabled) {
        newRequest.body = body.setCAPTCHAToken(configuration.captchaProvider)
        return proceed(newRequest)
    }

    // DFP is enabled.
    return when (configuration.getDfpConfiguration().dfpProtectedAuthMode) {
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
            if (!call.response.status.isSuccess() && parseResponseAsError(call.response)?.errorType == "captcha_required") {
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
