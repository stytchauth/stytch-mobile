/*
 * Copyright 2014-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.stytch.sdk.networking.reactnative.compatibility

import com.stytch.sdk.networking.reactnative.ReactNativeClientEngineConfig
import com.stytch.sdk.networking.reactnative.browser.readBodyBrowser
import io.ktor.client.fetch.AbortController
import io.ktor.client.fetch.RequestInit
import io.ktor.client.fetch.fetch
import io.ktor.util.PlatformUtils
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.js.Promise

@OptIn(InternalCoroutinesApi::class)
internal suspend fun commonFetch(
    input: String,
    init: RequestInit,
    requestConfig: RequestInit.() -> Unit,
    config: ReactNativeClientEngineConfig,
    callJob: Job,
): org.w3c.fetch.Response =
    suspendCancellableCoroutine { continuation ->
        val controller = AbortController()
        init.signal = controller.signal
        config.requestInit(init)
        requestConfig(init)

        callJob.invokeOnCompletion(onCancelling = true) { controller.abort() }

        val promise: Promise<org.w3c.fetch.Response> =
            when {
                PlatformUtils.IS_BROWSER -> {
                    fetch(input, init)
                }

                else -> {
                    val options = js("Object").assign(js("Object").create(null), init, config.nodeOptions)
                    fetch(input, options)
                }
            }

        promise.then(
            onFulfilled = {
                continuation.resume(it)
            },
            onRejected = {
                continuation.resumeWithException(Error("Fail to fetch", it))
            },
        )
    }

private fun AbortController(): AbortController = js("new AbortController()")

internal suspend fun CoroutineScope.readBody(response: org.w3c.fetch.Response): ByteReadChannel = readBodyBrowser(response)
