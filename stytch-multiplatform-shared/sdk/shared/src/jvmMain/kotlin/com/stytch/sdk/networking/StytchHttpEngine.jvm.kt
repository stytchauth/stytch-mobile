package com.stytch.sdk.networking

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp

internal actual val StytchHttpEngine: HttpClientEngine = OkHttp.create()
