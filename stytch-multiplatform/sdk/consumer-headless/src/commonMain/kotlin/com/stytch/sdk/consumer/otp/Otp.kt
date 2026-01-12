package com.stytch.sdk.consumer.otp

import com.stytch.sdk.consumer.networking.NetworkingClient
import com.stytch.sdk.consumer.networking.OtpAuthenticateRequest
import com.stytch.sdk.consumer.networking.OtpAuthenticateResponse
import com.stytch.sdk.consumer.networking.OtpSmsLoginOrCreateRequest
import com.stytch.sdk.consumer.networking.OtpSmsLoginOrCreateResponse
import com.stytch.sdk.data.StytchResult
import com.stytch.sdk.networking.stytchNetworkRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
@JsName("Otp")
public interface Otp {
    public val sms: SmsOtp

    public suspend fun authenticate(request: OtpAuthenticateRequest): StytchResult<OtpAuthenticateResponse>
}

@JsExport
@JsName("SmsOtp")
public interface SmsOtp {
    public suspend fun loginOrCreate(request: OtpSmsLoginOrCreateRequest): StytchResult<OtpSmsLoginOrCreateResponse>
}

internal class OtpImpl(
    private val client: NetworkingClient,
) : Otp {
    override val sms: SmsOtp = SmsOtpImpl(client)

    override suspend fun authenticate(request: OtpAuthenticateRequest): StytchResult<OtpAuthenticateResponse> =
        withContext(Dispatchers.Default) {
            client.request {
                it.otpAuthenticate(request)
            }
        }
}

internal class SmsOtpImpl(
    private val client: NetworkingClient,
) : SmsOtp {
    override suspend fun loginOrCreate(request: OtpSmsLoginOrCreateRequest): StytchResult<OtpSmsLoginOrCreateResponse> =
        withContext(Dispatchers.Default) {
            client.request {
                it.otpSmsLoginOrCreate(request)
            }
        }
}
