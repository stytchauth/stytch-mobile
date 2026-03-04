package com.stytch.sdk.biometrics

import com.stytch.sdk.data.Ed25519KeyPair
import com.stytch.sdk.encryption.StytchEncryptionClient
import com.stytch.sdk.persistence.StytchPlatformPersistenceClient
import io.ktor.util.decodeBase64Bytes
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Foundation.NSError
import platform.LocalAuthentication.LAContext
import platform.LocalAuthentication.LAPolicyDeviceOwnerAuthenticationWithBiometrics
import kotlin.coroutines.resumeWithException

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
public actual class BiometricsProvider(
    private val encryptionClient: StytchEncryptionClient,
    private val persistenceClient: StytchPlatformPersistenceClient,
) : IBiometricsProvider {
    public actual override suspend fun getAvailability(parameters: BiometricsParameters): BiometricsAvailability =
        memScoped {
            val laContext = LAContext()
            val registrationExists = persistenceClient.getData(BIOMETRIC_REGISTRATION_ID_KEY) != null
            val policyError = alloc<ObjCObjectVar<NSError?>>()
            val canEvaluate = laContext.canEvaluatePolicy(LAPolicyDeviceOwnerAuthenticationWithBiometrics, policyError.ptr)
            if (!canEvaluate) {
                return@memScoped BiometricsAvailability.Unavailable(
                    reason = "Biometrics Unavailable. Check code",
                    code = policyError.value?.code?.toInt(),
                )
            }
            if (registrationExists) {
                return@memScoped BiometricsAvailability.AlreadyRegistered
            }
            BiometricsAvailability.Available
        }

    public actual override suspend fun register(parameters: BiometricsParameters): Ed25519KeyPair {
        val laContext = LAContext()
        val canEvaluate = laContext.canEvaluatePolicy(LAPolicyDeviceOwnerAuthenticationWithBiometrics, null)
        if (!canEvaluate) {
            throw BiometricsUnsupportedError()
        }
        setPromptData(laContext, parameters.promptData)
        return suspendCancellableCoroutine { continuation ->
            laContext.evaluatePolicy(LAPolicyDeviceOwnerAuthenticationWithBiometrics, parameters.promptData.reason) { passed, error ->
                if (!passed || error != null) {
                    continuation.resumeWithException(BiometricAuthenticationFailed("Failed policy evaluation"))
                    return@evaluatePolicy
                }
                val keyPair = encryptionClient.generateEd25519KeyPair()
                val encryptedPrivateKey = encryptionClient.encrypt(keyPair.privateKey)
                continuation.resumeIgnoringCancellation(
                    Ed25519KeyPair(
                        publicKey = keyPair.publicKey,
                        privateKey = keyPair.privateKey,
                        encryptedPrivateKey = encryptedPrivateKey,
                    ),
                )
            }
        }
    }

    public actual override suspend fun authenticate(parameters: BiometricsParameters): Ed25519KeyPair {
        val laContext = LAContext()
        val canEvaluate = laContext.canEvaluatePolicy(LAPolicyDeviceOwnerAuthenticationWithBiometrics, null)
        if (!canEvaluate) {
            throw BiometricsUnsupportedError()
        }
        setPromptData(laContext, parameters.promptData)
        return suspendCancellableCoroutine { continuation ->
            laContext.evaluatePolicy(LAPolicyDeviceOwnerAuthenticationWithBiometrics, parameters.promptData.reason) { passed, error ->
                if (!passed || error != null) {
                    continuation.resumeWithException(BiometricAuthenticationFailed("Failed policy evaluation"))
                    return@evaluatePolicy
                }
                val encryptedPrivateKey =
                    encryptionClient.retrieveBiometricKey(BIOMETRIC_REGISTRATION_PRIVATE_KEY_KEY) ?: throw MissingBiometricKeyDataError()
                val decryptedPrivateKey = encryptionClient.decrypt(encryptedPrivateKey)
                val publicKey = encryptionClient.deriveEd25519PublicKeyFromPrivateKeyBytes(decryptedPrivateKey)
                continuation.resumeIgnoringCancellation(
                    Ed25519KeyPair(
                        publicKey = publicKey,
                        privateKey = decryptedPrivateKey,
                    ),
                )
            }
        }
    }

    public actual override suspend fun persistRegistration(
        registrationId: String,
        privateKeyData: String,
    ) {
        persistenceClient.saveData(BIOMETRIC_REGISTRATION_ID_KEY, registrationId)
        encryptionClient.createBiometricKey(BIOMETRIC_REGISTRATION_PRIVATE_KEY_KEY, privateKeyData.encodeToByteArray())
    }

    public actual override suspend fun removeRegistration() {
        persistenceClient.removeData(BIOMETRIC_REGISTRATION_ID_KEY)
        encryptionClient.deleteBiometricKey(BIOMETRIC_REGISTRATION_PRIVATE_KEY_KEY)
    }

    private fun setPromptData(
        laContext: LAContext,
        promptData: BiometricPromptData,
    ) {
        laContext.localizedReason = promptData.reason
        laContext.localizedFallbackTitle = promptData.fallbackTitle
        laContext.localizedCancelTitle = promptData.cancelTitle
    }
}

private fun <T> CancellableContinuation<T>.resumeIgnoringCancellation(result: T) {
    resume(result) { _, _, _ -> }
}
