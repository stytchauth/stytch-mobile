package com.stytch.sdk.networking

import com.stytch.sdk.data.DeviceInfo
import com.stytch.sdk.data.StytchClientConfiguration
import com.stytch.sdk.shared.BuildConfig
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.UserAgent
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BasicAuthCredentials
import io.ktor.client.plugins.auth.providers.basic
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMessageBuilder
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.encodeBase64
import kotlinx.serialization.json.Json

private const val THIRTY_SECONDS_IN_MS = 30_000L
private const val TEN_SECONDS_IN_MS = 10_000L
private const val X_SDK_CLIENT_HEADER = "X-SDK-CLIENT"

public fun getStytchNetworkingClient(
    configuration: StytchClientConfiguration,
    getSessionToken: () -> String?,
): HttpClient =
    HttpClient {
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
        install(UserAgent) {
            agent = "${BuildConfig.SDK_NAME}/${BuildConfig.SDK_VERSION}"
        }
        install(DefaultRequest) {
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            configuration.deviceInfo.asHeader(this)
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
        install(Logging) {
            logger =
                object : Logger {
                    override fun log(message: String) {
                        Napier.v(message, null, "StytchNetworkingClient")
                    }
                }
            level = LogLevel.ALL
        }.also { Napier.base(DebugAntilog()) }
    }

private fun DeviceInfo.asHeader(context: HttpMessageBuilder): HttpMessageBuilder {
    val x =
        context.apply {
            header(
                X_SDK_CLIENT_HEADER,
                """
                {
                  "sdk": {
                       "identifier": "${BuildConfig.SDK_NAME}",
                       "version": "${BuildConfig.SDK_VERSION}"
                  },
                  "app": {
                       "identifier": "$applicationPackageName",
                       "version": "$applicationVersion"
                  },
                  "os":  {
                       "identifier": "$osName",
                       "version": "$osVersion"
                  },
                  "device":  {
                       "model": "$deviceName",
                       "screen_size": "$screenSize"
                  }
                }
                """.trimIndent().encodeBase64(),
            )
        }
    return x
}
