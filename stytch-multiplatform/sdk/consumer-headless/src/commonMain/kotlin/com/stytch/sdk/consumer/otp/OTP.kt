package com.stytch.sdk.consumer.otp

import com.stytch.sdk.consumer.networking.NetworkingClient
import com.stytch.sdk.consumer.networking.Requests
import com.stytch.sdk.consumer.networking.Responses
import com.stytch.sdk.data.StytchResult
import com.stytch.sdk.networking.stytchNetworkRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
@JsName("OTP")
public interface OTP {
    public val sms: SMS

    public suspend fun authenticate(request: Requests.OTP.Authenticate): StytchResult<Responses.OTP.AuthenticateResponse>
}

@JsExport
@JsName("SMS")
public interface SMS {
    public suspend fun loginOrCreate(request: Requests.OTP.SMS.LoginOrCreate): StytchResult<Responses.OTP.SMS.LoginOrCreateResponse>
}

internal class OTPImpl(
    private val client: NetworkingClient,
) : OTP {
    override val sms: SMS = SMSImpl(client)

    override suspend fun authenticate(request: Requests.OTP.Authenticate): StytchResult<Responses.OTP.AuthenticateResponse> =
        withContext(Dispatchers.Default) {
            stytchNetworkRequest {
                client.api.otpAuthenticate(request)
            }
        }
}

internal class SMSImpl(
    private val client: NetworkingClient,
) : SMS {
    override suspend fun loginOrCreate(request: Requests.OTP.SMS.LoginOrCreate): StytchResult<Responses.OTP.SMS.LoginOrCreateResponse> =
        withContext(Dispatchers.Default) {
            stytchNetworkRequest {
                client.api.otpSmsLoginOrCreate(request)
            }
        }
}
