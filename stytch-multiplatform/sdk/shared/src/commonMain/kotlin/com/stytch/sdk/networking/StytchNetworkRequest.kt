package com.stytch.sdk.networking

import com.stytch.sdk.data.StytchDataResponse
import com.stytch.sdk.data.StytchNetworkError
import com.stytch.sdk.data.StytchResult
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.delay

public suspend fun <T> stytchNetworkRequest(
    middleware: StytchNetworkResponseMiddleware,
    block: suspend () -> StytchDataResponse<T>,
): StytchResult<T> =
    try {
        val response = block()
        middleware.onSuccess(response.data)
        StytchResult.Success(response.data)
    } catch (e: Exception) {
        val exception =
            when (e) {
                is ResponseException -> {
                    middleware.onError(e.response)
                }

                else -> {
                    StytchNetworkError("Unknown network error occurred.", e)
                }
            }
        StytchResult.Error(exception)
    }

public suspend fun <T> stytchNetworkRequestWithRetryAndBackoff(
    maxRetries: Int = 3,
    initialDelay: Long = 100,
    maxDelay: Long = 1000,
    factor: Double = 2.0,
    block: suspend () -> StytchDataResponse<T>,
    onSuccess: suspend (T) -> Unit,
): Unit =
    try {
        val response = block()
        onSuccess(response.data)
    } catch (e: Exception) {
        if (maxRetries <= 0) throw e
        delay(initialDelay)
        stytchNetworkRequestWithRetryAndBackoff(
            maxRetries = maxRetries - 1,
            initialDelay = (initialDelay * factor).toLong().coerceAtMost(maxDelay),
            maxDelay = maxDelay,
            factor = factor,
            block = block,
            onSuccess = onSuccess,
        )
    }
