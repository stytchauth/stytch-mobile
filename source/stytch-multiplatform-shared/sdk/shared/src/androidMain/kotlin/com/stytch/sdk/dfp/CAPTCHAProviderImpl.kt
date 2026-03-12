package com.stytch.sdk.dfp

import android.app.Application
import com.google.android.recaptcha.Recaptcha
import com.google.android.recaptcha.RecaptchaAction
import com.google.android.recaptcha.RecaptchaClient

public class CAPTCHAProviderImpl(
    private val application: Application,
) : CAPTCHAProvider {
    private lateinit var recaptchaClient: RecaptchaClient

    override suspend fun getCAPTCHAToken(): String =
        recaptchaClient.execute(RecaptchaAction.LOGIN).getOrElse {
            "NO_CAPTCHA_RETURNED"
        }

    override val isConfigured: Boolean
        get() = ::recaptchaClient.isInitialized

    override suspend fun initialize(siteKey: String) {
        recaptchaClient = Recaptcha.fetchClient(application, siteKey)
    }
}
