package com.stytch.sdk.consumer.networking

import com.stytch.sdk.data.StytchDataResponse
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST

internal interface API {
    // region OTP
    @POST("otps/sms/login_or_create")
    suspend fun otpSmsLoginOrCreate(
        @Body request: OtpSmsLoginOrCreateRequest,
    ): StytchDataResponse<OtpSmsLoginOrCreateResponse>

    @POST("otps/authenticate")
    suspend fun otpAuthenticate(
        @Body request: OtpAuthenticateRequest,
    ): StytchDataResponse<OtpAuthenticateResponse>
    // endregion OTP
}
