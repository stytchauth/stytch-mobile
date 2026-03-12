package com.stytch.sdk.consumer.user

import com.stytch.sdk.consumer.ConsumerClientTest
import com.stytch.sdk.consumer.networking.models.UpdateMeParameters
import com.stytch.sdk.consumer.networking.models.UpdateMeRequest
import com.stytch.sdk.data.StytchDataResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.JsonPrimitive
import kotlin.test.Test
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
internal class UserClientImplTest : ConsumerClientTest() {
    private val client = UserClientImpl(dispatchers, networkingClient)

    @Test
    fun `getUser calls getMe`() =
        runTest(testDispatcher) {
            coEvery { api.getMe() } returns StytchDataResponse(mockk(relaxed = true))

            client.getUser()

            coVerify { api.getMe() }
        }

    @Test
    fun `update calls updateMe with correct network model`() =
        runTest(testDispatcher) {
            coEvery { api.updateMe(any()) } returns StytchDataResponse(mockk(relaxed = true))

            client.update(UpdateMeParameters(trustedMetadata = mapOf("role" to "admin")))

            coVerify { api.updateMe(UpdateMeRequest(trustedMetadata = mapOf("role" to JsonPrimitive("admin")))) }
        }

    // --- deleteFactor routing ---

    @Test
    fun `deleteFactor TOTP calls deleteTOTP with factor id`() =
        runTest(testDispatcher) {
            coEvery { api.deleteTOTP(any()) } returns StytchDataResponse(mockk(relaxed = true))

            val result = client.deleteFactor(AuthenticationFactor.TOTP("totp-id"))

            coVerify { api.deleteTOTP("totp-id") }
            assertIs<DeleteFactorResponse>(result)
        }

    @Test
    fun `deleteFactor Biometric calls deleteBiometricRegistration with factor id`() =
        runTest(testDispatcher) {
            coEvery { api.deleteBiometricRegistration(any()) } returns StytchDataResponse(mockk(relaxed = true))

            val result = client.deleteFactor(AuthenticationFactor.Biometric("bio-id"))

            coVerify { api.deleteBiometricRegistration("bio-id") }
            assertIs<DeleteFactorResponse>(result)
        }

    @Test
    fun `deleteFactor CryptoWallet calls deleteCryptoWallet with factor id`() =
        runTest(testDispatcher) {
            coEvery { api.deleteCryptoWallet(any()) } returns StytchDataResponse(mockk(relaxed = true))

            val result = client.deleteFactor(AuthenticationFactor.CryptoWallet("crypto-id"))

            coVerify { api.deleteCryptoWallet("crypto-id") }
            assertIs<DeleteFactorResponse>(result)
        }

    @Test
    fun `deleteFactor Email calls deleteEmail with factor id`() =
        runTest(testDispatcher) {
            coEvery { api.deleteEmail(any()) } returns StytchDataResponse(mockk(relaxed = true))

            val result = client.deleteFactor(AuthenticationFactor.Email("email-id"))

            coVerify { api.deleteEmail("email-id") }
            assertIs<DeleteFactorResponse>(result)
        }

    @Test
    fun `deleteFactor OAuth calls deleteOAuthUserRegistration with factor id`() =
        runTest(testDispatcher) {
            coEvery { api.deleteOAuthUserRegistration(any()) } returns StytchDataResponse(mockk(relaxed = true))

            val result = client.deleteFactor(AuthenticationFactor.OAuth("oauth-id"))

            coVerify { api.deleteOAuthUserRegistration("oauth-id") }
            assertIs<DeleteFactorResponse>(result)
        }

    @Test
    fun `deleteFactor PhoneNumber calls deletePhoneNumber with factor id`() =
        runTest(testDispatcher) {
            coEvery { api.deletePhoneNumber(any()) } returns StytchDataResponse(mockk(relaxed = true))

            val result = client.deleteFactor(AuthenticationFactor.PhoneNumber("phone-id"))

            coVerify { api.deletePhoneNumber("phone-id") }
            assertIs<DeleteFactorResponse>(result)
        }

    @Test
    fun `deleteFactor WebAuthn calls deleteWebAuthnRegistration with factor id`() =
        runTest(testDispatcher) {
            coEvery { api.deleteWebAuthnRegistration(any()) } returns StytchDataResponse(mockk(relaxed = true))

            val result = client.deleteFactor(AuthenticationFactor.WebAuthn("webauthn-id"))

            coVerify { api.deleteWebAuthnRegistration("webauthn-id") }
            assertIs<DeleteFactorResponse>(result)
        }
}
