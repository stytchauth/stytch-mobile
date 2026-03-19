package com.stytch.sdk.consumer.biometrics

import com.stytch.sdk.StytchApi
import com.stytch.sdk.StytchAuthenticationStateManager
import com.stytch.sdk.biometrics.BiometricsAvailability
import com.stytch.sdk.biometrics.BiometricsParameters
import com.stytch.sdk.biometrics.BiometricsUnsupportedError
import com.stytch.sdk.biometrics.IBiometricsProvider
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
     * Registers the device's biometric credential for the current user.
     * Requires an active session. Throws [BiometricsAlreadyEnrolled] if already registered,
     * [NoSessionExists] if no session is present, or [com.stytch.sdk.biometrics.BiometricsUnsupportedError]
     * if the device does not support biometrics.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun register(parameters: BiometricsParameters): BiometricsRegisterResponse

    /**
     * Authenticates the user using a registered biometric credential.
     * Throws [NoBiometricsRegistered] if no registration exists for this device.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun authenticate(parameters: BiometricsParameters): BiometricsAuthenticateResponse

    /**
     * Removes the locally stored biometric registration from this device.
     * Requires an active session. Throws [NoSessionExists] if no session is present.
     * Returns `true` on success.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun removeRegistration(): Boolean

    /** Returns the biometric availability status for the current device and user. */
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
            throw BiometricsAlreadyEnrolled()
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
                // if we made it here, the registration was successful, so persist the data
                biometricsProvider.persistRegistration(
                    registrationId = response.data.biometricRegistrationId,
                    privateKeyData = keyPair.encryptedPrivateKey!!.encodeBase64(),
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
