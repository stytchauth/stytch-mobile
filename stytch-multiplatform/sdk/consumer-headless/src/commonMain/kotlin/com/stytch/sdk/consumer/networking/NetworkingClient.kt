package com.stytch.sdk.consumer.networking

import com.stytch.sdk.data.SDK_URL_PATH
import com.stytch.sdk.data.StytchClientConfigurationInternal
import com.stytch.sdk.data.StytchDataResponse
import com.stytch.sdk.networking.StytchNetworkResponseMiddleware
import com.stytch.sdk.networking.getStytchNetworkingClient
import com.stytch.sdk.networking.stytchNetworkRequest
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode

internal class NetworkingClient(
    configuration: StytchClientConfigurationInternal,
    getSessionToken: () -> String?,
) {
    internal val api: API

    init {
        val endpointOptions = configuration.endpointOptions
        val domain = if (configuration.tokenInfo.isTestToken) endpointOptions.testDomain else endpointOptions.liveDomain
        val ktorfit =
            Ktorfit
                .Builder()
                .baseUrl("https://$domain/$SDK_URL_PATH")
                .httpClient(getStytchNetworkingClient(configuration, getSessionToken))
                .build()
        api = ktorfit.createAPI()
    }

    internal val middlewares: StytchNetworkResponseMiddleware =
        object : StytchNetworkResponseMiddleware {
            override suspend fun <T> onSuccess(data: T) {
                TODO("Not yet implemented")
            }

            override suspend fun onError(response: HttpResponse): Exception {
                TODO("Not yet implemented")
                /*
                return when (response.status) {
                    in 300..399 -> RedirectResponseException(exceptionResponse, exceptionResponseText)
                    in 400..499 -> ClientRequestException(exceptionResponse, exceptionResponseText)
                    in 500..599 -> ServerResponseException(exceptionResponse, exceptionResponseText)
                    else -> ResponseException(exceptionResponse, exceptionResponseText)
                }
                 */
            }
        }

    internal suspend fun <T> request(block: suspend (API) -> StytchDataResponse<T>) =
        stytchNetworkRequest(middlewares) {
            block(api)
        }
}
