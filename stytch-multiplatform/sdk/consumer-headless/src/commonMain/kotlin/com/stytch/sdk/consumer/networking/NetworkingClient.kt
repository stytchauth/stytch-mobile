package com.stytch.sdk.consumer.networking

import com.stytch.sdk.data.SDK_URL_PATH
import com.stytch.sdk.data.StytchClientConfiguration
import com.stytch.sdk.networking.getStytchNetworkingClient
import de.jensklingenberg.ktorfit.Ktorfit

internal class NetworkingClient(
    configuration: StytchClientConfiguration,
    getSessionToken: () -> String?,
) {
    internal val api: API

    init {
        val endpointOptions = configuration.endpointOptions
        val domain = if (configuration.tokenInfo.isTestToken) endpointOptions.testDomain else endpointOptions.liveDomain
        val ktorfit =
            Ktorfit
                .Builder()
                .baseUrl("https://$domain/$SDK_URL_PATH")
                .httpClient(getStytchNetworkingClient(configuration, getSessionToken))
                .build()
        api = ktorfit.createAPI()
    }
}
