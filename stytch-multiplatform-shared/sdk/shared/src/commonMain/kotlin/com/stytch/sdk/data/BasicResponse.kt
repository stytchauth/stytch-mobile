package com.stytch.sdk.data

public interface BasicResponse : StytchAPIResponse {
    public val statusCode: Int
    public val requestId: String
}
