package com.stytch.sdk.consumer.networking

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.js.JsExport
import kotlin.time.Instant

public interface BasicResponse {
    public val statusCode: Int
    public val requestId: String
}

public interface AuthenticatedResponse {
    public val sessionToken: String
    public val sessionJwt: String
    public val user: User
    public val session: Session
}

@JsExport
@Serializable
public data class OtpSmsLoginOrCreateResponse(
    @SerialName("status_code")
    override val statusCode: Int,
    @SerialName("request_id")
    override val requestId: String,
    @SerialName("method_id")
    val methodId: String,
) : BasicResponse

@JsExport
@Serializable
public data class OtpAuthenticateResponse(
    @SerialName("status_code")
    override val statusCode: Int,
    @SerialName("request_id")
    override val requestId: String,
    @SerialName("session_token")
    override val sessionToken: String,
    @SerialName("session_jwt")
    override val sessionJwt: String,
    override val user: User,
    override val session: Session,
) : BasicResponse,
    AuthenticatedResponse

@JsExport
@Serializable
public data class User(
    @SerialName("created_at")
    val createdAt: Instant,
    @SerialName("user_id")
    val userId: String,
    val status: String,
    val name: Name,
)

@JsExport
@Serializable
public data class Name(
    @SerialName("first_name")
    val firstName: String,
    @SerialName("middle_name")
    val middleName: String,
    @SerialName("last_name")
    val lastName: String,
)

@JsExport
@Serializable
public data class Session(
    @SerialName("expires_at")
    val expiresAt: Instant,
    @SerialName("last_accessed_at")
    val lastAccessedAt: Instant,
    @SerialName("session_id")
    val sessionId: String,
    @SerialName("started_at")
    val startedAt: Instant,
    @SerialName("user_id")
    val userId: String,
)

@JsExport
@Serializable
public data class SessionsAuthenticateResponse(
    @SerialName("status_code")
    override val statusCode: Int,
    @SerialName("request_id")
    override val requestId: String,
    @SerialName("session_token")
    override val sessionToken: String,
    @SerialName("session_jwt")
    override val sessionJwt: String,
    override val user: User,
    override val session: Session,
) : BasicResponse,
    AuthenticatedResponse
