package com.stytch.sdk.networking

import io.ktor.client.engine.HttpClientEngine

/**
 * The standard way of doing this is to _not_ pass a direct HttpEngine in and let Ktor determine the correct engine at
 * runtime. HOWEVER, we need a slightly modified JS-engine, so here we are explicitly adding engines per-platform.
 * ALl of this because React Native doesn't provide a W3C-compliant fetch implementation 🙃
 */

internal expect val StytchHttpEngine: HttpClientEngine
