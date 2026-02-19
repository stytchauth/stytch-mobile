package com.stytch.sdk.consumer.otp

import com.stytch.sdk.consumer.networking.ConsumerNetworkingClient
import com.stytch.sdk.consumer.networking.models.IOTPsAuthenticateParameters
import com.stytch.sdk.consumer.networking.models.IOTPsSMSLoginOrCreateParameters
import com.stytch.sdk.consumer.networking.models.OTPsAuthenticateResponse
import com.stytch.sdk.consumer.networking.models.OTPsSMSLoginOrCreateResponse
import com.stytch.sdk.consumer.networking.models.toNetworkModel
import com.stytch.sdk.data.StytchDispatchers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.js.JsExport

@JsExport
public interface OtpClient {
    public val sms: SmsOtpClient

    public suspend fun authenticate(request: IOTPsAuthenticateParameters): OTPsAuthenticateResponse
}

@JsExport
public interface SmsOtpClient {
    public suspend fun loginOrCreate(request: IOTPsSMSLoginOrCreateParameters): OTPsSMSLoginOrCreateResponse
}

public class OtpImpl private constructor(
    private val dispatchers: StytchDispatchers,
    private val networkingClient: ConsumerNetworkingClient,
) : OtpClient {
    override val sms: SmsOtpClient = SmsOtpImpl.create(dispatchers, networkingClient)

    override suspend fun authenticate(request: IOTPsAuthenticateParameters): OTPsAuthenticateResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.oTPsAuthenticate(request.toNetworkModel())
            }
        }

    internal companion object {
        fun create(
            dispatchers: StytchDispatchers,
            networkingClient: ConsumerNetworkingClient,
        ): OtpImpl = OtpImpl(dispatchers, networkingClient)
    }
}

public class SmsOtpImpl private constructor(
    private val dispatchers: StytchDispatchers,
    private val networkingClient: ConsumerNetworkingClient,
) : SmsOtpClient {
    override suspend fun loginOrCreate(request: IOTPsSMSLoginOrCreateParameters): OTPsSMSLoginOrCreateResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.oTPsSMSLoginOrCreate(request.toNetworkModel())
            }
        }

    internal companion object {
        fun create(
            dispatchers: StytchDispatchers,
            networkingClient: ConsumerNetworkingClient,
        ): SmsOtpImpl = SmsOtpImpl(dispatchers, networkingClient)
    }
}
