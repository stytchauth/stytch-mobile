package com.stytch.sdk.b2b.discovery

import com.stytch.sdk.StytchApi
import com.stytch.sdk.b2b.StytchB2BAuthenticationStateManager
import com.stytch.sdk.b2b.networking.B2BNetworkingClient
import com.stytch.sdk.b2b.networking.models.B2BDiscoveryIntermediateSessionsExchangeResponse
import com.stytch.sdk.b2b.networking.models.B2BDiscoveryOrganizationsCreateResponse
import com.stytch.sdk.b2b.networking.models.B2BDiscoveryOrganizationsResponse
import com.stytch.sdk.b2b.networking.models.B2BDiscoveryPasswordResetResponse
import com.stytch.sdk.b2b.networking.models.B2BDiscoveryPasswordResetStartResponse
import com.stytch.sdk.b2b.networking.models.B2BPasswordDiscoveryAuthenticateResponse
import com.stytch.sdk.b2b.networking.models.IB2BDiscoveryIntermediateSessionsExchangeParameters
import com.stytch.sdk.b2b.networking.models.IB2BDiscoveryOrganizationsCreateParameters
import com.stytch.sdk.b2b.networking.models.IB2BDiscoveryOrganizationsParameters
import com.stytch.sdk.b2b.networking.models.IB2BDiscoveryPasswordResetParameters
import com.stytch.sdk.b2b.networking.models.IB2BDiscoveryPasswordResetStartParameters
import com.stytch.sdk.b2b.networking.models.IB2BPasswordDiscoveryAuthenticateParameters
import com.stytch.sdk.b2b.networking.models.toNetworkModel
import com.stytch.sdk.data.StytchDispatchers
import com.stytch.sdk.data.StytchError
import com.stytch.sdk.pkce.MissingPKCEException
import com.stytch.sdk.pkce.PKCEClient
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException
import kotlin.js.JsExport

/**
 * Discovery flow methods for listing and joining organizations.
 * Used after obtaining an intermediate session token via magic link, OTP, OAuth, or SSO discovery.
 */
@StytchApi
@JsExport
public interface B2BDiscoveryClient {
    /** Methods for listing and creating organizations during the discovery flow. */
    public val organizations: B2BDiscoveryOrganizationsClient

    /** Methods for exchanging an intermediate session token into a full member session. */
    public val intermediateSessions: B2BDiscoveryIntermediateSessionsClient

    /** Password-based authentication and reset within the discovery flow. */
    public val passwords: B2BDiscoveryPasswordsClient
}

