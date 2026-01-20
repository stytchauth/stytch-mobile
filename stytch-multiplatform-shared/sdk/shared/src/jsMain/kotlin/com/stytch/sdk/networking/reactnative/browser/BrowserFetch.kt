/*
 * Copyright 2014-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.stytch.sdk.networking.reactnative.browser

import com.stytch.sdk.networking.reactnative.asByteArray
import io.ktor.client.fetch.ArrayBuffer
import io.ktor.client.fetch.ReadableStream
import io.ktor.client.fetch.ReadableStreamDefaultReader
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.writeFully
import io.ktor.utils.io.writer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.await
import kotlinx.coroutines.suspendCancellableCoroutine
import org.khronos.webgl.Uint8Array
import org.w3c.fetch.Response
import org.w3c.files.Blob
import org.w3c.files.FileReader
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

internal suspend fun CoroutineScope.readBodyBrowser(response: Response): ByteReadChannel {
    @Suppress("UnsafeCastFromDynamic")
    val stream: ReadableStream<Uint8Array> = response.body ?: response.blob().await().stream()
    return channelFromStream(stream)
}

internal fun CoroutineScope.channelFromStream(stream: ReadableStream<Uint8Array>): ByteReadChannel =
    writer {
        val reader: ReadableStreamDefaultReader<Uint8Array> = stream.getReader()
        try {
            while (true) {
                val chunk = reader.readChunk() ?: break
                channel.writeFully(chunk.asByteArray())
                channel.flush()
            }
        } catch (cause: Throwable) {
            reader.cancel(cause).catch { /* ignore */ }.await()
            throw cause
        }
    }.channel

internal suspend fun ReadableStreamDefaultReader<Uint8Array>.readChunk(): Uint8Array? =
    suspendCancellableCoroutine { continuation ->
        read()
            .then {
                val chunk = it.value
                val result = if (it.done) null else chunk
                continuation.resume(result)
            }.catch { cause ->
                continuation.resumeWithException(cause)
            }
    }

internal suspend fun Blob.stream(): ReadableStream<Uint8Array> =
    if (this.asDynamic().stream != undefined) {
        this.asDynamic().stream()
    } else {
        // Fallback: Convert to ArrayBuffer and create a ReadableStream manually
        val arrayBuffer = this.arrayBuffer()
        js(
            """
            new ReadableStream({
                start: function(controller) {
                    controller.enqueue(new Uint8Array(arrayBuffer));
                    controller.close();
                }
            })
            """,
        )
    }

internal suspend fun Blob.arrayBuffer(): ArrayBuffer =
    suspendCancellableCoroutine { continuation ->
        val fileReader = FileReader()

        fileReader.onload = { event ->
            continuation.resume(event.target.asDynamic().result)
        }

        fileReader.onerror = { event ->
            continuation.resumeWithException(RuntimeException("Failed to read Blob"))
        }

        fileReader.readAsArrayBuffer(this)
    }
