package com.stytch.sdk.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// RN stored the whole session state as a JSON string; we only care about one property, so the model only has the one we care about
@Serializable
internal data class ReactNativeSessionState(
    @SerialName("session_token")
    val sessionToken: String,
)
