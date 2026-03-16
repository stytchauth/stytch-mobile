/*
 * Copyright 2014-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.stytch.sdk.networking.reactnative

import org.khronos.webgl.Int8Array
import org.khronos.webgl.Uint8Array

@Suppress("UnsafeCastFromDynamic")
internal fun Uint8Array.asByteArray(): ByteArray = Int8Array(buffer, byteOffset, length).asDynamic()
