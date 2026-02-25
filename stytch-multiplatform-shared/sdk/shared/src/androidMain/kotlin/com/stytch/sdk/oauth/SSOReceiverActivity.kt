package com.stytch.sdk.oauth

import android.app.Activity
import android.os.Bundle

internal class SSOReceiverActivity : Activity() {
    override fun onCreate(savedInstanceBundle: Bundle?) {
        super.onCreate(savedInstanceBundle)
        startActivity(SSOManagerActivity.Companion.createResponseHandlingIntent(this, intent.data))
        finish()
    }
}
