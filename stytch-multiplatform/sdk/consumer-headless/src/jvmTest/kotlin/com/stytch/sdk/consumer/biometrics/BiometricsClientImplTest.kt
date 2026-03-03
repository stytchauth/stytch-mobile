package com.stytch.sdk.consumer.biometrics

import com.stytch.sdk.StytchAuthenticationStateManager
import com.stytch.sdk.biometrics.BiometricsAvailability
import com.stytch.sdk.biometrics.BiometricsParameters
import com.stytch.sdk.biometrics.BiometricsUnsupportedError
import com.stytch.sdk.biometrics.IBiometricsProvider
import com.stytch.sdk.consumer.ConsumerClientTest
import com.stytch.sdk.consumer.networking.models.BiometricsAuthenticateStartResponse
import com.stytch.sdk.consumer.networking.models.BiometricsRegisterResponse
import com.stytch.sdk.consumer.networking.models.BiometricsRegisterStartResponse
import com.stytch.sdk.data.Ed25519KeyPair
import com.stytch.sdk.data.StytchDataResponse
import com.stytch.sdk.encryption.StytchEncryptionClient
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
    private val encryptionClient = mockk<StytchEncryptionClient>(relaxed = true)
    private val biometricsProvider = mockk<IBiometricsProvider>(relaxed = true)
    private val client = BiometricsClientImpl(dispatchers, networkingClient, sessionManager, encryptionClient, biometricsProvider)

    private val params = BiometricsParameters(sessionDurationMinutes = 30)
    private val fakeKeyPair = Ed25519KeyPair(
        publicKey = byteArrayOf(1, 2, 3),
        privateKey = byteArrayOf(4, 5, 6),
        encryptedPrivateKey = byteArrayOf(7, 8, 9),
    )

    // --- getAvailability ---

    @Test
    fun `getAvailability delegates to biometricsProvider`() = runTest(testDispatcher) {
        coEvery { biometricsProvider.getAvailability(params) } returns BiometricsAvailability.Available

        val result = client.getAvailability(params)

        coVerify { biometricsProvider.getAvailability(params) }
        assertTrue(result == BiometricsAvailability.Available)
    }

    // --- register error paths ---

    @Test
    fun `register throws BiometricsUnsupportedError when availability is Unavailable`() = runTest(testDispatcher) {
        coEvery { biometricsProvider.getAvailability(params) } returns BiometricsAvailability.Unavailable(reason = "no hardware")

        assertFailsWith<BiometricsUnsupportedError> { client.register(params) }
    }

    @Test
    fun `register throws BiometricsAlreadyEnrolled when availability is AlreadyRegistered`() = runTest(testDispatcher) {
        coEvery { biometricsProvider.getAvailability(params) } returns BiometricsAvailability.AlreadyRegistered

        assertFailsWith<BiometricsAlreadyEnrolled> { client.register(params) }
    }

    @Test
    fun `register throws NoSessionExists when no session token`() = runTest(testDispatcher) {
        coEvery { biometricsProvider.getAvailability(params) } returns BiometricsAvailability.Available
        every { sessionManager.currentSessionToken } returns null

        assertFailsWith<NoSessionExists> { client.register(params) }
    }

    // --- register happy path ---

    @Test
    fun `register generates key pair, calls registerStart, signs challenge, calls register, persists`() = runTest(testDispatcher) {
        coEvery { biometricsProvider.getAvailability(params) } returns BiometricsAvailability.Available
        every { sessionManager.currentSessionToken } returns "session-tok"
        coEvery { biometricsProvider.register(params) } returns fakeKeyPair

        val startResp = mockk<BiometricsRegisterStartResponse>()
        every { startResp.biometricRegistrationId } returns "bio-reg-id"
        every { startResp.challenge } returns "challenge-text"
        coEvery { api.biometricsRegisterStart(any()) } returns StytchDataResponse(startResp)

        every { encryptionClient.signEd25519(fakeKeyPair.privateKey, any()) } returns byteArrayOf(10, 11, 12)

        val registerResp = mockk<BiometricsRegisterResponse>(relaxed = true)
        every { registerResp.biometricRegistrationId } returns "bio-reg-id"
        coEvery { api.biometricsRegister(any()) } returns StytchDataResponse(registerResp)

        client.register(params)

        coVerify { biometricsProvider.register(params) }
        coVerify { api.biometricsRegisterStart(any()) }
        coVerify { encryptionClient.signEd25519(fakeKeyPair.privateKey, any()) }
        coVerify { api.biometricsRegister(any()) }
        coVerify { biometricsProvider.persistRegistration(eq("bio-reg-id"), any()) }
    }

    // --- authenticate error path ---

    @Test
    fun `authenticate throws NoBiometricsRegistered when availability is not AlreadyRegistered`() = runTest(testDispatcher) {
        coEvery { biometricsProvider.getAvailability(params) } returns BiometricsAvailability.Available

        assertFailsWith<NoBiometricsRegistered> { client.authenticate(params) }
    }

    // --- authenticate happy path ---

    @Test
    fun `authenticate gets key pair, calls authenticateStart, signs challenge, calls authenticate`() = runTest(testDispatcher) {
        coEvery { biometricsProvider.getAvailability(params) } returns BiometricsAvailability.AlreadyRegistered
        coEvery { biometricsProvider.authenticate(params) } returns fakeKeyPair

        val startResp = mockk<BiometricsAuthenticateStartResponse>()
        every { startResp.biometricRegistrationId } returns "bio-reg-id"
        every { startResp.challenge } returns "challenge-text"
        coEvery { api.biometricsAuthenticateStart(any()) } returns StytchDataResponse(startResp)

        every { encryptionClient.signEd25519(fakeKeyPair.privateKey, any()) } returns byteArrayOf(10, 11, 12)
        coEvery { api.biometricsAuthenticate(any()) } returns StytchDataResponse(mockk(relaxed = true))

        client.authenticate(params)

        coVerify { biometricsProvider.authenticate(params) }
        coVerify { api.biometricsAuthenticateStart(any()) }
        coVerify { encryptionClient.signEd25519(fakeKeyPair.privateKey, any()) }
        coVerify { api.biometricsAuthenticate(any()) }
    }

    // --- removeRegistration ---

    @Test
    fun `removeRegistration delegates to biometricsProvider`() = runTest(testDispatcher) {
        client.removeRegistration()

        coVerify { biometricsProvider.removeRegistration() }
    }
}
