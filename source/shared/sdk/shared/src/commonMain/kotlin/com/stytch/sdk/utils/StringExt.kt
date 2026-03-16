package com.stytch.sdk.utils

public fun String.stytchUrlEncode(): String =
    this
        .replace("+", "-")
        .replace("/", "_")
        .replace("=", "")
