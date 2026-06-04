package com.stytch.sdk.b2b.sso

import com.stytch.sdk.StytchApi
import com.stytch.sdk.b2b.StytchB2BAuthenticationStateManager
import com.stytch.sdk.b2b.networking.AuthenticatedResponse
import com.stytch.sdk.b2b.networking.B2BNetworkingClient
import com.stytch.sdk.b2b.networking.models.B2BCreateExternalConnectionResponse
import com.stytch.sdk.b2b.networking.models.B2BCreateOIDCConnectionResponse
import com.stytch.sdk.b2b.networking.models.B2BCreateSAMLConnectionResponse
import com.stytch.sdk.b2b.networking.models.B2BDeleteSAMLVerificationCertificateResponse
import com.stytch.sdk.b2b.networking.models.B2BDeleteSSOConnectionResponse
import com.stytch.sdk.b2b.networking.models.B2BGetSSOConnectionsResponse
import com.stytch.sdk.b2b.networking.models.B2BSSOAuthEnticateParameters
import com.stytch.sdk.b2b.networking.models.B2BSSOAuthEnticateResponse
import com.stytch.sdk.b2b.networking.models.B2BUpdateExternalConnectionResponse
import com.stytch.sdk.b2b.networking.models.B2BUpdateOIDCConnectionResponse
import com.stytch.sdk.b2b.networking.models.B2BUpdateSAMLConnectionByURLResponse
import com.stytch.sdk.b2b.networking.models.B2BUpdateSAMLConnectionResponse
import com.stytch.sdk.b2b.networking.models.IB2BCreateExternalConnectionParameters
import com.stytch.sdk.b2b.networking.models.IB2BCreateOIDCConnectionParameters
import com.stytch.sdk.b2b.networking.models.IB2BCreateSAMLConnectionParameters
import com.stytch.sdk.b2b.networking.models.IB2BSSOAuthEnticateParameters
import com.stytch.sdk.b2b.networking.models.IB2BUpdateExternalConnectionParameters
import com.stytch.sdk.b2b.networking.models.IB2BUpdateOIDCConnectionParameters
import com.stytch.sdk.b2b.networking.models.IB2BUpdateSAMLConnectionByURLParameters
import com.stytch.sdk.b2b.networking.models.IB2BUpdateSAMLConnectionParameters
import com.stytch.sdk.b2b.networking.models.toNetworkModel
import com.stytch.sdk.data.EndpointOptions
import com.stytch.sdk.data.PublicTokenInfo
import com.stytch.sdk.data.StytchDispatchers
import com.stytch.sdk.data.StytchError
import com.stytch.sdk.oauth.B2BSSOStartParameters
import com.stytch.sdk.oauth.IOAuthProvider
import com.stytch.sdk.oauth.OAuthException
import com.stytch.sdk.oauth.OAuthResult
import com.stytch.sdk.pkce.MissingPKCEException
import com.stytch.sdk.pkce.PKCEClient
import io.ktor.http.URLBuilder
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException
import kotlin.js.JsExport

/** Alias for [B2BSSOAuthEnticateResponse], correcting the typo in the generated type name. */
public typealias B2BSSOAuthenticateResponse = B2BSSOAuthEnticateResponse

/** B2B SSO (Single Sign-On) authentication methods via SAML, OIDC, and external connections. */
@StytchApi
@JsExport
public interface B2BSSOClient {
    /** SAML SSO connection management methods. */
    public val saml: B2BSSOSAMLClient

    /** OIDC SSO connection management methods. */
    public val oidc: B2BSSOOIDCClient

    /** External SSO connection management methods. */
    public val external: B2BSSOExternalClient

