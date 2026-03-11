package com.stytch.sdk.oauth

import android.app.Activity

public actual class B2BSSOStartParameters(
    public val activity: Activity,
    public actual val connectionId: String,
    public actual val loginRedirectUrl: String? = null,
    public actual val signupRedirectUrl: String? = null,
    public actual val sessionDurationMinutes: Int? = null,
) {
    public actual fun toOAuthStartParameters(): OAuthStartParameters = OAuthStartParameters(activity = activity)
}
