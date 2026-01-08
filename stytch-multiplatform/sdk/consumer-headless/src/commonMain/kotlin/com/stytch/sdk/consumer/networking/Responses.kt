package com.stytch.sdk.consumer.networking

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@JsExport
@Serializable
public data class OtpSmsLoginOrCreateResponse(
    @SerialName("status_code")
    val statusCode: Int,
    @SerialName("request_id")
    val requestId: String,
    @SerialName("method_id")
    val methodId: String,
)

@JsExport
@Serializable
public data class OtpAuthenticateResponse(
    @SerialName("status_code")
    val statusCode: Int,
    @SerialName("request_id")
    val requestId: String,
    @SerialName("session_token")
    val sessionToken: String,
    @SerialName("session_jwt")
    val sessionJwt: String,
)
