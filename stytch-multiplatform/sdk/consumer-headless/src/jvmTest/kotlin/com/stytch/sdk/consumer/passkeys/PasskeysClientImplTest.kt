package com.stytch.sdk.consumer.passkeys

import com.stytch.sdk.consumer.ConsumerClientTest
import com.stytch.sdk.consumer.StytchConsumerAuthenticationStateManager
import com.stytch.sdk.consumer.networking.models.WebAuthnAuthenticateRequest
import com.stytch.sdk.consumer.networking.models.WebAuthnAuthenticateStartSecondaryRequest
import com.stytch.sdk.consumer.networking.models.WebAuthnRegisterRequest
import com.stytch.sdk.consumer.networking.models.WebAuthnRegisterStartRequest
import com.stytch.sdk.consumer.networking.models.WebAuthnRegisterStartResponse
import com.stytch.sdk.consumer.networking.models.WebAuthnAuthenticateStartSecondaryResponse
import com.stytch.sdk.consumer.networking.models.WebAuthnUpdateParameters
import com.stytch.sdk.consumer.networking.models.WebAuthnUpdateRequest
import com.stytch.sdk.data.StytchDataResponse
import com.stytch.sdk.passkeys.IPasskeyProvider
import com.stytch.sdk.passkeys.PasskeysParameters
import com.stytch.sdk.passkeys.PasskeysUnsupportedError
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@OptIn(ExperimentalCoroutinesApi::class)
internal class PasskeysClientImplTest : ConsumerClientTest() {
    private val passkeyProvider = mockk<IPasskeyProvider>(relaxed = true)
    private val sessionManager = mockk<StytchConsumerAuthenticationStateManager>(relaxed = true)
    private val client = PasskeysClientImpl(dispatchers, networkingClient, sessionManager, passkeyProvider)

    private val params = PasskeysParameters(domain = "example.com", sessionDurationMinutes = 30)

    // --- isSupported ---

    @Test
    fun `isSupported reflects passkeyProvider value`() {
        every { passkeyProvider.isSupported } returns true
        val supported = PasskeysClientImpl(dispatchers, networkingClient, sessionManager, passkeyProvider)
        assertEquals(true, supported.isSupported)

        every { passkeyProvider.isSupported } returns false
        val unsupported = PasskeysClientImpl(dispatchers, networkingClient, sessionManager, passkeyProvider)
        assertEquals(false, unsupported.isSupported)
    }

    // --- register ---

    @Test
    fun `register throws PasskeysUnsupportedError when not supported`() = runTest(testDispatcher) {
        every { passkeyProvider.isSupported } returns false
        val c = PasskeysClientImpl(dispatchers, networkingClient, sessionManager, passkeyProvider)

        assertFailsWith<PasskeysUnsupportedError> { c.register(params) }
    }

    @Test
    fun `register calls registerStart, threads options to provider, then calls webAuthnRegister`() = runTest(testDispatcher) {
        every { passkeyProvider.isSupported } returns true
        val c = PasskeysClientImpl(dispatchers, networkingClient, sessionManager, passkeyProvider)

        val startResp = mockk<WebAuthnRegisterStartResponse>()
        every { startResp.publicKeyCredentialCreationOptions } returns "creation-options"
        coEvery { api.webAuthnRegisterStart(any()) } returns StytchDataResponse(startResp)
        coEvery { passkeyProvider.createPublicKeyCredential(params, dispatchers, "creation-options") } returns "cred-json"
        coEvery { api.webAuthnRegister(any()) } returns StytchDataResponse(mockk(relaxed = true))

        c.register(params)

        coVerify {
            api.webAuthnRegisterStart(
                WebAuthnRegisterStartRequest(
                    domain = "example.com",
                    authenticatorType = "platform",
                    returnPasskeyCredentialOptions = true,
                ),
            )
        }
        coVerify { passkeyProvider.createPublicKeyCredential(params, dispatchers, "creation-options") }
        coVerify {
            api.webAuthnRegister(
                WebAuthnRegisterRequest(publicKeyCredential = "cred-json", sessionDurationMinutes = 30),
            )
        }
    }

