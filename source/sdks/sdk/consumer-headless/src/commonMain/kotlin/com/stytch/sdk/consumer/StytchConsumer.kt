package com.stytch.sdk.consumer

import com.stytch.sdk.StytchApi
import com.stytch.sdk.StytchClient
import com.stytch.sdk.consumer.StytchConsumerAuthenticationStateManager.Companion.SESSION_TOKEN_IDENTIFIER
import com.stytch.sdk.consumer.biometrics.BiometricsClient
import com.stytch.sdk.consumer.biometrics.BiometricsClientImpl
import com.stytch.sdk.consumer.crypto.CryptoClient
import com.stytch.sdk.consumer.crypto.CryptoClientImpl
import com.stytch.sdk.consumer.data.ConsumerAuthenticationState
import com.stytch.sdk.consumer.data.ConsumerTokenType
import com.stytch.sdk.consumer.data.DeeplinkAuthenticationStatus
import com.stytch.sdk.consumer.data.DeeplinkToken
import com.stytch.sdk.consumer.dfp.DFPClient
import com.stytch.sdk.consumer.dfp.DFPClientImpl
import com.stytch.sdk.consumer.magicLinks.MagicLinksClient
import com.stytch.sdk.consumer.magicLinks.MagicLinksImpl
import com.stytch.sdk.consumer.migrations.LegacyTokenMigration
import com.stytch.sdk.consumer.networking.AuthenticatedResponse
import com.stytch.sdk.consumer.networking.ConsumerNetworkingClient
import com.stytch.sdk.consumer.networking.models.MagicLinksAuthenticateParameters
import com.stytch.sdk.consumer.networking.models.OAuthAuthenticateParameters
import com.stytch.sdk.consumer.oauth.OAuthClient
import com.stytch.sdk.consumer.oauth.OAuthClientImpl
import com.stytch.sdk.consumer.otp.OtpClient
import com.stytch.sdk.consumer.otp.OtpImpl
import com.stytch.sdk.consumer.passkeys.PasskeysClient
import com.stytch.sdk.consumer.passkeys.PasskeysClientImpl
import com.stytch.sdk.consumer.passwords.PasswordsClient
import com.stytch.sdk.consumer.passwords.PasswordsClientImpl
import com.stytch.sdk.consumer.session.SessionClient
import com.stytch.sdk.consumer.session.SessionImpl
import com.stytch.sdk.consumer.totp.TOTPClient
import com.stytch.sdk.consumer.totp.TOTPClientImpl
import com.stytch.sdk.consumer.user.UserClient
import com.stytch.sdk.consumer.user.UserClientImpl
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
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.concurrent.Volatile
import kotlin.js.JsExport
import kotlin.js.JsName

/** The main entry point for the Stytch Consumer SDK. */
@StytchApi
@JsExport
@JsName("StytchConsumer")
public interface StytchConsumer : StytchClient {
    /** OTP (one-time passcode) authentication via SMS, email, or WhatsApp. */
    public val otp: OtpClient

    /** Session management. */
    public val session: SessionClient

    /** Crypto wallet authentication. */
    public val crypto: CryptoClient

    /** Magic link authentication. */
    public val magicLinks: MagicLinksClient

    /** TOTP (time-based one-time passcode) authentication. */
    public val totp: TOTPClient

    /** Password-based authentication. */
    public val passwords: PasswordsClient

    /** User account management. */
    public val user: UserClient

    /** Passkey (WebAuthn) authentication. */
    public val passkeys: PasskeysClient

    /** Biometric authentication. */
    public val biometrics: BiometricsClient

    /** OAuth authentication (browser-based and native provider flows). */
    public val oauth: OAuthClient

    /** Device fingerprinting (DFP) integration. */
    public val dfp: DFPClient

    /** A [StateFlow] that emits the current authentication state whenever it changes. */
    public val authenticationStateFlow: StateFlow<ConsumerAuthenticationState>

    /**
     * Registers a callback that is invoked whenever the authentication state changes.
     * Returns a [JsCleanup] that stops the observer when [JsCleanup.stop] is called.
     */
    @JsName("authenticationStateObserver")
    public fun authenticationStateObserver(callback: (authenticationState: ConsumerAuthenticationState) -> Unit): JsCleanup

    /**
     * Authenticates a Stytch deeplink URL, dispatching to the appropriate auth method based on
     * the token type embedded in the URL. Returns [DeeplinkAuthenticationStatus.ManualHandlingRequired]
     * for password reset tokens, which must be handled by the caller.
     *
     * @param url The full deeplink URL to authenticate.
     * @param sessionDurationMinutes The desired session duration. Defaults to the SDK's configured value.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun authenticate(
        url: String,
        sessionDurationMinutes: Int?,
    ): DeeplinkAuthenticationStatus

    /** Returns the current PKCE code pair if one has been generated and not yet consumed. */
    public suspend fun getPKCECodePair(): PKCECodePair?

