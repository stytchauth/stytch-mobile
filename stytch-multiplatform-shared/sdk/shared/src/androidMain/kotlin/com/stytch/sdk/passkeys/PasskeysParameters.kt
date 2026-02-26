package com.stytch.sdk.passkeys

import android.app.Activity

public actual class PasskeysParameters(
    public val activity: Activity,
    public actual val domain: String,
    public actual val sessionDurationMinutes: Int? = null,
    public actual val preferImmediatelyAvailableCredentials: Boolean = true,
)
