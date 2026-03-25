package com.stytch.sdk.consumer.otp

import com.stytch.sdk.StytchApi
import com.stytch.sdk.consumer.StytchConsumerAuthenticationStateManager
import com.stytch.sdk.consumer.networking.ConsumerNetworkingClient
import com.stytch.sdk.consumer.networking.models.IOTPsAuthenticateParameters
import com.stytch.sdk.consumer.networking.models.IOTPsEmailLoginOrCreateParameters
import com.stytch.sdk.consumer.networking.models.IOTPsEmailSendSecondaryParameters
import com.stytch.sdk.consumer.networking.models.IOTPsSMSLoginOrCreateParameters
import com.stytch.sdk.consumer.networking.models.IOTPsSMSSendSecondaryParameters
import com.stytch.sdk.consumer.networking.models.IOTPsWhatsAppLoginOrCreateParameters
import com.stytch.sdk.consumer.networking.models.IOTPsWhatsAppSendSecondaryParameters
import com.stytch.sdk.consumer.networking.models.OTPsAuthenticateResponse
import com.stytch.sdk.consumer.networking.models.OTPsEmailLoginOrCreateResponse
import com.stytch.sdk.consumer.networking.models.OTPsEmailSendSecondaryResponse
import com.stytch.sdk.consumer.networking.models.OTPsSMSLoginOrCreateResponse
import com.stytch.sdk.consumer.networking.models.OTPsSMSSendSecondaryResponse
import com.stytch.sdk.consumer.networking.models.OTPsWhatsAppLoginOrCreateResponse
import com.stytch.sdk.consumer.networking.models.OTPsWhatsAppSendSecondaryResponse
import com.stytch.sdk.consumer.networking.models.toNetworkModel
import com.stytch.sdk.data.StytchDispatchers
import com.stytch.sdk.data.StytchError
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException
import kotlin.js.JsExport

/** OTP (one-time passcode) authentication via SMS, email, or WhatsApp. */
@StytchApi
@JsExport
public interface OtpClient {
    /** SMS OTP methods. */
    public val sms: SmsOtpClient

    /** Email OTP methods. */
    public val email: EmailOtpClient

    /** WhatsApp OTP methods. */
    public val whatsapp: WhatsAppOtpClient

