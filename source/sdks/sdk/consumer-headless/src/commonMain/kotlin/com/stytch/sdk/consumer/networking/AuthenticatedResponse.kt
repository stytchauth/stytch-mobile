package com.stytch.sdk.consumer.networking

import com.stytch.sdk.consumer.networking.models.ApiSessionV1Session
import com.stytch.sdk.consumer.networking.models.ApiUserV1User
import kotlin.js.JsExport

@JsExport
public interface AuthenticatedResponse {
    public val user: ApiUserV1User
    public val sessionToken: String

    public val session: ApiSessionV1Session
    public val sessionJwt: String
}
