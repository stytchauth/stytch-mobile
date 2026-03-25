package com.stytch.sdk.consumer.passkeys

import com.stytch.sdk.StytchApi
import com.stytch.sdk.consumer.StytchConsumerAuthenticationStateManager
import com.stytch.sdk.consumer.networking.ConsumerNetworkingClient
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
import com.stytch.sdk.data.StytchError
import com.stytch.sdk.passkeys.IPasskeyProvider
import com.stytch.sdk.passkeys.PasskeysParameters
import com.stytch.sdk.passkeys.PasskeysUnsupportedError
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException
import kotlin.js.JsExport

/** Passkey (WebAuthn) authentication methods. */
@StytchApi
@JsExport
public interface PasskeysClient {
    /** Whether passkeys are supported on the current platform. */
    public val isSupported: Boolean

    /**
     * Registers a new passkey for the current user. Performs a two-step flow: calls
     * `POST /sdk/v1/webauthn/register/start` to get creation options, invokes the platform
     * credential API to create the passkey, then calls `POST /sdk/v1/webauthn/register` to complete
     * registration. Requires an active session.
     *
     * **Kotlin (Android):**
     * ```kotlin
     * StytchConsumer.passkeys.register(
     *     PasskeysParameters(
     *         activity = activity,
     *         domain = "example.com",
     *         sessionDurationMinutes = 30,
     *     )
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = PasskeysParameters(domain: "example.com", sessionDurationMinutes: 30)
     * let response = try await StytchConsumer.passkeys.register(params)
     * ```
     *
     * **React Native:**
     * ```js
     * StytchConsumer.passkeys.register({ domain: "example.com", sessionDurationMinutes: 30 })
     * ```
     *
     * @param parameters - [PasskeysParameters]
     *   - `domain` — The relying party domain (e.g. `"example.com"`).
     *   - `sessionDurationMinutes?` — Duration of the session to create after registration, in minutes.
     *   - `preferImmediatelyAvailableCredentials` — Whether to prefer credentials that are immediately available on this device.
     *   - *(Android only)* `activity` — The `Activity` used to present the credential creation dialog.
     *
     * @return [WebAuthnRegisterResponse] containing the updated session and user.
     *
     * @throws [com.stytch.sdk.passkeys.PasskeysUnsupportedError] if passkeys are not supported on the current platform.
     * @throws [StytchError] if registration fails.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun register(parameters: PasskeysParameters): WebAuthnRegisterResponse

    /**
     * Authenticates the user with an existing registered passkey. Performs a two-step flow: calls
     * `POST /sdk/v1/webauthn/authenticate/start/primary` (no session) or
     * `POST /sdk/v1/webauthn/authenticate/start/secondary` (with session) to get assertion options,
     * invokes the platform credential API, then calls `POST /sdk/v1/webauthn/authenticate` to complete.
     *
     * **Kotlin (Android):**
     * ```kotlin
     * StytchConsumer.passkeys.authenticate(
     *     PasskeysParameters(
     *         activity = activity,
     *         domain = "example.com",
     *         sessionDurationMinutes = 30,
     *     )
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = PasskeysParameters(domain: "example.com", sessionDurationMinutes: 30)
     * let response = try await StytchConsumer.passkeys.authenticate(params)
     * ```
     *
     * **React Native:**
     * ```js
     * StytchConsumer.passkeys.authenticate({ domain: "example.com", sessionDurationMinutes: 30 })
     * ```
     *
     * @param parameters - [PasskeysParameters]
     *   - `domain` — The relying party domain (e.g. `"example.com"`).
     *   - `sessionDurationMinutes?` — Duration of the session to create, in minutes.
     *   - `preferImmediatelyAvailableCredentials` — Whether to prefer credentials that are immediately available on this device.
     *   - *(Android only)* `activity` — The `Activity` used to present the credential selection dialog.
     *
     * @return [WebAuthnAuthenticateResponse] containing the authenticated session and user.
     *
     * @throws [com.stytch.sdk.passkeys.PasskeysUnsupportedError] if passkeys are not supported on the current platform.
     * @throws [StytchError] if authentication fails.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun authenticate(parameters: PasskeysParameters): WebAuthnAuthenticateResponse

    /**
     * Updates metadata for an existing passkey registration (e.g. sets a user-friendly display name).
     * Calls the `PUT /sdk/v1/webauthn/update/{webauthn_registration_id}` endpoint.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchConsumer.passkeys.update(
     *     id = "webauthn-registration-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *     request = WebAuthnUpdateParameters(name = "My iPhone"),
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = WebAuthnUpdateParameters(name: "My iPhone")
     * let response = try await StytchConsumer.passkeys.update(
     *     id: "webauthn-registration-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *     request: params
     * )
     * ```
     *
     * **React Native:**
     * ```js
     * StytchConsumer.passkeys.update(
     *     "webauthn-registration-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *     { name: "My iPhone" }
     * )
     * ```
     *
     * @param id The unique ID of the WebAuthn registration to update.
     * @param request - [IWebAuthnUpdateParameters]
     *   - `name` — A human-readable display name for the passkey (e.g. `"My iPhone"`).
     *
     * @return [WebAuthnUpdateResponse] containing the updated registration.
     *
     * @throws [com.stytch.sdk.passkeys.PasskeysUnsupportedError] if passkeys are not supported on the current platform.
     * @throws [StytchError] if the registration ID does not exist or the request fails.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
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

    @Throws(StytchError::class, CancellationException::class)
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

    @Throws(StytchError::class, CancellationException::class)
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

    @Throws(StytchError::class, CancellationException::class)
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
