@file:Suppress("ktlint:standard:filename")
// Because of the way the OpenAPI spec is generated, and how we have the SDKs split, there need to be duplicate interfaces for the opposite
// package in each (ie: an unused B2B AuthenticatedResponse in consumer, and an unused Consumer AuthenticatedResponse in b2b. It's solely
// for the code generation tasks

package com.stytch.sdk.consumer.networking

import com.stytch.sdk.b2b.networking.models.ApiSessionV1Session
import com.stytch.sdk.b2b.networking.models.ApiUserV1User

internal interface AuthenticatedResponse {
    val user: ApiUserV1User
    val sessionToken: String

    val session: ApiSessionV1Session
    val sessionJwt: String
}
