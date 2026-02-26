package com.stytch.sdk.consumer.biometrics

import com.stytch.sdk.StytchAuthenticationStateManager
import com.stytch.sdk.biometrics.BiometricsAvailability
import com.stytch.sdk.biometrics.BiometricsParameters
import com.stytch.sdk.biometrics.BiometricsProvider
import com.stytch.sdk.biometrics.BiometricsUnsupportedError
import com.stytch.sdk.consumer.networking.ConsumerNetworkingClient
import com.stytch.sdk.consumer.networking.models.BiometricsAuthenticateParameters
import com.stytch.sdk.consumer.networking.models.BiometricsAuthenticateResponse
import com.stytch.sdk.consumer.networking.models.BiometricsAuthenticateStartParameters
import com.stytch.sdk.consumer.networking.models.BiometricsRegisterParameters
import com.stytch.sdk.consumer.networking.models.BiometricsRegisterResponse
import com.stytch.sdk.consumer.networking.models.BiometricsRegisterStartParameters
import com.stytch.sdk.consumer.networking.models.toNetworkModel
import com.stytch.sdk.data.StytchDispatchers
import com.stytch.sdk.encryption.StytchEncryptionClient
import io.ktor.util.encodeBase64
import kotlinx.coroutines.withContext
import kotlin.js.JsExport

@JsExport
public interface BiometricsClient {
    public suspend fun register(parameters: BiometricsParameters): BiometricsRegisterResponse

    public suspend fun authenticate(parameters: BiometricsParameters): BiometricsAuthenticateResponse

    public suspend fun removeRegistration(): Boolean

    public suspend fun getAvailability(parameters: BiometricsParameters): BiometricsAvailability
}

internal class BiometricsClientImpl(
    private val dispatchers: StytchDispatchers,
    private val networkingClient: ConsumerNetworkingClient,
    private val sessionManager: StytchAuthenticationStateManager,
    private val encryptionClient: StytchEncryptionClient,
    private val biometricsProvider: BiometricsProvider,
) : BiometricsClient {
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
                            data = startResponse.data.challenge.encodeToByteArray(),
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
                            data = startResponse.data.challenge.encodeToByteArray(),
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

    override suspend fun removeRegistration(): Boolean {
        biometricsProvider.removeRegistration()
        return true
    }

    override suspend fun getAvailability(parameters: BiometricsParameters): BiometricsAvailability =
        biometricsProvider.getAvailability(parameters)
}
