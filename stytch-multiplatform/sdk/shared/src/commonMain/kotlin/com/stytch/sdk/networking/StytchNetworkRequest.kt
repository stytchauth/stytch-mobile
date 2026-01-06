package com.stytch.sdk.networking

import com.stytch.sdk.data.StytchDataResponse
import com.stytch.sdk.data.StytchNetworkError
import io.ktor.client.plugins.ClientRequestException
import io.ktor.http.HttpStatusCode

public suspend fun <T> stytchNetworkRequest(block: suspend () -> StytchDataResponse<T>): Result<T> =
    try {
        val response = block()
        Result.success(response.data)
    } catch (e: Exception) {
        val exception =
            when (e) {
                is ClientRequestException -> {
                    when (e.response.status) {
                        HttpStatusCode.Unauthorized -> {
                            StytchNetworkError("Unauthorized", e)
                        }

                        else -> {
                            StytchNetworkError(
                                "Unknown network error occurred. Status code: ${e.response.status.value}",
                                e,
                            )
                        }
                    }
                }

                else -> {
                    StytchNetworkError("Unknown network error occurred.", e)
                }
            }
        Result.failure(exception)
    }
