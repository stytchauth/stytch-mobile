package com.stytch.sdk.consumer.passkeys

import com.stytch.sdk.consumer.StytchConsumerAuthenticationStateManager
import com.stytch.sdk.consumer.networking.ConsumerNetworkingClient
import com.stytch.sdk.consumer.networking.models.IWebAuthnAuthenticateStartSecondaryParameters
import com.stytch.sdk.consumer.networking.models.IWebAuthnRegisterParameters
import com.stytch.sdk.consumer.networking.models.IWebAuthnRegisterStartParameters
import com.stytch.sdk.consumer.networking.models.IWebAuthnUpdateParameters
import com.stytch.sdk.consumer.networking.models.WebAuthnAuthenticateRequest
import com.stytch.sdk.consumer.networking.models.WebAuthnAuthenticateResponse
import com.stytch.sdk.consumer.networking.models.WebAuthnAuthenticateStartSecondaryParameters
import com.stytch.sdk.consumer.networking.models.WebAuthnRegisterRequest
import com.stytch.sdk.consumer.networking.models.WebAuthnRegisterResponse
import com.stytch.sdk.consumer.networking.models.WebAuthnRegisterStartParameters
import com.stytch.sdk.consumer.networking.models.WebAuthnUpdateResponse
import com.stytch.sdk.consumer.networking.models.toNetworkModel
import com.stytch.sdk.data.StytchDispatchers
import com.stytch.sdk.passkeys.IPasskeyProvider
import com.stytch.sdk.passkeys.PasskeysParameters
import com.stytch.sdk.passkeys.PasskeysUnsupportedError
import kotlinx.coroutines.withContext
import kotlin.js.JsExport

@JsExport
public interface PasskeysClient {
    public val isSupported: Boolean

    public suspend fun register(parameters: PasskeysParameters): WebAuthnRegisterResponse

    public suspend fun authenticate(parameters: PasskeysParameters): WebAuthnAuthenticateResponse

    public suspend fun update(
        id: String,
        request: IWebAuthnUpdateParameters,
    ): WebAuthnUpdateResponse
}

internal class PasskeysClientImpl(
    private val dispatchers: StytchDispatchers,
    private val networkingClient: ConsumerNetworkingClient,
    private val sessionManager: StytchConsumerAuthenticationStateManager,
    private val passkeyProvider: IPasskeyProvider,
) : PasskeysClient {
    override val isSupported: Boolean = passkeyProvider.isSupported

    override suspend fun register(parameters: PasskeysParameters): WebAuthnRegisterResponse {
        if (!isSupported) throw PasskeysUnsupportedError()
        return withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                val startResponse =
                    networkingClient.api.webAuthnRegisterStart(
                        WebAuthnRegisterStartParameters(domain = parameters.domain).toNetworkModel(
                            authenticatorType = "platform",
                            returnPasskeyCredentialOptions = true,
                        ),
                    )
                val credentials =
                    passkeyProvider.createPublicKeyCredential(
                        parameters = parameters,
                        dispatchers = dispatchers,
                        json = startResponse.data.publicKeyCredentialCreationOptions,
                    )
                networkingClient.api.webAuthnRegister(
                    WebAuthnRegisterRequest(
                        publicKeyCredential = credentials,
                        sessionDurationMinutes = parameters.sessionDurationMinutes,
                    ),
                )
            }
        }
    }

    override suspend fun authenticate(parameters: PasskeysParameters): WebAuthnAuthenticateResponse {
        if (!isSupported) throw PasskeysUnsupportedError()
        return withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                val startResponse =
                    if (sessionManager.currentSessionToken.isNullOrEmpty()) {
                        networkingClient.api.webAuthnAuthenticateStartPrimary(
                            WebAuthnAuthenticateStartSecondaryParameters(
                                domain = parameters.domain,
                            ).toNetworkModel(returnPasskeyCredentialOptions = true),
                        )
                    } else {
                        networkingClient.api.webAuthnAuthenticateStartSecondary(
                            WebAuthnAuthenticateStartSecondaryParameters(
                                domain = parameters.domain,
                            ).toNetworkModel(returnPasskeyCredentialOptions = true),
                        )
                    }
                val credentials =
                    passkeyProvider.getPublicKeyCredential(
                        parameters = parameters,
                        dispatchers = dispatchers,
                        json = startResponse.data.publicKeyCredentialRequestOptions,
                    )
                networkingClient.api.webAuthnAuthenticate(
                    WebAuthnAuthenticateRequest(
                        publicKeyCredential = credentials,
                        sessionDurationMinutes = parameters.sessionDurationMinutes,
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
