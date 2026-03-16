package com.stytch.sdk.dfp
import com.stytch.sdk.StytchCAPTCHAProvider
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

@OptIn(ExperimentalForeignApi::class)
public class CAPTCHAProviderImpl : CAPTCHAProvider {
    private val provider = StytchCAPTCHAProvider.shared()

    override suspend fun getCAPTCHAToken(): String =
        suspendCancellableCoroutine { cont ->
            provider.executeRecaptchaWithCompletionHandler {
                cont.resume(it ?: "NO_CAPTCHA_RETURNED")
            }
        }

    override val isConfigured: Boolean
        get() = provider.isConfigured()

    override suspend fun initialize(siteKey: String) {
        suspendCancellableCoroutine { cont ->
            provider.setCaptchaClientWithSiteKey(siteKey) {
                cont.resume(Unit)
            }
        }
    }
}
