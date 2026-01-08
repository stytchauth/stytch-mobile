package com.stytch.sdk.consumer

import com.stytch.sdk.StytchClient
import com.stytch.sdk.consumer.networking.NetworkingClient
import com.stytch.sdk.consumer.otp.Otp
import com.stytch.sdk.consumer.otp.OtpImpl
import com.stytch.sdk.data.StytchClientConfiguration
import com.stytch.sdk.data.StytchClientConfigurationInternal
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

    override val otp: Otp = OtpImpl(networkingClient)

    companion object {
        @Volatile
        private var instance: StytchConsumer? = null

        fun getInstance(configuration: StytchClientConfiguration): StytchConsumer =
            instance ?: DefaultStytchConsumer(configuration.toInternal()).also { instance = it }
    }
}
