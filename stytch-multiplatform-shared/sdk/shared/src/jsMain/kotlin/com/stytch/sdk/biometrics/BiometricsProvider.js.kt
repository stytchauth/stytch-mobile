package com.stytch.sdk.biometrics

import com.stytch.sdk.StytchBridge
import com.stytch.sdk.data.Ed25519KeyPair

public actual class BiometricsProvider {
    public actual suspend fun getAvailability(parameters: BiometricsParameters): BiometricsAvailability {
        val availability =
            StytchBridge.getBiometricsAvailability(
                sessionDurationMinutes = parameters.sessionDurationMinutes,
                androidAllowDeviceCredentials = parameters.androidBiometricOptions.allowDeviceCredentials,
                androidTitle = parameters.androidBiometricOptions.title,
                androidSubTitle = parameters.androidBiometricOptions.subTitle,
                androidNegativeButtonText = parameters.androidBiometricOptions.negativeButtonText,
                androidAllowFallbackToCleartext = parameters.androidBiometricOptions.allowFallbackToCleartext,
                iosReason = parameters.iosBiometricOptions.reason,
                iosFallbackTitle = parameters.iosBiometricOptions.fallbackTitle,
                iosCancelTitle = parameters.iosBiometricOptions.cancelTitle,
            )
        val name = availability[0] as String
        var reason: String? = null
        var code: Int? = null
        if (availability.size > 1) {
            reason = availability[1] as String
            code = (availability[2] as String).toInt()
        }
        return BiometricsAvailability.fromString(name, reason, code)
    }

    public actual suspend fun register(parameters: BiometricsParameters): Ed25519KeyPair {
        val keyPairList =
            StytchBridge.registerBiometrics(
                sessionDurationMinutes = parameters.sessionDurationMinutes,
                androidAllowDeviceCredentials = parameters.androidBiometricOptions.allowDeviceCredentials,
                androidTitle = parameters.androidBiometricOptions.title,
                androidSubTitle = parameters.androidBiometricOptions.subTitle,
                androidNegativeButtonText = parameters.androidBiometricOptions.negativeButtonText,
                androidAllowFallbackToCleartext = parameters.androidBiometricOptions.allowFallbackToCleartext,
                iosReason = parameters.iosBiometricOptions.reason,
                iosFallbackTitle = parameters.iosBiometricOptions.fallbackTitle,
                iosCancelTitle = parameters.iosBiometricOptions.cancelTitle,
            )
        return Ed25519KeyPair(
            publicKey = keyPairList[0].encodeToByteArray(),
            privateKey = keyPairList[1].encodeToByteArray(),
            encryptedPrivateKey = keyPairList[2].encodeToByteArray(),
        )
    }

    public actual suspend fun authenticate(parameters: BiometricsParameters): Ed25519KeyPair {
        val keyPairList =
            StytchBridge.authenticateBiometrics(
                sessionDurationMinutes = parameters.sessionDurationMinutes,
                androidAllowDeviceCredentials = parameters.androidBiometricOptions.allowDeviceCredentials,
                androidTitle = parameters.androidBiometricOptions.title,
                androidSubTitle = parameters.androidBiometricOptions.subTitle,
                androidNegativeButtonText = parameters.androidBiometricOptions.negativeButtonText,
                androidAllowFallbackToCleartext = parameters.androidBiometricOptions.allowFallbackToCleartext,
                iosReason = parameters.iosBiometricOptions.reason,
                iosFallbackTitle = parameters.iosBiometricOptions.fallbackTitle,
                iosCancelTitle = parameters.iosBiometricOptions.cancelTitle,
            )
        return Ed25519KeyPair(
            publicKey = keyPairList[0].encodeToByteArray(),
            privateKey = keyPairList[1].encodeToByteArray(),
        )
    }

    public actual suspend fun persistRegistration(
        registrationId: String,
        privateKeyData: String,
    ) {
        StytchBridge.persistBiometricRegistration(registrationId, privateKeyData)
    }

    public actual suspend fun removeRegistration() {
        StytchBridge.removeBiometricRegistration()
    }
}
