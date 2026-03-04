package com.stytch.sdk.consumer.networking

import com.stytch.sdk.consumer.StytchConsumerAuthenticationStateManager
import com.stytch.sdk.consumer.networking.models.SessionsRevokeResponse
import com.stytch.sdk.data.StytchAPIError
import com.stytch.sdk.data.StytchNetworkError
import io.ktor.client.plugins.ResponseException
import io.ktor.client.statement.HttpResponse
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

private class FakeErrorResponseParser(
    private val apiError: StytchAPIError? = null,
    private val text: String = "raw error text",
) : IErrorResponseParser {
    override suspend fun parseAPIError(response: HttpResponse): StytchAPIError =
        apiError ?: throw Exception("parse failed")

    override suspend fun responseText(response: HttpResponse): String = text
}

internal class ConsumerNetworkingClientMiddlewareTest {
    private val sessionManager = mockk<StytchConsumerAuthenticationStateManager>(relaxed = true)
    private var sessionJobStarted = false

    private fun makeMiddleware(errorParser: IErrorResponseParser = FakeErrorResponseParser()) =
        ConsumerNetworkingClientMiddleware(
            sessionManager = sessionManager,
            onSessionAuthenticated = { sessionJobStarted = true },
            errorParser = errorParser,
        )

    @Test
    fun `onSuccess with AuthenticatedResponse calls update and starts session job`() = runTest {
        val middleware = makeMiddleware()
        val response = mockk<AuthenticatedResponse>(relaxed = true)
        middleware.onSuccess(response)
        coVerify { sessionManager.update(response) }
        assertTrue(sessionJobStarted)
    }

    @Test
    fun `onSuccess with SessionsRevokeResponse calls revoke`() = runTest {
        val middleware = makeMiddleware()
        middleware.onSuccess(SessionsRevokeResponse(requestId = "req-1", statusCode = 200))
        coVerify { sessionManager.revoke() }
        assertFalse(sessionJobStarted)
    }

    @Test
    fun `onSuccess with other type is a no-op`() = runTest {
        val middleware = makeMiddleware()
        middleware.onSuccess("some string")
        coVerify(exactly = 0) { sessionManager.update<Any?>(any()) }
        coVerify(exactly = 0) { sessionManager.revoke() }
        assertFalse(sessionJobStarted)
    }

    @Test
    fun `onError returns the parsed StytchAPIError`() = runTest {
        val apiError = StytchAPIError(
            statusCode = 400,
            requestId = "req-123",
            errorMessage = "Bad request",
            errorType = "bad_request",
            errorUrl = "https://stytch.com/docs/errors/400",
        )
        val middleware = makeMiddleware(FakeErrorResponseParser(apiError = apiError))
        val result = middleware.onError(ResponseException(mockk(relaxed = true), ""))
        assertEquals(apiError, result)
    }

    @Test
    fun `onError calls revoke for unrecoverable error type`() = runTest {
        val apiError = StytchAPIError(
            statusCode = 401,
            requestId = "req-456",
            errorMessage = "Session not found",
            errorType = "session_not_found",
            errorUrl = "https://stytch.com/docs/errors/401",
        )
        val middleware = makeMiddleware(FakeErrorResponseParser(apiError = apiError))
        middleware.onError(ResponseException(mockk(relaxed = true), ""))
        coVerify { sessionManager.revoke() }
    }

    @Test
    fun `onError returns StytchNetworkError when body cannot be parsed`() = runTest {
        val middleware = makeMiddleware(FakeErrorResponseParser(apiError = null, text = "raw error text"))
        val result = middleware.onError(ResponseException(mockk(relaxed = true), ""))
        assertIs<StytchNetworkError>(result)
        assertEquals("raw error text", result.message)
    }
}
