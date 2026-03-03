package com.stytch.sdk.data

import kotlin.js.JsExport

@JsExport
public class GoogleCredentialConfiguration(
    public val googleClientId: String,
    public val autoSelectEnabled: Boolean = true,
)
