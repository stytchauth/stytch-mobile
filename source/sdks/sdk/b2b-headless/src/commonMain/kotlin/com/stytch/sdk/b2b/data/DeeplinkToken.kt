package com.stytch.sdk.b2b.data

import kotlin.js.JsExport

@JsExport
public class DeeplinkToken(
    public val type: B2BTokenType,
    public val token: String,
)
