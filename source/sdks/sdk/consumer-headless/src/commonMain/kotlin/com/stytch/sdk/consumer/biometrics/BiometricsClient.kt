package com.stytch.sdk.consumer.biometrics

import com.stytch.sdk.StytchApi
import com.stytch.sdk.StytchAuthenticationStateManager
import com.stytch.sdk.biometrics.BiometricsAlreadyEnrolledError
import com.stytch.sdk.biometrics.BiometricsAvailability
import com.stytch.sdk.biometrics.BiometricsParameters
import com.stytch.sdk.biometrics.BiometricsUnsupportedError
import com.stytch.sdk.biometrics.IBiometricsProvider
import com.stytch.sdk.biometrics.MissingBiometricKeyDataError
import com.stytch.sdk.consumer.networking.ConsumerNetworkingClient
import com.stytch.sdk.consumer.networking.models.BiometricsAuthenticateParameters
import com.stytch.sdk.consumer.networking.models.BiometricsAuthenticateResponse
import com.stytch.sdk.consumer.networking.models.BiometricsAuthenticateStartParameters
import com.stytch.sdk.consumer.networking.models.BiometricsRegisterParameters
import com.stytch.sdk.consumer.networking.models.BiometricsRegisterResponse
import com.stytch.sdk.consumer.networking.models.BiometricsRegisterStartParameters
import com.stytch.sdk.consumer.networking.models.toNetworkModel
import com.stytch.sdk.data.StytchDispatchers
import com.stytch.sdk.data.StytchError
import com.stytch.sdk.encryption.StytchEncryptionClient
import io.ktor.util.decodeBase64Bytes
import io.ktor.util.encodeBase64
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException
import kotlin.js.JsExport

