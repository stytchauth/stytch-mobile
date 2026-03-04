package com.stytch.sdk.consumer.otp

import com.stytch.sdk.consumer.ConsumerClientTest
import com.stytch.sdk.consumer.StytchConsumerAuthenticationStateManager
import com.stytch.sdk.consumer.networking.models.OTPsAuthenticateParameters
import com.stytch.sdk.consumer.networking.models.OTPsAuthenticateRequest
import com.stytch.sdk.consumer.networking.models.OTPsEmailLoginOrCreateParameters
import com.stytch.sdk.consumer.networking.models.OTPsEmailLoginOrCreateRequest
import com.stytch.sdk.consumer.networking.models.OTPsEmailSendSecondaryParameters
import com.stytch.sdk.consumer.networking.models.OTPsEmailSendSecondaryRequest
import com.stytch.sdk.consumer.networking.models.OTPsSMSLoginOrCreateParameters
import com.stytch.sdk.consumer.networking.models.OTPsSMSLoginOrCreateRequest
import com.stytch.sdk.consumer.networking.models.OTPsSMSSendSecondaryParameters
import com.stytch.sdk.consumer.networking.models.OTPsSMSSendSecondaryRequest
import com.stytch.sdk.consumer.networking.models.OTPsWhatsAppLoginOrCreateParameters
import com.stytch.sdk.consumer.networking.models.OTPsWhatsAppLoginOrCreateRequest
import com.stytch.sdk.consumer.networking.models.OTPsWhatsAppSendSecondaryParameters
import com.stytch.sdk.consumer.networking.models.OTPsWhatsAppSendSecondaryRequest
import com.stytch.sdk.data.StytchDataResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class OtpImplTest : ConsumerClientTest() {
    private val sessionManager = mockk<StytchConsumerAuthenticationStateManager>(relaxed = true)
    private val client = OtpImpl(dispatchers, networkingClient, sessionManager)

    @Test
    fun `authenticate calls oTPsAuthenticate with correct network model`() = runTest(testDispatcher) {
        coEvery { api.oTPsAuthenticate(any()) } returns StytchDataResponse(mockk(relaxed = true))

        client.authenticate(OTPsAuthenticateParameters(token = "tok", methodId = "mid", sessionDurationMinutes = 30))

        coVerify { api.oTPsAuthenticate(OTPsAuthenticateRequest(token = "tok", methodId = "mid", sessionDurationMinutes = 30)) }
    }

    // --- SMS ---

    @Test
    fun `sms loginOrCreate calls oTPsSMSLoginOrCreate with correct network model`() = runTest(testDispatcher) {
        coEvery { api.oTPsSMSLoginOrCreate(any()) } returns StytchDataResponse(mockk(relaxed = true))

        client.sms.loginOrCreate(OTPsSMSLoginOrCreateParameters(phoneNumber = "+15551234567"))

        coVerify { api.oTPsSMSLoginOrCreate(OTPsSMSLoginOrCreateRequest(phoneNumber = "+15551234567")) }
    }

    @Test
    fun `sms send calls primary endpoint when no session token`() = runTest(testDispatcher) {
        every { sessionManager.currentSessionToken } returns null
        coEvery { api.oTPsSMSSendPrimary(any()) } returns StytchDataResponse(mockk(relaxed = true))

        client.sms.send(OTPsSMSSendSecondaryParameters(phoneNumber = "+15551234567"))

        coVerify { api.oTPsSMSSendPrimary(OTPsSMSSendSecondaryRequest(phoneNumber = "+15551234567")) }
        coVerify(exactly = 0) { api.oTPsSMSSendSecondary(any()) }
    }

    @Test
    fun `sms send calls secondary endpoint when session token present`() = runTest(testDispatcher) {
        every { sessionManager.currentSessionToken } returns "session-tok"
        coEvery { api.oTPsSMSSendSecondary(any()) } returns StytchDataResponse(mockk(relaxed = true))

        client.sms.send(OTPsSMSSendSecondaryParameters(phoneNumber = "+15551234567"))

        coVerify { api.oTPsSMSSendSecondary(OTPsSMSSendSecondaryRequest(phoneNumber = "+15551234567")) }
        coVerify(exactly = 0) { api.oTPsSMSSendPrimary(any()) }
    }

    // --- Email ---

    @Test
    fun `email loginOrCreate calls oTPsEmailLoginOrCreate with correct network model`() = runTest(testDispatcher) {
        coEvery { api.oTPsEmailLoginOrCreate(any()) } returns StytchDataResponse(mockk(relaxed = true))

        client.email.loginOrCreate(OTPsEmailLoginOrCreateParameters(email = "test@example.com"))

        coVerify { api.oTPsEmailLoginOrCreate(OTPsEmailLoginOrCreateRequest(email = "test@example.com")) }
    }

    @Test
    fun `email send calls primary endpoint when no session token`() = runTest(testDispatcher) {
        every { sessionManager.currentSessionToken } returns null
        coEvery { api.oTPsEmailSendPrimary(any()) } returns StytchDataResponse(mockk(relaxed = true))

        client.email.send(OTPsEmailSendSecondaryParameters(email = "test@example.com"))

        coVerify { api.oTPsEmailSendPrimary(OTPsEmailSendSecondaryRequest(email = "test@example.com")) }
        coVerify(exactly = 0) { api.oTPsEmailSendSecondary(any()) }
    }

    @Test
    fun `email send calls secondary endpoint when session token present`() = runTest(testDispatcher) {
        every { sessionManager.currentSessionToken } returns "session-tok"
        coEvery { api.oTPsEmailSendSecondary(any()) } returns StytchDataResponse(mockk(relaxed = true))

        client.email.send(OTPsEmailSendSecondaryParameters(email = "test@example.com"))

        coVerify { api.oTPsEmailSendSecondary(OTPsEmailSendSecondaryRequest(email = "test@example.com")) }
        coVerify(exactly = 0) { api.oTPsEmailSendPrimary(any()) }
    }

    // --- WhatsApp ---

    @Test
    fun `whatsapp loginOrCreate calls oTPsWhatsAppLoginOrCreate with correct network model`() = runTest(testDispatcher) {
        coEvery { api.oTPsWhatsAppLoginOrCreate(any()) } returns StytchDataResponse(mockk(relaxed = true))

        client.whatsapp.loginOrCreate(OTPsWhatsAppLoginOrCreateParameters(phoneNumber = "+15551234567"))

        coVerify { api.oTPsWhatsAppLoginOrCreate(OTPsWhatsAppLoginOrCreateRequest(phoneNumber = "+15551234567")) }
    }

    @Test
    fun `whatsapp send calls primary endpoint when no session token`() = runTest(testDispatcher) {
        every { sessionManager.currentSessionToken } returns null
        coEvery { api.oTPsWhatsAppSendPrimary(any()) } returns StytchDataResponse(mockk(relaxed = true))

        client.whatsapp.send(OTPsWhatsAppSendSecondaryParameters(phoneNumber = "+15551234567"))

        coVerify { api.oTPsWhatsAppSendPrimary(OTPsWhatsAppSendSecondaryRequest(phoneNumber = "+15551234567")) }
        coVerify(exactly = 0) { api.oTPsWhatsAppSendSecondary(any()) }
    }

    @Test
    fun `whatsapp send calls secondary endpoint when session token present`() = runTest(testDispatcher) {
        every { sessionManager.currentSessionToken } returns "session-tok"
        coEvery { api.oTPsWhatsAppSendSecondary(any()) } returns StytchDataResponse(mockk(relaxed = true))

        client.whatsapp.send(OTPsWhatsAppSendSecondaryParameters(phoneNumber = "+15551234567"))

        coVerify { api.oTPsWhatsAppSendSecondary(OTPsWhatsAppSendSecondaryRequest(phoneNumber = "+15551234567")) }
        coVerify(exactly = 0) { api.oTPsWhatsAppSendPrimary(any()) }
    }
}
