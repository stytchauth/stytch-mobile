package com.stytch.sdk.networking

import com.stytch.sdk.data.StytchDataResponse
import com.stytch.sdk.data.StytchError
import com.stytch.sdk.data.StytchNetworkError
import io.ktor.client.plugins.ResponseException
import kotlinx.coroutines.delay

public suspend fun <T> stytchNetworkRequest(
    middleware: StytchNetworkResponseMiddleware,
    block: suspend () -> StytchDataResponse<T>,
): T =
    try {
        val response = block()
        middleware.onSuccess(response.data)
        response.data
    } catch (e: Exception) {
        if (e is ResponseException) {
            throw middleware.onError(e)
        }
        if (e is StytchError) {
            throw e
        }
        throw StytchNetworkError("Unknown error occurred", e)
    }

public suspend fun <T> stytchNetworkRequestWithRetryAndBackoff(
    maxRetries: Int = 3,
    initialDelay: Long = 100,
    maxDelay: Long = 1000,
    factor: Double = 2.0,
    block: suspend () -> T,
): T =
    try {
        block()
    } catch (e: Exception) {
        if (maxRetries <= 0) throw e
        delay(initialDelay)
        stytchNetworkRequestWithRetryAndBackoff(
            maxRetries = maxRetries - 1,
            initialDelay = (initialDelay * factor).toLong().coerceAtMost(maxDelay),
            maxDelay = maxDelay,
            factor = factor,
            block = block,
        )
    }
