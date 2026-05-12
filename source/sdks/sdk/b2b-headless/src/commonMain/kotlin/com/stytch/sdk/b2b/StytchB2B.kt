package com.stytch.sdk.b2b

import com.stytch.sdk.StytchApi
import com.stytch.sdk.StytchClient
import com.stytch.sdk.b2b.StytchB2BAuthenticationStateManager.Companion.SESSION_TOKEN_IDENTIFIER
import com.stytch.sdk.b2b.data.B2BAuthenticationState
import com.stytch.sdk.b2b.data.B2BTokenType
import com.stytch.sdk.b2b.data.DeeplinkAuthenticationStatus
import com.stytch.sdk.b2b.data.DeeplinkToken
import com.stytch.sdk.b2b.dfp.DFPClient
import com.stytch.sdk.b2b.dfp.DFPClientImpl
import com.stytch.sdk.b2b.discovery.B2BDiscoveryClient
import com.stytch.sdk.b2b.discovery.B2BDiscoveryClientImpl
import com.stytch.sdk.b2b.magicLinks.B2BMagicLinksClient
import com.stytch.sdk.b2b.magicLinks.B2BMagicLinksClientImpl
import com.stytch.sdk.b2b.members.B2BMembersClient
import com.stytch.sdk.b2b.members.B2BMembersClientImpl
import com.stytch.sdk.b2b.migrations.LegacyTokenMigration
import com.stytch.sdk.b2b.networking.AuthenticatedResponse
import com.stytch.sdk.b2b.networking.B2BNetworkingClient
import com.stytch.sdk.b2b.networking.models.B2BMagicLinksAuthenticateParameters
import com.stytch.sdk.b2b.networking.models.B2BOAuthAuthenticateParameters
import com.stytch.sdk.b2b.networking.models.B2BSSOAuthEnticateParameters
import com.stytch.sdk.b2b.oauth.B2BOAuthClient
import com.stytch.sdk.b2b.oauth.B2BOAuthClientImpl
import com.stytch.sdk.b2b.organizations.B2BOrganizationsClient
import com.stytch.sdk.b2b.organizations.B2BOrganizationsClientImpl
import com.stytch.sdk.b2b.otp.B2BOtpClient
import com.stytch.sdk.b2b.otp.B2BOtpClientImpl
import com.stytch.sdk.b2b.passwords.B2BPasswordsClient
import com.stytch.sdk.b2b.passwords.B2BPasswordsClientImpl
import com.stytch.sdk.b2b.rbac.B2BRBACClient
import com.stytch.sdk.b2b.rbac.B2BRBACClientImpl
import com.stytch.sdk.b2b.recoveryCodes.B2BRecoveryCodesClient
import com.stytch.sdk.b2b.recoveryCodes.B2BRecoveryCodesClientImpl
import com.stytch.sdk.b2b.scim.B2BSCIMClient
import com.stytch.sdk.b2b.scim.B2BSCIMClientImpl
import com.stytch.sdk.b2b.session.B2BSessionsClient
import com.stytch.sdk.b2b.session.B2BSessionsClientImpl
import com.stytch.sdk.b2b.sso.B2BSSOClient
import com.stytch.sdk.b2b.sso.B2BSSOClientImpl
import com.stytch.sdk.b2b.totp.B2BTOTPClient
import com.stytch.sdk.b2b.totp.B2BTOTPClientImpl
import com.stytch.sdk.createStytchDispatchers
import com.stytch.sdk.data.BootstrapResponse
import com.stytch.sdk.data.JsCleanup
import com.stytch.sdk.data.PKCECodePair
import com.stytch.sdk.data.StytchClientConfiguration
import com.stytch.sdk.data.StytchClientConfigurationInternal
import com.stytch.sdk.data.StytchError
import com.stytch.sdk.migrations.LegacyTokenReader
import com.stytch.sdk.migrations.MigrationRunner
import com.stytch.sdk.migrations.MigrationStore
import com.stytch.sdk.persistence.StytchPersistenceClient
import com.stytch.sdk.pkce.PKCEClient
import io.ktor.http.URLBuilder
import io.ktor.utils.io.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.internal.SynchronizedObject
import kotlinx.coroutines.internal.synchronized
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.concurrent.Volatile
import kotlin.js.JsExport
import kotlin.js.JsName

