package com.stytch.sdk.data

import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
@JsName("StytchResult")
public sealed class StytchResult<out T> {
    public class Success<out T>(
        public val data: T,
    ) : StytchResult<T>()

    public class Error(
        public val exception: Throwable,
    ) : StytchResult<Nothing>()
}
