package com.stytch.sdk.data

import android.app.Application
import android.content.Context
import android.os.Build
import com.stytch.sdk.biometrics.BiometricsProvider
import com.stytch.sdk.dfp.CAPTCHAProviderImpl
import com.stytch.sdk.dfp.DFPProviderImpl
import com.stytch.sdk.encryption.StytchEncryptionClient
import com.stytch.sdk.oauth.OAuthProvider
import com.stytch.sdk.passkeys.PasskeyProvider
import com.stytch.sdk.persistence.STYTCH_PERSISTENCE_FILE_NAME
import com.stytch.sdk.persistence.StytchPlatformPersistenceClient

public actual class StytchClientConfiguration(
    internal val context: Context,
    internal val publicToken: String,
    internal val endpointOptions: EndpointOptions = EndpointOptions(),
    internal val defaultSessionDuration: Int? = null,
    internal val googleCredentialConfiguration: GoogleCredentialConfiguration? = null,
    internal val persistenceFileName: String = STYTCH_PERSISTENCE_FILE_NAME,
) {
    public actual fun toInternal(): StytchClientConfigurationInternal {
        // DFP initialization is a two-step process SOLELY because it has to be on React Native :upsidedownface:
        val dfpProvider = DFPProviderImpl(context)
        dfpProvider.configureDfp(publicToken = publicToken, dfppaDomain = endpointOptions.dfppaDomain)
        val encryptionClient = StytchEncryptionClient()
        val platformPersistenceClient = StytchPlatformPersistenceClient(context, persistenceFileName)
        return StytchClientConfigurationInternal(
            publicToken = publicToken,
            endpointOptions = endpointOptions,
            defaultSessionDuration = defaultSessionDuration ?: DEFAULT_SESSION_DURATION_MINUTES,
            deviceInfo = context.getDeviceInfo(),
            platformPersistenceClient = platformPersistenceClient,
            platform = KMPPlatformType.ANDROID,
            encryptionClient = encryptionClient,
            dfpProvider = dfpProvider,
            captchaProvider = CAPTCHAProviderImpl(context.applicationContext as Application),
            passkeyProvider = PasskeyProvider(),
            biometricsProvider = BiometricsProvider(encryptionClient, platformPersistenceClient),
            oAuthProvider =
                OAuthProvider(
                    application = context.applicationContext as Application,
                    packageName = context.applicationContext.packageName as String,
                    googleCredentialConfiguration = googleCredentialConfiguration,
                ),
        )
    }
}

public fun Context.getDeviceInfo(): DeviceInfo {
    val applicationPackageName = applicationContext.packageName.toString()
    val applicationVersion =
        try {
            applicationContext
                .packageManager
                .getPackageInfo(applicationPackageName, 0)
                .versionName
                .toString()
        } catch (_: Exception) {
            ""
        }
    val width = resources.displayMetrics.widthPixels
    val height = resources.displayMetrics.heightPixels
    return DeviceInfo(
        applicationPackageName = applicationPackageName,
        applicationVersion = applicationVersion,
        osName = "Android",
        osVersion = Build.VERSION.SDK_INT.toString(),
        deviceName = Build.MODEL,
        screenSize = "($width,$height)",
    )
}