    // --- authenticate ---

    @Test
    fun `authenticate throws PasskeysUnsupportedError when not supported`() = runTest(testDispatcher) {
        every { passkeyProvider.isSupported } returns false
        val c = PasskeysClientImpl(dispatchers, networkingClient, sessionManager, passkeyProvider)

        assertFailsWith<PasskeysUnsupportedError> { c.authenticate(params) }
    }

    @Test
    fun `authenticate calls primary start and threads options to provider when no session token`() = runTest(testDispatcher) {
        every { passkeyProvider.isSupported } returns true
        every { sessionManager.currentSessionToken } returns null
        val c = PasskeysClientImpl(dispatchers, networkingClient, sessionManager, passkeyProvider)

        val startResp = mockk<WebAuthnAuthenticateStartSecondaryResponse>()
        every { startResp.publicKeyCredentialRequestOptions } returns "request-options"
        coEvery { api.webAuthnAuthenticateStartPrimary(any()) } returns StytchDataResponse(startResp)
        coEvery { passkeyProvider.getPublicKeyCredential(params, dispatchers, "request-options") } returns "cred-json"
        coEvery { api.webAuthnAuthenticate(any()) } returns StytchDataResponse(mockk(relaxed = true))

        c.authenticate(params)

        coVerify {
            api.webAuthnAuthenticateStartPrimary(
                WebAuthnAuthenticateStartSecondaryRequest(domain = "example.com", returnPasskeyCredentialOptions = true),
            )
        }
        coVerify { passkeyProvider.getPublicKeyCredential(params, dispatchers, "request-options") }
        coVerify {
            api.webAuthnAuthenticate(
                WebAuthnAuthenticateRequest(publicKeyCredential = "cred-json", sessionDurationMinutes = 30),
            )
        }
        coVerify(exactly = 0) { api.webAuthnAuthenticateStartSecondary(any()) }
    }

    @Test
    fun `authenticate calls secondary start when session token is present`() = runTest(testDispatcher) {
        every { passkeyProvider.isSupported } returns true
        every { sessionManager.currentSessionToken } returns "session-tok"
        val c = PasskeysClientImpl(dispatchers, networkingClient, sessionManager, passkeyProvider)

        val startResp = mockk<WebAuthnAuthenticateStartSecondaryResponse>()
        every { startResp.publicKeyCredentialRequestOptions } returns "request-options"
        coEvery { api.webAuthnAuthenticateStartSecondary(any()) } returns StytchDataResponse(startResp)
        coEvery { passkeyProvider.getPublicKeyCredential(any(), any(), any()) } returns "cred-json"
        coEvery { api.webAuthnAuthenticate(any()) } returns StytchDataResponse(mockk(relaxed = true))

        c.authenticate(params)

        coVerify { api.webAuthnAuthenticateStartSecondary(any()) }
        coVerify(exactly = 0) { api.webAuthnAuthenticateStartPrimary(any()) }
    }

    // --- update ---

    @Test
    fun `update throws PasskeysUnsupportedError when not supported`() = runTest(testDispatcher) {
        every { passkeyProvider.isSupported } returns false
        val c = PasskeysClientImpl(dispatchers, networkingClient, sessionManager, passkeyProvider)

        assertFailsWith<PasskeysUnsupportedError> { c.update("key-id", WebAuthnUpdateParameters(name = "My Key")) }
    }

    @Test
    fun `update calls webAuthnUpdate with id and correct network model`() = runTest(testDispatcher) {
        every { passkeyProvider.isSupported } returns true
        val c = PasskeysClientImpl(dispatchers, networkingClient, sessionManager, passkeyProvider)
        coEvery { api.webAuthnUpdate(any(), any()) } returns StytchDataResponse(mockk(relaxed = true))

        c.update("key-id", WebAuthnUpdateParameters(name = "My Key"))

        coVerify { api.webAuthnUpdate("key-id", WebAuthnUpdateRequest(name = "My Key")) }
    }
}
