package com.stytch.sdk.data

import kotlin.js.JsExport
import kotlin.js.JsName

internal const val TEST_BASE_DOMAIN: String = "test.stytch.com"
internal const val LIVE_BASE_DOMAIN: String = "api.stytch.com"
internal const val DEFAULT_DFPPA_DOMAIN: String = "telemetry.stytch.com"

public const val SDK_URL_PATH: String = "sdk/v1/"

@JsExport
@JsName("EndpointOptions")
public class EndpointOptions(
    public val testDomain: String = TEST_BASE_DOMAIN,
    public val liveDomain: String = LIVE_BASE_DOMAIN,
    public val dfppaDomain: String = DEFAULT_DFPPA_DOMAIN,
)
