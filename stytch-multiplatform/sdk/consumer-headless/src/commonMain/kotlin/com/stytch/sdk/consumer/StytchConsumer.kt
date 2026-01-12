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
import kotlinx.serialization.Serializable
import kotlin.concurrent.Volatile
import kotlin.js.JsExport
import kotlin.js.JsName
import kotlin.time.Clock
import kotlin.time.Instant

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
            // TODO: any potential init-related cleanup. Like, in stytch-android, we clean up any invalid key/data stuff
            // Maybe this is also where we handle session hydration?

            // lets test the persistence
            val dataToPersist =
                MyComplexDataType(
                    name = "Jordan",
                    age = 38,
                    interests = listOf("axe throwing", "old servers"),
                    timestamp = Clock.System.now(),
                )
            val existedAtStartup = persistenceClient.get<MyComplexDataType>("MY_TEST_DATA", null)
            println("JORDAN > existed: ${existedAtStartup != null}")
            if (existedAtStartup == null) {
                println("JORDAN >> persist it. then relaunch the app and see if it shows up")
                persistenceClient.save("MY_TEST_DATA", dataToPersist)
                println("JORDAN >> successfully persisted")
            } else {
                println("JORDAN >> what i got back was: $existedAtStartup")
                println("JORDAN >> Now delete it, and verify it doesn't exist")
                persistenceClient.remove("MY_TEST_DATA")
                println("JORDAN >> successfully removed")
                println("JORDAN >> still exists = ${persistenceClient.get<MyComplexDataType>("MY_TEST_DATA", null) != null}")
            }
        }
    }

    companion object {
        @Volatile
        private var instance: StytchConsumer? = null

        fun getInstance(configuration: StytchClientConfiguration): StytchConsumer =
            instance ?: DefaultStytchConsumer(configuration.toInternal()).also { instance = it }
    }
}

@Serializable
public data class MyComplexDataType(
    val name: String,
    val age: Int,
    val interests: List<String>,
    val timestamp: Instant,
)
