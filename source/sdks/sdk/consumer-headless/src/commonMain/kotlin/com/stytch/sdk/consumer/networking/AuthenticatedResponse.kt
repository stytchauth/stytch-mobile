package com.stytch.sdk.consumer.networking

import com.stytch.sdk.consumer.networking.models.ApiSessionV1Session
import com.stytch.sdk.consumer.networking.models.ApiUserV1User
import kotlin.js.JsExport

/** Common fields returned in every successful consumer authentication response. */
@JsExport
public interface AuthenticatedResponse {
    /** The authenticated user. */
    public val user: ApiUserV1User

    /** The opaque session token. */
    public val sessionToken: String

    /** The active session. */
    public val session: ApiSessionV1Session

    /** The session JWT. */
    public val sessionJwt: String
}
