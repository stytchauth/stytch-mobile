package com.stytch.sdk.biometrics

import com.stytch.sdk.StytchBridge
import com.stytch.sdk.data.Ed25519KeyPair
import kotlinx.coroutines.await
import kotlinx.serialization.json.Json

public actual class BiometricsProvider : IBiometricsProvider {
    public actual override suspend fun getAvailability(parameters: BiometricsParameters): BiometricsAvailability =
        try {
            val result =
                StytchBridge
                    .getBiometricsAvailability(
                        sessionDurationMinutes = parameters.sessionDurationMinutes,
                        androidAllowDeviceCredentials = parameters.androidBiometricOptions.allowDeviceCredentials,
                        androidTitle = parameters.androidBiometricOptions.title,
                        androidSubTitle = parameters.androidBiometricOptions.subTitle,
                        androidNegativeButtonText = parameters.androidBiometricOptions.negativeButtonText,
                        androidAllowFallbackToCleartext = parameters.androidBiometricOptions.allowFallbackToCleartext,
                        iosReason = parameters.iosBiometricOptions.reason,
                        iosFallbackTitle = parameters.iosBiometricOptions.fallbackTitle,
                        iosCancelTitle = parameters.iosBiometricOptions.cancelTitle,
                    ).await()
            Json.decodeFromString(result)
        } catch (e: Throwable) {
            throw InvalidBiometricAvailabilityError(e.message ?: "Unknown error getting biometrics availability")
        }

    public actual override suspend fun createBiometricKey(parameters: BiometricsParameters): String =
        try {
            StytchBridge
                .createBiometricKey(
                    sessionDurationMinutes = parameters.sessionDurationMinutes,
                    androidAllowDeviceCredentials = parameters.androidBiometricOptions.allowDeviceCredentials,
                    androidTitle = parameters.androidBiometricOptions.title,
                    androidSubTitle = parameters.androidBiometricOptions.subTitle,
                    androidNegativeButtonText = parameters.androidBiometricOptions.negativeButtonText,
                    androidAllowFallbackToCleartext = parameters.androidBiometricOptions.allowFallbackToCleartext,
                    iosReason = parameters.iosBiometricOptions.reason,
                    iosFallbackTitle = parameters.iosBiometricOptions.fallbackTitle,
                    iosCancelTitle = parameters.iosBiometricOptions.cancelTitle,
                ).await()
        } catch (e: Throwable) {
            throw UnhandledCryptographyError(e)
        }

    public actual override suspend fun retrieveBiometricKey(parameters: BiometricsParameters): String =
        try {
            StytchBridge
                .retrieveBiometricKey(
                    sessionDurationMinutes = parameters.sessionDurationMinutes,
                    androidAllowDeviceCredentials = parameters.androidBiometricOptions.allowDeviceCredentials,
                    androidTitle = parameters.androidBiometricOptions.title,
                    androidSubTitle = parameters.androidBiometricOptions.subTitle,
                    androidNegativeButtonText = parameters.androidBiometricOptions.negativeButtonText,
                    androidAllowFallbackToCleartext = parameters.androidBiometricOptions.allowFallbackToCleartext,
                    iosReason = parameters.iosBiometricOptions.reason,
                    iosFallbackTitle = parameters.iosBiometricOptions.fallbackTitle,
                    iosCancelTitle = parameters.iosBiometricOptions.cancelTitle,
                ).await()
        } catch (e: Throwable) {
            throw UnhandledCryptographyError(e)
        }

    actual override suspend fun signWithBiometricKey(challenge: String): String =
        try {
            StytchBridge.signWithBiometricKey(challenge).await()
        } catch (e: Throwable) {
            throw UnhandledCryptographyError(e)
        }

    public actual override suspend fun persistRegistration(registrationId: String) {
        StytchBridge.persistBiometricRegistration(registrationId).await()
    }

    public actual override suspend fun removeRegistration() {
        StytchBridge.removeBiometricRegistration().await()
    }
}
