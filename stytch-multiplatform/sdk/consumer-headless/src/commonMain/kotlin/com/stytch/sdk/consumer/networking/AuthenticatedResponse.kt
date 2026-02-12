package com.stytch.sdk.consumer.networking

import com.stytch.sdk.consumer.networking.models.ApiSessionV1Session
import com.stytch.sdk.consumer.networking.models.ApiUserV1User

internal interface AuthenticatedResponse {
    val user: ApiUserV1User
    val sessionToken: String

    val session: ApiSessionV1Session
    val sessionJwt: String
}
