package com.stytch.sdk.biometrics

import com.stytch.sdk.StytchBridge
import com.stytch.sdk.data.Ed25519KeyPair
import kotlinx.coroutines.await
import kotlinx.serialization.json.Json

public actual class BiometricsProvider : IBiometricsProvider {
    public actual suspend fun getAvailability(parameters: BiometricsParameters): BiometricsAvailability {
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
        return Json.decodeFromString(result)
    }

    public actual suspend fun register(parameters: BiometricsParameters): Ed25519KeyPair {
        val keyPairString =
            StytchBridge
                .registerBiometrics(
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
        return Json.decodeFromString(keyPairString)
    }

    public actual suspend fun authenticate(parameters: BiometricsParameters): Ed25519KeyPair {
        val keyPairString =
            StytchBridge
                .authenticateBiometrics(
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
        return Json.decodeFromString(keyPairString)
    }

    public actual suspend fun persistRegistration(
        registrationId: String,
        privateKeyData: String,
    ) {
        StytchBridge.persistBiometricRegistration(registrationId, privateKeyData).await()
    }

    public actual suspend fun removeRegistration() {
        StytchBridge.removeBiometricRegistration().await()
    }
}
