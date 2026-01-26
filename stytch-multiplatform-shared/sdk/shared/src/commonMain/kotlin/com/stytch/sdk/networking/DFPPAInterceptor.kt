package com.stytch.sdk.networking

import com.stytch.sdk.data.DFPConfiguration
import com.stytch.sdk.data.DFPProtectedAuthMode
import com.stytch.sdk.dfp.CAPTCHAProvider
import com.stytch.sdk.dfp.DFPProvider
import de.jensklingenberg.ktorfit.annotations
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.request
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.TextContent
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject

private const val DFP_TELEMETRY_ID_KEY = "dfp_telemetry_id"
private const val CAPTCHA_TOKEN_KEY = "captcha_token"

internal val DFPPAInterceptor =
    createClientPlugin("DFPPAInterceptor", ::DFPPAInterceptorConfiguration) {
        val configuration = pluginConfig

        transformRequestBody { request, body, _ ->
            return@transformRequestBody if (request.annotations.contains(DFPPAEnabled()) && body is TextContent) {
                if (!configuration.getDfpConfiguration().dfpProtectedAuthEnabled) {
                    // If DFP is disabled, ONLY add a captcha token (if it's configured)
                    return@transformRequestBody body.setCAPTCHAToken(configuration.captchaProvider)
                }
                // If DFP is enabled, add a telemetry ID
                var updatedBody = body.setTelemetryID(configuration.dfpProvider)
                // If it's observation mode, also add a captcha token (if it's configured)
                if (configuration.getDfpConfiguration().dfpProtectedAuthMode == DFPProtectedAuthMode.OBSERVATION) {
                    updatedBody = updatedBody.setCAPTCHAToken(configuration.captchaProvider)
                }
                updatedBody
            } else {
                null
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

private fun HttpResponse.isRetriableDFPPARequest(authMode: DFPProtectedAuthMode): Boolean =
    request.annotations.contains(DFPPAEnabled()) && status == HttpStatusCode.Forbidden && authMode == DFPProtectedAuthMode.DECISIONING
