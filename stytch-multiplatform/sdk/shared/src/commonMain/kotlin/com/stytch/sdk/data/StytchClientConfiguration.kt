package com.stytch.sdk.data

import kotlinx.serialization.Serializable

@Serializable
public data class StytchClientConfiguration(
    val publicToken: String,
    val endpointOptions: EndpointOptions = EndpointOptions(),
) {
    public val isTestToken: Boolean

    init {
        val matches = pattern.find(publicToken)
        require(matches != null) { "Invalid public token provided: $publicToken" }
        isTestToken = matches.groupValues[1] == "test"
    }

    internal companion object {
        private val pattern = Regex("^public-token-(test|live)-[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")
    }
}
