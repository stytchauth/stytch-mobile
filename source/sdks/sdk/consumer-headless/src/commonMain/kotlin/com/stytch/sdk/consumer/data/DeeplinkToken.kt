package com.stytch.sdk.consumer.data

import kotlin.js.JsExport

/** A token parsed from a Stytch deeplink URL. */
@JsExport
public class DeeplinkToken(
    /** The type of the token. */
    public val type: ConsumerTokenType,
    /** The raw token value. */
    public val token: String,
)
