package com.stytch.sdk.b2b.networking

import com.stytch.sdk.b2b.StytchB2BAuthenticationStateManager
import com.stytch.sdk.b2b.networking.models.SessionsRevokeResponse
import com.stytch.sdk.data.StytchAPIError
import com.stytch.sdk.data.StytchNetworkError
import com.stytch.sdk.networking.StytchNetworkResponseMiddleware
import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlin.time.Instant

internal interface IErrorResponseParser {
    suspend fun parseAPIError(response: HttpResponse): StytchAPIError

    suspend fun responseText(response: HttpResponse): String
}

internal class DefaultErrorResponseParser : IErrorResponseParser {
    override suspend fun parseAPIError(response: HttpResponse): StytchAPIError = response.body()

    override suspend fun responseText(response: HttpResponse): String = response.bodyAsText()
}

internal class B2BNetworkingClientMiddleware(
    private val sessionManager: StytchB2BAuthenticationStateManager,
    private val onSessionAuthenticated: (Instant) -> Unit,
    private val errorParser: IErrorResponseParser = DefaultErrorResponseParser(),
) : StytchNetworkResponseMiddleware {
    override suspend fun <T> onSuccess(data: T) {
        if (data is B2BResponse) {
            sessionManager.potentiallyUpdateIST(data)
        }
        if (data is AuthenticatedResponse) {
            sessionManager.update(data)
            onSessionAuthenticated(data.memberSession.expiresAt ?: Instant.DISTANT_PAST)
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
