package com.stytch.sdk.consumer.networking

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

public object Responses {
    public object OTP {
        public object SMS {
            @Serializable
            public data class LoginOrCreateResponse(
                @SerialName("status_code")
                val statusCode: Int,
                @SerialName("request_id")
                val requestId: String,
                @SerialName("method_id")
                val methodId: String,
            )
        }

        @Serializable
        public data class AuthenticateResponse(
            @SerialName("status_code")
            val statusCode: Int,
            @SerialName("request_id")
            val requestId: String,
            @SerialName("session_token")
            val sessionToken: String,
            @SerialName("session_jwt")
            val sessionJwt: String,
        )
    }
}
