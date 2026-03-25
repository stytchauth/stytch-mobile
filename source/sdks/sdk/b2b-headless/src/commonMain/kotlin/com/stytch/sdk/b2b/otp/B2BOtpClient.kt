package com.stytch.sdk.b2b.otp

import com.stytch.sdk.StytchApi
import com.stytch.sdk.b2b.StytchB2BAuthenticationStateManager
import com.stytch.sdk.b2b.networking.B2BNetworkingClient
import com.stytch.sdk.b2b.networking.models.B2BOTPsEmailAuthenticateResponse
import com.stytch.sdk.b2b.networking.models.B2BOTPsEmailDiscoveryAuthenticateResponse
import com.stytch.sdk.b2b.networking.models.B2BOTPsEmailDiscoverySendResponse
import com.stytch.sdk.b2b.networking.models.B2BOTPsEmailLoginOrSignupResponse
import com.stytch.sdk.b2b.networking.models.B2BOTPsSMSAuthenticateResponse
import com.stytch.sdk.b2b.networking.models.B2BOTPsSMSSendResponse
import com.stytch.sdk.b2b.networking.models.IB2BOTPsEmailAuthenticateParameters
import com.stytch.sdk.b2b.networking.models.IB2BOTPsEmailDiscoveryAuthenticateParameters
import com.stytch.sdk.b2b.networking.models.IB2BOTPsEmailDiscoverySendParameters
import com.stytch.sdk.b2b.networking.models.IB2BOTPsEmailLoginOrSignupParameters
import com.stytch.sdk.b2b.networking.models.IB2BOTPsSMSAuthenticateParameters
import com.stytch.sdk.b2b.networking.models.IB2BOTPsSMSSendParameters
import com.stytch.sdk.b2b.networking.models.toNetworkModel
import com.stytch.sdk.data.StytchDispatchers
import com.stytch.sdk.data.StytchError
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException
import kotlin.js.JsExport

/** B2B OTP (one-time passcode) authentication via SMS and email. */
@StytchApi
@JsExport
public interface B2BOtpClient {
    /** SMS OTP methods. */
    public val sms: B2BSmsOtpClient

    /** Email OTP methods. */
    public val email: B2BEmailOtpClient
}

/** B2B SMS OTP methods. */
@StytchApi
@JsExport
public interface B2BSmsOtpClient {
    /**
     * Sends an SMS OTP to the member's phone number for MFA verification.
     * Calls the `POST /sdk/v1/b2b/otps/sms/send` endpoint. Automatically includes the intermediate
     * session token if one is present.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchB2B.otp.sms.send(
     *     B2BOTPsSMSSendParameters(
     *         organizationId = "org-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *         memberId = "member-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *     )
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = B2BOTPsSMSSendParameters(
     *     organizationId: "org-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *     memberId: "member-test-d5a3b680-e8a3-40c0-b815-ab79986666d0"
     * )
     * let response = try await StytchB2B.otp.sms.send(params)
     * ```
     *
     * **React Native:**
     * ```js
     * StytchB2B.otp.sms.send({
     *     organizationId: "org-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *     memberId: "member-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     * })
     * ```
     *
     * @param request - [IB2BOTPsSMSSendParameters]
     *   - `organizationId` — The ID of the organization.
     *   - `memberId` — The ID of the member to send the OTP to.
     *   - `mfaPhoneNumber?` — Phone number to send to; uses the member's existing number if omitted.
     *   - `locale?` — Locale for the SMS content.
     *   - `enableAutofill?` — Whether to enable OS-level SMS autofill on supported devices.
     *
     * @return [B2BOTPsSMSSendResponse] confirming the OTP was sent.
     *
     * @throws [StytchError] if the request fails.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun send(request: IB2BOTPsSMSSendParameters): B2BOTPsSMSSendResponse

    /**
     * Authenticates an SMS OTP code submitted by the member, completing the MFA step.
     * Calls the `POST /sdk/v1/b2b/otps/sms/authenticate` endpoint. Automatically includes the
     * intermediate session token if one is present.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchB2B.otp.sms.authenticate(
     *     B2BOTPsSMSAuthenticateParameters(
     *         organizationId = "org-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *         memberId = "member-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *         code = "123456",
     *         sessionDurationMinutes = 30,
     *     )
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = B2BOTPsSMSAuthenticateParameters(
     *     organizationId: "org-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *     memberId: "member-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *     code: "123456",
     *     sessionDurationMinutes: 30
     * )
     * let response = try await StytchB2B.otp.sms.authenticate(params)
     * ```
     *
     * **React Native:**
     * ```js
     * StytchB2B.otp.sms.authenticate({
     *     organizationId: "org-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *     memberId: "member-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *     code: "123456",
     *     sessionDurationMinutes: 30,
     * })
     * ```
     *
     * @param request - [IB2BOTPsSMSAuthenticateParameters]
     *   - `organizationId` — The ID of the organization.
     *   - `memberId` — The ID of the member authenticating.
     *   - `code` — The OTP code the member received via SMS.
     *   - `sessionDurationMinutes` — Duration of the session to create, in minutes.
     *   - `setMfaEnrollment?` — Whether to enroll or unenroll the member's MFA method (`"enroll"` or `"unenroll"`).
     *
     * @return [B2BOTPsSMSAuthenticateResponse] containing the authenticated member session.
     *
     * @throws [StytchError] if the code is invalid or expired.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun authenticate(request: IB2BOTPsSMSAuthenticateParameters): B2BOTPsSMSAuthenticateResponse
}

/** B2B email OTP methods. */
@StytchApi
@JsExport
public interface B2BEmailOtpClient {
    /** Email OTP discovery methods for listing organizations before a session is established. */
    public val discovery: B2BEmailOtpDiscoveryClient

