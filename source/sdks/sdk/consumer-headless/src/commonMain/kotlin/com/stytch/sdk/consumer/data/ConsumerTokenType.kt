package com.stytch.sdk.consumer.data

import kotlin.js.JsExport

/** The type of token present in a Stytch deeplink URL. */
@JsExport
public enum class ConsumerTokenType {
    /** A magic link authentication token. */
    MAGIC_LINKS,

    /** An OAuth authentication token. */
    OAUTH,

    /** A password reset token. Must be handled manually by the caller via the passwords reset flow. */
    RESET_PASSWORD,

    /** An unrecognized or missing token type. */
    UNKNOWN,
    ;

    internal companion object {
        fun fromString(typeString: String?): ConsumerTokenType =
            try {
                ConsumerTokenType.valueOf(typeString?.uppercase()!!)
            } catch (_: Exception) {
                UNKNOWN
            }
    }
}
