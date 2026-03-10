package com.stytch.sdk.b2b

import com.stytch.sdk.StytchClient
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
                /* TODO
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
                */
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
