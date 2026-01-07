package com.stytch.sdk.consumer

import com.stytch.sdk.StytchClient
import com.stytch.sdk.consumer.networking.NetworkingClient
import com.stytch.sdk.consumer.otp.OTP
import com.stytch.sdk.consumer.otp.OTPImpl
import com.stytch.sdk.data.StytchClientConfiguration
import kotlin.concurrent.Volatile
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
@JsName("StytchConsumer")
public interface StytchConsumer : StytchClient {
    public val otp: OTP
}

@JsExport
@JsName("createStytchConsumer")
public fun createStytchConsumer(configuration: StytchClientConfiguration): StytchConsumer = DefaultStytchConsumer.getInstance(configuration)

private class DefaultStytchConsumer(
    configuration: StytchClientConfiguration,
) : StytchConsumer {
    private val networkingClient =
        NetworkingClient(configuration) {
            // TODO: once persistence is built, this will be fetching the session token from the device
            null
        }

    override val otp: OTP = OTPImpl(networkingClient)

    companion object {
        @Volatile
        private var instance: StytchConsumer? = null

        fun getInstance(configuration: StytchClientConfiguration): StytchConsumer =
            instance ?: DefaultStytchConsumer(configuration).also { instance = it }
    }
}
