package com.stytch.sdk.data

import kotlin.js.JsExport

@JsExport
public interface BasicResponse : StytchAPIResponse {
    public val statusCode: Int
    public val requestId: String
}
