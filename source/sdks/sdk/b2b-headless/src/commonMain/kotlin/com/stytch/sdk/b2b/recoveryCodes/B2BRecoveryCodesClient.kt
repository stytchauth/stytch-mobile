package com.stytch.sdk.b2b.recoveryCodes

import com.stytch.sdk.StytchApi
import com.stytch.sdk.b2b.StytchB2BAuthenticationStateManager
import com.stytch.sdk.b2b.networking.B2BNetworkingClient
import com.stytch.sdk.b2b.networking.models.B2BRecoveryCodesGetResponse
import com.stytch.sdk.b2b.networking.models.B2BRecoveryCodesRecoverResponse
import com.stytch.sdk.b2b.networking.models.B2BRecoveryCodesRotateResponse
import com.stytch.sdk.b2b.networking.models.IB2BRecoveryCodesRecoverParameters
import com.stytch.sdk.b2b.networking.models.IB2BRecoveryCodesRotateParameters
import com.stytch.sdk.b2b.networking.models.toNetworkModel
import com.stytch.sdk.data.StytchDispatchers
import com.stytch.sdk.data.StytchError
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException
import kotlin.js.JsExport

/** Recovery code management methods for the current member. */
@StytchApi
@JsExport
public interface B2BRecoveryCodesClient {
    /**
     * Retrieves the recovery codes for the current member's account.
     * Calls the `GET /sdk/v1/b2b/recovery_codes` endpoint. Requires an active session.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchB2B.recoveryCodes.get()
     * ```
     *
     * **iOS:**
     * ```swift
     * let response = try await StytchB2B.recoveryCodes.get()
     * ```
     *
     * **React Native:**
     * ```js
     * StytchB2B.recoveryCodes.get()
     * ```
     *
     * @return [B2BRecoveryCodesGetResponse] containing the member's current recovery codes.
     *
     * @throws [StytchError] if the request fails or no active session exists.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun get(): B2BRecoveryCodesGetResponse

    /**
     * Authenticates the member using a recovery code as an MFA fallback, establishing a member
     * session. Calls the `POST /sdk/v1/b2b/recovery_codes/recover` endpoint. Automatically includes
     * the intermediate session token if one is present. Each recovery code can only be used once.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchB2B.recoveryCodes.recover(
     *     B2BRecoveryCodesRecoverParameters(
     *         organizationId = "org-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *         memberId = "member-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *         recoveryCode = "recovery-code",
     *         sessionDurationMinutes = 30,
     *     )
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = B2BRecoveryCodesRecoverParameters(
     *     organizationId: "org-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *     memberId: "member-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *     recoveryCode: "recovery-code",
     *     sessionDurationMinutes: 30
     * )
     * let response = try await StytchB2B.recoveryCodes.recover(params)
     * ```
     *
     * **React Native:**
     * ```js
     * StytchB2B.recoveryCodes.recover({
     *     organizationId: "org-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *     memberId: "member-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *     recoveryCode: "recovery-code",
     *     sessionDurationMinutes: 30,
     * })
     * ```
     *
     * @param request - [IB2BRecoveryCodesRecoverParameters]
     *   - `organizationId` — The ID of the organization.
     *   - `memberId` — The ID of the member using the recovery code.
     *   - `recoveryCode` — A one-time-use recovery code issued during MFA enrollment.
     *   - `sessionDurationMinutes` — Duration of the session to create, in minutes.
     *
     * @return [B2BRecoveryCodesRecoverResponse] containing the authenticated member session.
     *
     * @throws [StytchError] if the recovery code is invalid or has already been used.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun recover(request: IB2BRecoveryCodesRecoverParameters): B2BRecoveryCodesRecoverResponse

    /**
     * Rotates the recovery codes for the current member's account, invalidating the previous set
     * and generating a new set. Calls the `POST /sdk/v1/b2b/recovery_codes/rotate` endpoint.
     * Requires an active session.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchB2B.recoveryCodes.rotate(B2BRecoveryCodesRotateParameters())
     * ```
     *
     * **iOS:**
     * ```swift
     * let response = try await StytchB2B.recoveryCodes.rotate(B2BRecoveryCodesRotateParameters())
     * ```
     *
     * **React Native:**
     * ```js
     * StytchB2B.recoveryCodes.rotate({})
     * ```
     *
     * @param request - [IB2BRecoveryCodesRotateParameters] — no fields required.
     *
     * @return [B2BRecoveryCodesRotateResponse] containing the newly generated recovery codes.
     *
     * @throws [StytchError] if the request fails.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun rotate(request: IB2BRecoveryCodesRotateParameters): B2BRecoveryCodesRotateResponse
}

internal class B2BRecoveryCodesClientImpl(
    private val dispatchers: StytchDispatchers,
    private val networkingClient: B2BNetworkingClient,
    private val sessionManager: StytchB2BAuthenticationStateManager,
) : B2BRecoveryCodesClient {
    @Throws(StytchError::class, CancellationException::class)
    override suspend fun get(): B2BRecoveryCodesGetResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request { networkingClient.api.b2BRecoveryCodesGet() }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun recover(request: IB2BRecoveryCodesRecoverParameters): B2BRecoveryCodesRecoverResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.b2BRecoveryCodesRecover(
                    request.toNetworkModel(intermediateSessionToken = sessionManager.intermediateSessionToken),
                )
            }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun rotate(request: IB2BRecoveryCodesRotateParameters): B2BRecoveryCodesRotateResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request { networkingClient.api.b2BRecoveryCodesRotate(request.toNetworkModel()) }
        }
}
