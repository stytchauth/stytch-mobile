package com.stytch.sdk.consumer.passkeys

import com.stytch.sdk.consumer.StytchConsumerAuthenticationStateManager
import com.stytch.sdk.consumer.networking.ConsumerNetworkingClient
import com.stytch.sdk.consumer.networking.models.IWebAuthnAuthenticateStartSecondaryParameters
import com.stytch.sdk.consumer.networking.models.IWebAuthnRegisterStartParameters
import com.stytch.sdk.consumer.networking.models.IWebAuthnUpdateParameters
import com.stytch.sdk.consumer.networking.models.WebAuthnAuthenticateRequest
import com.stytch.sdk.consumer.networking.models.WebAuthnAuthenticateResponse
import com.stytch.sdk.consumer.networking.models.WebAuthnRegisterRequest
import com.stytch.sdk.consumer.networking.models.WebAuthnRegisterResponse
import com.stytch.sdk.consumer.networking.models.WebAuthnUpdateResponse
import com.stytch.sdk.consumer.networking.models.toNetworkModel
import com.stytch.sdk.data.StytchDispatchers
import com.stytch.sdk.passkeys.PasskeyProvider
import com.stytch.sdk.passkeys.PasskeysUnsupportedError
import kotlinx.coroutines.withContext
import kotlin.js.JsExport

@JsExport
public interface PasskeysClient {
    public val isSupported: Boolean

    public suspend fun register(
        request: IWebAuthnRegisterStartParameters,
        preferImmediatelyAvailableCredentials: Boolean,
    ): WebAuthnRegisterResponse

    public suspend fun authenticate(
        request: IWebAuthnAuthenticateStartSecondaryParameters,
        preferImmediatelyAvailableCredentials: Boolean,
    ): WebAuthnAuthenticateResponse

    public suspend fun update(
        id: String,
        request: IWebAuthnUpdateParameters,
    ): WebAuthnUpdateResponse
}

internal class PasskeysClientImpl(
    private val dispatchers: StytchDispatchers,
    private val networkingClient: ConsumerNetworkingClient,
    private val sessionManager: StytchConsumerAuthenticationStateManager,
    private val passkeyProvider: PasskeyProvider,
) : PasskeysClient {
    override val isSupported: Boolean = passkeyProvider.isSupported

    override suspend fun register(
        request: IWebAuthnRegisterStartParameters,
        preferImmediatelyAvailableCredentials: Boolean,
    ): WebAuthnRegisterResponse {
        if (!isSupported) throw PasskeysUnsupportedError()
        return withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                val startResponse = networkingClient.api.webAuthnRegisterStart(request.toNetworkModel())
                val credentials =
                    passkeyProvider.createPublicKeyCredential(
                        json = startResponse.data.publicKeyCredentialCreationOptions,
                        preferImmediatelyAvailableCredentials = preferImmediatelyAvailableCredentials,
                    )
                networkingClient.api.webAuthnRegister(
                    WebAuthnRegisterRequest(
                        publicKeyCredential = credentials,
                    ),
                )
            }
        }
    }

    override suspend fun authenticate(
        request: IWebAuthnAuthenticateStartSecondaryParameters,
        preferImmediatelyAvailableCredentials: Boolean,
    ): WebAuthnAuthenticateResponse {
        if (!isSupported) throw PasskeysUnsupportedError()
        return withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                val startResponse =
                    if (sessionManager.currentSessionToken.isNullOrEmpty()) {
                        networkingClient.api.webAuthnAuthenticateStartPrimary(request.toNetworkModel())
                    } else {
                        networkingClient.api.webAuthnAuthenticateStartSecondary(request.toNetworkModel())
                    }
                val credentials =
                    passkeyProvider.getPublicKeyCredential(
                        json = startResponse.data.publicKeyCredentialRequestOptions,
                        preferImmediatelyAvailableCredentials = preferImmediatelyAvailableCredentials,
                    )
                networkingClient.api.webAuthnAuthenticate(
                    WebAuthnAuthenticateRequest(
                        publicKeyCredential = credentials,
                    ),
                )
            }
        }
    }

    override suspend fun update(
        id: String,
        request: IWebAuthnUpdateParameters,
    ): WebAuthnUpdateResponse {
        if (!isSupported) throw PasskeysUnsupportedError()
        return withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.webAuthnUpdate(id, request.toNetworkModel())
            }
        }
    }
}
