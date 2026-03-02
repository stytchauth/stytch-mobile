package com.stytch.sdk.utils

import kotlinx.serialization.json.Json

public class JsonSerDeHelper {
    public inline fun <reified T> encodeToString(data: T): String = Json.encodeToString(data)

    public inline fun <reified T> decodeFromString(data: String): T = Json.decodeFromString(data)
}
