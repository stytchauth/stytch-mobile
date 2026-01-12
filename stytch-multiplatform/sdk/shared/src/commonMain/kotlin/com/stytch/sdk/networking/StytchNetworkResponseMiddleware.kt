package com.stytch.sdk.networking

import io.ktor.client.statement.HttpResponse

public interface StytchNetworkResponseMiddleware {
    public suspend fun <T> onSuccess(data: T)

    public suspend fun onError(response: HttpResponse): Exception
}