/** Organization discovery methods. */
@StytchApi
@JsExport
public interface B2BDiscoveryOrganizationsClient {
    /**
     * Lists all organizations discoverable for the authenticated user. Calls the
     * `POST /sdk/v1/b2b/discovery/organizations` endpoint. Automatically includes the intermediate
     * session token if one is present.
     *
     * **Kotlin:**
     * ```kotlin
     * val response = StytchB2B.discovery.organizations.list(B2BDiscoveryOrganizationsParameters())
     * ```
     *
     * **iOS:**
     * ```swift
     * let response = try await StytchB2B.discovery.organizations.list(B2BDiscoveryOrganizationsParameters())
     * ```
     *
     * **React Native:**
     * ```js
     * StytchB2B.discovery.organizations.list({})
     * ```
     *
     * @param request - [IB2BDiscoveryOrganizationsParameters] — no fields required.
     *
     * @return [B2BDiscoveryOrganizationsResponse] containing discoverable organizations.
     *
     * @throws [StytchError] if the request fails.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun list(request: IB2BDiscoveryOrganizationsParameters): B2BDiscoveryOrganizationsResponse

    /**
     * Creates a new organization and immediately establishes a member session within it.
     * Calls the `POST /sdk/v1/b2b/discovery/organizations/create` endpoint. Automatically
     * includes the intermediate session token if one is present.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchB2B.discovery.organizations.create(
     *     B2BDiscoveryOrganizationsCreateParameters(
     *         organizationName = "My Company",
     *         organizationSlug = "my-company",
     *         sessionDurationMinutes = 30,
     *     )
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = B2BDiscoveryOrganizationsCreateParameters(
     *     organizationName: "My Company",
     *     organizationSlug: "my-company",
     *     sessionDurationMinutes: 30
     * )
     * let response = try await StytchB2B.discovery.organizations.create(params)
     * ```
     *
     * **React Native:**
     * ```js
     * StytchB2B.discovery.organizations.create({
     *     organizationName: "My Company",
     *     organizationSlug: "my-company",
     *     sessionDurationMinutes: 30,
     * })
     * ```
     *
     * @param request - [IB2BDiscoveryOrganizationsCreateParameters]
     *   - `sessionDurationMinutes` — Duration of the session to create, in minutes.
     *   - `organizationName?` — Display name for the new organization.
     *   - `organizationSlug?` — URL-safe identifier for the new organization.
     *   - `organizationLogoUrl?` — URL for the organization's logo.
     *   - `ssoJitProvisioning?` — SSO just-in-time provisioning policy.
     *   - `emailAllowedDomains?` — List of allowed email domains for the organization.
     *   - `emailJitProvisioning?` — Email just-in-time provisioning policy.
     *   - `emailInvites?` — Email invite policy for the organization.
     *   - `authMethods?` — Authentication methods policy.
     *   - `allowedAuthMethods?` — List of allowed authentication methods.
     *   - `mfaMethods?` — MFA methods policy.
     *   - `allowedMfaMethods?` — List of allowed MFA methods.
     *   - `mfaPolicy?` — MFA enforcement policy.
     *   - `oauthTenantJitProvisioning?` — OAuth tenant just-in-time provisioning policy.
     *   - `allowedOauthTenants?` — Map of allowed OAuth tenants.
     *
     * @return [B2BDiscoveryOrganizationsCreateResponse] containing the new organization and member session.
     *
     * @throws [StytchError] if the organization cannot be created.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun create(request: IB2BDiscoveryOrganizationsCreateParameters): B2BDiscoveryOrganizationsCreateResponse
}

/** Intermediate session exchange — converts a discovery token into a full member session. */
@StytchApi
@JsExport
public interface B2BDiscoveryIntermediateSessionsClient {
    /**
     * Exchanges an intermediate session token for a full member session in the selected organization.
     * Calls the `POST /sdk/v1/b2b/discovery/intermediate_sessions/exchange` endpoint. Automatically
     * includes the intermediate session token if one is present.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchB2B.discovery.intermediateSessions.exchange(
     *     B2BDiscoveryIntermediateSessionsExchangeParameters(
     *         organizationId = "org-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *         sessionDurationMinutes = 30,
     *     )
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = B2BDiscoveryIntermediateSessionsExchangeParameters(
     *     organizationId: "org-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *     sessionDurationMinutes: 30
     * )
     * let response = try await StytchB2B.discovery.intermediateSessions.exchange(params)
     * ```
     *
     * **React Native:**
     * ```js
     * StytchB2B.discovery.intermediateSessions.exchange({
     *     organizationId: "org-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *     sessionDurationMinutes: 30,
     * })
     * ```
     *
     * @param request - [IB2BDiscoveryIntermediateSessionsExchangeParameters]
     *   - `organizationId` — The ID of the organization to join.
     *   - `sessionDurationMinutes` — Duration of the session to create, in minutes.
     *   - `locale?` — Locale for any follow-up communications.
     *
     * @return [B2BDiscoveryIntermediateSessionsExchangeResponse] containing the full member session.
     *
     * @throws [StytchError] if the exchange fails or no intermediate session token is present.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun exchange(
        request: IB2BDiscoveryIntermediateSessionsExchangeParameters,
    ): B2BDiscoveryIntermediateSessionsExchangeResponse
}

/** Password authentication and reset within the discovery flow (before an org is selected). */
@StytchApi
@JsExport
public interface B2BDiscoveryPasswordsClient {
    /**
     * Authenticates an email and password credential during the cross-org discovery flow, before an
     * organization is selected. Calls the `POST /sdk/v1/b2b/passwords/discovery/authenticate` endpoint.
     * Returns discovered organizations and an intermediate session token; call
     * [B2BDiscoveryIntermediateSessionsClient.exchange] to establish a full member session.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchB2B.discovery.passwords.authenticate(
     *     B2BPasswordDiscoveryAuthenticateParameters(
     *         emailAddress = "user@example.com",
     *         password = "correct-horse-battery-staple",
     *     )
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = B2BPasswordDiscoveryAuthenticateParameters(
     *     emailAddress: "user@example.com",
     *     password: "correct-horse-battery-staple"
     * )
     * let response = try await StytchB2B.discovery.passwords.authenticate(params)
     * ```
     *
     * **React Native:**
     * ```js
     * StytchB2B.discovery.passwords.authenticate({
     *     emailAddress: "user@example.com",
     *     password: "correct-horse-battery-staple",
     * })
     * ```
     *
     * @param request - [IB2BPasswordDiscoveryAuthenticateParameters]
     *   - `emailAddress` — The user's email address.
     *   - `password` — The user's password.
     *
     * @return [B2BPasswordDiscoveryAuthenticateResponse] containing discovered organizations and an intermediate session token.
     *
     * @throws [StytchError] if the credentials are invalid.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun authenticate(request: IB2BPasswordDiscoveryAuthenticateParameters): B2BPasswordDiscoveryAuthenticateResponse

    /**
     * Sends a password reset email to the specified address for use in the cross-org discovery flow.
     * Calls the `POST /sdk/v1/b2b/passwords/discovery/reset/start` endpoint. Stores a PKCE code
     * challenge for verification when [reset] is called.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchB2B.discovery.passwords.resetStart(
     *     B2BDiscoveryPasswordResetStartParameters(
     *         emailAddress = "user@example.com",
     *         discoveryRedirectUrl = "myapp://discovery",
     *     )
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = B2BDiscoveryPasswordResetStartParameters(
     *     emailAddress: "user@example.com",
     *     discoveryRedirectUrl: "myapp://discovery"
     * )
     * let response = try await StytchB2B.discovery.passwords.resetStart(params)
     * ```
     *
     * **React Native:**
     * ```js
     * StytchB2B.discovery.passwords.resetStart({
     *     emailAddress: "user@example.com",
     *     discoveryRedirectUrl: "myapp://discovery",
     * })
     * ```
     *
     * @param request - [IB2BDiscoveryPasswordResetStartParameters]
     *   - `emailAddress` — The user's email address.
     *   - `discoveryRedirectUrl?` — URL to redirect to after the reset flow completes.
     *   - `resetPasswordRedirectUrl?` — URL embedded in the reset email link.
     *   - `resetPasswordExpirationMinutes?` — Expiry duration for the reset link, in minutes.
     *   - `resetPasswordTemplateId?` — Custom email template ID for the reset email.
     *   - `locale?` — Locale for the reset email.
     *   - `verifyEmailTemplateId?` — Custom email template ID for email verification.
     *
     * @return [B2BDiscoveryPasswordResetStartResponse] confirming the reset email was sent.
     *
     * @throws [StytchError] if the email address is not found or the request fails.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun resetStart(request: IB2BDiscoveryPasswordResetStartParameters): B2BDiscoveryPasswordResetStartResponse

    /**
     * Completes the password reset in the cross-org discovery flow using the token from the reset
     * email. Calls the `POST /sdk/v1/b2b/passwords/discovery/reset` endpoint. Retrieves the PKCE
     * code verifier stored during the [resetStart] call.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchB2B.discovery.passwords.reset(
     *     B2BDiscoveryPasswordResetParameters(
     *         passwordResetToken = "token-from-email",
     *         password = "new-correct-horse-battery-staple",
     *     )
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = B2BDiscoveryPasswordResetParameters(
     *     passwordResetToken: "token-from-email",
     *     password: "new-correct-horse-battery-staple"
     * )
     * let response = try await StytchB2B.discovery.passwords.reset(params)
     * ```
     *
     * **React Native:**
     * ```js
     * StytchB2B.discovery.passwords.reset({
     *     passwordResetToken: "token-from-email",
     *     password: "new-correct-horse-battery-staple",
     * })
     * ```
     *
     * @param request - [IB2BDiscoveryPasswordResetParameters]
     *   - `passwordResetToken` — The password reset token extracted from the email link.
     *   - `password` — The new password to set.
     *
     * @return [B2BDiscoveryPasswordResetResponse] containing discovered organizations and an intermediate session token.
     *
     * @throws [StytchError] if the token is invalid, expired, or no PKCE verifier is found in storage.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun reset(request: IB2BDiscoveryPasswordResetParameters): B2BDiscoveryPasswordResetResponse
}

internal class B2BDiscoveryClientImpl(
    private val dispatchers: StytchDispatchers,
    private val networkingClient: B2BNetworkingClient,
    private val pkceClient: PKCEClient,
    private val sessionManager: StytchB2BAuthenticationStateManager,
) : B2BDiscoveryClient {
    override val organizations: B2BDiscoveryOrganizationsClient =
        B2BDiscoveryOrganizationsClientImpl(dispatchers, networkingClient, sessionManager)
    override val intermediateSessions: B2BDiscoveryIntermediateSessionsClient =
        B2BDiscoveryIntermediateSessionsClientImpl(dispatchers, networkingClient, sessionManager)
    override val passwords: B2BDiscoveryPasswordsClient =
        B2BDiscoveryPasswordsClientImpl(dispatchers, networkingClient, pkceClient)
}

internal class B2BDiscoveryOrganizationsClientImpl(
    private val dispatchers: StytchDispatchers,
    private val networkingClient: B2BNetworkingClient,
    private val sessionManager: StytchB2BAuthenticationStateManager,
) : B2BDiscoveryOrganizationsClient {
    @Throws(StytchError::class, CancellationException::class)
    override suspend fun list(request: IB2BDiscoveryOrganizationsParameters): B2BDiscoveryOrganizationsResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.b2BDiscoveryOrganizations(
                    request.toNetworkModel(intermediateSessionToken = sessionManager.intermediateSessionToken),
                )
            }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun create(request: IB2BDiscoveryOrganizationsCreateParameters): B2BDiscoveryOrganizationsCreateResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.b2BDiscoveryOrganizationsCreate(
                    request.toNetworkModel(intermediateSessionToken = sessionManager.intermediateSessionToken ?: ""),
                )
            }
        }
}

internal class B2BDiscoveryIntermediateSessionsClientImpl(
    private val dispatchers: StytchDispatchers,
    private val networkingClient: B2BNetworkingClient,
    private val sessionManager: StytchB2BAuthenticationStateManager,
) : B2BDiscoveryIntermediateSessionsClient {
    @Throws(StytchError::class, CancellationException::class)
    override suspend fun exchange(
        request: IB2BDiscoveryIntermediateSessionsExchangeParameters,
    ): B2BDiscoveryIntermediateSessionsExchangeResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.b2BDiscoveryIntermediateSessionsExchange(
                    request.toNetworkModel(intermediateSessionToken = sessionManager.intermediateSessionToken ?: ""),
                )
            }
        }
}

internal class B2BDiscoveryPasswordsClientImpl(
    private val dispatchers: StytchDispatchers,
    private val networkingClient: B2BNetworkingClient,
    private val pkceClient: PKCEClient,
) : B2BDiscoveryPasswordsClient {
    @Throws(StytchError::class, CancellationException::class)
    override suspend fun authenticate(request: IB2BPasswordDiscoveryAuthenticateParameters): B2BPasswordDiscoveryAuthenticateResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.b2BPasswordDiscoveryAuthenticate(request.toNetworkModel())
            }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun resetStart(request: IB2BDiscoveryPasswordResetStartParameters): B2BDiscoveryPasswordResetStartResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                val codePair = pkceClient.create()
                networkingClient.api.b2BDiscoveryPasswordResetStart(
                    request.toNetworkModel(pkceCodeChallenge = codePair.challenge),
                )
            }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun reset(request: IB2BDiscoveryPasswordResetParameters): B2BDiscoveryPasswordResetResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                val codePair = pkceClient.retrieve() ?: throw MissingPKCEException()
                networkingClient.api
                    .b2BDiscoveryPasswordReset(
                        request.toNetworkModel(pkceCodeVerifier = codePair.verifier),
                    ).also { pkceClient.revoke() }
            }
        }
}
