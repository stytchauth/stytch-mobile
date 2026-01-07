package com.stytch.sdk.data

import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
@JsName("StytchResult")
public sealed class StytchResult<out T> {
    public data class Success<out T>(
        val data: T,
    ) : StytchResult<T>()

    public data class Error(
        val exception: Throwable,
    ) : StytchResult<Nothing>()
}
