package com.stytch.sdk.b2b.magicLinks

import com.stytch.sdk.StytchApi
import com.stytch.sdk.b2b.StytchB2BAuthenticationStateManager
import com.stytch.sdk.b2b.networking.B2BNetworkingClient
import com.stytch.sdk.b2b.networking.models.B2BMagicLinksAuthenticateResponse
import com.stytch.sdk.b2b.networking.models.B2BMagicLinksDiscoveryAuthenticateResponse
import com.stytch.sdk.b2b.networking.models.B2BMagicLinksDiscoveryEmailSendResponse
import com.stytch.sdk.b2b.networking.models.B2BMagicLinksInviteResponse
import com.stytch.sdk.b2b.networking.models.B2BMagicLinksLoginOrSignupResponse
import com.stytch.sdk.b2b.networking.models.IB2BMagicLinksAuthenticateParameters
import com.stytch.sdk.b2b.networking.models.IB2BMagicLinksDiscoveryAuthenticateParameters
import com.stytch.sdk.b2b.networking.models.IB2BMagicLinksDiscoveryEmailSendParameters
import com.stytch.sdk.b2b.networking.models.IB2BMagicLinksInviteParameters
import com.stytch.sdk.b2b.networking.models.IB2BMagicLinksLoginOrSignupParameters
import com.stytch.sdk.b2b.networking.models.toNetworkModel
import com.stytch.sdk.data.StytchDispatchers
import com.stytch.sdk.data.StytchError
import com.stytch.sdk.pkce.MissingPKCEException
import com.stytch.sdk.pkce.PKCEClient
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException
import kotlin.js.JsExport

/** B2B magic link authentication methods. */
@StytchApi
@JsExport
public interface B2BMagicLinksClient {
    /** Email magic link methods. */
    public val email: B2BEmailMagicLinksClient

    /** Discovery magic link methods (cross-org, unauthenticated). */
    public val discovery: B2BMagicLinksDiscoveryClient

