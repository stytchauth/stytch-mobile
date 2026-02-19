package com.stytch.sdk.consumer

import com.stytch.sdk.StytchClient
import com.stytch.sdk.consumer.crypto.CryptoClient
import com.stytch.sdk.consumer.crypto.CryptoClientImpl
import com.stytch.sdk.consumer.data.ConsumerAuthenticationState
import com.stytch.sdk.consumer.networking.ConsumerNetworkingClient
import com.stytch.sdk.consumer.otp.OtpClient
import com.stytch.sdk.consumer.otp.OtpImpl
import com.stytch.sdk.consumer.session.SessionClient
import com.stytch.sdk.consumer.session.SessionImpl
import com.stytch.sdk.data.JsCleanup
import com.stytch.sdk.data.StytchClientConfiguration
import com.stytch.sdk.data.StytchClientConfigurationInternal
import com.stytch.sdk.data.StytchDispatchers
import com.stytch.sdk.persistence.StytchPersistenceClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.concurrent.Volatile
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
@JsName("StytchConsumer")
public interface StytchConsumer : StytchClient {
    public val otp: OtpClient
    public val session: SessionClient
    public val crypto: CryptoClient
    public val authenticationStateFlow: StateFlow<ConsumerAuthenticationState>

    @JsName("authenticationStateObserver")
    public fun authenticationStateObserver(callback: (authenticatonState: ConsumerAuthenticationState) -> Unit): JsCleanup
}

@JsExport
@JsName("createStytchConsumer")
public fun createStytchConsumer(configuration: StytchClientConfiguration): StytchConsumer = DefaultStytchConsumer.getInstance(configuration)

internal class DefaultStytchConsumer(
    configuration: StytchClientConfigurationInternal,
) : StytchConsumer {
    private val dispatchers =
        StytchDispatchers(
            ioDispatcher = Dispatchers.Default,
            mainDispatcher = Dispatchers.Main,
        )
    private val persistenceClient =
        StytchPersistenceClient(
            dispatcher = dispatchers.ioDispatcher,
            encryptionClient = configuration.encryptionClient,
            platformPersistenceClient = configuration.platformPersistenceClient,
        )

    private val sessionManager = StytchConsumerAuthenticationStateManager(dispatchers, persistenceClient)

    private val networkingClient = ConsumerNetworkingClient(configuration, dispatchers, sessionManager)

    override val otp: OtpClient = OtpImpl.create(dispatchers, networkingClient)

    override val session: SessionClient = SessionImpl(dispatchers, networkingClient)

    override val crypto: CryptoClient = CryptoClientImpl(dispatchers, networkingClient, sessionManager)

    override val authenticationStateFlow: StateFlow<ConsumerAuthenticationState> = sessionManager.authenticationStateFlow

    override fun authenticationStateObserver(callback: (authenticatonState: ConsumerAuthenticationState) -> Unit): JsCleanup {
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

    init {
        CoroutineScope(Dispatchers.Default).launch {
            networkingClient.refreshBootStrapData()
        }
    }

    companion object {
        @Volatile
        private var instance: StytchConsumer? = null

        fun getInstance(configuration: StytchClientConfiguration): StytchConsumer =
            instance ?: DefaultStytchConsumer(configuration.toInternal()).also { instance = it }
    }
}
