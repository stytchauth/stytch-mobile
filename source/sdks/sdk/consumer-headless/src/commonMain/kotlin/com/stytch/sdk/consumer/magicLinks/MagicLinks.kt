package com.stytch.sdk.consumer.magicLinks

import com.stytch.sdk.StytchApi
import com.stytch.sdk.consumer.StytchConsumerAuthenticationStateManager
import com.stytch.sdk.consumer.networking.ConsumerNetworkingClient
import com.stytch.sdk.consumer.networking.models.IMagicLinksAuthenticateParameters
import com.stytch.sdk.consumer.networking.models.IMagicLinksEmailLoginOrCreateParameters
import com.stytch.sdk.consumer.networking.models.IMagicLinksEmailSendSecondaryParameters
import com.stytch.sdk.consumer.networking.models.MagicLinksAuthenticateResponse
import com.stytch.sdk.consumer.networking.models.MagicLinksEmailLoginOrCreateResponse
import com.stytch.sdk.consumer.networking.models.MagicLinksEmailSendSecondaryResponse
import com.stytch.sdk.consumer.networking.models.toNetworkModel
import com.stytch.sdk.data.StytchDispatchers
import com.stytch.sdk.data.StytchError
import com.stytch.sdk.pkce.MissingPKCEException
import com.stytch.sdk.pkce.PKCEClient
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException
import kotlin.js.JsExport

/** Magic link authentication methods. */
@StytchApi
@JsExport
public interface MagicLinksClient {
    /** Email magic link methods. */
    public val email: EmailMagicLinksClient

