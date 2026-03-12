package com.stytch.sdk.networking

import io.ktor.client.plugins.ResponseException

public interface StytchNetworkResponseMiddleware {
    public suspend fun <T> onSuccess(data: T)

    public suspend fun onError(exception: ResponseException): Exception
}
