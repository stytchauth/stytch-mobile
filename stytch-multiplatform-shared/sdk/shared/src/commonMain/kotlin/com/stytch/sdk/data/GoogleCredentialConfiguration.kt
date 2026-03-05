package com.stytch.sdk.data

import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@JsExport
@Serializable
public class GoogleCredentialConfiguration(
    public val googleClientId: String,
    public val autoSelectEnabled: Boolean = true,
)
