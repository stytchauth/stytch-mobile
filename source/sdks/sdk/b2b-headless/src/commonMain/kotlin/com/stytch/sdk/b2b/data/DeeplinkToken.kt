package com.stytch.sdk.b2b.data

import kotlin.js.JsExport

/** A token parsed from a Stytch B2B deeplink URL. */
@JsExport
public class DeeplinkToken(
    /** The type of the token. */
    public val type: B2BTokenType,
    /** The raw token value. */
    public val token: String,
)
