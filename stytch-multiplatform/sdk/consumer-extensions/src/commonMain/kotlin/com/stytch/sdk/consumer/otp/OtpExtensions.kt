package com.stytch.sdk.consumer.otp

import com.stytch.sdk.consumer.networking.OtpAuthenticateRequest
import com.stytch.sdk.consumer.networking.OtpAuthenticateResponse
import com.stytch.sdk.consumer.networking.OtpSmsLoginOrCreateRequest
import com.stytch.sdk.consumer.networking.OtpSmsLoginOrCreateResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

public fun OtpClient.authenticate(
    request: OtpAuthenticateRequest,
    callback: (OtpAuthenticateResponse) -> Unit,
) {
    CoroutineScope(Dispatchers.Main).launch {
        callback(authenticate(request))
    }
}

public fun SmsOtpClient.loginOrCreate(
    request: OtpSmsLoginOrCreateRequest,
    callback: (OtpSmsLoginOrCreateResponse) -> Unit,
) {
    CoroutineScope(Dispatchers.Main).launch {
        callback(loginOrCreate(request))
    }
}