/**
 * Main entry point for the Stytch B2B SDK.
 *
 * Provides access to all B2B authentication methods and session management.
 * Create an instance with [createStytchB2B].
 */
@StytchApi
@JsExport
@JsName("StytchB2B")
public interface StytchB2B : StytchClient {
    /** Session management methods. */
    public val session: B2BSessionsClient

    /** Magic link authentication methods. */
    public val magicLinks: B2BMagicLinksClient

    /** OTP (SMS and email) authentication methods. */
    public val otp: B2BOtpClient

    /** Password-based authentication methods. */
    public val passwords: B2BPasswordsClient

    /** TOTP authenticator methods. */
    public val totp: B2BTOTPClient

    /** Cross-org discovery flow methods for listing and joining organizations. */
    public val discovery: B2BDiscoveryClient

    /** Member management methods. */
    public val members: B2BMembersClient

    /** Organization management methods. */
    public val organizations: B2BOrganizationsClient

    /** Recovery code management methods. */
    public val recoveryCodes: B2BRecoveryCodesClient

    /** SCIM provisioning methods. */
    public val scim: B2BSCIMClient

    /** OAuth authentication methods. */
    public val oauth: B2BOAuthClient

    /** SSO authentication methods. */
    public val sso: B2BSSOClient

    /** Role-based access control (RBAC) methods. */
    public val rbac: B2BRBACClient

    /** Device fingerprinting (DFP) methods. */
    public val dfp: DFPClient

    /** A [StateFlow] emitting the current authentication state whenever it changes. */
    public val authenticationStateFlow: StateFlow<B2BAuthenticationState>

    /**
     * Subscribes to authentication state changes via a callback.
     * Returns a [JsCleanup] whose [JsCleanup.stop] cancels the subscription.
     */
    @JsName("authenticationStateObserver")
    public fun authenticationStateObserver(callback: (authenticationState: B2BAuthenticationState) -> Unit): JsCleanup

    /**
     * Handles an incoming deeplink URL, authenticating the token it contains if possible.
     *
     * Returns [DeeplinkAuthenticationStatus.Authenticated] for magic links, OAuth, and SSO tokens.
     * Returns [DeeplinkAuthenticationStatus.ManualHandlingRequired] for password reset and
     * discovery tokens, which require additional user interaction.
     * Returns [DeeplinkAuthenticationStatus.UnknownDeeplink] if the URL is not a Stytch deeplink.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun authenticate(
        url: String,
        sessionDurationMinutes: Int?,
    ): DeeplinkAuthenticationStatus

    /** Returns the current PKCE code pair if one exists (used for advanced manual flows). */
    public suspend fun getPKCECodePair(): PKCECodePair?

    /** Parses a URL and returns a [DeeplinkToken] if it contains a Stytch token, or null otherwise. */
    public fun parseDeeplink(url: String): DeeplinkToken?

    /**
     * Hydrates a session from a given session token
     */
    public suspend fun hydrate(sessionToken: String)
}

/**
 * Creates a new [StytchB2B] instance with the given [StytchClientConfiguration].
 * Repeated calls with the same process return the same singleton instance.
 */
@JsExport
@JsName("createStytchB2B")
public fun createStytchB2B(configuration: StytchClientConfiguration): StytchB2B = DefaultStytchB2B.getInstance(configuration)

