package com.stytch.sdk.consumer.user

import com.stytch.sdk.StytchApi
import com.stytch.sdk.consumer.networking.ConsumerNetworkingClient
import com.stytch.sdk.consumer.networking.models.ApiUserV1User
import com.stytch.sdk.consumer.networking.models.DeleteBiometricRegistrationResponse
import com.stytch.sdk.consumer.networking.models.DeleteCryptoWalletResponse
import com.stytch.sdk.consumer.networking.models.DeleteEmailResponse
import com.stytch.sdk.consumer.networking.models.DeleteOAuthUserRegistrationResponse
import com.stytch.sdk.consumer.networking.models.DeletePhoneNumberResponse
import com.stytch.sdk.consumer.networking.models.DeleteTOTPResponse
import com.stytch.sdk.consumer.networking.models.DeleteWebAuthnRegistrationResponse
import com.stytch.sdk.consumer.networking.models.GetMeResponse
import com.stytch.sdk.consumer.networking.models.IUpdateMeParameters
import com.stytch.sdk.consumer.networking.models.UpdateMeResponse
import com.stytch.sdk.consumer.networking.models.toNetworkModel
import com.stytch.sdk.data.BasicResponse
import com.stytch.sdk.data.StytchDataResponse
import com.stytch.sdk.data.StytchDispatchers
import com.stytch.sdk.data.StytchError
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException
import kotlin.js.JsExport

