package com.stytch.sdk.b2b.passwords

import com.stytch.sdk.b2b.B2BClientTest
import com.stytch.sdk.b2b.StytchB2BAuthenticationStateManager
import com.stytch.sdk.b2b.networking.models.IB2BPasswordAuthenticateParameters
import com.stytch.sdk.b2b.networking.models.IB2BPasswordEmailResetParameters
import com.stytch.sdk.b2b.networking.models.IB2BPasswordEmailResetStartParameters
import com.stytch.sdk.b2b.networking.models.IB2BPasswordExistingPasswordResetParameters
import com.stytch.sdk.b2b.networking.models.IB2BPasswordSessionResetParameters
import com.stytch.sdk.b2b.networking.models.IB2BPasswordStrengthCheckParameters
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
internal class B2BPasswordsClientImplTest : B2BClientTest() {
    private val pkceClient = mockk<PKCEClient>(relaxed = true)
    private val sessionManager = mockk<StytchB2BAuthenticationStateManager>(relaxed = true)
    private val fakePair = PKCECodePair(challenge = "test-challenge", verifier = "test-verifier")

    private val client = B2BPasswordsClientImpl(dispatchers, networkingClient, pkceClient, sessionManager)

    // --- authenticate ---

    @Test
    fun `authenticate calls b2BPasswordAuthenticate with intermediateSessionToken`() =
        runTest(testDispatcher) {
            every { sessionManager.intermediateSessionToken } returns "ist-token"
            coEvery { api.b2BPasswordAuthenticate(any()) } returns StytchDataResponse(mockk(relaxed = true))

            client.authenticate(mockk<IB2BPasswordAuthenticateParameters>(relaxed = true))

            coVerify { api.b2BPasswordAuthenticate(match { it.intermediateSessionToken == "ist-token" }) }
        }

    // --- strengthCheck ---

    @Test
    fun `strengthCheck calls b2BPasswordStrengthCheck`() =
        runTest(testDispatcher) {
            coEvery { api.b2BPasswordStrengthCheck(any()) } returns StytchDataResponse(mockk(relaxed = true))

            client.strengthCheck(mockk<IB2BPasswordStrengthCheckParameters>(relaxed = true))

            coVerify { api.b2BPasswordStrengthCheck(any()) }
        }

    // --- email.resetStart ---

    @Test
    fun `email resetStart creates PKCE and calls b2BPasswordEmailResetStart`() =
        runTest(testDispatcher) {
            coEvery { pkceClient.create() } returns fakePair
            coEvery { api.b2BPasswordEmailResetStart(any()) } returns StytchDataResponse(mockk(relaxed = true))

            client.email.resetStart(mockk<IB2BPasswordEmailResetStartParameters>(relaxed = true))

            coVerify { api.b2BPasswordEmailResetStart(match { it.codeChallenge == "test-challenge" }) }
        }

    // --- email.reset ---

    @Test
    fun `email reset retrieves PKCE and calls b2BPasswordEmailReset with verifier and IST`() =
        runTest(testDispatcher) {
            coEvery { pkceClient.retrieve() } returns fakePair
            every { sessionManager.intermediateSessionToken } returns "ist-token"
            coEvery { api.b2BPasswordEmailReset(any()) } returns StytchDataResponse(mockk(relaxed = true))

            client.email.reset(mockk<IB2BPasswordEmailResetParameters>(relaxed = true))

            coVerify {
                api.b2BPasswordEmailReset(
                    match { it.codeVerifier == "test-verifier" && it.intermediateSessionToken == "ist-token" },
                )
            }
            coVerify { pkceClient.revoke() }
        }

    @Test
    fun `email reset throws MissingPKCEException when no code pair stored`() =
        runTest(testDispatcher) {
            coEvery { pkceClient.retrieve() } returns null

            assertFailsWith<MissingPKCEException> {
                client.email.reset(mockk<IB2BPasswordEmailResetParameters>(relaxed = true))
            }
        }

    // --- existingPassword.reset ---

    @Test
    fun `existingPassword reset calls b2BPasswordExistingPasswordReset`() =
        runTest(testDispatcher) {
            coEvery { api.b2BPasswordExistingPasswordReset(any()) } returns StytchDataResponse(mockk(relaxed = true))

            client.existingPassword.reset(mockk<IB2BPasswordExistingPasswordResetParameters>(relaxed = true))

            coVerify { api.b2BPasswordExistingPasswordReset(any()) }
        }

    // --- session.reset ---

    @Test
    fun `session reset calls b2BPasswordSessionReset`() =
        runTest(testDispatcher) {
            coEvery { api.b2BPasswordSessionReset(any()) } returns StytchDataResponse(mockk(relaxed = true))

            client.session.reset(mockk<IB2BPasswordSessionResetParameters>(relaxed = true))

            coVerify { api.b2BPasswordSessionReset(any()) }
        }
}
