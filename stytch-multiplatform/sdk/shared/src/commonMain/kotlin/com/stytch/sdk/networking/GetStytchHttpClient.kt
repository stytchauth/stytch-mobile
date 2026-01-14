package com.stytch.sdk.networking

import com.stytch.sdk.data.StytchClientConfigurationInternal
import com.stytch.sdk.shared.BuildConfig
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.UserAgent
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BasicAuthCredentials
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
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

private const val THIRTY_SECONDS_IN_MS = 30_000L
private const val TEN_SECONDS_IN_MS = 10_000L
private const val X_SDK_CLIENT_HEADER = "X-SDK-CLIENT"

public fun getStytchHttpClient(
    configuration: StytchClientConfigurationInternal,
    getSessionToken: suspend () -> String?,
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
            agent = "${BuildConfig.SDK_NAME}/${configuration.platform}/${BuildConfig.SDK_VERSION}"
        }

        install(DefaultRequest) {
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            sdkClientHeader(configuration)
        }

        install(Auth) {
            providers.add(
                StytchCredentialProvider(
                    credentials = {
                        val username = configuration.tokenInfo.publicToken
                        val password = getSessionToken() ?: username
                        BasicAuthCredentials(username, password)
                    },
                ),
            )
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

@OptIn(ExperimentalUuidApi::class)
private fun HttpMessageBuilder.sdkClientHeader(config: StytchClientConfigurationInternal) {
    val eventId: String = Uuid.generateV4().toString()
    // I've NEVER understood what this was, but maintaining parity...
    val persistentId: String = Uuid.generateV4().toString()
    header(
        X_SDK_CLIENT_HEADER,
        """
        {
          "app_session_id": "${config.appSessionId}",
          "timezone": "${config.timezone}",
          "event_id": "event-id-$eventId",
          "persistent_id": "persistent-id-$persistentId",
          "sdk": {
               "identifier": "${BuildConfig.SDK_NAME}",
               "version": "${BuildConfig.SDK_VERSION}"
          },
          "app": {
               "identifier": "${config.deviceInfo.applicationPackageName}",
               "version": "${config.deviceInfo.applicationVersion}"
          },
          "os":  {
               "identifier": "${config.deviceInfo.osName}",
               "version": "${config.deviceInfo.osVersion}"
          },
          "device":  {
               "model": "${config.deviceInfo.deviceName}",
               "screen_size": "${config.deviceInfo.screenSize}"
          }
        }
        """.trimIndent().encodeBase64(),
    )
}
