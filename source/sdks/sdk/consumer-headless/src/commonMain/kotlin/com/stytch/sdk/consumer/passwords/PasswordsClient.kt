package com.stytch.sdk.consumer.passwords

import com.stytch.sdk.StytchApi
import com.stytch.sdk.consumer.networking.ConsumerNetworkingClient
import com.stytch.sdk.consumer.networking.models.IPasswordsAuthenticateParameters
import com.stytch.sdk.consumer.networking.models.IPasswordsCreateParameters
import com.stytch.sdk.consumer.networking.models.IPasswordsEmailResetParameters
import com.stytch.sdk.consumer.networking.models.IPasswordsEmailResetStartParameters
import com.stytch.sdk.consumer.networking.models.IPasswordsExistingPasswordResetParameters
import com.stytch.sdk.consumer.networking.models.IPasswordsSessionResetParameters
import com.stytch.sdk.consumer.networking.models.IPasswordsStrengthCheckParameters
import com.stytch.sdk.consumer.networking.models.PasswordsAuthenticateResponse
import com.stytch.sdk.consumer.networking.models.PasswordsCreateResponse
import com.stytch.sdk.consumer.networking.models.PasswordsEmailResetResponse
import com.stytch.sdk.consumer.networking.models.PasswordsEmailResetStartResponse
import com.stytch.sdk.consumer.networking.models.PasswordsExistingPasswordResetResponse
import com.stytch.sdk.consumer.networking.models.PasswordsSessionResetResponse
import com.stytch.sdk.consumer.networking.models.PasswordsStrengthCheckResponse
import com.stytch.sdk.consumer.networking.models.toNetworkModel
import com.stytch.sdk.data.StytchDispatchers
import com.stytch.sdk.data.StytchError
import com.stytch.sdk.pkce.MissingPKCEException
import com.stytch.sdk.pkce.PKCEClient
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException
import kotlin.js.JsExport