/** Biometric authentication methods. */
@StytchApi
@JsExport
public interface BiometricsClient {
    /**
     * Registers the device's biometric credential (fingerprint, face, etc.) for the current user.
     * Performs a two-step flow: calls `POST /sdk/v1/biometrics/register/start` to get a challenge,
     * prompts the user for biometric authentication, then calls `POST /sdk/v1/biometrics/register`
     * to complete registration. Requires an active session.
     *
     * **Kotlin (Android):**
     * ```kotlin
     * StytchConsumer.biometrics.register(
     *     BiometricsParameters(
     *         context = activity,
     *         sessionDurationMinutes = 30,
     *     )
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = BiometricsParameters(sessionDurationMinutes: 30)
     * let response = try await StytchConsumer.biometrics.register(params)
     * ```
     *
     * **React Native:**
     * ```js
     * StytchConsumer.biometrics.register({ sessionDurationMinutes: 30 })
     * ```
     *
     * @param parameters - [BiometricsParameters]
     *   - `sessionDurationMinutes` — Duration of the session after successful registration, in minutes.
     *   - *(Android only)* `context` — The `FragmentActivity` used to display the biometric prompt.
     *   - *(Android only)* `allowDeviceCredentials` — Whether to allow PIN/pattern/password as a fallback.
     *   - *(Android only)* `promptData` — Custom title, subtitle, and description for the biometric prompt.
     *   - *(Android only)* `allowFallbackToCleartext` — Whether to store the key in cleartext if the secure enclave is unavailable.
     *   - *(iOS only)* `promptData` — Custom reason string shown in the Face ID / Touch ID prompt.
     *
     * @return [BiometricsRegisterResponse] containing the updated session and user.
     *
     * @throws [BiometricsAlreadyEnrolled] if a biometric registration already exists on this device.
     * @throws [NoSessionExists] if no active session is present.
     * @throws [com.stytch.sdk.biometrics.BiometricsUnsupportedError] if the device does not support biometrics.
     * @throws [StytchError] if registration fails.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun register(parameters: BiometricsParameters): BiometricsRegisterResponse

    /**
     * Authenticates the user using a previously registered biometric credential. Performs a two-step
     * flow: calls `POST /sdk/v1/biometrics/authenticate/start` to get a challenge, prompts the user
     * for biometric authentication, then calls `POST /sdk/v1/biometrics/authenticate` to complete.
     *
     * **Kotlin (Android):**
     * ```kotlin
     * StytchConsumer.biometrics.authenticate(
     *     BiometricsParameters(
     *         context = activity,
     *         sessionDurationMinutes = 30,
     *     )
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = BiometricsParameters(sessionDurationMinutes: 30)
     * let response = try await StytchConsumer.biometrics.authenticate(params)
     * ```
     *
     * **React Native:**
     * ```js
     * StytchConsumer.biometrics.authenticate({ sessionDurationMinutes: 30 })
     * ```
     *
     * @param parameters - [BiometricsParameters]
     *   - `sessionDurationMinutes` — Duration of the session to create, in minutes.
     *   - *(Android only)* `context` — The `FragmentActivity` used to display the biometric prompt.
     *   - *(Android only)* `allowDeviceCredentials` — Whether to allow PIN/pattern/password as a fallback.
     *   - *(Android only)* `promptData` — Custom title, subtitle, and description for the biometric prompt.
     *   - *(Android only)* `allowFallbackToCleartext` — Whether to store the key in cleartext if the secure enclave is unavailable.
     *   - *(iOS only)* `promptData` — Custom reason string shown in the Face ID / Touch ID prompt.
     *
     * @return [BiometricsAuthenticateResponse] containing the authenticated session and user.
     *
     * @throws [NoBiometricsRegistered] if no biometric registration exists on this device.
     * @throws [StytchError] if authentication fails.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun authenticate(parameters: BiometricsParameters): BiometricsAuthenticateResponse

    /**
     * Removes the locally stored biometric registration keypair from this device. This is a local-only
     * operation — it does not make a network call and does not remove the registration from the Stytch
     * backend. Requires an active session.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchConsumer.biometrics.removeRegistration()
     * ```
     *
     * **iOS:**
     * ```swift
     * let success = try await StytchConsumer.biometrics.removeRegistration()
     * ```
     *
     * **React Native:**
     * ```js
     * StytchConsumer.biometrics.removeRegistration()
     * ```
     *
     * @return `true` if the local registration was successfully removed.
     *
     * @throws [NoSessionExists] if no active session is present.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun removeRegistration(): Boolean

    /**
     * Returns the biometric availability status for the current device and user. This is a local-only
     * operation — it does not make a network call.
     *
     * **Kotlin (Android):**
     * ```kotlin
     * StytchConsumer.biometrics.getAvailability(
     *     BiometricsParameters(context = activity, sessionDurationMinutes = 0)
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = BiometricsParameters(sessionDurationMinutes: 0)
     * let availability = try await StytchConsumer.biometrics.getAvailability(params)
     * ```
     *
     * **React Native:**
     * ```js
     * StytchConsumer.biometrics.getAvailability({ sessionDurationMinutes: 0 })
     * ```
     *
     * @param parameters - [BiometricsParameters] (see [register] for field descriptions).
     *
     * @return [BiometricsAvailability] — one of `Available`, `AlreadyRegistered`, or `Unavailable`.
     *
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun getAvailability(parameters: BiometricsParameters): BiometricsAvailability
}

internal class BiometricsClientImpl(
    private val dispatchers: StytchDispatchers,
    private val networkingClient: ConsumerNetworkingClient,
    private val sessionManager: StytchAuthenticationStateManager,
    private val encryptionClient: StytchEncryptionClient,
    private val biometricsProvider: IBiometricsProvider,
) : BiometricsClient {
    @Throws(StytchError::class, CancellationException::class)
    override suspend fun register(parameters: BiometricsParameters): BiometricsRegisterResponse {
        val availability = getAvailability(parameters)
        if (availability is BiometricsAvailability.Unavailable) {
            throw BiometricsUnsupportedError()
        }
        if (availability == BiometricsAvailability.AlreadyRegistered) {
            throw BiometricsAlreadyEnrolledError()
        }
        if (sessionManager.currentSessionToken.isNullOrEmpty()) {
            throw NoSessionExists()
        }
        val keyPair = biometricsProvider.register(parameters)
        return withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                val startResponse =
                    networkingClient.api.biometricsRegisterStart(
                        BiometricsRegisterStartParameters().toNetworkModel(publicKey = keyPair.publicKey.encodeBase64()),
                    )
                val signature =
                    encryptionClient
                        .signEd25519(
                            key = keyPair.privateKey,
                            data = startResponse.data.challenge.decodeBase64Bytes(),
                        ).encodeBase64()
                val response =
                    networkingClient.api.biometricsRegister(
                        BiometricsRegisterParameters(sessionDurationMinutes = parameters.sessionDurationMinutes).toNetworkModel(
                            biometricRegistrationId = startResponse.data.biometricRegistrationId,
                            signature = signature,
                        ),
                    )
                val encKey = keyPair.encryptedPrivateKey ?: throw MissingBiometricKeyDataError()
                // if we made it here, the registration was successful, so persist the data
                biometricsProvider.persistRegistration(
                    registrationId = response.data.biometricRegistrationId,
                    privateKeyData = encKey.encodeBase64(),
                )
                // return the response
                response
            }
        }
    }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun authenticate(parameters: BiometricsParameters): BiometricsAuthenticateResponse {
        if (getAvailability(parameters) != BiometricsAvailability.AlreadyRegistered) {
            throw NoBiometricsRegistered()
        }
        val keyPair = biometricsProvider.authenticate(parameters)
        return withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                val startResponse =
                    networkingClient.api.biometricsAuthenticateStart(
                        BiometricsAuthenticateStartParameters().toNetworkModel(publicKey = keyPair.publicKey.encodeBase64()),
                    )
                val signature =
                    encryptionClient
                        .signEd25519(
                            key = keyPair.privateKey,
                            data = startResponse.data.challenge.decodeBase64Bytes(),
                        ).encodeBase64()
                networkingClient.api.biometricsAuthenticate(
                    BiometricsAuthenticateParameters(sessionDurationMinutes = parameters.sessionDurationMinutes).toNetworkModel(
                        biometricRegistrationId = startResponse.data.biometricRegistrationId,
                        signature = signature,
                    ),
                )
            }
        }
    }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun removeRegistration(): Boolean {
        if (sessionManager.currentSessionToken.isNullOrEmpty()) {
            throw NoSessionExists()
        }
        biometricsProvider.removeRegistration()
        return true
    }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun getAvailability(parameters: BiometricsParameters): BiometricsAvailability =
        biometricsProvider.getAvailability(parameters)
}
