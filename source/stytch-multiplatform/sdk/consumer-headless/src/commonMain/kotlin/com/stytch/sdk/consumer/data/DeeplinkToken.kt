package com.stytch.sdk.consumer.data

import kotlin.js.JsExport

@JsExport
public class DeeplinkToken(
    public val type: ConsumerTokenType,
    public val token: String,
)
