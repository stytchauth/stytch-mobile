package com.stytch.sdk.b2b

import com.stytch.sdk.StytchClient
import com.stytch.sdk.b2b.data.B2BAuthenticationState
import com.stytch.sdk.b2b.data.B2BTokenType
import com.stytch.sdk.b2b.data.DeeplinkAuthenticationStatus
import com.stytch.sdk.b2b.data.DeeplinkToken
import com.stytch.sdk.b2b.discovery.B2BDiscoveryClient
import com.stytch.sdk.b2b.discovery.B2BDiscoveryClientImpl
import com.stytch.sdk.b2b.magicLinks.B2BMagicLinksClient
import com.stytch.sdk.b2b.magicLinks.B2BMagicLinksClientImpl
import com.stytch.sdk.b2b.members.B2BMembersClient
import com.stytch.sdk.b2b.members.B2BMembersClientImpl
import com.stytch.sdk.b2b.networking.AuthenticatedResponse
import com.stytch.sdk.b2b.networking.B2BNetworkingClient
import com.stytch.sdk.b2b.networking.models.B2BMagicLinksAuthenticateParameters
import com.stytch.sdk.b2b.networking.models.B2BOAuthAuthenticateParameters
import com.stytch.sdk.b2b.oauth.B2BOAuthClient
import com.stytch.sdk.b2b.oauth.B2BOAuthClientImpl
import com.stytch.sdk.b2b.organizations.B2BOrganizationsClient
import com.stytch.sdk.b2b.organizations.B2BOrganizationsClientImpl
import com.stytch.sdk.b2b.otp.B2BOtpClient
import com.stytch.sdk.b2b.otp.B2BOtpClientImpl
import com.stytch.sdk.b2b.passwords.B2BPasswordsClient
import com.stytch.sdk.b2b.passwords.B2BPasswordsClientImpl
import com.stytch.sdk.b2b.recoveryCodes.B2BRecoveryCodesClient
import com.stytch.sdk.b2b.recoveryCodes.B2BRecoveryCodesClientImpl
import com.stytch.sdk.b2b.scim.B2BSCIMClient
import com.stytch.sdk.b2b.scim.B2BSCIMClientImpl
import com.stytch.sdk.b2b.session.B2BSessionsClient
import com.stytch.sdk.b2b.session.B2BSessionsClientImpl
import com.stytch.sdk.b2b.totp.B2BTOTPClient
import com.stytch.sdk.b2b.totp.B2BTOTPClientImpl
import com.stytch.sdk.data.BootstrapResponse
import com.stytch.sdk.data.JsCleanup
import com.stytch.sdk.data.PKCECodePair
import com.stytch.sdk.data.StytchClientConfiguration
import com.stytch.sdk.data.StytchClientConfigurationInternal
import com.stytch.sdk.data.StytchError
import com.stytch.sdk.persistence.StytchPersistenceClient
import com.stytch.sdk.pkce.PKCEClient
import io.ktor.http.URLBuilder
import io.ktor.utils.io.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.concurrent.Volatile
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
@JsName("StytchB2B")
public interface StytchB2B : StytchClient {
    public val session: B2BSessionsClient
    public val magicLinks: B2BMagicLinksClient
    public val otp: B2BOtpClient
    public val passwords: B2BPasswordsClient
    public val totp: B2BTOTPClient
    public val discovery: B2BDiscoveryClient
    public val members: B2BMembersClient
    public val organizations: B2BOrganizationsClient
    public val recoveryCodes: B2BRecoveryCodesClient
    public val scim: B2BSCIMClient
    public val oauth: B2BOAuthClient

    public val authenticationStateFlow: StateFlow<B2BAuthenticationState>

    @JsName("authenticationStateObserver")
    public fun authenticationStateObserver(callback: (authenticationState: B2BAuthenticationState) -> Unit): JsCleanup

    @Throws(StytchError::class, CancellationException::class)
    public suspend fun authenticate(
        url: String,
        sessionDurationMinutes: Int?,
    ): DeeplinkAuthenticationStatus

    public suspend fun getPKCECodePair(): PKCECodePair?

    public fun parseDeeplink(url: String): DeeplinkToken?
}

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

                B2BTokenType.DISCOVERY_OAUTH -> {
                    // Discovery OAuth returns IST + discovered orgs, not a full session.
                    // Caller must present org selection and then call oauth.discovery.authenticate().
                    DeeplinkAuthenticationStatus.ManualHandlingRequired(token.token)
                }

                else -> {
                    // TODO: Handle SSO token type (PR 3)
                    DeeplinkAuthenticationStatus.UnknownDeeplink(url)
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

    internal var bootstrapResponse: BootstrapResponse? = null

    init {
        CoroutineScope(dispatchers.ioDispatcher).launch {
            bootstrapResponse = networkingClient.refreshBootStrapData()
        }
    }

    companion object {
        @Volatile
        private var instance: StytchB2B? = null

        fun getInstance(configuration: StytchClientConfiguration): StytchB2B =
            instance ?: DefaultStytchB2B(configuration.toInternal()).also { instance = it }
    }
}