    /**
     * Initiates an SSO authentication flow by opening a browser to the SSO provider. Opens a browser
     * session at `https://{domain}/b2b/public/sso/start`, then automatically exchanges the resulting
     * token by calling `POST /sdk/v1/b2b/sso/authenticate`, establishing a member session on success.
     *
     * **Kotlin:**
     * ```kotlin
     * val response = StytchB2B.sso.start(
     *     B2BSSOStartParameters(
     *         connectionId = "saml-connection-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *         loginRedirectUrl = "myapp://callback",
     *         signupRedirectUrl = "myapp://callback",
     *         sessionDurationMinutes = 30,
     *     )
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = B2BSSOStartParameters(
     *     connectionId: "saml-connection-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *     loginRedirectUrl: "myapp://callback",
     *     signupRedirectUrl: "myapp://callback",
     *     sessionDurationMinutes: 30
     * )
     * let response = try await StytchB2B.sso.start(params)
     * ```
     *
     * **React Native:**
     * ```js
     * StytchB2B.sso.start({
     *     connectionId: "saml-connection-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *     loginRedirectUrl: "myapp://callback",
     *     signupRedirectUrl: "myapp://callback",
     *     sessionDurationMinutes: 30,
     * })
     * ```
     *
     * @param parameters - [B2BSSOStartParameters]
     *   - `connectionId` — The ID of the SSO connection to authenticate with.
     *   - `loginRedirectUrl?` — URL to redirect to after a successful login.
     *   - `signupRedirectUrl?` — URL to redirect to after a successful sign-up.
     *   - `sessionDurationMinutes?` — Duration of the session to create, in minutes.
     *   - `activity?` *(Android only)* — The Android `Activity` used to launch the browser.
     *   - `oauthPresentationContextProvider?` *(iOS only)* — Presentation context for the `ASWebAuthenticationSession`.
     *
     * @return [AuthenticatedResponse] containing the authenticated member session.
     *
     * @throws [StytchError] if the SSO flow fails or the token cannot be exchanged.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun start(parameters: B2BSSOStartParameters): AuthenticatedResponse

    /**
     * Authenticates an SSO token received via deeplink after the browser-based SSO flow completes,
     * establishing a member session. Calls the `POST /sdk/v1/b2b/sso/authenticate` endpoint.
     * Retrieves the PKCE code verifier stored during the [start] call, and automatically includes the
     * intermediate session token if one is present.
     *
     * Use this method when handling deeplinks manually; prefer [start] for the end-to-end flow.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchB2B.sso.authenticate(
     *     B2BSSOAuthEnticateParameters(
     *         ssoToken = "token",
     *         sessionDurationMinutes = 30,
     *     )
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = B2BSSOAuthEnticateParameters(ssoToken: "token", sessionDurationMinutes: 30)
     * let response = try await StytchB2B.sso.authenticate(params)
     * ```
     *
     * **React Native:**
     * ```js
     * StytchB2B.sso.authenticate({ ssoToken: "token", sessionDurationMinutes: 30 })
     * ```
     *
     * @param request - [IB2BSSOAuthEnticateParameters]
     *   - `ssoToken` — The SSO token extracted from the deeplink URL.
     *   - `sessionDurationMinutes` — Duration of the session to create, in minutes.
     *   - `locale?` — Locale for any follow-up communications.
     *
     * @return [B2BSSOAuthenticateResponse] containing the authenticated member session.
     *
     * @throws [StytchError] if the token is invalid, expired, or no PKCE verifier is found in storage.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun authenticate(request: IB2BSSOAuthEnticateParameters): B2BSSOAuthenticateResponse

    /**
     * Returns all SSO connections (SAML, OIDC, and external) configured for the organization.
     * Calls the `GET /sdk/v1/b2b/sso` endpoint. Requires an active session.
     *
     * **Kotlin:**
     * ```kotlin
     * val response = StytchB2B.sso.getConnections()
     * ```
     *
     * **iOS:**
     * ```swift
     * let response = try await StytchB2B.sso.getConnections()
     * ```
     *
     * **React Native:**
     * ```js
     * StytchB2B.sso.getConnections()
     * ```
     *
     * @return [B2BGetSSOConnectionsResponse] containing all SSO connections for the organization.
     *
     * @throws [StytchError] if the request fails or no active session exists.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun getConnections(): B2BGetSSOConnectionsResponse

    /**
     * Deletes the specified SSO connection from the organization.
     * Calls the `DELETE /sdk/v1/b2b/sso/{connection_id}` endpoint. Requires an active session.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchB2B.sso.deleteConnection("saml-connection-test-d5a3b680-e8a3-40c0-b815-ab79986666d0")
     * ```
     *
     * **iOS:**
     * ```swift
     * let response = try await StytchB2B.sso.deleteConnection("saml-connection-test-d5a3b680-e8a3-40c0-b815-ab79986666d0")
     * ```
     *
     * **React Native:**
     * ```js
     * StytchB2B.sso.deleteConnection("saml-connection-test-d5a3b680-e8a3-40c0-b815-ab79986666d0")
     * ```
     *
     * @param connectionId The ID of the SSO connection to delete.
     *
     * @return [B2BDeleteSSOConnectionResponse] confirming the deletion.
     *
     * @throws [StytchError] if the connection is not found or the request fails.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun deleteConnection(connectionId: String): B2BDeleteSSOConnectionResponse
}

/** SAML SSO connection management methods. */
@StytchApi
@JsExport
public interface B2BSSOSAMLClient {
    /**
     * Creates a new SAML SSO connection for the organization.
     * Calls the `POST /sdk/v1/b2b/sso/saml` endpoint. Requires an active session.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchB2B.sso.saml.createConnection(
     *     B2BCreateSAMLConnectionParameters(
     *         displayName = "My SAML IdP",
     *     )
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = B2BCreateSAMLConnectionParameters(displayName: "My SAML IdP")
     * let response = try await StytchB2B.sso.saml.createConnection(params)
     * ```
     *
     * **React Native:**
     * ```js
     * StytchB2B.sso.saml.createConnection({ displayName: "My SAML IdP" })
     * ```
     *
     * @param request - [IB2BCreateSAMLConnectionParameters]
     *   - `displayName?` — Human-readable label for the connection.
     *   - `identityProvider?` — The identity provider type (e.g. `"okta"`, `"google"`, `"generic"`).
     *
     * @return [B2BCreateSAMLConnectionResponse] containing the newly created connection.
     *
     * @throws [StytchError] if the request fails.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun createConnection(request: IB2BCreateSAMLConnectionParameters): B2BCreateSAMLConnectionResponse

    /**
     * Updates an existing SAML SSO connection.
     * Calls the `PUT /sdk/v1/b2b/sso/saml/{connection_id}` endpoint. Requires an active session.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchB2B.sso.saml.updateConnection(
     *     connectionId = "saml-connection-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *     request = B2BUpdateSAMLConnectionParameters(
     *         displayName = "Updated IdP",
     *         idpSsoUrl = "https://idp.example.com/sso",
     *     ),
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = B2BUpdateSAMLConnectionParameters(
     *     displayName: "Updated IdP",
     *     idpSsoUrl: "https://idp.example.com/sso"
     * )
     * let response = try await StytchB2B.sso.saml.updateConnection(
     *     connectionId: "saml-connection-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *     request: params
     * )
     * ```
     *
     * **React Native:**
     * ```js
     * StytchB2B.sso.saml.updateConnection(
     *     "saml-connection-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *     { displayName: "Updated IdP", idpSsoUrl: "https://idp.example.com/sso" },
     * )
     * ```
     *
     * @param connectionId The ID of the SAML connection to update.
     * @param request - [IB2BUpdateSAMLConnectionParameters]
     *   - `idpEntityId?` — The IdP entity ID.
     *   - `displayName?` — Human-readable label for the connection.
     *   - `attributeMapping?` — Map of IdP attributes to Stytch member fields.
     *   - `x509Certificate?` — PEM-encoded X.509 certificate from the IdP.
     *   - `idpSsoUrl?` — The IdP SSO URL.
     *   - `samlConnectionImplicitRoleAssignments?` — Roles assigned to all members via this connection.
     *   - `samlGroupImplicitRoleAssignments?` — Roles assigned based on SAML group membership.
     *   - `alternativeAudienceUri?` — Alternative audience URI for the SAML assertion.
     *   - `identityProvider?` — The identity provider type.
     *   - `signingPrivateKey?` — Private key used to sign SAML requests.
     *   - `allowGatewayCallback?` — Whether to allow gateway callback.
     *
     * @return [B2BUpdateSAMLConnectionResponse] containing the updated connection.
     *
     * @throws [StytchError] if the connection is not found or the request fails.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun updateConnection(
        connectionId: String,
        request: IB2BUpdateSAMLConnectionParameters,
    ): B2BUpdateSAMLConnectionResponse

    /**
     * Updates a SAML SSO connection by fetching its metadata directly from the provided URL.
     * Calls the `PUT /sdk/v1/b2b/sso/saml/{connection_id}/url` endpoint. Requires an active session.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchB2B.sso.saml.updateConnectionByUrl(
     *     connectionId = "saml-connection-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *     request = B2BUpdateSAMLConnectionByURLParameters(
     *         metadataUrl = "https://idp.example.com/metadata.xml",
     *     ),
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = B2BUpdateSAMLConnectionByURLParameters(metadataUrl: "https://idp.example.com/metadata.xml")
     * let response = try await StytchB2B.sso.saml.updateConnectionByUrl(
     *     connectionId: "saml-connection-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *     request: params
     * )
     * ```
     *
     * **React Native:**
     * ```js
     * StytchB2B.sso.saml.updateConnectionByUrl(
     *     "saml-connection-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *     { metadataUrl: "https://idp.example.com/metadata.xml" },
     * )
     * ```
     *
     * @param connectionId The ID of the SAML connection to update.
     * @param request - [IB2BUpdateSAMLConnectionByURLParameters]
     *   - `metadataUrl` — URL from which to fetch the SAML IdP metadata XML.
     *
     * @return [B2BUpdateSAMLConnectionByURLResponse] containing the updated connection.
     *
     * @throws [StytchError] if the metadata URL is unreachable or the request fails.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun updateConnectionByUrl(
        connectionId: String,
        request: IB2BUpdateSAMLConnectionByURLParameters,
    ): B2BUpdateSAMLConnectionByURLResponse

    /**
     * Deletes a verification certificate from the specified SAML SSO connection.
     * Calls the `DELETE /sdk/v1/b2b/sso/saml/{connection_id}/verification_certificates/{certificate_id}`
     * endpoint. Requires an active session.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchB2B.sso.saml.deleteVerificationCertificate(
     *     connectionId = "saml-connection-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *     certificateId = "cert-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let response = try await StytchB2B.sso.saml.deleteVerificationCertificate(
     *     connectionId: "saml-connection-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *     certificateId: "cert-test-d5a3b680-e8a3-40c0-b815-ab79986666d0"
     * )
     * ```
     *
     * **React Native:**
     * ```js
     * StytchB2B.sso.saml.deleteVerificationCertificate(
     *     "saml-connection-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *     "cert-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     * )
     * ```
     *
     * @param connectionId The ID of the SAML connection.
     * @param certificateId The ID of the certificate to delete.
     *
     * @return [B2BDeleteSAMLVerificationCertificateResponse] confirming the deletion.
     *
     * @throws [StytchError] if the certificate or connection is not found.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun deleteVerificationCertificate(
        connectionId: String,
        certificateId: String,
    ): B2BDeleteSAMLVerificationCertificateResponse
}

/** OIDC SSO connection management methods. */
@StytchApi
@JsExport
public interface B2BSSOOIDCClient {
    /**
     * Creates a new OIDC SSO connection for the organization.
     * Calls the `POST /sdk/v1/b2b/sso/oidc` endpoint. Requires an active session.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchB2B.sso.oidc.createConnection(
     *     B2BCreateOIDCConnectionParameters(
     *         displayName = "My OIDC IdP",
     *     )
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = B2BCreateOIDCConnectionParameters(displayName: "My OIDC IdP")
     * let response = try await StytchB2B.sso.oidc.createConnection(params)
     * ```
     *
     * **React Native:**
     * ```js
     * StytchB2B.sso.oidc.createConnection({ displayName: "My OIDC IdP" })
     * ```
     *
     * @param request - [IB2BCreateOIDCConnectionParameters]
     *   - `displayName?` — Human-readable label for the connection.
     *   - `identityProvider?` — The identity provider type.
     *
     * @return [B2BCreateOIDCConnectionResponse] containing the newly created connection.
     *
     * @throws [StytchError] if the request fails.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun createConnection(request: IB2BCreateOIDCConnectionParameters): B2BCreateOIDCConnectionResponse

    /**
     * Updates an existing OIDC SSO connection.
     * Calls the `PUT /sdk/v1/b2b/sso/oidc/{connection_id}` endpoint. Requires an active session.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchB2B.sso.oidc.updateConnection(
     *     connectionId = "oidc-connection-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *     request = B2BUpdateOIDCConnectionParameters(
     *         displayName = "Updated OIDC IdP",
     *         issuer = "https://idp.example.com",
     *     ),
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = B2BUpdateOIDCConnectionParameters(
     *     displayName: "Updated OIDC IdP",
     *     issuer: "https://idp.example.com"
     * )
     * let response = try await StytchB2B.sso.oidc.updateConnection(
     *     connectionId: "oidc-connection-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *     request: params
     * )
     * ```
     *
     * **React Native:**
     * ```js
     * StytchB2B.sso.oidc.updateConnection(
     *     "oidc-connection-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *     { displayName: "Updated OIDC IdP", issuer: "https://idp.example.com" },
     * )
     * ```
     *
     * @param connectionId The ID of the OIDC connection to update.
     * @param request - [IB2BUpdateOIDCConnectionParameters]
     *   - `displayName?` — Human-readable label for the connection.
     *   - `clientId?` — The OIDC client ID.
     *   - `clientSecret?` — The OIDC client secret.
     *   - `issuer?` — The OIDC issuer URL.
     *   - `authorizationUrl?` — The OIDC authorization endpoint URL.
     *   - `tokenUrl?` — The OIDC token endpoint URL.
     *   - `userinfoUrl?` — The OIDC userinfo endpoint URL.
     *   - `jwksUrl?` — The OIDC JWKS endpoint URL.
     *   - `identityProvider?` — The identity provider type.
     *   - `customScopes?` — Additional OIDC scopes to request.
     *   - `attributeMapping?` — Map of OIDC claims to Stytch member fields.
     *
     * @return [B2BUpdateOIDCConnectionResponse] containing the updated connection.
     *
     * @throws [StytchError] if the connection is not found or the request fails.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun updateConnection(
        connectionId: String,
        request: IB2BUpdateOIDCConnectionParameters,
    ): B2BUpdateOIDCConnectionResponse
}

/** External SSO connection management methods. */
@StytchApi
@JsExport
public interface B2BSSOExternalClient {
    /**
     * Creates a new external SSO connection for the organization.
     * Calls the `POST /sdk/v1/b2b/sso/external` endpoint. Requires an active session.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchB2B.sso.external.createConnection(
     *     B2BCreateExternalConnectionParameters(
     *         externalConnectionId = "ext-conn-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *         externalOrganizationId = "ext-org-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *     )
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = B2BCreateExternalConnectionParameters(
     *     externalConnectionId: "ext-conn-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *     externalOrganizationId: "ext-org-test-d5a3b680-e8a3-40c0-b815-ab79986666d0"
     * )
     * let response = try await StytchB2B.sso.external.createConnection(params)
     * ```
     *
     * **React Native:**
     * ```js
     * StytchB2B.sso.external.createConnection({
     *     externalConnectionId: "ext-conn-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *     externalOrganizationId: "ext-org-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     * })
     * ```
     *
     * @param request - [IB2BCreateExternalConnectionParameters]
     *   - `externalConnectionId` — The ID of the external SSO connection in the partner organization.
     *   - `externalOrganizationId` — The ID of the partner organization.
     *   - `displayName?` — Human-readable label for the connection.
     *
     * @return [B2BCreateExternalConnectionResponse] containing the newly created connection.
     *
     * @throws [StytchError] if the request fails.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun createConnection(request: IB2BCreateExternalConnectionParameters): B2BCreateExternalConnectionResponse

    /**
     * Updates an existing external SSO connection.
     * Calls the `PUT /sdk/v1/b2b/sso/external/{connection_id}` endpoint. Requires an active session.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchB2B.sso.external.updateConnection(
     *     connectionId = "ext-conn-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *     request = B2BUpdateExternalConnectionParameters(
     *         displayName = "Updated External Connection",
     *     ),
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = B2BUpdateExternalConnectionParameters(displayName: "Updated External Connection")
     * let response = try await StytchB2B.sso.external.updateConnection(
     *     connectionId: "ext-conn-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *     request: params
     * )
     * ```
     *
     * **React Native:**
     * ```js
     * StytchB2B.sso.external.updateConnection(
     *     "ext-conn-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *     { displayName: "Updated External Connection" },
     * )
     * ```
     *
     * @param connectionId The ID of the external SSO connection to update.
     * @param request - [IB2BUpdateExternalConnectionParameters]
     *   - `displayName` — Human-readable label for the connection.
     *   - `externalConnectionImplicitRoleAssignments?` — Roles assigned to all members via this connection.
     *   - `externalGroupImplicitRoleAssignments?` — Roles assigned based on group membership.
     *
     * @return [B2BUpdateExternalConnectionResponse] containing the updated connection.
     *
     * @throws [StytchError] if the connection is not found or the request fails.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun updateConnection(
        connectionId: String,
        request: IB2BUpdateExternalConnectionParameters,
    ): B2BUpdateExternalConnectionResponse
}

internal class B2BSSOClientImpl(
    private val dispatchers: StytchDispatchers,
    private val networkingClient: B2BNetworkingClient,
    private val pkceClient: PKCEClient,
    private val sessionManager: StytchB2BAuthenticationStateManager,
    private val oauthProvider: IOAuthProvider,
    private val publicTokenInfo: PublicTokenInfo,
    private val endpointOptions: EndpointOptions,
    private val cnameDomain: () -> String?,
    private val defaultSessionDuration: Int,
) : B2BSSOClient {
    override val saml: B2BSSOSAMLClient = B2BSSOSAMLClientImpl(dispatchers, networkingClient)
    override val oidc: B2BSSOOIDCClient = B2BSSOOIDCClientImpl(dispatchers, networkingClient)
    override val external: B2BSSOExternalClient = B2BSSOExternalClientImpl(dispatchers, networkingClient)

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun start(parameters: B2BSSOStartParameters): AuthenticatedResponse =
        withContext(dispatchers.ioDispatcher) {
            val domain = cnameDomain() ?: if (publicTokenInfo.isTestToken) endpointOptions.testDomain else endpointOptions.liveDomain
            val baseUrl = "https://$domain/b2b/public/sso/start"
            try {
                val codePair = pkceClient.create()
                val url = buildSSOUrl(baseUrl, codePair.challenge, parameters)
                val result = oauthProvider.startBrowserFlow(url, parameters.toOAuthStartParameters(), dispatchers)
                when (result) {
                    is OAuthResult.ClassicToken -> {
                        networkingClient
                            .request {
                                networkingClient.api.b2BSSOAuthEnticate(
                                    B2BSSOAuthEnticateParameters(
                                        ssoToken = result.token,
                                        sessionDurationMinutes = parameters.sessionDurationMinutes ?: defaultSessionDuration,
                                    ).toNetworkModel(
                                        pkceCodeVerifier = codePair.verifier,
                                        intermediateSessionToken = sessionManager.intermediateSessionToken,
                                    ),
                                )
                            }.also { pkceClient.revoke() } as AuthenticatedResponse
                    }

                    is OAuthResult.Error -> {
                        throw OAuthException(RuntimeException(result.message))
                    }

                    else -> {
                        throw OAuthException(RuntimeException("Unexpected OAuth result type"))
                    }
                }
            } finally {
                pkceClient.revoke()
            }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun authenticate(request: IB2BSSOAuthEnticateParameters): B2BSSOAuthenticateResponse =
        withContext(dispatchers.ioDispatcher) {
            val codePair = pkceClient.retrieve() ?: throw MissingPKCEException()
            networkingClient
                .request {
                    networkingClient.api.b2BSSOAuthEnticate(
                        request.toNetworkModel(
                            pkceCodeVerifier = codePair.verifier,
                            intermediateSessionToken = sessionManager.intermediateSessionToken,
                        ),
                    )
                }.also { pkceClient.revoke() }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun getConnections(): B2BGetSSOConnectionsResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request { networkingClient.api.b2BGetSSOConnections() }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun deleteConnection(connectionId: String): B2BDeleteSSOConnectionResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request { networkingClient.api.b2BDeleteSSOConnection(connectionId) }
        }

    private fun buildSSOUrl(
        baseUrl: String,
        challenge: String,
        parameters: B2BSSOStartParameters,
    ): String {
        val params =
            mutableMapOf(
                "connection_id" to parameters.connectionId,
                "public_token" to publicTokenInfo.publicToken,
                "pkce_code_challenge" to challenge,
                "login_redirect_url" to parameters.loginRedirectUrl,
                "signup_redirect_url" to parameters.signupRedirectUrl,
            )
        val uri = URLBuilder(baseUrl)
        params.forEach { (key, value) -> if (value?.isNotEmpty() == true) uri.parameters.append(key, value) }
        return uri.build().toString()
    }
}

internal class B2BSSOSAMLClientImpl(
    private val dispatchers: StytchDispatchers,
    private val networkingClient: B2BNetworkingClient,
) : B2BSSOSAMLClient {
    @Throws(StytchError::class, CancellationException::class)
    override suspend fun createConnection(request: IB2BCreateSAMLConnectionParameters): B2BCreateSAMLConnectionResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request { networkingClient.api.b2BCreateSAMLConnection(request.toNetworkModel()) }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun updateConnection(
        connectionId: String,
        request: IB2BUpdateSAMLConnectionParameters,
    ): B2BUpdateSAMLConnectionResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request { networkingClient.api.b2BUpdateSAMLConnection(connectionId, request.toNetworkModel()) }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun updateConnectionByUrl(
        connectionId: String,
        request: IB2BUpdateSAMLConnectionByURLParameters,
    ): B2BUpdateSAMLConnectionByURLResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request { networkingClient.api.b2BUpdateSAMLConnectionByURL(connectionId, request.toNetworkModel()) }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun deleteVerificationCertificate(
        connectionId: String,
        certificateId: String,
    ): B2BDeleteSAMLVerificationCertificateResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request { networkingClient.api.b2BDeleteSAMLVerificationCertificate(connectionId, certificateId) }
        }
}

internal class B2BSSOOIDCClientImpl(
    private val dispatchers: StytchDispatchers,
    private val networkingClient: B2BNetworkingClient,
) : B2BSSOOIDCClient {
    @Throws(StytchError::class, CancellationException::class)
    override suspend fun createConnection(request: IB2BCreateOIDCConnectionParameters): B2BCreateOIDCConnectionResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request { networkingClient.api.b2BCreateOIDCConnection(request.toNetworkModel()) }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun updateConnection(
        connectionId: String,
        request: IB2BUpdateOIDCConnectionParameters,
    ): B2BUpdateOIDCConnectionResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request { networkingClient.api.b2BUpdateOIDCConnection(connectionId, request.toNetworkModel()) }
        }
}

internal class B2BSSOExternalClientImpl(
    private val dispatchers: StytchDispatchers,
    private val networkingClient: B2BNetworkingClient,
) : B2BSSOExternalClient {
    @Throws(StytchError::class, CancellationException::class)
    override suspend fun createConnection(request: IB2BCreateExternalConnectionParameters): B2BCreateExternalConnectionResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request { networkingClient.api.b2BCreateExternalConnection(request.toNetworkModel()) }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun updateConnection(
        connectionId: String,
        request: IB2BUpdateExternalConnectionParameters,
    ): B2BUpdateExternalConnectionResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request { networkingClient.api.b2BUpdateExternalConnection(connectionId, request.toNetworkModel()) }
        }
}
