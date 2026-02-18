package com.stytch.sdk.consumer.otp

import com.stytch.sdk.consumer.networking.ConsumerNetworkingClient
import com.stytch.sdk.consumer.networking.models.OTPsAuthenticateRequest
import com.stytch.sdk.consumer.networking.models.OTPsAuthenticateResponse
import com.stytch.sdk.consumer.networking.models.OTPsSMSLoginOrCreateRequest
import com.stytch.sdk.consumer.networking.models.OTPsSMSLoginOrCreateResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.js.JsExport

@JsExport
public interface OtpClient {
    public val sms: SmsOtpClient

    public suspend fun authenticate(request: OTPsAuthenticateRequest): OTPsAuthenticateResponse
}

@JsExport
public interface SmsOtpClient {
    public suspend fun loginOrCreate(request: OTPsSMSLoginOrCreateRequest): OTPsSMSLoginOrCreateResponse
}

public class OtpImpl private constructor(
    private val networkingClient: ConsumerNetworkingClient,
) : OtpClient {
    override val sms: SmsOtpClient = SmsOtpImpl.create(networkingClient)

    override suspend fun authenticate(request: OTPsAuthenticateRequest): OTPsAuthenticateResponse =
        withContext(Dispatchers.Default) {
            networkingClient.request {
                networkingClient.api.oTPsAuthenticate(request)
            }
        }

    internal companion object {
        fun create(networkingClient: ConsumerNetworkingClient): OtpImpl = OtpImpl(networkingClient)
    }
}

public class SmsOtpImpl private constructor(
    private val networkingClient: ConsumerNetworkingClient,
) : SmsOtpClient {
    override suspend fun loginOrCreate(request: OTPsSMSLoginOrCreateRequest): OTPsSMSLoginOrCreateResponse =
        withContext(Dispatchers.Default) {
            networkingClient.request {
                networkingClient.api.oTPsSMSLoginOrCreate(request)
            }
        }

    internal companion object {
        fun create(networkingClient: ConsumerNetworkingClient): SmsOtpImpl = SmsOtpImpl(networkingClient)
    }
}
