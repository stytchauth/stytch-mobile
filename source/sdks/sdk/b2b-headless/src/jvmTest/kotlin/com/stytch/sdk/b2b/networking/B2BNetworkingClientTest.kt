package com.stytch.sdk.b2b.networking

import com.stytch.sdk.b2b.StytchB2BAuthenticationStateManager
import com.stytch.sdk.b2b.networking.api.SdkExternalApi
import com.stytch.sdk.b2b.networking.models.ApiB2bSessionV1MemberSession
import com.stytch.sdk.b2b.networking.models.B2BSessionsAuthenticateResponse
import com.stytch.sdk.data.DeviceInfo
import com.stytch.sdk.data.EndpointOptions
import com.stytch.sdk.data.KMPPlatformType
import com.stytch.sdk.data.StytchClientConfigurationInternal
import com.stytch.sdk.data.StytchDataResponse
import com.stytch.sdk.data.StytchDispatchers
import com.stytch.sdk.encryption.StytchEncryptionClient
import com.stytch.sdk.persistence.StytchPlatformPersistenceClient
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours
import kotlin.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
internal class CheckAndHandleInitialB2BSessionTest {
    private val fixedNow: Instant = Clock.System.now()

    @Test
    fun `calls onExpired when session expiresAt is in the past`() =
        runTest {
            val session = mockk<ApiB2bSessionV1MemberSession>(relaxed = true)
            every { session.expiresAt } returns fixedNow - 1.hours
            var expiredCalled = false

            checkAndHandleInitialSession(
                session = session,
                now = fixedNow,
                onExpired = { expiredCalled = true },
                onValid = { error("should not be called") },
            )

            assertTrue(expiredCalled)
        }

    @Test
    fun `calls onValid when session expiresAt is in the future`() =
        runTest {
            val session = mockk<ApiB2bSessionV1MemberSession>(relaxed = true)
            every { session.expiresAt } returns fixedNow + 1.hours
            var validCalled = false

            checkAndHandleInitialSession(
                session = session,
                now = fixedNow,
                onExpired = { error("should not be called") },
                onValid = { validCalled = true },
            )

            assertTrue(validCalled)
        }
}

@OptIn(ExperimentalCoroutinesApi::class)
internal class B2BNetworkingClientTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private val dispatchers = StytchDispatchers(ioDispatcher = testDispatcher, mainDispatcher = testDispatcher)
    private val sessionManager = mockk<StytchB2BAuthenticationStateManager>(relaxed = true)
    private val mockApi = mockk<SdkExternalApi>(relaxed = true)

    init {
        every { sessionManager.sessionFlow } returns MutableStateFlow(null)
    }

    private fun makeClient() =
        B2BNetworkingClient(
            configuration =
                StytchClientConfigurationInternal(
                    publicToken = "public-token-test-00000000-0000-0000-0000-000000000000",
                    endpointOptions = EndpointOptions(),
                    defaultSessionDuration = 30,
                    deviceInfo = DeviceInfo("pkg", "1.0", "JVM", "1.0", "Test", "0x0"),
                    platformPersistenceClient = mockk<StytchPlatformPersistenceClient>(relaxed = true),
                    platform = KMPPlatformType.JVM,
                    encryptionClient = mockk<StytchEncryptionClient>(relaxed = true),
                    passkeyProvider = mockk(relaxed = true),
                    biometricsProvider = mockk(relaxed = true),
                    oAuthProvider = mockk(relaxed = true),
                ),
            dispatchers = dispatchers,
            sessionManager = sessionManager,
            apiOverride = mockApi,
        )

    @Test
    fun `updateSessionAndReturnExpiration returns memberSession expiresAt`() =
        runTest(testDispatcher) {
            val futureInstant = Clock.System.now() + 1.hours
            val mockSession = mockk<ApiB2bSessionV1MemberSession>(relaxed = true)
            every { mockSession.expiresAt } returns futureInstant
            val mockResponse = mockk<B2BSessionsAuthenticateResponse>(relaxed = true)
            every { mockResponse.memberSession } returns mockSession
            coEvery { mockApi.b2BSessionsAuthenticate(any()) } returns StytchDataResponse(mockResponse)

            val result = makeClient().updateSessionAndReturnExpiration()

            assertEquals(futureInstant, result)
        }
}
