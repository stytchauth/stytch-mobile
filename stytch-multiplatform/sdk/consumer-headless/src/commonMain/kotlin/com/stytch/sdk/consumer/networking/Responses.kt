package com.stytch.sdk.consumer.networking

import com.stytch.sdk.data.BasicResponse
import com.stytch.sdk.data.StytchAPIResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.js.JsExport
import kotlin.time.Instant

public interface AuthenticatedResponse :
    BasicResponse,
    StytchAPIResponse {
    public val sessionToken: String
    public val sessionJwt: String
    public val user: User
    public val session: Session
}

@JsExport
@Serializable
public class OtpSmsLoginOrCreateResponse(
    @SerialName("status_code")
    public override val statusCode: Int,
    @SerialName("request_id")
    public override val requestId: String,
    @SerialName("method_id")
    public val methodId: String,
) : BasicResponse

@JsExport
@Serializable
public class OtpAuthenticateResponse(
    @SerialName("status_code")
    public override val statusCode: Int,
    @SerialName("request_id")
    public override val requestId: String,
    @SerialName("session_token")
    public override val sessionToken: String,
    @SerialName("session_jwt")
    public override val sessionJwt: String,
    public override val user: User,
    public override val session: Session,
) : AuthenticatedResponse

@JsExport
@Serializable
public class User(
    @SerialName("created_at")
    public val createdAt: Instant,
    @SerialName("user_id")
    public val userId: String,
    public val status: String,
    public val name: Name,
)

@JsExport
@Serializable
public class Name(
    @SerialName("first_name")
    public val firstName: String,
    @SerialName("middle_name")
    public val middleName: String,
    @SerialName("last_name")
    public val lastName: String,
)

@JsExport
@Serializable
public class Session(
    @SerialName("expires_at")
    public val expiresAt: Instant,
    @SerialName("last_accessed_at")
    public val lastAccessedAt: Instant,
    @SerialName("session_id")
    public val sessionId: String,
    @SerialName("started_at")
    public val startedAt: Instant,
    @SerialName("user_id")
    public val userId: String,
)

@JsExport
@Serializable
public class SessionsAuthenticateResponse(
    @SerialName("status_code")
    public override val statusCode: Int,
    @SerialName("request_id")
    public override val requestId: String,
    @SerialName("session_token")
    public override val sessionToken: String,
    @SerialName("session_jwt")
    public override val sessionJwt: String,
    public override val user: User,
    public override val session: Session,
) : AuthenticatedResponse

@JsExport
@Serializable
public class SessionsRevokeResponse(
    @SerialName("status_code")
    public override val statusCode: Int,
    @SerialName("request_id")
    public override val requestId: String,
) : BasicResponse