    /**
     * Sends an email OTP to the provided address for login or signup within the organization.
     * Calls the `POST /sdk/v1/b2b/otps/email/login_or_signup` endpoint.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchB2B.otp.email.loginOrSignup(
     *     B2BOTPsEmailLoginOrSignupParameters(
     *         organizationId = "org-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *         emailAddress = "user@example.com",
     *     )
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = B2BOTPsEmailLoginOrSignupParameters(
     *     organizationId: "org-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *     emailAddress: "user@example.com"
     * )
     * let response = try await StytchB2B.otp.email.loginOrSignup(params)
     * ```
     *
     * **React Native:**
     * ```js
     * StytchB2B.otp.email.loginOrSignup({
     *     organizationId: "org-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *     emailAddress: "user@example.com",
     * })
     * ```
     *
     * @param request - [IB2BOTPsEmailLoginOrSignupParameters]
     *   - `organizationId` — The ID of the organization.
     *   - `emailAddress` — The email address to send the OTP to.
     *   - `loginTemplateId?` — Custom email template ID for the login OTP email.
     *   - `signupTemplateId?` — Custom email template ID for the signup OTP email.
     *   - `locale?` — Locale for the email content.
     *   - `loginExpirationMinutes?` — Expiration for the login OTP, in minutes.
     *   - `signupExpirationMinutes?` — Expiration for the signup OTP, in minutes.
     *
     * @return [B2BOTPsEmailLoginOrSignupResponse] confirming the OTP email was sent.
     *
     * @throws [StytchError] if the request fails.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun loginOrSignup(request: IB2BOTPsEmailLoginOrSignupParameters): B2BOTPsEmailLoginOrSignupResponse

    /**
     * Authenticates an email OTP code submitted by the member, completing the authentication step.
     * Calls the `POST /sdk/v1/b2b/otps/email/authenticate` endpoint. Automatically includes the
     * intermediate session token if one is present.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchB2B.otp.email.authenticate(
     *     B2BOTPsEmailAuthenticateParameters(
     *         organizationId = "org-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *         emailAddress = "user@example.com",
     *         code = "123456",
     *         sessionDurationMinutes = 30,
     *     )
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = B2BOTPsEmailAuthenticateParameters(
     *     organizationId: "org-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *     emailAddress: "user@example.com",
     *     code: "123456",
     *     sessionDurationMinutes: 30
     * )
     * let response = try await StytchB2B.otp.email.authenticate(params)
     * ```
     *
     * **React Native:**
     * ```js
     * StytchB2B.otp.email.authenticate({
     *     organizationId: "org-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *     emailAddress: "user@example.com",
     *     code: "123456",
     *     sessionDurationMinutes: 30,
     * })
     * ```
     *
     * @param request - [IB2BOTPsEmailAuthenticateParameters]
     *   - `organizationId` — The ID of the organization.
     *   - `emailAddress` — The email address that received the OTP.
     *   - `code` — The OTP code submitted by the member.
     *   - `sessionDurationMinutes` — Duration of the session to create, in minutes.
     *   - `locale?` — Locale for any follow-up communications.
     *
     * @return [B2BOTPsEmailAuthenticateResponse] containing the authenticated member session.
     *
     * @throws [StytchError] if the code is invalid or expired.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun authenticate(request: IB2BOTPsEmailAuthenticateParameters): B2BOTPsEmailAuthenticateResponse
}

/** Email OTP discovery methods for enumerating organizations before authentication. */
@StytchApi
@JsExport
public interface B2BEmailOtpDiscoveryClient {
    /**
     * Sends a discovery email OTP to enumerate the organizations associated with the given email
     * address. Calls the `POST /sdk/v1/b2b/otps/email/discovery/send` endpoint.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchB2B.otp.email.discovery.send(
     *     B2BOTPsEmailDiscoverySendParameters(emailAddress = "user@example.com")
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = B2BOTPsEmailDiscoverySendParameters(emailAddress: "user@example.com")
     * let response = try await StytchB2B.otp.email.discovery.send(params)
     * ```
     *
     * **React Native:**
     * ```js
     * StytchB2B.otp.email.discovery.send({ emailAddress: "user@example.com" })
     * ```
     *
     * @param request - [IB2BOTPsEmailDiscoverySendParameters]
     *   - `emailAddress` — The email address to send the discovery OTP to.
     *   - `loginTemplateId?` — Custom email template ID for the discovery email.
     *   - `locale?` — Locale for the email content.
     *   - `discoveryExpirationMinutes?` — Expiration time for the discovery OTP, in minutes.
     *
     * @return [B2BOTPsEmailDiscoverySendResponse] confirming the discovery OTP was sent.
     *
     * @throws [StytchError] if the request fails.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun send(request: IB2BOTPsEmailDiscoverySendParameters): B2BOTPsEmailDiscoverySendResponse

    /**
     * Authenticates the discovery email OTP, returning an intermediate session token and the list
     * of discovered organizations. Calls the `POST /sdk/v1/b2b/otps/email/discovery/authenticate`
     * endpoint.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchB2B.otp.email.discovery.authenticate(
     *     B2BOTPsEmailDiscoveryAuthenticateParameters(
     *         emailAddress = "user@example.com",
     *         code = "123456",
     *     )
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = B2BOTPsEmailDiscoveryAuthenticateParameters(
     *     emailAddress: "user@example.com",
     *     code: "123456"
     * )
     * let response = try await StytchB2B.otp.email.discovery.authenticate(params)
     * ```
     *
     * **React Native:**
     * ```js
     * StytchB2B.otp.email.discovery.authenticate({ emailAddress: "user@example.com", code: "123456" })
     * ```
     *
     * @param request - [IB2BOTPsEmailDiscoveryAuthenticateParameters]
     *   - `emailAddress` — The email address that received the discovery OTP.
     *   - `code` — The OTP code submitted by the user.
     *
     * @return [B2BOTPsEmailDiscoveryAuthenticateResponse] containing an intermediate session token
     *   and a list of discovered organizations.
     *
     * @throws [StytchError] if the code is invalid or expired.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun authenticate(request: IB2BOTPsEmailDiscoveryAuthenticateParameters): B2BOTPsEmailDiscoveryAuthenticateResponse
}

internal class B2BOtpClientImpl(
    private val dispatchers: StytchDispatchers,
    private val networkingClient: B2BNetworkingClient,
    private val sessionManager: StytchB2BAuthenticationStateManager,
) : B2BOtpClient {
    override val sms: B2BSmsOtpClient = B2BSmsOtpClientImpl(dispatchers, networkingClient, sessionManager)
    override val email: B2BEmailOtpClient = B2BEmailOtpClientImpl(dispatchers, networkingClient, sessionManager)
}

internal class B2BSmsOtpClientImpl(
    private val dispatchers: StytchDispatchers,
    private val networkingClient: B2BNetworkingClient,
    private val sessionManager: StytchB2BAuthenticationStateManager,
) : B2BSmsOtpClient {
    @Throws(StytchError::class, CancellationException::class)
    override suspend fun send(request: IB2BOTPsSMSSendParameters): B2BOTPsSMSSendResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.b2BOTPsSMSSend(
                    request.toNetworkModel(intermediateSessionToken = sessionManager.intermediateSessionToken),
                )
            }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun authenticate(request: IB2BOTPsSMSAuthenticateParameters): B2BOTPsSMSAuthenticateResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.b2BOTPsSMSAuthenticate(
                    request.toNetworkModel(intermediateSessionToken = sessionManager.intermediateSessionToken),
                )
            }
        }
}

internal class B2BEmailOtpClientImpl(
    private val dispatchers: StytchDispatchers,
    private val networkingClient: B2BNetworkingClient,
    private val sessionManager: StytchB2BAuthenticationStateManager,
) : B2BEmailOtpClient {
    override val discovery: B2BEmailOtpDiscoveryClient = B2BEmailOtpDiscoveryClientImpl(dispatchers, networkingClient)

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun loginOrSignup(request: IB2BOTPsEmailLoginOrSignupParameters): B2BOTPsEmailLoginOrSignupResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.b2BOTPsEmailLoginOrSignup(request.toNetworkModel())
            }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun authenticate(request: IB2BOTPsEmailAuthenticateParameters): B2BOTPsEmailAuthenticateResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.b2BOTPsEmailAuthenticate(
                    request.toNetworkModel(intermediateSessionToken = sessionManager.intermediateSessionToken),
                )
            }
        }
}

internal class B2BEmailOtpDiscoveryClientImpl(
    private val dispatchers: StytchDispatchers,
    private val networkingClient: B2BNetworkingClient,
) : B2BEmailOtpDiscoveryClient {
    @Throws(StytchError::class, CancellationException::class)
    override suspend fun send(request: IB2BOTPsEmailDiscoverySendParameters): B2BOTPsEmailDiscoverySendResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.b2BOTPsEmailDiscoverySend(request.toNetworkModel())
            }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun authenticate(request: IB2BOTPsEmailDiscoveryAuthenticateParameters): B2BOTPsEmailDiscoveryAuthenticateResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.b2BOTPsEmailDiscoveryAuthenticate(request.toNetworkModel())
            }
        }
}
