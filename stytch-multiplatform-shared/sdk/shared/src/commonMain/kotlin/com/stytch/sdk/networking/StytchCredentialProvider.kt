package com.stytch.sdk.networking

import io.ktor.client.plugins.auth.AuthProvider
import io.ktor.client.plugins.auth.providers.BasicAuthCredentials
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpHeaders
import io.ktor.http.auth.HttpAuthHeader
import io.ktor.util.encodeBase64
import io.ktor.utils.io.charsets.Charsets
import io.ktor.utils.io.core.toByteArray

/**
 * This provider exists as a patch until ktor 3.4.0, which will enable per-request refreshing of credentials
 * Until then, we'll create this Stytch-specific implementation, which differs in the following ways:
 * 1. does NOT cache the auth header tokens (fetches new tokens on every request)
 * 2. always sends the credentials (without waiting for a 401 first)
 */

internal class StytchCredentialProvider(
    private val credentials: suspend () -> BasicAuthCredentials?,
) : AuthProvider {
    @Suppress("OverridingDeprecatedMember")
    @Deprecated("Please use sendWithoutRequest function instead", level = DeprecationLevel.ERROR)
    override val sendWithoutRequest: Boolean
        get() = error("Deprecated")

    override fun sendWithoutRequest(request: HttpRequestBuilder): Boolean = true

    override fun isApplicable(auth: HttpAuthHeader): Boolean = true

    override suspend fun addRequestHeaders(
        request: HttpRequestBuilder,
        authHeader: HttpAuthHeader?,
    ) {
        credentials()?.let { credentials ->
            val authString = "${credentials.username}:${credentials.password}"
            val authBuf = authString.toByteArray(Charsets.UTF_8).encodeBase64()
            request.headers[HttpHeaders.Authorization] = "Basic $authBuf"
        }
    }

    override suspend fun refreshToken(response: HttpResponse): Boolean = true
}
