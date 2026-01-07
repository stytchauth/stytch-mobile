package com.stytch.sdk.consumer.networking

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@JsExport
public object Requests {
    public object OTP {
        public object SMS {
            @Serializable
            public data class LoginOrCreate(
                @SerialName("phone_number")
                val phoneNumber: String,
                @SerialName("expiration_minutes")
                val expirationMinutes: Int?,
                @SerialName("enable_autofill")
                val enableAutofill: Boolean = false,
            )
        }

        @Serializable
        public data class Authenticate(
            val token: String,
            @SerialName("method_id")
            val methodId: String,
            @SerialName("session_duration_minutes")
            val sessionDurationMinutes: Int,
        )
    }
}
