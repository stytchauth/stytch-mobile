package com.stytch.sdk.consumer.otp

import com.stytch.sdk.consumer.networking.ConsumerNetworkingClient
import com.stytch.sdk.consumer.networking.models.IOTPsAuthenticateParameters
import com.stytch.sdk.consumer.networking.models.IOTPsSMSLoginOrCreateParameters
import com.stytch.sdk.consumer.networking.models.OTPsAuthenticateResponse
import com.stytch.sdk.consumer.networking.models.OTPsSMSLoginOrCreateResponse
import com.stytch.sdk.consumer.networking.models.toNetworkModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.js.JsExport

@JsExport
public interface OtpClient {
    public val sms: SmsOtpClient

    public suspend fun authenticate(request: IOTPsAuthenticateParameters): OTPsAuthenticateResponse
}

@JsExport
public interface SmsOtpClient {
    public suspend fun loginOrCreate(request: IOTPsSMSLoginOrCreateParameters): OTPsSMSLoginOrCreateResponse
}

public class OtpImpl private constructor(
    private val networkingClient: ConsumerNetworkingClient,
) : OtpClient {
    override val sms: SmsOtpClient = SmsOtpImpl.create(networkingClient)

    override suspend fun authenticate(request: IOTPsAuthenticateParameters): OTPsAuthenticateResponse =
        withContext(Dispatchers.Default) {
            networkingClient.request {
                networkingClient.api.oTPsAuthenticate(request.toNetworkModel())
            }
        }

    internal companion object {
        fun create(networkingClient: ConsumerNetworkingClient): OtpImpl = OtpImpl(networkingClient)
    }
}

public class SmsOtpImpl private constructor(
    private val networkingClient: ConsumerNetworkingClient,
) : SmsOtpClient {
    override suspend fun loginOrCreate(request: IOTPsSMSLoginOrCreateParameters): OTPsSMSLoginOrCreateResponse =
        withContext(Dispatchers.Default) {
            networkingClient.request {
                networkingClient.api.oTPsSMSLoginOrCreate(request.toNetworkModel())
            }
        }

    internal companion object {
        fun create(networkingClient: ConsumerNetworkingClient): SmsOtpImpl = SmsOtpImpl(networkingClient)
    }
}