    /**
     * Authenticates a one-time passcode entered by the user, establishing a session.
     * Calls the `POST /sdk/v1/otps/authenticate` endpoint.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchConsumer.otps.authenticate(
     *     OTPsAuthenticateParameters(
     *         token = "123456",
     *         methodId = "phone-number-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *         sessionDurationMinutes = 30,
     *     )
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = OTPsAuthenticateParameters(
     *     token: "123456",
     *     methodId: "phone-number-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *     sessionDurationMinutes: 30
     * )
     * let response = try await StytchConsumer.otps.authenticate(params)
     * ```
     *
     * **React Native:**
     * ```js
     * StytchConsumer.otps.authenticate({
     *     token: "123456",
     *     methodId: "phone-number-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *     sessionDurationMinutes: 30,
     * })
     * ```
     *
     * @param request - [IOTPsAuthenticateParameters]
     *   - `token` — The OTP code entered by the user.
     *   - `methodId` — The method ID returned from the send or loginOrCreate call (e.g. `phone-number-xxx` or `email-xxx`).
     *   - `sessionDurationMinutes` — Duration of the session to create, in minutes.
     *
     * @return [OTPsAuthenticateResponse] containing the authenticated session and user.
     *
     * @throws [StytchError] if the code is invalid, expired, or the method ID does not match.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun authenticate(request: IOTPsAuthenticateParameters): OTPsAuthenticateResponse
}

/** SMS OTP methods. */
@StytchApi
@JsExport
public interface SmsOtpClient {
    /**
     * Sends a one-time passcode via SMS to the provided phone number, logging in an existing user
     * or creating a new one. Calls the `POST /sdk/v1/otps/sms/login_or_create` endpoint.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchConsumer.otps.sms.loginOrCreate(
     *     OTPsSMSLoginOrCreateParameters(phoneNumber = "+15005550006")
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = OTPsSMSLoginOrCreateParameters(phoneNumber: "+15005550006")
     * let response = try await StytchConsumer.otps.sms.loginOrCreate(params)
     * ```
     *
     * **React Native:**
     * ```js
     * StytchConsumer.otps.sms.loginOrCreate({ phoneNumber: "+15005550006" })
     * ```
     *
     * @param request - [IOTPsSMSLoginOrCreateParameters]
     *   - `phoneNumber` — The phone number to send the OTP to, in E.164 format (e.g. `"+15005550006"`).
     *   - `expirationMinutes?` — Expiration for the OTP, in minutes.
     *   - `locale?` — Locale used for the SMS message (e.g. `"en"`).
     *   - `enableAutofill?` — Whether to enable OS-level OTP autofill hints on supported platforms.
     *
     * @return [OTPsSMSLoginOrCreateResponse] containing the `methodId` needed for [OtpClient.authenticate].
     *
     * @throws [StytchError] if the request fails.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun loginOrCreate(request: IOTPsSMSLoginOrCreateParameters): OTPsSMSLoginOrCreateResponse

    /**
     * Sends a one-time passcode via SMS to an existing user's phone number. Routes to
     * `POST /sdk/v1/otps/sms/send/primary` if no session is active, or
     * `POST /sdk/v1/otps/sms/send/secondary` if a session exists (to add an additional auth factor).
     *
     * **Kotlin:**
     * ```kotlin
     * StytchConsumer.otps.sms.send(
     *     OTPsSMSSendSecondaryParameters(phoneNumber = "+15005550006")
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = OTPsSMSSendSecondaryParameters(phoneNumber: "+15005550006")
     * let response = try await StytchConsumer.otps.sms.send(params)
     * ```
     *
     * **React Native:**
     * ```js
     * StytchConsumer.otps.sms.send({ phoneNumber: "+15005550006" })
     * ```
     *
     * @param request - [IOTPsSMSSendSecondaryParameters]
     *   - `phoneNumber` — The phone number to send the OTP to, in E.164 format (e.g. `"+15005550006"`).
     *   - `expirationMinutes?` — Expiration for the OTP, in minutes.
     *   - `locale?` — Locale used for the SMS message (e.g. `"en"`).
     *   - `enableAutofill?` — Whether to enable OS-level OTP autofill hints on supported platforms.
     *
     * @return [OTPsSMSSendSecondaryResponse] containing the `methodId` needed for [OtpClient.authenticate].
     *
     * @throws [StytchError] if the request fails.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun send(request: IOTPsSMSSendSecondaryParameters): OTPsSMSSendSecondaryResponse
}

/** Email OTP methods. */
@StytchApi
@JsExport
public interface EmailOtpClient {
    /**
     * Sends a one-time passcode via email to the provided address, logging in an existing user
     * or creating a new one. Calls the `POST /sdk/v1/otps/email/login_or_create` endpoint.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchConsumer.otps.email.loginOrCreate(
     *     OTPsEmailLoginOrCreateParameters(email = "user@example.com")
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = OTPsEmailLoginOrCreateParameters(email: "user@example.com")
     * let response = try await StytchConsumer.otps.email.loginOrCreate(params)
     * ```
     *
     * **React Native:**
     * ```js
     * StytchConsumer.otps.email.loginOrCreate({ email: "user@example.com" })
     * ```
     *
     * @param request - [IOTPsEmailLoginOrCreateParameters]
     *   - `email` — The email address to send the OTP to.
     *   - `expirationMinutes?` — Expiration for the OTP, in minutes.
     *   - `signupTemplateId?` — Custom email template ID to use for new-user emails.
     *   - `loginTemplateId?` — Custom email template ID to use for returning-user emails.
     *   - `locale?` — Locale used for the email content (e.g. `"en"`).
     *
     * @return [OTPsEmailLoginOrCreateResponse] containing the `methodId` needed for [OtpClient.authenticate].
     *
     * @throws [StytchError] if the request fails.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun loginOrCreate(request: IOTPsEmailLoginOrCreateParameters): OTPsEmailLoginOrCreateResponse

    /**
     * Sends a one-time passcode via email to an existing user's email address. Routes to
     * `POST /sdk/v1/otps/email/send/primary` if no session is active, or
     * `POST /sdk/v1/otps/email/send/secondary` if a session exists (to add an additional auth factor).
     *
     * **Kotlin:**
     * ```kotlin
     * StytchConsumer.otps.email.send(
     *     OTPsEmailSendSecondaryParameters(email = "user@example.com")
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = OTPsEmailSendSecondaryParameters(email: "user@example.com")
     * let response = try await StytchConsumer.otps.email.send(params)
     * ```
     *
     * **React Native:**
     * ```js
     * StytchConsumer.otps.email.send({ email: "user@example.com" })
     * ```
     *
     * @param request - [IOTPsEmailSendSecondaryParameters]
     *   - `email` — The email address to send the OTP to.
     *   - `expirationMinutes?` — Expiration for the OTP, in minutes.
     *   - `signupTemplateId?` — Custom email template ID to use for new-user emails.
     *   - `loginTemplateId?` — Custom email template ID to use for returning-user emails.
     *   - `locale?` — Locale used for the email content (e.g. `"en"`).
     *
     * @return [OTPsEmailSendSecondaryResponse] containing the `methodId` needed for [OtpClient.authenticate].
     *
     * @throws [StytchError] if the request fails.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun send(request: IOTPsEmailSendSecondaryParameters): OTPsEmailSendSecondaryResponse
}

/** WhatsApp OTP methods. */
@StytchApi
@JsExport
public interface WhatsAppOtpClient {
    /**
     * Sends a one-time passcode via WhatsApp to the provided phone number, logging in an existing user
     * or creating a new one. Calls the `POST /sdk/v1/otps/whatsapp/login_or_create` endpoint.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchConsumer.otps.whatsapp.loginOrCreate(
     *     OTPsWhatsAppLoginOrCreateParameters(phoneNumber = "+15005550006")
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = OTPsWhatsAppLoginOrCreateParameters(phoneNumber: "+15005550006")
     * let response = try await StytchConsumer.otps.whatsapp.loginOrCreate(params)
     * ```
     *
     * **React Native:**
     * ```js
     * StytchConsumer.otps.whatsapp.loginOrCreate({ phoneNumber: "+15005550006" })
     * ```
     *
     * @param request - [IOTPsWhatsAppLoginOrCreateParameters]
     *   - `phoneNumber` — The phone number to send the OTP to, in E.164 format (e.g. `"+15005550006"`).
     *   - `expirationMinutes?` — Expiration for the OTP, in minutes.
     *   - `locale?` — Locale used for the message (e.g. `"en"`).
     *
     * @return [OTPsWhatsAppLoginOrCreateResponse] containing the `methodId` needed for [OtpClient.authenticate].
     *
     * @throws [StytchError] if the request fails.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun loginOrCreate(request: IOTPsWhatsAppLoginOrCreateParameters): OTPsWhatsAppLoginOrCreateResponse

    /**
     * Sends a one-time passcode via WhatsApp to an existing user's phone number. Routes to
     * `POST /sdk/v1/otps/whatsapp/send/primary` if no session is active, or
     * `POST /sdk/v1/otps/whatsapp/send/secondary` if a session exists (to add an additional auth factor).
     *
     * **Kotlin:**
     * ```kotlin
     * StytchConsumer.otps.whatsapp.send(
     *     OTPsWhatsAppSendSecondaryParameters(phoneNumber = "+15005550006")
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = OTPsWhatsAppSendSecondaryParameters(phoneNumber: "+15005550006")
     * let response = try await StytchConsumer.otps.whatsapp.send(params)
     * ```
     *
     * **React Native:**
     * ```js
     * StytchConsumer.otps.whatsapp.send({ phoneNumber: "+15005550006" })
     * ```
     *
     * @param request - [IOTPsWhatsAppSendSecondaryParameters]
     *   - `phoneNumber` — The phone number to send the OTP to, in E.164 format (e.g. `"+15005550006"`).
     *   - `expirationMinutes?` — Expiration for the OTP, in minutes.
     *   - `locale?` — Locale used for the message (e.g. `"en"`).
     *
     * @return [OTPsWhatsAppSendSecondaryResponse] containing the `methodId` needed for [OtpClient.authenticate].
     *
     * @throws [StytchError] if the request fails.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun send(request: IOTPsWhatsAppSendSecondaryParameters): OTPsWhatsAppSendSecondaryResponse
}

internal class OtpImpl(
    private val dispatchers: StytchDispatchers,
    private val networkingClient: ConsumerNetworkingClient,
    private val sessionManager: StytchConsumerAuthenticationStateManager,
) : OtpClient {
    override val sms: SmsOtpClient = SmsOtpImpl(dispatchers, networkingClient, sessionManager)
    override val email: EmailOtpClient = EmailOtpImpl(dispatchers, networkingClient, sessionManager)
    override val whatsapp: WhatsAppOtpClient = WhatsAppOtpImpl(dispatchers, networkingClient, sessionManager)

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun authenticate(request: IOTPsAuthenticateParameters): OTPsAuthenticateResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.oTPsAuthenticate(request.toNetworkModel())
            }
        }
}

internal class SmsOtpImpl(
    private val dispatchers: StytchDispatchers,
    private val networkingClient: ConsumerNetworkingClient,
    private val sessionManager: StytchConsumerAuthenticationStateManager,
) : SmsOtpClient {
    @Throws(StytchError::class, CancellationException::class)
    override suspend fun loginOrCreate(request: IOTPsSMSLoginOrCreateParameters): OTPsSMSLoginOrCreateResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.oTPsSMSLoginOrCreate(request.toNetworkModel())
            }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun send(request: IOTPsSMSSendSecondaryParameters): OTPsSMSSendSecondaryResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                if (sessionManager.currentSessionToken.isNullOrEmpty()) {
                    networkingClient.api.oTPsSMSSendPrimary(request.toNetworkModel())
                } else {
                    networkingClient.api.oTPsSMSSendSecondary(request.toNetworkModel())
                }
            }
        }
}

internal class WhatsAppOtpImpl(
    private val dispatchers: StytchDispatchers,
    private val networkingClient: ConsumerNetworkingClient,
    private val sessionManager: StytchConsumerAuthenticationStateManager,
) : WhatsAppOtpClient {
    @Throws(StytchError::class, CancellationException::class)
    override suspend fun loginOrCreate(request: IOTPsWhatsAppLoginOrCreateParameters): OTPsWhatsAppLoginOrCreateResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.oTPsWhatsAppLoginOrCreate(request.toNetworkModel())
            }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun send(request: IOTPsWhatsAppSendSecondaryParameters): OTPsWhatsAppSendSecondaryResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                if (sessionManager.currentSessionToken.isNullOrEmpty()) {
                    networkingClient.api.oTPsWhatsAppSendPrimary(request.toNetworkModel())
                } else {
                    networkingClient.api.oTPsWhatsAppSendSecondary(request.toNetworkModel())
                }
            }
        }
}

internal class EmailOtpImpl(
    private val dispatchers: StytchDispatchers,
    private val networkingClient: ConsumerNetworkingClient,
    private val sessionManager: StytchConsumerAuthenticationStateManager,
) : EmailOtpClient {
    @Throws(StytchError::class, CancellationException::class)
    override suspend fun loginOrCreate(request: IOTPsEmailLoginOrCreateParameters): OTPsEmailLoginOrCreateResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.oTPsEmailLoginOrCreate(request.toNetworkModel())
            }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun send(request: IOTPsEmailSendSecondaryParameters): OTPsEmailSendSecondaryResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                if (sessionManager.currentSessionToken.isNullOrEmpty()) {
                    networkingClient.api.oTPsEmailSendPrimary(request.toNetworkModel())
                } else {
                    networkingClient.api.oTPsEmailSendSecondary(request.toNetworkModel())
                }
            }
        }
}