internal class DefaultStytchB2B(
    private val configuration: StytchClientConfigurationInternal,
) : StytchB2B {
    private val dispatchers = createStytchDispatchers()
    private val persistenceClient =
        StytchPersistenceClient(
            dispatcher = dispatchers.ioDispatcher,
            encryptionClient = configuration.encryptionClient,
            platformPersistenceClient = configuration.platformPersistenceClient,
        )

    private val sessionManager = StytchB2BAuthenticationStateManager(dispatchers, persistenceClient)

    private val networkingClient = B2BNetworkingClient(configuration, dispatchers, sessionManager)

    private val pkceClient = PKCEClient(configuration.encryptionClient, persistenceClient)

    private val migrationRunner =
        MigrationRunner(
            migrations =
                listOf(
                    LegacyTokenMigration(
                        publicToken = configuration.tokenInfo.publicToken,
                        platform = configuration.platform,
                        tokenReader = LegacyTokenReader(),
                        persistenceClient = persistenceClient,
                        dispatchers = dispatchers,
                    ),
                ),
            store = MigrationStore("b2b", configuration.platformPersistenceClient),
        )

    override val session: B2BSessionsClient = B2BSessionsClientImpl(dispatchers, networkingClient)

    override val magicLinks: B2BMagicLinksClient = B2BMagicLinksClientImpl(dispatchers, networkingClient, pkceClient, sessionManager)

    override val otp: B2BOtpClient = B2BOtpClientImpl(dispatchers, networkingClient, sessionManager)

    override val passwords: B2BPasswordsClient = B2BPasswordsClientImpl(dispatchers, networkingClient, pkceClient, sessionManager)

    override val totp: B2BTOTPClient = B2BTOTPClientImpl(dispatchers, networkingClient, sessionManager)

    override val discovery: B2BDiscoveryClient = B2BDiscoveryClientImpl(dispatchers, networkingClient, pkceClient, sessionManager)

    override val members: B2BMembersClient = B2BMembersClientImpl(dispatchers, networkingClient)

    override val organizations: B2BOrganizationsClient = B2BOrganizationsClientImpl(dispatchers, networkingClient)

    override val recoveryCodes: B2BRecoveryCodesClient = B2BRecoveryCodesClientImpl(dispatchers, networkingClient, sessionManager)

    override val scim: B2BSCIMClient = B2BSCIMClientImpl(dispatchers, networkingClient)

    override val oauth: B2BOAuthClient =
        B2BOAuthClientImpl(
            dispatchers = dispatchers,
            networkingClient = networkingClient,
            pkceClient = pkceClient,
            sessionManager = sessionManager,
            oauthProvider = configuration.oAuthProvider,
            publicTokenInfo = configuration.tokenInfo,
            endpointOptions = configuration.endpointOptions,
            cnameDomain = { bootstrapResponse?.cnameDomain },
            defaultSessionDuration = configuration.defaultSessionDuration,
        )

    override val sso: B2BSSOClient =
        B2BSSOClientImpl(
            dispatchers = dispatchers,
            networkingClient = networkingClient,
            pkceClient = pkceClient,
            sessionManager = sessionManager,
            oauthProvider = configuration.oAuthProvider,
            publicTokenInfo = configuration.tokenInfo,
            endpointOptions = configuration.endpointOptions,
            cnameDomain = { bootstrapResponse?.cnameDomain },
            defaultSessionDuration = configuration.defaultSessionDuration,
        )

    override val dfp: DFPClient = DFPClientImpl(dispatchers, configuration.dfpProvider)

    override val rbac: B2BRBACClient =
        B2BRBACClientImpl(
            dispatchers = dispatchers,
            sessionManager = sessionManager,
            getRbacPolicy = { bootstrapResponse?.rbacPolicy },
            refreshAndGetRbacPolicy = {
                bootstrapResponse =
                    networkingClient.refreshBootStrapData(bootstrapResponse).also {
                        // and persist whatever the latest bootstrap response was
                        persistenceClient.save(BOOTSTRAP_IDENTIFIER, it)
                    }
                bootstrapResponse?.rbacPolicy
            },
        )

    override val authenticationStateFlow: StateFlow<B2BAuthenticationState> = sessionManager.authenticationStateFlow

    override fun authenticationStateObserver(callback: (authenticationState: B2BAuthenticationState) -> Unit): JsCleanup {
        val job =
            CoroutineScope(dispatchers.mainDispatcher).launch {
                authenticationStateFlow.collect { callback(it) }
            }
        return object : JsCleanup {
            override fun stop() {
                job.cancel()
            }
        }
    }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun authenticate(
        url: String,
        sessionDurationMinutes: Int?,
    ): DeeplinkAuthenticationStatus =
        withContext(dispatchers.ioDispatcher) {
            val token = parseDeeplink(url) ?: return@withContext DeeplinkAuthenticationStatus.UnknownDeeplink(url)
            when (token.type) {
                B2BTokenType.UNKNOWN -> {
                    DeeplinkAuthenticationStatus.UnknownDeeplink(url)
                }

                B2BTokenType.MULTI_TENANT_MAGIC_LINKS -> {
                    DeeplinkAuthenticationStatus.Authenticated(
                        magicLinks.authenticate(
                            B2BMagicLinksAuthenticateParameters(
                                magicLinksToken = token.token,
                                sessionDurationMinutes = sessionDurationMinutes ?: configuration.defaultSessionDuration,
                            ),
                        ) as AuthenticatedResponse,
                    )
                }

                B2BTokenType.MULTI_TENANT_PASSWORDS -> {
                    DeeplinkAuthenticationStatus.ManualHandlingRequired(token.token)
                }

                B2BTokenType.OAUTH -> {
                    DeeplinkAuthenticationStatus.Authenticated(
                        oauth.authenticate(
                            B2BOAuthAuthenticateParameters(
                                oauthToken = token.token,
                                sessionDurationMinutes = sessionDurationMinutes ?: configuration.defaultSessionDuration,
                            ),
                        ) as AuthenticatedResponse,
                    )
                }

                B2BTokenType.DISCOVERY, B2BTokenType.DISCOVERY_OAUTH -> {
                    // Discovery OAuth returns IST + discovered orgs, not a full session.
                    // Caller must present org selection and then call oauth.discovery.authenticate().
                    DeeplinkAuthenticationStatus.ManualHandlingRequired(token.token)
                }

                B2BTokenType.SSO -> {
                    DeeplinkAuthenticationStatus.Authenticated(
                        sso.authenticate(
                            B2BSSOAuthEnticateParameters(
                                ssoToken = token.token,
                                sessionDurationMinutes = sessionDurationMinutes ?: configuration.defaultSessionDuration,
                            ),
                        ) as AuthenticatedResponse,
                    )
                }
            }
        }

    override suspend fun getPKCECodePair(): PKCECodePair? = pkceClient.retrieve()

    override fun parseDeeplink(url: String): DeeplinkToken? {
        val uri = URLBuilder(url).build()
        val tokenType = B2BTokenType.fromString(uri.parameters["stytch_token_type"])
        val token = uri.parameters["token"] ?: return null
        return DeeplinkToken(tokenType, token)
    }

    override suspend fun hydrate(sessionToken: String) {
        withContext(dispatchers.ioDispatcher) {
            persistenceClient.save(SESSION_TOKEN_IDENTIFIER, sessionToken)
            networkingClient.startSessionUpdateJob(0L)
        }
    }

    internal var bootstrapResponse: BootstrapResponse? = null

    init {
        CoroutineScope(dispatchers.ioDispatcher).launch {
            // Bootstrap (unauthenticated) and migrations are independent — run concurrently.
            val bootstrapJob =
                async {
                    val cached = persistenceClient.get<BootstrapResponse>(BOOTSTRAP_IDENTIFIER, null)
                    networkingClient.refreshBootStrapData(cached).also {
                        persistenceClient.save(BOOTSTRAP_IDENTIFIER, it)
                    }
                }
            // Migrations must complete before hydration so session data is in the correct format.
            migrationRunner.runPendingMigrations()
            sessionManager.hydrate()
            bootstrapResponse = bootstrapJob.await()
        }
    }

    companion object {
        @Volatile
        private var instance: StytchB2B? = null
        private const val BOOTSTRAP_IDENTIFIER = "stytch_b2b_bootstrap_data"

        @OptIn(InternalCoroutinesApi::class)
        fun getInstance(configuration: StytchClientConfiguration): StytchB2B =
            instance ?: synchronized(DefaultStytchB2B::class as SynchronizedObject) {
                instance ?: DefaultStytchB2B(configuration.toInternal()).also { instance = it }
            }
    }
}
