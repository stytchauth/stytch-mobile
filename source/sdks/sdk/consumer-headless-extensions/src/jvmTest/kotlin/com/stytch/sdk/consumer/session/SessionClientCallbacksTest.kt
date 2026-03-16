package com.stytch.sdk.consumer.session

import com.stytch.sdk.consumer.networking.models.SessionsRevokeResponse
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.cancellation.CancellationException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class SessionClientCallbacksTest {
    private val client = mockk<SessionClient>()
    private val response = mockk<SessionsRevokeResponse>()

    @Test
    fun `onSuccess fires when suspend function succeeds`() =
        runBlocking {
            coEvery { client.revoke() } returns response

            var successValue: SessionsRevokeResponse? = null
            val job =
                client.revoke(
                    onSuccess = { successValue = it },
                    onFailure = { throw AssertionError("onFailure should not be called: $it") },
                )
            job.join()

            assertEquals(response, successValue)
        }

    @Test
    fun `onFailure fires when suspend function throws`() =
        runBlocking {
            val error = RuntimeException("something went wrong")
            coEvery { client.revoke() } throws error

            var failureValue: Throwable? = null
            val job =
                client.revoke(
                    onSuccess = { throw AssertionError("onSuccess should not be called") },
                    onFailure = { failureValue = it },
                )
            job.join()

            assertEquals(error, failureValue)
        }

    @Test
    fun `returned Job can be cancelled`() =
        runBlocking {
            val latch = CompletableDeferred<SessionsRevokeResponse>()
            coEvery { client.revoke() } coAnswers { latch.await() }

            var failureValue: Throwable? = null
            val job =
                client.revoke(
                    onSuccess = { throw AssertionError("onSuccess should not be called") },
                    onFailure = { failureValue = it },
                )

            assertTrue(job.isActive)
            job.cancel()
            job.join()

            assertTrue(failureValue is CancellationException)
        }
}