/** Password-based authentication methods. */
@StytchApi
@JsExport
public interface PasswordsClient {
    /**
     * Authenticates a user with their email address and password, establishing a session.
     * Calls the `POST /sdk/v1/passwords/authenticate` endpoint.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchConsumer.passwords.authenticate(
     *     PasswordsAuthenticateParameters(
     *         email = "user@example.com",
     *         password = "secretpassword",
     *         sessionDurationMinutes = 30,
     *     )
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = PasswordsAuthenticateParameters(
     *     email: "user@example.com",
     *     password: "secretpassword",
     *     sessionDurationMinutes: 30
     * )
     * let response = try await StytchConsumer.passwords.authenticate(params)
     * ```
     *
     * **React Native:**
     * ```js
     * StytchConsumer.passwords.authenticate({
     *     email: "user@example.com",
     *     password: "secretpassword",
     *     sessionDurationMinutes: 30,
     * })
     * ```
     *
     * @param request - [IPasswordsAuthenticateParameters]
     *   - `email` — The user's email address.
     *   - `password` — The user's password.
     *   - `sessionDurationMinutes` — Duration of the session to create, in minutes.
     *
     * @return [PasswordsAuthenticateResponse] containing the authenticated session and user.
     *
     * @throws [StytchError] if the credentials are invalid.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun authenticate(request: IPasswordsAuthenticateParameters): PasswordsAuthenticateResponse

    /**
     * Creates a new user with an email address and password, and establishes a session.
     * Calls the `POST /sdk/v1/passwords` endpoint.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchConsumer.passwords.create(
     *     PasswordsCreateParameters(
     *         email = "user@example.com",
     *         password = "secretpassword",
     *         sessionDurationMinutes = 30,
     *     )
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = PasswordsCreateParameters(
     *     email: "user@example.com",
     *     password: "secretpassword",
     *     sessionDurationMinutes: 30
     * )
     * let response = try await StytchConsumer.passwords.create(params)
     * ```
     *
     * **React Native:**
     * ```js
     * StytchConsumer.passwords.create({
     *     email: "user@example.com",
     *     password: "secretpassword",
     *     sessionDurationMinutes: 30,
     * })
     * ```
     *
     * @param request - [IPasswordsCreateParameters]
     *   - `email` — The email address for the new user.
     *   - `password` — The password for the new user.
     *   - `sessionDurationMinutes` — Duration of the session to create, in minutes.
     *
     * @return [PasswordsCreateResponse] containing the newly created user and their session.
     *
     * @throws [StytchError] if the email is already in use or the password does not meet strength requirements.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun create(request: IPasswordsCreateParameters): PasswordsCreateResponse

    /**
     * Initiates an email-based password reset by sending a reset link to the user's email address.
     * Calls the `POST /sdk/v1/passwords/email/reset/start` endpoint. Generates and stores a PKCE
     * code pair for use in the subsequent [resetByEmail] call.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchConsumer.passwords.resetByEmailStart(
     *     PasswordsEmailResetStartParameters(email = "user@example.com")
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = PasswordsEmailResetStartParameters(email: "user@example.com")
     * let response = try await StytchConsumer.passwords.resetByEmailStart(params)
     * ```
     *
     * **React Native:**
     * ```js
     * StytchConsumer.passwords.resetByEmailStart({ email: "user@example.com" })
     * ```
     *
     * @param request - [IPasswordsEmailResetStartParameters]
     *   - `email` — The email address of the user requesting a password reset.
     *   - `loginRedirectUrl?` — URL to redirect the user to if they click the link but already have a valid session.
     *   - `resetPasswordRedirectUrl?` — URL to redirect the user to for completing the password reset.
     *   - `resetPasswordExpirationMinutes?` — Expiration for the reset link, in minutes.
     *   - `resetPasswordTemplateId?` — Custom email template ID for the reset email.
     *
     * @return [PasswordsEmailResetStartResponse] confirming the reset email was sent.
     *
     * @throws [StytchError] if no user with the given email exists.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun resetByEmailStart(request: IPasswordsEmailResetStartParameters): PasswordsEmailResetStartResponse

    /**
     * Completes the email password reset flow by setting a new password using the token from the
     * reset link. Calls the `POST /sdk/v1/passwords/email/reset` endpoint. Retrieves the PKCE code
     * verifier stored during the corresponding [resetByEmailStart] call.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchConsumer.passwords.resetByEmail(
     *     PasswordsEmailResetParameters(
     *         token = "token",
     *         password = "newpassword",
     *         sessionDurationMinutes = 30,
     *     )
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = PasswordsEmailResetParameters(
     *     token: "token",
     *     password: "newpassword",
     *     sessionDurationMinutes: 30
     * )
     * let response = try await StytchConsumer.passwords.resetByEmail(params)
     * ```
     *
     * **React Native:**
     * ```js
     * StytchConsumer.passwords.resetByEmail({
     *     token: "token",
     *     password: "newpassword",
     *     sessionDurationMinutes: 30,
     * })
     * ```
     *
     * @param request - [IPasswordsEmailResetParameters]
     *   - `token` — The password reset token extracted from the deeplink URL.
     *   - `password` — The new password to set for the user.
     *   - `sessionDurationMinutes` — Duration of the session to create, in minutes.
     *
     * @return [PasswordsEmailResetResponse] containing the authenticated session and user.
     *
     * @throws [StytchError] if the token is invalid or expired, or if no PKCE verifier is found in storage.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun resetByEmail(request: IPasswordsEmailResetParameters): PasswordsEmailResetResponse

    /**
     * Resets a user's password by verifying their existing password, then setting a new one.
     * Calls the `POST /sdk/v1/passwords/existing_password/reset` endpoint.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchConsumer.passwords.resetByExistingPassword(
     *     PasswordsExistingPasswordResetParameters(
     *         email = "user@example.com",
     *         existingPassword = "oldpassword",
     *         newPassword = "newpassword",
     *     )
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = PasswordsExistingPasswordResetParameters(
     *     email: "user@example.com",
     *     existingPassword: "oldpassword",
     *     newPassword: "newpassword"
     * )
     * let response = try await StytchConsumer.passwords.resetByExistingPassword(params)
     * ```
     *
     * **React Native:**
     * ```js
     * StytchConsumer.passwords.resetByExistingPassword({
     *     email: "user@example.com",
     *     existingPassword: "oldpassword",
     *     newPassword: "newpassword",
     * })
     * ```
     *
     * @param request - [IPasswordsExistingPasswordResetParameters]
     *   - `email` — The user's email address.
     *   - `existingPassword` — The user's current password for verification.
     *   - `newPassword` — The new password to set.
     *   - `sessionDurationMinutes?` — Duration of the session to create, in minutes.
     *
     * @return [PasswordsExistingPasswordResetResponse] containing the authenticated session and user.
     *
     * @throws [StytchError] if the existing password is incorrect or the new password is too weak.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun resetByExistingPassword(request: IPasswordsExistingPasswordResetParameters): PasswordsExistingPasswordResetResponse

    /**
     * Resets a user's password using their active session for verification (no existing password required).
     * Calls the `POST /sdk/v1/passwords/session/reset` endpoint. Requires an active session.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchConsumer.passwords.resetBySession(
     *     PasswordsSessionResetParameters(password = "newpassword")
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = PasswordsSessionResetParameters(password: "newpassword")
     * let response = try await StytchConsumer.passwords.resetBySession(params)
     * ```
     *
     * **React Native:**
     * ```js
     * StytchConsumer.passwords.resetBySession({ password: "newpassword" })
     * ```
     *
     * @param request - [IPasswordsSessionResetParameters]
     *   - `password` — The new password to set for the currently authenticated user.
     *   - `sessionDurationMinutes?` — Duration of the session to create, in minutes.
     *
     * @return [PasswordsSessionResetResponse] containing the updated session and user.
     *
     * @throws [StytchError] if no active session exists or the password does not meet strength requirements.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun resetBySession(request: IPasswordsSessionResetParameters): PasswordsSessionResetResponse

    /**
     * Checks the strength of a password and returns a score with actionable feedback.
     * Calls the `POST /sdk/v1/passwords/strength_check` endpoint. Does not create a session.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchConsumer.passwords.strengthCheck(
     *     PasswordsStrengthCheckParameters(
     *         password = "mypassword",
     *         email = "user@example.com",
     *     )
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = PasswordsStrengthCheckParameters(password: "mypassword", email: "user@example.com")
     * let response = try await StytchConsumer.passwords.strengthCheck(params)
     * ```
     *
     * **React Native:**
     * ```js
     * StytchConsumer.passwords.strengthCheck({ password: "mypassword", email: "user@example.com" })
     * ```
     *
     * @param request - [IPasswordsStrengthCheckParameters]
     *   - `password` — The password to evaluate.
     *   - `email?` — The user's email address, used to improve strength scoring by checking against personal information.
     *
     * @return [PasswordsStrengthCheckResponse] containing a strength score, breach detection result, and feedback.
     *
     * @throws [StytchError] if the request fails.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun strengthCheck(request: IPasswordsStrengthCheckParameters): PasswordsStrengthCheckResponse
}

internal class PasswordsClientImpl(
    private val dispatchers: StytchDispatchers,
    private val networkingClient: ConsumerNetworkingClient,
    private val pkceClient: PKCEClient,
) : PasswordsClient {
    @Throws(StytchError::class, CancellationException::class)
    override suspend fun authenticate(request: IPasswordsAuthenticateParameters): PasswordsAuthenticateResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.passwordsAuthenticate(request.toNetworkModel())
            }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun create(request: IPasswordsCreateParameters): PasswordsCreateResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.passwordsCreate(request.toNetworkModel())
            }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun resetByEmailStart(request: IPasswordsEmailResetStartParameters): PasswordsEmailResetStartResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                val codePair = pkceClient.create()
                networkingClient.api.passwordsEmailResetStart(request.toNetworkModel(codeChallenge = codePair.challenge))
            }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun resetByEmail(request: IPasswordsEmailResetParameters): PasswordsEmailResetResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                val codePair = pkceClient.retrieve() ?: throw MissingPKCEException()
                networkingClient.api.passwordsEmailReset(request.toNetworkModel(codeVerifier = codePair.verifier))
            }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun resetByExistingPassword(
        request: IPasswordsExistingPasswordResetParameters,
    ): PasswordsExistingPasswordResetResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.passwordsExistingPasswordReset(request.toNetworkModel())
            }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun resetBySession(request: IPasswordsSessionResetParameters): PasswordsSessionResetResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.passwordsSessionReset(request.toNetworkModel())
            }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun strengthCheck(request: IPasswordsStrengthCheckParameters): PasswordsStrengthCheckResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.passwordsStrengthCheck(request.toNetworkModel())
            }
        }
}
