package com.stytch.sdk.consumer.biometrics

import com.stytch.sdk.StytchAuthenticationStateManager
import com.stytch.sdk.biometrics.BiometricsAvailability
import com.stytch.sdk.biometrics.BiometricsProvider
import com.stytch.sdk.biometrics.BiometricsUnsupportedError
import com.stytch.sdk.consumer.networking.ConsumerNetworkingClient
import com.stytch.sdk.consumer.networking.models.BiometricsAuthenticateResponse
import com.stytch.sdk.consumer.networking.models.BiometricsAuthenticateStartParameters
import com.stytch.sdk.consumer.networking.models.BiometricsRegisterParameters
import com.stytch.sdk.consumer.networking.models.BiometricsRegisterResponse
import com.stytch.sdk.consumer.networking.models.BiometricsRegisterStartParameters
import com.stytch.sdk.consumer.networking.models.IBiometricsAuthenticateParameters
import com.stytch.sdk.consumer.networking.models.IBiometricsRegisterParameters
import com.stytch.sdk.consumer.networking.models.IBiometricsRegisterStartParameters
import com.stytch.sdk.consumer.networking.models.toNetworkModel
import com.stytch.sdk.data.StytchDispatchers
import com.stytch.sdk.encryption.StytchEncryptionClient
import com.stytch.sdk.persistence.StytchPersistenceClient
import io.ktor.util.encodeBase64
import kotlinx.coroutines.withContext
import kotlin.js.JsExport

@JsExport
public interface BiometricsClient {
    public val isSupported: Boolean

    public suspend fun register(request: IBiometricsRegisterParameters): BiometricsRegisterResponse

    public suspend fun authenticate(request: IBiometricsAuthenticateParameters): BiometricsAuthenticateResponse

    public suspend fun removeRegistration(): Boolean

    public suspend fun getAvailability(): BiometricsAvailability
}

internal class BiometricsClientImpl(
    private val dispatchers: StytchDispatchers,
    private val networkingClient: ConsumerNetworkingClient,
    private val sessionManager: StytchAuthenticationStateManager,
    private val persistenceClient: StytchPersistenceClient,
    private val encryptionClient: StytchEncryptionClient,
    private val biometricsProvider: BiometricsProvider,
) : BiometricsClient {
    override val isSupported: Boolean = biometricsProvider.isSupported

    override suspend fun register(request: IBiometricsRegisterParameters): BiometricsRegisterResponse {
        val availability = getAvailability()
        if (isSupported.not() || availability is BiometricsAvailability.Unavailable) {
            throw BiometricsUnsupportedError()
        }
        if (availability == BiometricsAvailability.AlreadyRegistered) {
            throw BiometricsAlreadyEnrolled()
        }
        if (sessionManager.currentSessionToken.isNullOrEmpty()) {
            throw NoSessionExists()
        }
        val keyPair = biometricsProvider.register()
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
                        request.toNetworkModel(
                            biometricRegistrationId = startResponse.data.biometricRegistrationId,
                            signature = signature,
                        ),
                    )
                // if we made it here, the registration was successful, so persist the data
                persistenceClient.save(BIOMETRIC_REGISTRATION_ID_KEY, response.data.biometricRegistrationId)
                persistenceClient.save(BIOMETRIC_REGISTRATION_PRIVATE_KEY_KEY, keyPair.privateKey.encodeBase64())
                // return the response
                response
            }
        }
    }

    override suspend fun authenticate(request: IBiometricsAuthenticateParameters): BiometricsAuthenticateResponse {
        if (getAvailability() != BiometricsAvailability.AlreadyRegistered) {
            throw NoBiometricsRegistered()
        }
        val keyPair = biometricsProvider.authenticate()
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
                    request.toNetworkModel(
                        biometricRegistrationId = startResponse.data.biometricRegistrationId,
                        signature = signature,
                    ),
                )
            }
        }
    }

    override suspend fun removeRegistration(): Boolean {
        persistenceClient.remove(BIOMETRIC_REGISTRATION_ID_KEY)
        persistenceClient.remove(BIOMETRIC_REGISTRATION_PRIVATE_KEY_KEY)
        return true
    }

    override suspend fun getAvailability(): BiometricsAvailability = biometricsProvider.getAvailability()

    private companion object {
        private const val BIOMETRIC_REGISTRATION_ID_KEY = "BIOMETRIC_REGISTRATION_ID"
        private const val BIOMETRIC_REGISTRATION_PRIVATE_KEY_KEY = "BIOMETRIC_REGISTRATION_PRIVATE_KEY"
    }
}
