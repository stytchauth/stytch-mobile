package com.stytch.sdk.b2b.networking.models

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

internal fun Any?.toJsonElement(): JsonElement =
    when (this) {
        null -> JsonNull
        is JsonElement -> this
        is Boolean -> JsonPrimitive(this)
        is Number -> JsonPrimitive(this)
        is String -> JsonPrimitive(this)
        is Map<*, *> -> JsonObject(entries.associate { (k, v) -> k.toString() to v.toJsonElement() })
        is List<*> -> JsonArray(map { it.toJsonElement() })
        else -> JsonPrimitive(toString())
    }

internal fun Map<String, Any?>.toJsonElementMap(): Map<String, JsonElement> = mapValues { (_, v) -> v.toJsonElement() }
