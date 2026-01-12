package com.stytch.sdk.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class StytchAPIError(
    @SerialName("error_message")
    val errorMessage: String,
    @SerialName("error_type")
    val errorType: String,
    @SerialName("error_url")
    val errorUrl: String,
) : Exception(errorMessage) {
    public fun isUnrecoverableError(): Boolean =
        errorType in
            listOf(
                "unauthorized_credentials",
                "user_unauthenticated",
                "invalid_secret_authentication",
                "session_not_found",
            )
}
