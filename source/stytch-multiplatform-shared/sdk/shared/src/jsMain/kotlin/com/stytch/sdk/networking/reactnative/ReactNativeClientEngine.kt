/*
 * Copyright 2014-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.stytch.sdk.networking.reactnative

import com.stytch.sdk.networking.reactnative.compatibility.commonFetch
import com.stytch.sdk.networking.reactnative.compatibility.readBody
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngineBase
import io.ktor.client.engine.callContext
import io.ktor.client.fetch.RequestInit
import io.ktor.client.plugins.HttpTimeoutCapability
import io.ktor.client.plugins.sse.SSECapability
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.client.request.ResponseAdapterAttributeKey
import io.ktor.client.request.isUpgradeRequest
import io.ktor.client.utils.buildHeaders
import io.ktor.client.utils.dropCompressionHeaders
import io.ktor.http.Headers
import io.ktor.http.HttpMethod
import io.ktor.http.HttpProtocolVersion
import io.ktor.http.HttpStatusCode
import io.ktor.util.AttributeKey
import io.ktor.util.Attributes
import io.ktor.util.PlatformUtils
import io.ktor.util.date.GMTDate
import io.ktor.utils.io.InternalAPI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.job

internal val CLIENT_CONFIG = AttributeKey<HttpClientConfig<*>>("client-config")

internal class FetchOptions(
    val requestInit: RequestInit.() -> Unit,
) {
    companion object {
        val key = AttributeKey<FetchOptions>("FetchOptions")
    }
}

internal class ReactNativeClientEngine(
    override val config: ReactNativeClientEngineConfig,
) : HttpClientEngineBase("ktor-reactnative") {
    override val supportedCapabilities = setOf(HttpTimeoutCapability, SSECapability)

    init {
        check(config.proxy == null) { "Proxy unsupported in Js engine." }
    }

    @OptIn(InternalAPI::class)
    override suspend fun execute(data: HttpRequestData): HttpResponseData {
        val callContext = callContext()
        val clientConfig = data.attributes[CLIENT_CONFIG]

        if (data.isUpgradeRequest()) {
            throw IllegalStateException("Websockets unsupported in RN engine.")
        }

        val requestTime = GMTDate()
        val rawRequest = data.toRaw(clientConfig, callContext)
        val fetchOptions = data.attributes.getOrNull(FetchOptions.key)?.requestInit ?: {}
        val rawResponse = commonFetch(data.url.toString(), rawRequest, fetchOptions, config, callContext.job)

        val status = HttpStatusCode(rawResponse.status.toInt(), rawResponse.statusText)
        val headers = rawResponse.headers.mapToKtor(data.method, data.attributes)
        val version = HttpProtocolVersion.HTTP_1_1

        val body = CoroutineScope(callContext).readBody(rawResponse)
        val responseBody: Any =
            data.attributes
                .getOrNull(ResponseAdapterAttributeKey)
                ?.adapt(data, status, headers, body, data.body, callContext)
                ?: body

        return HttpResponseData(
            status,
            requestTime,
            headers,
            version,
            responseBody,
            callContext,
        )
    }
}

@OptIn(InternalAPI::class)
private fun org.w3c.fetch.Headers.mapToKtor(
    method: HttpMethod,
    attributes: Attributes,
): Headers =
    buildHeaders {
        this@mapToKtor.asDynamic().forEach { value: String, key: String ->
            append(key, value)
        }

        dropCompressionHeaders(
            method,
            attributes,
            alwaysRemove = PlatformUtils.IS_BROWSER,
        )
    }

/**
 * Wrapper for javascript `error` objects.
 *
 * [Report a problem](https://ktor.io/feedback/?fqname=io.ktor.client.engine.js.JsError)
 *
 * @property origin: fail reason
 */
@Suppress("MemberVisibilityCanBePrivate")
public class JsError(
    public val origin: dynamic,
) : Throwable("Error from javascript[$origin].")
