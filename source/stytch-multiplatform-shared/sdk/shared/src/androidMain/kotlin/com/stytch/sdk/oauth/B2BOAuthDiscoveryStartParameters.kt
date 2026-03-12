package com.stytch.sdk.oauth

import android.app.Activity

public actual class B2BOAuthDiscoveryStartParameters(
    public val activity: Activity,
    public actual val discoveryRedirectUrl: String? = null,
    public actual val customScopes: List<String>? = null,
    public actual val providerParams: Map<String, String>? = null,
) {
    public actual fun toOAuthStartParameters(): OAuthStartParameters = OAuthStartParameters(activity = activity)
}
