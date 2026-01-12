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
import io.ktor.util.encodeBase64
import io.ktor.utils.io.core.toByteArray
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
    private val networkingClient =
        NetworkingClient(configuration) {
            // TODO: once persistence is built, this will be fetching the session token from the device
            null
        }
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

    override val otp: Otp = OtpImpl(networkingClient)

    init {
        CoroutineScope(Dispatchers.Default).launch {
            val plaintext = "Jordan's Cool Plaintext String"
            val encrypted = encryptionClient.encrypt(plaintext.toByteArray())
            val decrypted = encryptionClient.decrypt(encrypted).decodeToString()
            println(
                """
                JORDAN ENCRYPTION TEST:
                PLAINTEXT = $plaintext
                ENCRYPTED = ${encrypted.encodeBase64()}
                DECRYPTED = $decrypted
                """.trimIndent(),
            )
        }
    }

    companion object {
        @Volatile
        private var instance: StytchConsumer? = null

        fun getInstance(configuration: StytchClientConfiguration): StytchConsumer =
            instance ?: DefaultStytchConsumer(configuration.toInternal()).also { instance = it }
    }
}