    /**
     * Authenticates a magic link token received via deeplink, establishing a member session.
     * Calls the `POST /sdk/v1/b2b/magic_links/authenticate` endpoint. Retrieves the PKCE code
     * verifier stored during the corresponding [B2BEmailMagicLinksClient.loginOrSignup] call, and
     * automatically includes the intermediate session token if one is present.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchB2B.magicLinks.authenticate(
     *     B2BMagicLinksAuthenticateParameters(
     *         magicLinksToken = "token",
     *         sessionDurationMinutes = 30,
     *     )
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = B2BMagicLinksAuthenticateParameters(magicLinksToken: "token", sessionDurationMinutes: 30)
     * let response = try await StytchB2B.magicLinks.authenticate(params)
     * ```
     *
     * **React Native:**
     * ```js
     * StytchB2B.magicLinks.authenticate({ magicLinksToken: "token", sessionDurationMinutes: 30 })
     * ```
     *
     * @param request - [IB2BMagicLinksAuthenticateParameters]
     *   - `magicLinksToken` — The token extracted from the magic link deeplink URL.
     *   - `sessionDurationMinutes` — Duration of the session to create, in minutes.
     *   - `locale?` — Locale used for any follow-up communications.
     *
     * @return [B2BMagicLinksAuthenticateResponse] containing the authenticated member session.
     *
     * @throws [StytchError] if the token is invalid, expired, or no PKCE verifier is found in storage.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun authenticate(request: IB2BMagicLinksAuthenticateParameters): B2BMagicLinksAuthenticateResponse
}

/** Email magic link methods for org-scoped authentication. */
@StytchApi
@JsExport
public interface B2BEmailMagicLinksClient {
    /**
     * Sends a magic link email to the provided address for login or signup within an organization.
     * Calls the `POST /sdk/v1/b2b/magic_links/email/login_or_signup` endpoint. Generates and stores
     * a PKCE code pair for use in the subsequent [B2BMagicLinksClient.authenticate] call.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchB2B.magicLinks.email.loginOrSignup(
     *     B2BMagicLinksLoginOrSignupParameters(
     *         emailAddress = "user@example.com",
     *         organizationId = "org-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *     )
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = B2BMagicLinksLoginOrSignupParameters(
     *     emailAddress: "user@example.com",
     *     organizationId: "org-test-d5a3b680-e8a3-40c0-b815-ab79986666d0"
     * )
     * let response = try await StytchB2B.magicLinks.email.loginOrSignup(params)
     * ```
     *
     * **React Native:**
     * ```js
     * StytchB2B.magicLinks.email.loginOrSignup({
     *     emailAddress: "user@example.com",
     *     organizationId: "org-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     * })
     * ```
     *
     * @param request - [IB2BMagicLinksLoginOrSignupParameters]
     *   - `emailAddress` — The email address of the member.
     *   - `organizationId` — The ID of the organization the member belongs to.
     *   - `loginRedirectUrl?` — URL to redirect to after a successful login magic link click.
     *   - `signupRedirectUrl?` — URL to redirect to after a successful signup magic link click.
     *   - `loginTemplateId?` — Custom email template ID for the login email.
     *   - `signupTemplateId?` — Custom email template ID for the signup email.
     *   - `locale?` — Locale used for the email content.
     *
     * @return [B2BMagicLinksLoginOrSignupResponse] confirming the magic link email was sent.
     *
     * @throws [StytchError] if the request fails.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun loginOrSignup(request: IB2BMagicLinksLoginOrSignupParameters): B2BMagicLinksLoginOrSignupResponse

    /**
     * Sends a magic link invitation email to a new member of the organization.
     * Calls the `POST /sdk/v1/b2b/magic_links/email/invite` endpoint. Requires an active session.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchB2B.magicLinks.email.invite(
     *     B2BMagicLinksInviteParameters(emailAddress = "newmember@example.com")
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = B2BMagicLinksInviteParameters(emailAddress: "newmember@example.com")
     * let response = try await StytchB2B.magicLinks.email.invite(params)
     * ```
     *
     * **React Native:**
     * ```js
     * StytchB2B.magicLinks.email.invite({ emailAddress: "newmember@example.com" })
     * ```
     *
     * @param request - [IB2BMagicLinksInviteParameters]
     *   - `emailAddress` — The email address of the person to invite.
     *   - `inviteRedirectUrl?` — URL to redirect to when the invite is accepted.
     *   - `inviteTemplateId?` — Custom email template ID for the invitation email.
     *   - `name?` — Display name to pre-populate for the new member.
     *   - `untrustedMetadata?` — Client-settable key-value metadata to assign to the new member.
     *   - `locale?` — Locale used for the invitation email.
     *   - `roles?` — List of RBAC role IDs to assign to the new member.
     *
     * @return [B2BMagicLinksInviteResponse] confirming the invitation email was sent.
     *
     * @throws [StytchError] if the request fails or the caller lacks permission to invite members.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun invite(request: IB2BMagicLinksInviteParameters): B2BMagicLinksInviteResponse
}

/** Magic link discovery methods for listing organizations before a session is established. */
@StytchApi
@JsExport
public interface B2BMagicLinksDiscoveryClient {
    /**
     * Sends a discovery magic link email to enumerate the organizations associated with the given
     * email address. Calls the `POST /sdk/v1/b2b/magic_links/email/discovery/send` endpoint.
     * Generates and stores a PKCE code pair for use in the subsequent [authenticate] call.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchB2B.magicLinks.discovery.emailSend(
     *     B2BMagicLinksDiscoveryEmailSendParameters(emailAddress = "user@example.com")
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = B2BMagicLinksDiscoveryEmailSendParameters(emailAddress: "user@example.com")
     * let response = try await StytchB2B.magicLinks.discovery.emailSend(params)
     * ```
     *
     * **React Native:**
     * ```js
     * StytchB2B.magicLinks.discovery.emailSend({ emailAddress: "user@example.com" })
     * ```
     *
     * @param request - [IB2BMagicLinksDiscoveryEmailSendParameters]
     *   - `emailAddress` — The email address to send the discovery link to.
     *   - `discoveryRedirectUrl?` — URL to redirect to after the user clicks the discovery link.
     *   - `loginTemplateId?` — Custom email template ID for the discovery email.
     *   - `locale?` — Locale used for the email content.
     *   - `discoveryExpirationMinutes?` — Expiration time for the discovery link, in minutes.
     *
     * @return [B2BMagicLinksDiscoveryEmailSendResponse] confirming the discovery email was sent.
     *
     * @throws [StytchError] if the request fails.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun emailSend(request: IB2BMagicLinksDiscoveryEmailSendParameters): B2BMagicLinksDiscoveryEmailSendResponse

    /**
     * Authenticates the discovery magic link token received via deeplink, returning an intermediate
     * session token and the list of discovered organizations. Calls the
     * `POST /sdk/v1/b2b/magic_links/discovery/authenticate` endpoint. Retrieves the PKCE code
     * verifier stored during the corresponding [emailSend] call.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchB2B.magicLinks.discovery.authenticate(
     *     B2BMagicLinksDiscoveryAuthenticateParameters(
     *         discoveryMagicLinksToken = "token",
     *     )
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = B2BMagicLinksDiscoveryAuthenticateParameters(discoveryMagicLinksToken: "token")
     * let response = try await StytchB2B.magicLinks.discovery.authenticate(params)
     * ```
     *
     * **React Native:**
     * ```js
     * StytchB2B.magicLinks.discovery.authenticate({ discoveryMagicLinksToken: "token" })
     * ```
     *
     * @param request - [IB2BMagicLinksDiscoveryAuthenticateParameters]
     *   - `discoveryMagicLinksToken` — The discovery token extracted from the magic link deeplink URL.
     *
     * @return [B2BMagicLinksDiscoveryAuthenticateResponse] containing an intermediate session token
     *   and a list of discovered organizations.
     *
     * @throws [StytchError] if the token is invalid, expired, or no PKCE verifier is found in storage.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun authenticate(request: IB2BMagicLinksDiscoveryAuthenticateParameters): B2BMagicLinksDiscoveryAuthenticateResponse
}

internal class B2BMagicLinksClientImpl(
    private val dispatchers: StytchDispatchers,
    private val networkingClient: B2BNetworkingClient,
    private val pkceClient: PKCEClient,
    private val sessionManager: StytchB2BAuthenticationStateManager,
) : B2BMagicLinksClient {
    override val email: B2BEmailMagicLinksClient = B2BEmailMagicLinksClientImpl(dispatchers, networkingClient, pkceClient)
    override val discovery: B2BMagicLinksDiscoveryClient = B2BMagicLinksDiscoveryClientImpl(dispatchers, networkingClient, pkceClient)

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun authenticate(request: IB2BMagicLinksAuthenticateParameters): B2BMagicLinksAuthenticateResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                val codePair = pkceClient.retrieve() ?: throw MissingPKCEException()
                networkingClient.api
                    .b2BMagicLinksAuthenticate(
                        request.toNetworkModel(
                            pkceCodeVerifier = codePair.verifier,
                            intermediateSessionToken = sessionManager.intermediateSessionToken,
                        ),
                    ).also {
                        pkceClient.revoke()
                    }
            }
        }
}

internal class B2BEmailMagicLinksClientImpl(
    private val dispatchers: StytchDispatchers,
    private val networkingClient: B2BNetworkingClient,
    private val pkceClient: PKCEClient,
) : B2BEmailMagicLinksClient {
    @Throws(StytchError::class, CancellationException::class)
    override suspend fun loginOrSignup(request: IB2BMagicLinksLoginOrSignupParameters): B2BMagicLinksLoginOrSignupResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                val codePair = pkceClient.create()
                networkingClient.api.b2BMagicLinksLoginOrSignup(request.toNetworkModel(pkceCodeChallenge = codePair.challenge))
            }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun invite(request: IB2BMagicLinksInviteParameters): B2BMagicLinksInviteResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.b2BMagicLinksInvite(request.toNetworkModel())
            }
        }
}

internal class B2BMagicLinksDiscoveryClientImpl(
    private val dispatchers: StytchDispatchers,
    private val networkingClient: B2BNetworkingClient,
    private val pkceClient: PKCEClient,
) : B2BMagicLinksDiscoveryClient {
    @Throws(StytchError::class, CancellationException::class)
    override suspend fun emailSend(request: IB2BMagicLinksDiscoveryEmailSendParameters): B2BMagicLinksDiscoveryEmailSendResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                val codePair = pkceClient.create()
                networkingClient.api.b2BMagicLinksDiscoveryEmailSend(request.toNetworkModel(pkceCodeChallenge = codePair.challenge))
            }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun authenticate(request: IB2BMagicLinksDiscoveryAuthenticateParameters): B2BMagicLinksDiscoveryAuthenticateResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                val codePair = pkceClient.retrieve() ?: throw MissingPKCEException()
                networkingClient.api.b2BMagicLinksDiscoveryAuthenticate(request.toNetworkModel(pkceCodeVerifier = codePair.verifier)).also {
                    pkceClient.revoke()
                }
            }
        }
}
