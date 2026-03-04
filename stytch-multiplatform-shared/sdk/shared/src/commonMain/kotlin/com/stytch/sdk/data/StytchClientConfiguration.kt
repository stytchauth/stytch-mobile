package com.stytch.sdk.data

import com.stytch.sdk.biometrics.IBiometricsProvider
import com.stytch.sdk.dfp.CAPTCHAProvider
import com.stytch.sdk.dfp.DFPProvider
import com.stytch.sdk.encryption.StytchEncryptionClient
import com.stytch.sdk.oauth.IOAuthProvider
import com.stytch.sdk.passkeys.IPasskeyProvider
import com.stytch.sdk.persistence.StytchPlatformPersistenceClient
import kotlinx.datetime.TimeZone
import kotlin.js.JsExport
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

public expect class StytchClientConfiguration {
    public fun toInternal(): StytchClientConfigurationInternal
}

@JsExport
public class PublicTokenInfo(
    public val publicToken: String,
    public val isTestToken: Boolean,
)

private val PUBLIC_TOKEN_REGEX = Regex("^public-token-(test|live)-[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")

public fun getPublicTokenInfo(publicToken: String): PublicTokenInfo {
    val matches = PUBLIC_TOKEN_REGEX.find(publicToken)
    require(matches != null) { "Invalid public token provided: $publicToken" }
    return PublicTokenInfo(
        publicToken = publicToken,
        isTestToken = matches.groupValues[1] == "test",
    )
}

@JsExport
public class StytchClientConfigurationInternal(
    publicToken: String,
    public val endpointOptions: EndpointOptions,
    public val defaultSessionDuration: Int,
    internal val deviceInfo: DeviceInfo,
    public val tokenInfo: PublicTokenInfo = getPublicTokenInfo(publicToken),
    @OptIn(ExperimentalUuidApi::class)
    internal val appSessionId: String = Uuid.random().toString(),
    internal val timezone: String = TimeZone.currentSystemDefault().id,
    public val platformPersistenceClient: StytchPlatformPersistenceClient,
    internal val platform: KMPPlatformType,
    public val encryptionClient: StytchEncryptionClient,
    public val dfpProvider: DFPProvider? = null,
    public val captchaProvider: CAPTCHAProvider? = null,
    public val passkeyProvider: IPasskeyProvider,
    public val biometricsProvider: IBiometricsProvider,
    public val oAuthProvider: IOAuthProvider,
)

@JsExport
public enum class KMPPlatformType {
    ANDROID,
    IOS,
    REACTNATIVE,
    JVM,
}

// This is the minimum a session can last in Stytch
internal const val DEFAULT_SESSION_DURATION_MINUTES = 5
