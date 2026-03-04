package com.stytch.sdk.consumer.networking

import com.stytch.sdk.consumer.StytchConsumerAuthenticationStateManager
import com.stytch.sdk.consumer.networking.models.SessionsRevokeResponse
import com.stytch.sdk.data.StytchAPIError
import com.stytch.sdk.data.StytchNetworkError
import com.stytch.sdk.networking.StytchNetworkResponseMiddleware
import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText

internal interface IErrorResponseParser {
    suspend fun parseAPIError(response: HttpResponse): StytchAPIError

    suspend fun responseText(response: HttpResponse): String
}

internal class DefaultErrorResponseParser : IErrorResponseParser {
    override suspend fun parseAPIError(response: HttpResponse): StytchAPIError = response.body()

    override suspend fun responseText(response: HttpResponse): String = response.bodyAsText()
}

internal class ConsumerNetworkingClientMiddleware(
    private val sessionManager: StytchConsumerAuthenticationStateManager,
    private val onSessionAuthenticated: () -> Unit,
    private val errorParser: IErrorResponseParser = DefaultErrorResponseParser(),
) : StytchNetworkResponseMiddleware {
    override suspend fun <T> onSuccess(data: T) {
        if (data is AuthenticatedResponse) {
            sessionManager.update(data)
            onSessionAuthenticated()
        } else if (data is SessionsRevokeResponse) {
            sessionManager.revoke()
        }
    }

    override suspend fun onError(exception: ResponseException): Exception =
        try {
            val error = errorParser.parseAPIError(exception.response)
            if (error.isUnrecoverableError()) {
                sessionManager.revoke()
            }
            error
        } catch (_: Exception) {
            StytchNetworkError(errorParser.responseText(exception.response))
        }
}
