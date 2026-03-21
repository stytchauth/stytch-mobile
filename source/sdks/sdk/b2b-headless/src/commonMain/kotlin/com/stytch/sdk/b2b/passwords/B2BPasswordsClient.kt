package com.stytch.sdk.b2b.passwords

import com.stytch.sdk.StytchApi
import com.stytch.sdk.b2b.StytchB2BAuthenticationStateManager
import com.stytch.sdk.b2b.networking.B2BNetworkingClient
import com.stytch.sdk.b2b.networking.models.B2BPasswordAuthenticateResponse
import com.stytch.sdk.b2b.networking.models.B2BPasswordEmailResetResponse
import com.stytch.sdk.b2b.networking.models.B2BPasswordEmailResetStartResponse
import com.stytch.sdk.b2b.networking.models.B2BPasswordExistingPasswordResetResponse
import com.stytch.sdk.b2b.networking.models.B2BPasswordSessionResetResponse
import com.stytch.sdk.b2b.networking.models.B2BPasswordStrengthCheckResponse
import com.stytch.sdk.b2b.networking.models.IB2BPasswordAuthenticateParameters
import com.stytch.sdk.b2b.networking.models.IB2BPasswordEmailResetParameters
import com.stytch.sdk.b2b.networking.models.IB2BPasswordEmailResetStartParameters
import com.stytch.sdk.b2b.networking.models.IB2BPasswordExistingPasswordResetParameters
import com.stytch.sdk.b2b.networking.models.IB2BPasswordSessionResetParameters
import com.stytch.sdk.b2b.networking.models.IB2BPasswordStrengthCheckParameters
import com.stytch.sdk.b2b.networking.models.toNetworkModel
import com.stytch.sdk.data.StytchDispatchers
import com.stytch.sdk.data.StytchError
import com.stytch.sdk.pkce.MissingPKCEException
import com.stytch.sdk.pkce.PKCEClient
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException
import kotlin.js.JsExport

/** B2B password-based authentication methods. */
@StytchApi
@JsExport
public interface B2BPasswordsClient {
    /** Email password reset methods. */
    public val email: B2BPasswordsEmailClient

    /** Existing-password reset methods. */
    public val existingPassword: B2BPasswordsExistingPasswordClient

    /** Session-based password reset methods. */
    public val session: B2BPasswordsSessionClient

