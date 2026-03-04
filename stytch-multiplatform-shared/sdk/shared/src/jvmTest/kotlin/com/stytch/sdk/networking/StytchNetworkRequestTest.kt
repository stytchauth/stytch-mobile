package com.stytch.sdk.networking

import com.stytch.sdk.data.StytchAPIError
import com.stytch.sdk.data.StytchDataResponse
import com.stytch.sdk.data.StytchNetworkError
import io.ktor.client.plugins.ResponseException
import io.ktor.client.statement.HttpResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@OptIn(ExperimentalCoroutinesApi::class)
internal class StytchNetworkRequestTest {
    private val dispatcher = UnconfinedTestDispatcher()
    private val middleware = mockk<StytchNetworkResponseMiddleware>(relaxed = true)

    @Test
    fun `returns data and calls onSuccess on success`() = runTest(dispatcher) {
        val result = stytchNetworkRequest(middleware) { StytchDataResponse("hello") }
        assertEquals("hello", result)
        coVerify { middleware.onSuccess("hello") }
    }

    @Test
    fun `calls onError and throws the result on ResponseException`() = runTest(dispatcher) {
        val apiError = StytchAPIError(
            statusCode = 400,
            requestId = "req-1",
            errorMessage = "Bad request",
            errorType = "bad_request",
            errorUrl = "https://stytch.com/docs/errors/400",
        )
        coEvery { middleware.onError(any()) } returns apiError
        val thrown = assertFailsWith<StytchAPIError> {
            stytchNetworkRequest(middleware) {
                throw ResponseException(mockk<HttpResponse>(relaxed = true), "")
            }
        }
        assertEquals(apiError, thrown)
        coVerify { middleware.onError(any()) }
    }

    @Test
    fun `wraps unknown exceptions in StytchNetworkError without calling onError`() = runTest(dispatcher) {
        val cause = RuntimeException("unexpected")
        val thrown = assertFailsWith<StytchNetworkError> {
            stytchNetworkRequest(middleware) { throw cause }
        }
        assertEquals(cause, thrown.cause)
        coVerify(exactly = 0) { middleware.onError(any()) }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
internal class StytchNetworkRequestWithRetryAndBackoffTest {
    private val dispatcher = UnconfinedTestDispatcher()

    @Test
    fun `returns result immediately on first success`() = runTest(dispatcher) {
        val result = stytchNetworkRequestWithRetryAndBackoff { "success" }
        assertEquals("success", result)
    }

    @Test
    fun `retries on failure and returns result on eventual success`() = runTest(dispatcher) {
        var callCount = 0
        val result = stytchNetworkRequestWithRetryAndBackoff(maxRetries = 2, initialDelay = 1) {
            callCount++
            if (callCount < 3) throw Exception("fail")
            "success"
        }
        assertEquals("success", result)
        assertEquals(3, callCount)
    }

    @Test
    fun `rethrows after max retries exhausted`() = runTest(dispatcher) {
        var callCount = 0
        assertFailsWith<Exception> {
            stytchNetworkRequestWithRetryAndBackoff(maxRetries = 2, initialDelay = 1) {
                callCount++
                throw Exception("always fails")
            }
        }
        assertEquals(3, callCount) // 1 initial attempt + 2 retries
    }
}
