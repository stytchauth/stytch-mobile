package com.stytch.sdk.data

import kotlinx.serialization.Serializable

internal const val TEST_BASE_DOMAIN: String = "test.stytch.com"
internal const val LIVE_BASE_DOMAIN: String = "api.stytch.com"
internal const val DEFAULT_DFPPA_DOMAIN: String = "telemetry.stytch.com"

public const val SDK_URL_PATH: String = "sdk/v1/"

@Serializable
public data class EndpointOptions(
    val testDomain: String = TEST_BASE_DOMAIN,
    val liveDomain: String = LIVE_BASE_DOMAIN,
    val dfppaDomain: String = DEFAULT_DFPPA_DOMAIN,
)
