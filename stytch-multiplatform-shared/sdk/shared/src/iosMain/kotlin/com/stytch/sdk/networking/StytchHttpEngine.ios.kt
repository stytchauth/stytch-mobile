package com.stytch.sdk.networking

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin

internal actual val StytchHttpEngine: HttpClientEngine = Darwin.create()
