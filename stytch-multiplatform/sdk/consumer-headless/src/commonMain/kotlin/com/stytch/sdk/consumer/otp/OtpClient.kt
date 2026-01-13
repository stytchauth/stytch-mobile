package com.stytch.sdk.consumer.otp

import com.stytch.sdk.consumer.networking.ConsumerNetworkingClient
import com.stytch.sdk.consumer.networking.OtpAuthenticateRequest
import com.stytch.sdk.consumer.networking.OtpAuthenticateResponse
import com.stytch.sdk.consumer.networking.OtpSmsLoginOrCreateRequest
import com.stytch.sdk.consumer.networking.OtpSmsLoginOrCreateResponse
import com.stytch.sdk.data.StytchResult
import com.stytch.sdk.networking.StytchNetworkingClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
@JsName("OtpClient")
public interface OtpClient {
    public val sms: SmsOtpClient

    public suspend fun authenticate(request: OtpAuthenticateRequest): StytchResult<OtpAuthenticateResponse>
}

@JsExport
@JsName("SmsOtpClient")
public interface SmsOtpClient {
    public suspend fun loginOrCreate(request: OtpSmsLoginOrCreateRequest): StytchResult<OtpSmsLoginOrCreateResponse>
}

public class OtpImpl private constructor(
    private val networkingClient: ConsumerNetworkingClient,
) : OtpClient {
    override val sms: SmsOtpClient = SmsOtpImpl.create(networkingClient)

    override suspend fun authenticate(request: OtpAuthenticateRequest): StytchResult<OtpAuthenticateResponse> =
        withContext(Dispatchers.Default) {
            networkingClient.request {
                networkingClient.api.otpAuthenticate(request)
            }
        }

    internal companion object {
        fun create(networkingClient: ConsumerNetworkingClient): OtpImpl = OtpImpl(networkingClient)
    }
}

public class SmsOtpImpl private constructor(
    private val networkingClient: ConsumerNetworkingClient,
) : SmsOtpClient {
    override suspend fun loginOrCreate(request: OtpSmsLoginOrCreateRequest): StytchResult<OtpSmsLoginOrCreateResponse> =
        withContext(Dispatchers.Default) {
            networkingClient.request {
                networkingClient.api.otpSmsLoginOrCreate(request)
            }
        }

    internal companion object {
        fun create(networkingClient: ConsumerNetworkingClient): SmsOtpImpl = SmsOtpImpl(networkingClient)
    }
}
