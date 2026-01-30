package com.stytch.sdk.dfp

import com.stytch.sdk.StytchBridge
import kotlinx.coroutines.await

public class CAPTCHAProviderImpl : CAPTCHAProvider {
    override suspend fun getCAPTCHAToken(): String = StytchBridge.getCAPTCHAToken().await()

    override val isConfigured: Boolean
        get() = true

    override suspend fun initialize(siteKey: String) {
        StytchBridge.configureCaptcha(siteKey)
    }
}
