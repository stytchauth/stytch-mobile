package com.stytch.sdk.networking

import com.stytch.sdk.networking.reactnative.ReactNativeClientEngine
import com.stytch.sdk.networking.reactnative.ReactNativeClientEngineConfig
import io.ktor.client.engine.HttpClientEngine

internal actual val StytchHttpEngine: HttpClientEngine = ReactNativeClientEngine(ReactNativeClientEngineConfig())
