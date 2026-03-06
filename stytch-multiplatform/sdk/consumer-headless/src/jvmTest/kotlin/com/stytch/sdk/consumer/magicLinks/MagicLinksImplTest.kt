package com.stytch.sdk.consumer.magicLinks

import com.stytch.sdk.consumer.ConsumerClientTest
import com.stytch.sdk.consumer.StytchConsumerAuthenticationStateManager
import com.stytch.sdk.consumer.networking.models.MagicLinksAuthenticateParameters
import com.stytch.sdk.consumer.networking.models.MagicLinksAuthenticateRequest
import com.stytch.sdk.consumer.networking.models.MagicLinksEmailLoginOrCreateParameters
import com.stytch.sdk.consumer.networking.models.MagicLinksEmailLoginOrCreateRequest
import com.stytch.sdk.consumer.networking.models.MagicLinksEmailSendSecondaryParameters
import com.stytch.sdk.consumer.networking.models.MagicLinksEmailSendSecondaryRequest
import com.stytch.sdk.data.PKCECodePair
import com.stytch.sdk.data.StytchDataResponse
import com.stytch.sdk.pkce.MissingPKCEException
import com.stytch.sdk.pkce.PKCEClient
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFailsWith

@OptIn(ExperimentalCoroutinesApi::class)
internal class MagicLinksImplTest : ConsumerClientTest() {
    private val pkceClient = mockk<PKCEClient>(relaxed = true)
    private val sessionManager = mockk<StytchConsumerAuthenticationStateManager>(relaxed = true)
    private val client = MagicLinksImpl(dispatchers, networkingClient, pkceClient, sessionManager)

    private val fakePair = PKCECodePair(challenge = "test-challenge", verifier = "test-verifier")

    // --- authenticate ---

    @Test
    fun `authenticate retrieves PKCE, calls magicLinksAuthenticate with verifier, then revokes PKCE`() =
        runTest(testDispatcher) {
            coEvery { pkceClient.retrieve() } returns fakePair
            coEvery { api.magicLinksAuthenticate(any()) } returns StytchDataResponse(mockk(relaxed = true))

            client.authenticate(MagicLinksAuthenticateParameters(token = "tok", sessionDurationMinutes = 30))

            coVerify {
                api.magicLinksAuthenticate(
                    MagicLinksAuthenticateRequest(token = "tok", sessionDurationMinutes = 30, codeVerifier = "test-verifier"),
                )
            }
            coVerify { pkceClient.revoke() }
        }

    @Test
    fun `authenticate throws IllegalStateException when PKCE is missing`() =
        runTest(testDispatcher) {
            coEvery { pkceClient.retrieve() } returns null

            assertFailsWith<MissingPKCEException> {
                client.authenticate(MagicLinksAuthenticateParameters(token = "tok", sessionDurationMinutes = 30))
            }
        }

    // --- email.loginOrCreate ---

    @Test
    fun `email loginOrCreate creates PKCE and calls magicLinksEmailLoginOrCreate with code challenge`() =
        runTest(testDispatcher) {
            coEvery { pkceClient.create() } returns fakePair
            coEvery { api.magicLinksEmailLoginOrCreate(any()) } returns StytchDataResponse(mockk(relaxed = true))

            client.email.loginOrCreate(MagicLinksEmailLoginOrCreateParameters(email = "test@example.com"))

            coVerify {
                api.magicLinksEmailLoginOrCreate(
                    MagicLinksEmailLoginOrCreateRequest(email = "test@example.com", codeChallenge = "test-challenge"),
                )
            }
        }

    // --- email.send ---

    @Test
    fun `email send creates PKCE and calls primary endpoint when no session token`() =
        runTest(testDispatcher) {
            coEvery { pkceClient.create() } returns fakePair
            every { sessionManager.currentSessionToken } returns null
            coEvery { api.magicLinksEmailSendPrimary(any()) } returns StytchDataResponse(mockk(relaxed = true))

            client.email.send(MagicLinksEmailSendSecondaryParameters(email = "test@example.com"))

            coVerify {
                api.magicLinksEmailSendPrimary(
                    MagicLinksEmailSendSecondaryRequest(email = "test@example.com", codeChallenge = "test-challenge"),
                )
            }
            coVerify(exactly = 0) { api.magicLinksEmailSendSecondary(any()) }
        }

    @Test
    fun `email send creates PKCE and calls secondary endpoint when session token present`() =
        runTest(testDispatcher) {
            coEvery { pkceClient.create() } returns fakePair
            every { sessionManager.currentSessionToken } returns "session-tok"
            coEvery { api.magicLinksEmailSendSecondary(any()) } returns StytchDataResponse(mockk(relaxed = true))

            client.email.send(MagicLinksEmailSendSecondaryParameters(email = "test@example.com"))

            coVerify {
                api.magicLinksEmailSendSecondary(
                    MagicLinksEmailSendSecondaryRequest(email = "test@example.com", codeChallenge = "test-challenge"),
                )
            }
            coVerify(exactly = 0) { api.magicLinksEmailSendPrimary(any()) }
        }
}