/** User account management methods. */
@StytchApi
@JsExport
public interface UserClient {
    /**
     * Fetches the current user's full profile from the Stytch backend.
     * Calls the `GET /sdk/v1/users/me` endpoint. Requires an active session.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchConsumer.user.getUser()
     * ```
     *
     * **iOS:**
     * ```swift
     * let response = try await StytchConsumer.user.getUser()
     * ```
     *
     * **React Native:**
     * ```js
     * StytchConsumer.user.getUser()
     * ```
     *
     * @return [GetMeResponse] containing the full user object with all registered authentication factors.
     *
     * @throws [StytchError] if the request fails or no active session exists.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun getUser(): GetMeResponse

    /**
     * Deletes an authentication factor from the current user's account. The endpoint called depends
     * on the type of the [AuthenticationFactor] passed:
     * - [AuthenticationFactor.TOTP] → `DELETE /sdk/v1/users/totps/{totp_id}`
     * - [AuthenticationFactor.Email] → `DELETE /sdk/v1/users/emails/{email_id}`
     * - [AuthenticationFactor.OAuth] → `DELETE /sdk/v1/users/oauth/{oauth_user_registration_id}`
     * - [AuthenticationFactor.WebAuthn] → `DELETE /sdk/v1/users/webauthn_registrations/{webauthn_registration_id}`
     * - [AuthenticationFactor.Biometric] → `DELETE /sdk/v1/users/biometric_registrations/{biometric_registration_id}`
     * - [AuthenticationFactor.CryptoWallet] → `DELETE /sdk/v1/users/crypto_wallets/{crypto_wallet_id}`
     * - [AuthenticationFactor.PhoneNumber] → `DELETE /sdk/v1/users/phone_numbers/{phone_id}`
     *
     * **Kotlin:**
     * ```kotlin
     * StytchConsumer.user.deleteFactor(
     *     AuthenticationFactor.Email(factorId = "email-test-d5a3b680-e8a3-40c0-b815-ab79986666d0")
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let factor = AuthenticationFactor.Email(factorId: "email-test-d5a3b680-e8a3-40c0-b815-ab79986666d0")
     * let response = try await StytchConsumer.user.deleteFactor(factor)
     * ```
     *
     * **React Native:**
     * ```js
     * StytchConsumer.user.deleteFactor({ type: "email", factorId: "email-test-d5a3b680-e8a3-40c0-b815-ab79986666d0" })
     * ```
     *
     * @param factor - [AuthenticationFactor] sealed class identifying the factor to delete. The `factorId`
     *   is the unique ID of the specific factor registration (e.g. the email ID or phone number ID).
     *
     * @return [DeleteFactorResponse] containing the updated user object after the factor was removed.
     *
     * @throws [StytchError] if the factor does not exist or the request fails.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun deleteFactor(factor: AuthenticationFactor): DeleteFactorResponse

    /**
     * Updates the current user's profile. Calls the `PUT /sdk/v1/users/me` endpoint.
     * Requires an active session.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchConsumer.user.update(
     *     UpdateMeParameters(
     *         name = ApiUserV1Name(firstName = "Jane", lastName = "Doe"),
     *     )
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let name = ApiUserV1Name(firstName: "Jane", lastName: "Doe")
     * let params = UpdateMeParameters(name: name)
     * let response = try await StytchConsumer.user.update(params)
     * ```
     *
     * **React Native:**
     * ```js
     * StytchConsumer.user.update({ name: { firstName: "Jane", lastName: "Doe" } })
     * ```
     *
     * @param request - [IUpdateMeParameters]
     *   - `name?` — Updated name fields (`firstName`, `middleName`, `lastName`).
     *   - `emails?` — List of email addresses to set on the user.
     *   - `phoneNumbers?` — List of phone numbers to set on the user.
     *   - `cryptoWallets?` — List of crypto wallet addresses to set on the user.
     *   - `trustedMetadata?` — Key-value metadata set by your backend (requires server-side auth to modify).
     *   - `untrustedMetadata?` — Key-value metadata that can be set by the client.
     *
     * @return [UpdateMeResponse] containing the updated user object.
     *
     * @throws [StytchError] if the request fails or no active session exists.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun update(request: IUpdateMeParameters): UpdateMeResponse
}

/** A user authentication factor that can be deleted from the user's account. */
@JsExport
public sealed class AuthenticationFactor(
    /** The unique ID of the factor. */
    public open val factorId: String,
) {
    /** A TOTP authenticator factor. */
    public data class TOTP(
        override val factorId: String,
    ) : AuthenticationFactor(factorId)

    /** An email address factor. */
    public data class Email(
        override val factorId: String,
    ) : AuthenticationFactor(factorId)

    /** An OAuth provider registration factor. */
    public data class OAuth(
        override val factorId: String,
    ) : AuthenticationFactor(factorId)

    /** A WebAuthn (passkey) factor. */
    public data class WebAuthn(
        override val factorId: String,
    ) : AuthenticationFactor(factorId)

    /** A biometric factor. */
    public data class Biometric(
        override val factorId: String,
    ) : AuthenticationFactor(factorId)

    /** A crypto wallet factor. */
    public data class CryptoWallet(
        override val factorId: String,
    ) : AuthenticationFactor(factorId)

    /** A phone number factor. */
    public data class PhoneNumber(
        override val factorId: String,
    ) : AuthenticationFactor(factorId)
}

/** The response returned after successfully deleting an authentication factor. */
@JsExport
public data class DeleteFactorResponse(
    override val requestId: String,
    override val statusCode: Int,
    /** The updated user object after the factor was removed. */
    val user: ApiUserV1User,
) : BasicResponse

internal class UserClientImpl(
    private val dispatchers: StytchDispatchers,
    private val networkingClient: ConsumerNetworkingClient,
) : UserClient {
    @Throws(StytchError::class, CancellationException::class)
    override suspend fun getUser(): GetMeResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.getMe()
            }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun deleteFactor(factor: AuthenticationFactor) =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                StytchDataResponse(
                    when (factor) {
                        is AuthenticationFactor.TOTP -> {
                            networkingClient.api
                                .deleteTOTP(factor.factorId)
                                .data
                                .toDeleteFactorResponse()
                        }

                        is AuthenticationFactor.Biometric -> {
                            networkingClient.api
                                .deleteBiometricRegistration(factor.factorId)
                                .data
                                .toDeleteFactorResponse()
                        }

                        is AuthenticationFactor.CryptoWallet -> {
                            networkingClient.api
                                .deleteCryptoWallet(factor.factorId)
                                .data
                                .toDeleteFactorResponse()
                        }

                        is AuthenticationFactor.Email -> {
                            networkingClient.api
                                .deleteEmail(factor.factorId)
                                .data
                                .toDeleteFactorResponse()
                        }

                        is AuthenticationFactor.OAuth -> {
                            networkingClient.api
                                .deleteOAuthUserRegistration(factor.factorId)
                                .data
                                .toDeleteFactorResponse()
                        }

                        is AuthenticationFactor.PhoneNumber -> {
                            networkingClient.api
                                .deletePhoneNumber(factor.factorId)
                                .data
                                .toDeleteFactorResponse()
                        }

                        is AuthenticationFactor.WebAuthn -> {
                            networkingClient.api
                                .deleteWebAuthnRegistration(factor.factorId)
                                .data
                                .toDeleteFactorResponse()
                        }
                    },
                )
            }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun update(request: IUpdateMeParameters): UpdateMeResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.updateMe(request.toNetworkModel())
            }
        }
}

private fun DeleteTOTPResponse.toDeleteFactorResponse() =
    DeleteFactorResponse(
        requestId = requestId,
        statusCode = statusCode,
        user = user,
    )

private fun DeleteBiometricRegistrationResponse.toDeleteFactorResponse() =
    DeleteFactorResponse(
        requestId = requestId,
        statusCode = statusCode,
        user = user,
    )

private fun DeleteCryptoWalletResponse.toDeleteFactorResponse() =
    DeleteFactorResponse(
        requestId = requestId,
        statusCode = statusCode,
        user = user,
    )

private fun DeleteEmailResponse.toDeleteFactorResponse() =
    DeleteFactorResponse(
        requestId = requestId,
        statusCode = statusCode,
        user = user,
    )

private fun DeleteOAuthUserRegistrationResponse.toDeleteFactorResponse() =
    DeleteFactorResponse(
        requestId = requestId,
        statusCode = statusCode,
        user = user,
    )

private fun DeletePhoneNumberResponse.toDeleteFactorResponse() =
    DeleteFactorResponse(
        requestId = requestId,
        statusCode = statusCode,
        user = user,
    )

private fun DeleteWebAuthnRegistrationResponse.toDeleteFactorResponse() =
    DeleteFactorResponse(
        requestId = requestId,
        statusCode = statusCode,
        user = user,
    )