    /**
     * Authenticates a member with their email address and password, establishing a member session.
     * Calls the `POST /sdk/v1/b2b/passwords/authenticate` endpoint. Automatically includes the
     * intermediate session token if one is present.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchB2B.passwords.authenticate(
     *     B2BPasswordAuthenticateParameters(
     *         organizationId = "org-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *         emailAddress = "user@example.com",
     *         password = "secretpassword",
     *         sessionDurationMinutes = 30,
     *     )
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = B2BPasswordAuthenticateParameters(
     *     organizationId: "org-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *     emailAddress: "user@example.com",
     *     password: "secretpassword",
     *     sessionDurationMinutes: 30
     * )
     * let response = try await StytchB2B.passwords.authenticate(params)
     * ```
     *
     * **React Native:**
     * ```js
     * StytchB2B.passwords.authenticate({
     *     organizationId: "org-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *     emailAddress: "user@example.com",
     *     password: "secretpassword",
     *     sessionDurationMinutes: 30,
     * })
     * ```
     *
     * @param request - [IB2BPasswordAuthenticateParameters]
     *   - `organizationId` — The ID of the organization the member belongs to.
     *   - `emailAddress` — The member's email address.
     *   - `password` — The member's password.
     *   - `sessionDurationMinutes` — Duration of the session to create, in minutes.
     *   - `locale?` — Locale for any follow-up communications.
     *
     * @return [B2BPasswordAuthenticateResponse] containing the authenticated member session.
     *
     * @throws [StytchError] if the credentials are invalid.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun authenticate(request: IB2BPasswordAuthenticateParameters): B2BPasswordAuthenticateResponse

    /**
     * Checks the strength of a password and returns a score with actionable feedback.
     * Calls the `POST /sdk/v1/b2b/passwords/strength_check` endpoint. Does not require a session.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchB2B.passwords.strengthCheck(
     *     B2BPasswordStrengthCheckParameters(
     *         password = "mypassword",
     *         emailAddress = "user@example.com",
     *     )
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = B2BPasswordStrengthCheckParameters(password: "mypassword", emailAddress: "user@example.com")
     * let response = try await StytchB2B.passwords.strengthCheck(params)
     * ```
     *
     * **React Native:**
     * ```js
     * StytchB2B.passwords.strengthCheck({ password: "mypassword", emailAddress: "user@example.com" })
     * ```
     *
     * @param request - [IB2BPasswordStrengthCheckParameters]
     *   - `password` — The password to evaluate.
     *   - `emailAddress?` — The member's email address, used to improve scoring by checking against personal information.
     *
     * @return [B2BPasswordStrengthCheckResponse] containing a strength score, breach detection result, and feedback.
     *
     * @throws [StytchError] if the request fails.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun strengthCheck(request: IB2BPasswordStrengthCheckParameters): B2BPasswordStrengthCheckResponse
}

/** Email-based password reset methods. */
@StytchApi
@JsExport
public interface B2BPasswordsEmailClient {
    /**
     * Initiates an email-based password reset by sending a reset link to the member's email address.
     * Calls the `POST /sdk/v1/b2b/passwords/email/reset/start` endpoint. Generates and stores a
     * PKCE code pair for use in the subsequent [reset] call.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchB2B.passwords.email.resetStart(
     *     B2BPasswordEmailResetStartParameters(
     *         organizationId = "org-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *         emailAddress = "user@example.com",
     *     )
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = B2BPasswordEmailResetStartParameters(
     *     organizationId: "org-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *     emailAddress: "user@example.com"
     * )
     * let response = try await StytchB2B.passwords.email.resetStart(params)
     * ```
     *
     * **React Native:**
     * ```js
     * StytchB2B.passwords.email.resetStart({
     *     organizationId: "org-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *     emailAddress: "user@example.com",
     * })
     * ```
     *
     * @param request - [IB2BPasswordEmailResetStartParameters]
     *   - `organizationId` — The ID of the organization the member belongs to.
     *   - `emailAddress` — The email address of the member requesting a password reset.
     *   - `loginRedirectUrl?` — URL to redirect to if the member clicks the link but already has a valid session.
     *   - `resetPasswordRedirectUrl?` — URL to redirect to for completing the password reset.
     *   - `resetPasswordExpirationMinutes?` — Expiration for the reset link, in minutes.
     *   - `resetPasswordTemplateId?` — Custom email template ID for the reset email.
     *   - `locale?` — Locale for the email content.
     *   - `verifyEmailTemplateId?` — Custom email template ID for an email verification step, if required.
     *
     * @return [B2BPasswordEmailResetStartResponse] confirming the reset email was sent.
     *
     * @throws [StytchError] if no member with the given email exists in the organization.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun resetStart(request: IB2BPasswordEmailResetStartParameters): B2BPasswordEmailResetStartResponse

    /**
     * Completes the email password reset flow by setting a new password using the token from the
     * reset link. Calls the `POST /sdk/v1/b2b/passwords/email/reset` endpoint. Retrieves the PKCE
     * code verifier stored during the corresponding [resetStart] call, and automatically includes
     * the intermediate session token if one is present.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchB2B.passwords.email.reset(
     *     B2BPasswordEmailResetParameters(
     *         passwordResetToken = "token",
     *         password = "newpassword",
     *         sessionDurationMinutes = 30,
     *     )
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = B2BPasswordEmailResetParameters(
     *     passwordResetToken: "token",
     *     password: "newpassword",
     *     sessionDurationMinutes: 30
     * )
     * let response = try await StytchB2B.passwords.email.reset(params)
     * ```
     *
     * **React Native:**
     * ```js
     * StytchB2B.passwords.email.reset({
     *     passwordResetToken: "token",
     *     password: "newpassword",
     *     sessionDurationMinutes: 30,
     * })
     * ```
     *
     * @param request - [IB2BPasswordEmailResetParameters]
     *   - `passwordResetToken` — The password reset token extracted from the deeplink URL.
     *   - `password` — The new password to set for the member.
     *   - `sessionDurationMinutes` — Duration of the session to create, in minutes.
     *   - `locale?` — Locale for any follow-up communications.
     *
     * @return [B2BPasswordEmailResetResponse] containing the authenticated member session.
     *
     * @throws [StytchError] if the token is invalid or expired, or if no PKCE verifier is found in storage.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun reset(request: IB2BPasswordEmailResetParameters): B2BPasswordEmailResetResponse
}

/** Existing-password reset — updates the password using the current password for verification. */
@StytchApi
@JsExport
public interface B2BPasswordsExistingPasswordClient {
    /**
     * Resets a member's password by verifying their existing password, then setting a new one.
     * Calls the `POST /sdk/v1/b2b/passwords/existing_password/reset` endpoint.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchB2B.passwords.existingPassword.reset(
     *     B2BPasswordExistingPasswordResetParameters(
     *         organizationId = "org-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *         emailAddress = "user@example.com",
     *         existingPassword = "oldpassword",
     *         newPassword = "newpassword",
     *         sessionDurationMinutes = 30,
     *     )
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = B2BPasswordExistingPasswordResetParameters(
     *     organizationId: "org-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *     emailAddress: "user@example.com",
     *     existingPassword: "oldpassword",
     *     newPassword: "newpassword",
     *     sessionDurationMinutes: 30
     * )
     * let response = try await StytchB2B.passwords.existingPassword.reset(params)
     * ```
     *
     * **React Native:**
     * ```js
     * StytchB2B.passwords.existingPassword.reset({
     *     organizationId: "org-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *     emailAddress: "user@example.com",
     *     existingPassword: "oldpassword",
     *     newPassword: "newpassword",
     *     sessionDurationMinutes: 30,
     * })
     * ```
     *
     * @param request - [IB2BPasswordExistingPasswordResetParameters]
     *   - `organizationId` — The ID of the organization.
     *   - `emailAddress` — The member's email address.
     *   - `existingPassword` — The member's current password for verification.
     *   - `newPassword` — The new password to set.
     *   - `sessionDurationMinutes` — Duration of the session to create, in minutes.
     *   - `locale?` — Locale for any follow-up communications.
     *
     * @return [B2BPasswordExistingPasswordResetResponse] containing the authenticated member session.
     *
     * @throws [StytchError] if the existing password is incorrect or the new password is too weak.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun reset(request: IB2BPasswordExistingPasswordResetParameters): B2BPasswordExistingPasswordResetResponse
}

/** Session-based password reset — updates the password using an active session for verification. */
@StytchApi
@JsExport
public interface B2BPasswordsSessionClient {
    /**
     * Resets a member's password using their active session for verification (no existing password
     * required). Calls the `POST /sdk/v1/b2b/passwords/session/reset` endpoint. Requires an active
     * session.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchB2B.passwords.session.reset(
     *     B2BPasswordSessionResetParameters(password = "newpassword")
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = B2BPasswordSessionResetParameters(password: "newpassword")
     * let response = try await StytchB2B.passwords.session.reset(params)
     * ```
     *
     * **React Native:**
     * ```js
     * StytchB2B.passwords.session.reset({ password: "newpassword" })
     * ```
     *
     * @param request - [IB2BPasswordSessionResetParameters]
     *   - `password` — The new password to set for the currently authenticated member.
     *
     * @return [B2BPasswordSessionResetResponse] containing the updated member session.
     *
     * @throws [StytchError] if no active session exists or the password does not meet strength requirements.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun reset(request: IB2BPasswordSessionResetParameters): B2BPasswordSessionResetResponse
}

internal class B2BPasswordsClientImpl(
    private val dispatchers: StytchDispatchers,
    private val networkingClient: B2BNetworkingClient,
    private val pkceClient: PKCEClient,
    private val sessionManager: StytchB2BAuthenticationStateManager,
) : B2BPasswordsClient {
    override val email: B2BPasswordsEmailClient =
        B2BPasswordsEmailClientImpl(dispatchers, networkingClient, pkceClient, sessionManager)
    override val existingPassword: B2BPasswordsExistingPasswordClient =
        B2BPasswordsExistingPasswordClientImpl(dispatchers, networkingClient)
    override val session: B2BPasswordsSessionClient =
        B2BPasswordsSessionClientImpl(dispatchers, networkingClient)

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun authenticate(request: IB2BPasswordAuthenticateParameters): B2BPasswordAuthenticateResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.b2BPasswordAuthenticate(
                    request.toNetworkModel(intermediateSessionToken = sessionManager.intermediateSessionToken),
                )
            }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun strengthCheck(request: IB2BPasswordStrengthCheckParameters): B2BPasswordStrengthCheckResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.b2BPasswordStrengthCheck(request.toNetworkModel())
            }
        }
}

internal class B2BPasswordsEmailClientImpl(
    private val dispatchers: StytchDispatchers,
    private val networkingClient: B2BNetworkingClient,
    private val pkceClient: PKCEClient,
    private val sessionManager: StytchB2BAuthenticationStateManager,
) : B2BPasswordsEmailClient {
    @Throws(StytchError::class, CancellationException::class)
    override suspend fun resetStart(request: IB2BPasswordEmailResetStartParameters): B2BPasswordEmailResetStartResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                val codePair = pkceClient.create()
                networkingClient.api.b2BPasswordEmailResetStart(request.toNetworkModel(codeChallenge = codePair.challenge))
            }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun reset(request: IB2BPasswordEmailResetParameters): B2BPasswordEmailResetResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                val codePair = pkceClient.retrieve() ?: throw MissingPKCEException()
                networkingClient.api
                    .b2BPasswordEmailReset(
                        request.toNetworkModel(
                            codeVerifier = codePair.verifier,
                            intermediateSessionToken = sessionManager.intermediateSessionToken,
                        ),
                    ).also { pkceClient.revoke() }
            }
        }
}

internal class B2BPasswordsExistingPasswordClientImpl(
    private val dispatchers: StytchDispatchers,
    private val networkingClient: B2BNetworkingClient,
) : B2BPasswordsExistingPasswordClient {
    @Throws(StytchError::class, CancellationException::class)
    override suspend fun reset(request: IB2BPasswordExistingPasswordResetParameters): B2BPasswordExistingPasswordResetResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.b2BPasswordExistingPasswordReset(request.toNetworkModel())
            }
        }
}

internal class B2BPasswordsSessionClientImpl(
    private val dispatchers: StytchDispatchers,
    private val networkingClient: B2BNetworkingClient,
) : B2BPasswordsSessionClient {
    @Throws(StytchError::class, CancellationException::class)
    override suspend fun reset(request: IB2BPasswordSessionResetParameters): B2BPasswordSessionResetResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.b2BPasswordSessionReset(request.toNetworkModel())
            }
        }
}
