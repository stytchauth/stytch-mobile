package com.stytch.sdk.networking

import com.stytch.sdk.data.StytchDataResponse
import com.stytch.sdk.data.StytchNetworkError
import com.stytch.sdk.data.StytchResult
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.http.HttpStatusCode

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
