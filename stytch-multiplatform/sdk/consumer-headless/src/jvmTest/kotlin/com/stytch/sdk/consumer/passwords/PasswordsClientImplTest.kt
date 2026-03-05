package com.stytch.sdk.consumer.passwords

import com.stytch.sdk.consumer.ConsumerClientTest
import com.stytch.sdk.consumer.networking.models.PasswordsAuthenticateParameters
import com.stytch.sdk.consumer.networking.models.PasswordsAuthenticateRequest
import com.stytch.sdk.consumer.networking.models.PasswordsCreateParameters
import com.stytch.sdk.consumer.networking.models.PasswordsCreateRequest
import com.stytch.sdk.consumer.networking.models.PasswordsEmailResetParameters
import com.stytch.sdk.consumer.networking.models.PasswordsEmailResetRequest
import com.stytch.sdk.consumer.networking.models.PasswordsEmailResetStartParameters
import com.stytch.sdk.consumer.networking.models.PasswordsEmailResetStartRequest
import com.stytch.sdk.consumer.networking.models.PasswordsExistingPasswordResetParameters
import com.stytch.sdk.consumer.networking.models.PasswordsExistingPasswordResetRequest
import com.stytch.sdk.consumer.networking.models.PasswordsSessionResetParameters
import com.stytch.sdk.consumer.networking.models.PasswordsSessionResetRequest
import com.stytch.sdk.consumer.networking.models.PasswordsStrengthCheckParameters
import com.stytch.sdk.consumer.networking.models.PasswordsStrengthCheckRequest
import com.stytch.sdk.data.PKCECodePair
import com.stytch.sdk.data.StytchDataResponse
import com.stytch.sdk.pkce.PKCEClient
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFailsWith

@OptIn(ExperimentalCoroutinesApi::class)
internal class PasswordsClientImplTest : ConsumerClientTest() {
    private val pkceClient = mockk<PKCEClient>(relaxed = true)
    private val client = PasswordsClientImpl(dispatchers, networkingClient, pkceClient)

    private val fakePair = PKCECodePair(challenge = "test-challenge", verifier = "test-verifier")

    @Test
    fun `authenticate calls passwordsAuthenticate with correct network model`() =
        runTest(testDispatcher) {
            coEvery { api.passwordsAuthenticate(any()) } returns StytchDataResponse(mockk(relaxed = true))

            client.authenticate(
                PasswordsAuthenticateParameters(email = "test@example.com", password = "p@ssw0rd", sessionDurationMinutes = 30),
            )

            coVerify {
                api.passwordsAuthenticate(
                    PasswordsAuthenticateRequest(email = "test@example.com", password = "p@ssw0rd", sessionDurationMinutes = 30),
                )
            }
        }

    @Test
    fun `create calls passwordsCreate with correct network model`() =
        runTest(testDispatcher) {
            coEvery { api.passwordsCreate(any()) } returns StytchDataResponse(mockk(relaxed = true))

            client.create(PasswordsCreateParameters(email = "test@example.com", password = "p@ssw0rd", sessionDurationMinutes = 30))

            coVerify {
                api.passwordsCreate(
                    PasswordsCreateRequest(email = "test@example.com", password = "p@ssw0rd", sessionDurationMinutes = 30),
                )
            }
        }

    @Test
    fun `resetByEmailStart creates PKCE and calls passwordsEmailResetStart with code challenge`() =
        runTest(testDispatcher) {
            coEvery { pkceClient.create() } returns fakePair
            coEvery { api.passwordsEmailResetStart(any()) } returns StytchDataResponse(mockk(relaxed = true))

            client.resetByEmailStart(PasswordsEmailResetStartParameters(email = "test@example.com"))

            coVerify {
                api.passwordsEmailResetStart(
                    PasswordsEmailResetStartRequest(email = "test@example.com", codeChallenge = "test-challenge"),
                )
            }
        }

    @Test
    fun `resetByEmail retrieves PKCE and calls passwordsEmailReset with code verifier`() =
        runTest(testDispatcher) {
            coEvery { pkceClient.retrieve() } returns fakePair
            coEvery { api.passwordsEmailReset(any()) } returns StytchDataResponse(mockk(relaxed = true))

            client.resetByEmail(PasswordsEmailResetParameters(token = "reset-tok", password = "newP@ss", sessionDurationMinutes = 30))

            coVerify {
                api.passwordsEmailReset(
                    PasswordsEmailResetRequest(
                        token = "reset-tok",
                        password = "newP@ss",
                        sessionDurationMinutes = 30,
                        codeVerifier = "test-verifier",
                    ),
                )
            }
        }

    @Test
    fun `resetByEmail throws IllegalStateException when PKCE is missing`() =
        runTest(testDispatcher) {
            coEvery { pkceClient.retrieve() } returns null

            assertFailsWith<IllegalStateException> {
                client.resetByEmail(PasswordsEmailResetParameters(token = "reset-tok", password = "newP@ss", sessionDurationMinutes = 30))
            }
        }

    @Test
    fun `resetByExistingPassword calls passwordsExistingPasswordReset with correct network model`() =
        runTest(testDispatcher) {
            coEvery { api.passwordsExistingPasswordReset(any()) } returns StytchDataResponse(mockk(relaxed = true))

            client.resetByExistingPassword(
                PasswordsExistingPasswordResetParameters(
                    email = "test@example.com",
                    existingPassword = "old",
                    newPassword = "new",
                ),
            )

            coVerify {
                api.passwordsExistingPasswordReset(
                    PasswordsExistingPasswordResetRequest(email = "test@example.com", existingPassword = "old", newPassword = "new"),
                )
            }
        }

    @Test
    fun `resetBySession calls passwordsSessionReset with correct network model`() =
        runTest(testDispatcher) {
            coEvery { api.passwordsSessionReset(any()) } returns StytchDataResponse(mockk(relaxed = true))

            client.resetBySession(PasswordsSessionResetParameters(password = "newP@ss"))

            coVerify { api.passwordsSessionReset(PasswordsSessionResetRequest(password = "newP@ss")) }
        }

    @Test
    fun `strengthCheck calls passwordsStrengthCheck with correct network model`() =
        runTest(testDispatcher) {
            coEvery { api.passwordsStrengthCheck(any()) } returns StytchDataResponse(mockk(relaxed = true))

            client.strengthCheck(PasswordsStrengthCheckParameters(password = "p@ssw0rd"))

            coVerify { api.passwordsStrengthCheck(PasswordsStrengthCheckRequest(password = "p@ssw0rd")) }
        }
}
