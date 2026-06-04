package com.stytch.sdk.consumer.biometrics

import com.stytch.sdk.StytchAuthenticationStateManager
import com.stytch.sdk.biometrics.BiometricsAlreadyEnrolledError
import com.stytch.sdk.biometrics.BiometricsAvailability
import com.stytch.sdk.biometrics.BiometricsParameters
import com.stytch.sdk.biometrics.BiometricsUnsupportedError
import com.stytch.sdk.biometrics.IBiometricsProvider
import com.stytch.sdk.consumer.ConsumerClientTest
import com.stytch.sdk.consumer.networking.models.BiometricsAuthenticateStartResponse
import com.stytch.sdk.consumer.networking.models.BiometricsRegisterResponse
import com.stytch.sdk.consumer.networking.models.BiometricsRegisterStartResponse
import com.stytch.sdk.data.StytchDataResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
internal class BiometricsClientImplTest : ConsumerClientTest() {
    private val sessionManager = mockk<StytchAuthenticationStateManager>(relaxed = true)
    private val biometricsProvider = mockk<IBiometricsProvider>(relaxed = true)
    private val client = BiometricsClientImpl(dispatchers, networkingClient, sessionManager, biometricsProvider)

    private val params = BiometricsParameters(sessionDurationMinutes = 30)
    private val publicKey = "fake-public-key"
    private val signature = "fake-signature"

    // --- getAvailability ---

    @Test
    fun `getAvailability delegates to biometricsProvider`() =
        runTest(testDispatcher) {
            coEvery { biometricsProvider.getAvailability(params) } returns BiometricsAvailability.Available

            val result = client.getAvailability(params)

            coVerify { biometricsProvider.getAvailability(params) }
            assertTrue(result == BiometricsAvailability.Available)
        }

    // --- register error paths ---

    @Test
    fun `register throws BiometricsUnsupportedError when availability is Unavailable`() =
        runTest(testDispatcher) {
            coEvery { biometricsProvider.getAvailability(params) } returns BiometricsAvailability.Unavailable(reason = "no hardware")

            assertFailsWith<BiometricsUnsupportedError> { client.register(params) }
        }

    @Test
    fun `register throws BiometricsAlreadyEnrolled when availability is AlreadyRegistered`() =
        runTest(testDispatcher) {
            coEvery { biometricsProvider.getAvailability(params) } returns BiometricsAvailability.AlreadyRegistered

            assertFailsWith<BiometricsAlreadyEnrolledError> { client.register(params) }
        }

    @Test
    fun `register throws NoSessionExists when no session token`() =
        runTest(testDispatcher) {
            coEvery { biometricsProvider.getAvailability(params) } returns BiometricsAvailability.Available
            every { sessionManager.currentSessionToken } returns null

            assertFailsWith<NoSessionExists> { client.register(params) }
        }

    // --- register happy path ---

    @Test
    fun `register creates biometric key, calls registerStart, signs challenge, calls register, persists`() =
        runTest(testDispatcher) {
            coEvery { biometricsProvider.getAvailability(params) } returns BiometricsAvailability.Available
            every { sessionManager.currentSessionToken } returns "session-tok"
            coEvery { biometricsProvider.createBiometricKey(params) } returns publicKey
            coEvery { biometricsProvider.signWithBiometricKey("challenge-text") } returns signature

            val startResp = mockk<BiometricsRegisterStartResponse>()
            every { startResp.biometricRegistrationId } returns "bio-reg-id"
            every { startResp.challenge } returns "challenge-text"
            coEvery { api.biometricsRegisterStart(any()) } returns StytchDataResponse(startResp)

            val registerResp = mockk<BiometricsRegisterResponse>(relaxed = true)
            every { registerResp.biometricRegistrationId } returns "bio-reg-id"
            coEvery { api.biometricsRegister(any()) } returns StytchDataResponse(registerResp)

            client.register(params)

            coVerify { biometricsProvider.createBiometricKey(params) }
            coVerify { api.biometricsRegisterStart(any()) }
            coVerify { biometricsProvider.signWithBiometricKey("challenge-text") }
            coVerify { api.biometricsRegister(any()) }
            coVerify { biometricsProvider.persistRegistration(registrationId = "bio-reg-id") }
        }

    // --- authenticate error path ---

    @Test
    fun `authenticate throws NoBiometricsRegistered when availability is not AlreadyRegistered`() =
        runTest(testDispatcher) {
            coEvery { biometricsProvider.getAvailability(params) } returns BiometricsAvailability.Available

            assertFailsWith<NoBiometricsRegistered> { client.authenticate(params) }
        }

    // --- authenticate happy path ---

    @Test
    fun `authenticate retrieves biometric key, calls authenticateStart, signs challenge, calls authenticate`() =
        runTest(testDispatcher) {
            coEvery { biometricsProvider.getAvailability(params) } returns BiometricsAvailability.AlreadyRegistered
            coEvery { biometricsProvider.retrieveBiometricKey(params) } returns publicKey
            coEvery { biometricsProvider.signWithBiometricKey("challenge-text") } returns signature

            val startResp = mockk<BiometricsAuthenticateStartResponse>()
            every { startResp.biometricRegistrationId } returns "bio-reg-id"
            every { startResp.challenge } returns "challenge-text"
            coEvery { api.biometricsAuthenticateStart(any()) } returns StytchDataResponse(startResp)

            coEvery { api.biometricsAuthenticate(any()) } returns StytchDataResponse(mockk(relaxed = true))

            client.authenticate(params)

            coVerify { biometricsProvider.retrieveBiometricKey(params) }
            coVerify { api.biometricsAuthenticateStart(any()) }
            coVerify { biometricsProvider.signWithBiometricKey("challenge-text") }
            coVerify { api.biometricsAuthenticate(any()) }
        }

    // --- removeRegistration ---

    @Test
    fun `removeRegistration throws NoSessionExists when no session token`() =
        runTest(testDispatcher) {
            every { sessionManager.currentSessionToken } returns null

            assertFailsWith<NoSessionExists> { client.removeRegistration() }
        }

    @Test
    fun `removeRegistration delegates to biometricsProvider when session exists`() =
        runTest(testDispatcher) {
            every { sessionManager.currentSessionToken } returns "session-tok"

            client.removeRegistration()

            coVerify { biometricsProvider.removeRegistration() }
        }
}
