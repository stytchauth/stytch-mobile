package com.stytch.sdk.consumer.otp

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
import com.stytch.sdk.StytchApi

@StytchApi
@JsExport
public interface OtpClient {
    public val sms: SmsOtpClient
    public val email: EmailOtpClient
    public val whatsapp: WhatsAppOtpClient

    @Throws(StytchError::class, CancellationException::class)
    public suspend fun authenticate(request: IOTPsAuthenticateParameters): OTPsAuthenticateResponse
}

@StytchApi
@JsExport
public interface SmsOtpClient {
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun loginOrCreate(request: IOTPsSMSLoginOrCreateParameters): OTPsSMSLoginOrCreateResponse

    @Throws(StytchError::class, CancellationException::class)
    public suspend fun send(request: IOTPsSMSSendSecondaryParameters): OTPsSMSSendSecondaryResponse
}

@StytchApi
@JsExport
public interface EmailOtpClient {
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun loginOrCreate(request: IOTPsEmailLoginOrCreateParameters): OTPsEmailLoginOrCreateResponse

    @Throws(StytchError::class, CancellationException::class)
    public suspend fun send(request: IOTPsEmailSendSecondaryParameters): OTPsEmailSendSecondaryResponse
}

@StytchApi
@JsExport
public interface WhatsAppOtpClient {
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun loginOrCreate(request: IOTPsWhatsAppLoginOrCreateParameters): OTPsWhatsAppLoginOrCreateResponse

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
