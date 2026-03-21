package com.stytch.sdk.b2b.totp

import com.stytch.sdk.StytchApi
import com.stytch.sdk.b2b.StytchB2BAuthenticationStateManager
import com.stytch.sdk.b2b.networking.B2BNetworkingClient
import com.stytch.sdk.b2b.networking.models.B2BTOTPsAuthenticateResponse
import com.stytch.sdk.b2b.networking.models.B2BTOTPsCreateResponse
import com.stytch.sdk.b2b.networking.models.IB2BTOTPsAuthenticateParameters
import com.stytch.sdk.b2b.networking.models.IB2BTOTPsCreateParameters
import com.stytch.sdk.b2b.networking.models.toNetworkModel
import com.stytch.sdk.data.StytchDispatchers
import com.stytch.sdk.data.StytchError
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException
import kotlin.js.JsExport

/** TOTP (time-based one-time passcode) MFA methods for B2B members. */
@StytchApi
@JsExport
public interface B2BTOTPClient {
    /**
     * Creates a new TOTP instance for the current member and returns the secret, QR code URL, and
     * recovery codes. Calls the `POST /sdk/v1/b2b/totp` endpoint. Automatically includes the
     * intermediate session token if one is present. The member should scan the QR code with their
     * authenticator app, then call [authenticate] to verify setup.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchB2B.totp.create(
     *     B2BTOTPsCreateParameters(
     *         organizationId = "org-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *         memberId = "member-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *     )
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = B2BTOTPsCreateParameters(
     *     organizationId: "org-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *     memberId: "member-test-d5a3b680-e8a3-40c0-b815-ab79986666d0"
     * )
     * let response = try await StytchB2B.totp.create(params)
     * ```
     *
     * **React Native:**
     * ```js
     * StytchB2B.totp.create({
     *     organizationId: "org-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *     memberId: "member-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     * })
     * ```
     *
     * @param request - [IB2BTOTPsCreateParameters]
     *   - `organizationId` — The ID of the organization.
     *   - `memberId` — The ID of the member to create the TOTP instance for.
     *   - `expirationMinutes?` — Time before the TOTP setup expires if not verified, in minutes.
     *
     * @return [B2BTOTPsCreateResponse] containing the TOTP `secret`, `qrCode` URL, and `recoveryCodes`.
     *
     * @throws [StytchError] if the request fails or the member already has an active TOTP instance.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun create(request: IB2BTOTPsCreateParameters): B2BTOTPsCreateResponse

    /**
     * Authenticates a TOTP code from the member's authenticator app, completing the MFA step.
     * Calls the `POST /sdk/v1/b2b/totp/authenticate` endpoint. Automatically includes the
     * intermediate session token if one is present.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchB2B.totp.authenticate(
     *     B2BTOTPsAuthenticateParameters(
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
     * let params = B2BTOTPsAuthenticateParameters(
     *     organizationId: "org-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *     memberId: "member-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *     code: "123456",
     *     sessionDurationMinutes: 30
     * )
     * let response = try await StytchB2B.totp.authenticate(params)
     * ```
     *
     * **React Native:**
     * ```js
     * StytchB2B.totp.authenticate({
     *     organizationId: "org-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *     memberId: "member-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *     code: "123456",
     *     sessionDurationMinutes: 30,
     * })
     * ```
     *
     * @param request - [IB2BTOTPsAuthenticateParameters]
     *   - `organizationId` — The ID of the organization.
     *   - `memberId` — The ID of the member authenticating.
     *   - `code` — The 6-digit code from the member's authenticator app.
     *   - `sessionDurationMinutes` — Duration of the session to create, in minutes.
     *   - `setMfaEnrollment?` — Whether to enroll or unenroll the member's MFA method (`"enroll"` or `"unenroll"`).
     *   - `setDefaultMfa?` — Whether to set TOTP as the member's default MFA method.
     *
     * @return [B2BTOTPsAuthenticateResponse] containing the authenticated member session.
     *
     * @throws [StytchError] if the code is invalid or expired.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun authenticate(request: IB2BTOTPsAuthenticateParameters): B2BTOTPsAuthenticateResponse
}

internal class B2BTOTPClientImpl(
    private val dispatchers: StytchDispatchers,
    private val networkingClient: B2BNetworkingClient,
    private val sessionManager: StytchB2BAuthenticationStateManager,
) : B2BTOTPClient {
    @Throws(StytchError::class, CancellationException::class)
    override suspend fun create(request: IB2BTOTPsCreateParameters): B2BTOTPsCreateResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.b2BTOTPsCreate(
                    request.toNetworkModel(intermediateSessionToken = sessionManager.intermediateSessionToken),
                )
            }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun authenticate(request: IB2BTOTPsAuthenticateParameters): B2BTOTPsAuthenticateResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.b2BTOTPsAuthenticate(
                    request.toNetworkModel(intermediateSessionToken = sessionManager.intermediateSessionToken),
                )
            }
        }
}
