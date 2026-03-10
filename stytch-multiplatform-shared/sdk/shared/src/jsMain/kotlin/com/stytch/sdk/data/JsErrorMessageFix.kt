package com.stytch.sdk.data

internal actual fun Any.fixJsErrorMessage(message: String) {
    asDynamic().message = message
}
