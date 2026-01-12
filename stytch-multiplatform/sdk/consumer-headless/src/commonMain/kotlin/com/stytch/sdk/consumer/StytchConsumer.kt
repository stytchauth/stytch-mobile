package com.stytch.sdk.consumer

import com.stytch.sdk.StytchClient
import com.stytch.sdk.consumer.networking.NetworkingClient
import com.stytch.sdk.consumer.otp.Otp
import com.stytch.sdk.consumer.otp.OtpImpl
import com.stytch.sdk.data.StytchClientConfiguration
import com.stytch.sdk.data.StytchClientConfigurationInternal
import com.stytch.sdk.data.StytchDispatchers
import com.stytch.sdk.encryption.StytchEncryptionClient
import com.stytch.sdk.persistence.StytchPersistenceClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.concurrent.Volatile
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
@JsName("StytchConsumer")
public interface StytchConsumer : StytchClient {
    public val otp: Otp
}

@JsExport
@JsName("createStytchConsumer")
public fun createStytchConsumer(configuration: StytchClientConfiguration): StytchConsumer = DefaultStytchConsumer.getInstance(configuration)

private class DefaultStytchConsumer(
    configuration: StytchClientConfigurationInternal,
) : StytchConsumer {
    private val dispatchers =
        StytchDispatchers(
            ioDispatcher = Dispatchers.Default,
            mainDispatcher = Dispatchers.Main,
        )
    private val encryptionClient = StytchEncryptionClient()
    private val persistenceClient =
        StytchPersistenceClient(
            dispatcher = dispatchers.ioDispatcher,
            encryptionClient = encryptionClient,
            platformPersistenceClient = configuration.platformPersistenceClient,
        )

    private val sessionManager = StytchConsumerSessionManager(dispatchers, persistenceClient)

    private val networkingClient = NetworkingClient(configuration, dispatchers, sessionManager)

    override val otp: Otp = OtpImpl(networkingClient)

    init {
        CoroutineScope(Dispatchers.Default).launch {
            // TODO: any potential init-related cleanup. Like, in stytch-android, we clean up any invalid key/data stuff
            // Maybe this is also where we handle session hydration?
        }
    }

    companion object {
        @Volatile
        private var instance: StytchConsumer? = null

        fun getInstance(configuration: StytchClientConfiguration): StytchConsumer =
            instance ?: DefaultStytchConsumer(configuration.toInternal()).also { instance = it }
    }
}
