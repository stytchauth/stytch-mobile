package com.stytch.sdk.consumer.otp

import com.stytch.sdk.consumer.networking.NetworkingClient
import com.stytch.sdk.consumer.networking.Requests
import com.stytch.sdk.consumer.networking.Responses
import com.stytch.sdk.networking.stytchNetworkRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

public interface OTP {
    public val sms: SMS

    public interface SMS {
        public suspend fun loginOrCreate(request: Requests.OTP.SMS.LoginOrCreate): Result<Responses.OTP.SMS.LoginOrCreateResponse>
    }

    public suspend fun authenticate(request: Requests.OTP.Authenticate): Result<Responses.OTP.AuthenticateResponse>
}

internal class OTPImpl(
    private val client: NetworkingClient,
) : OTP {
    override val sms: OTP.SMS = SMSImpl(client)

    override suspend fun authenticate(request: Requests.OTP.Authenticate): Result<Responses.OTP.AuthenticateResponse> =
        withContext(Dispatchers.Default) {
            stytchNetworkRequest {
                client.api.otpAuthenticate(request)
            }
        }
}

internal class SMSImpl(
    private val client: NetworkingClient,
) : OTP.SMS {
    override suspend fun loginOrCreate(request: Requests.OTP.SMS.LoginOrCreate): Result<Responses.OTP.SMS.LoginOrCreateResponse> =
        withContext(Dispatchers.Default) {
            stytchNetworkRequest {
                client.api.otpSmsLoginOrCreate(request)
            }
        }
}
