@file:Suppress("ktlint:standard:filename")
// Because of the way the OpenAPI spec is generated, and how we have the SDKs split, there need to be duplicate interfaces for the opposite
// package in each (ie: an unused B2B AuthenticatedResponse in consumer, and an unused Consumer AuthenticatedResponse in b2b. It's solely
// for the code generation tasks

package com.stytch.sdk.b2b.networking

import com.stytch.sdk.consumer.networking.models.ApiB2bSessionV1MemberSession
import com.stytch.sdk.consumer.networking.models.ApiOrganizationV1Member
import com.stytch.sdk.consumer.networking.models.ApiOrganizationV1Organization

internal interface AuthenticatedResponse {
    val member: ApiOrganizationV1Member
    val memberSession: ApiB2bSessionV1MemberSession?
    val organization: ApiOrganizationV1Organization
    val sessionToken: String
    val sessionJwt: String
}
