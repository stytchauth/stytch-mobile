package com.stytch.sdk.consumer.totp

import com.stytch.sdk.StytchApi
import com.stytch.sdk.consumer.networking.ConsumerNetworkingClient
import com.stytch.sdk.consumer.networking.models.ITOTPsAuthenticateParameters
import com.stytch.sdk.consumer.networking.models.ITOTPsCreateParameters
import com.stytch.sdk.consumer.networking.models.ITOTPsRecoverParameters
import com.stytch.sdk.consumer.networking.models.TOTPsAuthenticateResponse
import com.stytch.sdk.consumer.networking.models.TOTPsCreateResponse
import com.stytch.sdk.consumer.networking.models.TOTPsGetRecoveryCodesResponse
import com.stytch.sdk.consumer.networking.models.TOTPsRecoverResponse
import com.stytch.sdk.consumer.networking.models.toNetworkModel
import com.stytch.sdk.data.StytchDispatchers
import com.stytch.sdk.data.StytchError
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException
import kotlin.js.JsExport

/** TOTP (time-based one-time passcode) authentication methods. */
@StytchApi
@JsExport
public interface TOTPClient {
    /**
     * Creates a new TOTP instance for the current user and returns the secret, QR code URL,
     * and recovery codes. Calls the `POST /sdk/v1/totps` endpoint. The user should scan the QR code
     * with their authenticator app, then call [authenticate] to verify setup.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchConsumer.totps.create(TOTPsCreateParameters())
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = TOTPsCreateParameters()
     * let response = try await StytchConsumer.totps.create(params)
     * ```
     *
     * **React Native:**
     * ```js
     * StytchConsumer.totps.create({})
     * ```
     *
     * @param request - [ITOTPsCreateParameters]
     *   - `expirationMinutes?` — Time before the TOTP setup expires if not verified, in minutes.
     *
     * @return [TOTPsCreateResponse] containing the TOTP `secret`, `qrCode` URL, and `recoveryCodes`.
     *
     * @throws [StytchError] if the request fails or the user already has an active TOTP instance.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun create(request: ITOTPsCreateParameters): TOTPsCreateResponse

    /**
     * Authenticates a time-based one-time passcode from the user's authenticator app.
     * Calls the `POST /sdk/v1/totps/authenticate` endpoint.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchConsumer.totps.authenticate(
     *     TOTPsAuthenticateParameters(totpCode = "123456", sessionDurationMinutes = 30)
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = TOTPsAuthenticateParameters(totpCode: "123456", sessionDurationMinutes: 30)
     * let response = try await StytchConsumer.totps.authenticate(params)
     * ```
     *
     * **React Native:**
     * ```js
     * StytchConsumer.totps.authenticate({ totpCode: "123456", sessionDurationMinutes: 30 })
     * ```
     *
     * @param request - [ITOTPsAuthenticateParameters]
     *   - `totpCode` — The 6-digit code from the user's authenticator app.
     *   - `sessionDurationMinutes` — Duration of the session to create, in minutes.
     *
     * @return [TOTPsAuthenticateResponse] containing the authenticated session and user.
     *
     * @throws [StytchError] if the code is invalid or expired.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun authenticate(request: ITOTPsAuthenticateParameters): TOTPsAuthenticateResponse

    /**
     * Authenticates the user with a TOTP recovery code instead of a time-based code.
     * Calls the `POST /sdk/v1/totps/recover` endpoint. Each recovery code can only be used once.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchConsumer.totps.recover(
     *     TOTPsRecoverParameters(recoveryCode = "recovery-code", sessionDurationMinutes = 30)
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = TOTPsRecoverParameters(recoveryCode: "recovery-code", sessionDurationMinutes: 30)
     * let response = try await StytchConsumer.totps.recover(params)
     * ```
     *
     * **React Native:**
     * ```js
     * StytchConsumer.totps.recover({ recoveryCode: "recovery-code", sessionDurationMinutes: 30 })
     * ```
     *
     * @param request - [ITOTPsRecoverParameters]
     *   - `recoveryCode` — A one-time-use recovery code issued during TOTP setup.
     *   - `sessionDurationMinutes` — Duration of the session to create, in minutes.
     *
     * @return [TOTPsRecoverResponse] containing the authenticated session and user.
     *
     * @throws [StytchError] if the recovery code is invalid or has already been used.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun recover(request: ITOTPsRecoverParameters): TOTPsRecoverResponse

    /**
     * Retrieves the recovery codes for the current user's TOTP instance.
     * Calls the `POST /sdk/v1/totps/recovery_codes` endpoint. Requires an active session.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchConsumer.totps.recoveryCodes()
     * ```
     *
     * **iOS:**
     * ```swift
     * let response = try await StytchConsumer.totps.recoveryCodes()
     * ```
     *
     * **React Native:**
     * ```js
     * StytchConsumer.totps.recoveryCodes()
     * ```
     *
     * @return [TOTPsGetRecoveryCodesResponse] containing the user's TOTP recovery codes.
     *
     * @throws [StytchError] if the request fails or no TOTP instance exists.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun recoveryCodes(): TOTPsGetRecoveryCodesResponse
}

internal class TOTPClientImpl(
    private val dispatchers: StytchDispatchers,
    private val networkingClient: ConsumerNetworkingClient,
) : TOTPClient {
    @Throws(StytchError::class, CancellationException::class)
    override suspend fun create(request: ITOTPsCreateParameters): TOTPsCreateResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.tOTPsCreate(request.toNetworkModel())
            }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun authenticate(request: ITOTPsAuthenticateParameters): TOTPsAuthenticateResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.tOTPsAuthenticate(request.toNetworkModel())
            }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun recover(request: ITOTPsRecoverParameters): TOTPsRecoverResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.tOTPsRecover(request.toNetworkModel())
            }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun recoveryCodes() =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.tOTPsGetRecoveryCodes()
            }
        }
}
