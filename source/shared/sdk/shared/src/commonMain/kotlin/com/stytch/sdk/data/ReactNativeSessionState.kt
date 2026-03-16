package com.stytch.sdk.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// RN stored the whole session state as a JSON string; we only care about two properties, so the model only has what we care about
// We also don't know if it's a b2b session or a consumer session (yet) so we can't serialize it properly, so just return it as a string
// and let the client that started this call deal with it. Fortunately, RN uses the same name for sessions when persisting them 😮‍💨
@Serializable
internal data class ReactNativeSessionState(
    @SerialName("session_token")
    val sessionToken: String,
    val session: String?,
)
