package com.stytch.sdk.networking

import com.stytch.sdk.data.StytchClientConfiguration
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BasicAuthCredentials
import io.ktor.client.plugins.auth.providers.basic
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

private const val THIRTY_SECONDS_IN_MS = 30_000L
private const val TEN_SECONDS_IN_MS = 10_000L
private const val X_SDK_CLIENT_HEADER = "X-SDK-CLIENT-ID"

public fun getStytchNetworkingClient(
    configuration: StytchClientConfiguration,
    getSessionToken: () -> String?,
): HttpClient =
    HttpClient(CIO) {
        expectSuccess = true
        install(ContentNegotiation) {
            json(
                Json {
                    isLenient = true
                    ignoreUnknownKeys = true
                },
            )
        }
        install(HttpTimeout) {
            requestTimeoutMillis = THIRTY_SECONDS_IN_MS
            connectTimeoutMillis = TEN_SECONDS_IN_MS
            socketTimeoutMillis = TEN_SECONDS_IN_MS
        }
        install(DefaultRequest) {
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            // TODO: KMP-friendly way of generating this
            header(X_SDK_CLIENT_HEADER, "")
        }
        install(Auth) {
            basic {
                credentials {
                    /**
                     * basic auth, where the username is the project's public token and the password is either
                     * the session token (for authenticated users) or the public token (for unauthenticated users)
                     */
                    val username = configuration.publicToken
                    val password = getSessionToken() ?: username
                    BasicAuthCredentials(username, password)
                }
                // Ensure we send the auth header on all requests, without waiting for a 401 first
                sendWithoutRequest { _ -> true }
            }
        }
    }