    /**
     * Authenticates a magic link token received via deeplink, establishing a session for the user.
     * Calls the `POST /sdk/v1/magic_links/authenticate` endpoint. Retrieves the PKCE code verifier
     * stored during the corresponding [EmailMagicLinksClient.loginOrCreate] or [EmailMagicLinksClient.send] call.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchConsumer.magicLinks.authenticate(
     *     MagicLinksAuthenticateParameters(
     *         token = "token",
     *         sessionDurationMinutes = 30,
     *     )
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = MagicLinksAuthenticateParameters(token: "token", sessionDurationMinutes: 30)
     * let response = try await StytchConsumer.magicLinks.authenticate(params)
     * ```
     *
     * **React Native:**
     * ```js
     * StytchConsumer.magicLinks.authenticate({ token: "token", sessionDurationMinutes: 30 })
     * ```
     *
     * @param request - [IMagicLinksAuthenticateParameters]
     *   - `token` — The magic link token extracted from the deeplink URL.
     *   - `sessionDurationMinutes` — Duration of the session to create, in minutes.
     *
     * @return [MagicLinksAuthenticateResponse] containing the authenticated session and user.
     *
     * @throws [StytchError] if the token is invalid or expired, or if no PKCE verifier is found in storage.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun authenticate(request: IMagicLinksAuthenticateParameters): MagicLinksAuthenticateResponse
}

/** Email magic link methods. */
@StytchApi
@JsExport
public interface EmailMagicLinksClient {
    /**
     * Sends a magic link email to the provided address, logging in an existing user or creating a new one.
     * Calls the `POST /sdk/v1/magic_links/email/login_or_create` endpoint. Generates and stores a PKCE
     * code pair for use in the subsequent [MagicLinksClient.authenticate] call.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchConsumer.magicLinks.email.loginOrCreate(
     *     MagicLinksEmailLoginOrCreateParameters(
     *         email = "user@example.com",
     *     )
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = MagicLinksEmailLoginOrCreateParameters(email: "user@example.com")
     * let response = try await StytchConsumer.magicLinks.email.loginOrCreate(params)
     * ```
     *
     * **React Native:**
     * ```js
     * StytchConsumer.magicLinks.email.loginOrCreate({ email: "user@example.com" })
     * ```
     *
     * @param request - [IMagicLinksEmailLoginOrCreateParameters]
     *   - `email` — The email address to send the magic link to.
     *   - `loginMagicLinkUrl?` — The URL to redirect existing users to after clicking the link.
     *   - `signupMagicLinkUrl?` — The URL to redirect new users to after clicking the link.
     *   - `loginExpirationMinutes?` — Expiration for the login magic link, in minutes.
     *   - `signupExpirationMinutes?` — Expiration for the signup magic link, in minutes.
     *   - `loginTemplateId?` — Custom email template ID to use for login emails.
     *   - `signupTemplateId?` — Custom email template ID to use for signup emails.
     *   - `locale?` — Locale for the email content (e.g. `"en"`).
     *
     * @return [MagicLinksEmailLoginOrCreateResponse] confirming the email was sent.
     *
     * @throws [StytchError] if the request fails.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun loginOrCreate(request: IMagicLinksEmailLoginOrCreateParameters): MagicLinksEmailLoginOrCreateResponse

    /**
     * Sends a magic link email to an existing user's email address. Routes to
     * `POST /sdk/v1/magic_links/email/send/primary` if no session is active, or
     * `POST /sdk/v1/magic_links/email/send/secondary` if a session exists (to add an additional
     * auth factor). Generates and stores a PKCE code pair for use in the subsequent
     * [MagicLinksClient.authenticate] call.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchConsumer.magicLinks.email.send(
     *     MagicLinksEmailSendSecondaryParameters(
     *         email = "user@example.com",
     *     )
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = MagicLinksEmailSendSecondaryParameters(email: "user@example.com")
     * let response = try await StytchConsumer.magicLinks.email.send(params)
     * ```
     *
     * **React Native:**
     * ```js
     * StytchConsumer.magicLinks.email.send({ email: "user@example.com" })
     * ```
     *
     * @param request - [IMagicLinksEmailSendSecondaryParameters]
     *   - `email` — The email address to send the magic link to.
     *   - `loginMagicLinkUrl?` — The URL to redirect existing users to after clicking the link.
     *   - `signupMagicLinkUrl?` — The URL to redirect new users to after clicking the link.
     *   - `loginExpirationMinutes?` — Expiration for the login magic link, in minutes.
     *   - `signupExpirationMinutes?` — Expiration for the signup magic link, in minutes.
     *   - `loginTemplateId?` — Custom email template ID to use for login emails.
     *   - `signupTemplateId?` — Custom email template ID to use for signup emails.
     *   - `locale?` — Locale for the email content (e.g. `"en"`).
     *
     * @return [MagicLinksEmailSendSecondaryResponse] confirming the email was sent.
     *
     * @throws [StytchError] if the request fails.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun send(request: IMagicLinksEmailSendSecondaryParameters): MagicLinksEmailSendSecondaryResponse
}

internal class MagicLinksImpl(
    private val dispatchers: StytchDispatchers,
    private val networkingClient: ConsumerNetworkingClient,
    private val pkceClient: PKCEClient,
    private val sessionManager: StytchConsumerAuthenticationStateManager,
) : MagicLinksClient {
    override val email: EmailMagicLinksClient = EmailMagicLinksImpl(dispatchers, networkingClient, pkceClient, sessionManager)

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun authenticate(request: IMagicLinksAuthenticateParameters): MagicLinksAuthenticateResponse =
        withContext(dispatchers.ioDispatcher) {
            val codePair = pkceClient.retrieve() ?: throw MissingPKCEException()
            networkingClient.request {
                networkingClient.api.magicLinksAuthenticate(request.toNetworkModel(codeVerifier = codePair.verifier)).also {
                    pkceClient.revoke()
                }
            }
        }
}

internal class EmailMagicLinksImpl(
    private val dispatchers: StytchDispatchers,
    private val networkingClient: ConsumerNetworkingClient,
    private val pkceClient: PKCEClient,
    private val sessionManager: StytchConsumerAuthenticationStateManager,
) : EmailMagicLinksClient {
    @Throws(StytchError::class, CancellationException::class)
    override suspend fun loginOrCreate(request: IMagicLinksEmailLoginOrCreateParameters): MagicLinksEmailLoginOrCreateResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                val codePair = pkceClient.create()
                networkingClient.api.magicLinksEmailLoginOrCreate(request.toNetworkModel(codeChallenge = codePair.challenge))
            }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun send(request: IMagicLinksEmailSendSecondaryParameters): MagicLinksEmailSendSecondaryResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                val codePair = pkceClient.create()
                val parameters = request.toNetworkModel(codeChallenge = codePair.challenge)
                if (sessionManager.currentSessionToken.isNullOrEmpty()) {
                    networkingClient.api.magicLinksEmailSendPrimary(parameters)
                } else {
                    networkingClient.api.magicLinksEmailSendSecondary(parameters)
                }
            }
        }
}
