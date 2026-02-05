package com.stytch.sdk.consumer.networking

import com.stytch.sdk.data.StytchDataResponse
import com.stytch.sdk.dfp.DFPPAEnabled
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST

internal interface API {
    // region Sessions
    @POST("sessions/authenticate")
    suspend fun sessionsAuthenticate(
        @Body body: SessionsAuthenticateRequest,
    ): StytchDataResponse<SessionsAuthenticateResponse>

    @POST("sessions/revoke")
    suspend fun sessionsRevoke(): StytchDataResponse<SessionsRevokeResponse>

    // endregion Sessions

    // region OTP
    @POST("otps/sms/login_or_create")
    @DFPPAEnabled
    suspend fun otpSmsLoginOrCreate(
        @Body request: OtpSmsLoginOrCreateRequest,
    ): StytchDataResponse<OtpSmsLoginOrCreateResponse>

    @POST("otps/authenticate")
    @DFPPAEnabled
    suspend fun otpAuthenticate(
        @Body request: OtpAuthenticateRequest,
    ): StytchDataResponse<OtpAuthenticateResponse>
    // endregion OTP
}