    /**
     * Parses a URL and returns the Stytch token it contains, or `null` if the URL is not a
     * recognized Stytch deeplink.
     */
    public fun parseDeeplink(url: String): DeeplinkToken?

    /**
     * Hydrates a session from a given session token
     */
    public suspend fun hydrate(sessionToken: String)
}

/**
 * Creates and returns a [StytchConsumer] instance configured with the provided [StytchClientConfiguration].
 * Repeated calls with the same process return the same singleton instance.
 */
@JsExport
@JsName("createStytchConsumer")
public fun createStytchConsumer(configuration: StytchClientConfiguration): StytchConsumer = DefaultStytchConsumer.getInstance(configuration)

internal class DefaultStytchConsumer(
    private val configuration: StytchClientConfigurationInternal,
) : StytchConsumer {
    private val dispatchers = createStytchDispatchers()
    private val persistenceClient =
        StytchPersistenceClient(
            dispatcher = dispatchers.ioDispatcher,
            encryptionClient = configuration.encryptionClient,
            platformPersistenceClient = configuration.platformPersistenceClient,
        )

    private val sessionManager = StytchConsumerAuthenticationStateManager(dispatchers, persistenceClient)

    private val networkingClient = ConsumerNetworkingClient(configuration, dispatchers, sessionManager)

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
            store = MigrationStore("consumer", configuration.platformPersistenceClient),
        )

    override val otp: OtpClient = OtpImpl(dispatchers, networkingClient, sessionManager)

    override val session: SessionClient = SessionImpl(dispatchers, networkingClient)

    override val crypto: CryptoClient = CryptoClientImpl(dispatchers, networkingClient, sessionManager)

    override val magicLinks: MagicLinksClient = MagicLinksImpl(dispatchers, networkingClient, pkceClient, sessionManager)

    override val totp: TOTPClient = TOTPClientImpl(dispatchers, networkingClient)

    override val passwords: PasswordsClient = PasswordsClientImpl(dispatchers, networkingClient, pkceClient)

    override val user: UserClient = UserClientImpl(dispatchers, networkingClient)

    override val passkeys: PasskeysClient = PasskeysClientImpl(dispatchers, networkingClient, sessionManager, configuration.passkeyProvider)

    override val biometrics: BiometricsClient =
        BiometricsClientImpl(
            dispatchers = dispatchers,
            networkingClient = networkingClient,
            sessionManager = sessionManager,
            encryptionClient = configuration.encryptionClient,
            biometricsProvider = configuration.biometricsProvider,
        )

    override val dfp: DFPClient = DFPClientImpl(dispatchers, configuration.dfpProvider)

    override val oauth: OAuthClient =
        OAuthClientImpl(
            publicTokenInfo = configuration.tokenInfo,
            endpointOptions = configuration.endpointOptions,
            cnameDomain = { bootstrapResponse?.cnameDomain },
            dispatchers = dispatchers,
            networkingClient = networkingClient,
            pkceClient = pkceClient,
            oauthProvider = configuration.oAuthProvider,
            defaultSessionDuration = configuration.defaultSessionDuration,
        )
    override val authenticationStateFlow: StateFlow<ConsumerAuthenticationState> = sessionManager.authenticationStateFlow

    override fun authenticationStateObserver(callback: (authenticationState: ConsumerAuthenticationState) -> Unit): JsCleanup {
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
                ConsumerTokenType.UNKNOWN -> {
                    DeeplinkAuthenticationStatus.UnknownDeeplink(url)
                }

                ConsumerTokenType.RESET_PASSWORD -> {
                    DeeplinkAuthenticationStatus.ManualHandlingRequired(token.token)
                }

                ConsumerTokenType.MAGIC_LINKS -> {
                    DeeplinkAuthenticationStatus.Authenticated(
                        magicLinks.authenticate(
                            MagicLinksAuthenticateParameters(
                                token = token.token,
                                sessionDurationMinutes =
                                    sessionDurationMinutes ?: configuration.defaultSessionDuration,
                            ),
                        ) as AuthenticatedResponse,
                    )
                }

                ConsumerTokenType.OAUTH -> {
                    DeeplinkAuthenticationStatus.Authenticated(
                        oauth.authenticate(
                            OAuthAuthenticateParameters(
                                token = token.token,
                                sessionDurationMinutes =
                                    sessionDurationMinutes ?: configuration.defaultSessionDuration,
                            ),
                        ),
                    )
                }
            }
        }

    override suspend fun getPKCECodePair(): PKCECodePair? = pkceClient.retrieve()

    override fun parseDeeplink(url: String): DeeplinkToken? {
        val uri = URLBuilder(url).build()
        val tokenType = ConsumerTokenType.fromString(uri.parameters["stytch_token_type"])
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
        private var instance: StytchConsumer? = null
        private const val BOOTSTRAP_IDENTIFIER = "stytch_consumer_bootstrap_data"

        fun getInstance(configuration: StytchClientConfiguration): StytchConsumer =
            instance ?: DefaultStytchConsumer(configuration.toInternal()).also { instance = it }
    }
}
