package com.stytch.sdk.biometrics

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.KeyProperties
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity
import com.stytch.sdk.data.Ed25519KeyPair
import com.stytch.sdk.encryption.StytchEncryptionClient
import com.stytch.sdk.persistence.StytchPlatformPersistenceClient
import io.ktor.util.decodeBase64Bytes
import io.ktor.util.encodeBase64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.security.InvalidAlgorithmParameterException
import java.security.KeyStore
import java.util.concurrent.Executors
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

public actual class BiometricsProvider(
    private val encryptionClient: StytchEncryptionClient,
    private val persistenceClient: StytchPlatformPersistenceClient,
) : IBiometricsProvider {
    private var keyStoreLoaded = false

    public actual override suspend fun getAvailability(parameters: BiometricsParameters): BiometricsAvailability {
        val allowedAuthenticators = getAllowedAuthenticators(parameters.allowDeviceCredentials)
        var errorEncounteredWhenGeneratingKey = false
        try {
            ensureSecretKeyIsAvailable(allowedAuthenticators)
        } catch (_: KeyPermanentlyInvalidatedException) {
            removeRegistration()
            return BiometricsAvailability.RegistrationRevoked
        } catch (_: IllegalStateException) {
            // Secret key is null/couldn't be created (likely because of missing biometric factor)
            errorEncounteredWhenGeneratingKey = true
        } catch (_: InvalidAlgorithmParameterException) {
            errorEncounteredWhenGeneratingKey = true
        }
        return when (val result = areBiometricsAvailable(parameters.context, allowedAuthenticators)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                // if this returns success, BUT there were errors generating the key, biometrics aren't _really_
                // available, see: https://issuetracker.google.com/issues/147374428. We wait until here to
                // return the Unavailable case because it's possible one of the other error statuses is more
                // informative/applicable. This is *just* to account for this known bug in the canAuthenticate()
                // check.
                if (errorEncounteredWhenGeneratingKey) {
                    return BiometricsAvailability.Unavailable("Error encountered when attempting to generate secret key")
                }
                when (persistenceClient.getData(BIOMETRIC_REGISTRATION_PRIVATE_KEY_KEY) != null) {
                    true -> BiometricsAvailability.AlreadyRegistered
                    false -> BiometricsAvailability.Available
                }
            }

            else -> {
                BiometricsAvailability.Unavailable(reason = "Biometrics Unavailable. Check code", code = result)
            }
        }
    }

    public actual override suspend fun register(parameters: BiometricsParameters): Ed25519KeyPair =
        try {
            val allowedAuthenticators = getAllowedAuthenticators(parameters.allowDeviceCredentials)
            val cipher =
                showBiometricPromptForRegistration(
                    context = parameters.context,
                    promptData = parameters.promptData,
                    allowedAuthenticators = allowedAuthenticators,
                )
            val keyPair = encryptionClient.generateEd25519KeyPair()
            val encryptedPrivateKeyBytes = cipher.iv + cipher.doFinal(keyPair.privateKey)
            Ed25519KeyPair(
                publicKey = keyPair.publicKey,
                privateKey = keyPair.privateKey,
                encryptedPrivateKey = encryptedPrivateKeyBytes,
            )
        } catch (e: Throwable) {
            throw UnhandledCryptographyError(e)
        }

    public actual override suspend fun authenticate(parameters: BiometricsParameters): Ed25519KeyPair =
        try {
            val allowedAuthenticators = getAllowedAuthenticators(parameters.allowDeviceCredentials)
            val encodedPrivateKeyBytes =
                persistenceClient.getData(BIOMETRIC_REGISTRATION_PRIVATE_KEY_KEY)?.decodeBase64Bytes()
                    ?: throw MissingBiometricKeyDataError()
            val encodedPrivateKeyIv = encodedPrivateKeyBytes.sliceArray(0 until CBC_IV_LENGTH)
            val encodedPrivateKeyData = encodedPrivateKeyBytes.sliceArray(CBC_IV_LENGTH until encodedPrivateKeyBytes.size)
            val cipher =
                showBiometricPromptForAuthentication(
                    context = parameters.context,
                    promptData = parameters.promptData,
                    iv = encodedPrivateKeyIv,
                    allowedAuthenticators = allowedAuthenticators,
                )
            val decodedPrivateKeyData = cipher.doFinal(encodedPrivateKeyData)
            val derivedPublicKey = encryptionClient.deriveEd25519PublicKeyFromPrivateKeyBytes(decodedPrivateKeyData)
            Ed25519KeyPair(
                publicKey = derivedPublicKey,
                privateKey = decodedPrivateKeyData,
            )
        } catch (e: Throwable) {
            throw UnhandledCryptographyError(e)
        }

    private suspend fun ensureKeystoreIsLoaded() =
        withContext(Dispatchers.IO) {
            if (!keyStoreLoaded) {
                keyStore.load(null).also { keyStoreLoaded = true }
            }
        }

    private fun getAllowedAuthenticators(allowDeviceCredentials: Boolean) =
        if (allowDeviceCredentials && Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            BIOMETRIC_STRONG or DEVICE_CREDENTIAL
        } else {
            BIOMETRIC_STRONG
        }

    private val keyStore = KeyStore.getInstance("AndroidKeyStore")

    private fun allowedAuthenticatorsIncludeDeviceCredentials(allowedAuthenticators: Int) =
        allowedAuthenticators == BIOMETRIC_STRONG or DEVICE_CREDENTIAL

    private suspend fun getSecretKey(allowedAuthenticators: Int): SecretKey? =
        try {
            ensureKeystoreIsLoaded()
            if (!keyStore.containsAlias(BIOMETRIC_KEY_NAME)) {
                val keyGenerator =
                    KeyGenerator.getInstance(
                        KeyProperties.KEY_ALGORITHM_AES,
                        "AndroidKeyStore",
                    )
                val keyGenParameterSpec =
                    KeyGenParameterSpec
                        .Builder(
                            BIOMETRIC_KEY_NAME,
                            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
                        ).apply {
                            setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                            setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                            setUserAuthenticationRequired(true)
                            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                                val authenticationParameters =
                                    if (allowedAuthenticatorsIncludeDeviceCredentials(allowedAuthenticators)) {
                                        KeyProperties.AUTH_BIOMETRIC_STRONG or KeyProperties.AUTH_DEVICE_CREDENTIAL
                                    } else {
                                        KeyProperties.AUTH_BIOMETRIC_STRONG
                                    }
                                setUserAuthenticationParameters(0, authenticationParameters)
                            }
                        }.build()
                keyGenerator.init(keyGenParameterSpec)
                keyGenerator.generateKey()
            }
            keyStore.getKey(BIOMETRIC_KEY_NAME, null) as SecretKey
        } catch (_: Exception) {
            null
        }

    private fun getCipher(): Cipher =
        Cipher.getInstance(
            KeyProperties.KEY_ALGORITHM_AES + "/" +
                KeyProperties.BLOCK_MODE_CBC + "/" +
                KeyProperties.ENCRYPTION_PADDING_PKCS7,
        )

    private suspend fun showBiometricPrompt(
        context: FragmentActivity,
        promptData: BiometricPromptData?,
        cipher: Cipher,
        allowedAuthenticators: Int,
    ): Cipher =
        suspendCancellableCoroutine { continuation ->
            val executor = Executors.newSingleThreadExecutor()
            val callback =
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationError(
                        errorCode: Int,
                        errString: CharSequence,
                    ) {
                        super.onAuthenticationError(errorCode, errString)
                        continuation.resumeWithException(BiometricAuthenticationFailed(errString.toString()))
                    }

                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                        result.cryptoObject
                            ?.cipher
                            ?.let { continuation.resume(it) }
                            ?: continuation.resumeWithException(BiometricAuthenticationFailed("Missing Cipher"))
                    }
                }
            val prompt =
                BiometricPrompt.PromptInfo
                    .Builder()
                    .apply {
                        setTitle(promptData?.title ?: "Biometric Authentication")
                        setSubtitle(promptData?.subTitle ?: "Authenticate using your device biometrics")
                        setAllowedAuthenticators(allowedAuthenticators)
                        if (!allowedAuthenticatorsIncludeDeviceCredentials(allowedAuthenticators)) {
                            // can only show negative button if device credentials are not allowed
                            setNegativeButtonText(promptData?.negativeButtonText ?: "Cancel")
                        }
                    }.build()
            BiometricPrompt(context, executor, callback).authenticate(prompt, BiometricPrompt.CryptoObject(cipher))
        }

    private suspend fun showBiometricPromptForRegistration(
        context: FragmentActivity,
        promptData: BiometricPromptData?,
        allowedAuthenticators: Int,
    ): Cipher {
        val cipher = getCipher()
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(allowedAuthenticators))
        return showBiometricPrompt(context, promptData, cipher, allowedAuthenticators)
    }

    private suspend fun showBiometricPromptForAuthentication(
        context: FragmentActivity,
        promptData: BiometricPromptData?,
        iv: ByteArray,
        allowedAuthenticators: Int,
    ): Cipher {
        val cipher = getCipher()
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey(allowedAuthenticators), IvParameterSpec(iv))
        return showBiometricPrompt(context, promptData, cipher, allowedAuthenticators)
    }

    private fun areBiometricsAvailable(
        context: FragmentActivity,
        allowedAuthenticators: Int,
    ): Int = BiometricManager.from(context).canAuthenticate(allowedAuthenticators)

    private suspend fun ensureSecretKeyIsAvailable(allowedAuthenticators: Int) {
        val secretKey = getSecretKey(allowedAuthenticators) ?: error("SecretKey cannot be null")
        // initialize a cipher (that we won't use) with the secretkey to ensure it hasn't been invalidated
        val cipher = getCipher()
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
    }

    public actual override suspend fun persistRegistration(
        registrationId: String,
        privateKeyData: String,
    ) {
        persistenceClient.saveData(BIOMETRIC_REGISTRATION_ID_KEY, registrationId)
        persistenceClient.saveData(BIOMETRIC_REGISTRATION_PRIVATE_KEY_KEY, privateKeyData)
    }

    public actual override suspend fun removeRegistration() {
        ensureKeystoreIsLoaded()
        persistenceClient.removeData(BIOMETRIC_REGISTRATION_ID_KEY)
        persistenceClient.removeData(BIOMETRIC_REGISTRATION_PRIVATE_KEY_KEY)
        keyStore.deleteEntry(BIOMETRIC_KEY_NAME)
    }

    private companion object {
        private const val CBC_IV_LENGTH = 16
    }
}
