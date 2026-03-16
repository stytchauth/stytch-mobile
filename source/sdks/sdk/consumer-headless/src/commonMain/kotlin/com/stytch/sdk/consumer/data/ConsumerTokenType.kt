package com.stytch.sdk.consumer.data

import kotlin.js.JsExport

@JsExport
public enum class ConsumerTokenType {
    MAGIC_LINKS,
    OAUTH,
    RESET_PASSWORD,
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
