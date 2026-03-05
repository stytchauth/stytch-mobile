package com.stytch.sdk.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public class StytchAPIError(
    @SerialName("status_code")
    public val statusCode: Int,
    @SerialName("request_id")
    public val requestId: String,
    @SerialName("error_message")
    public val errorMessage: String,
    @SerialName("error_type")
    public val errorType: String,
    @SerialName("error_url")
    public val errorUrl: String,
) : StytchError() {
    init {
        fixJsErrorMessage(errorMessage)
    }
    public fun isUnrecoverableError(): Boolean =
        errorType in
            listOf(
                "unauthorized_credentials",
                "user_unauthenticated",
                "invalid_secret_authentication",
                "session_not_found",
            )
}
