package com.stytch.sdk.data

import kotlin.js.JsExport

@JsExport
public data class PKCECodePair(
    public val challenge: String,
    public val verifier: String,
)
