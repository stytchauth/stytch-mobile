package com.stytch.sdk.consumer.networking

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@JsExport
@Serializable
public class OtpSmsLoginOrCreateRequest(
    @SerialName("phone_number")
    public val phoneNumber: String,
    @SerialName("expiration_minutes")
    public val expirationMinutes: Int?,
    @SerialName("enable_autofill")
    public val enableAutofill: Boolean = false,
)

@JsExport
@Serializable
public class OtpAuthenticateRequest(
    public val token: String,
    @SerialName("method_id")
    public val methodId: String,
    @SerialName("session_duration_minutes")
    public val sessionDurationMinutes: Int,
)

@JsExport
@Serializable
public class SessionsAuthenticateRequest(
    @SerialName("session_duration_minutes")
    public val sessionDurationMinutes: Int?,
)
