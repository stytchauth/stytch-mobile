type Nullable<T> = T | null | undefined
declare function KtSingleton<T>(): T & (abstract new() => any);
export declare interface KtList<E> /* extends Collection<E> */ {
    asJsReadonlyArrayView(): ReadonlyArray<E>;
    readonly __doNotUseOrImplementIt: {
        readonly "kotlin.collections.KtList": unique symbol;
    };
}
export declare namespace KtList {
    function fromJsArray<E>(array: ReadonlyArray<E>): KtList<E>;
}
export declare interface KtMap<K, V> {
    asJsReadonlyMapView(): ReadonlyMap<K, V>;
    readonly __doNotUseOrImplementIt: {
        readonly "kotlin.collections.KtMap": unique symbol;
    };
}
export declare namespace KtMap {
    function fromJsMap<K, V>(map: ReadonlyMap<K, V>): KtMap<K, V>;
}
/** @deprecated  */
export declare const initHook: { get(): any; };
export declare interface StytchClient {
    readonly __doNotUseOrImplementIt: {
        readonly "com.stytch.sdk.StytchClient": unique symbol;
    };
}
export declare interface BasicResponse extends StytchAPIResponse {
    readonly statusCode: number;
    readonly requestId: string;
    readonly __doNotUseOrImplementIt: {
        readonly "com.stytch.sdk.data.BasicResponse": unique symbol;
    } & StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare class EndpointOptions {
    constructor(testDomain?: string, liveDomain?: string, dfppaDomain?: string);
    get testDomain(): string;
    get liveDomain(): string;
    get dfppaDomain(): string;
}
export declare namespace EndpointOptions {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => EndpointOptions;
    }
}
export declare class GoogleCredentialConfiguration {
    constructor(googleClientId: string, autoSelectEnabled?: boolean);
    get googleClientId(): string;
    get autoSelectEnabled(): boolean;
}
export declare namespace GoogleCredentialConfiguration {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => GoogleCredentialConfiguration;
    }
}
export declare interface JsCleanup {
    stop(): void;
    readonly __doNotUseOrImplementIt: {
        readonly "com.stytch.sdk.data.JsCleanup": unique symbol;
    };
}
export declare interface StytchAPIResponse {
    readonly __doNotUseOrImplementIt: {
        readonly "com.stytch.sdk.data.StytchAPIResponse": unique symbol;
    };
}
export declare class PublicTokenInfo {
    constructor(publicToken: string, isTestToken: boolean);
    get publicToken(): string;
    get isTestToken(): boolean;
}
export declare namespace PublicTokenInfo {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => PublicTokenInfo;
    }
}
export declare class StytchClientConfigurationInternal {
    constructor(publicToken: string, endpointOptions: EndpointOptions, defaultSessionDuration: number, deviceInfo: any/* DeviceInfo */, tokenInfo: PublicTokenInfo | undefined, appSessionId: string | undefined, timezone: string | undefined, platformPersistenceClient: StytchPlatformPersistenceClient, platform: KMPPlatformType, encryptionClient: StytchEncryptionClient, dfpProvider: Nullable<any>/* Nullable<DFPProvider> */ | undefined, captchaProvider: Nullable<any>/* Nullable<CAPTCHAProvider> */ | undefined, passkeyProvider: any/* IPasskeyProvider */, biometricsProvider: any/* IBiometricsProvider */, oAuthProvider: any/* IOAuthProvider */);
    get endpointOptions(): EndpointOptions;
    get defaultSessionDuration(): number;
    get tokenInfo(): PublicTokenInfo;
    get platformPersistenceClient(): StytchPlatformPersistenceClient;
    get encryptionClient(): StytchEncryptionClient;
    get dfpProvider(): Nullable<any>/* Nullable<DFPProvider> */;
    get captchaProvider(): Nullable<any>/* Nullable<CAPTCHAProvider> */;
    get passkeyProvider(): any/* IPasskeyProvider */;
    get biometricsProvider(): any/* IBiometricsProvider */;
    get oAuthProvider(): any/* IOAuthProvider */;
}
export declare namespace StytchClientConfigurationInternal {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => StytchClientConfigurationInternal;
    }
}
export declare abstract class KMPPlatformType {
    private constructor();
    static get ANDROID(): KMPPlatformType & {
        get name(): "ANDROID";
        get ordinal(): 0;
    };
    static get IOS(): KMPPlatformType & {
        get name(): "IOS";
        get ordinal(): 1;
    };
    static get REACTNATIVE(): KMPPlatformType & {
        get name(): "REACTNATIVE";
        get ordinal(): 2;
    };
    static get JVM(): KMPPlatformType & {
        get name(): "JVM";
        get ordinal(): 3;
    };
    get name(): "ANDROID" | "IOS" | "REACTNATIVE" | "JVM";
    get ordinal(): 0 | 1 | 2 | 3;
    static values(): Array<KMPPlatformType>;
    static valueOf(value: string): KMPPlatformType;
}
export declare namespace KMPPlatformType {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => KMPPlatformType;
    }
}
export declare class AndroidBiometricOptions {
    constructor(allowDeviceCredentials?: boolean, title?: Nullable<string>, subTitle?: Nullable<string>, negativeButtonText?: Nullable<string>, allowFallbackToCleartext?: boolean);
    get allowDeviceCredentials(): boolean;
    get title(): Nullable<string>;
    get subTitle(): Nullable<string>;
    get negativeButtonText(): Nullable<string>;
    get allowFallbackToCleartext(): boolean;
}
export declare namespace AndroidBiometricOptions {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => AndroidBiometricOptions;
    }
}
export declare class BiometricsParameters {
    constructor(sessionDurationMinutes: number, androidBiometricOptions?: AndroidBiometricOptions, iosBiometricOptions?: IosBiometricOptions);
    get sessionDurationMinutes(): number;
    get androidBiometricOptions(): AndroidBiometricOptions;
    get iosBiometricOptions(): IosBiometricOptions;
}
export declare namespace BiometricsParameters {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => BiometricsParameters;
    }
}
export declare class IosBiometricOptions {
    constructor(reason?: string, fallbackTitle?: Nullable<string>, cancelTitle?: Nullable<string>);
    get reason(): string;
    get fallbackTitle(): Nullable<string>;
    get cancelTitle(): Nullable<string>;
}
export declare namespace IosBiometricOptions {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => IosBiometricOptions;
    }
}
export declare class StytchClientConfiguration {
    constructor(publicToken: string, endpointOptions?: EndpointOptions, defaultSessionDuration?: Nullable<number>, googleCredentialConfiguration?: Nullable<GoogleCredentialConfiguration>);
    toInternal(): StytchClientConfigurationInternal;
}
export declare namespace StytchClientConfiguration {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => StytchClientConfiguration;
    }
}
export declare class StytchEncryptionClient {
    constructor();
    encrypt(data: Int8Array): Int8Array;
    decrypt(data: Int8Array): Int8Array;
    deleteKey(): void;
    generateCodeVerifier(): Int8Array;
    generateCodeChallenge(codeVerifier: Int8Array): Int8Array;
    signEd25519(key: Int8Array, data: Int8Array): Int8Array;
    generateEd25519KeyPair(): any/* Ed25519KeyPair */;
    deriveEd25519PublicKeyFromPrivateKeyBytes(privateKeyBytes: Int8Array): Int8Array;
}
export declare namespace StytchEncryptionClient {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => StytchEncryptionClient;
    }
}
export declare class B2BOAuthDiscoveryStartParameters {
    constructor(discoveryRedirectUrl?: Nullable<string>, customScopes?: Nullable<KtList<string>>, providerParams?: Nullable<KtMap<string, string>>);
    get discoveryRedirectUrl(): Nullable<string>;
    get customScopes(): Nullable<KtList<string>>;
    get providerParams(): Nullable<KtMap<string, string>>;
    toOAuthStartParameters(): OAuthStartParameters;
}
export declare namespace B2BOAuthDiscoveryStartParameters {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BOAuthDiscoveryStartParameters;
    }
}
export declare class B2BOAuthStartParameters {
    constructor(loginRedirectUrl?: Nullable<string>, signupRedirectUrl?: Nullable<string>, organizationId?: Nullable<string>, organizationSlug?: Nullable<string>, customScopes?: Nullable<KtList<string>>, providerParams?: Nullable<KtMap<string, string>>, sessionDurationMinutes?: Nullable<number>);
    get loginRedirectUrl(): Nullable<string>;
    get signupRedirectUrl(): Nullable<string>;
    get organizationId(): Nullable<string>;
    get organizationSlug(): Nullable<string>;
    get customScopes(): Nullable<KtList<string>>;
    get providerParams(): Nullable<KtMap<string, string>>;
    get sessionDurationMinutes(): Nullable<number>;
    toOAuthStartParameters(): OAuthStartParameters;
}
export declare namespace B2BOAuthStartParameters {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BOAuthStartParameters;
    }
}
export declare class B2BSSOStartParameters {
    constructor(connectionId: string, loginRedirectUrl?: Nullable<string>, signupRedirectUrl?: Nullable<string>, sessionDurationMinutes?: Nullable<number>);
    get connectionId(): string;
    get loginRedirectUrl(): Nullable<string>;
    get signupRedirectUrl(): Nullable<string>;
    get sessionDurationMinutes(): Nullable<number>;
    toOAuthStartParameters(): OAuthStartParameters;
}
export declare namespace B2BSSOStartParameters {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BSSOStartParameters;
    }
}
export declare class OAuthStartParameters {
    constructor(loginRedirectUrl?: Nullable<string>, signupRedirectUrl?: Nullable<string>, customScopes?: Nullable<KtList<string>>, providerParams?: Nullable<KtMap<string, string>>, oauthAttachToken?: Nullable<string>, sessionDurationMinutes?: Nullable<number>);
    get loginRedirectUrl(): Nullable<string>;
    get signupRedirectUrl(): Nullable<string>;
    get customScopes(): Nullable<KtList<string>>;
    get providerParams(): Nullable<KtMap<string, string>>;
    get oauthAttachToken(): Nullable<string>;
    get sessionDurationMinutes(): Nullable<number>;
}
export declare namespace OAuthStartParameters {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => OAuthStartParameters;
    }
}
export declare class PasskeysParameters {
    constructor(domain: string, sessionDurationMinutes?: Nullable<number>, preferImmediatelyAvailableCredentials?: boolean);
    get domain(): string;
    get sessionDurationMinutes(): Nullable<number>;
    get preferImmediatelyAvailableCredentials(): boolean;
}
export declare namespace PasskeysParameters {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => PasskeysParameters;
    }
}
export declare class StytchPlatformPersistenceClient {
    constructor(bridge: typeof StytchBridge);
    saveData(key: string, data: string): void;
    getData(key: string): Nullable<string>;
    removeData(key: string): void;
    reset(): void;
}
export declare namespace StytchPlatformPersistenceClient {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => StytchPlatformPersistenceClient;
    }
}
export declare interface B2BCreateExternalConnectionParameters {
    readonly displayName: string;
    readonly externalConnectionId: string;
    readonly externalOrganizationId: string;
}
export declare interface B2BCreateOIDCConnectionParameters {
    readonly displayName: string;
    readonly identityProvider?: Nullable<string>;
}
export declare interface B2BCreateSAMLConnectionParameters {
    readonly displayName: string;
    readonly identityProvider?: Nullable<string>;
}
export declare interface B2BDiscoveryIntermediateSessionsExchangeParameters {
    readonly organizationId: string;
    readonly sessionDurationMinutes: number;
    readonly locale?: Nullable<string>;
}
export declare interface B2BDiscoveryOrganizationsCreateParameters {
    readonly sessionDurationMinutes: number;
    readonly emailAllowedDomains: KtList<string>;
    readonly allowedAuthMethods: KtList<string>;
    readonly allowedMfaMethods: KtList<string>;
    readonly organizationName?: Nullable<string>;
    readonly organizationSlug?: Nullable<string>;
    readonly organizationLogoUrl?: Nullable<string>;
    readonly ssoJitProvisioning?: Nullable<string>;
    readonly emailJitProvisioning?: Nullable<string>;
    readonly emailInvites?: Nullable<string>;
    readonly authMethods?: Nullable<string>;
    readonly mfaMethods?: Nullable<string>;
    readonly mfaPolicy?: Nullable<string>;
    readonly oauthTenantJitProvisioning?: Nullable<string>;
    readonly allowedOauthTenants?: Nullable<KtMap<string, Nullable<any>>>;
}
export declare interface B2BDiscoveryOrganizationsParameters {
}
export declare interface B2BDiscoveryPasswordResetParameters {
    readonly passwordResetToken: string;
    readonly password: string;
}
export declare interface B2BDiscoveryPasswordResetStartParameters {
    readonly emailAddress: string;
    readonly discoveryRedirectUrl?: Nullable<string>;
    readonly resetPasswordRedirectUrl?: Nullable<string>;
    readonly resetPasswordExpirationMinutes?: Nullable<number>;
    readonly resetPasswordTemplateId?: Nullable<string>;
    readonly locale?: Nullable<string>;
    readonly verifyEmailTemplateId?: Nullable<string>;
}
export declare interface B2BGetSCIMConnectionGroupsParameters {
    readonly cursor?: Nullable<string>;
    readonly limit?: Nullable<number>;
}
export declare interface B2BImpersonationTokenAuthenticateParameters {
    readonly impersonationToken: string;
}
export declare interface B2BMagicLinksAuthenticateParameters {
    readonly magicLinksToken: string;
    readonly sessionDurationMinutes: number;
    readonly locale?: Nullable<string>;
}
export declare interface B2BMagicLinksDiscoveryAuthenticateParameters {
    readonly discoveryMagicLinksToken: string;
}
export declare interface B2BMagicLinksDiscoveryEmailSendParameters {
    readonly emailAddress: string;
    readonly discoveryRedirectUrl?: Nullable<string>;
    readonly loginTemplateId?: Nullable<string>;
    readonly locale?: Nullable<string>;
    readonly discoveryExpirationMinutes?: Nullable<number>;
}
export declare interface B2BMagicLinksInviteParameters {
    readonly emailAddress: string;
    readonly roles: KtList<string>;
    readonly inviteRedirectUrl?: Nullable<string>;
    readonly inviteTemplateId?: Nullable<string>;
    readonly name?: Nullable<string>;
    readonly untrustedMetadata?: Nullable<KtMap<string, Nullable<any>>>;
    readonly locale?: Nullable<string>;
}
export declare interface B2BMagicLinksLoginOrSignupParameters {
    readonly emailAddress: string;
    readonly organizationId: string;
    readonly loginRedirectUrl?: Nullable<string>;
    readonly signupRedirectUrl?: Nullable<string>;
    readonly loginTemplateId?: Nullable<string>;
    readonly signupTemplateId?: Nullable<string>;
    readonly locale?: Nullable<string>;
}
export declare interface B2BOAuthAuthenticateParameters {
    readonly oauthToken: string;
    readonly sessionDurationMinutes?: Nullable<number>;
    readonly locale?: Nullable<string>;
}
export declare interface B2BOAuthAuthorizeParameters {
    readonly clientId: string;
    readonly redirectUri: string;
    readonly responseType: string;
    readonly scope: string;
    readonly state?: Nullable<string>;
    readonly nonce?: Nullable<string>;
    readonly consentGranted?: Nullable<string>;
    readonly prompt?: Nullable<string>;
    readonly resources?: Nullable<KtList<string>>;
}
export declare interface B2BOAuthAuthorizeStartParameters {
    readonly clientId: string;
    readonly redirectUri: string;
    readonly responseType: string;
    readonly scope: string;
    readonly prompt?: Nullable<string>;
}
export declare interface B2BOAuthDiscoveryAuthenticateParameters {
    readonly discoveryOauthToken: string;
}
export declare interface B2BOAuthGoogleOneTapDiscoverySubmitParameters {
    readonly idToken: string;
    readonly discoveryRedirectUrl?: Nullable<string>;
}
export declare interface B2BOAuthGoogleOneTapSubmitParameters {
    readonly idToken: string;
    readonly organizationId: string;
    readonly loginRedirectUrl?: Nullable<string>;
    readonly signupRedirectUrl?: Nullable<string>;
}
export declare interface B2BOTPsEmailAuthenticateParameters {
    readonly organizationId: string;
    readonly emailAddress: string;
    readonly code: string;
    readonly sessionDurationMinutes: number;
    readonly locale?: Nullable<string>;
}
export declare interface B2BOTPsEmailDiscoveryAuthenticateParameters {
    readonly emailAddress: string;
    readonly code: string;
}
export declare interface B2BOTPsEmailDiscoverySendParameters {
    readonly emailAddress: string;
    readonly loginTemplateId?: Nullable<string>;
    readonly locale?: Nullable<string>;
    readonly discoveryExpirationMinutes?: Nullable<number>;
}
export declare interface B2BOTPsEmailLoginOrSignupParameters {
    readonly organizationId: string;
    readonly emailAddress: string;
    readonly loginTemplateId?: Nullable<string>;
    readonly signupTemplateId?: Nullable<string>;
    readonly locale?: Nullable<string>;
    readonly loginExpirationMinutes?: Nullable<number>;
    readonly signupExpirationMinutes?: Nullable<number>;
}
export declare interface B2BOTPsSMSAuthenticateParameters {
    readonly organizationId: string;
    readonly memberId: string;
    readonly code: string;
    readonly sessionDurationMinutes: number;
    readonly setMfaEnrollment?: Nullable<string>;
}
export declare interface B2BOTPsSMSSendParameters {
    readonly organizationId: string;
    readonly memberId: string;
    readonly mfaPhoneNumber?: Nullable<string>;
    readonly locale?: Nullable<string>;
    readonly enableAutofill?: Nullable<boolean>;
}
export declare interface B2BOrganizationsUpdateParameters {
    readonly organizationName?: Nullable<string>;
    readonly organizationSlug?: Nullable<string>;
    readonly organizationLogoUrl?: Nullable<string>;
    readonly ssoDefaultConnectionId?: Nullable<string>;
    readonly ssoJitProvisioning?: Nullable<string>;
    readonly ssoJitProvisioningAllowedConnections?: Nullable<KtList<string>>;
    readonly emailAllowedDomains?: Nullable<KtList<string>>;
    readonly emailJitProvisioning?: Nullable<string>;
    readonly emailInvites?: Nullable<string>;
    readonly authMethods?: Nullable<string>;
    readonly allowedAuthMethods?: Nullable<KtList<string>>;
    readonly mfaPolicy?: Nullable<string>;
    readonly rbacEmailImplicitRoleAssignments?: Nullable<KtList<string>>;
    readonly mfaMethods?: Nullable<string>;
    readonly allowedMfaMethods?: Nullable<KtList<string>>;
    readonly oauthTenantJitProvisioning?: Nullable<string>;
    readonly allowedOauthTenants?: Nullable<KtMap<string, Nullable<any>>>;
    readonly firstPartyConnectedAppsAllowedType?: Nullable<string>;
    readonly allowedFirstPartyConnectedApps?: Nullable<KtList<string>>;
    readonly thirdPartyConnectedAppsAllowedType?: Nullable<string>;
    readonly allowedThirdPartyConnectedApps?: Nullable<KtList<string>>;
    readonly organizationExternalId?: Nullable<string>;
}
export declare interface B2BPasswordAuthenticateParameters {
    readonly organizationId: string;
    readonly emailAddress: string;
    readonly password: string;
    readonly sessionDurationMinutes: number;
    readonly locale?: Nullable<string>;
}
export declare interface B2BPasswordDiscoveryAuthenticateParameters {
    readonly emailAddress: string;
    readonly password: string;
}
export declare interface B2BPasswordEmailResetParameters {
    readonly passwordResetToken: string;
    readonly password: string;
    readonly sessionDurationMinutes: number;
    readonly locale?: Nullable<string>;
}
export declare interface B2BPasswordEmailResetStartParameters {
    readonly organizationId: string;
    readonly emailAddress: string;
    readonly loginRedirectUrl?: Nullable<string>;
    readonly resetPasswordRedirectUrl?: Nullable<string>;
    readonly resetPasswordExpirationMinutes?: Nullable<number>;
    readonly resetPasswordTemplateId?: Nullable<string>;
    readonly locale?: Nullable<string>;
    readonly verifyEmailTemplateId?: Nullable<string>;
}
export declare interface B2BPasswordExistingPasswordResetParameters {
    readonly organizationId: string;
    readonly emailAddress: string;
    readonly existingPassword: string;
    readonly newPassword: string;
    readonly sessionDurationMinutes: number;
    readonly locale?: Nullable<string>;
}
export declare interface B2BPasswordSessionResetParameters {
    readonly password: string;
}
export declare interface B2BPasswordStrengthCheckParameters {
    readonly password: string;
    readonly emailAddress?: Nullable<string>;
}
export declare interface B2BRecoveryCodesRecoverParameters {
    readonly organizationId: string;
    readonly memberId: string;
    readonly recoveryCode: string;
    readonly sessionDurationMinutes: number;
}
export declare interface B2BRecoveryCodesRotateParameters {
}
export declare interface B2BSCIMCreateConnectionParameters {
    readonly displayName: string;
    readonly identityProvider?: Nullable<string>;
}
export declare interface B2BSCIMUpdateConnectionParameters {
    readonly identityProvider?: Nullable<string>;
    readonly displayName?: Nullable<string>;
    readonly scimGroupImplicitRoleAssignments?: Nullable<KtList<string>>;
}
export declare interface B2BSSOAuthEnticateParameters {
    readonly ssoToken: string;
    readonly sessionDurationMinutes: number;
    readonly locale?: Nullable<string>;
}
export declare interface B2BSessionsAccessTokenExchangeParameters {
    readonly accessToken: string;
    readonly sessionDurationMinutes: number;
}
export declare interface B2BSessionsAttestParameters {
    readonly profileId: string;
    readonly token: string;
    readonly organizationId?: Nullable<string>;
    readonly sessionDurationMinutes?: Nullable<number>;
}
export declare interface B2BSessionsAuthenticateParameters {
    readonly sessionDurationMinutes?: Nullable<number>;
}
export declare interface B2BSessionsExchangeParameters {
    readonly organizationId: string;
    readonly sessionDurationMinutes: number;
    readonly locale?: Nullable<string>;
}
export declare interface B2BTOTPsAuthenticateParameters {
    readonly organizationId: string;
    readonly memberId: string;
    readonly code: string;
    readonly sessionDurationMinutes: number;
    readonly setMfaEnrollment?: Nullable<string>;
    readonly setDefaultMfa?: Nullable<boolean>;
}
export declare interface B2BTOTPsCreateParameters {
    readonly organizationId: string;
    readonly memberId: string;
    readonly expirationMinutes?: Nullable<number>;
}
export declare interface B2BUpdateExternalConnectionParameters {
    readonly displayName: string;
    readonly externalConnectionImplicitRoleAssignments?: Nullable<KtList<string>>;
    readonly externalGroupImplicitRoleAssignments?: Nullable<KtList<string>>;
}
export declare interface B2BUpdateOIDCConnectionParameters {
    readonly displayName: string;
    readonly clientId: string;
    readonly clientSecret: string;
    readonly issuer: string;
    readonly authorizationUrl: string;
    readonly tokenUrl: string;
    readonly jwksUrl: string;
    readonly customScopes: string;
    readonly userinfoUrl?: Nullable<string>;
    readonly identityProvider?: Nullable<string>;
    readonly attributeMapping?: Nullable<KtMap<string, Nullable<any>>>;
}
export declare interface B2BUpdateSAMLConnectionByURLParameters {
    readonly metadataUrl: string;
}
export declare interface B2BUpdateSAMLConnectionParameters {
    readonly idpEntityId: string;
    readonly displayName: string;
    readonly x509Certificate: string;
    readonly idpSsoUrl: string;
    readonly attributeMapping?: Nullable<KtMap<string, Nullable<any>>>;
    readonly samlConnectionImplicitRoleAssignments?: Nullable<KtList<string>>;
    readonly samlGroupImplicitRoleAssignments?: Nullable<KtList<string>>;
    readonly alternativeAudienceUri?: Nullable<string>;
    readonly identityProvider?: Nullable<string>;
    readonly signingPrivateKey?: Nullable<string>;
    readonly allowGatewayCallback?: Nullable<boolean>;
}
export declare interface BiometricsAuthenticateParameters {
    readonly sessionDurationMinutes: number;
}
export declare interface BiometricsAuthenticateStartParameters {
}
export declare interface BiometricsRegisterParameters {
    readonly sessionDurationMinutes: number;
}
export declare interface BiometricsRegisterStartParameters {
}
export declare interface CryptoWalletsAuthenticateParameters {
    readonly cryptoWalletAddress: string;
    readonly cryptoWalletType: string;
    readonly sessionDurationMinutes: number;
}
export declare interface CryptoWalletsAuthenticateStartSecondaryParameters {
    readonly cryptoWalletType: string;
    readonly cryptoWalletAddress: string;
    readonly siweParams?: Nullable<SDKSIWEParams>;
}
export declare interface ExternalB2BOAuthAuthorizeStartParameters {
    readonly clientId: string;
    readonly redirectUri: string;
    readonly responseType: string;
    readonly scopes: KtList<string>;
    readonly prompt?: Nullable<string>;
}
export declare interface ExternalB2BOAuthAuthorizeSubmitParameters {
    readonly clientId: string;
    readonly redirectUri: string;
    readonly responseType: string;
    readonly scopes: KtList<string>;
    readonly consentGranted: boolean;
    readonly state?: Nullable<string>;
    readonly nonce?: Nullable<string>;
    readonly prompt?: Nullable<string>;
}
export declare interface ExternalOAuthAuthorizeStartParameters {
    readonly clientId: string;
    readonly redirectUri: string;
    readonly responseType: string;
    readonly scopes: KtList<string>;
    readonly prompt?: Nullable<string>;
}
export declare interface ExternalOAuthAuthorizeSubmitParameters {
    readonly clientId: string;
    readonly redirectUri: string;
    readonly responseType: string;
    readonly scopes: KtList<string>;
    readonly consentGranted: boolean;
    readonly state?: Nullable<string>;
    readonly nonce?: Nullable<string>;
    readonly prompt?: Nullable<string>;
    readonly resources?: Nullable<KtList<string>>;
}
export declare interface ImpersonationTokenAuthenticateParameters {
    readonly impersonationToken: string;
}
export declare interface MagicLinksAuthenticateParameters {
    readonly token: string;
    readonly sessionDurationMinutes: number;
}
export declare interface MagicLinksEmailLoginOrCreateParameters {
    readonly email: string;
    readonly loginMagicLinkUrl?: Nullable<string>;
    readonly signupMagicLinkUrl?: Nullable<string>;
    readonly loginExpirationMinutes?: Nullable<number>;
    readonly signupExpirationMinutes?: Nullable<number>;
    readonly loginTemplateId?: Nullable<string>;
    readonly signupTemplateId?: Nullable<string>;
    readonly locale?: Nullable<string>;
}
export declare interface MagicLinksEmailSendSecondaryParameters {
    readonly email: string;
    readonly loginMagicLinkUrl?: Nullable<string>;
    readonly signupMagicLinkUrl?: Nullable<string>;
    readonly loginExpirationMinutes?: Nullable<number>;
    readonly signupExpirationMinutes?: Nullable<number>;
    readonly loginTemplateId?: Nullable<string>;
    readonly signupTemplateId?: Nullable<string>;
    readonly locale?: Nullable<string>;
}
export declare interface MemberSearchByEmailParameters {
    readonly organizationId: string;
    readonly emailAddress: string;
}
export declare interface OAuthAppleIDTokenAuthenticateParameters {
    readonly idToken: string;
    readonly sessionDurationMinutes: number;
    readonly name?: Nullable<ApiUserV1Name>;
    readonly nonce?: Nullable<string>;
    readonly oauthAttachToken?: Nullable<string>;
}
export declare interface OAuthAttachParameters {
    readonly provider: string;
}
export declare interface OAuthAuthenticateParameters {
    readonly token: string;
    readonly sessionDurationMinutes: number;
}
export declare interface OAuthAuthorizeParameters {
    readonly clientId: string;
    readonly redirectUri: string;
    readonly responseType: string;
    readonly scope: string;
    readonly state?: Nullable<string>;
    readonly nonce?: Nullable<string>;
    readonly consentGranted?: Nullable<string>;
    readonly prompt?: Nullable<string>;
    readonly resources?: Nullable<KtList<string>>;
}
export declare interface OAuthAuthorizeStartParameters {
    readonly clientId: string;
    readonly redirectUri: string;
    readonly responseType: string;
    readonly scope: string;
    readonly prompt?: Nullable<string>;
}
export declare interface OAuthGoogleIDTokenAuthenticateParameters {
    readonly idToken: string;
    readonly sessionDurationMinutes: number;
    readonly nonce?: Nullable<string>;
    readonly oauthAttachToken?: Nullable<string>;
}
export declare interface OIDCLogoutParameters {
    readonly clientId: string;
    readonly postLogoutRedirectUri: string;
    readonly state: string;
    readonly idTokenHint?: Nullable<string>;
}
export declare interface OTPsAuthenticateParameters {
    readonly token: string;
    readonly methodId: string;
    readonly sessionDurationMinutes: number;
}
export declare interface OTPsEmailLoginOrCreateParameters {
    readonly email: string;
    readonly expirationMinutes?: Nullable<number>;
    readonly signupTemplateId?: Nullable<string>;
    readonly loginTemplateId?: Nullable<string>;
    readonly locale?: Nullable<string>;
}
export declare interface OTPsEmailSendSecondaryParameters {
    readonly email: string;
    readonly expirationMinutes?: Nullable<number>;
    readonly signupTemplateId?: Nullable<string>;
    readonly loginTemplateId?: Nullable<string>;
    readonly locale?: Nullable<string>;
}
export declare interface OTPsSMSLoginOrCreateParameters {
    readonly phoneNumber: string;
    readonly expirationMinutes?: Nullable<number>;
    readonly locale?: Nullable<string>;
    readonly enableAutofill?: Nullable<boolean>;
}
export declare interface OTPsSMSSendSecondaryParameters {
    readonly phoneNumber: string;
    readonly expirationMinutes?: Nullable<number>;
    readonly locale?: Nullable<string>;
    readonly enableAutofill?: Nullable<boolean>;
}
export declare interface OTPsWhatsAppLoginOrCreateParameters {
    readonly phoneNumber: string;
    readonly expirationMinutes?: Nullable<number>;
    readonly locale?: Nullable<string>;
}
export declare interface OTPsWhatsAppSendSecondaryParameters {
    readonly phoneNumber: string;
    readonly expirationMinutes?: Nullable<number>;
    readonly locale?: Nullable<string>;
}
export declare interface OrgSearchBySlugParameters {
    readonly organizationSlug: string;
}
export declare interface OrganizationsAdminMemberStartEmailUpdateParameters {
    readonly emailAddress: string;
    readonly loginRedirectUrl?: Nullable<string>;
    readonly locale?: Nullable<string>;
    readonly loginTemplateId?: Nullable<string>;
    readonly deliveryMethod?: Nullable<string>;
}
export declare interface OrganizationsAdminMemberUnlinkRetiredEmailParameters {
    readonly emailId?: Nullable<string>;
    readonly emailAddress?: Nullable<string>;
}
export declare interface OrganizationsAdminMemberUpdateParameters {
    readonly preserveExistingSessions: boolean;
    readonly name?: Nullable<string>;
    readonly mfaEnrolled?: Nullable<boolean>;
    readonly mfaPhoneNumber?: Nullable<string>;
    readonly untrustedMetadata?: Nullable<KtMap<string, Nullable<any>>>;
    readonly isBreakglass?: Nullable<boolean>;
    readonly roles?: Nullable<KtList<string>>;
    readonly defaultMfaMethod?: Nullable<string>;
    readonly emailAddress?: Nullable<string>;
    readonly unlinkEmail?: Nullable<boolean>;
}
export declare interface OrganizationsMemberCreateParameters {
    readonly emailAddress: string;
    readonly isBreakglass: boolean;
    readonly createMemberAsPending: boolean;
    readonly roles: KtList<string>;
    readonly name?: Nullable<string>;
    readonly mfaEnrolled?: Nullable<boolean>;
    readonly mfaPhoneNumber?: Nullable<string>;
    readonly untrustedMetadata?: Nullable<KtMap<string, Nullable<any>>>;
}
export declare interface OrganizationsMemberSearchParameters {
    readonly cursor: string;
    readonly limit?: Nullable<number>;
    readonly query?: Nullable<ApiOrganizationV1ExternalSearchQuery>;
}
export declare interface OrganizationsMemberStartEmailUpdateParameters {
    readonly emailAddress: string;
    readonly loginRedirectUrl?: Nullable<string>;
    readonly locale?: Nullable<string>;
    readonly loginTemplateId?: Nullable<string>;
    readonly deliveryMethod?: Nullable<string>;
}
export declare interface OrganizationsMemberUnlinkRetiredEmailParameters {
    readonly emailId?: Nullable<string>;
    readonly emailAddress?: Nullable<string>;
}
export declare interface OrganizationsMemberUpdateParameters {
    readonly name?: Nullable<string>;
    readonly mfaEnrolled?: Nullable<boolean>;
    readonly mfaPhoneNumber?: Nullable<string>;
    readonly untrustedMetadata?: Nullable<KtMap<string, Nullable<any>>>;
    readonly defaultMfaMethod?: Nullable<string>;
}
export declare interface PasswordsAuthenticateParameters {
    readonly email: string;
    readonly password: string;
    readonly sessionDurationMinutes: number;
}
export declare interface PasswordsCreateParameters {
    readonly email: string;
    readonly password: string;
    readonly sessionDurationMinutes: number;
}
export declare interface PasswordsEmailResetParameters {
    readonly token: string;
    readonly password: string;
    readonly sessionDurationMinutes: number;
}
export declare interface PasswordsEmailResetStartParameters {
    readonly email: string;
    readonly loginRedirectUrl?: Nullable<string>;
    readonly resetPasswordRedirectUrl?: Nullable<string>;
    readonly resetPasswordExpirationMinutes?: Nullable<number>;
    readonly resetPasswordTemplateId?: Nullable<string>;
}
export declare interface PasswordsExistingPasswordResetParameters {
    readonly email: string;
    readonly existingPassword: string;
    readonly newPassword: string;
    readonly sessionDurationMinutes?: Nullable<number>;
}
export declare interface PasswordsSessionResetParameters {
    readonly password: string;
    readonly sessionDurationMinutes?: Nullable<number>;
}
export declare interface PasswordsStrengthCheckParameters {
    readonly password: string;
    readonly email?: Nullable<string>;
}
export declare interface SCIMRotateTokenCancelParameters {
    readonly connectionId: string;
}
export declare interface SCIMRotateTokenCompleteParameters {
    readonly connectionId: string;
}
export declare interface SCIMRotateTokenStartParameters {
    readonly connectionId: string;
}
export declare interface SessionsAccessTokenExchangeParameters {
    readonly accessToken: string;
    readonly sessionDurationMinutes: number;
}
export declare interface SessionsAttestParameters {
    readonly profileId: string;
    readonly token: string;
    readonly sessionDurationMinutes?: Nullable<number>;
}
export declare interface SessionsAuthenticateParameters {
    readonly sessionDurationMinutes?: Nullable<number>;
}
export declare interface TOTPsAuthenticateParameters {
    readonly totpCode: string;
    readonly sessionDurationMinutes: number;
}
export declare interface TOTPsCreateParameters {
    readonly expirationMinutes?: Nullable<number>;
}
export declare interface TOTPsRecoverParameters {
    readonly recoveryCode: string;
    readonly sessionDurationMinutes: number;
}
export declare interface UpdateMeParameters {
    readonly name?: Nullable<ApiUserV1Name>;
    readonly emails?: Nullable<KtList<ApiUserV1EmailString>>;
    readonly phoneNumbers?: Nullable<KtList<ApiUserV1PhoneNumberString>>;
    readonly cryptoWallets?: Nullable<KtList<ApiUserV1CryptoWalletString>>;
    readonly trustedMetadata?: Nullable<KtMap<string, Nullable<any>>>;
    readonly untrustedMetadata?: Nullable<KtMap<string, Nullable<any>>>;
}
export declare interface UserSearchByEmailParameters {
    readonly email: string;
}
export declare interface WebAuthnAuthenticateParameters {
    readonly publicKeyCredential: string;
    readonly sessionDurationMinutes?: Nullable<number>;
}
export declare interface WebAuthnAuthenticateStartSecondaryParameters {
    readonly domain: string;
}
export declare interface WebAuthnRegisterParameters {
    readonly publicKeyCredential: string;
    readonly sessionDurationMinutes?: Nullable<number>;
}
export declare interface WebAuthnRegisterStartParameters {
    readonly domain: string;
    readonly overrideId?: Nullable<string>;
    readonly overrideName?: Nullable<string>;
    readonly overrideDisplayName?: Nullable<string>;
}
export declare interface WebAuthnUpdateParameters {
    readonly name: string;
}
export declare class ActiveSSOConnection {
    constructor(connectionId: string, displayName: string, identityProvider: string);
    get connectionId(): string;
    get displayName(): string;
    get identityProvider(): string;
    copy(connectionId?: string, displayName?: string, identityProvider?: string): ActiveSSOConnection;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ActiveSSOConnection {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ActiveSSOConnection;
    }
}
export declare class ApiAttributeV1Attributes {
    constructor(ipAddress?: Nullable<string>, userAgent?: Nullable<string>);
    get ipAddress(): Nullable<string>;
    get userAgent(): Nullable<string>;
    copy(ipAddress?: Nullable<string>, userAgent?: Nullable<string>): ApiAttributeV1Attributes;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiAttributeV1Attributes {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiAttributeV1Attributes;
    }
}
export declare class ApiB2bIdpV1ScopeResult {
    constructor(scope: string, description: string, isGrantable: boolean);
    get scope(): string;
    get description(): string;
    get isGrantable(): boolean;
    copy(scope?: string, description?: string, isGrantable?: boolean): ApiB2bIdpV1ScopeResult;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiB2bIdpV1ScopeResult {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiB2bIdpV1ScopeResult;
    }
}
export declare class ApiB2bMfaV1MemberOptions {
    constructor(mfaPhoneNumber: string, totpRegistrationId: string);
    get mfaPhoneNumber(): string;
    get totpRegistrationId(): string;
    copy(mfaPhoneNumber?: string, totpRegistrationId?: string): ApiB2bMfaV1MemberOptions;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiB2bMfaV1MemberOptions {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiB2bMfaV1MemberOptions;
    }
}
export declare class ApiB2bMfaV1MfaRequired {
    constructor(memberOptions?: Nullable<ApiB2bMfaV1MemberOptions>, secondaryAuthInitiated?: Nullable<string>);
    get memberOptions(): Nullable<ApiB2bMfaV1MemberOptions>;
    get secondaryAuthInitiated(): Nullable<string>;
    copy(memberOptions?: Nullable<ApiB2bMfaV1MemberOptions>, secondaryAuthInitiated?: Nullable<string>): ApiB2bMfaV1MfaRequired;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiB2bMfaV1MfaRequired {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiB2bMfaV1MfaRequired;
    }
}
export declare class ApiB2bOauthV1ProviderValues {
    constructor(scopes: KtList<string>, accessToken?: Nullable<string>, refreshToken?: Nullable<string>, expiresAt?: Nullable<any>/* Nullable<Instant> */, idToken?: Nullable<string>);
    get scopes(): KtList<string>;
    get accessToken(): Nullable<string>;
    get refreshToken(): Nullable<string>;
    get expiresAt(): Nullable<any>/* Nullable<Instant> */;
    get idToken(): Nullable<string>;
    copy(scopes?: KtList<string>, accessToken?: Nullable<string>, refreshToken?: Nullable<string>, expiresAt?: Nullable<any>/* Nullable<Instant> */, idToken?: Nullable<string>): ApiB2bOauthV1ProviderValues;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiB2bOauthV1ProviderValues {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiB2bOauthV1ProviderValues;
    }
}
export declare class ApiB2bPasswordV1LudsFeedback {
    constructor(hasLowerCase: boolean, hasUpperCase: boolean, hasDigit: boolean, hasSymbol: boolean, missingComplexity: number, missingCharacters: number);
    get hasLowerCase(): boolean;
    get hasUpperCase(): boolean;
    get hasDigit(): boolean;
    get hasSymbol(): boolean;
    get missingComplexity(): number;
    get missingCharacters(): number;
    copy(hasLowerCase?: boolean, hasUpperCase?: boolean, hasDigit?: boolean, hasSymbol?: boolean, missingComplexity?: number, missingCharacters?: number): ApiB2bPasswordV1LudsFeedback;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiB2bPasswordV1LudsFeedback {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiB2bPasswordV1LudsFeedback;
    }
}
export declare class ApiB2bPasswordV1ZxcvbnFeedback {
    constructor(warning: string, suggestions: KtList<string>);
    get warning(): string;
    get suggestions(): KtList<string>;
    copy(warning?: string, suggestions?: KtList<string>): ApiB2bPasswordV1ZxcvbnFeedback;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiB2bPasswordV1ZxcvbnFeedback {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiB2bPasswordV1ZxcvbnFeedback;
    }
}
export declare class ApiB2bRbacV1Policy {
    constructor(roles: KtList<ApiB2bRbacV1PolicyRole>, resources: KtList<ApiB2bRbacV1PolicyResource>, scopes: KtList<ApiB2bRbacV1PolicyScope>);
    get roles(): KtList<ApiB2bRbacV1PolicyRole>;
    get resources(): KtList<ApiB2bRbacV1PolicyResource>;
    get scopes(): KtList<ApiB2bRbacV1PolicyScope>;
    copy(roles?: KtList<ApiB2bRbacV1PolicyRole>, resources?: KtList<ApiB2bRbacV1PolicyResource>, scopes?: KtList<ApiB2bRbacV1PolicyScope>): ApiB2bRbacV1Policy;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiB2bRbacV1Policy {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiB2bRbacV1Policy;
    }
}
export declare class ApiB2bRbacV1PolicyResource {
    constructor(resourceId: string, description: string, actions: KtList<string>);
    get resourceId(): string;
    get description(): string;
    get actions(): KtList<string>;
    copy(resourceId?: string, description?: string, actions?: KtList<string>): ApiB2bRbacV1PolicyResource;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiB2bRbacV1PolicyResource {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiB2bRbacV1PolicyResource;
    }
}
export declare class ApiB2bRbacV1PolicyRole {
    constructor(roleId: string, description: string, permissions: KtList<ApiB2bRbacV1PolicyRolePermission>);
    get roleId(): string;
    get description(): string;
    get permissions(): KtList<ApiB2bRbacV1PolicyRolePermission>;
    copy(roleId?: string, description?: string, permissions?: KtList<ApiB2bRbacV1PolicyRolePermission>): ApiB2bRbacV1PolicyRole;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiB2bRbacV1PolicyRole {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiB2bRbacV1PolicyRole;
    }
}
export declare class ApiB2bRbacV1PolicyRolePermission {
    constructor(resourceId: string, actions: KtList<string>);
    get resourceId(): string;
    get actions(): KtList<string>;
    copy(resourceId?: string, actions?: KtList<string>): ApiB2bRbacV1PolicyRolePermission;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiB2bRbacV1PolicyRolePermission {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiB2bRbacV1PolicyRolePermission;
    }
}
export declare class ApiB2bRbacV1PolicyScope {
    constructor(scope: string, description: string, permissions: KtList<ApiB2bRbacV1PolicyScopePermission>);
    get scope(): string;
    get description(): string;
    get permissions(): KtList<ApiB2bRbacV1PolicyScopePermission>;
    copy(scope?: string, description?: string, permissions?: KtList<ApiB2bRbacV1PolicyScopePermission>): ApiB2bRbacV1PolicyScope;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiB2bRbacV1PolicyScope {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiB2bRbacV1PolicyScope;
    }
}
export declare class ApiB2bRbacV1PolicyScopePermission {
    constructor(resourceId: string, actions: KtList<string>);
    get resourceId(): string;
    get actions(): KtList<string>;
    copy(resourceId?: string, actions?: KtList<string>): ApiB2bRbacV1PolicyScopePermission;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiB2bRbacV1PolicyScopePermission {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiB2bRbacV1PolicyScopePermission;
    }
}
export declare class ApiB2bScimV1Address {
    constructor(formatted: string, streetAddress: string, locality: string, region: string, postalCode: string, country: string, type: string, primary: boolean);
    get formatted(): string;
    get streetAddress(): string;
    get locality(): string;
    get region(): string;
    get postalCode(): string;
    get country(): string;
    get type(): string;
    get primary(): boolean;
    copy(formatted?: string, streetAddress?: string, locality?: string, region?: string, postalCode?: string, country?: string, type?: string, primary?: boolean): ApiB2bScimV1Address;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiB2bScimV1Address {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiB2bScimV1Address;
    }
}
export declare class ApiB2bScimV1Email {
    constructor(value: string, type: string, primary: boolean);
    get value(): string;
    get type(): string;
    get primary(): boolean;
    copy(value?: string, type?: string, primary?: boolean): ApiB2bScimV1Email;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiB2bScimV1Email {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiB2bScimV1Email;
    }
}
export declare class ApiB2bScimV1EnterpriseExtension {
    constructor(employeeNumber: string, costCenter: string, division: string, department: string, organization: string, manager?: Nullable<ApiB2bScimV1Manager>);
    get employeeNumber(): string;
    get costCenter(): string;
    get division(): string;
    get department(): string;
    get organization(): string;
    get manager(): Nullable<ApiB2bScimV1Manager>;
    copy(employeeNumber?: string, costCenter?: string, division?: string, department?: string, organization?: string, manager?: Nullable<ApiB2bScimV1Manager>): ApiB2bScimV1EnterpriseExtension;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiB2bScimV1EnterpriseExtension {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiB2bScimV1EnterpriseExtension;
    }
}
export declare class ApiB2bScimV1Entitlement {
    constructor(value: string, type: string, primary: boolean);
    get value(): string;
    get type(): string;
    get primary(): boolean;
    copy(value?: string, type?: string, primary?: boolean): ApiB2bScimV1Entitlement;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiB2bScimV1Entitlement {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiB2bScimV1Entitlement;
    }
}
export declare class ApiB2bScimV1Group {
    constructor(value: string, display: string);
    get value(): string;
    get display(): string;
    copy(value?: string, display?: string): ApiB2bScimV1Group;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiB2bScimV1Group {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiB2bScimV1Group;
    }
}
export declare class ApiB2bScimV1IMs {
    constructor(value: string, type: string, primary: boolean);
    get value(): string;
    get type(): string;
    get primary(): boolean;
    copy(value?: string, type?: string, primary?: boolean): ApiB2bScimV1IMs;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiB2bScimV1IMs {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiB2bScimV1IMs;
    }
}
export declare class ApiB2bScimV1Manager {
    constructor(value: string, ref: string, displayName: string);
    get value(): string;
    get ref(): string;
    get displayName(): string;
    copy(value?: string, ref?: string, displayName?: string): ApiB2bScimV1Manager;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiB2bScimV1Manager {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiB2bScimV1Manager;
    }
}
export declare class ApiB2bScimV1Name {
    constructor(formatted: string, familyName: string, givenName: string, middleName: string, honorificPrefix: string, honorificSuffix: string);
    get formatted(): string;
    get familyName(): string;
    get givenName(): string;
    get middleName(): string;
    get honorificPrefix(): string;
    get honorificSuffix(): string;
    copy(formatted?: string, familyName?: string, givenName?: string, middleName?: string, honorificPrefix?: string, honorificSuffix?: string): ApiB2bScimV1Name;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiB2bScimV1Name {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiB2bScimV1Name;
    }
}
export declare class ApiB2bScimV1PhoneNumber {
    constructor(value: string, type: string, primary: boolean);
    get value(): string;
    get type(): string;
    get primary(): boolean;
    copy(value?: string, type?: string, primary?: boolean): ApiB2bScimV1PhoneNumber;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiB2bScimV1PhoneNumber {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiB2bScimV1PhoneNumber;
    }
}
export declare class ApiB2bScimV1Photo {
    constructor(value: string, type: string, primary: boolean);
    get value(): string;
    get type(): string;
    get primary(): boolean;
    copy(value?: string, type?: string, primary?: boolean): ApiB2bScimV1Photo;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiB2bScimV1Photo {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiB2bScimV1Photo;
    }
}
export declare class ApiB2bScimV1Role {
    constructor(value: string, type: string, primary: boolean);
    get value(): string;
    get type(): string;
    get primary(): boolean;
    copy(value?: string, type?: string, primary?: boolean): ApiB2bScimV1Role;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiB2bScimV1Role {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiB2bScimV1Role;
    }
}
export declare class ApiB2bScimV1SCIMAttributes {
    constructor(userName: string, id: string, externalId: string, active: boolean, groups: KtList<ApiB2bScimV1Group>, displayName: string, nickName: string, profileUrl: string, userType: string, title: string, preferredLanguage: string, locale: string, timezone: string, emails: KtList<ApiB2bScimV1Email>, phoneNumbers: KtList<ApiB2bScimV1PhoneNumber>, addresses: KtList<ApiB2bScimV1Address>, ims: KtList<ApiB2bScimV1IMs>, photos: KtList<ApiB2bScimV1Photo>, entitlements: KtList<ApiB2bScimV1Entitlement>, roles: KtList<ApiB2bScimV1Role>, x509certificates: KtList<ApiB2bScimV1X509Certificate>, name?: Nullable<ApiB2bScimV1Name>, enterpriseExtension?: Nullable<ApiB2bScimV1EnterpriseExtension>);
    get userName(): string;
    get id(): string;
    get externalId(): string;
    get active(): boolean;
    get groups(): KtList<ApiB2bScimV1Group>;
    get displayName(): string;
    get nickName(): string;
    get profileUrl(): string;
    get userType(): string;
    get title(): string;
    get preferredLanguage(): string;
    get locale(): string;
    get timezone(): string;
    get emails(): KtList<ApiB2bScimV1Email>;
    get phoneNumbers(): KtList<ApiB2bScimV1PhoneNumber>;
    get addresses(): KtList<ApiB2bScimV1Address>;
    get ims(): KtList<ApiB2bScimV1IMs>;
    get photos(): KtList<ApiB2bScimV1Photo>;
    get entitlements(): KtList<ApiB2bScimV1Entitlement>;
    get roles(): KtList<ApiB2bScimV1Role>;
    get x509certificates(): KtList<ApiB2bScimV1X509Certificate>;
    get name(): Nullable<ApiB2bScimV1Name>;
    get enterpriseExtension(): Nullable<ApiB2bScimV1EnterpriseExtension>;
    copy(userName?: string, id?: string, externalId?: string, active?: boolean, groups?: KtList<ApiB2bScimV1Group>, displayName?: string, nickName?: string, profileUrl?: string, userType?: string, title?: string, preferredLanguage?: string, locale?: string, timezone?: string, emails?: KtList<ApiB2bScimV1Email>, phoneNumbers?: KtList<ApiB2bScimV1PhoneNumber>, addresses?: KtList<ApiB2bScimV1Address>, ims?: KtList<ApiB2bScimV1IMs>, photos?: KtList<ApiB2bScimV1Photo>, entitlements?: KtList<ApiB2bScimV1Entitlement>, roles?: KtList<ApiB2bScimV1Role>, x509certificates?: KtList<ApiB2bScimV1X509Certificate>, name?: Nullable<ApiB2bScimV1Name>, enterpriseExtension?: Nullable<ApiB2bScimV1EnterpriseExtension>): ApiB2bScimV1SCIMAttributes;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiB2bScimV1SCIMAttributes {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiB2bScimV1SCIMAttributes;
    }
}
export declare class ApiB2bScimV1SCIMConnection {
    constructor(organizationId: string, connectionId: string, status: string, displayName: string, identityProvider: string, baseUrl: string, bearerTokenLastFour: string, scimGroupImplicitRoleAssignments: KtList<ApiB2bScimV1SCIMGroupImplicitRoleAssignments>, nextBearerTokenLastFour: string, bearerTokenExpiresAt?: Nullable<any>/* Nullable<Instant> */, nextBearerTokenExpiresAt?: Nullable<any>/* Nullable<Instant> */);
    get organizationId(): string;
    get connectionId(): string;
    get status(): string;
    get displayName(): string;
    get identityProvider(): string;
    get baseUrl(): string;
    get bearerTokenLastFour(): string;
    get scimGroupImplicitRoleAssignments(): KtList<ApiB2bScimV1SCIMGroupImplicitRoleAssignments>;
    get nextBearerTokenLastFour(): string;
    get bearerTokenExpiresAt(): Nullable<any>/* Nullable<Instant> */;
    get nextBearerTokenExpiresAt(): Nullable<any>/* Nullable<Instant> */;
    copy(organizationId?: string, connectionId?: string, status?: string, displayName?: string, identityProvider?: string, baseUrl?: string, bearerTokenLastFour?: string, scimGroupImplicitRoleAssignments?: KtList<ApiB2bScimV1SCIMGroupImplicitRoleAssignments>, nextBearerTokenLastFour?: string, bearerTokenExpiresAt?: Nullable<any>/* Nullable<Instant> */, nextBearerTokenExpiresAt?: Nullable<any>/* Nullable<Instant> */): ApiB2bScimV1SCIMConnection;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiB2bScimV1SCIMConnection {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiB2bScimV1SCIMConnection;
    }
}
export declare class ApiB2bScimV1SCIMConnectionWithNextToken {
    constructor(organizationId: string, connectionId: string, status: string, displayName: string, baseUrl: string, identityProvider: string, bearerTokenLastFour: string, nextBearerToken: string, scimGroupImplicitRoleAssignments: KtList<ApiB2bScimV1SCIMGroupImplicitRoleAssignments>, bearerTokenExpiresAt?: Nullable<any>/* Nullable<Instant> */, nextBearerTokenExpiresAt?: Nullable<any>/* Nullable<Instant> */);
    get organizationId(): string;
    get connectionId(): string;
    get status(): string;
    get displayName(): string;
    get baseUrl(): string;
    get identityProvider(): string;
    get bearerTokenLastFour(): string;
    get nextBearerToken(): string;
    get scimGroupImplicitRoleAssignments(): KtList<ApiB2bScimV1SCIMGroupImplicitRoleAssignments>;
    get bearerTokenExpiresAt(): Nullable<any>/* Nullable<Instant> */;
    get nextBearerTokenExpiresAt(): Nullable<any>/* Nullable<Instant> */;
    copy(organizationId?: string, connectionId?: string, status?: string, displayName?: string, baseUrl?: string, identityProvider?: string, bearerTokenLastFour?: string, nextBearerToken?: string, scimGroupImplicitRoleAssignments?: KtList<ApiB2bScimV1SCIMGroupImplicitRoleAssignments>, bearerTokenExpiresAt?: Nullable<any>/* Nullable<Instant> */, nextBearerTokenExpiresAt?: Nullable<any>/* Nullable<Instant> */): ApiB2bScimV1SCIMConnectionWithNextToken;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiB2bScimV1SCIMConnectionWithNextToken {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiB2bScimV1SCIMConnectionWithNextToken;
    }
}
export declare class ApiB2bScimV1SCIMConnectionWithToken {
    constructor(organizationId: string, connectionId: string, status: string, displayName: string, identityProvider: string, baseUrl: string, bearerToken: string, scimGroupImplicitRoleAssignments: KtList<ApiB2bScimV1SCIMGroupImplicitRoleAssignments>, bearerTokenExpiresAt?: Nullable<any>/* Nullable<Instant> */);
    get organizationId(): string;
    get connectionId(): string;
    get status(): string;
    get displayName(): string;
    get identityProvider(): string;
    get baseUrl(): string;
    get bearerToken(): string;
    get scimGroupImplicitRoleAssignments(): KtList<ApiB2bScimV1SCIMGroupImplicitRoleAssignments>;
    get bearerTokenExpiresAt(): Nullable<any>/* Nullable<Instant> */;
    copy(organizationId?: string, connectionId?: string, status?: string, displayName?: string, identityProvider?: string, baseUrl?: string, bearerToken?: string, scimGroupImplicitRoleAssignments?: KtList<ApiB2bScimV1SCIMGroupImplicitRoleAssignments>, bearerTokenExpiresAt?: Nullable<any>/* Nullable<Instant> */): ApiB2bScimV1SCIMConnectionWithToken;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiB2bScimV1SCIMConnectionWithToken {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiB2bScimV1SCIMConnectionWithToken;
    }
}
export declare class ApiB2bScimV1SCIMGroup {
    constructor(groupId: string, groupName: string, organizationId: string, connectionId: string);
    get groupId(): string;
    get groupName(): string;
    get organizationId(): string;
    get connectionId(): string;
    copy(groupId?: string, groupName?: string, organizationId?: string, connectionId?: string): ApiB2bScimV1SCIMGroup;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiB2bScimV1SCIMGroup {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiB2bScimV1SCIMGroup;
    }
}
export declare class ApiB2bScimV1SCIMGroupImplicitRoleAssignments {
    constructor(roleId: string, groupId: string, groupName: string);
    get roleId(): string;
    get groupId(): string;
    get groupName(): string;
    copy(roleId?: string, groupId?: string, groupName?: string): ApiB2bScimV1SCIMGroupImplicitRoleAssignments;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiB2bScimV1SCIMGroupImplicitRoleAssignments {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiB2bScimV1SCIMGroupImplicitRoleAssignments;
    }
}
export declare class ApiB2bScimV1X509Certificate {
    constructor(value: string, type: string, primary: boolean);
    get value(): string;
    get type(): string;
    get primary(): boolean;
    copy(value?: string, type?: string, primary?: boolean): ApiB2bScimV1X509Certificate;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiB2bScimV1X509Certificate {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiB2bScimV1X509Certificate;
    }
}
export declare class ApiB2bSessionV1AuthorizationVerdict {
    constructor(authorized: boolean, grantingRoles: KtList<string>);
    get authorized(): boolean;
    get grantingRoles(): KtList<string>;
    copy(authorized?: boolean, grantingRoles?: KtList<string>): ApiB2bSessionV1AuthorizationVerdict;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiB2bSessionV1AuthorizationVerdict {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiB2bSessionV1AuthorizationVerdict;
    }
}
export declare class ApiB2bSessionV1MemberSession {
    constructor(memberSessionId: string, memberId: string, startedAt: any/* Instant */, lastAccessedAt: any/* Instant */, expiresAt: any/* Instant */, authenticationFactors: KtList<ApiSessionV1AuthenticationFactor>, organizationId: string, roles: KtList<string>, organizationSlug: string, customClaims?: Nullable<KtMap<string, any/* JsonElement */>>);
    get memberSessionId(): string;
    get memberId(): string;
    get startedAt(): any/* Instant */;
    get lastAccessedAt(): any/* Instant */;
    get expiresAt(): any/* Instant */;
    get authenticationFactors(): KtList<ApiSessionV1AuthenticationFactor>;
    get organizationId(): string;
    get roles(): KtList<string>;
    get organizationSlug(): string;
    get customClaims(): Nullable<KtMap<string, any/* JsonElement */>>;
    copy(memberSessionId?: string, memberId?: string, startedAt?: any/* Instant */, lastAccessedAt?: any/* Instant */, expiresAt?: any/* Instant */, authenticationFactors?: KtList<ApiSessionV1AuthenticationFactor>, organizationId?: string, roles?: KtList<string>, organizationSlug?: string, customClaims?: Nullable<KtMap<string, any/* JsonElement */>>): ApiB2bSessionV1MemberSession;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiB2bSessionV1MemberSession {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiB2bSessionV1MemberSession;
    }
}
export declare class ApiB2bSessionV1PrimaryRequired {
    constructor(allowedAuthMethods: KtList<string>);
    get allowedAuthMethods(): KtList<string>;
    copy(allowedAuthMethods?: KtList<string>): ApiB2bSessionV1PrimaryRequired;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiB2bSessionV1PrimaryRequired {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiB2bSessionV1PrimaryRequired;
    }
}
export declare class ApiConnectedappsV1ConnectedAppPublic {
    constructor(clientId: string, clientName: string, clientDescription: string, clientType: string, logoUrl?: Nullable<string>);
    get clientId(): string;
    get clientName(): string;
    get clientDescription(): string;
    get clientType(): string;
    get logoUrl(): Nullable<string>;
    copy(clientId?: string, clientName?: string, clientDescription?: string, clientType?: string, logoUrl?: Nullable<string>): ApiConnectedappsV1ConnectedAppPublic;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiConnectedappsV1ConnectedAppPublic {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiConnectedappsV1ConnectedAppPublic;
    }
}
export declare class ApiCryptoWalletV1SIWEParamsResponse implements StytchAPIResponse {
    constructor(domain: string, uri: string, chainId: string, resources: KtList<string>, statusCode: number, issuedAt?: Nullable<any>/* Nullable<Instant> */, messageRequestId?: Nullable<string>);
    get domain(): string;
    get uri(): string;
    get chainId(): string;
    get resources(): KtList<string>;
    get statusCode(): number;
    get issuedAt(): Nullable<any>/* Nullable<Instant> */;
    get messageRequestId(): Nullable<string>;
    copy(domain?: string, uri?: string, chainId?: string, resources?: KtList<string>, statusCode?: number, issuedAt?: Nullable<any>/* Nullable<Instant> */, messageRequestId?: Nullable<string>): ApiCryptoWalletV1SIWEParamsResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace ApiCryptoWalletV1SIWEParamsResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiCryptoWalletV1SIWEParamsResponse;
    }
}
export declare class ApiDeviceHistoryV1DeviceAttributeDetails {
    constructor(isNew: boolean, firstSeenAt?: Nullable<any>/* Nullable<Instant> */, lastSeenAt?: Nullable<any>/* Nullable<Instant> */);
    get isNew(): boolean;
    get firstSeenAt(): Nullable<any>/* Nullable<Instant> */;
    get lastSeenAt(): Nullable<any>/* Nullable<Instant> */;
    copy(isNew?: boolean, firstSeenAt?: Nullable<any>/* Nullable<Instant> */, lastSeenAt?: Nullable<any>/* Nullable<Instant> */): ApiDeviceHistoryV1DeviceAttributeDetails;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiDeviceHistoryV1DeviceAttributeDetails {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiDeviceHistoryV1DeviceAttributeDetails;
    }
}
export declare class ApiDiscoveryV1DiscoveredOrganization {
    constructor(memberAuthenticated: boolean, organization?: Nullable<ApiOrganizationV1Organization>, membership?: Nullable<ApiDiscoveryV1Membership>, primaryRequired?: Nullable<ApiB2bSessionV1PrimaryRequired>, mfaRequired?: Nullable<ApiB2bMfaV1MfaRequired>);
    get memberAuthenticated(): boolean;
    get organization(): Nullable<ApiOrganizationV1Organization>;
    get membership(): Nullable<ApiDiscoveryV1Membership>;
    get primaryRequired(): Nullable<ApiB2bSessionV1PrimaryRequired>;
    get mfaRequired(): Nullable<ApiB2bMfaV1MfaRequired>;
    copy(memberAuthenticated?: boolean, organization?: Nullable<ApiOrganizationV1Organization>, membership?: Nullable<ApiDiscoveryV1Membership>, primaryRequired?: Nullable<ApiB2bSessionV1PrimaryRequired>, mfaRequired?: Nullable<ApiB2bMfaV1MfaRequired>): ApiDiscoveryV1DiscoveredOrganization;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiDiscoveryV1DiscoveredOrganization {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiDiscoveryV1DiscoveredOrganization;
    }
}
export declare class ApiDiscoveryV1Membership {
    constructor(type: string, details?: Nullable<KtMap<string, any/* JsonElement */>>, member?: Nullable<ApiOrganizationV1Member>);
    get type(): string;
    get details(): Nullable<KtMap<string, any/* JsonElement */>>;
    get member(): Nullable<ApiOrganizationV1Member>;
    copy(type?: string, details?: Nullable<KtMap<string, any/* JsonElement */>>, member?: Nullable<ApiOrganizationV1Member>): ApiDiscoveryV1Membership;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiDiscoveryV1Membership {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiDiscoveryV1Membership;
    }
}
export declare class ApiIdpV1ScopeResult {
    constructor(scope: string, description: string, isGrantable: boolean);
    get scope(): string;
    get description(): string;
    get isGrantable(): boolean;
    copy(scope?: string, description?: string, isGrantable?: boolean): ApiIdpV1ScopeResult;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiIdpV1ScopeResult {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiIdpV1ScopeResult;
    }
}
export declare class ApiOauthV1ProviderValues {
    constructor(accessToken: string, refreshToken: string, idToken: string, scopes: KtList<string>, expiresAt?: Nullable<any>/* Nullable<Instant> */);
    get accessToken(): string;
    get refreshToken(): string;
    get idToken(): string;
    get scopes(): KtList<string>;
    get expiresAt(): Nullable<any>/* Nullable<Instant> */;
    copy(accessToken?: string, refreshToken?: string, idToken?: string, scopes?: KtList<string>, expiresAt?: Nullable<any>/* Nullable<Instant> */): ApiOauthV1ProviderValues;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiOauthV1ProviderValues {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiOauthV1ProviderValues;
    }
}
export declare class ApiOrganizationV1ActiveSCIMConnection {
    constructor(connectionId: string, displayName: string, bearerTokenLastFour: string, bearerTokenExpiresAt?: Nullable<any>/* Nullable<Instant> */);
    get connectionId(): string;
    get displayName(): string;
    get bearerTokenLastFour(): string;
    get bearerTokenExpiresAt(): Nullable<any>/* Nullable<Instant> */;
    copy(connectionId?: string, displayName?: string, bearerTokenLastFour?: string, bearerTokenExpiresAt?: Nullable<any>/* Nullable<Instant> */): ApiOrganizationV1ActiveSCIMConnection;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiOrganizationV1ActiveSCIMConnection {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiOrganizationV1ActiveSCIMConnection;
    }
}
export declare class ApiOrganizationV1ActiveSSOConnection {
    constructor(connectionId: string, displayName: string, identityProvider: string);
    get connectionId(): string;
    get displayName(): string;
    get identityProvider(): string;
    copy(connectionId?: string, displayName?: string, identityProvider?: string): ApiOrganizationV1ActiveSSOConnection;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiOrganizationV1ActiveSSOConnection {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiOrganizationV1ActiveSSOConnection;
    }
}
export declare class ApiOrganizationV1CustomRole {
    constructor(roleId: string, description: string, permissions: KtList<ApiOrganizationV1CustomRolePermission>);
    get roleId(): string;
    get description(): string;
    get permissions(): KtList<ApiOrganizationV1CustomRolePermission>;
    copy(roleId?: string, description?: string, permissions?: KtList<ApiOrganizationV1CustomRolePermission>): ApiOrganizationV1CustomRole;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiOrganizationV1CustomRole {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiOrganizationV1CustomRole;
    }
}
export declare class ApiOrganizationV1CustomRolePermission {
    constructor(resourceId: string, actions: KtList<string>);
    get resourceId(): string;
    get actions(): KtList<string>;
    copy(resourceId?: string, actions?: KtList<string>): ApiOrganizationV1CustomRolePermission;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiOrganizationV1CustomRolePermission {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiOrganizationV1CustomRolePermission;
    }
}
export declare class ApiOrganizationV1EmailImplicitRoleAssignment {
    constructor(domain: string, roleId: string);
    get domain(): string;
    get roleId(): string;
    copy(domain?: string, roleId?: string): ApiOrganizationV1EmailImplicitRoleAssignment;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiOrganizationV1EmailImplicitRoleAssignment {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiOrganizationV1EmailImplicitRoleAssignment;
    }
}
export declare class ApiOrganizationV1ExternalSearchQuery {
    constructor(operator: any/* ApiOrganizationV1ExternalSearchQueryOperator */, operands: KtList<KtMap<string, any/* JsonElement */>>);
    get operator(): any/* ApiOrganizationV1ExternalSearchQueryOperator */;
    get operands(): KtList<KtMap<string, any/* JsonElement */>>;
    copy(operator?: any/* ApiOrganizationV1ExternalSearchQueryOperator */, operands?: KtList<KtMap<string, any/* JsonElement */>>): ApiOrganizationV1ExternalSearchQuery;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiOrganizationV1ExternalSearchQuery {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiOrganizationV1ExternalSearchQuery;
    }
}
export declare class ApiOrganizationV1Member {
    constructor(organizationId: string, memberId: string, emailAddress: string, status: string, name: string, ssoRegistrations: KtList<ApiOrganizationV1SSORegistration>, isBreakglass: boolean, memberPasswordId: string, oauthRegistrations: KtList<ApiOrganizationV1OAuthRegistration>, emailAddressVerified: boolean, mfaPhoneNumberVerified: boolean, isAdmin: boolean, totpRegistrationId: string, retiredEmailAddresses: KtList<ApiOrganizationV1RetiredEmail>, isLocked: boolean, mfaEnrolled: boolean, mfaPhoneNumber: string, defaultMfaMethod: string, roles: KtList<ApiOrganizationV1MemberRole>, trustedMetadata?: Nullable<KtMap<string, any/* JsonElement */>>, untrustedMetadata?: Nullable<KtMap<string, any/* JsonElement */>>, createdAt?: Nullable<any>/* Nullable<Instant> */, updatedAt?: Nullable<any>/* Nullable<Instant> */, scimRegistration?: Nullable<ApiOrganizationV1SCIMRegistration>, externalId?: Nullable<string>, lockCreatedAt?: Nullable<any>/* Nullable<Instant> */, lockExpiresAt?: Nullable<any>/* Nullable<Instant> */);
    get organizationId(): string;
    get memberId(): string;
    get emailAddress(): string;
    get status(): string;
    get name(): string;
    get ssoRegistrations(): KtList<ApiOrganizationV1SSORegistration>;
    get isBreakglass(): boolean;
    get memberPasswordId(): string;
    get oauthRegistrations(): KtList<ApiOrganizationV1OAuthRegistration>;
    get emailAddressVerified(): boolean;
    get mfaPhoneNumberVerified(): boolean;
    get isAdmin(): boolean;
    get totpRegistrationId(): string;
    get retiredEmailAddresses(): KtList<ApiOrganizationV1RetiredEmail>;
    get isLocked(): boolean;
    get mfaEnrolled(): boolean;
    get mfaPhoneNumber(): string;
    get defaultMfaMethod(): string;
    get roles(): KtList<ApiOrganizationV1MemberRole>;
    get trustedMetadata(): Nullable<KtMap<string, any/* JsonElement */>>;
    get untrustedMetadata(): Nullable<KtMap<string, any/* JsonElement */>>;
    get createdAt(): Nullable<any>/* Nullable<Instant> */;
    get updatedAt(): Nullable<any>/* Nullable<Instant> */;
    get scimRegistration(): Nullable<ApiOrganizationV1SCIMRegistration>;
    get externalId(): Nullable<string>;
    get lockCreatedAt(): Nullable<any>/* Nullable<Instant> */;
    get lockExpiresAt(): Nullable<any>/* Nullable<Instant> */;
    copy(organizationId?: string, memberId?: string, emailAddress?: string, status?: string, name?: string, ssoRegistrations?: KtList<ApiOrganizationV1SSORegistration>, isBreakglass?: boolean, memberPasswordId?: string, oauthRegistrations?: KtList<ApiOrganizationV1OAuthRegistration>, emailAddressVerified?: boolean, mfaPhoneNumberVerified?: boolean, isAdmin?: boolean, totpRegistrationId?: string, retiredEmailAddresses?: KtList<ApiOrganizationV1RetiredEmail>, isLocked?: boolean, mfaEnrolled?: boolean, mfaPhoneNumber?: string, defaultMfaMethod?: string, roles?: KtList<ApiOrganizationV1MemberRole>, trustedMetadata?: Nullable<KtMap<string, any/* JsonElement */>>, untrustedMetadata?: Nullable<KtMap<string, any/* JsonElement */>>, createdAt?: Nullable<any>/* Nullable<Instant> */, updatedAt?: Nullable<any>/* Nullable<Instant> */, scimRegistration?: Nullable<ApiOrganizationV1SCIMRegistration>, externalId?: Nullable<string>, lockCreatedAt?: Nullable<any>/* Nullable<Instant> */, lockExpiresAt?: Nullable<any>/* Nullable<Instant> */): ApiOrganizationV1Member;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiOrganizationV1Member {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiOrganizationV1Member;
    }
}
export declare class ApiOrganizationV1MemberConnectedApp {
    constructor(connectedAppId: string, name: string, description: string, clientType: string, scopesGranted: string, logoUrl?: Nullable<string>);
    get connectedAppId(): string;
    get name(): string;
    get description(): string;
    get clientType(): string;
    get scopesGranted(): string;
    get logoUrl(): Nullable<string>;
    copy(connectedAppId?: string, name?: string, description?: string, clientType?: string, scopesGranted?: string, logoUrl?: Nullable<string>): ApiOrganizationV1MemberConnectedApp;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiOrganizationV1MemberConnectedApp {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiOrganizationV1MemberConnectedApp;
    }
}
export declare class ApiOrganizationV1MemberRole {
    constructor(roleId: string, sources: KtList<ApiOrganizationV1MemberRoleSource>);
    get roleId(): string;
    get sources(): KtList<ApiOrganizationV1MemberRoleSource>;
    copy(roleId?: string, sources?: KtList<ApiOrganizationV1MemberRoleSource>): ApiOrganizationV1MemberRole;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiOrganizationV1MemberRole {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiOrganizationV1MemberRole;
    }
}
export declare class ApiOrganizationV1MemberRoleSource {
    constructor(type: string, details?: Nullable<KtMap<string, any/* JsonElement */>>);
    get type(): string;
    get details(): Nullable<KtMap<string, any/* JsonElement */>>;
    copy(type?: string, details?: Nullable<KtMap<string, any/* JsonElement */>>): ApiOrganizationV1MemberRoleSource;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiOrganizationV1MemberRoleSource {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiOrganizationV1MemberRoleSource;
    }
}
export declare class ApiOrganizationV1OAuthRegistration {
    constructor(providerType: string, providerSubject: string, memberOauthRegistrationId: string, profilePictureUrl?: Nullable<string>, locale?: Nullable<string>);
    get providerType(): string;
    get providerSubject(): string;
    get memberOauthRegistrationId(): string;
    get profilePictureUrl(): Nullable<string>;
    get locale(): Nullable<string>;
    copy(providerType?: string, providerSubject?: string, memberOauthRegistrationId?: string, profilePictureUrl?: Nullable<string>, locale?: Nullable<string>): ApiOrganizationV1OAuthRegistration;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiOrganizationV1OAuthRegistration {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiOrganizationV1OAuthRegistration;
    }
}
export declare class ApiOrganizationV1Organization {
    constructor(organizationId: string, organizationName: string, organizationLogoUrl: string, organizationSlug: string, ssoJitProvisioning: string, ssoJitProvisioningAllowedConnections: KtList<string>, ssoActiveConnections: KtList<ApiOrganizationV1ActiveSSOConnection>, emailAllowedDomains: KtList<string>, emailJitProvisioning: string, emailInvites: string, authMethods: string, allowedAuthMethods: KtList<string>, mfaPolicy: string, rbacEmailImplicitRoleAssignments: KtList<ApiOrganizationV1EmailImplicitRoleAssignment>, mfaMethods: string, allowedMfaMethods: KtList<string>, oauthTenantJitProvisioning: string, claimedEmailDomains: KtList<string>, firstPartyConnectedAppsAllowedType: string, allowedFirstPartyConnectedApps: KtList<string>, thirdPartyConnectedAppsAllowedType: string, allowedThirdPartyConnectedApps: KtList<string>, customRoles: KtList<ApiOrganizationV1CustomRole>, trustedMetadata?: Nullable<KtMap<string, any/* JsonElement */>>, createdAt?: Nullable<any>/* Nullable<Instant> */, updatedAt?: Nullable<any>/* Nullable<Instant> */, organizationExternalId?: Nullable<string>, ssoDefaultConnectionId?: Nullable<string>, scimActiveConnection?: Nullable<ApiOrganizationV1ActiveSCIMConnection>, allowedOauthTenants?: Nullable<KtMap<string, any/* JsonElement */>>);
    get organizationId(): string;
    get organizationName(): string;
    get organizationLogoUrl(): string;
    get organizationSlug(): string;
    get ssoJitProvisioning(): string;
    get ssoJitProvisioningAllowedConnections(): KtList<string>;
    get ssoActiveConnections(): KtList<ApiOrganizationV1ActiveSSOConnection>;
    get emailAllowedDomains(): KtList<string>;
    get emailJitProvisioning(): string;
    get emailInvites(): string;
    get authMethods(): string;
    get allowedAuthMethods(): KtList<string>;
    get mfaPolicy(): string;
    get rbacEmailImplicitRoleAssignments(): KtList<ApiOrganizationV1EmailImplicitRoleAssignment>;
    get mfaMethods(): string;
    get allowedMfaMethods(): KtList<string>;
    get oauthTenantJitProvisioning(): string;
    get claimedEmailDomains(): KtList<string>;
    get firstPartyConnectedAppsAllowedType(): string;
    get allowedFirstPartyConnectedApps(): KtList<string>;
    get thirdPartyConnectedAppsAllowedType(): string;
    get allowedThirdPartyConnectedApps(): KtList<string>;
    get customRoles(): KtList<ApiOrganizationV1CustomRole>;
    get trustedMetadata(): Nullable<KtMap<string, any/* JsonElement */>>;
    get createdAt(): Nullable<any>/* Nullable<Instant> */;
    get updatedAt(): Nullable<any>/* Nullable<Instant> */;
    get organizationExternalId(): Nullable<string>;
    get ssoDefaultConnectionId(): Nullable<string>;
    get scimActiveConnection(): Nullable<ApiOrganizationV1ActiveSCIMConnection>;
    get allowedOauthTenants(): Nullable<KtMap<string, any/* JsonElement */>>;
    copy(organizationId?: string, organizationName?: string, organizationLogoUrl?: string, organizationSlug?: string, ssoJitProvisioning?: string, ssoJitProvisioningAllowedConnections?: KtList<string>, ssoActiveConnections?: KtList<ApiOrganizationV1ActiveSSOConnection>, emailAllowedDomains?: KtList<string>, emailJitProvisioning?: string, emailInvites?: string, authMethods?: string, allowedAuthMethods?: KtList<string>, mfaPolicy?: string, rbacEmailImplicitRoleAssignments?: KtList<ApiOrganizationV1EmailImplicitRoleAssignment>, mfaMethods?: string, allowedMfaMethods?: KtList<string>, oauthTenantJitProvisioning?: string, claimedEmailDomains?: KtList<string>, firstPartyConnectedAppsAllowedType?: string, allowedFirstPartyConnectedApps?: KtList<string>, thirdPartyConnectedAppsAllowedType?: string, allowedThirdPartyConnectedApps?: KtList<string>, customRoles?: KtList<ApiOrganizationV1CustomRole>, trustedMetadata?: Nullable<KtMap<string, any/* JsonElement */>>, createdAt?: Nullable<any>/* Nullable<Instant> */, updatedAt?: Nullable<any>/* Nullable<Instant> */, organizationExternalId?: Nullable<string>, ssoDefaultConnectionId?: Nullable<string>, scimActiveConnection?: Nullable<ApiOrganizationV1ActiveSCIMConnection>, allowedOauthTenants?: Nullable<KtMap<string, any/* JsonElement */>>): ApiOrganizationV1Organization;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiOrganizationV1Organization {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiOrganizationV1Organization;
    }
}
export declare class ApiOrganizationV1OrganizationConnectedApp {
    constructor(connectedAppId: string, name: string, description: string, clientType: string, logoUrl?: Nullable<string>);
    get connectedAppId(): string;
    get name(): string;
    get description(): string;
    get clientType(): string;
    get logoUrl(): Nullable<string>;
    copy(connectedAppId?: string, name?: string, description?: string, clientType?: string, logoUrl?: Nullable<string>): ApiOrganizationV1OrganizationConnectedApp;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiOrganizationV1OrganizationConnectedApp {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiOrganizationV1OrganizationConnectedApp;
    }
}
export declare class ApiOrganizationV1OrganizationConnectedAppActiveMember {
    constructor(memberId: string, grantedScopes: KtList<string>);
    get memberId(): string;
    get grantedScopes(): KtList<string>;
    copy(memberId?: string, grantedScopes?: KtList<string>): ApiOrganizationV1OrganizationConnectedAppActiveMember;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiOrganizationV1OrganizationConnectedAppActiveMember {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiOrganizationV1OrganizationConnectedAppActiveMember;
    }
}
export declare class ApiOrganizationV1ResultsMetadata {
    constructor(total: number, nextCursor?: Nullable<string>);
    get total(): number;
    get nextCursor(): Nullable<string>;
    copy(total?: number, nextCursor?: Nullable<string>): ApiOrganizationV1ResultsMetadata;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiOrganizationV1ResultsMetadata {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiOrganizationV1ResultsMetadata;
    }
}
export declare class ApiOrganizationV1RetiredEmail {
    constructor(emailId: string, emailAddress: string);
    get emailId(): string;
    get emailAddress(): string;
    copy(emailId?: string, emailAddress?: string): ApiOrganizationV1RetiredEmail;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiOrganizationV1RetiredEmail {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiOrganizationV1RetiredEmail;
    }
}
export declare class ApiOrganizationV1SCIMRegistration {
    constructor(connectionId: string, registrationId: string, externalId?: Nullable<string>, scimAttributes?: Nullable<ApiB2bScimV1SCIMAttributes>);
    get connectionId(): string;
    get registrationId(): string;
    get externalId(): Nullable<string>;
    get scimAttributes(): Nullable<ApiB2bScimV1SCIMAttributes>;
    copy(connectionId?: string, registrationId?: string, externalId?: Nullable<string>, scimAttributes?: Nullable<ApiB2bScimV1SCIMAttributes>): ApiOrganizationV1SCIMRegistration;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiOrganizationV1SCIMRegistration {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiOrganizationV1SCIMRegistration;
    }
}
export declare class ApiOrganizationV1SSORegistration {
    constructor(connectionId: string, externalId: string, registrationId: string, ssoAttributes?: Nullable<KtMap<string, any/* JsonElement */>>);
    get connectionId(): string;
    get externalId(): string;
    get registrationId(): string;
    get ssoAttributes(): Nullable<KtMap<string, any/* JsonElement */>>;
    copy(connectionId?: string, externalId?: string, registrationId?: string, ssoAttributes?: Nullable<KtMap<string, any/* JsonElement */>>): ApiOrganizationV1SSORegistration;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiOrganizationV1SSORegistration {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiOrganizationV1SSORegistration;
    }
}
export declare class ApiPasswordV1Feedback {
    constructor(warning: string, suggestions: KtList<string>, ludsRequirements?: Nullable<ApiPasswordV1LUDSRequirements>);
    get warning(): string;
    get suggestions(): KtList<string>;
    get ludsRequirements(): Nullable<ApiPasswordV1LUDSRequirements>;
    copy(warning?: string, suggestions?: KtList<string>, ludsRequirements?: Nullable<ApiPasswordV1LUDSRequirements>): ApiPasswordV1Feedback;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiPasswordV1Feedback {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiPasswordV1Feedback;
    }
}
export declare class ApiPasswordV1LUDSRequirements {
    constructor(hasLowerCase: boolean, hasUpperCase: boolean, hasDigit: boolean, hasSymbol: boolean, missingComplexity: number, missingCharacters: number);
    get hasLowerCase(): boolean;
    get hasUpperCase(): boolean;
    get hasDigit(): boolean;
    get hasSymbol(): boolean;
    get missingComplexity(): number;
    get missingCharacters(): number;
    copy(hasLowerCase?: boolean, hasUpperCase?: boolean, hasDigit?: boolean, hasSymbol?: boolean, missingComplexity?: number, missingCharacters?: number): ApiPasswordV1LUDSRequirements;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiPasswordV1LUDSRequirements {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiPasswordV1LUDSRequirements;
    }
}
export declare class ApiSessionV1AmazonOAuthFactor {
    constructor(id: string, providerSubject: string, emailId?: Nullable<string>);
    get id(): string;
    get providerSubject(): string;
    get emailId(): Nullable<string>;
    copy(id?: string, providerSubject?: string, emailId?: Nullable<string>): ApiSessionV1AmazonOAuthFactor;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiSessionV1AmazonOAuthFactor {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiSessionV1AmazonOAuthFactor;
    }
}
export declare class ApiSessionV1AppleOAuthFactor {
    constructor(id: string, providerSubject: string, emailId?: Nullable<string>);
    get id(): string;
    get providerSubject(): string;
    get emailId(): Nullable<string>;
    copy(id?: string, providerSubject?: string, emailId?: Nullable<string>): ApiSessionV1AppleOAuthFactor;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiSessionV1AppleOAuthFactor {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiSessionV1AppleOAuthFactor;
    }
}
export declare class ApiSessionV1AuthenticationFactor {
    constructor(type: any/* ApiSessionV1AuthenticationFactorType */, deliveryMethod: any/* ApiSessionV1AuthenticationFactorDeliveryMethod */, lastAuthenticatedAt?: Nullable<any>/* Nullable<Instant> */, createdAt?: Nullable<any>/* Nullable<Instant> */, updatedAt?: Nullable<any>/* Nullable<Instant> */, emailFactor?: Nullable<ApiSessionV1EmailFactor>, phoneNumberFactor?: Nullable<ApiSessionV1PhoneNumberFactor>, googleOauthFactor?: Nullable<ApiSessionV1GoogleOAuthFactor>, microsoftOauthFactor?: Nullable<ApiSessionV1MicrosoftOAuthFactor>, appleOauthFactor?: Nullable<ApiSessionV1AppleOAuthFactor>, webauthnFactor?: Nullable<ApiSessionV1WebAuthnFactor>, authenticatorAppFactor?: Nullable<ApiSessionV1AuthenticatorAppFactor>, githubOauthFactor?: Nullable<ApiSessionV1GithubOAuthFactor>, recoveryCodeFactor?: Nullable<ApiSessionV1RecoveryCodeFactor>, facebookOauthFactor?: Nullable<ApiSessionV1FacebookOAuthFactor>, cryptoWalletFactor?: Nullable<ApiSessionV1CryptoWalletFactor>, amazonOauthFactor?: Nullable<ApiSessionV1AmazonOAuthFactor>, bitbucketOauthFactor?: Nullable<ApiSessionV1BitbucketOAuthFactor>, coinbaseOauthFactor?: Nullable<ApiSessionV1CoinbaseOAuthFactor>, discordOauthFactor?: Nullable<ApiSessionV1DiscordOAuthFactor>, figmaOauthFactor?: Nullable<ApiSessionV1FigmaOAuthFactor>, gitLabOauthFactor?: Nullable<ApiSessionV1GitLabOAuthFactor>, instagramOauthFactor?: Nullable<ApiSessionV1InstagramOAuthFactor>, linkedInOauthFactor?: Nullable<ApiSessionV1LinkedInOAuthFactor>, shopifyOauthFactor?: Nullable<ApiSessionV1ShopifyOAuthFactor>, slackOauthFactor?: Nullable<ApiSessionV1SlackOAuthFactor>, snapchatOauthFactor?: Nullable<ApiSessionV1SnapchatOAuthFactor>, spotifyOauthFactor?: Nullable<ApiSessionV1SpotifyOAuthFactor>, steamOauthFactor?: Nullable<ApiSessionV1SteamOAuthFactor>, tikTokOauthFactor?: Nullable<ApiSessionV1TikTokOAuthFactor>, twitchOauthFactor?: Nullable<ApiSessionV1TwitchOAuthFactor>, twitterOauthFactor?: Nullable<ApiSessionV1TwitterOAuthFactor>, embeddableMagicLinkFactor?: Nullable<ApiSessionV1EmbeddableMagicLinkFactor>, biometricFactor?: Nullable<ApiSessionV1BiometricFactor>, samlSsoFactor?: Nullable<ApiSessionV1SAMLSSOFactor>, oidcSsoFactor?: Nullable<ApiSessionV1OIDCSSOFactor>, salesforceOauthFactor?: Nullable<ApiSessionV1SalesforceOAuthFactor>, yahooOauthFactor?: Nullable<ApiSessionV1YahooOAuthFactor>, hubspotOauthFactor?: Nullable<ApiSessionV1HubspotOAuthFactor>, slackOauthExchangeFactor?: Nullable<ApiSessionV1SlackOAuthExchangeFactor>, hubspotOauthExchangeFactor?: Nullable<ApiSessionV1HubspotOAuthExchangeFactor>, githubOauthExchangeFactor?: Nullable<ApiSessionV1GithubOAuthExchangeFactor>, googleOauthExchangeFactor?: Nullable<ApiSessionV1GoogleOAuthExchangeFactor>, impersonatedFactor?: Nullable<ApiSessionV1ImpersonatedFactor>, oauthAccessTokenExchangeFactor?: Nullable<ApiSessionV1OAuthAccessTokenExchangeFactor>, trustedAuthTokenFactor?: Nullable<ApiSessionV1TrustedAuthTokenFactor>);
    get type(): any/* ApiSessionV1AuthenticationFactorType */;
    get deliveryMethod(): any/* ApiSessionV1AuthenticationFactorDeliveryMethod */;
    get lastAuthenticatedAt(): Nullable<any>/* Nullable<Instant> */;
    get createdAt(): Nullable<any>/* Nullable<Instant> */;
    get updatedAt(): Nullable<any>/* Nullable<Instant> */;
    get emailFactor(): Nullable<ApiSessionV1EmailFactor>;
    get phoneNumberFactor(): Nullable<ApiSessionV1PhoneNumberFactor>;
    get googleOauthFactor(): Nullable<ApiSessionV1GoogleOAuthFactor>;
    get microsoftOauthFactor(): Nullable<ApiSessionV1MicrosoftOAuthFactor>;
    get appleOauthFactor(): Nullable<ApiSessionV1AppleOAuthFactor>;
    get webauthnFactor(): Nullable<ApiSessionV1WebAuthnFactor>;
    get authenticatorAppFactor(): Nullable<ApiSessionV1AuthenticatorAppFactor>;
    get githubOauthFactor(): Nullable<ApiSessionV1GithubOAuthFactor>;
    get recoveryCodeFactor(): Nullable<ApiSessionV1RecoveryCodeFactor>;
    get facebookOauthFactor(): Nullable<ApiSessionV1FacebookOAuthFactor>;
    get cryptoWalletFactor(): Nullable<ApiSessionV1CryptoWalletFactor>;
    get amazonOauthFactor(): Nullable<ApiSessionV1AmazonOAuthFactor>;
    get bitbucketOauthFactor(): Nullable<ApiSessionV1BitbucketOAuthFactor>;
    get coinbaseOauthFactor(): Nullable<ApiSessionV1CoinbaseOAuthFactor>;
    get discordOauthFactor(): Nullable<ApiSessionV1DiscordOAuthFactor>;
    get figmaOauthFactor(): Nullable<ApiSessionV1FigmaOAuthFactor>;
    get gitLabOauthFactor(): Nullable<ApiSessionV1GitLabOAuthFactor>;
    get instagramOauthFactor(): Nullable<ApiSessionV1InstagramOAuthFactor>;
    get linkedInOauthFactor(): Nullable<ApiSessionV1LinkedInOAuthFactor>;
    get shopifyOauthFactor(): Nullable<ApiSessionV1ShopifyOAuthFactor>;
    get slackOauthFactor(): Nullable<ApiSessionV1SlackOAuthFactor>;
    get snapchatOauthFactor(): Nullable<ApiSessionV1SnapchatOAuthFactor>;
    get spotifyOauthFactor(): Nullable<ApiSessionV1SpotifyOAuthFactor>;
    get steamOauthFactor(): Nullable<ApiSessionV1SteamOAuthFactor>;
    get tikTokOauthFactor(): Nullable<ApiSessionV1TikTokOAuthFactor>;
    get twitchOauthFactor(): Nullable<ApiSessionV1TwitchOAuthFactor>;
    get twitterOauthFactor(): Nullable<ApiSessionV1TwitterOAuthFactor>;
    get embeddableMagicLinkFactor(): Nullable<ApiSessionV1EmbeddableMagicLinkFactor>;
    get biometricFactor(): Nullable<ApiSessionV1BiometricFactor>;
    get samlSsoFactor(): Nullable<ApiSessionV1SAMLSSOFactor>;
    get oidcSsoFactor(): Nullable<ApiSessionV1OIDCSSOFactor>;
    get salesforceOauthFactor(): Nullable<ApiSessionV1SalesforceOAuthFactor>;
    get yahooOauthFactor(): Nullable<ApiSessionV1YahooOAuthFactor>;
    get hubspotOauthFactor(): Nullable<ApiSessionV1HubspotOAuthFactor>;
    get slackOauthExchangeFactor(): Nullable<ApiSessionV1SlackOAuthExchangeFactor>;
    get hubspotOauthExchangeFactor(): Nullable<ApiSessionV1HubspotOAuthExchangeFactor>;
    get githubOauthExchangeFactor(): Nullable<ApiSessionV1GithubOAuthExchangeFactor>;
    get googleOauthExchangeFactor(): Nullable<ApiSessionV1GoogleOAuthExchangeFactor>;
    get impersonatedFactor(): Nullable<ApiSessionV1ImpersonatedFactor>;
    get oauthAccessTokenExchangeFactor(): Nullable<ApiSessionV1OAuthAccessTokenExchangeFactor>;
    get trustedAuthTokenFactor(): Nullable<ApiSessionV1TrustedAuthTokenFactor>;
    copy(type?: any/* ApiSessionV1AuthenticationFactorType */, deliveryMethod?: any/* ApiSessionV1AuthenticationFactorDeliveryMethod */, lastAuthenticatedAt?: Nullable<any>/* Nullable<Instant> */, createdAt?: Nullable<any>/* Nullable<Instant> */, updatedAt?: Nullable<any>/* Nullable<Instant> */, emailFactor?: Nullable<ApiSessionV1EmailFactor>, phoneNumberFactor?: Nullable<ApiSessionV1PhoneNumberFactor>, googleOauthFactor?: Nullable<ApiSessionV1GoogleOAuthFactor>, microsoftOauthFactor?: Nullable<ApiSessionV1MicrosoftOAuthFactor>, appleOauthFactor?: Nullable<ApiSessionV1AppleOAuthFactor>, webauthnFactor?: Nullable<ApiSessionV1WebAuthnFactor>, authenticatorAppFactor?: Nullable<ApiSessionV1AuthenticatorAppFactor>, githubOauthFactor?: Nullable<ApiSessionV1GithubOAuthFactor>, recoveryCodeFactor?: Nullable<ApiSessionV1RecoveryCodeFactor>, facebookOauthFactor?: Nullable<ApiSessionV1FacebookOAuthFactor>, cryptoWalletFactor?: Nullable<ApiSessionV1CryptoWalletFactor>, amazonOauthFactor?: Nullable<ApiSessionV1AmazonOAuthFactor>, bitbucketOauthFactor?: Nullable<ApiSessionV1BitbucketOAuthFactor>, coinbaseOauthFactor?: Nullable<ApiSessionV1CoinbaseOAuthFactor>, discordOauthFactor?: Nullable<ApiSessionV1DiscordOAuthFactor>, figmaOauthFactor?: Nullable<ApiSessionV1FigmaOAuthFactor>, gitLabOauthFactor?: Nullable<ApiSessionV1GitLabOAuthFactor>, instagramOauthFactor?: Nullable<ApiSessionV1InstagramOAuthFactor>, linkedInOauthFactor?: Nullable<ApiSessionV1LinkedInOAuthFactor>, shopifyOauthFactor?: Nullable<ApiSessionV1ShopifyOAuthFactor>, slackOauthFactor?: Nullable<ApiSessionV1SlackOAuthFactor>, snapchatOauthFactor?: Nullable<ApiSessionV1SnapchatOAuthFactor>, spotifyOauthFactor?: Nullable<ApiSessionV1SpotifyOAuthFactor>, steamOauthFactor?: Nullable<ApiSessionV1SteamOAuthFactor>, tikTokOauthFactor?: Nullable<ApiSessionV1TikTokOAuthFactor>, twitchOauthFactor?: Nullable<ApiSessionV1TwitchOAuthFactor>, twitterOauthFactor?: Nullable<ApiSessionV1TwitterOAuthFactor>, embeddableMagicLinkFactor?: Nullable<ApiSessionV1EmbeddableMagicLinkFactor>, biometricFactor?: Nullable<ApiSessionV1BiometricFactor>, samlSsoFactor?: Nullable<ApiSessionV1SAMLSSOFactor>, oidcSsoFactor?: Nullable<ApiSessionV1OIDCSSOFactor>, salesforceOauthFactor?: Nullable<ApiSessionV1SalesforceOAuthFactor>, yahooOauthFactor?: Nullable<ApiSessionV1YahooOAuthFactor>, hubspotOauthFactor?: Nullable<ApiSessionV1HubspotOAuthFactor>, slackOauthExchangeFactor?: Nullable<ApiSessionV1SlackOAuthExchangeFactor>, hubspotOauthExchangeFactor?: Nullable<ApiSessionV1HubspotOAuthExchangeFactor>, githubOauthExchangeFactor?: Nullable<ApiSessionV1GithubOAuthExchangeFactor>, googleOauthExchangeFactor?: Nullable<ApiSessionV1GoogleOAuthExchangeFactor>, impersonatedFactor?: Nullable<ApiSessionV1ImpersonatedFactor>, oauthAccessTokenExchangeFactor?: Nullable<ApiSessionV1OAuthAccessTokenExchangeFactor>, trustedAuthTokenFactor?: Nullable<ApiSessionV1TrustedAuthTokenFactor>): ApiSessionV1AuthenticationFactor;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiSessionV1AuthenticationFactor {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiSessionV1AuthenticationFactor;
    }
}
export declare class ApiSessionV1AuthenticatorAppFactor {
    constructor(totpId: string);
    get totpId(): string;
    copy(totpId?: string): ApiSessionV1AuthenticatorAppFactor;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiSessionV1AuthenticatorAppFactor {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiSessionV1AuthenticatorAppFactor;
    }
}
export declare class ApiSessionV1BiometricFactor {
    constructor(biometricRegistrationId: string);
    get biometricRegistrationId(): string;
    copy(biometricRegistrationId?: string): ApiSessionV1BiometricFactor;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiSessionV1BiometricFactor {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiSessionV1BiometricFactor;
    }
}
export declare class ApiSessionV1BitbucketOAuthFactor {
    constructor(id: string, providerSubject: string, emailId?: Nullable<string>);
    get id(): string;
    get providerSubject(): string;
    get emailId(): Nullable<string>;
    copy(id?: string, providerSubject?: string, emailId?: Nullable<string>): ApiSessionV1BitbucketOAuthFactor;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiSessionV1BitbucketOAuthFactor {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiSessionV1BitbucketOAuthFactor;
    }
}
export declare class ApiSessionV1CoinbaseOAuthFactor {
    constructor(id: string, providerSubject: string, emailId?: Nullable<string>);
    get id(): string;
    get providerSubject(): string;
    get emailId(): Nullable<string>;
    copy(id?: string, providerSubject?: string, emailId?: Nullable<string>): ApiSessionV1CoinbaseOAuthFactor;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiSessionV1CoinbaseOAuthFactor {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiSessionV1CoinbaseOAuthFactor;
    }
}
export declare class ApiSessionV1CryptoWalletFactor {
    constructor(cryptoWalletId: string, cryptoWalletAddress: string, cryptoWalletType: string);
    get cryptoWalletId(): string;
    get cryptoWalletAddress(): string;
    get cryptoWalletType(): string;
    copy(cryptoWalletId?: string, cryptoWalletAddress?: string, cryptoWalletType?: string): ApiSessionV1CryptoWalletFactor;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiSessionV1CryptoWalletFactor {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiSessionV1CryptoWalletFactor;
    }
}
export declare class ApiSessionV1DiscordOAuthFactor {
    constructor(id: string, providerSubject: string, emailId?: Nullable<string>);
    get id(): string;
    get providerSubject(): string;
    get emailId(): Nullable<string>;
    copy(id?: string, providerSubject?: string, emailId?: Nullable<string>): ApiSessionV1DiscordOAuthFactor;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiSessionV1DiscordOAuthFactor {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiSessionV1DiscordOAuthFactor;
    }
}
export declare class ApiSessionV1EmailFactor {
    constructor(emailId: string, emailAddress: string);
    get emailId(): string;
    get emailAddress(): string;
    copy(emailId?: string, emailAddress?: string): ApiSessionV1EmailFactor;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiSessionV1EmailFactor {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiSessionV1EmailFactor;
    }
}
export declare class ApiSessionV1EmbeddableMagicLinkFactor {
    constructor(embeddedId: string);
    get embeddedId(): string;
    copy(embeddedId?: string): ApiSessionV1EmbeddableMagicLinkFactor;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiSessionV1EmbeddableMagicLinkFactor {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiSessionV1EmbeddableMagicLinkFactor;
    }
}
export declare class ApiSessionV1FacebookOAuthFactor {
    constructor(id: string, providerSubject: string, emailId?: Nullable<string>);
    get id(): string;
    get providerSubject(): string;
    get emailId(): Nullable<string>;
    copy(id?: string, providerSubject?: string, emailId?: Nullable<string>): ApiSessionV1FacebookOAuthFactor;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiSessionV1FacebookOAuthFactor {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiSessionV1FacebookOAuthFactor;
    }
}
export declare class ApiSessionV1FigmaOAuthFactor {
    constructor(id: string, providerSubject: string, emailId?: Nullable<string>);
    get id(): string;
    get providerSubject(): string;
    get emailId(): Nullable<string>;
    copy(id?: string, providerSubject?: string, emailId?: Nullable<string>): ApiSessionV1FigmaOAuthFactor;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiSessionV1FigmaOAuthFactor {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiSessionV1FigmaOAuthFactor;
    }
}
export declare class ApiSessionV1GitLabOAuthFactor {
    constructor(id: string, providerSubject: string, emailId?: Nullable<string>);
    get id(): string;
    get providerSubject(): string;
    get emailId(): Nullable<string>;
    copy(id?: string, providerSubject?: string, emailId?: Nullable<string>): ApiSessionV1GitLabOAuthFactor;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiSessionV1GitLabOAuthFactor {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiSessionV1GitLabOAuthFactor;
    }
}
export declare class ApiSessionV1GithubOAuthExchangeFactor {
    constructor(emailId: string);
    get emailId(): string;
    copy(emailId?: string): ApiSessionV1GithubOAuthExchangeFactor;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiSessionV1GithubOAuthExchangeFactor {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiSessionV1GithubOAuthExchangeFactor;
    }
}
export declare class ApiSessionV1GithubOAuthFactor {
    constructor(id: string, providerSubject: string, emailId?: Nullable<string>);
    get id(): string;
    get providerSubject(): string;
    get emailId(): Nullable<string>;
    copy(id?: string, providerSubject?: string, emailId?: Nullable<string>): ApiSessionV1GithubOAuthFactor;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiSessionV1GithubOAuthFactor {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiSessionV1GithubOAuthFactor;
    }
}
export declare class ApiSessionV1GoogleOAuthExchangeFactor {
    constructor(emailId: string);
    get emailId(): string;
    copy(emailId?: string): ApiSessionV1GoogleOAuthExchangeFactor;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiSessionV1GoogleOAuthExchangeFactor {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiSessionV1GoogleOAuthExchangeFactor;
    }
}
export declare class ApiSessionV1GoogleOAuthFactor {
    constructor(id: string, providerSubject: string, emailId?: Nullable<string>);
    get id(): string;
    get providerSubject(): string;
    get emailId(): Nullable<string>;
    copy(id?: string, providerSubject?: string, emailId?: Nullable<string>): ApiSessionV1GoogleOAuthFactor;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiSessionV1GoogleOAuthFactor {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiSessionV1GoogleOAuthFactor;
    }
}
export declare class ApiSessionV1HubspotOAuthExchangeFactor {
    constructor(emailId: string);
    get emailId(): string;
    copy(emailId?: string): ApiSessionV1HubspotOAuthExchangeFactor;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiSessionV1HubspotOAuthExchangeFactor {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiSessionV1HubspotOAuthExchangeFactor;
    }
}
export declare class ApiSessionV1HubspotOAuthFactor {
    constructor(id: string, providerSubject: string, emailId?: Nullable<string>);
    get id(): string;
    get providerSubject(): string;
    get emailId(): Nullable<string>;
    copy(id?: string, providerSubject?: string, emailId?: Nullable<string>): ApiSessionV1HubspotOAuthFactor;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiSessionV1HubspotOAuthFactor {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiSessionV1HubspotOAuthFactor;
    }
}
export declare class ApiSessionV1ImpersonatedFactor {
    constructor(impersonatorId: string, impersonatorEmailAddress: string);
    get impersonatorId(): string;
    get impersonatorEmailAddress(): string;
    copy(impersonatorId?: string, impersonatorEmailAddress?: string): ApiSessionV1ImpersonatedFactor;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiSessionV1ImpersonatedFactor {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiSessionV1ImpersonatedFactor;
    }
}
export declare class ApiSessionV1InstagramOAuthFactor {
    constructor(id: string, providerSubject: string, emailId?: Nullable<string>);
    get id(): string;
    get providerSubject(): string;
    get emailId(): Nullable<string>;
    copy(id?: string, providerSubject?: string, emailId?: Nullable<string>): ApiSessionV1InstagramOAuthFactor;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiSessionV1InstagramOAuthFactor {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiSessionV1InstagramOAuthFactor;
    }
}
export declare class ApiSessionV1LinkedInOAuthFactor {
    constructor(id: string, providerSubject: string, emailId?: Nullable<string>);
    get id(): string;
    get providerSubject(): string;
    get emailId(): Nullable<string>;
    copy(id?: string, providerSubject?: string, emailId?: Nullable<string>): ApiSessionV1LinkedInOAuthFactor;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiSessionV1LinkedInOAuthFactor {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiSessionV1LinkedInOAuthFactor;
    }
}
export declare class ApiSessionV1MicrosoftOAuthFactor {
    constructor(id: string, providerSubject: string, emailId?: Nullable<string>);
    get id(): string;
    get providerSubject(): string;
    get emailId(): Nullable<string>;
    copy(id?: string, providerSubject?: string, emailId?: Nullable<string>): ApiSessionV1MicrosoftOAuthFactor;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiSessionV1MicrosoftOAuthFactor {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiSessionV1MicrosoftOAuthFactor;
    }
}
export declare class ApiSessionV1OAuthAccessTokenExchangeFactor {
    constructor(clientId: string);
    get clientId(): string;
    copy(clientId?: string): ApiSessionV1OAuthAccessTokenExchangeFactor;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiSessionV1OAuthAccessTokenExchangeFactor {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiSessionV1OAuthAccessTokenExchangeFactor;
    }
}
export declare class ApiSessionV1OIDCSSOFactor {
    constructor(id: string, providerId: string, externalId: string);
    get id(): string;
    get providerId(): string;
    get externalId(): string;
    copy(id?: string, providerId?: string, externalId?: string): ApiSessionV1OIDCSSOFactor;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiSessionV1OIDCSSOFactor {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiSessionV1OIDCSSOFactor;
    }
}
export declare class ApiSessionV1PhoneNumberFactor {
    constructor(phoneId: string, phoneNumber: string);
    get phoneId(): string;
    get phoneNumber(): string;
    copy(phoneId?: string, phoneNumber?: string): ApiSessionV1PhoneNumberFactor;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiSessionV1PhoneNumberFactor {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiSessionV1PhoneNumberFactor;
    }
}
export declare class ApiSessionV1RecoveryCodeFactor {
    constructor(totpRecoveryCodeId: string);
    get totpRecoveryCodeId(): string;
    copy(totpRecoveryCodeId?: string): ApiSessionV1RecoveryCodeFactor;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiSessionV1RecoveryCodeFactor {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiSessionV1RecoveryCodeFactor;
    }
}
export declare class ApiSessionV1SAMLSSOFactor {
    constructor(id: string, providerId: string, externalId: string);
    get id(): string;
    get providerId(): string;
    get externalId(): string;
    copy(id?: string, providerId?: string, externalId?: string): ApiSessionV1SAMLSSOFactor;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiSessionV1SAMLSSOFactor {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiSessionV1SAMLSSOFactor;
    }
}
export declare class ApiSessionV1SalesforceOAuthFactor {
    constructor(id: string, providerSubject: string, emailId?: Nullable<string>);
    get id(): string;
    get providerSubject(): string;
    get emailId(): Nullable<string>;
    copy(id?: string, providerSubject?: string, emailId?: Nullable<string>): ApiSessionV1SalesforceOAuthFactor;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiSessionV1SalesforceOAuthFactor {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiSessionV1SalesforceOAuthFactor;
    }
}
export declare class ApiSessionV1Session {
    constructor(sessionId: string, userId: string, authenticationFactors: KtList<ApiSessionV1AuthenticationFactor>, roles: KtList<string>, startedAt?: Nullable<any>/* Nullable<Instant> */, lastAccessedAt?: Nullable<any>/* Nullable<Instant> */, expiresAt?: Nullable<any>/* Nullable<Instant> */, attributes?: Nullable<ApiAttributeV1Attributes>, customClaims?: Nullable<KtMap<string, any/* JsonElement */>>);
    get sessionId(): string;
    get userId(): string;
    get authenticationFactors(): KtList<ApiSessionV1AuthenticationFactor>;
    get roles(): KtList<string>;
    get startedAt(): Nullable<any>/* Nullable<Instant> */;
    get lastAccessedAt(): Nullable<any>/* Nullable<Instant> */;
    get expiresAt(): Nullable<any>/* Nullable<Instant> */;
    get attributes(): Nullable<ApiAttributeV1Attributes>;
    get customClaims(): Nullable<KtMap<string, any/* JsonElement */>>;
    copy(sessionId?: string, userId?: string, authenticationFactors?: KtList<ApiSessionV1AuthenticationFactor>, roles?: KtList<string>, startedAt?: Nullable<any>/* Nullable<Instant> */, lastAccessedAt?: Nullable<any>/* Nullable<Instant> */, expiresAt?: Nullable<any>/* Nullable<Instant> */, attributes?: Nullable<ApiAttributeV1Attributes>, customClaims?: Nullable<KtMap<string, any/* JsonElement */>>): ApiSessionV1Session;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiSessionV1Session {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiSessionV1Session;
    }
}
export declare class ApiSessionV1ShopifyOAuthFactor {
    constructor(id: string, providerSubject: string, emailId?: Nullable<string>);
    get id(): string;
    get providerSubject(): string;
    get emailId(): Nullable<string>;
    copy(id?: string, providerSubject?: string, emailId?: Nullable<string>): ApiSessionV1ShopifyOAuthFactor;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiSessionV1ShopifyOAuthFactor {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiSessionV1ShopifyOAuthFactor;
    }
}
export declare class ApiSessionV1SlackOAuthExchangeFactor {
    constructor(emailId: string);
    get emailId(): string;
    copy(emailId?: string): ApiSessionV1SlackOAuthExchangeFactor;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiSessionV1SlackOAuthExchangeFactor {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiSessionV1SlackOAuthExchangeFactor;
    }
}
export declare class ApiSessionV1SlackOAuthFactor {
    constructor(id: string, providerSubject: string, emailId?: Nullable<string>);
    get id(): string;
    get providerSubject(): string;
    get emailId(): Nullable<string>;
    copy(id?: string, providerSubject?: string, emailId?: Nullable<string>): ApiSessionV1SlackOAuthFactor;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiSessionV1SlackOAuthFactor {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiSessionV1SlackOAuthFactor;
    }
}
export declare class ApiSessionV1SnapchatOAuthFactor {
    constructor(id: string, providerSubject: string, emailId?: Nullable<string>);
    get id(): string;
    get providerSubject(): string;
    get emailId(): Nullable<string>;
    copy(id?: string, providerSubject?: string, emailId?: Nullable<string>): ApiSessionV1SnapchatOAuthFactor;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiSessionV1SnapchatOAuthFactor {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiSessionV1SnapchatOAuthFactor;
    }
}
export declare class ApiSessionV1SpotifyOAuthFactor {
    constructor(id: string, providerSubject: string, emailId?: Nullable<string>);
    get id(): string;
    get providerSubject(): string;
    get emailId(): Nullable<string>;
    copy(id?: string, providerSubject?: string, emailId?: Nullable<string>): ApiSessionV1SpotifyOAuthFactor;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiSessionV1SpotifyOAuthFactor {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiSessionV1SpotifyOAuthFactor;
    }
}
export declare class ApiSessionV1SteamOAuthFactor {
    constructor(id: string, providerSubject: string, emailId?: Nullable<string>);
    get id(): string;
    get providerSubject(): string;
    get emailId(): Nullable<string>;
    copy(id?: string, providerSubject?: string, emailId?: Nullable<string>): ApiSessionV1SteamOAuthFactor;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiSessionV1SteamOAuthFactor {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiSessionV1SteamOAuthFactor;
    }
}
export declare class ApiSessionV1TikTokOAuthFactor {
    constructor(id: string, providerSubject: string, emailId?: Nullable<string>);
    get id(): string;
    get providerSubject(): string;
    get emailId(): Nullable<string>;
    copy(id?: string, providerSubject?: string, emailId?: Nullable<string>): ApiSessionV1TikTokOAuthFactor;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiSessionV1TikTokOAuthFactor {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiSessionV1TikTokOAuthFactor;
    }
}
export declare class ApiSessionV1TrustedAuthTokenFactor {
    constructor(tokenId: string);
    get tokenId(): string;
    copy(tokenId?: string): ApiSessionV1TrustedAuthTokenFactor;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiSessionV1TrustedAuthTokenFactor {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiSessionV1TrustedAuthTokenFactor;
    }
}
export declare class ApiSessionV1TwitchOAuthFactor {
    constructor(id: string, providerSubject: string, emailId?: Nullable<string>);
    get id(): string;
    get providerSubject(): string;
    get emailId(): Nullable<string>;
    copy(id?: string, providerSubject?: string, emailId?: Nullable<string>): ApiSessionV1TwitchOAuthFactor;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiSessionV1TwitchOAuthFactor {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiSessionV1TwitchOAuthFactor;
    }
}
export declare class ApiSessionV1TwitterOAuthFactor {
    constructor(id: string, providerSubject: string, emailId?: Nullable<string>);
    get id(): string;
    get providerSubject(): string;
    get emailId(): Nullable<string>;
    copy(id?: string, providerSubject?: string, emailId?: Nullable<string>): ApiSessionV1TwitterOAuthFactor;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiSessionV1TwitterOAuthFactor {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiSessionV1TwitterOAuthFactor;
    }
}
export declare class ApiSessionV1WebAuthnFactor {
    constructor(webauthnRegistrationId: string, domain: string, userAgent?: Nullable<string>);
    get webauthnRegistrationId(): string;
    get domain(): string;
    get userAgent(): Nullable<string>;
    copy(webauthnRegistrationId?: string, domain?: string, userAgent?: Nullable<string>): ApiSessionV1WebAuthnFactor;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiSessionV1WebAuthnFactor {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiSessionV1WebAuthnFactor;
    }
}
export declare class ApiSessionV1YahooOAuthFactor {
    constructor(id: string, providerSubject: string, emailId?: Nullable<string>);
    get id(): string;
    get providerSubject(): string;
    get emailId(): Nullable<string>;
    copy(id?: string, providerSubject?: string, emailId?: Nullable<string>): ApiSessionV1YahooOAuthFactor;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiSessionV1YahooOAuthFactor {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiSessionV1YahooOAuthFactor;
    }
}
export declare class ApiSsoV1EncryptionPrivateKey {
    constructor(privateKeyId: string, privateKey: string, createdAt?: Nullable<any>/* Nullable<Instant> */);
    get privateKeyId(): string;
    get privateKey(): string;
    get createdAt(): Nullable<any>/* Nullable<Instant> */;
    copy(privateKeyId?: string, privateKey?: string, createdAt?: Nullable<any>/* Nullable<Instant> */): ApiSsoV1EncryptionPrivateKey;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiSsoV1EncryptionPrivateKey {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiSsoV1EncryptionPrivateKey;
    }
}
export declare class ApiSsoV1ExternalConnection {
    constructor(organizationId: string, connectionId: string, externalOrganizationId: string, externalConnectionId: string, displayName: string, status: string, externalConnectionImplicitRoleAssignments: KtList<ApiSsoV1ExternalConnectionImplicitRoleAssignment>, externalGroupImplicitRoleAssignments: KtList<ApiSsoV1ExternalGroupImplicitRoleAssignment>);
    get organizationId(): string;
    get connectionId(): string;
    get externalOrganizationId(): string;
    get externalConnectionId(): string;
    get displayName(): string;
    get status(): string;
    get externalConnectionImplicitRoleAssignments(): KtList<ApiSsoV1ExternalConnectionImplicitRoleAssignment>;
    get externalGroupImplicitRoleAssignments(): KtList<ApiSsoV1ExternalGroupImplicitRoleAssignment>;
    copy(organizationId?: string, connectionId?: string, externalOrganizationId?: string, externalConnectionId?: string, displayName?: string, status?: string, externalConnectionImplicitRoleAssignments?: KtList<ApiSsoV1ExternalConnectionImplicitRoleAssignment>, externalGroupImplicitRoleAssignments?: KtList<ApiSsoV1ExternalGroupImplicitRoleAssignment>): ApiSsoV1ExternalConnection;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiSsoV1ExternalConnection {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiSsoV1ExternalConnection;
    }
}
export declare class ApiSsoV1ExternalConnectionImplicitRoleAssignment {
    constructor(roleId: string);
    get roleId(): string;
    copy(roleId?: string): ApiSsoV1ExternalConnectionImplicitRoleAssignment;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiSsoV1ExternalConnectionImplicitRoleAssignment {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiSsoV1ExternalConnectionImplicitRoleAssignment;
    }
}
export declare class ApiSsoV1ExternalGroupImplicitRoleAssignment {
    constructor(roleId: string, group: string);
    get roleId(): string;
    get group(): string;
    copy(roleId?: string, group?: string): ApiSsoV1ExternalGroupImplicitRoleAssignment;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiSsoV1ExternalGroupImplicitRoleAssignment {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiSsoV1ExternalGroupImplicitRoleAssignment;
    }
}
export declare class ApiSsoV1OIDCConnection {
    constructor(organizationId: string, connectionId: string, status: string, displayName: string, redirectUrl: string, clientId: string, clientSecret: string, issuer: string, authorizationUrl: string, tokenUrl: string, userinfoUrl: string, jwksUrl: string, identityProvider: string, customScopes: string, attributeMapping?: Nullable<KtMap<string, any/* JsonElement */>>);
    get organizationId(): string;
    get connectionId(): string;
    get status(): string;
    get displayName(): string;
    get redirectUrl(): string;
    get clientId(): string;
    get clientSecret(): string;
    get issuer(): string;
    get authorizationUrl(): string;
    get tokenUrl(): string;
    get userinfoUrl(): string;
    get jwksUrl(): string;
    get identityProvider(): string;
    get customScopes(): string;
    get attributeMapping(): Nullable<KtMap<string, any/* JsonElement */>>;
    copy(organizationId?: string, connectionId?: string, status?: string, displayName?: string, redirectUrl?: string, clientId?: string, clientSecret?: string, issuer?: string, authorizationUrl?: string, tokenUrl?: string, userinfoUrl?: string, jwksUrl?: string, identityProvider?: string, customScopes?: string, attributeMapping?: Nullable<KtMap<string, any/* JsonElement */>>): ApiSsoV1OIDCConnection;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiSsoV1OIDCConnection {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiSsoV1OIDCConnection;
    }
}
export declare class ApiSsoV1SAMLConnection {
    constructor(organizationId: string, connectionId: string, status: string, idpEntityId: string, displayName: string, idpSsoUrl: string, acsUrl: string, audienceUri: string, signingCertificates: KtList<ApiSsoV1X509Certificate>, verificationCertificates: KtList<ApiSsoV1X509Certificate>, encryptionPrivateKeys: KtList<ApiSsoV1EncryptionPrivateKey>, samlConnectionImplicitRoleAssignments: KtList<ApiSsoV1SAMLConnectionImplicitRoleAssignment>, samlGroupImplicitRoleAssignments: KtList<ApiSsoV1SAMLGroupImplicitRoleAssignment>, alternativeAudienceUri: string, identityProvider: string, nameidFormat: string, alternativeAcsUrl: string, idpInitiatedAuthDisabled: boolean, allowGatewayCallback: boolean, attributeMapping?: Nullable<KtMap<string, any/* JsonElement */>>);
    get organizationId(): string;
    get connectionId(): string;
    get status(): string;
    get idpEntityId(): string;
    get displayName(): string;
    get idpSsoUrl(): string;
    get acsUrl(): string;
    get audienceUri(): string;
    get signingCertificates(): KtList<ApiSsoV1X509Certificate>;
    get verificationCertificates(): KtList<ApiSsoV1X509Certificate>;
    get encryptionPrivateKeys(): KtList<ApiSsoV1EncryptionPrivateKey>;
    get samlConnectionImplicitRoleAssignments(): KtList<ApiSsoV1SAMLConnectionImplicitRoleAssignment>;
    get samlGroupImplicitRoleAssignments(): KtList<ApiSsoV1SAMLGroupImplicitRoleAssignment>;
    get alternativeAudienceUri(): string;
    get identityProvider(): string;
    get nameidFormat(): string;
    get alternativeAcsUrl(): string;
    get idpInitiatedAuthDisabled(): boolean;
    get allowGatewayCallback(): boolean;
    get attributeMapping(): Nullable<KtMap<string, any/* JsonElement */>>;
    copy(organizationId?: string, connectionId?: string, status?: string, idpEntityId?: string, displayName?: string, idpSsoUrl?: string, acsUrl?: string, audienceUri?: string, signingCertificates?: KtList<ApiSsoV1X509Certificate>, verificationCertificates?: KtList<ApiSsoV1X509Certificate>, encryptionPrivateKeys?: KtList<ApiSsoV1EncryptionPrivateKey>, samlConnectionImplicitRoleAssignments?: KtList<ApiSsoV1SAMLConnectionImplicitRoleAssignment>, samlGroupImplicitRoleAssignments?: KtList<ApiSsoV1SAMLGroupImplicitRoleAssignment>, alternativeAudienceUri?: string, identityProvider?: string, nameidFormat?: string, alternativeAcsUrl?: string, idpInitiatedAuthDisabled?: boolean, allowGatewayCallback?: boolean, attributeMapping?: Nullable<KtMap<string, any/* JsonElement */>>): ApiSsoV1SAMLConnection;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiSsoV1SAMLConnection {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiSsoV1SAMLConnection;
    }
}
export declare class ApiSsoV1SAMLConnectionImplicitRoleAssignment {
    constructor(roleId: string);
    get roleId(): string;
    copy(roleId?: string): ApiSsoV1SAMLConnectionImplicitRoleAssignment;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiSsoV1SAMLConnectionImplicitRoleAssignment {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiSsoV1SAMLConnectionImplicitRoleAssignment;
    }
}
export declare class ApiSsoV1SAMLGroupImplicitRoleAssignment {
    constructor(roleId: string, group: string);
    get roleId(): string;
    get group(): string;
    copy(roleId?: string, group?: string): ApiSsoV1SAMLGroupImplicitRoleAssignment;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiSsoV1SAMLGroupImplicitRoleAssignment {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiSsoV1SAMLGroupImplicitRoleAssignment;
    }
}
export declare class ApiSsoV1X509Certificate {
    constructor(certificateId: string, certificate: string, issuer: string, createdAt?: Nullable<any>/* Nullable<Instant> */, expiresAt?: Nullable<any>/* Nullable<Instant> */, updatedAt?: Nullable<any>/* Nullable<Instant> */);
    get certificateId(): string;
    get certificate(): string;
    get issuer(): string;
    get createdAt(): Nullable<any>/* Nullable<Instant> */;
    get expiresAt(): Nullable<any>/* Nullable<Instant> */;
    get updatedAt(): Nullable<any>/* Nullable<Instant> */;
    copy(certificateId?: string, certificate?: string, issuer?: string, createdAt?: Nullable<any>/* Nullable<Instant> */, expiresAt?: Nullable<any>/* Nullable<Instant> */, updatedAt?: Nullable<any>/* Nullable<Instant> */): ApiSsoV1X509Certificate;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiSsoV1X509Certificate {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiSsoV1X509Certificate;
    }
}
export declare class ApiTotpV1TOTP {
    constructor(totpId: string, verified: boolean, recoveryCodes: KtList<string>);
    get totpId(): string;
    get verified(): boolean;
    get recoveryCodes(): KtList<string>;
    copy(totpId?: string, verified?: boolean, recoveryCodes?: KtList<string>): ApiTotpV1TOTP;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiTotpV1TOTP {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiTotpV1TOTP;
    }
}
export declare class ApiUserV1BiometricRegistration {
    constructor(biometricRegistrationId: string, verified: boolean);
    get biometricRegistrationId(): string;
    get verified(): boolean;
    copy(biometricRegistrationId?: string, verified?: boolean): ApiUserV1BiometricRegistration;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiUserV1BiometricRegistration {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiUserV1BiometricRegistration;
    }
}
export declare class ApiUserV1CryptoWallet {
    constructor(cryptoWalletId: string, cryptoWalletAddress: string, cryptoWalletType: string, verified: boolean);
    get cryptoWalletId(): string;
    get cryptoWalletAddress(): string;
    get cryptoWalletType(): string;
    get verified(): boolean;
    copy(cryptoWalletId?: string, cryptoWalletAddress?: string, cryptoWalletType?: string, verified?: boolean): ApiUserV1CryptoWallet;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiUserV1CryptoWallet {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiUserV1CryptoWallet;
    }
}
export declare class ApiUserV1CryptoWalletString {
    constructor(cryptoWalletAddress: string, cryptoWalletType: string);
    get cryptoWalletAddress(): string;
    get cryptoWalletType(): string;
    copy(cryptoWalletAddress?: string, cryptoWalletType?: string): ApiUserV1CryptoWalletString;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiUserV1CryptoWalletString {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiUserV1CryptoWalletString;
    }
}
export declare class ApiUserV1Email {
    constructor(emailId: string, email: string, verified: boolean);
    get emailId(): string;
    get email(): string;
    get verified(): boolean;
    copy(emailId?: string, email?: string, verified?: boolean): ApiUserV1Email;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiUserV1Email {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiUserV1Email;
    }
}
export declare class ApiUserV1EmailString {
    constructor(email: string);
    get email(): string;
    copy(email?: string): ApiUserV1EmailString;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiUserV1EmailString {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiUserV1EmailString;
    }
}
export declare class ApiUserV1Name {
    constructor(firstName?: Nullable<string>, middleName?: Nullable<string>, lastName?: Nullable<string>);
    get firstName(): Nullable<string>;
    get middleName(): Nullable<string>;
    get lastName(): Nullable<string>;
    copy(firstName?: Nullable<string>, middleName?: Nullable<string>, lastName?: Nullable<string>): ApiUserV1Name;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiUserV1Name {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiUserV1Name;
    }
}
export declare class ApiUserV1OAuthProvider {
    constructor(providerType: string, providerSubject: string, profilePictureUrl: string, locale: string, oauthUserRegistrationId: string);
    get providerType(): string;
    get providerSubject(): string;
    get profilePictureUrl(): string;
    get locale(): string;
    get oauthUserRegistrationId(): string;
    copy(providerType?: string, providerSubject?: string, profilePictureUrl?: string, locale?: string, oauthUserRegistrationId?: string): ApiUserV1OAuthProvider;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiUserV1OAuthProvider {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiUserV1OAuthProvider;
    }
}
export declare class ApiUserV1Password {
    constructor(passwordId: string, requiresReset: boolean);
    get passwordId(): string;
    get requiresReset(): boolean;
    copy(passwordId?: string, requiresReset?: boolean): ApiUserV1Password;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiUserV1Password {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiUserV1Password;
    }
}
export declare class ApiUserV1PhoneNumber {
    constructor(phoneId: string, phoneNumber: string, verified: boolean);
    get phoneId(): string;
    get phoneNumber(): string;
    get verified(): boolean;
    copy(phoneId?: string, phoneNumber?: string, verified?: boolean): ApiUserV1PhoneNumber;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiUserV1PhoneNumber {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiUserV1PhoneNumber;
    }
}
export declare class ApiUserV1PhoneNumberString {
    constructor(phoneNumber: string);
    get phoneNumber(): string;
    copy(phoneNumber?: string): ApiUserV1PhoneNumberString;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiUserV1PhoneNumberString {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiUserV1PhoneNumberString;
    }
}
export declare class ApiUserV1TOTP {
    constructor(totpId: string, verified: boolean);
    get totpId(): string;
    get verified(): boolean;
    copy(totpId?: string, verified?: boolean): ApiUserV1TOTP;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiUserV1TOTP {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiUserV1TOTP;
    }
}
export declare class ApiUserV1User {
    constructor(userId: string, emails: KtList<ApiUserV1Email>, status: string, phoneNumbers: KtList<ApiUserV1PhoneNumber>, webauthnRegistrations: KtList<ApiUserV1WebAuthnRegistration>, providers: KtList<ApiUserV1OAuthProvider>, totps: KtList<ApiUserV1TOTP>, cryptoWallets: KtList<ApiUserV1CryptoWallet>, biometricRegistrations: KtList<ApiUserV1BiometricRegistration>, isLocked: boolean, roles: KtList<string>, name?: Nullable<ApiUserV1Name>, createdAt?: Nullable<any>/* Nullable<Instant> */, password?: Nullable<ApiUserV1Password>, trustedMetadata?: Nullable<KtMap<string, any/* JsonElement */>>, untrustedMetadata?: Nullable<KtMap<string, any/* JsonElement */>>, externalId?: Nullable<string>, lockCreatedAt?: Nullable<any>/* Nullable<Instant> */, lockExpiresAt?: Nullable<any>/* Nullable<Instant> */);
    get userId(): string;
    get emails(): KtList<ApiUserV1Email>;
    get status(): string;
    get phoneNumbers(): KtList<ApiUserV1PhoneNumber>;
    get webauthnRegistrations(): KtList<ApiUserV1WebAuthnRegistration>;
    get providers(): KtList<ApiUserV1OAuthProvider>;
    get totps(): KtList<ApiUserV1TOTP>;
    get cryptoWallets(): KtList<ApiUserV1CryptoWallet>;
    get biometricRegistrations(): KtList<ApiUserV1BiometricRegistration>;
    get isLocked(): boolean;
    get roles(): KtList<string>;
    get name(): Nullable<ApiUserV1Name>;
    get createdAt(): Nullable<any>/* Nullable<Instant> */;
    get password(): Nullable<ApiUserV1Password>;
    get trustedMetadata(): Nullable<KtMap<string, any/* JsonElement */>>;
    get untrustedMetadata(): Nullable<KtMap<string, any/* JsonElement */>>;
    get externalId(): Nullable<string>;
    get lockCreatedAt(): Nullable<any>/* Nullable<Instant> */;
    get lockExpiresAt(): Nullable<any>/* Nullable<Instant> */;
    copy(userId?: string, emails?: KtList<ApiUserV1Email>, status?: string, phoneNumbers?: KtList<ApiUserV1PhoneNumber>, webauthnRegistrations?: KtList<ApiUserV1WebAuthnRegistration>, providers?: KtList<ApiUserV1OAuthProvider>, totps?: KtList<ApiUserV1TOTP>, cryptoWallets?: KtList<ApiUserV1CryptoWallet>, biometricRegistrations?: KtList<ApiUserV1BiometricRegistration>, isLocked?: boolean, roles?: KtList<string>, name?: Nullable<ApiUserV1Name>, createdAt?: Nullable<any>/* Nullable<Instant> */, password?: Nullable<ApiUserV1Password>, trustedMetadata?: Nullable<KtMap<string, any/* JsonElement */>>, untrustedMetadata?: Nullable<KtMap<string, any/* JsonElement */>>, externalId?: Nullable<string>, lockCreatedAt?: Nullable<any>/* Nullable<Instant> */, lockExpiresAt?: Nullable<any>/* Nullable<Instant> */): ApiUserV1User;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiUserV1User {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiUserV1User;
    }
}
export declare class ApiUserV1UserConnectedApp {
    constructor(connectedAppId: string, name: string, description: string, clientType: string, scopesGranted: string, logoUrl?: Nullable<string>);
    get connectedAppId(): string;
    get name(): string;
    get description(): string;
    get clientType(): string;
    get scopesGranted(): string;
    get logoUrl(): Nullable<string>;
    copy(connectedAppId?: string, name?: string, description?: string, clientType?: string, scopesGranted?: string, logoUrl?: Nullable<string>): ApiUserV1UserConnectedApp;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiUserV1UserConnectedApp {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiUserV1UserConnectedApp;
    }
}
export declare class ApiUserV1WebAuthnRegistration {
    constructor(webauthnRegistrationId: string, domain: string, userAgent: string, verified: boolean, authenticatorType: string, name: string);
    get webauthnRegistrationId(): string;
    get domain(): string;
    get userAgent(): string;
    get verified(): boolean;
    get authenticatorType(): string;
    get name(): string;
    copy(webauthnRegistrationId?: string, domain?: string, userAgent?: string, verified?: boolean, authenticatorType?: string, name?: string): ApiUserV1WebAuthnRegistration;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ApiUserV1WebAuthnRegistration {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ApiUserV1WebAuthnRegistration;
    }
}
export declare class B2BAdminPortalConfigResponse implements StytchAPIResponse {
    constructor(ssoConfig: B2BSSOConfig, scimConfig: B2BSCIMConfig, organizationConfig: B2BOrganizationConfig, statusCode: number);
    get ssoConfig(): B2BSSOConfig;
    get scimConfig(): B2BSCIMConfig;
    get organizationConfig(): B2BOrganizationConfig;
    get statusCode(): number;
    copy(ssoConfig?: B2BSSOConfig, scimConfig?: B2BSCIMConfig, organizationConfig?: B2BOrganizationConfig, statusCode?: number): B2BAdminPortalConfigResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace B2BAdminPortalConfigResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BAdminPortalConfigResponse;
    }
}
export declare class B2BCreateExternalConnectionRequest {
    constructor(displayName: string, externalConnectionId: string, externalOrganizationId: string);
    get displayName(): string;
    get externalConnectionId(): string;
    get externalOrganizationId(): string;
    copy(displayName?: string, externalConnectionId?: string, externalOrganizationId?: string): B2BCreateExternalConnectionRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace B2BCreateExternalConnectionRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BCreateExternalConnectionRequest;
    }
}
export declare class B2BCreateExternalConnectionResponse implements StytchAPIResponse {
    constructor(requestId: string, connection: ApiSsoV1ExternalConnection, statusCode: number);
    get requestId(): string;
    get connection(): ApiSsoV1ExternalConnection;
    get statusCode(): number;
    copy(requestId?: string, connection?: ApiSsoV1ExternalConnection, statusCode?: number): B2BCreateExternalConnectionResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace B2BCreateExternalConnectionResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BCreateExternalConnectionResponse;
    }
}
export declare class B2BCreateOIDCConnectionRequest {
    constructor(displayName: string, identityProvider?: Nullable<string>);
    get displayName(): string;
    get identityProvider(): Nullable<string>;
    copy(displayName?: string, identityProvider?: Nullable<string>): B2BCreateOIDCConnectionRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace B2BCreateOIDCConnectionRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BCreateOIDCConnectionRequest;
    }
}
export declare class B2BCreateOIDCConnectionResponse implements StytchAPIResponse {
    constructor(requestId: string, connection: ApiSsoV1OIDCConnection, statusCode: number);
    get requestId(): string;
    get connection(): ApiSsoV1OIDCConnection;
    get statusCode(): number;
    copy(requestId?: string, connection?: ApiSsoV1OIDCConnection, statusCode?: number): B2BCreateOIDCConnectionResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace B2BCreateOIDCConnectionResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BCreateOIDCConnectionResponse;
    }
}
export declare class B2BCreateSAMLConnectionRequest {
    constructor(displayName: string, identityProvider?: Nullable<string>);
    get displayName(): string;
    get identityProvider(): Nullable<string>;
    copy(displayName?: string, identityProvider?: Nullable<string>): B2BCreateSAMLConnectionRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace B2BCreateSAMLConnectionRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BCreateSAMLConnectionRequest;
    }
}
export declare class B2BCreateSAMLConnectionResponse implements StytchAPIResponse {
    constructor(requestId: string, connection: ApiSsoV1SAMLConnection, statusCode: number);
    get requestId(): string;
    get connection(): ApiSsoV1SAMLConnection;
    get statusCode(): number;
    copy(requestId?: string, connection?: ApiSsoV1SAMLConnection, statusCode?: number): B2BCreateSAMLConnectionResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace B2BCreateSAMLConnectionResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BCreateSAMLConnectionResponse;
    }
}
export declare class B2BDeleteSAMLEncryptionPrivateKeyResponse implements StytchAPIResponse {
    constructor(requestId: string, privateKeyId: string, statusCode: number);
    get requestId(): string;
    get privateKeyId(): string;
    get statusCode(): number;
    copy(requestId?: string, privateKeyId?: string, statusCode?: number): B2BDeleteSAMLEncryptionPrivateKeyResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace B2BDeleteSAMLEncryptionPrivateKeyResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BDeleteSAMLEncryptionPrivateKeyResponse;
    }
}
export declare class B2BDeleteSAMLVerificationCertificateResponse implements StytchAPIResponse {
    constructor(requestId: string, certificateId: string, statusCode: number);
    get requestId(): string;
    get certificateId(): string;
    get statusCode(): number;
    copy(requestId?: string, certificateId?: string, statusCode?: number): B2BDeleteSAMLVerificationCertificateResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace B2BDeleteSAMLVerificationCertificateResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BDeleteSAMLVerificationCertificateResponse;
    }
}
export declare class B2BDeleteSSOConnectionResponse implements StytchAPIResponse {
    constructor(requestId: string, connectionId: string, statusCode: number);
    get requestId(): string;
    get connectionId(): string;
    get statusCode(): number;
    copy(requestId?: string, connectionId?: string, statusCode?: number): B2BDeleteSSOConnectionResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace B2BDeleteSSOConnectionResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BDeleteSSOConnectionResponse;
    }
}
export declare class B2BDiscoveryIntermediateSessionsExchangeRequest {
    constructor(intermediateSessionToken: string, organizationId: string, sessionDurationMinutes: number, locale?: Nullable<string>);
    get intermediateSessionToken(): string;
    get organizationId(): string;
    get sessionDurationMinutes(): number;
    get locale(): Nullable<string>;
    copy(intermediateSessionToken?: string, organizationId?: string, sessionDurationMinutes?: number, locale?: Nullable<string>): B2BDiscoveryIntermediateSessionsExchangeRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace B2BDiscoveryIntermediateSessionsExchangeRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BDiscoveryIntermediateSessionsExchangeRequest;
    }
}
export declare class B2BDiscoveryIntermediateSessionsExchangeResponse implements StytchAPIResponse/*, AuthenticatedResponse, B2BResponse */ {
    constructor(requestId: string, memberId: string, memberSession: ApiB2bSessionV1MemberSession, sessionToken: string, sessionJwt: string, member: ApiOrganizationV1Member, organization: ApiOrganizationV1Organization, memberAuthenticated: boolean, intermediateSessionToken: string, mfaRequired: ApiB2bMfaV1MfaRequired, primaryRequired: ApiB2bSessionV1PrimaryRequired, statusCode: number);
    get requestId(): string;
    get memberId(): string;
    get memberAuthenticated(): boolean;
    get primaryRequired(): ApiB2bSessionV1PrimaryRequired;
    get statusCode(): number;
    copy(requestId?: string, memberId?: string, memberSession?: ApiB2bSessionV1MemberSession, sessionToken?: string, sessionJwt?: string, member?: ApiOrganizationV1Member, organization?: ApiOrganizationV1Organization, memberAuthenticated?: boolean, intermediateSessionToken?: string, mfaRequired?: ApiB2bMfaV1MfaRequired, primaryRequired?: ApiB2bSessionV1PrimaryRequired, statusCode?: number): B2BDiscoveryIntermediateSessionsExchangeResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace B2BDiscoveryIntermediateSessionsExchangeResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BDiscoveryIntermediateSessionsExchangeResponse;
    }
}
export declare class B2BDiscoveryOrganizationsCreateRequest {
    constructor(intermediateSessionToken: string, sessionDurationMinutes: number, emailAllowedDomains: KtList<string>, allowedAuthMethods: KtList<string>, allowedMfaMethods: KtList<string>, organizationName?: Nullable<string>, organizationSlug?: Nullable<string>, organizationLogoUrl?: Nullable<string>, ssoJitProvisioning?: Nullable<string>, emailJitProvisioning?: Nullable<string>, emailInvites?: Nullable<string>, authMethods?: Nullable<string>, mfaMethods?: Nullable<string>, mfaPolicy?: Nullable<string>, oauthTenantJitProvisioning?: Nullable<string>, allowedOauthTenants?: Nullable<KtMap<string, any/* JsonElement */>>);
    get intermediateSessionToken(): string;
    get sessionDurationMinutes(): number;
    get emailAllowedDomains(): KtList<string>;
    get allowedAuthMethods(): KtList<string>;
    get allowedMfaMethods(): KtList<string>;
    get organizationName(): Nullable<string>;
    get organizationSlug(): Nullable<string>;
    get organizationLogoUrl(): Nullable<string>;
    get ssoJitProvisioning(): Nullable<string>;
    get emailJitProvisioning(): Nullable<string>;
    get emailInvites(): Nullable<string>;
    get authMethods(): Nullable<string>;
    get mfaMethods(): Nullable<string>;
    get mfaPolicy(): Nullable<string>;
    get oauthTenantJitProvisioning(): Nullable<string>;
    get allowedOauthTenants(): Nullable<KtMap<string, any/* JsonElement */>>;
    copy(intermediateSessionToken?: string, sessionDurationMinutes?: number, emailAllowedDomains?: KtList<string>, allowedAuthMethods?: KtList<string>, allowedMfaMethods?: KtList<string>, organizationName?: Nullable<string>, organizationSlug?: Nullable<string>, organizationLogoUrl?: Nullable<string>, ssoJitProvisioning?: Nullable<string>, emailJitProvisioning?: Nullable<string>, emailInvites?: Nullable<string>, authMethods?: Nullable<string>, mfaMethods?: Nullable<string>, mfaPolicy?: Nullable<string>, oauthTenantJitProvisioning?: Nullable<string>, allowedOauthTenants?: Nullable<KtMap<string, any/* JsonElement */>>): B2BDiscoveryOrganizationsCreateRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace B2BDiscoveryOrganizationsCreateRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BDiscoveryOrganizationsCreateRequest;
    }
}
export declare class B2BDiscoveryOrganizationsCreateResponse implements StytchAPIResponse/*, AuthenticatedResponse, B2BResponse */ {
    constructor(requestId: string, memberId: string, memberSession: ApiB2bSessionV1MemberSession, sessionToken: string, sessionJwt: string, member: ApiOrganizationV1Member, organization: ApiOrganizationV1Organization, memberAuthenticated: boolean, intermediateSessionToken: string, mfaRequired: ApiB2bMfaV1MfaRequired, primaryRequired: ApiB2bSessionV1PrimaryRequired, statusCode: number);
    get requestId(): string;
    get memberId(): string;
    get memberAuthenticated(): boolean;
    get primaryRequired(): ApiB2bSessionV1PrimaryRequired;
    get statusCode(): number;
    copy(requestId?: string, memberId?: string, memberSession?: ApiB2bSessionV1MemberSession, sessionToken?: string, sessionJwt?: string, member?: ApiOrganizationV1Member, organization?: ApiOrganizationV1Organization, memberAuthenticated?: boolean, intermediateSessionToken?: string, mfaRequired?: ApiB2bMfaV1MfaRequired, primaryRequired?: ApiB2bSessionV1PrimaryRequired, statusCode?: number): B2BDiscoveryOrganizationsCreateResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace B2BDiscoveryOrganizationsCreateResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BDiscoveryOrganizationsCreateResponse;
    }
}
export declare class B2BDiscoveryOrganizationsRequest {
    constructor(intermediateSessionToken?: Nullable<string>);
    get intermediateSessionToken(): Nullable<string>;
    copy(intermediateSessionToken?: Nullable<string>): B2BDiscoveryOrganizationsRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace B2BDiscoveryOrganizationsRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BDiscoveryOrganizationsRequest;
    }
}
export declare class B2BDiscoveryOrganizationsResponse implements StytchAPIResponse {
    constructor(requestId: string, emailAddress: string, discoveredOrganizations: KtList<ApiDiscoveryV1DiscoveredOrganization>, statusCode: number, organizationIdHint?: Nullable<string>);
    get requestId(): string;
    get emailAddress(): string;
    get discoveredOrganizations(): KtList<ApiDiscoveryV1DiscoveredOrganization>;
    get statusCode(): number;
    get organizationIdHint(): Nullable<string>;
    copy(requestId?: string, emailAddress?: string, discoveredOrganizations?: KtList<ApiDiscoveryV1DiscoveredOrganization>, statusCode?: number, organizationIdHint?: Nullable<string>): B2BDiscoveryOrganizationsResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace B2BDiscoveryOrganizationsResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BDiscoveryOrganizationsResponse;
    }
}
export declare class B2BDiscoveryPasswordResetRequest {
    constructor(passwordResetToken: string, password: string, pkceCodeVerifier?: Nullable<string>, captchaToken?: Nullable<string>, dfpTelemetryId?: Nullable<string>);
    get passwordResetToken(): string;
    get password(): string;
    get pkceCodeVerifier(): Nullable<string>;
    get captchaToken(): Nullable<string>;
    get dfpTelemetryId(): Nullable<string>;
    copy(passwordResetToken?: string, password?: string, pkceCodeVerifier?: Nullable<string>, captchaToken?: Nullable<string>, dfpTelemetryId?: Nullable<string>): B2BDiscoveryPasswordResetRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace B2BDiscoveryPasswordResetRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BDiscoveryPasswordResetRequest;
    }
}
export declare class B2BDiscoveryPasswordResetResponse implements StytchAPIResponse/*, B2BResponse */ {
    constructor(requestId: string, emailAddress: string, intermediateSessionToken: string, discoveredOrganizations: KtList<ApiDiscoveryV1DiscoveredOrganization>, statusCode: number);
    get requestId(): string;
    get emailAddress(): string;
    get discoveredOrganizations(): KtList<ApiDiscoveryV1DiscoveredOrganization>;
    get statusCode(): number;
    copy(requestId?: string, emailAddress?: string, intermediateSessionToken?: string, discoveredOrganizations?: KtList<ApiDiscoveryV1DiscoveredOrganization>, statusCode?: number): B2BDiscoveryPasswordResetResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace B2BDiscoveryPasswordResetResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BDiscoveryPasswordResetResponse;
    }
}
export declare class B2BDiscoveryPasswordResetStartRequest {
    constructor(emailAddress: string, discoveryRedirectUrl?: Nullable<string>, resetPasswordRedirectUrl?: Nullable<string>, resetPasswordExpirationMinutes?: Nullable<number>, resetPasswordTemplateId?: Nullable<string>, pkceCodeChallenge?: Nullable<string>, locale?: Nullable<string>, dfpTelemetryId?: Nullable<string>, captchaToken?: Nullable<string>, verifyEmailTemplateId?: Nullable<string>);
    get emailAddress(): string;
    get discoveryRedirectUrl(): Nullable<string>;
    get resetPasswordRedirectUrl(): Nullable<string>;
    get resetPasswordExpirationMinutes(): Nullable<number>;
    get resetPasswordTemplateId(): Nullable<string>;
    get pkceCodeChallenge(): Nullable<string>;
    get locale(): Nullable<string>;
    get dfpTelemetryId(): Nullable<string>;
    get captchaToken(): Nullable<string>;
    get verifyEmailTemplateId(): Nullable<string>;
    copy(emailAddress?: string, discoveryRedirectUrl?: Nullable<string>, resetPasswordRedirectUrl?: Nullable<string>, resetPasswordExpirationMinutes?: Nullable<number>, resetPasswordTemplateId?: Nullable<string>, pkceCodeChallenge?: Nullable<string>, locale?: Nullable<string>, dfpTelemetryId?: Nullable<string>, captchaToken?: Nullable<string>, verifyEmailTemplateId?: Nullable<string>): B2BDiscoveryPasswordResetStartRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace B2BDiscoveryPasswordResetStartRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BDiscoveryPasswordResetStartRequest;
    }
}
export declare class B2BDiscoveryPasswordResetStartResponse implements StytchAPIResponse {
    constructor(requestId: string, statusCode: number);
    get requestId(): string;
    get statusCode(): number;
    copy(requestId?: string, statusCode?: number): B2BDiscoveryPasswordResetStartResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace B2BDiscoveryPasswordResetStartResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BDiscoveryPasswordResetStartResponse;
    }
}
export declare class B2BGetMeResponse implements StytchAPIResponse {
    constructor(requestId: string, member: ApiOrganizationV1Member, statusCode: number);
    get requestId(): string;
    get member(): ApiOrganizationV1Member;
    get statusCode(): number;
    copy(requestId?: string, member?: ApiOrganizationV1Member, statusCode?: number): B2BGetMeResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace B2BGetMeResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BGetMeResponse;
    }
}
export declare class B2BGetSCIMConnectionGroupsRequest {
    constructor(cursor?: Nullable<string>, limit?: Nullable<number>);
    get cursor(): Nullable<string>;
    get limit(): Nullable<number>;
    copy(cursor?: Nullable<string>, limit?: Nullable<number>): B2BGetSCIMConnectionGroupsRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace B2BGetSCIMConnectionGroupsRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BGetSCIMConnectionGroupsRequest;
    }
}
export declare class B2BGetSCIMConnectionGroupsResponse implements StytchAPIResponse {
    constructor(scimGroups: KtList<ApiB2bScimV1SCIMGroup>, statusCode: number, nextCursor?: Nullable<string>);
    get scimGroups(): KtList<ApiB2bScimV1SCIMGroup>;
    get statusCode(): number;
    get nextCursor(): Nullable<string>;
    copy(scimGroups?: KtList<ApiB2bScimV1SCIMGroup>, statusCode?: number, nextCursor?: Nullable<string>): B2BGetSCIMConnectionGroupsResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace B2BGetSCIMConnectionGroupsResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BGetSCIMConnectionGroupsResponse;
    }
}
export declare class B2BGetSCIMConnectionResponse implements StytchAPIResponse {
    constructor(requestId: string, connection: ApiB2bScimV1SCIMConnection, statusCode: number);
    get requestId(): string;
    get connection(): ApiB2bScimV1SCIMConnection;
    get statusCode(): number;
    copy(requestId?: string, connection?: ApiB2bScimV1SCIMConnection, statusCode?: number): B2BGetSCIMConnectionResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace B2BGetSCIMConnectionResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BGetSCIMConnectionResponse;
    }
}
export declare class B2BGetSSOConnectionsResponse implements StytchAPIResponse {
    constructor(requestId: string, samlConnections: KtList<ApiSsoV1SAMLConnection>, oidcConnections: KtList<ApiSsoV1OIDCConnection>, externalConnections: KtList<ApiSsoV1ExternalConnection>, statusCode: number);
    get requestId(): string;
    get samlConnections(): KtList<ApiSsoV1SAMLConnection>;
    get oidcConnections(): KtList<ApiSsoV1OIDCConnection>;
    get externalConnections(): KtList<ApiSsoV1ExternalConnection>;
    get statusCode(): number;
    copy(requestId?: string, samlConnections?: KtList<ApiSsoV1SAMLConnection>, oidcConnections?: KtList<ApiSsoV1OIDCConnection>, externalConnections?: KtList<ApiSsoV1ExternalConnection>, statusCode?: number): B2BGetSSOConnectionsResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace B2BGetSSOConnectionsResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BGetSSOConnectionsResponse;
    }
}
export declare class B2BImpersonationTokenAuthenticateRequest {
    constructor(impersonationToken: string, dfpTelemetryId?: Nullable<string>, captchaToken?: Nullable<string>);
    get impersonationToken(): string;
    get dfpTelemetryId(): Nullable<string>;
    get captchaToken(): Nullable<string>;
    copy(impersonationToken?: string, dfpTelemetryId?: Nullable<string>, captchaToken?: Nullable<string>): B2BImpersonationTokenAuthenticateRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace B2BImpersonationTokenAuthenticateRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BImpersonationTokenAuthenticateRequest;
    }
}
export declare class B2BImpersonationTokenAuthenticateResponse implements StytchAPIResponse/*, AuthenticatedResponse, B2BResponse */ {
    constructor(requestId: string, memberId: string, organizationId: string, member: ApiOrganizationV1Member, sessionToken: string, sessionJwt: string, memberSession: ApiB2bSessionV1MemberSession, organization: ApiOrganizationV1Organization, intermediateSessionToken: string, memberAuthenticated: boolean, mfaRequired: ApiB2bMfaV1MfaRequired, statusCode: number);
    get requestId(): string;
    get memberId(): string;
    get organizationId(): string;
    get memberAuthenticated(): boolean;
    get statusCode(): number;
    copy(requestId?: string, memberId?: string, organizationId?: string, member?: ApiOrganizationV1Member, sessionToken?: string, sessionJwt?: string, memberSession?: ApiB2bSessionV1MemberSession, organization?: ApiOrganizationV1Organization, intermediateSessionToken?: string, memberAuthenticated?: boolean, mfaRequired?: ApiB2bMfaV1MfaRequired, statusCode?: number): B2BImpersonationTokenAuthenticateResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace B2BImpersonationTokenAuthenticateResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BImpersonationTokenAuthenticateResponse;
    }
}
export declare class B2BMagicLinksAuthenticateRequest {
    constructor(magicLinksToken: string, sessionDurationMinutes: number, pkceCodeVerifier?: Nullable<string>, locale?: Nullable<string>, dfpTelemetryId?: Nullable<string>, captchaToken?: Nullable<string>, intermediateSessionToken?: Nullable<string>);
    get magicLinksToken(): string;
    get sessionDurationMinutes(): number;
    get pkceCodeVerifier(): Nullable<string>;
    get locale(): Nullable<string>;
    get dfpTelemetryId(): Nullable<string>;
    get captchaToken(): Nullable<string>;
    get intermediateSessionToken(): Nullable<string>;
    copy(magicLinksToken?: string, sessionDurationMinutes?: number, pkceCodeVerifier?: Nullable<string>, locale?: Nullable<string>, dfpTelemetryId?: Nullable<string>, captchaToken?: Nullable<string>, intermediateSessionToken?: Nullable<string>): B2BMagicLinksAuthenticateRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace B2BMagicLinksAuthenticateRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BMagicLinksAuthenticateRequest;
    }
}
export declare class B2BMagicLinksAuthenticateResponse implements StytchAPIResponse/*, AuthenticatedResponse, B2BResponse */ {
    constructor(requestId: string, memberId: string, methodId: string, resetSessions: boolean, organizationId: string, member: ApiOrganizationV1Member, sessionToken: string, sessionJwt: string, memberSession: ApiB2bSessionV1MemberSession, organization: ApiOrganizationV1Organization, intermediateSessionToken: string, memberAuthenticated: boolean, mfaRequired: ApiB2bMfaV1MfaRequired, primaryRequired: ApiB2bSessionV1PrimaryRequired, memberDevice: SDKDeviceInfo, statusCode: number);
    get requestId(): string;
    get memberId(): string;
    get methodId(): string;
    get resetSessions(): boolean;
    get organizationId(): string;
    get memberAuthenticated(): boolean;
    get primaryRequired(): ApiB2bSessionV1PrimaryRequired;
    get memberDevice(): SDKDeviceInfo;
    get statusCode(): number;
    copy(requestId?: string, memberId?: string, methodId?: string, resetSessions?: boolean, organizationId?: string, member?: ApiOrganizationV1Member, sessionToken?: string, sessionJwt?: string, memberSession?: ApiB2bSessionV1MemberSession, organization?: ApiOrganizationV1Organization, intermediateSessionToken?: string, memberAuthenticated?: boolean, mfaRequired?: ApiB2bMfaV1MfaRequired, primaryRequired?: ApiB2bSessionV1PrimaryRequired, memberDevice?: SDKDeviceInfo, statusCode?: number): B2BMagicLinksAuthenticateResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace B2BMagicLinksAuthenticateResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BMagicLinksAuthenticateResponse;
    }
}
export declare class B2BMagicLinksDiscoveryAuthenticateRequest {
    constructor(discoveryMagicLinksToken: string, pkceCodeVerifier?: Nullable<string>, dfpTelemetryId?: Nullable<string>, captchaToken?: Nullable<string>);
    get discoveryMagicLinksToken(): string;
    get pkceCodeVerifier(): Nullable<string>;
    get dfpTelemetryId(): Nullable<string>;
    get captchaToken(): Nullable<string>;
    copy(discoveryMagicLinksToken?: string, pkceCodeVerifier?: Nullable<string>, dfpTelemetryId?: Nullable<string>, captchaToken?: Nullable<string>): B2BMagicLinksDiscoveryAuthenticateRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace B2BMagicLinksDiscoveryAuthenticateRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BMagicLinksDiscoveryAuthenticateRequest;
    }
}
export declare class B2BMagicLinksDiscoveryAuthenticateResponse implements StytchAPIResponse/*, B2BResponse */ {
    constructor(requestId: string, emailAddress: string, intermediateSessionToken: string, discoveredOrganizations: KtList<ApiDiscoveryV1DiscoveredOrganization>, statusCode: number);
    get requestId(): string;
    get emailAddress(): string;
    get discoveredOrganizations(): KtList<ApiDiscoveryV1DiscoveredOrganization>;
    get statusCode(): number;
    copy(requestId?: string, emailAddress?: string, intermediateSessionToken?: string, discoveredOrganizations?: KtList<ApiDiscoveryV1DiscoveredOrganization>, statusCode?: number): B2BMagicLinksDiscoveryAuthenticateResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace B2BMagicLinksDiscoveryAuthenticateResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BMagicLinksDiscoveryAuthenticateResponse;
    }
}
export declare class B2BMagicLinksDiscoveryEmailSendRequest {
    constructor(emailAddress: string, discoveryRedirectUrl?: Nullable<string>, pkceCodeChallenge?: Nullable<string>, loginTemplateId?: Nullable<string>, locale?: Nullable<string>, dfpTelemetryId?: Nullable<string>, captchaToken?: Nullable<string>, discoveryExpirationMinutes?: Nullable<number>);
    get emailAddress(): string;
    get discoveryRedirectUrl(): Nullable<string>;
    get pkceCodeChallenge(): Nullable<string>;
    get loginTemplateId(): Nullable<string>;
    get locale(): Nullable<string>;
    get dfpTelemetryId(): Nullable<string>;
    get captchaToken(): Nullable<string>;
    get discoveryExpirationMinutes(): Nullable<number>;
    copy(emailAddress?: string, discoveryRedirectUrl?: Nullable<string>, pkceCodeChallenge?: Nullable<string>, loginTemplateId?: Nullable<string>, locale?: Nullable<string>, dfpTelemetryId?: Nullable<string>, captchaToken?: Nullable<string>, discoveryExpirationMinutes?: Nullable<number>): B2BMagicLinksDiscoveryEmailSendRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace B2BMagicLinksDiscoveryEmailSendRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BMagicLinksDiscoveryEmailSendRequest;
    }
}
export declare class B2BMagicLinksDiscoveryEmailSendResponse implements StytchAPIResponse {
    constructor(requestId: string, statusCode: number);
    get requestId(): string;
    get statusCode(): number;
    copy(requestId?: string, statusCode?: number): B2BMagicLinksDiscoveryEmailSendResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace B2BMagicLinksDiscoveryEmailSendResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BMagicLinksDiscoveryEmailSendResponse;
    }
}
export declare class B2BMagicLinksInviteRequest {
    constructor(emailAddress: string, roles: KtList<string>, inviteRedirectUrl?: Nullable<string>, inviteTemplateId?: Nullable<string>, name?: Nullable<string>, untrustedMetadata?: Nullable<KtMap<string, any/* JsonElement */>>, locale?: Nullable<string>);
    get emailAddress(): string;
    get roles(): KtList<string>;
    get inviteRedirectUrl(): Nullable<string>;
    get inviteTemplateId(): Nullable<string>;
    get name(): Nullable<string>;
    get untrustedMetadata(): Nullable<KtMap<string, any/* JsonElement */>>;
    get locale(): Nullable<string>;
    copy(emailAddress?: string, roles?: KtList<string>, inviteRedirectUrl?: Nullable<string>, inviteTemplateId?: Nullable<string>, name?: Nullable<string>, untrustedMetadata?: Nullable<KtMap<string, any/* JsonElement */>>, locale?: Nullable<string>): B2BMagicLinksInviteRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace B2BMagicLinksInviteRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BMagicLinksInviteRequest;
    }
}
export declare class B2BMagicLinksInviteResponse implements StytchAPIResponse {
    constructor(requestId: string, memberId: string, member: ApiOrganizationV1Member, organization: ApiOrganizationV1Organization, statusCode: number);
    get requestId(): string;
    get memberId(): string;
    get member(): ApiOrganizationV1Member;
    get organization(): ApiOrganizationV1Organization;
    get statusCode(): number;
    copy(requestId?: string, memberId?: string, member?: ApiOrganizationV1Member, organization?: ApiOrganizationV1Organization, statusCode?: number): B2BMagicLinksInviteResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace B2BMagicLinksInviteResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BMagicLinksInviteResponse;
    }
}
export declare class B2BMagicLinksLoginOrSignupRequest {
    constructor(emailAddress: string, organizationId: string, loginRedirectUrl?: Nullable<string>, signupRedirectUrl?: Nullable<string>, pkceCodeChallenge?: Nullable<string>, loginTemplateId?: Nullable<string>, signupTemplateId?: Nullable<string>, locale?: Nullable<string>);
    get emailAddress(): string;
    get organizationId(): string;
    get loginRedirectUrl(): Nullable<string>;
    get signupRedirectUrl(): Nullable<string>;
    get pkceCodeChallenge(): Nullable<string>;
    get loginTemplateId(): Nullable<string>;
    get signupTemplateId(): Nullable<string>;
    get locale(): Nullable<string>;
    copy(emailAddress?: string, organizationId?: string, loginRedirectUrl?: Nullable<string>, signupRedirectUrl?: Nullable<string>, pkceCodeChallenge?: Nullable<string>, loginTemplateId?: Nullable<string>, signupTemplateId?: Nullable<string>, locale?: Nullable<string>): B2BMagicLinksLoginOrSignupRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace B2BMagicLinksLoginOrSignupRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BMagicLinksLoginOrSignupRequest;
    }
}
export declare class B2BMagicLinksLoginOrSignupResponse implements StytchAPIResponse {
    constructor(requestId: string, statusCode: number);
    get requestId(): string;
    get statusCode(): number;
    copy(requestId?: string, statusCode?: number): B2BMagicLinksLoginOrSignupResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace B2BMagicLinksLoginOrSignupResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BMagicLinksLoginOrSignupResponse;
    }
}
export declare class B2BOAuthAuthenticateRequest {
    constructor(oauthToken: string, sessionDurationMinutes?: Nullable<number>, pkceCodeVerifier?: Nullable<string>, locale?: Nullable<string>, dfpTelemetryId?: Nullable<string>, captchaToken?: Nullable<string>, intermediateSessionToken?: Nullable<string>);
    get oauthToken(): string;
    get sessionDurationMinutes(): Nullable<number>;
    get pkceCodeVerifier(): Nullable<string>;
    get locale(): Nullable<string>;
    get dfpTelemetryId(): Nullable<string>;
    get captchaToken(): Nullable<string>;
    get intermediateSessionToken(): Nullable<string>;
    copy(oauthToken?: string, sessionDurationMinutes?: Nullable<number>, pkceCodeVerifier?: Nullable<string>, locale?: Nullable<string>, dfpTelemetryId?: Nullable<string>, captchaToken?: Nullable<string>, intermediateSessionToken?: Nullable<string>): B2BOAuthAuthenticateRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace B2BOAuthAuthenticateRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BOAuthAuthenticateRequest;
    }
}
export declare class B2BOAuthAuthenticateResponse implements StytchAPIResponse/*, AuthenticatedResponse, B2BResponse */ {
    constructor(requestId: string, memberId: string, providerSubject: string, providerType: string, memberSession: ApiB2bSessionV1MemberSession, sessionToken: string, sessionJwt: string, providerValues: ApiB2bOauthV1ProviderValues, member: ApiOrganizationV1Member, organizationId: string, organization: ApiOrganizationV1Organization, resetSessions: boolean, memberAuthenticated: boolean, intermediateSessionToken: string, mfaRequired: ApiB2bMfaV1MfaRequired, primaryRequired: ApiB2bSessionV1PrimaryRequired, memberDevice: SDKDeviceInfo, statusCode: number);
    get requestId(): string;
    get memberId(): string;
    get providerSubject(): string;
    get providerType(): string;
    get providerValues(): ApiB2bOauthV1ProviderValues;
    get organizationId(): string;
    get resetSessions(): boolean;
    get memberAuthenticated(): boolean;
    get primaryRequired(): ApiB2bSessionV1PrimaryRequired;
    get memberDevice(): SDKDeviceInfo;
    get statusCode(): number;
    copy(requestId?: string, memberId?: string, providerSubject?: string, providerType?: string, memberSession?: ApiB2bSessionV1MemberSession, sessionToken?: string, sessionJwt?: string, providerValues?: ApiB2bOauthV1ProviderValues, member?: ApiOrganizationV1Member, organizationId?: string, organization?: ApiOrganizationV1Organization, resetSessions?: boolean, memberAuthenticated?: boolean, intermediateSessionToken?: string, mfaRequired?: ApiB2bMfaV1MfaRequired, primaryRequired?: ApiB2bSessionV1PrimaryRequired, memberDevice?: SDKDeviceInfo, statusCode?: number): B2BOAuthAuthenticateResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace B2BOAuthAuthenticateResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BOAuthAuthenticateResponse;
    }
}
export declare class B2BOAuthAuthorizeRequest {
    constructor(clientId: string, redirectUri: string, responseType: string, scope: string, state?: Nullable<string>, nonce?: Nullable<string>, codeChallenge?: Nullable<string>, consentGranted?: Nullable<string>, prompt?: Nullable<string>, resources?: Nullable<KtList<string>>);
    get clientId(): string;
    get redirectUri(): string;
    get responseType(): string;
    get scope(): string;
    get state(): Nullable<string>;
    get nonce(): Nullable<string>;
    get codeChallenge(): Nullable<string>;
    get consentGranted(): Nullable<string>;
    get prompt(): Nullable<string>;
    get resources(): Nullable<KtList<string>>;
    copy(clientId?: string, redirectUri?: string, responseType?: string, scope?: string, state?: Nullable<string>, nonce?: Nullable<string>, codeChallenge?: Nullable<string>, consentGranted?: Nullable<string>, prompt?: Nullable<string>, resources?: Nullable<KtList<string>>): B2BOAuthAuthorizeRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace B2BOAuthAuthorizeRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BOAuthAuthorizeRequest;
    }
}
export declare class B2BOAuthAuthorizeResponse implements StytchAPIResponse {
    constructor(requestId: string, redirectUrl: string, statusCode: number);
    get requestId(): string;
    get redirectUrl(): string;
    get statusCode(): number;
    copy(requestId?: string, redirectUrl?: string, statusCode?: number): B2BOAuthAuthorizeResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace B2BOAuthAuthorizeResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BOAuthAuthorizeResponse;
    }
}
export declare class B2BOAuthAuthorizeStartRequest {
    constructor(clientId: string, redirectUri: string, responseType: string, scope: string, prompt?: Nullable<string>);
    get clientId(): string;
    get redirectUri(): string;
    get responseType(): string;
    get scope(): string;
    get prompt(): Nullable<string>;
    copy(clientId?: string, redirectUri?: string, responseType?: string, scope?: string, prompt?: Nullable<string>): B2BOAuthAuthorizeStartRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace B2BOAuthAuthorizeStartRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BOAuthAuthorizeStartRequest;
    }
}
export declare class B2BOAuthAuthorizeStartResponse implements StytchAPIResponse {
    constructor(requestId: string, consentRequired: boolean, oidcClient: ApiConnectedappsV1ConnectedAppPublic, grantableScope: string, ungrantableScope: string, statusCode: number);
    get requestId(): string;
    get consentRequired(): boolean;
    get oidcClient(): ApiConnectedappsV1ConnectedAppPublic;
    get grantableScope(): string;
    get ungrantableScope(): string;
    get statusCode(): number;
    copy(requestId?: string, consentRequired?: boolean, oidcClient?: ApiConnectedappsV1ConnectedAppPublic, grantableScope?: string, ungrantableScope?: string, statusCode?: number): B2BOAuthAuthorizeStartResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace B2BOAuthAuthorizeStartResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BOAuthAuthorizeStartResponse;
    }
}
export declare class B2BOAuthDiscoveryAuthenticateRequest {
    constructor(discoveryOauthToken: string, pkceCodeVerifier?: Nullable<string>, dfpTelemetryId?: Nullable<string>, captchaToken?: Nullable<string>);
    get discoveryOauthToken(): string;
    get pkceCodeVerifier(): Nullable<string>;
    get dfpTelemetryId(): Nullable<string>;
    get captchaToken(): Nullable<string>;
    copy(discoveryOauthToken?: string, pkceCodeVerifier?: Nullable<string>, dfpTelemetryId?: Nullable<string>, captchaToken?: Nullable<string>): B2BOAuthDiscoveryAuthenticateRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace B2BOAuthDiscoveryAuthenticateRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BOAuthDiscoveryAuthenticateRequest;
    }
}
export declare class B2BOAuthDiscoveryAuthenticateResponse implements StytchAPIResponse/*, B2BResponse */ {
    constructor(requestId: string, intermediateSessionToken: string, emailAddress: string, discoveredOrganizations: KtList<ApiDiscoveryV1DiscoveredOrganization>, providerType: string, providerTenantId: string, fullName: string, statusCode: number);
    get requestId(): string;
    get emailAddress(): string;
    get discoveredOrganizations(): KtList<ApiDiscoveryV1DiscoveredOrganization>;
    get providerType(): string;
    get providerTenantId(): string;
    get fullName(): string;
    get statusCode(): number;
    copy(requestId?: string, intermediateSessionToken?: string, emailAddress?: string, discoveredOrganizations?: KtList<ApiDiscoveryV1DiscoveredOrganization>, providerType?: string, providerTenantId?: string, fullName?: string, statusCode?: number): B2BOAuthDiscoveryAuthenticateResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace B2BOAuthDiscoveryAuthenticateResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BOAuthDiscoveryAuthenticateResponse;
    }
}
export declare class B2BOAuthGoogleOneTapDiscoverySubmitRequest {
    constructor(idToken: string, discoveryRedirectUrl?: Nullable<string>, pkceCodeChallenge?: Nullable<string>);
    get idToken(): string;
    get discoveryRedirectUrl(): Nullable<string>;
    get pkceCodeChallenge(): Nullable<string>;
    copy(idToken?: string, discoveryRedirectUrl?: Nullable<string>, pkceCodeChallenge?: Nullable<string>): B2BOAuthGoogleOneTapDiscoverySubmitRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace B2BOAuthGoogleOneTapDiscoverySubmitRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BOAuthGoogleOneTapDiscoverySubmitRequest;
    }
}
export declare class B2BOAuthGoogleOneTapDiscoverySubmitResponse implements StytchAPIResponse {
    constructor(requestId: string, redirectUrl: string, statusCode: number);
    get requestId(): string;
    get redirectUrl(): string;
    get statusCode(): number;
    copy(requestId?: string, redirectUrl?: string, statusCode?: number): B2BOAuthGoogleOneTapDiscoverySubmitResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace B2BOAuthGoogleOneTapDiscoverySubmitResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BOAuthGoogleOneTapDiscoverySubmitResponse;
    }
}
export declare class B2BOAuthGoogleOneTapStartResponse implements StytchAPIResponse {
    constructor(requestId: string, googleClientId: string, statusCode: number);
    get requestId(): string;
    get googleClientId(): string;
    get statusCode(): number;
    copy(requestId?: string, googleClientId?: string, statusCode?: number): B2BOAuthGoogleOneTapStartResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace B2BOAuthGoogleOneTapStartResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BOAuthGoogleOneTapStartResponse;
    }
}
export declare class B2BOAuthGoogleOneTapSubmitRequest {
    constructor(idToken: string, organizationId: string, loginRedirectUrl?: Nullable<string>, signupRedirectUrl?: Nullable<string>, pkceCodeChallenge?: Nullable<string>);
    get idToken(): string;
    get organizationId(): string;
    get loginRedirectUrl(): Nullable<string>;
    get signupRedirectUrl(): Nullable<string>;
    get pkceCodeChallenge(): Nullable<string>;
    copy(idToken?: string, organizationId?: string, loginRedirectUrl?: Nullable<string>, signupRedirectUrl?: Nullable<string>, pkceCodeChallenge?: Nullable<string>): B2BOAuthGoogleOneTapSubmitRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace B2BOAuthGoogleOneTapSubmitRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BOAuthGoogleOneTapSubmitRequest;
    }
}
export declare class B2BOAuthGoogleOneTapSubmitResponse implements StytchAPIResponse {
    constructor(requestId: string, redirectUrl: string, statusCode: number);
    get requestId(): string;
    get redirectUrl(): string;
    get statusCode(): number;
    copy(requestId?: string, redirectUrl?: string, statusCode?: number): B2BOAuthGoogleOneTapSubmitResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace B2BOAuthGoogleOneTapSubmitResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BOAuthGoogleOneTapSubmitResponse;
    }
}
export declare class B2BOTPsEmailAuthenticateRequest {
    constructor(organizationId: string, emailAddress: string, code: string, sessionDurationMinutes: number, intermediateSessionToken?: Nullable<string>, locale?: Nullable<string>, captchaToken?: Nullable<string>, dfpTelemetryId?: Nullable<string>);
    get organizationId(): string;
    get emailAddress(): string;
    get code(): string;
    get sessionDurationMinutes(): number;
    get intermediateSessionToken(): Nullable<string>;
    get locale(): Nullable<string>;
    get captchaToken(): Nullable<string>;
    get dfpTelemetryId(): Nullable<string>;
    copy(organizationId?: string, emailAddress?: string, code?: string, sessionDurationMinutes?: number, intermediateSessionToken?: Nullable<string>, locale?: Nullable<string>, captchaToken?: Nullable<string>, dfpTelemetryId?: Nullable<string>): B2BOTPsEmailAuthenticateRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace B2BOTPsEmailAuthenticateRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BOTPsEmailAuthenticateRequest;
    }
}
export declare class B2BOTPsEmailAuthenticateResponse implements StytchAPIResponse/*, AuthenticatedResponse, B2BResponse */ {
    constructor(requestId: string, memberId: string, methodId: string, organizationId: string, member: ApiOrganizationV1Member, sessionToken: string, sessionJwt: string, memberSession: ApiB2bSessionV1MemberSession, organization: ApiOrganizationV1Organization, intermediateSessionToken: string, memberAuthenticated: boolean, mfaRequired: ApiB2bMfaV1MfaRequired, primaryRequired: ApiB2bSessionV1PrimaryRequired, memberDevice: SDKDeviceInfo, statusCode: number);
    get requestId(): string;
    get memberId(): string;
    get methodId(): string;
    get organizationId(): string;
    get memberAuthenticated(): boolean;
    get primaryRequired(): ApiB2bSessionV1PrimaryRequired;
    get memberDevice(): SDKDeviceInfo;
    get statusCode(): number;
    copy(requestId?: string, memberId?: string, methodId?: string, organizationId?: string, member?: ApiOrganizationV1Member, sessionToken?: string, sessionJwt?: string, memberSession?: ApiB2bSessionV1MemberSession, organization?: ApiOrganizationV1Organization, intermediateSessionToken?: string, memberAuthenticated?: boolean, mfaRequired?: ApiB2bMfaV1MfaRequired, primaryRequired?: ApiB2bSessionV1PrimaryRequired, memberDevice?: SDKDeviceInfo, statusCode?: number): B2BOTPsEmailAuthenticateResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace B2BOTPsEmailAuthenticateResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BOTPsEmailAuthenticateResponse;
    }
}
export declare class B2BOTPsEmailDiscoveryAuthenticateRequest {
    constructor(emailAddress: string, code: string, captchaToken?: Nullable<string>, dfpTelemetryId?: Nullable<string>);
    get emailAddress(): string;
    get code(): string;
    get captchaToken(): Nullable<string>;
    get dfpTelemetryId(): Nullable<string>;
    copy(emailAddress?: string, code?: string, captchaToken?: Nullable<string>, dfpTelemetryId?: Nullable<string>): B2BOTPsEmailDiscoveryAuthenticateRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace B2BOTPsEmailDiscoveryAuthenticateRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BOTPsEmailDiscoveryAuthenticateRequest;
    }
}
export declare class B2BOTPsEmailDiscoveryAuthenticateResponse implements StytchAPIResponse/*, B2BResponse */ {
    constructor(requestId: string, emailAddress: string, intermediateSessionToken: string, discoveredOrganizations: KtList<ApiDiscoveryV1DiscoveredOrganization>, statusCode: number);
    get requestId(): string;
    get emailAddress(): string;
    get discoveredOrganizations(): KtList<ApiDiscoveryV1DiscoveredOrganization>;
    get statusCode(): number;
    copy(requestId?: string, emailAddress?: string, intermediateSessionToken?: string, discoveredOrganizations?: KtList<ApiDiscoveryV1DiscoveredOrganization>, statusCode?: number): B2BOTPsEmailDiscoveryAuthenticateResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace B2BOTPsEmailDiscoveryAuthenticateResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BOTPsEmailDiscoveryAuthenticateResponse;
    }
}
export declare class B2BOTPsEmailDiscoverySendRequest {
    constructor(emailAddress: string, loginTemplateId?: Nullable<string>, locale?: Nullable<string>, captchaToken?: Nullable<string>, dfpTelemetryId?: Nullable<string>, discoveryExpirationMinutes?: Nullable<number>);
    get emailAddress(): string;
    get loginTemplateId(): Nullable<string>;
    get locale(): Nullable<string>;
    get captchaToken(): Nullable<string>;
    get dfpTelemetryId(): Nullable<string>;
    get discoveryExpirationMinutes(): Nullable<number>;
    copy(emailAddress?: string, loginTemplateId?: Nullable<string>, locale?: Nullable<string>, captchaToken?: Nullable<string>, dfpTelemetryId?: Nullable<string>, discoveryExpirationMinutes?: Nullable<number>): B2BOTPsEmailDiscoverySendRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace B2BOTPsEmailDiscoverySendRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BOTPsEmailDiscoverySendRequest;
    }
}
export declare class B2BOTPsEmailDiscoverySendResponse implements StytchAPIResponse {
    constructor(requestId: string, statusCode: number);
    get requestId(): string;
    get statusCode(): number;
    copy(requestId?: string, statusCode?: number): B2BOTPsEmailDiscoverySendResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace B2BOTPsEmailDiscoverySendResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BOTPsEmailDiscoverySendResponse;
    }
}
export declare class B2BOTPsEmailLoginOrSignupRequest {
    constructor(organizationId: string, emailAddress: string, loginTemplateId?: Nullable<string>, signupTemplateId?: Nullable<string>, locale?: Nullable<string>, captchaToken?: Nullable<string>, dfpTelemetryId?: Nullable<string>, loginExpirationMinutes?: Nullable<number>, signupExpirationMinutes?: Nullable<number>);
    get organizationId(): string;
    get emailAddress(): string;
    get loginTemplateId(): Nullable<string>;
    get signupTemplateId(): Nullable<string>;
    get locale(): Nullable<string>;
    get captchaToken(): Nullable<string>;
    get dfpTelemetryId(): Nullable<string>;
    get loginExpirationMinutes(): Nullable<number>;
    get signupExpirationMinutes(): Nullable<number>;
    copy(organizationId?: string, emailAddress?: string, loginTemplateId?: Nullable<string>, signupTemplateId?: Nullable<string>, locale?: Nullable<string>, captchaToken?: Nullable<string>, dfpTelemetryId?: Nullable<string>, loginExpirationMinutes?: Nullable<number>, signupExpirationMinutes?: Nullable<number>): B2BOTPsEmailLoginOrSignupRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace B2BOTPsEmailLoginOrSignupRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BOTPsEmailLoginOrSignupRequest;
    }
}
export declare class B2BOTPsEmailLoginOrSignupResponse implements StytchAPIResponse {
    constructor(requestId: string, statusCode: number);
    get requestId(): string;
    get statusCode(): number;
    copy(requestId?: string, statusCode?: number): B2BOTPsEmailLoginOrSignupResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace B2BOTPsEmailLoginOrSignupResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BOTPsEmailLoginOrSignupResponse;
    }
}
export declare class B2BOTPsSMSAuthenticateRequest {
    constructor(organizationId: string, memberId: string, code: string, sessionDurationMinutes: number, intermediateSessionToken?: Nullable<string>, setMfaEnrollment?: Nullable<string>, captchaToken?: Nullable<string>, dfpTelemetryId?: Nullable<string>);
    get organizationId(): string;
    get memberId(): string;
    get code(): string;
    get sessionDurationMinutes(): number;
    get intermediateSessionToken(): Nullable<string>;
    get setMfaEnrollment(): Nullable<string>;
    get captchaToken(): Nullable<string>;
    get dfpTelemetryId(): Nullable<string>;
    copy(organizationId?: string, memberId?: string, code?: string, sessionDurationMinutes?: number, intermediateSessionToken?: Nullable<string>, setMfaEnrollment?: Nullable<string>, captchaToken?: Nullable<string>, dfpTelemetryId?: Nullable<string>): B2BOTPsSMSAuthenticateRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace B2BOTPsSMSAuthenticateRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BOTPsSMSAuthenticateRequest;
    }
}
export declare class B2BOTPsSMSAuthenticateResponse implements StytchAPIResponse/*, AuthenticatedResponse */ {
    constructor(requestId: string, memberId: string, member: ApiOrganizationV1Member, organization: ApiOrganizationV1Organization, sessionToken: string, sessionJwt: string, memberSession: ApiB2bSessionV1MemberSession, memberDevice: SDKDeviceInfo, statusCode: number);
    get requestId(): string;
    get memberId(): string;
    get memberDevice(): SDKDeviceInfo;
    get statusCode(): number;
    copy(requestId?: string, memberId?: string, member?: ApiOrganizationV1Member, organization?: ApiOrganizationV1Organization, sessionToken?: string, sessionJwt?: string, memberSession?: ApiB2bSessionV1MemberSession, memberDevice?: SDKDeviceInfo, statusCode?: number): B2BOTPsSMSAuthenticateResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace B2BOTPsSMSAuthenticateResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BOTPsSMSAuthenticateResponse;
    }
}
export declare class B2BOTPsSMSSendRequest {
    constructor(organizationId: string, memberId: string, mfaPhoneNumber?: Nullable<string>, locale?: Nullable<string>, intermediateSessionToken?: Nullable<string>, captchaToken?: Nullable<string>, dfpTelemetryId?: Nullable<string>, enableAutofill?: Nullable<boolean>);
    get organizationId(): string;
    get memberId(): string;
    get mfaPhoneNumber(): Nullable<string>;
    get locale(): Nullable<string>;
    get intermediateSessionToken(): Nullable<string>;
    get captchaToken(): Nullable<string>;
    get dfpTelemetryId(): Nullable<string>;
    get enableAutofill(): Nullable<boolean>;
    copy(organizationId?: string, memberId?: string, mfaPhoneNumber?: Nullable<string>, locale?: Nullable<string>, intermediateSessionToken?: Nullable<string>, captchaToken?: Nullable<string>, dfpTelemetryId?: Nullable<string>, enableAutofill?: Nullable<boolean>): B2BOTPsSMSSendRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace B2BOTPsSMSSendRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BOTPsSMSSendRequest;
    }
}
export declare class B2BOTPsSMSSendResponse implements StytchAPIResponse {
    constructor(requestId: string, statusCode: number);
    get requestId(): string;
    get statusCode(): number;
    copy(requestId?: string, statusCode?: number): B2BOTPsSMSSendResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace B2BOTPsSMSSendResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BOTPsSMSSendResponse;
    }
}
export declare class B2BOrganizationConfig {
    constructor(mfaControlsEnabled: boolean);
    get mfaControlsEnabled(): boolean;
    copy(mfaControlsEnabled?: boolean): B2BOrganizationConfig;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace B2BOrganizationConfig {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BOrganizationConfig;
    }
}
export declare class B2BOrganizationsDeleteResponse implements StytchAPIResponse {
    constructor(requestId: string, organizationId: string, statusCode: number);
    get requestId(): string;
    get organizationId(): string;
    get statusCode(): number;
    copy(requestId?: string, organizationId?: string, statusCode?: number): B2BOrganizationsDeleteResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace B2BOrganizationsDeleteResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BOrganizationsDeleteResponse;
    }
}
export declare class B2BOrganizationsGetResponse implements StytchAPIResponse {
    constructor(requestId: string, organization: ApiOrganizationV1Organization, statusCode: number);
    get requestId(): string;
    get organization(): ApiOrganizationV1Organization;
    get statusCode(): number;
    copy(requestId?: string, organization?: ApiOrganizationV1Organization, statusCode?: number): B2BOrganizationsGetResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace B2BOrganizationsGetResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BOrganizationsGetResponse;
    }
}
export declare class B2BOrganizationsUpdateRequest {
    constructor(organizationName?: Nullable<string>, organizationSlug?: Nullable<string>, organizationLogoUrl?: Nullable<string>, ssoDefaultConnectionId?: Nullable<string>, ssoJitProvisioning?: Nullable<string>, ssoJitProvisioningAllowedConnections?: Nullable<KtList<string>>, emailAllowedDomains?: Nullable<KtList<string>>, emailJitProvisioning?: Nullable<string>, emailInvites?: Nullable<string>, authMethods?: Nullable<string>, allowedAuthMethods?: Nullable<KtList<string>>, mfaPolicy?: Nullable<string>, rbacEmailImplicitRoleAssignments?: Nullable<KtList<string>>, mfaMethods?: Nullable<string>, allowedMfaMethods?: Nullable<KtList<string>>, oauthTenantJitProvisioning?: Nullable<string>, allowedOauthTenants?: Nullable<KtMap<string, any/* JsonElement */>>, firstPartyConnectedAppsAllowedType?: Nullable<string>, allowedFirstPartyConnectedApps?: Nullable<KtList<string>>, thirdPartyConnectedAppsAllowedType?: Nullable<string>, allowedThirdPartyConnectedApps?: Nullable<KtList<string>>, organizationExternalId?: Nullable<string>);
    get organizationName(): Nullable<string>;
    get organizationSlug(): Nullable<string>;
    get organizationLogoUrl(): Nullable<string>;
    get ssoDefaultConnectionId(): Nullable<string>;
    get ssoJitProvisioning(): Nullable<string>;
    get ssoJitProvisioningAllowedConnections(): Nullable<KtList<string>>;
    get emailAllowedDomains(): Nullable<KtList<string>>;
    get emailJitProvisioning(): Nullable<string>;
    get emailInvites(): Nullable<string>;
    get authMethods(): Nullable<string>;
    get allowedAuthMethods(): Nullable<KtList<string>>;
    get mfaPolicy(): Nullable<string>;
    get rbacEmailImplicitRoleAssignments(): Nullable<KtList<string>>;
    get mfaMethods(): Nullable<string>;
    get allowedMfaMethods(): Nullable<KtList<string>>;
    get oauthTenantJitProvisioning(): Nullable<string>;
    get allowedOauthTenants(): Nullable<KtMap<string, any/* JsonElement */>>;
    get firstPartyConnectedAppsAllowedType(): Nullable<string>;
    get allowedFirstPartyConnectedApps(): Nullable<KtList<string>>;
    get thirdPartyConnectedAppsAllowedType(): Nullable<string>;
    get allowedThirdPartyConnectedApps(): Nullable<KtList<string>>;
    get organizationExternalId(): Nullable<string>;
    copy(organizationName?: Nullable<string>, organizationSlug?: Nullable<string>, organizationLogoUrl?: Nullable<string>, ssoDefaultConnectionId?: Nullable<string>, ssoJitProvisioning?: Nullable<string>, ssoJitProvisioningAllowedConnections?: Nullable<KtList<string>>, emailAllowedDomains?: Nullable<KtList<string>>, emailJitProvisioning?: Nullable<string>, emailInvites?: Nullable<string>, authMethods?: Nullable<string>, allowedAuthMethods?: Nullable<KtList<string>>, mfaPolicy?: Nullable<string>, rbacEmailImplicitRoleAssignments?: Nullable<KtList<string>>, mfaMethods?: Nullable<string>, allowedMfaMethods?: Nullable<KtList<string>>, oauthTenantJitProvisioning?: Nullable<string>, allowedOauthTenants?: Nullable<KtMap<string, any/* JsonElement */>>, firstPartyConnectedAppsAllowedType?: Nullable<string>, allowedFirstPartyConnectedApps?: Nullable<KtList<string>>, thirdPartyConnectedAppsAllowedType?: Nullable<string>, allowedThirdPartyConnectedApps?: Nullable<KtList<string>>, organizationExternalId?: Nullable<string>): B2BOrganizationsUpdateRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace B2BOrganizationsUpdateRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BOrganizationsUpdateRequest;
    }
}
export declare class B2BOrganizationsUpdateResponse implements StytchAPIResponse {
    constructor(requestId: string, organization: ApiOrganizationV1Organization, statusCode: number);
    get requestId(): string;
    get organization(): ApiOrganizationV1Organization;
    get statusCode(): number;
    copy(requestId?: string, organization?: ApiOrganizationV1Organization, statusCode?: number): B2BOrganizationsUpdateResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace B2BOrganizationsUpdateResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BOrganizationsUpdateResponse;
    }
}
export declare class B2BPasswordAuthenticateRequest {
    constructor(organizationId: string, emailAddress: string, password: string, sessionDurationMinutes: number, locale?: Nullable<string>, intermediateSessionToken?: Nullable<string>, dfpTelemetryId?: Nullable<string>, captchaToken?: Nullable<string>);
    get organizationId(): string;
    get emailAddress(): string;
    get password(): string;
    get sessionDurationMinutes(): number;
    get locale(): Nullable<string>;
    get intermediateSessionToken(): Nullable<string>;
    get dfpTelemetryId(): Nullable<string>;
    get captchaToken(): Nullable<string>;
    copy(organizationId?: string, emailAddress?: string, password?: string, sessionDurationMinutes?: number, locale?: Nullable<string>, intermediateSessionToken?: Nullable<string>, dfpTelemetryId?: Nullable<string>, captchaToken?: Nullable<string>): B2BPasswordAuthenticateRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace B2BPasswordAuthenticateRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BPasswordAuthenticateRequest;
    }
}
export declare class B2BPasswordAuthenticateResponse implements StytchAPIResponse/*, AuthenticatedResponse, B2BResponse */ {
    constructor(requestId: string, memberId: string, organizationId: string, member: ApiOrganizationV1Member, sessionToken: string, sessionJwt: string, memberSession: ApiB2bSessionV1MemberSession, organization: ApiOrganizationV1Organization, intermediateSessionToken: string, memberAuthenticated: boolean, mfaRequired: ApiB2bMfaV1MfaRequired, primaryRequired: ApiB2bSessionV1PrimaryRequired, memberDevice: SDKDeviceInfo, statusCode: number);
    get requestId(): string;
    get memberId(): string;
    get organizationId(): string;
    get memberAuthenticated(): boolean;
    get primaryRequired(): ApiB2bSessionV1PrimaryRequired;
    get memberDevice(): SDKDeviceInfo;
    get statusCode(): number;
    copy(requestId?: string, memberId?: string, organizationId?: string, member?: ApiOrganizationV1Member, sessionToken?: string, sessionJwt?: string, memberSession?: ApiB2bSessionV1MemberSession, organization?: ApiOrganizationV1Organization, intermediateSessionToken?: string, memberAuthenticated?: boolean, mfaRequired?: ApiB2bMfaV1MfaRequired, primaryRequired?: ApiB2bSessionV1PrimaryRequired, memberDevice?: SDKDeviceInfo, statusCode?: number): B2BPasswordAuthenticateResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace B2BPasswordAuthenticateResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BPasswordAuthenticateResponse;
    }
}
export declare class B2BPasswordDiscoveryAuthenticateRequest {
    constructor(emailAddress: string, password: string, dfpTelemetryId?: Nullable<string>, captchaToken?: Nullable<string>);
    get emailAddress(): string;
    get password(): string;
    get dfpTelemetryId(): Nullable<string>;
    get captchaToken(): Nullable<string>;
    copy(emailAddress?: string, password?: string, dfpTelemetryId?: Nullable<string>, captchaToken?: Nullable<string>): B2BPasswordDiscoveryAuthenticateRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace B2BPasswordDiscoveryAuthenticateRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BPasswordDiscoveryAuthenticateRequest;
    }
}
export declare class B2BPasswordDiscoveryAuthenticateResponse implements StytchAPIResponse/*, B2BResponse */ {
    constructor(requestId: string, emailAddress: string, intermediateSessionToken: string, discoveredOrganizations: KtList<ApiDiscoveryV1DiscoveredOrganization>, statusCode: number);
    get requestId(): string;
    get emailAddress(): string;
    get discoveredOrganizations(): KtList<ApiDiscoveryV1DiscoveredOrganization>;
    get statusCode(): number;
    copy(requestId?: string, emailAddress?: string, intermediateSessionToken?: string, discoveredOrganizations?: KtList<ApiDiscoveryV1DiscoveredOrganization>, statusCode?: number): B2BPasswordDiscoveryAuthenticateResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace B2BPasswordDiscoveryAuthenticateResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BPasswordDiscoveryAuthenticateResponse;
    }
}
export declare class B2BPasswordEmailResetRequest {
    constructor(passwordResetToken: string, password: string, sessionDurationMinutes: number, codeVerifier?: Nullable<string>, captchaToken?: Nullable<string>, dfpTelemetryId?: Nullable<string>, locale?: Nullable<string>, intermediateSessionToken?: Nullable<string>);
    get passwordResetToken(): string;
    get password(): string;
    get sessionDurationMinutes(): number;
    get codeVerifier(): Nullable<string>;
    get captchaToken(): Nullable<string>;
    get dfpTelemetryId(): Nullable<string>;
    get locale(): Nullable<string>;
    get intermediateSessionToken(): Nullable<string>;
    copy(passwordResetToken?: string, password?: string, sessionDurationMinutes?: number, codeVerifier?: Nullable<string>, captchaToken?: Nullable<string>, dfpTelemetryId?: Nullable<string>, locale?: Nullable<string>, intermediateSessionToken?: Nullable<string>): B2BPasswordEmailResetRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace B2BPasswordEmailResetRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BPasswordEmailResetRequest;
    }
}
export declare class B2BPasswordEmailResetResponse implements StytchAPIResponse/*, AuthenticatedResponse, B2BResponse */ {
    constructor(requestId: string, memberId: string, memberEmailId: string, organizationId: string, member: ApiOrganizationV1Member, sessionToken: string, sessionJwt: string, memberSession: ApiB2bSessionV1MemberSession, organization: ApiOrganizationV1Organization, intermediateSessionToken: string, memberAuthenticated: boolean, mfaRequired: ApiB2bMfaV1MfaRequired, primaryRequired: ApiB2bSessionV1PrimaryRequired, memberDevice: SDKDeviceInfo, statusCode: number);
    get requestId(): string;
    get memberId(): string;
    get memberEmailId(): string;
    get organizationId(): string;
    get memberAuthenticated(): boolean;
    get primaryRequired(): ApiB2bSessionV1PrimaryRequired;
    get memberDevice(): SDKDeviceInfo;
    get statusCode(): number;
    copy(requestId?: string, memberId?: string, memberEmailId?: string, organizationId?: string, member?: ApiOrganizationV1Member, sessionToken?: string, sessionJwt?: string, memberSession?: ApiB2bSessionV1MemberSession, organization?: ApiOrganizationV1Organization, intermediateSessionToken?: string, memberAuthenticated?: boolean, mfaRequired?: ApiB2bMfaV1MfaRequired, primaryRequired?: ApiB2bSessionV1PrimaryRequired, memberDevice?: SDKDeviceInfo, statusCode?: number): B2BPasswordEmailResetResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace B2BPasswordEmailResetResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BPasswordEmailResetResponse;
    }
}
export declare class B2BPasswordEmailResetStartRequest {
    constructor(organizationId: string, emailAddress: string, loginRedirectUrl?: Nullable<string>, resetPasswordRedirectUrl?: Nullable<string>, resetPasswordExpirationMinutes?: Nullable<number>, resetPasswordTemplateId?: Nullable<string>, codeChallenge?: Nullable<string>, locale?: Nullable<string>, dfpTelemetryId?: Nullable<string>, captchaToken?: Nullable<string>, verifyEmailTemplateId?: Nullable<string>);
    get organizationId(): string;
    get emailAddress(): string;
    get loginRedirectUrl(): Nullable<string>;
    get resetPasswordRedirectUrl(): Nullable<string>;
    get resetPasswordExpirationMinutes(): Nullable<number>;
    get resetPasswordTemplateId(): Nullable<string>;
    get codeChallenge(): Nullable<string>;
    get locale(): Nullable<string>;
    get dfpTelemetryId(): Nullable<string>;
    get captchaToken(): Nullable<string>;
    get verifyEmailTemplateId(): Nullable<string>;
    copy(organizationId?: string, emailAddress?: string, loginRedirectUrl?: Nullable<string>, resetPasswordRedirectUrl?: Nullable<string>, resetPasswordExpirationMinutes?: Nullable<number>, resetPasswordTemplateId?: Nullable<string>, codeChallenge?: Nullable<string>, locale?: Nullable<string>, dfpTelemetryId?: Nullable<string>, captchaToken?: Nullable<string>, verifyEmailTemplateId?: Nullable<string>): B2BPasswordEmailResetStartRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace B2BPasswordEmailResetStartRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BPasswordEmailResetStartRequest;
    }
}
export declare class B2BPasswordEmailResetStartResponse implements StytchAPIResponse {
    constructor(requestId: string, statusCode: number);
    get requestId(): string;
    get statusCode(): number;
    copy(requestId?: string, statusCode?: number): B2BPasswordEmailResetStartResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace B2BPasswordEmailResetStartResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BPasswordEmailResetStartResponse;
    }
}
export declare class B2BPasswordExistingPasswordResetRequest {
    constructor(organizationId: string, emailAddress: string, existingPassword: string, newPassword: string, sessionDurationMinutes: number, locale?: Nullable<string>, captchaToken?: Nullable<string>, dfpTelemetryId?: Nullable<string>);
    get organizationId(): string;
    get emailAddress(): string;
    get existingPassword(): string;
    get newPassword(): string;
    get sessionDurationMinutes(): number;
    get locale(): Nullable<string>;
    get captchaToken(): Nullable<string>;
    get dfpTelemetryId(): Nullable<string>;
    copy(organizationId?: string, emailAddress?: string, existingPassword?: string, newPassword?: string, sessionDurationMinutes?: number, locale?: Nullable<string>, captchaToken?: Nullable<string>, dfpTelemetryId?: Nullable<string>): B2BPasswordExistingPasswordResetRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace B2BPasswordExistingPasswordResetRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BPasswordExistingPasswordResetRequest;
    }
}
export declare class B2BPasswordExistingPasswordResetResponse implements StytchAPIResponse/*, AuthenticatedResponse, B2BResponse */ {
    constructor(requestId: string, memberId: string, memberSession: ApiB2bSessionV1MemberSession, member: ApiOrganizationV1Member, sessionToken: string, sessionJwt: string, organization: ApiOrganizationV1Organization, intermediateSessionToken: string, memberAuthenticated: boolean, mfaRequired: ApiB2bMfaV1MfaRequired, primaryRequired: ApiB2bSessionV1PrimaryRequired, memberDevice: SDKDeviceInfo, statusCode: number);
    get requestId(): string;
    get memberId(): string;
    get memberAuthenticated(): boolean;
    get primaryRequired(): ApiB2bSessionV1PrimaryRequired;
    get memberDevice(): SDKDeviceInfo;
    get statusCode(): number;
    copy(requestId?: string, memberId?: string, memberSession?: ApiB2bSessionV1MemberSession, member?: ApiOrganizationV1Member, sessionToken?: string, sessionJwt?: string, organization?: ApiOrganizationV1Organization, intermediateSessionToken?: string, memberAuthenticated?: boolean, mfaRequired?: ApiB2bMfaV1MfaRequired, primaryRequired?: ApiB2bSessionV1PrimaryRequired, memberDevice?: SDKDeviceInfo, statusCode?: number): B2BPasswordExistingPasswordResetResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace B2BPasswordExistingPasswordResetResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BPasswordExistingPasswordResetResponse;
    }
}
export declare class B2BPasswordSessionResetRequest {
    constructor(password: string, dfpTelemetryId?: Nullable<string>, captchaToken?: Nullable<string>);
    get password(): string;
    get dfpTelemetryId(): Nullable<string>;
    get captchaToken(): Nullable<string>;
    copy(password?: string, dfpTelemetryId?: Nullable<string>, captchaToken?: Nullable<string>): B2BPasswordSessionResetRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace B2BPasswordSessionResetRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BPasswordSessionResetRequest;
    }
}
export declare class B2BPasswordSessionResetResponse implements StytchAPIResponse/*, AuthenticatedResponse, B2BResponse */ {
    constructor(requestId: string, memberId: string, memberSession: ApiB2bSessionV1MemberSession, member: ApiOrganizationV1Member, organization: ApiOrganizationV1Organization, sessionToken: string, sessionJwt: string, intermediateSessionToken: string, memberAuthenticated: boolean, mfaRequired: ApiB2bMfaV1MfaRequired, memberDevice: SDKDeviceInfo, statusCode: number);
    get requestId(): string;
    get memberId(): string;
    get memberAuthenticated(): boolean;
    get memberDevice(): SDKDeviceInfo;
    get statusCode(): number;
    copy(requestId?: string, memberId?: string, memberSession?: ApiB2bSessionV1MemberSession, member?: ApiOrganizationV1Member, organization?: ApiOrganizationV1Organization, sessionToken?: string, sessionJwt?: string, intermediateSessionToken?: string, memberAuthenticated?: boolean, mfaRequired?: ApiB2bMfaV1MfaRequired, memberDevice?: SDKDeviceInfo, statusCode?: number): B2BPasswordSessionResetResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace B2BPasswordSessionResetResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BPasswordSessionResetResponse;
    }
}
export declare class B2BPasswordStrengthCheckRequest {
    constructor(password: string, emailAddress?: Nullable<string>);
    get password(): string;
    get emailAddress(): Nullable<string>;
    copy(password?: string, emailAddress?: Nullable<string>): B2BPasswordStrengthCheckRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace B2BPasswordStrengthCheckRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BPasswordStrengthCheckRequest;
    }
}
export declare class B2BPasswordStrengthCheckResponse implements StytchAPIResponse {
    constructor(requestId: string, validPassword: boolean, score: number, breachedPassword: boolean, strengthPolicy: string, breachDetectionOnCreate: boolean, ludsFeedback: ApiB2bPasswordV1LudsFeedback, zxcvbnFeedback: ApiB2bPasswordV1ZxcvbnFeedback, statusCode: number);
    get requestId(): string;
    get validPassword(): boolean;
    get score(): number;
    get breachedPassword(): boolean;
    get strengthPolicy(): string;
    get breachDetectionOnCreate(): boolean;
    get ludsFeedback(): ApiB2bPasswordV1LudsFeedback;
    get zxcvbnFeedback(): ApiB2bPasswordV1ZxcvbnFeedback;
    get statusCode(): number;
    copy(requestId?: string, validPassword?: boolean, score?: number, breachedPassword?: boolean, strengthPolicy?: string, breachDetectionOnCreate?: boolean, ludsFeedback?: ApiB2bPasswordV1LudsFeedback, zxcvbnFeedback?: ApiB2bPasswordV1ZxcvbnFeedback, statusCode?: number): B2BPasswordStrengthCheckResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace B2BPasswordStrengthCheckResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BPasswordStrengthCheckResponse;
    }
}
export declare class B2BRecoveryCodesGetResponse implements StytchAPIResponse {
    constructor(requestId: string, memberId: string, member: ApiOrganizationV1Member, organization: ApiOrganizationV1Organization, recoveryCodes: KtList<string>, statusCode: number);
    get requestId(): string;
    get memberId(): string;
    get member(): ApiOrganizationV1Member;
    get organization(): ApiOrganizationV1Organization;
    get recoveryCodes(): KtList<string>;
    get statusCode(): number;
    copy(requestId?: string, memberId?: string, member?: ApiOrganizationV1Member, organization?: ApiOrganizationV1Organization, recoveryCodes?: KtList<string>, statusCode?: number): B2BRecoveryCodesGetResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace B2BRecoveryCodesGetResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BRecoveryCodesGetResponse;
    }
}
export declare class B2BRecoveryCodesRecoverRequest {
    constructor(organizationId: string, memberId: string, recoveryCode: string, sessionDurationMinutes: number, intermediateSessionToken?: Nullable<string>, dfpTelemetryId?: Nullable<string>, captchaToken?: Nullable<string>);
    get organizationId(): string;
    get memberId(): string;
    get recoveryCode(): string;
    get sessionDurationMinutes(): number;
    get intermediateSessionToken(): Nullable<string>;
    get dfpTelemetryId(): Nullable<string>;
    get captchaToken(): Nullable<string>;
    copy(organizationId?: string, memberId?: string, recoveryCode?: string, sessionDurationMinutes?: number, intermediateSessionToken?: Nullable<string>, dfpTelemetryId?: Nullable<string>, captchaToken?: Nullable<string>): B2BRecoveryCodesRecoverRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace B2BRecoveryCodesRecoverRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BRecoveryCodesRecoverRequest;
    }
}
export declare class B2BRecoveryCodesRecoverResponse implements StytchAPIResponse/*, AuthenticatedResponse */ {
    constructor(requestId: string, memberId: string, member: ApiOrganizationV1Member, organization: ApiOrganizationV1Organization, sessionToken: string, sessionJwt: string, recoveryCodesRemaining: number, memberSession: ApiB2bSessionV1MemberSession, memberDevice: SDKDeviceInfo, statusCode: number);
    get requestId(): string;
    get memberId(): string;
    get recoveryCodesRemaining(): number;
    get memberDevice(): SDKDeviceInfo;
    get statusCode(): number;
    copy(requestId?: string, memberId?: string, member?: ApiOrganizationV1Member, organization?: ApiOrganizationV1Organization, sessionToken?: string, sessionJwt?: string, recoveryCodesRemaining?: number, memberSession?: ApiB2bSessionV1MemberSession, memberDevice?: SDKDeviceInfo, statusCode?: number): B2BRecoveryCodesRecoverResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace B2BRecoveryCodesRecoverResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BRecoveryCodesRecoverResponse;
    }
}
export declare class B2BRecoveryCodesRotateRequest {
    constructor(dfpTelemetryId?: Nullable<string>, captchaToken?: Nullable<string>);
    get dfpTelemetryId(): Nullable<string>;
    get captchaToken(): Nullable<string>;
    copy(dfpTelemetryId?: Nullable<string>, captchaToken?: Nullable<string>): B2BRecoveryCodesRotateRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace B2BRecoveryCodesRotateRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BRecoveryCodesRotateRequest;
    }
}
export declare class B2BRecoveryCodesRotateResponse implements StytchAPIResponse {
    constructor(requestId: string, memberId: string, member: ApiOrganizationV1Member, organization: ApiOrganizationV1Organization, recoveryCodes: KtList<string>, statusCode: number);
    get requestId(): string;
    get memberId(): string;
    get member(): ApiOrganizationV1Member;
    get organization(): ApiOrganizationV1Organization;
    get recoveryCodes(): KtList<string>;
    get statusCode(): number;
    copy(requestId?: string, memberId?: string, member?: ApiOrganizationV1Member, organization?: ApiOrganizationV1Organization, recoveryCodes?: KtList<string>, statusCode?: number): B2BRecoveryCodesRotateResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace B2BRecoveryCodesRotateResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BRecoveryCodesRotateResponse;
    }
}
export declare class B2BSCIMConfig {
    constructor(scimEnabled: boolean);
    get scimEnabled(): boolean;
    copy(scimEnabled?: boolean): B2BSCIMConfig;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace B2BSCIMConfig {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BSCIMConfig;
    }
}
export declare class B2BSCIMCreateConnectionRequest {
    constructor(displayName: string, identityProvider?: Nullable<string>);
    get displayName(): string;
    get identityProvider(): Nullable<string>;
    copy(displayName?: string, identityProvider?: Nullable<string>): B2BSCIMCreateConnectionRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace B2BSCIMCreateConnectionRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BSCIMCreateConnectionRequest;
    }
}
export declare class B2BSCIMCreateConnectionResponse implements StytchAPIResponse {
    constructor(requestId: string, connection: ApiB2bScimV1SCIMConnectionWithToken, statusCode: number);
    get requestId(): string;
    get connection(): ApiB2bScimV1SCIMConnectionWithToken;
    get statusCode(): number;
    copy(requestId?: string, connection?: ApiB2bScimV1SCIMConnectionWithToken, statusCode?: number): B2BSCIMCreateConnectionResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace B2BSCIMCreateConnectionResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BSCIMCreateConnectionResponse;
    }
}
export declare class B2BSCIMDeleteConnectionResponse implements StytchAPIResponse {
    constructor(requestId: string, connectionId: string, statusCode: number);
    get requestId(): string;
    get connectionId(): string;
    get statusCode(): number;
    copy(requestId?: string, connectionId?: string, statusCode?: number): B2BSCIMDeleteConnectionResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace B2BSCIMDeleteConnectionResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BSCIMDeleteConnectionResponse;
    }
}
export declare class B2BSCIMUpdateConnectionRequest {
    constructor(identityProvider?: Nullable<string>, displayName?: Nullable<string>, scimGroupImplicitRoleAssignments?: Nullable<KtList<string>>);
    get identityProvider(): Nullable<string>;
    get displayName(): Nullable<string>;
    get scimGroupImplicitRoleAssignments(): Nullable<KtList<string>>;
    copy(identityProvider?: Nullable<string>, displayName?: Nullable<string>, scimGroupImplicitRoleAssignments?: Nullable<KtList<string>>): B2BSCIMUpdateConnectionRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace B2BSCIMUpdateConnectionRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BSCIMUpdateConnectionRequest;
    }
}
export declare class B2BSCIMUpdateConnectionResponse implements StytchAPIResponse {
    constructor(requestId: string, connection: ApiB2bScimV1SCIMConnection, statusCode: number);
    get requestId(): string;
    get connection(): ApiB2bScimV1SCIMConnection;
    get statusCode(): number;
    copy(requestId?: string, connection?: ApiB2bScimV1SCIMConnection, statusCode?: number): B2BSCIMUpdateConnectionResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace B2BSCIMUpdateConnectionResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BSCIMUpdateConnectionResponse;
    }
}
export declare class B2BSSOAuthEnticateRequest {
    constructor(ssoToken: string, sessionDurationMinutes: number, pkceCodeVerifier?: Nullable<string>, locale?: Nullable<string>, dfpTelemetryId?: Nullable<string>, captchaToken?: Nullable<string>, intermediateSessionToken?: Nullable<string>);
    get ssoToken(): string;
    get sessionDurationMinutes(): number;
    get pkceCodeVerifier(): Nullable<string>;
    get locale(): Nullable<string>;
    get dfpTelemetryId(): Nullable<string>;
    get captchaToken(): Nullable<string>;
    get intermediateSessionToken(): Nullable<string>;
    copy(ssoToken?: string, sessionDurationMinutes?: number, pkceCodeVerifier?: Nullable<string>, locale?: Nullable<string>, dfpTelemetryId?: Nullable<string>, captchaToken?: Nullable<string>, intermediateSessionToken?: Nullable<string>): B2BSSOAuthEnticateRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace B2BSSOAuthEnticateRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BSSOAuthEnticateRequest;
    }
}
export declare class B2BSSOAuthEnticateResponse implements StytchAPIResponse/*, AuthenticatedResponse, B2BResponse */ {
    constructor(requestId: string, memberId: string, organizationId: string, member: ApiOrganizationV1Member, sessionToken: string, sessionJwt: string, memberSession: ApiB2bSessionV1MemberSession, resetSession: boolean, organization: ApiOrganizationV1Organization, intermediateSessionToken: string, memberAuthenticated: boolean, mfaRequired: ApiB2bMfaV1MfaRequired, primaryRequired: ApiB2bSessionV1PrimaryRequired, memberDevice: SDKDeviceInfo, statusCode: number);
    get requestId(): string;
    get memberId(): string;
    get organizationId(): string;
    get resetSession(): boolean;
    get memberAuthenticated(): boolean;
    get primaryRequired(): ApiB2bSessionV1PrimaryRequired;
    get memberDevice(): SDKDeviceInfo;
    get statusCode(): number;
    copy(requestId?: string, memberId?: string, organizationId?: string, member?: ApiOrganizationV1Member, sessionToken?: string, sessionJwt?: string, memberSession?: ApiB2bSessionV1MemberSession, resetSession?: boolean, organization?: ApiOrganizationV1Organization, intermediateSessionToken?: string, memberAuthenticated?: boolean, mfaRequired?: ApiB2bMfaV1MfaRequired, primaryRequired?: ApiB2bSessionV1PrimaryRequired, memberDevice?: SDKDeviceInfo, statusCode?: number): B2BSSOAuthEnticateResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace B2BSSOAuthEnticateResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BSSOAuthEnticateResponse;
    }
}
export declare class B2BSSOConfig {
    constructor(ssoEnabled: boolean, canCreateSamlConnection: boolean, canCreateOidcConnection: boolean);
    get ssoEnabled(): boolean;
    get canCreateSamlConnection(): boolean;
    get canCreateOidcConnection(): boolean;
    copy(ssoEnabled?: boolean, canCreateSamlConnection?: boolean, canCreateOidcConnection?: boolean): B2BSSOConfig;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace B2BSSOConfig {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BSSOConfig;
    }
}
export declare class B2BSSODiscoveryConnectionsResponse implements StytchAPIResponse {
    constructor(requestId: string, connections: KtList<SSOConnection>, statusCode: number);
    get requestId(): string;
    get connections(): KtList<SSOConnection>;
    get statusCode(): number;
    copy(requestId?: string, connections?: KtList<SSOConnection>, statusCode?: number): B2BSSODiscoveryConnectionsResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace B2BSSODiscoveryConnectionsResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BSSODiscoveryConnectionsResponse;
    }
}
export declare class B2BSessionsAccessTokenExchangeRequest {
    constructor(accessToken: string, sessionDurationMinutes: number);
    get accessToken(): string;
    get sessionDurationMinutes(): number;
    copy(accessToken?: string, sessionDurationMinutes?: number): B2BSessionsAccessTokenExchangeRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace B2BSessionsAccessTokenExchangeRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BSessionsAccessTokenExchangeRequest;
    }
}
export declare class B2BSessionsAccessTokenExchangeResponse implements StytchAPIResponse/*, AuthenticatedResponse */ {
    constructor(requestId: string, memberId: string, memberSession: ApiB2bSessionV1MemberSession, sessionToken: string, sessionJwt: string, member: ApiOrganizationV1Member, organization: ApiOrganizationV1Organization, statusCode: number);
    get requestId(): string;
    get memberId(): string;
    get statusCode(): number;
    copy(requestId?: string, memberId?: string, memberSession?: ApiB2bSessionV1MemberSession, sessionToken?: string, sessionJwt?: string, member?: ApiOrganizationV1Member, organization?: ApiOrganizationV1Organization, statusCode?: number): B2BSessionsAccessTokenExchangeResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace B2BSessionsAccessTokenExchangeResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BSessionsAccessTokenExchangeResponse;
    }
}
export declare class B2BSessionsAttestRequest {
    constructor(profileId: string, token: string, organizationId?: Nullable<string>, sessionDurationMinutes?: Nullable<number>);
    get profileId(): string;
    get token(): string;
    get organizationId(): Nullable<string>;
    get sessionDurationMinutes(): Nullable<number>;
    copy(profileId?: string, token?: string, organizationId?: Nullable<string>, sessionDurationMinutes?: Nullable<number>): B2BSessionsAttestRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace B2BSessionsAttestRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BSessionsAttestRequest;
    }
}
export declare class B2BSessionsAttestResponse implements StytchAPIResponse/*, AuthenticatedResponse */ {
    constructor(requestId: string, memberId: string, memberSession: ApiB2bSessionV1MemberSession, sessionToken: string, sessionJwt: string, member: ApiOrganizationV1Member, organization: ApiOrganizationV1Organization, statusCode: number);
    get requestId(): string;
    get memberId(): string;
    get statusCode(): number;
    copy(requestId?: string, memberId?: string, memberSession?: ApiB2bSessionV1MemberSession, sessionToken?: string, sessionJwt?: string, member?: ApiOrganizationV1Member, organization?: ApiOrganizationV1Organization, statusCode?: number): B2BSessionsAttestResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace B2BSessionsAttestResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BSessionsAttestResponse;
    }
}
export declare class B2BSessionsAuthenticateRequest {
    constructor(sessionDurationMinutes?: Nullable<number>);
    get sessionDurationMinutes(): Nullable<number>;
    copy(sessionDurationMinutes?: Nullable<number>): B2BSessionsAuthenticateRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace B2BSessionsAuthenticateRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BSessionsAuthenticateRequest;
    }
}
export declare class B2BSessionsAuthenticateResponse implements StytchAPIResponse/*, AuthenticatedResponse */ {
    constructor(requestId: string, memberSession: ApiB2bSessionV1MemberSession, sessionToken: string, sessionJwt: string, member: ApiOrganizationV1Member, organization: ApiOrganizationV1Organization, verdict: ApiB2bSessionV1AuthorizationVerdict, statusCode: number);
    get requestId(): string;
    get verdict(): ApiB2bSessionV1AuthorizationVerdict;
    get statusCode(): number;
    copy(requestId?: string, memberSession?: ApiB2bSessionV1MemberSession, sessionToken?: string, sessionJwt?: string, member?: ApiOrganizationV1Member, organization?: ApiOrganizationV1Organization, verdict?: ApiB2bSessionV1AuthorizationVerdict, statusCode?: number): B2BSessionsAuthenticateResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace B2BSessionsAuthenticateResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BSessionsAuthenticateResponse;
    }
}
export declare class B2BSessionsExchangeRequest {
    constructor(organizationId: string, sessionDurationMinutes: number, locale?: Nullable<string>);
    get organizationId(): string;
    get sessionDurationMinutes(): number;
    get locale(): Nullable<string>;
    copy(organizationId?: string, sessionDurationMinutes?: number, locale?: Nullable<string>): B2BSessionsExchangeRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace B2BSessionsExchangeRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BSessionsExchangeRequest;
    }
}
export declare class B2BSessionsExchangeResponse implements StytchAPIResponse/*, AuthenticatedResponse, B2BResponse */ {
    constructor(requestId: string, memberId: string, memberSession: ApiB2bSessionV1MemberSession, sessionToken: string, sessionJwt: string, member: ApiOrganizationV1Member, organization: ApiOrganizationV1Organization, memberAuthenticated: boolean, intermediateSessionToken: string, mfaRequired: ApiB2bMfaV1MfaRequired, primaryRequired: ApiB2bSessionV1PrimaryRequired, statusCode: number);
    get requestId(): string;
    get memberId(): string;
    get memberAuthenticated(): boolean;
    get primaryRequired(): ApiB2bSessionV1PrimaryRequired;
    get statusCode(): number;
    copy(requestId?: string, memberId?: string, memberSession?: ApiB2bSessionV1MemberSession, sessionToken?: string, sessionJwt?: string, member?: ApiOrganizationV1Member, organization?: ApiOrganizationV1Organization, memberAuthenticated?: boolean, intermediateSessionToken?: string, mfaRequired?: ApiB2bMfaV1MfaRequired, primaryRequired?: ApiB2bSessionV1PrimaryRequired, statusCode?: number): B2BSessionsExchangeResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace B2BSessionsExchangeResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BSessionsExchangeResponse;
    }
}
export declare class B2BSessionsRevokeByMemberIDResponse implements StytchAPIResponse {
    constructor(requestId: string, statusCode: number);
    get requestId(): string;
    get statusCode(): number;
    copy(requestId?: string, statusCode?: number): B2BSessionsRevokeByMemberIDResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace B2BSessionsRevokeByMemberIDResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BSessionsRevokeByMemberIDResponse;
    }
}
export declare class B2BSessionsRevokeResponse implements StytchAPIResponse {
    constructor(requestId: string, statusCode: number);
    get requestId(): string;
    get statusCode(): number;
    copy(requestId?: string, statusCode?: number): B2BSessionsRevokeResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace B2BSessionsRevokeResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BSessionsRevokeResponse;
    }
}
export declare class B2BTOTPsAuthenticateRequest {
    constructor(organizationId: string, memberId: string, code: string, sessionDurationMinutes: number, intermediateSessionToken?: Nullable<string>, setMfaEnrollment?: Nullable<string>, setDefaultMfa?: Nullable<boolean>, dfpTelemetryId?: Nullable<string>, captchaToken?: Nullable<string>);
    get organizationId(): string;
    get memberId(): string;
    get code(): string;
    get sessionDurationMinutes(): number;
    get intermediateSessionToken(): Nullable<string>;
    get setMfaEnrollment(): Nullable<string>;
    get setDefaultMfa(): Nullable<boolean>;
    get dfpTelemetryId(): Nullable<string>;
    get captchaToken(): Nullable<string>;
    copy(organizationId?: string, memberId?: string, code?: string, sessionDurationMinutes?: number, intermediateSessionToken?: Nullable<string>, setMfaEnrollment?: Nullable<string>, setDefaultMfa?: Nullable<boolean>, dfpTelemetryId?: Nullable<string>, captchaToken?: Nullable<string>): B2BTOTPsAuthenticateRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace B2BTOTPsAuthenticateRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BTOTPsAuthenticateRequest;
    }
}
export declare class B2BTOTPsAuthenticateResponse implements StytchAPIResponse/*, AuthenticatedResponse */ {
    constructor(requestId: string, memberId: string, member: ApiOrganizationV1Member, organization: ApiOrganizationV1Organization, sessionToken: string, sessionJwt: string, memberSession: ApiB2bSessionV1MemberSession, memberDevice: SDKDeviceInfo, statusCode: number);
    get requestId(): string;
    get memberId(): string;
    get memberDevice(): SDKDeviceInfo;
    get statusCode(): number;
    copy(requestId?: string, memberId?: string, member?: ApiOrganizationV1Member, organization?: ApiOrganizationV1Organization, sessionToken?: string, sessionJwt?: string, memberSession?: ApiB2bSessionV1MemberSession, memberDevice?: SDKDeviceInfo, statusCode?: number): B2BTOTPsAuthenticateResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace B2BTOTPsAuthenticateResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BTOTPsAuthenticateResponse;
    }
}
export declare class B2BTOTPsCreateRequest {
    constructor(organizationId: string, memberId: string, expirationMinutes?: Nullable<number>, dfpTelemetryId?: Nullable<string>, captchaToken?: Nullable<string>, intermediateSessionToken?: Nullable<string>);
    get organizationId(): string;
    get memberId(): string;
    get expirationMinutes(): Nullable<number>;
    get dfpTelemetryId(): Nullable<string>;
    get captchaToken(): Nullable<string>;
    get intermediateSessionToken(): Nullable<string>;
    copy(organizationId?: string, memberId?: string, expirationMinutes?: Nullable<number>, dfpTelemetryId?: Nullable<string>, captchaToken?: Nullable<string>, intermediateSessionToken?: Nullable<string>): B2BTOTPsCreateRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace B2BTOTPsCreateRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BTOTPsCreateRequest;
    }
}
export declare class B2BTOTPsCreateResponse implements StytchAPIResponse {
    constructor(requestId: string, memberId: string, totpRegistrationId: string, secret: string, qrCode: string, recoveryCodes: KtList<string>, member: ApiOrganizationV1Member, organization: ApiOrganizationV1Organization, statusCode: number);
    get requestId(): string;
    get memberId(): string;
    get totpRegistrationId(): string;
    get secret(): string;
    get qrCode(): string;
    get recoveryCodes(): KtList<string>;
    get member(): ApiOrganizationV1Member;
    get organization(): ApiOrganizationV1Organization;
    get statusCode(): number;
    copy(requestId?: string, memberId?: string, totpRegistrationId?: string, secret?: string, qrCode?: string, recoveryCodes?: KtList<string>, member?: ApiOrganizationV1Member, organization?: ApiOrganizationV1Organization, statusCode?: number): B2BTOTPsCreateResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace B2BTOTPsCreateResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BTOTPsCreateResponse;
    }
}
export declare class B2BUpdateExternalConnectionRequest {
    constructor(displayName: string, externalConnectionImplicitRoleAssignments?: Nullable<KtList<string>>, externalGroupImplicitRoleAssignments?: Nullable<KtList<string>>);
    get displayName(): string;
    get externalConnectionImplicitRoleAssignments(): Nullable<KtList<string>>;
    get externalGroupImplicitRoleAssignments(): Nullable<KtList<string>>;
    copy(displayName?: string, externalConnectionImplicitRoleAssignments?: Nullable<KtList<string>>, externalGroupImplicitRoleAssignments?: Nullable<KtList<string>>): B2BUpdateExternalConnectionRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace B2BUpdateExternalConnectionRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BUpdateExternalConnectionRequest;
    }
}
export declare class B2BUpdateExternalConnectionResponse implements StytchAPIResponse {
    constructor(requestId: string, connection: ApiSsoV1ExternalConnection, statusCode: number);
    get requestId(): string;
    get connection(): ApiSsoV1ExternalConnection;
    get statusCode(): number;
    copy(requestId?: string, connection?: ApiSsoV1ExternalConnection, statusCode?: number): B2BUpdateExternalConnectionResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace B2BUpdateExternalConnectionResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BUpdateExternalConnectionResponse;
    }
}
export declare class B2BUpdateOIDCConnectionRequest {
    constructor(displayName: string, clientId: string, clientSecret: string, issuer: string, authorizationUrl: string, tokenUrl: string, jwksUrl: string, customScopes: string, userinfoUrl?: Nullable<string>, identityProvider?: Nullable<string>, attributeMapping?: Nullable<KtMap<string, any/* JsonElement */>>);
    get displayName(): string;
    get clientId(): string;
    get clientSecret(): string;
    get issuer(): string;
    get authorizationUrl(): string;
    get tokenUrl(): string;
    get jwksUrl(): string;
    get customScopes(): string;
    get userinfoUrl(): Nullable<string>;
    get identityProvider(): Nullable<string>;
    get attributeMapping(): Nullable<KtMap<string, any/* JsonElement */>>;
    copy(displayName?: string, clientId?: string, clientSecret?: string, issuer?: string, authorizationUrl?: string, tokenUrl?: string, jwksUrl?: string, customScopes?: string, userinfoUrl?: Nullable<string>, identityProvider?: Nullable<string>, attributeMapping?: Nullable<KtMap<string, any/* JsonElement */>>): B2BUpdateOIDCConnectionRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace B2BUpdateOIDCConnectionRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BUpdateOIDCConnectionRequest;
    }
}
export declare class B2BUpdateOIDCConnectionResponse implements StytchAPIResponse {
    constructor(requestId: string, connection: ApiSsoV1OIDCConnection, statusCode: number, warning?: Nullable<string>);
    get requestId(): string;
    get connection(): ApiSsoV1OIDCConnection;
    get statusCode(): number;
    get warning(): Nullable<string>;
    copy(requestId?: string, connection?: ApiSsoV1OIDCConnection, statusCode?: number, warning?: Nullable<string>): B2BUpdateOIDCConnectionResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace B2BUpdateOIDCConnectionResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BUpdateOIDCConnectionResponse;
    }
}
export declare class B2BUpdateSAMLConnectionByURLRequest {
    constructor(metadataUrl: string);
    get metadataUrl(): string;
    copy(metadataUrl?: string): B2BUpdateSAMLConnectionByURLRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace B2BUpdateSAMLConnectionByURLRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BUpdateSAMLConnectionByURLRequest;
    }
}
export declare class B2BUpdateSAMLConnectionByURLResponse implements StytchAPIResponse {
    constructor(requestId: string, connection: ApiSsoV1SAMLConnection, statusCode: number);
    get requestId(): string;
    get connection(): ApiSsoV1SAMLConnection;
    get statusCode(): number;
    copy(requestId?: string, connection?: ApiSsoV1SAMLConnection, statusCode?: number): B2BUpdateSAMLConnectionByURLResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace B2BUpdateSAMLConnectionByURLResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BUpdateSAMLConnectionByURLResponse;
    }
}
export declare class B2BUpdateSAMLConnectionRequest {
    constructor(idpEntityId: string, displayName: string, x509Certificate: string, idpSsoUrl: string, attributeMapping?: Nullable<KtMap<string, any/* JsonElement */>>, samlConnectionImplicitRoleAssignments?: Nullable<KtList<string>>, samlGroupImplicitRoleAssignments?: Nullable<KtList<string>>, alternativeAudienceUri?: Nullable<string>, identityProvider?: Nullable<string>, signingPrivateKey?: Nullable<string>, allowGatewayCallback?: Nullable<boolean>);
    get idpEntityId(): string;
    get displayName(): string;
    get x509Certificate(): string;
    get idpSsoUrl(): string;
    get attributeMapping(): Nullable<KtMap<string, any/* JsonElement */>>;
    get samlConnectionImplicitRoleAssignments(): Nullable<KtList<string>>;
    get samlGroupImplicitRoleAssignments(): Nullable<KtList<string>>;
    get alternativeAudienceUri(): Nullable<string>;
    get identityProvider(): Nullable<string>;
    get signingPrivateKey(): Nullable<string>;
    get allowGatewayCallback(): Nullable<boolean>;
    copy(idpEntityId?: string, displayName?: string, x509Certificate?: string, idpSsoUrl?: string, attributeMapping?: Nullable<KtMap<string, any/* JsonElement */>>, samlConnectionImplicitRoleAssignments?: Nullable<KtList<string>>, samlGroupImplicitRoleAssignments?: Nullable<KtList<string>>, alternativeAudienceUri?: Nullable<string>, identityProvider?: Nullable<string>, signingPrivateKey?: Nullable<string>, allowGatewayCallback?: Nullable<boolean>): B2BUpdateSAMLConnectionRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace B2BUpdateSAMLConnectionRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BUpdateSAMLConnectionRequest;
    }
}
export declare class B2BUpdateSAMLConnectionResponse implements StytchAPIResponse {
    constructor(requestId: string, connection: ApiSsoV1SAMLConnection, statusCode: number);
    get requestId(): string;
    get connection(): ApiSsoV1SAMLConnection;
    get statusCode(): number;
    copy(requestId?: string, connection?: ApiSsoV1SAMLConnection, statusCode?: number): B2BUpdateSAMLConnectionResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace B2BUpdateSAMLConnectionResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BUpdateSAMLConnectionResponse;
    }
}
export declare class BiometricsAuthenticateRequest {
    constructor(biometricRegistrationId: string, signature: string, sessionDurationMinutes: number, dfpTelemetryId?: Nullable<string>, captchaToken?: Nullable<string>);
    get biometricRegistrationId(): string;
    get signature(): string;
    get sessionDurationMinutes(): number;
    get dfpTelemetryId(): Nullable<string>;
    get captchaToken(): Nullable<string>;
    copy(biometricRegistrationId?: string, signature?: string, sessionDurationMinutes?: number, dfpTelemetryId?: Nullable<string>, captchaToken?: Nullable<string>): BiometricsAuthenticateRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace BiometricsAuthenticateRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => BiometricsAuthenticateRequest;
    }
}
export declare class BiometricsAuthenticateResponse implements StytchAPIResponse/*, AuthenticatedResponse */ {
    constructor(requestId: string, userId: string, biometricRegistrationId: string, sessionToken: string, session: ApiSessionV1Session, sessionJwt: string, user: ApiUserV1User, userDevice: SDKDeviceInfo, statusCode: number);
    get requestId(): string;
    get userId(): string;
    get biometricRegistrationId(): string;
    get userDevice(): SDKDeviceInfo;
    get statusCode(): number;
    copy(requestId?: string, userId?: string, biometricRegistrationId?: string, sessionToken?: string, session?: ApiSessionV1Session, sessionJwt?: string, user?: ApiUserV1User, userDevice?: SDKDeviceInfo, statusCode?: number): BiometricsAuthenticateResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace BiometricsAuthenticateResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => BiometricsAuthenticateResponse;
    }
}
export declare class BiometricsAuthenticateStartRequest {
    constructor(publicKey: string);
    get publicKey(): string;
    copy(publicKey?: string): BiometricsAuthenticateStartRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace BiometricsAuthenticateStartRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => BiometricsAuthenticateStartRequest;
    }
}
export declare class BiometricsAuthenticateStartResponse implements StytchAPIResponse {
    constructor(requestId: string, userId: string, biometricRegistrationId: string, challenge: string, statusCode: number);
    get requestId(): string;
    get userId(): string;
    get biometricRegistrationId(): string;
    get challenge(): string;
    get statusCode(): number;
    copy(requestId?: string, userId?: string, biometricRegistrationId?: string, challenge?: string, statusCode?: number): BiometricsAuthenticateStartResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace BiometricsAuthenticateStartResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => BiometricsAuthenticateStartResponse;
    }
}
export declare class BiometricsRegisterRequest {
    constructor(biometricRegistrationId: string, signature: string, sessionDurationMinutes: number);
    get biometricRegistrationId(): string;
    get signature(): string;
    get sessionDurationMinutes(): number;
    copy(biometricRegistrationId?: string, signature?: string, sessionDurationMinutes?: number): BiometricsRegisterRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace BiometricsRegisterRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => BiometricsRegisterRequest;
    }
}
export declare class BiometricsRegisterResponse implements StytchAPIResponse/*, AuthenticatedResponse */ {
    constructor(requestId: string, userId: string, biometricRegistrationId: string, user: ApiUserV1User, sessionToken: string, session: ApiSessionV1Session, sessionJwt: string, statusCode: number);
    get requestId(): string;
    get userId(): string;
    get biometricRegistrationId(): string;
    get statusCode(): number;
    copy(requestId?: string, userId?: string, biometricRegistrationId?: string, user?: ApiUserV1User, sessionToken?: string, session?: ApiSessionV1Session, sessionJwt?: string, statusCode?: number): BiometricsRegisterResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace BiometricsRegisterResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => BiometricsRegisterResponse;
    }
}
export declare class BiometricsRegisterStartRequest {
    constructor(publicKey: string);
    get publicKey(): string;
    copy(publicKey?: string): BiometricsRegisterStartRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace BiometricsRegisterStartRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => BiometricsRegisterStartRequest;
    }
}
export declare class BiometricsRegisterStartResponse implements StytchAPIResponse {
    constructor(requestId: string, userId: string, biometricRegistrationId: string, challenge: string, statusCode: number);
    get requestId(): string;
    get userId(): string;
    get biometricRegistrationId(): string;
    get challenge(): string;
    get statusCode(): number;
    copy(requestId?: string, userId?: string, biometricRegistrationId?: string, challenge?: string, statusCode?: number): BiometricsRegisterStartResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace BiometricsRegisterStartResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => BiometricsRegisterStartResponse;
    }
}
export declare class BootstrapDataCaptcha {
    constructor(enabled: boolean, siteKey?: Nullable<string>, providerType?: Nullable<string>);
    get enabled(): boolean;
    get siteKey(): Nullable<string>;
    get providerType(): Nullable<string>;
    copy(enabled?: boolean, siteKey?: Nullable<string>, providerType?: Nullable<string>): BootstrapDataCaptcha;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace BootstrapDataCaptcha {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => BootstrapDataCaptcha;
    }
}
export declare class BootstrapPasswordConfig {
    constructor(ludsComplexity: number, ludsMinimumCount: number);
    get ludsComplexity(): number;
    get ludsMinimumCount(): number;
    copy(ludsComplexity?: number, ludsMinimumCount?: number): BootstrapPasswordConfig;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace BootstrapPasswordConfig {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => BootstrapPasswordConfig;
    }
}
export declare class BootstrapResponse implements StytchAPIResponse {
    constructor(requestId: string, disableSdkWatermark: boolean, emailDomains: KtList<string>, captchaSettings: BootstrapDataCaptcha, pkceRequiredForEmailMagicLinks: boolean, pkceRequiredForPasswordResets: boolean, pkceRequiredForOauth: boolean, pkceRequiredForSso: boolean, createOrganizationEnabled: boolean, passwordConfig: BootstrapPasswordConfig, dfpProtectedAuthEnabled: boolean, rbacPolicy: ApiB2bRbacV1Policy, siweRequiredForCryptoWallets: boolean, opaqueErrors: boolean, projectName: string, statusCode: number, cnameDomain?: Nullable<string>, slugPattern?: Nullable<string>, dfpProtectedAuthMode?: Nullable<string>, vertical?: Nullable<any>/* Nullable<ApiAttributeV1Vertical> */);
    get requestId(): string;
    get disableSdkWatermark(): boolean;
    get emailDomains(): KtList<string>;
    get captchaSettings(): BootstrapDataCaptcha;
    get pkceRequiredForEmailMagicLinks(): boolean;
    get pkceRequiredForPasswordResets(): boolean;
    get pkceRequiredForOauth(): boolean;
    get pkceRequiredForSso(): boolean;
    get createOrganizationEnabled(): boolean;
    get passwordConfig(): BootstrapPasswordConfig;
    get dfpProtectedAuthEnabled(): boolean;
    get rbacPolicy(): ApiB2bRbacV1Policy;
    get siweRequiredForCryptoWallets(): boolean;
    get opaqueErrors(): boolean;
    get projectName(): string;
    get statusCode(): number;
    get cnameDomain(): Nullable<string>;
    get slugPattern(): Nullable<string>;
    get dfpProtectedAuthMode(): Nullable<string>;
    get vertical(): Nullable<any>/* Nullable<ApiAttributeV1Vertical> */;
    copy(requestId?: string, disableSdkWatermark?: boolean, emailDomains?: KtList<string>, captchaSettings?: BootstrapDataCaptcha, pkceRequiredForEmailMagicLinks?: boolean, pkceRequiredForPasswordResets?: boolean, pkceRequiredForOauth?: boolean, pkceRequiredForSso?: boolean, createOrganizationEnabled?: boolean, passwordConfig?: BootstrapPasswordConfig, dfpProtectedAuthEnabled?: boolean, rbacPolicy?: ApiB2bRbacV1Policy, siweRequiredForCryptoWallets?: boolean, opaqueErrors?: boolean, projectName?: string, statusCode?: number, cnameDomain?: Nullable<string>, slugPattern?: Nullable<string>, dfpProtectedAuthMode?: Nullable<string>, vertical?: Nullable<any>/* Nullable<ApiAttributeV1Vertical> */): BootstrapResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace BootstrapResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => BootstrapResponse;
    }
}
export declare class CryptoWalletsAuthenticateRequest {
    constructor(cryptoWalletAddress: string, cryptoWalletType: string, signature: string, sessionDurationMinutes: number, dfpTelemetryId?: Nullable<string>, captchaToken?: Nullable<string>);
    get cryptoWalletAddress(): string;
    get cryptoWalletType(): string;
    get signature(): string;
    get sessionDurationMinutes(): number;
    get dfpTelemetryId(): Nullable<string>;
    get captchaToken(): Nullable<string>;
    copy(cryptoWalletAddress?: string, cryptoWalletType?: string, signature?: string, sessionDurationMinutes?: number, dfpTelemetryId?: Nullable<string>, captchaToken?: Nullable<string>): CryptoWalletsAuthenticateRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace CryptoWalletsAuthenticateRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => CryptoWalletsAuthenticateRequest;
    }
}
export declare class CryptoWalletsAuthenticateResponse implements StytchAPIResponse/*, AuthenticatedResponse */ {
    constructor(requestId: string, userId: string, sessionToken: string, session: ApiSessionV1Session, sessionJwt: string, user: ApiUserV1User, siweParams: ApiCryptoWalletV1SIWEParamsResponse, userDevice: SDKDeviceInfo, statusCode: number);
    get requestId(): string;
    get userId(): string;
    get siweParams(): ApiCryptoWalletV1SIWEParamsResponse;
    get userDevice(): SDKDeviceInfo;
    get statusCode(): number;
    copy(requestId?: string, userId?: string, sessionToken?: string, session?: ApiSessionV1Session, sessionJwt?: string, user?: ApiUserV1User, siweParams?: ApiCryptoWalletV1SIWEParamsResponse, userDevice?: SDKDeviceInfo, statusCode?: number): CryptoWalletsAuthenticateResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace CryptoWalletsAuthenticateResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => CryptoWalletsAuthenticateResponse;
    }
}
export declare class CryptoWalletsAuthenticateStartPrimaryResponse implements StytchAPIResponse {
    constructor(requestId: string, challenge: string, statusCode: number);
    get requestId(): string;
    get challenge(): string;
    get statusCode(): number;
    copy(requestId?: string, challenge?: string, statusCode?: number): CryptoWalletsAuthenticateStartPrimaryResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace CryptoWalletsAuthenticateStartPrimaryResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => CryptoWalletsAuthenticateStartPrimaryResponse;
    }
}
export declare class CryptoWalletsAuthenticateStartSecondaryRequest {
    constructor(cryptoWalletType: string, cryptoWalletAddress: string, siweParams?: Nullable<SDKSIWEParams>);
    get cryptoWalletType(): string;
    get cryptoWalletAddress(): string;
    get siweParams(): Nullable<SDKSIWEParams>;
    copy(cryptoWalletType?: string, cryptoWalletAddress?: string, siweParams?: Nullable<SDKSIWEParams>): CryptoWalletsAuthenticateStartSecondaryRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace CryptoWalletsAuthenticateStartSecondaryRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => CryptoWalletsAuthenticateStartSecondaryRequest;
    }
}
export declare class CryptoWalletsAuthenticateStartSecondaryResponse implements StytchAPIResponse {
    constructor(requestId: string, userId: string, challenge: string, userCreated: boolean, statusCode: number);
    get requestId(): string;
    get userId(): string;
    get challenge(): string;
    get userCreated(): boolean;
    get statusCode(): number;
    copy(requestId?: string, userId?: string, challenge?: string, userCreated?: boolean, statusCode?: number): CryptoWalletsAuthenticateStartSecondaryResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace CryptoWalletsAuthenticateStartSecondaryResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => CryptoWalletsAuthenticateStartSecondaryResponse;
    }
}
export declare class DeleteBiometricRegistrationResponse implements StytchAPIResponse {
    constructor(requestId: string, userId: string, user: ApiUserV1User, statusCode: number);
    get requestId(): string;
    get userId(): string;
    get user(): ApiUserV1User;
    get statusCode(): number;
    copy(requestId?: string, userId?: string, user?: ApiUserV1User, statusCode?: number): DeleteBiometricRegistrationResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace DeleteBiometricRegistrationResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => DeleteBiometricRegistrationResponse;
    }
}
export declare class DeleteCryptoWalletResponse implements StytchAPIResponse {
    constructor(requestId: string, userId: string, user: ApiUserV1User, statusCode: number);
    get requestId(): string;
    get userId(): string;
    get user(): ApiUserV1User;
    get statusCode(): number;
    copy(requestId?: string, userId?: string, user?: ApiUserV1User, statusCode?: number): DeleteCryptoWalletResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace DeleteCryptoWalletResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => DeleteCryptoWalletResponse;
    }
}
export declare class DeleteEmailResponse implements StytchAPIResponse {
    constructor(requestId: string, userId: string, user: ApiUserV1User, statusCode: number);
    get requestId(): string;
    get userId(): string;
    get user(): ApiUserV1User;
    get statusCode(): number;
    copy(requestId?: string, userId?: string, user?: ApiUserV1User, statusCode?: number): DeleteEmailResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace DeleteEmailResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => DeleteEmailResponse;
    }
}
export declare class DeleteOAuthUserRegistrationResponse implements StytchAPIResponse {
    constructor(requestId: string, userId: string, user: ApiUserV1User, statusCode: number);
    get requestId(): string;
    get userId(): string;
    get user(): ApiUserV1User;
    get statusCode(): number;
    copy(requestId?: string, userId?: string, user?: ApiUserV1User, statusCode?: number): DeleteOAuthUserRegistrationResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace DeleteOAuthUserRegistrationResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => DeleteOAuthUserRegistrationResponse;
    }
}
export declare class DeletePhoneNumberResponse implements StytchAPIResponse {
    constructor(requestId: string, userId: string, user: ApiUserV1User, statusCode: number);
    get requestId(): string;
    get userId(): string;
    get user(): ApiUserV1User;
    get statusCode(): number;
    copy(requestId?: string, userId?: string, user?: ApiUserV1User, statusCode?: number): DeletePhoneNumberResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace DeletePhoneNumberResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => DeletePhoneNumberResponse;
    }
}
export declare class DeleteTOTPResponse implements StytchAPIResponse {
    constructor(requestId: string, userId: string, user: ApiUserV1User, statusCode: number);
    get requestId(): string;
    get userId(): string;
    get user(): ApiUserV1User;
    get statusCode(): number;
    copy(requestId?: string, userId?: string, user?: ApiUserV1User, statusCode?: number): DeleteTOTPResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace DeleteTOTPResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => DeleteTOTPResponse;
    }
}
export declare class DeleteWebAuthnRegistrationResponse implements StytchAPIResponse {
    constructor(requestId: string, userId: string, user: ApiUserV1User, statusCode: number);
    get requestId(): string;
    get userId(): string;
    get user(): ApiUserV1User;
    get statusCode(): number;
    copy(requestId?: string, userId?: string, user?: ApiUserV1User, statusCode?: number): DeleteWebAuthnRegistrationResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace DeleteWebAuthnRegistrationResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => DeleteWebAuthnRegistrationResponse;
    }
}
export declare class ExternalB2BOAuthAuthorizeStartRequest {
    constructor(clientId: string, redirectUri: string, responseType: string, scopes: KtList<string>, prompt?: Nullable<string>);
    get clientId(): string;
    get redirectUri(): string;
    get responseType(): string;
    get scopes(): KtList<string>;
    get prompt(): Nullable<string>;
    copy(clientId?: string, redirectUri?: string, responseType?: string, scopes?: KtList<string>, prompt?: Nullable<string>): ExternalB2BOAuthAuthorizeStartRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ExternalB2BOAuthAuthorizeStartRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ExternalB2BOAuthAuthorizeStartRequest;
    }
}
export declare class ExternalB2BOAuthAuthorizeStartResponse implements StytchAPIResponse {
    constructor(requestId: string, consentRequired: boolean, client: ApiConnectedappsV1ConnectedAppPublic, scopeResults: KtList<ApiB2bIdpV1ScopeResult>, statusCode: number);
    get requestId(): string;
    get consentRequired(): boolean;
    get client(): ApiConnectedappsV1ConnectedAppPublic;
    get scopeResults(): KtList<ApiB2bIdpV1ScopeResult>;
    get statusCode(): number;
    copy(requestId?: string, consentRequired?: boolean, client?: ApiConnectedappsV1ConnectedAppPublic, scopeResults?: KtList<ApiB2bIdpV1ScopeResult>, statusCode?: number): ExternalB2BOAuthAuthorizeStartResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace ExternalB2BOAuthAuthorizeStartResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ExternalB2BOAuthAuthorizeStartResponse;
    }
}
export declare class ExternalB2BOAuthAuthorizeSubmitRequest {
    constructor(clientId: string, redirectUri: string, responseType: string, scopes: KtList<string>, consentGranted: boolean, state?: Nullable<string>, nonce?: Nullable<string>, codeChallenge?: Nullable<string>, prompt?: Nullable<string>);
    get clientId(): string;
    get redirectUri(): string;
    get responseType(): string;
    get scopes(): KtList<string>;
    get consentGranted(): boolean;
    get state(): Nullable<string>;
    get nonce(): Nullable<string>;
    get codeChallenge(): Nullable<string>;
    get prompt(): Nullable<string>;
    copy(clientId?: string, redirectUri?: string, responseType?: string, scopes?: KtList<string>, consentGranted?: boolean, state?: Nullable<string>, nonce?: Nullable<string>, codeChallenge?: Nullable<string>, prompt?: Nullable<string>): ExternalB2BOAuthAuthorizeSubmitRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ExternalB2BOAuthAuthorizeSubmitRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ExternalB2BOAuthAuthorizeSubmitRequest;
    }
}
export declare class ExternalB2BOAuthAuthorizeSubmitResponse implements StytchAPIResponse {
    constructor(requestId: string, redirectUri: string, statusCode: number, authorizationCode?: Nullable<string>);
    get requestId(): string;
    get redirectUri(): string;
    get statusCode(): number;
    get authorizationCode(): Nullable<string>;
    copy(requestId?: string, redirectUri?: string, statusCode?: number, authorizationCode?: Nullable<string>): ExternalB2BOAuthAuthorizeSubmitResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace ExternalB2BOAuthAuthorizeSubmitResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ExternalB2BOAuthAuthorizeSubmitResponse;
    }
}
export declare class ExternalOAuthAuthorizeStartRequest {
    constructor(clientId: string, redirectUri: string, responseType: string, scopes: KtList<string>, prompt?: Nullable<string>);
    get clientId(): string;
    get redirectUri(): string;
    get responseType(): string;
    get scopes(): KtList<string>;
    get prompt(): Nullable<string>;
    copy(clientId?: string, redirectUri?: string, responseType?: string, scopes?: KtList<string>, prompt?: Nullable<string>): ExternalOAuthAuthorizeStartRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ExternalOAuthAuthorizeStartRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ExternalOAuthAuthorizeStartRequest;
    }
}
export declare class ExternalOAuthAuthorizeStartResponse implements StytchAPIResponse {
    constructor(requestId: string, userId: string, user: ApiUserV1User, client: ApiConnectedappsV1ConnectedAppPublic, consentRequired: boolean, scopeResults: KtList<ApiIdpV1ScopeResult>, statusCode: number);
    get requestId(): string;
    get userId(): string;
    get user(): ApiUserV1User;
    get client(): ApiConnectedappsV1ConnectedAppPublic;
    get consentRequired(): boolean;
    get scopeResults(): KtList<ApiIdpV1ScopeResult>;
    get statusCode(): number;
    copy(requestId?: string, userId?: string, user?: ApiUserV1User, client?: ApiConnectedappsV1ConnectedAppPublic, consentRequired?: boolean, scopeResults?: KtList<ApiIdpV1ScopeResult>, statusCode?: number): ExternalOAuthAuthorizeStartResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace ExternalOAuthAuthorizeStartResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ExternalOAuthAuthorizeStartResponse;
    }
}
export declare class ExternalOAuthAuthorizeSubmitRequest {
    constructor(clientId: string, redirectUri: string, responseType: string, scopes: KtList<string>, consentGranted: boolean, state?: Nullable<string>, nonce?: Nullable<string>, codeChallenge?: Nullable<string>, prompt?: Nullable<string>, resources?: Nullable<KtList<string>>);
    get clientId(): string;
    get redirectUri(): string;
    get responseType(): string;
    get scopes(): KtList<string>;
    get consentGranted(): boolean;
    get state(): Nullable<string>;
    get nonce(): Nullable<string>;
    get codeChallenge(): Nullable<string>;
    get prompt(): Nullable<string>;
    get resources(): Nullable<KtList<string>>;
    copy(clientId?: string, redirectUri?: string, responseType?: string, scopes?: KtList<string>, consentGranted?: boolean, state?: Nullable<string>, nonce?: Nullable<string>, codeChallenge?: Nullable<string>, prompt?: Nullable<string>, resources?: Nullable<KtList<string>>): ExternalOAuthAuthorizeSubmitRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ExternalOAuthAuthorizeSubmitRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ExternalOAuthAuthorizeSubmitRequest;
    }
}
export declare class ExternalOAuthAuthorizeSubmitResponse implements StytchAPIResponse {
    constructor(requestId: string, redirectUri: string, statusCode: number, authorizationCode?: Nullable<string>);
    get requestId(): string;
    get redirectUri(): string;
    get statusCode(): number;
    get authorizationCode(): Nullable<string>;
    copy(requestId?: string, redirectUri?: string, statusCode?: number, authorizationCode?: Nullable<string>): ExternalOAuthAuthorizeSubmitResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace ExternalOAuthAuthorizeSubmitResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ExternalOAuthAuthorizeSubmitResponse;
    }
}
export declare class GetConnectedAppsResponse implements StytchAPIResponse {
    constructor(requestId: string, connectedApps: KtList<ApiUserV1UserConnectedApp>, statusCode: number);
    get requestId(): string;
    get connectedApps(): KtList<ApiUserV1UserConnectedApp>;
    get statusCode(): number;
    copy(requestId?: string, connectedApps?: KtList<ApiUserV1UserConnectedApp>, statusCode?: number): GetConnectedAppsResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace GetConnectedAppsResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => GetConnectedAppsResponse;
    }
}
export declare class GetMeResponse implements StytchAPIResponse {
    constructor(requestId: string, userId: string, name: ApiUserV1Name, emails: KtList<ApiUserV1Email>, status: string, phoneNumbers: KtList<ApiUserV1PhoneNumber>, webauthnRegistrations: KtList<ApiUserV1WebAuthnRegistration>, providers: KtList<ApiUserV1OAuthProvider>, totps: KtList<ApiUserV1TOTP>, cryptoWallets: KtList<ApiUserV1CryptoWallet>, password: ApiUserV1Password, biometricRegistrations: KtList<ApiUserV1BiometricRegistration>, statusCode: number, createdAt?: Nullable<any>/* Nullable<Instant> */, trustedMetadata?: Nullable<KtMap<string, any/* JsonElement */>>, untrustedMetadata?: Nullable<KtMap<string, any/* JsonElement */>>);
    get requestId(): string;
    get userId(): string;
    get name(): ApiUserV1Name;
    get emails(): KtList<ApiUserV1Email>;
    get status(): string;
    get phoneNumbers(): KtList<ApiUserV1PhoneNumber>;
    get webauthnRegistrations(): KtList<ApiUserV1WebAuthnRegistration>;
    get providers(): KtList<ApiUserV1OAuthProvider>;
    get totps(): KtList<ApiUserV1TOTP>;
    get cryptoWallets(): KtList<ApiUserV1CryptoWallet>;
    get password(): ApiUserV1Password;
    get biometricRegistrations(): KtList<ApiUserV1BiometricRegistration>;
    get statusCode(): number;
    get createdAt(): Nullable<any>/* Nullable<Instant> */;
    get trustedMetadata(): Nullable<KtMap<string, any/* JsonElement */>>;
    get untrustedMetadata(): Nullable<KtMap<string, any/* JsonElement */>>;
    copy(requestId?: string, userId?: string, name?: ApiUserV1Name, emails?: KtList<ApiUserV1Email>, status?: string, phoneNumbers?: KtList<ApiUserV1PhoneNumber>, webauthnRegistrations?: KtList<ApiUserV1WebAuthnRegistration>, providers?: KtList<ApiUserV1OAuthProvider>, totps?: KtList<ApiUserV1TOTP>, cryptoWallets?: KtList<ApiUserV1CryptoWallet>, password?: ApiUserV1Password, biometricRegistrations?: KtList<ApiUserV1BiometricRegistration>, statusCode?: number, createdAt?: Nullable<any>/* Nullable<Instant> */, trustedMetadata?: Nullable<KtMap<string, any/* JsonElement */>>, untrustedMetadata?: Nullable<KtMap<string, any/* JsonElement */>>): GetMeResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace GetMeResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => GetMeResponse;
    }
}
export declare class ImpersonationTokenAuthenticateRequest {
    constructor(impersonationToken: string, dfpTelemetryId?: Nullable<string>, captchaToken?: Nullable<string>);
    get impersonationToken(): string;
    get dfpTelemetryId(): Nullable<string>;
    get captchaToken(): Nullable<string>;
    copy(impersonationToken?: string, dfpTelemetryId?: Nullable<string>, captchaToken?: Nullable<string>): ImpersonationTokenAuthenticateRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace ImpersonationTokenAuthenticateRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ImpersonationTokenAuthenticateRequest;
    }
}
export declare class ImpersonationTokenAuthenticateResponse implements StytchAPIResponse/*, AuthenticatedResponse */ {
    constructor(requestId: string, userId: string, session: ApiSessionV1Session, sessionToken: string, sessionJwt: string, user: ApiUserV1User, statusCode: number);
    get requestId(): string;
    get userId(): string;
    get statusCode(): number;
    copy(requestId?: string, userId?: string, session?: ApiSessionV1Session, sessionToken?: string, sessionJwt?: string, user?: ApiUserV1User, statusCode?: number): ImpersonationTokenAuthenticateResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace ImpersonationTokenAuthenticateResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => ImpersonationTokenAuthenticateResponse;
    }
}
export declare class MagicLinksAuthenticateRequest {
    constructor(token: string, sessionDurationMinutes: number, codeVerifier?: Nullable<string>);
    get token(): string;
    get sessionDurationMinutes(): number;
    get codeVerifier(): Nullable<string>;
    copy(token?: string, sessionDurationMinutes?: number, codeVerifier?: Nullable<string>): MagicLinksAuthenticateRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace MagicLinksAuthenticateRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => MagicLinksAuthenticateRequest;
    }
}
export declare class MagicLinksAuthenticateResponse implements StytchAPIResponse/*, AuthenticatedResponse */ {
    constructor(requestId: string, userId: string, methodId: string, sessionToken: string, session: ApiSessionV1Session, sessionJwt: string, user: ApiUserV1User, resetSessions: boolean, statusCode: number);
    get requestId(): string;
    get userId(): string;
    get methodId(): string;
    get resetSessions(): boolean;
    get statusCode(): number;
    copy(requestId?: string, userId?: string, methodId?: string, sessionToken?: string, session?: ApiSessionV1Session, sessionJwt?: string, user?: ApiUserV1User, resetSessions?: boolean, statusCode?: number): MagicLinksAuthenticateResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace MagicLinksAuthenticateResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => MagicLinksAuthenticateResponse;
    }
}
export declare class MagicLinksEmailLoginOrCreateRequest {
    constructor(email: string, captchaToken?: Nullable<string>, loginMagicLinkUrl?: Nullable<string>, signupMagicLinkUrl?: Nullable<string>, loginExpirationMinutes?: Nullable<number>, signupExpirationMinutes?: Nullable<number>, codeChallenge?: Nullable<string>, loginTemplateId?: Nullable<string>, signupTemplateId?: Nullable<string>, dfpTelemetryId?: Nullable<string>, locale?: Nullable<string>);
    get email(): string;
    get captchaToken(): Nullable<string>;
    get loginMagicLinkUrl(): Nullable<string>;
    get signupMagicLinkUrl(): Nullable<string>;
    get loginExpirationMinutes(): Nullable<number>;
    get signupExpirationMinutes(): Nullable<number>;
    get codeChallenge(): Nullable<string>;
    get loginTemplateId(): Nullable<string>;
    get signupTemplateId(): Nullable<string>;
    get dfpTelemetryId(): Nullable<string>;
    get locale(): Nullable<string>;
    copy(email?: string, captchaToken?: Nullable<string>, loginMagicLinkUrl?: Nullable<string>, signupMagicLinkUrl?: Nullable<string>, loginExpirationMinutes?: Nullable<number>, signupExpirationMinutes?: Nullable<number>, codeChallenge?: Nullable<string>, loginTemplateId?: Nullable<string>, signupTemplateId?: Nullable<string>, dfpTelemetryId?: Nullable<string>, locale?: Nullable<string>): MagicLinksEmailLoginOrCreateRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace MagicLinksEmailLoginOrCreateRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => MagicLinksEmailLoginOrCreateRequest;
    }
}
export declare class MagicLinksEmailLoginOrCreateResponse implements StytchAPIResponse {
    constructor(requestId: string, statusCode: number);
    get requestId(): string;
    get statusCode(): number;
    copy(requestId?: string, statusCode?: number): MagicLinksEmailLoginOrCreateResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace MagicLinksEmailLoginOrCreateResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => MagicLinksEmailLoginOrCreateResponse;
    }
}
export declare class MagicLinksEmailSendSecondaryRequest {
    constructor(email: string, captchaToken?: Nullable<string>, loginMagicLinkUrl?: Nullable<string>, signupMagicLinkUrl?: Nullable<string>, loginExpirationMinutes?: Nullable<number>, signupExpirationMinutes?: Nullable<number>, codeChallenge?: Nullable<string>, loginTemplateId?: Nullable<string>, signupTemplateId?: Nullable<string>, dfpTelemetryId?: Nullable<string>, locale?: Nullable<string>);
    get email(): string;
    get captchaToken(): Nullable<string>;
    get loginMagicLinkUrl(): Nullable<string>;
    get signupMagicLinkUrl(): Nullable<string>;
    get loginExpirationMinutes(): Nullable<number>;
    get signupExpirationMinutes(): Nullable<number>;
    get codeChallenge(): Nullable<string>;
    get loginTemplateId(): Nullable<string>;
    get signupTemplateId(): Nullable<string>;
    get dfpTelemetryId(): Nullable<string>;
    get locale(): Nullable<string>;
    copy(email?: string, captchaToken?: Nullable<string>, loginMagicLinkUrl?: Nullable<string>, signupMagicLinkUrl?: Nullable<string>, loginExpirationMinutes?: Nullable<number>, signupExpirationMinutes?: Nullable<number>, codeChallenge?: Nullable<string>, loginTemplateId?: Nullable<string>, signupTemplateId?: Nullable<string>, dfpTelemetryId?: Nullable<string>, locale?: Nullable<string>): MagicLinksEmailSendSecondaryRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace MagicLinksEmailSendSecondaryRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => MagicLinksEmailSendSecondaryRequest;
    }
}
export declare class MagicLinksEmailSendSecondaryResponse implements StytchAPIResponse {
    constructor(requestId: string, statusCode: number);
    get requestId(): string;
    get statusCode(): number;
    copy(requestId?: string, statusCode?: number): MagicLinksEmailSendSecondaryResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace MagicLinksEmailSendSecondaryResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => MagicLinksEmailSendSecondaryResponse;
    }
}
export declare class Member {
    constructor(status: string, name: string, memberPasswordId: string, mfaEnrolled: boolean);
    get status(): string;
    get name(): string;
    get memberPasswordId(): string;
    get mfaEnrolled(): boolean;
    copy(status?: string, name?: string, memberPasswordId?: string, mfaEnrolled?: boolean): Member;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace Member {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => Member;
    }
}
export declare class MemberSearchByEmailRequest {
    constructor(organizationId: string, emailAddress: string);
    get organizationId(): string;
    get emailAddress(): string;
    copy(organizationId?: string, emailAddress?: string): MemberSearchByEmailRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace MemberSearchByEmailRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => MemberSearchByEmailRequest;
    }
}
export declare class MemberSearchByEmailResponse implements StytchAPIResponse {
    constructor(requestId: string, member: Member, statusCode: number);
    get requestId(): string;
    get member(): Member;
    get statusCode(): number;
    copy(requestId?: string, member?: Member, statusCode?: number): MemberSearchByEmailResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace MemberSearchByEmailResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => MemberSearchByEmailResponse;
    }
}
export declare class OAuthAppleIDTokenAuthenticateRequest {
    constructor(idToken: string, sessionDurationMinutes: number, name?: Nullable<ApiUserV1Name>, nonce?: Nullable<string>, oauthAttachToken?: Nullable<string>);
    get idToken(): string;
    get sessionDurationMinutes(): number;
    get name(): Nullable<ApiUserV1Name>;
    get nonce(): Nullable<string>;
    get oauthAttachToken(): Nullable<string>;
    copy(idToken?: string, sessionDurationMinutes?: number, name?: Nullable<ApiUserV1Name>, nonce?: Nullable<string>, oauthAttachToken?: Nullable<string>): OAuthAppleIDTokenAuthenticateRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace OAuthAppleIDTokenAuthenticateRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => OAuthAppleIDTokenAuthenticateRequest;
    }
}
export declare class OAuthAttachRequest {
    constructor(provider: string);
    get provider(): string;
    copy(provider?: string): OAuthAttachRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace OAuthAttachRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => OAuthAttachRequest;
    }
}
export declare class OAuthAttachResponse implements StytchAPIResponse {
    constructor(requestId: string, oauthAttachToken: string, statusCode: number);
    get requestId(): string;
    get oauthAttachToken(): string;
    get statusCode(): number;
    copy(requestId?: string, oauthAttachToken?: string, statusCode?: number): OAuthAttachResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace OAuthAttachResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => OAuthAttachResponse;
    }
}
export declare class OAuthAuthenticateRequest {
    constructor(token: string, sessionDurationMinutes: number, codeVerifier?: Nullable<string>);
    get token(): string;
    get sessionDurationMinutes(): number;
    get codeVerifier(): Nullable<string>;
    copy(token?: string, sessionDurationMinutes?: number, codeVerifier?: Nullable<string>): OAuthAuthenticateRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace OAuthAuthenticateRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => OAuthAuthenticateRequest;
    }
}
export declare class OAuthAuthenticateResponse implements StytchAPIResponse/*, AuthenticatedResponse */ {
    constructor(requestId: string, userId: string, providerSubject: string, providerType: string, session: ApiSessionV1Session, userSession: ApiSessionV1Session, sessionToken: string, sessionJwt: string, providerValues: ApiOauthV1ProviderValues, user: ApiUserV1User, resetSessions: boolean, oauthUserRegistrationId: string, statusCode: number);
    get requestId(): string;
    get userId(): string;
    get providerSubject(): string;
    get providerType(): string;
    get userSession(): ApiSessionV1Session;
    get providerValues(): ApiOauthV1ProviderValues;
    get resetSessions(): boolean;
    get oauthUserRegistrationId(): string;
    get statusCode(): number;
    copy(requestId?: string, userId?: string, providerSubject?: string, providerType?: string, session?: ApiSessionV1Session, userSession?: ApiSessionV1Session, sessionToken?: string, sessionJwt?: string, providerValues?: ApiOauthV1ProviderValues, user?: ApiUserV1User, resetSessions?: boolean, oauthUserRegistrationId?: string, statusCode?: number): OAuthAuthenticateResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace OAuthAuthenticateResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => OAuthAuthenticateResponse;
    }
}
export declare class OAuthAuthorizeRequest {
    constructor(clientId: string, redirectUri: string, responseType: string, scope: string, state?: Nullable<string>, nonce?: Nullable<string>, codeChallenge?: Nullable<string>, consentGranted?: Nullable<string>, prompt?: Nullable<string>, resources?: Nullable<KtList<string>>);
    get clientId(): string;
    get redirectUri(): string;
    get responseType(): string;
    get scope(): string;
    get state(): Nullable<string>;
    get nonce(): Nullable<string>;
    get codeChallenge(): Nullable<string>;
    get consentGranted(): Nullable<string>;
    get prompt(): Nullable<string>;
    get resources(): Nullable<KtList<string>>;
    copy(clientId?: string, redirectUri?: string, responseType?: string, scope?: string, state?: Nullable<string>, nonce?: Nullable<string>, codeChallenge?: Nullable<string>, consentGranted?: Nullable<string>, prompt?: Nullable<string>, resources?: Nullable<KtList<string>>): OAuthAuthorizeRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace OAuthAuthorizeRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => OAuthAuthorizeRequest;
    }
}
export declare class OAuthAuthorizeResponse implements StytchAPIResponse {
    constructor(requestId: string, redirectUrl: string, statusCode: number);
    get requestId(): string;
    get redirectUrl(): string;
    get statusCode(): number;
    copy(requestId?: string, redirectUrl?: string, statusCode?: number): OAuthAuthorizeResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace OAuthAuthorizeResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => OAuthAuthorizeResponse;
    }
}
export declare class OAuthAuthorizeStartRequest {
    constructor(clientId: string, redirectUri: string, responseType: string, scope: string, prompt?: Nullable<string>);
    get clientId(): string;
    get redirectUri(): string;
    get responseType(): string;
    get scope(): string;
    get prompt(): Nullable<string>;
    copy(clientId?: string, redirectUri?: string, responseType?: string, scope?: string, prompt?: Nullable<string>): OAuthAuthorizeStartRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace OAuthAuthorizeStartRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => OAuthAuthorizeStartRequest;
    }
}
export declare class OAuthAuthorizeStartResponse implements StytchAPIResponse {
    constructor(requestId: string, consentRequired: boolean, oidcClient: ApiConnectedappsV1ConnectedAppPublic, grantableScope: string, ungrantableScope: string, statusCode: number);
    get requestId(): string;
    get consentRequired(): boolean;
    get oidcClient(): ApiConnectedappsV1ConnectedAppPublic;
    get grantableScope(): string;
    get ungrantableScope(): string;
    get statusCode(): number;
    copy(requestId?: string, consentRequired?: boolean, oidcClient?: ApiConnectedappsV1ConnectedAppPublic, grantableScope?: string, ungrantableScope?: string, statusCode?: number): OAuthAuthorizeStartResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace OAuthAuthorizeStartResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => OAuthAuthorizeStartResponse;
    }
}
export declare class OAuthGoogleIDTokenAuthenticateRequest {
    constructor(idToken: string, sessionDurationMinutes: number, nonce?: Nullable<string>, oauthAttachToken?: Nullable<string>);
    get idToken(): string;
    get sessionDurationMinutes(): number;
    get nonce(): Nullable<string>;
    get oauthAttachToken(): Nullable<string>;
    copy(idToken?: string, sessionDurationMinutes?: number, nonce?: Nullable<string>, oauthAttachToken?: Nullable<string>): OAuthGoogleIDTokenAuthenticateRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace OAuthGoogleIDTokenAuthenticateRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => OAuthGoogleIDTokenAuthenticateRequest;
    }
}
export declare class OAuthGoogleIDTokenAuthenticateResponse implements StytchAPIResponse/*, AuthenticatedResponse */ {
    constructor(requestId: string, userId: string, providerSubject: string, session: ApiSessionV1Session, sessionToken: string, sessionJwt: string, user: ApiUserV1User, resetSessions: boolean, userCreated: boolean, statusCode: number);
    get requestId(): string;
    get userId(): string;
    get providerSubject(): string;
    get resetSessions(): boolean;
    get userCreated(): boolean;
    get statusCode(): number;
    copy(requestId?: string, userId?: string, providerSubject?: string, session?: ApiSessionV1Session, sessionToken?: string, sessionJwt?: string, user?: ApiUserV1User, resetSessions?: boolean, userCreated?: boolean, statusCode?: number): OAuthGoogleIDTokenAuthenticateResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace OAuthGoogleIDTokenAuthenticateResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => OAuthGoogleIDTokenAuthenticateResponse;
    }
}
export declare class OIDCLogoutRequest {
    constructor(clientId: string, postLogoutRedirectUri: string, state: string, idTokenHint?: Nullable<string>);
    get clientId(): string;
    get postLogoutRedirectUri(): string;
    get state(): string;
    get idTokenHint(): Nullable<string>;
    copy(clientId?: string, postLogoutRedirectUri?: string, state?: string, idTokenHint?: Nullable<string>): OIDCLogoutRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace OIDCLogoutRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => OIDCLogoutRequest;
    }
}
export declare class OIDCLogoutResponse implements StytchAPIResponse {
    constructor(consentRequired: boolean, redirectUri: string, statusCode: number);
    get consentRequired(): boolean;
    get redirectUri(): string;
    get statusCode(): number;
    copy(consentRequired?: boolean, redirectUri?: string, statusCode?: number): OIDCLogoutResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace OIDCLogoutResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => OIDCLogoutResponse;
    }
}
export declare class OTPsAuthenticateRequest {
    constructor(token: string, methodId: string, sessionDurationMinutes: number, dfpTelemetryId?: Nullable<string>, captchaToken?: Nullable<string>);
    get token(): string;
    get methodId(): string;
    get sessionDurationMinutes(): number;
    get dfpTelemetryId(): Nullable<string>;
    get captchaToken(): Nullable<string>;
    copy(token?: string, methodId?: string, sessionDurationMinutes?: number, dfpTelemetryId?: Nullable<string>, captchaToken?: Nullable<string>): OTPsAuthenticateRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace OTPsAuthenticateRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => OTPsAuthenticateRequest;
    }
}
export declare class OTPsAuthenticateResponse implements StytchAPIResponse/*, AuthenticatedResponse */ {
    constructor(requestId: string, userId: string, methodId: string, sessionToken: string, session: ApiSessionV1Session, sessionJwt: string, user: ApiUserV1User, resetSessions: boolean, userDevice: SDKDeviceInfo, statusCode: number);
    get requestId(): string;
    get userId(): string;
    get methodId(): string;
    get resetSessions(): boolean;
    get userDevice(): SDKDeviceInfo;
    get statusCode(): number;
    copy(requestId?: string, userId?: string, methodId?: string, sessionToken?: string, session?: ApiSessionV1Session, sessionJwt?: string, user?: ApiUserV1User, resetSessions?: boolean, userDevice?: SDKDeviceInfo, statusCode?: number): OTPsAuthenticateResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace OTPsAuthenticateResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => OTPsAuthenticateResponse;
    }
}
export declare class OTPsEmailLoginOrCreateRequest {
    constructor(email: string, expirationMinutes?: Nullable<number>, signupTemplateId?: Nullable<string>, loginTemplateId?: Nullable<string>, captchaToken?: Nullable<string>, locale?: Nullable<string>, dfpTelemetryId?: Nullable<string>);
    get email(): string;
    get expirationMinutes(): Nullable<number>;
    get signupTemplateId(): Nullable<string>;
    get loginTemplateId(): Nullable<string>;
    get captchaToken(): Nullable<string>;
    get locale(): Nullable<string>;
    get dfpTelemetryId(): Nullable<string>;
    copy(email?: string, expirationMinutes?: Nullable<number>, signupTemplateId?: Nullable<string>, loginTemplateId?: Nullable<string>, captchaToken?: Nullable<string>, locale?: Nullable<string>, dfpTelemetryId?: Nullable<string>): OTPsEmailLoginOrCreateRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace OTPsEmailLoginOrCreateRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => OTPsEmailLoginOrCreateRequest;
    }
}
export declare class OTPsEmailLoginOrCreateResponse implements StytchAPIResponse {
    constructor(requestId: string, methodId: string, statusCode: number);
    get requestId(): string;
    get methodId(): string;
    get statusCode(): number;
    copy(requestId?: string, methodId?: string, statusCode?: number): OTPsEmailLoginOrCreateResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace OTPsEmailLoginOrCreateResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => OTPsEmailLoginOrCreateResponse;
    }
}
export declare class OTPsEmailSendSecondaryRequest {
    constructor(email: string, expirationMinutes?: Nullable<number>, signupTemplateId?: Nullable<string>, loginTemplateId?: Nullable<string>, captchaToken?: Nullable<string>, locale?: Nullable<string>);
    get email(): string;
    get expirationMinutes(): Nullable<number>;
    get signupTemplateId(): Nullable<string>;
    get loginTemplateId(): Nullable<string>;
    get captchaToken(): Nullable<string>;
    get locale(): Nullable<string>;
    copy(email?: string, expirationMinutes?: Nullable<number>, signupTemplateId?: Nullable<string>, loginTemplateId?: Nullable<string>, captchaToken?: Nullable<string>, locale?: Nullable<string>): OTPsEmailSendSecondaryRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace OTPsEmailSendSecondaryRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => OTPsEmailSendSecondaryRequest;
    }
}
export declare class OTPsEmailSendSecondaryResponse implements StytchAPIResponse {
    constructor(requestId: string, methodId: string, statusCode: number);
    get requestId(): string;
    get methodId(): string;
    get statusCode(): number;
    copy(requestId?: string, methodId?: string, statusCode?: number): OTPsEmailSendSecondaryResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace OTPsEmailSendSecondaryResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => OTPsEmailSendSecondaryResponse;
    }
}
export declare class OTPsSMSLoginOrCreateRequest {
    constructor(phoneNumber: string, expirationMinutes?: Nullable<number>, dfpTelemetryId?: Nullable<string>, captchaToken?: Nullable<string>, locale?: Nullable<string>, enableAutofill?: Nullable<boolean>);
    get phoneNumber(): string;
    get expirationMinutes(): Nullable<number>;
    get dfpTelemetryId(): Nullable<string>;
    get captchaToken(): Nullable<string>;
    get locale(): Nullable<string>;
    get enableAutofill(): Nullable<boolean>;
    copy(phoneNumber?: string, expirationMinutes?: Nullable<number>, dfpTelemetryId?: Nullable<string>, captchaToken?: Nullable<string>, locale?: Nullable<string>, enableAutofill?: Nullable<boolean>): OTPsSMSLoginOrCreateRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace OTPsSMSLoginOrCreateRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => OTPsSMSLoginOrCreateRequest;
    }
}
export declare class OTPsSMSLoginOrCreateResponse implements StytchAPIResponse {
    constructor(requestId: string, methodId: string, statusCode: number);
    get requestId(): string;
    get methodId(): string;
    get statusCode(): number;
    copy(requestId?: string, methodId?: string, statusCode?: number): OTPsSMSLoginOrCreateResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace OTPsSMSLoginOrCreateResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => OTPsSMSLoginOrCreateResponse;
    }
}
export declare class OTPsSMSSendSecondaryRequest {
    constructor(phoneNumber: string, expirationMinutes?: Nullable<number>, dfpTelemetryId?: Nullable<string>, captchaToken?: Nullable<string>, locale?: Nullable<string>, enableAutofill?: Nullable<boolean>);
    get phoneNumber(): string;
    get expirationMinutes(): Nullable<number>;
    get dfpTelemetryId(): Nullable<string>;
    get captchaToken(): Nullable<string>;
    get locale(): Nullable<string>;
    get enableAutofill(): Nullable<boolean>;
    copy(phoneNumber?: string, expirationMinutes?: Nullable<number>, dfpTelemetryId?: Nullable<string>, captchaToken?: Nullable<string>, locale?: Nullable<string>, enableAutofill?: Nullable<boolean>): OTPsSMSSendSecondaryRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace OTPsSMSSendSecondaryRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => OTPsSMSSendSecondaryRequest;
    }
}
export declare class OTPsSMSSendSecondaryResponse implements StytchAPIResponse {
    constructor(requestId: string, methodId: string, statusCode: number);
    get requestId(): string;
    get methodId(): string;
    get statusCode(): number;
    copy(requestId?: string, methodId?: string, statusCode?: number): OTPsSMSSendSecondaryResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace OTPsSMSSendSecondaryResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => OTPsSMSSendSecondaryResponse;
    }
}
export declare class OTPsWhatsAppLoginOrCreateRequest {
    constructor(phoneNumber: string, expirationMinutes?: Nullable<number>, dfpTelemetryId?: Nullable<string>, captchaToken?: Nullable<string>, locale?: Nullable<string>);
    get phoneNumber(): string;
    get expirationMinutes(): Nullable<number>;
    get dfpTelemetryId(): Nullable<string>;
    get captchaToken(): Nullable<string>;
    get locale(): Nullable<string>;
    copy(phoneNumber?: string, expirationMinutes?: Nullable<number>, dfpTelemetryId?: Nullable<string>, captchaToken?: Nullable<string>, locale?: Nullable<string>): OTPsWhatsAppLoginOrCreateRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace OTPsWhatsAppLoginOrCreateRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => OTPsWhatsAppLoginOrCreateRequest;
    }
}
export declare class OTPsWhatsAppLoginOrCreateResponse implements StytchAPIResponse {
    constructor(requestId: string, methodId: string, statusCode: number);
    get requestId(): string;
    get methodId(): string;
    get statusCode(): number;
    copy(requestId?: string, methodId?: string, statusCode?: number): OTPsWhatsAppLoginOrCreateResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace OTPsWhatsAppLoginOrCreateResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => OTPsWhatsAppLoginOrCreateResponse;
    }
}
export declare class OTPsWhatsAppSendSecondaryRequest {
    constructor(phoneNumber: string, expirationMinutes?: Nullable<number>, dfpTelemetryId?: Nullable<string>, captchaToken?: Nullable<string>, locale?: Nullable<string>);
    get phoneNumber(): string;
    get expirationMinutes(): Nullable<number>;
    get dfpTelemetryId(): Nullable<string>;
    get captchaToken(): Nullable<string>;
    get locale(): Nullable<string>;
    copy(phoneNumber?: string, expirationMinutes?: Nullable<number>, dfpTelemetryId?: Nullable<string>, captchaToken?: Nullable<string>, locale?: Nullable<string>): OTPsWhatsAppSendSecondaryRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace OTPsWhatsAppSendSecondaryRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => OTPsWhatsAppSendSecondaryRequest;
    }
}
export declare class OTPsWhatsAppSendSecondaryResponse implements StytchAPIResponse {
    constructor(requestId: string, methodId: string, statusCode: number);
    get requestId(): string;
    get methodId(): string;
    get statusCode(): number;
    copy(requestId?: string, methodId?: string, statusCode?: number): OTPsWhatsAppSendSecondaryResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace OTPsWhatsAppSendSecondaryResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => OTPsWhatsAppSendSecondaryResponse;
    }
}
export declare class OrgSearchBySlugRequest {
    constructor(organizationSlug: string);
    get organizationSlug(): string;
    copy(organizationSlug?: string): OrgSearchBySlugRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace OrgSearchBySlugRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => OrgSearchBySlugRequest;
    }
}
export declare class OrgSearchBySlugResponse implements StytchAPIResponse {
    constructor(requestId: string, organization: Organization, statusCode: number);
    get requestId(): string;
    get organization(): Organization;
    get statusCode(): number;
    copy(requestId?: string, organization?: Organization, statusCode?: number): OrgSearchBySlugResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace OrgSearchBySlugResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => OrgSearchBySlugResponse;
    }
}
export declare class Organization {
    constructor(organizationName: string, organizationLogoUrl: string, organizationId: string, ssoActiveConnections: KtList<ActiveSSOConnection>, emailJitProvisioning: string, emailAllowedDomains: KtList<string>, authMethods: string, allowedAuthMethods: KtList<string>, mfaPolicy: string, organizationSlug: string, ssoDefaultConnectionId?: Nullable<string>);
    get organizationName(): string;
    get organizationLogoUrl(): string;
    get organizationId(): string;
    get ssoActiveConnections(): KtList<ActiveSSOConnection>;
    get emailJitProvisioning(): string;
    get emailAllowedDomains(): KtList<string>;
    get authMethods(): string;
    get allowedAuthMethods(): KtList<string>;
    get mfaPolicy(): string;
    get organizationSlug(): string;
    get ssoDefaultConnectionId(): Nullable<string>;
    copy(organizationName?: string, organizationLogoUrl?: string, organizationId?: string, ssoActiveConnections?: KtList<ActiveSSOConnection>, emailJitProvisioning?: string, emailAllowedDomains?: KtList<string>, authMethods?: string, allowedAuthMethods?: KtList<string>, mfaPolicy?: string, organizationSlug?: string, ssoDefaultConnectionId?: Nullable<string>): Organization;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace Organization {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => Organization;
    }
}
export declare class OrganizationsAdminMemberDeleteMFAPhoneNumberResponse implements StytchAPIResponse {
    constructor(requestId: string, memberId: string, member: ApiOrganizationV1Member, organization: ApiOrganizationV1Organization, statusCode: number);
    get requestId(): string;
    get memberId(): string;
    get member(): ApiOrganizationV1Member;
    get organization(): ApiOrganizationV1Organization;
    get statusCode(): number;
    copy(requestId?: string, memberId?: string, member?: ApiOrganizationV1Member, organization?: ApiOrganizationV1Organization, statusCode?: number): OrganizationsAdminMemberDeleteMFAPhoneNumberResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace OrganizationsAdminMemberDeleteMFAPhoneNumberResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => OrganizationsAdminMemberDeleteMFAPhoneNumberResponse;
    }
}
export declare class OrganizationsAdminMemberDeleteMFATOTPResponse implements StytchAPIResponse {
    constructor(requestId: string, memberId: string, member: ApiOrganizationV1Member, organization: ApiOrganizationV1Organization, statusCode: number);
    get requestId(): string;
    get memberId(): string;
    get member(): ApiOrganizationV1Member;
    get organization(): ApiOrganizationV1Organization;
    get statusCode(): number;
    copy(requestId?: string, memberId?: string, member?: ApiOrganizationV1Member, organization?: ApiOrganizationV1Organization, statusCode?: number): OrganizationsAdminMemberDeleteMFATOTPResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace OrganizationsAdminMemberDeleteMFATOTPResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => OrganizationsAdminMemberDeleteMFATOTPResponse;
    }
}
export declare class OrganizationsAdminMemberDeletePasswordResponse implements StytchAPIResponse {
    constructor(requestId: string, memberId: string, member: ApiOrganizationV1Member, organization: ApiOrganizationV1Organization, statusCode: number);
    get requestId(): string;
    get memberId(): string;
    get member(): ApiOrganizationV1Member;
    get organization(): ApiOrganizationV1Organization;
    get statusCode(): number;
    copy(requestId?: string, memberId?: string, member?: ApiOrganizationV1Member, organization?: ApiOrganizationV1Organization, statusCode?: number): OrganizationsAdminMemberDeletePasswordResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace OrganizationsAdminMemberDeletePasswordResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => OrganizationsAdminMemberDeletePasswordResponse;
    }
}
export declare class OrganizationsAdminMemberDeleteResponse implements StytchAPIResponse {
    constructor(requestId: string, memberId: string, statusCode: number);
    get requestId(): string;
    get memberId(): string;
    get statusCode(): number;
    copy(requestId?: string, memberId?: string, statusCode?: number): OrganizationsAdminMemberDeleteResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace OrganizationsAdminMemberDeleteResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => OrganizationsAdminMemberDeleteResponse;
    }
}
export declare class OrganizationsAdminMemberGetConnectedAppsResponse implements StytchAPIResponse {
    constructor(requestId: string, connectedApps: KtList<ApiOrganizationV1MemberConnectedApp>, statusCode: number);
    get requestId(): string;
    get connectedApps(): KtList<ApiOrganizationV1MemberConnectedApp>;
    get statusCode(): number;
    copy(requestId?: string, connectedApps?: KtList<ApiOrganizationV1MemberConnectedApp>, statusCode?: number): OrganizationsAdminMemberGetConnectedAppsResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace OrganizationsAdminMemberGetConnectedAppsResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => OrganizationsAdminMemberGetConnectedAppsResponse;
    }
}
export declare class OrganizationsAdminMemberReactivateResponse implements StytchAPIResponse {
    constructor(requestId: string, memberId: string, member: ApiOrganizationV1Member, organization: ApiOrganizationV1Organization, statusCode: number);
    get requestId(): string;
    get memberId(): string;
    get member(): ApiOrganizationV1Member;
    get organization(): ApiOrganizationV1Organization;
    get statusCode(): number;
    copy(requestId?: string, memberId?: string, member?: ApiOrganizationV1Member, organization?: ApiOrganizationV1Organization, statusCode?: number): OrganizationsAdminMemberReactivateResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace OrganizationsAdminMemberReactivateResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => OrganizationsAdminMemberReactivateResponse;
    }
}
export declare class OrganizationsAdminMemberRevokeConnectedAppResponse implements StytchAPIResponse {
    constructor(requestId: string, statusCode: number);
    get requestId(): string;
    get statusCode(): number;
    copy(requestId?: string, statusCode?: number): OrganizationsAdminMemberRevokeConnectedAppResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace OrganizationsAdminMemberRevokeConnectedAppResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => OrganizationsAdminMemberRevokeConnectedAppResponse;
    }
}
export declare class OrganizationsAdminMemberStartEmailUpdateRequest {
    constructor(emailAddress: string, loginRedirectUrl?: Nullable<string>, locale?: Nullable<string>, loginTemplateId?: Nullable<string>, deliveryMethod?: Nullable<string>);
    get emailAddress(): string;
    get loginRedirectUrl(): Nullable<string>;
    get locale(): Nullable<string>;
    get loginTemplateId(): Nullable<string>;
    get deliveryMethod(): Nullable<string>;
    copy(emailAddress?: string, loginRedirectUrl?: Nullable<string>, locale?: Nullable<string>, loginTemplateId?: Nullable<string>, deliveryMethod?: Nullable<string>): OrganizationsAdminMemberStartEmailUpdateRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace OrganizationsAdminMemberStartEmailUpdateRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => OrganizationsAdminMemberStartEmailUpdateRequest;
    }
}
export declare class OrganizationsAdminMemberStartEmailUpdateResponse implements StytchAPIResponse {
    constructor(requestId: string, memberId: string, member: ApiOrganizationV1Member, organization: ApiOrganizationV1Organization, statusCode: number);
    get requestId(): string;
    get memberId(): string;
    get member(): ApiOrganizationV1Member;
    get organization(): ApiOrganizationV1Organization;
    get statusCode(): number;
    copy(requestId?: string, memberId?: string, member?: ApiOrganizationV1Member, organization?: ApiOrganizationV1Organization, statusCode?: number): OrganizationsAdminMemberStartEmailUpdateResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace OrganizationsAdminMemberStartEmailUpdateResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => OrganizationsAdminMemberStartEmailUpdateResponse;
    }
}
export declare class OrganizationsAdminMemberUnlinkRetiredEmailRequest {
    constructor(emailId?: Nullable<string>, emailAddress?: Nullable<string>);
    get emailId(): Nullable<string>;
    get emailAddress(): Nullable<string>;
    copy(emailId?: Nullable<string>, emailAddress?: Nullable<string>): OrganizationsAdminMemberUnlinkRetiredEmailRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace OrganizationsAdminMemberUnlinkRetiredEmailRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => OrganizationsAdminMemberUnlinkRetiredEmailRequest;
    }
}
export declare class OrganizationsAdminMemberUnlinkRetiredEmailResponse implements StytchAPIResponse {
    constructor(requestId: string, memberId: string, member: ApiOrganizationV1Member, organization: ApiOrganizationV1Organization, statusCode: number);
    get requestId(): string;
    get memberId(): string;
    get member(): ApiOrganizationV1Member;
    get organization(): ApiOrganizationV1Organization;
    get statusCode(): number;
    copy(requestId?: string, memberId?: string, member?: ApiOrganizationV1Member, organization?: ApiOrganizationV1Organization, statusCode?: number): OrganizationsAdminMemberUnlinkRetiredEmailResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace OrganizationsAdminMemberUnlinkRetiredEmailResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => OrganizationsAdminMemberUnlinkRetiredEmailResponse;
    }
}
export declare class OrganizationsAdminMemberUpdateRequest {
    constructor(preserveExistingSessions: boolean, name?: Nullable<string>, mfaEnrolled?: Nullable<boolean>, mfaPhoneNumber?: Nullable<string>, untrustedMetadata?: Nullable<KtMap<string, any/* JsonElement */>>, isBreakglass?: Nullable<boolean>, roles?: Nullable<KtList<string>>, defaultMfaMethod?: Nullable<string>, emailAddress?: Nullable<string>, unlinkEmail?: Nullable<boolean>);
    get preserveExistingSessions(): boolean;
    get name(): Nullable<string>;
    get mfaEnrolled(): Nullable<boolean>;
    get mfaPhoneNumber(): Nullable<string>;
    get untrustedMetadata(): Nullable<KtMap<string, any/* JsonElement */>>;
    get isBreakglass(): Nullable<boolean>;
    get roles(): Nullable<KtList<string>>;
    get defaultMfaMethod(): Nullable<string>;
    get emailAddress(): Nullable<string>;
    get unlinkEmail(): Nullable<boolean>;
    copy(preserveExistingSessions?: boolean, name?: Nullable<string>, mfaEnrolled?: Nullable<boolean>, mfaPhoneNumber?: Nullable<string>, untrustedMetadata?: Nullable<KtMap<string, any/* JsonElement */>>, isBreakglass?: Nullable<boolean>, roles?: Nullable<KtList<string>>, defaultMfaMethod?: Nullable<string>, emailAddress?: Nullable<string>, unlinkEmail?: Nullable<boolean>): OrganizationsAdminMemberUpdateRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace OrganizationsAdminMemberUpdateRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => OrganizationsAdminMemberUpdateRequest;
    }
}
export declare class OrganizationsAdminMemberUpdateResponse implements StytchAPIResponse {
    constructor(requestId: string, memberId: string, member: ApiOrganizationV1Member, organization: ApiOrganizationV1Organization, statusCode: number);
    get requestId(): string;
    get memberId(): string;
    get member(): ApiOrganizationV1Member;
    get organization(): ApiOrganizationV1Organization;
    get statusCode(): number;
    copy(requestId?: string, memberId?: string, member?: ApiOrganizationV1Member, organization?: ApiOrganizationV1Organization, statusCode?: number): OrganizationsAdminMemberUpdateResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace OrganizationsAdminMemberUpdateResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => OrganizationsAdminMemberUpdateResponse;
    }
}
export declare class OrganizationsGetConnectedAppResponse implements StytchAPIResponse {
    constructor(requestId: string, connectedAppId: string, name: string, description: string, clientType: string, activeMembers: KtList<ApiOrganizationV1OrganizationConnectedAppActiveMember>, statusCode: number, logoUrl?: Nullable<string>);
    get requestId(): string;
    get connectedAppId(): string;
    get name(): string;
    get description(): string;
    get clientType(): string;
    get activeMembers(): KtList<ApiOrganizationV1OrganizationConnectedAppActiveMember>;
    get statusCode(): number;
    get logoUrl(): Nullable<string>;
    copy(requestId?: string, connectedAppId?: string, name?: string, description?: string, clientType?: string, activeMembers?: KtList<ApiOrganizationV1OrganizationConnectedAppActiveMember>, statusCode?: number, logoUrl?: Nullable<string>): OrganizationsGetConnectedAppResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace OrganizationsGetConnectedAppResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => OrganizationsGetConnectedAppResponse;
    }
}
export declare class OrganizationsGetConnectedAppsResponse implements StytchAPIResponse {
    constructor(requestId: string, connectedApps: KtList<ApiOrganizationV1OrganizationConnectedApp>, statusCode: number);
    get requestId(): string;
    get connectedApps(): KtList<ApiOrganizationV1OrganizationConnectedApp>;
    get statusCode(): number;
    copy(requestId?: string, connectedApps?: KtList<ApiOrganizationV1OrganizationConnectedApp>, statusCode?: number): OrganizationsGetConnectedAppsResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace OrganizationsGetConnectedAppsResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => OrganizationsGetConnectedAppsResponse;
    }
}
export declare class OrganizationsMemberCreateRequest {
    constructor(emailAddress: string, isBreakglass: boolean, createMemberAsPending: boolean, roles: KtList<string>, name?: Nullable<string>, mfaEnrolled?: Nullable<boolean>, mfaPhoneNumber?: Nullable<string>, untrustedMetadata?: Nullable<KtMap<string, any/* JsonElement */>>);
    get emailAddress(): string;
    get isBreakglass(): boolean;
    get createMemberAsPending(): boolean;
    get roles(): KtList<string>;
    get name(): Nullable<string>;
    get mfaEnrolled(): Nullable<boolean>;
    get mfaPhoneNumber(): Nullable<string>;
    get untrustedMetadata(): Nullable<KtMap<string, any/* JsonElement */>>;
    copy(emailAddress?: string, isBreakglass?: boolean, createMemberAsPending?: boolean, roles?: KtList<string>, name?: Nullable<string>, mfaEnrolled?: Nullable<boolean>, mfaPhoneNumber?: Nullable<string>, untrustedMetadata?: Nullable<KtMap<string, any/* JsonElement */>>): OrganizationsMemberCreateRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace OrganizationsMemberCreateRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => OrganizationsMemberCreateRequest;
    }
}
export declare class OrganizationsMemberCreateResponse implements StytchAPIResponse {
    constructor(requestId: string, memberId: string, member: ApiOrganizationV1Member, organization: ApiOrganizationV1Organization, statusCode: number);
    get requestId(): string;
    get memberId(): string;
    get member(): ApiOrganizationV1Member;
    get organization(): ApiOrganizationV1Organization;
    get statusCode(): number;
    copy(requestId?: string, memberId?: string, member?: ApiOrganizationV1Member, organization?: ApiOrganizationV1Organization, statusCode?: number): OrganizationsMemberCreateResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace OrganizationsMemberCreateResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => OrganizationsMemberCreateResponse;
    }
}
export declare class OrganizationsMemberDeleteMFAPhoneNumberResponse implements StytchAPIResponse {
    constructor(requestId: string, memberId: string, member: ApiOrganizationV1Member, organization: ApiOrganizationV1Organization, statusCode: number);
    get requestId(): string;
    get memberId(): string;
    get member(): ApiOrganizationV1Member;
    get organization(): ApiOrganizationV1Organization;
    get statusCode(): number;
    copy(requestId?: string, memberId?: string, member?: ApiOrganizationV1Member, organization?: ApiOrganizationV1Organization, statusCode?: number): OrganizationsMemberDeleteMFAPhoneNumberResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace OrganizationsMemberDeleteMFAPhoneNumberResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => OrganizationsMemberDeleteMFAPhoneNumberResponse;
    }
}
export declare class OrganizationsMemberDeleteMFATOTPResponse implements StytchAPIResponse {
    constructor(requestId: string, memberId: string, member: ApiOrganizationV1Member, organization: ApiOrganizationV1Organization, statusCode: number);
    get requestId(): string;
    get memberId(): string;
    get member(): ApiOrganizationV1Member;
    get organization(): ApiOrganizationV1Organization;
    get statusCode(): number;
    copy(requestId?: string, memberId?: string, member?: ApiOrganizationV1Member, organization?: ApiOrganizationV1Organization, statusCode?: number): OrganizationsMemberDeleteMFATOTPResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace OrganizationsMemberDeleteMFATOTPResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => OrganizationsMemberDeleteMFATOTPResponse;
    }
}
export declare class OrganizationsMemberGetConnectedAppsResponse implements StytchAPIResponse {
    constructor(requestId: string, connectedApps: KtList<ApiOrganizationV1MemberConnectedApp>, statusCode: number);
    get requestId(): string;
    get connectedApps(): KtList<ApiOrganizationV1MemberConnectedApp>;
    get statusCode(): number;
    copy(requestId?: string, connectedApps?: KtList<ApiOrganizationV1MemberConnectedApp>, statusCode?: number): OrganizationsMemberGetConnectedAppsResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace OrganizationsMemberGetConnectedAppsResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => OrganizationsMemberGetConnectedAppsResponse;
    }
}
export declare class OrganizationsMemberRevokeConnectedAppResponse implements StytchAPIResponse {
    constructor(requestId: string, statusCode: number);
    get requestId(): string;
    get statusCode(): number;
    copy(requestId?: string, statusCode?: number): OrganizationsMemberRevokeConnectedAppResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace OrganizationsMemberRevokeConnectedAppResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => OrganizationsMemberRevokeConnectedAppResponse;
    }
}
export declare class OrganizationsMemberSearchRequest {
    constructor(cursor: string, limit?: Nullable<number>, query?: Nullable<ApiOrganizationV1ExternalSearchQuery>);
    get cursor(): string;
    get limit(): Nullable<number>;
    get query(): Nullable<ApiOrganizationV1ExternalSearchQuery>;
    copy(cursor?: string, limit?: Nullable<number>, query?: Nullable<ApiOrganizationV1ExternalSearchQuery>): OrganizationsMemberSearchRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace OrganizationsMemberSearchRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => OrganizationsMemberSearchRequest;
    }
}
export declare class OrganizationsMemberSearchResponse implements StytchAPIResponse {
    constructor(requestId: string, members: KtList<ApiOrganizationV1Member>, resultsMetadata: ApiOrganizationV1ResultsMetadata, organizations: KtMap<string, ApiOrganizationV1Organization>, statusCode: number);
    get requestId(): string;
    get members(): KtList<ApiOrganizationV1Member>;
    get resultsMetadata(): ApiOrganizationV1ResultsMetadata;
    get organizations(): KtMap<string, ApiOrganizationV1Organization>;
    get statusCode(): number;
    copy(requestId?: string, members?: KtList<ApiOrganizationV1Member>, resultsMetadata?: ApiOrganizationV1ResultsMetadata, organizations?: KtMap<string, ApiOrganizationV1Organization>, statusCode?: number): OrganizationsMemberSearchResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace OrganizationsMemberSearchResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => OrganizationsMemberSearchResponse;
    }
}
export declare class OrganizationsMemberStartEmailUpdateRequest {
    constructor(emailAddress: string, loginRedirectUrl?: Nullable<string>, locale?: Nullable<string>, loginTemplateId?: Nullable<string>, deliveryMethod?: Nullable<string>);
    get emailAddress(): string;
    get loginRedirectUrl(): Nullable<string>;
    get locale(): Nullable<string>;
    get loginTemplateId(): Nullable<string>;
    get deliveryMethod(): Nullable<string>;
    copy(emailAddress?: string, loginRedirectUrl?: Nullable<string>, locale?: Nullable<string>, loginTemplateId?: Nullable<string>, deliveryMethod?: Nullable<string>): OrganizationsMemberStartEmailUpdateRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace OrganizationsMemberStartEmailUpdateRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => OrganizationsMemberStartEmailUpdateRequest;
    }
}
export declare class OrganizationsMemberStartEmailUpdateResponse implements StytchAPIResponse {
    constructor(requestId: string, memberId: string, member: ApiOrganizationV1Member, organization: ApiOrganizationV1Organization, statusCode: number);
    get requestId(): string;
    get memberId(): string;
    get member(): ApiOrganizationV1Member;
    get organization(): ApiOrganizationV1Organization;
    get statusCode(): number;
    copy(requestId?: string, memberId?: string, member?: ApiOrganizationV1Member, organization?: ApiOrganizationV1Organization, statusCode?: number): OrganizationsMemberStartEmailUpdateResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace OrganizationsMemberStartEmailUpdateResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => OrganizationsMemberStartEmailUpdateResponse;
    }
}
export declare class OrganizationsMemberUnlinkRetiredEmailRequest {
    constructor(emailId?: Nullable<string>, emailAddress?: Nullable<string>);
    get emailId(): Nullable<string>;
    get emailAddress(): Nullable<string>;
    copy(emailId?: Nullable<string>, emailAddress?: Nullable<string>): OrganizationsMemberUnlinkRetiredEmailRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace OrganizationsMemberUnlinkRetiredEmailRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => OrganizationsMemberUnlinkRetiredEmailRequest;
    }
}
export declare class OrganizationsMemberUnlinkRetiredEmailResponse implements StytchAPIResponse {
    constructor(requestId: string, memberId: string, member: ApiOrganizationV1Member, organization: ApiOrganizationV1Organization, statusCode: number);
    get requestId(): string;
    get memberId(): string;
    get member(): ApiOrganizationV1Member;
    get organization(): ApiOrganizationV1Organization;
    get statusCode(): number;
    copy(requestId?: string, memberId?: string, member?: ApiOrganizationV1Member, organization?: ApiOrganizationV1Organization, statusCode?: number): OrganizationsMemberUnlinkRetiredEmailResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace OrganizationsMemberUnlinkRetiredEmailResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => OrganizationsMemberUnlinkRetiredEmailResponse;
    }
}
export declare class OrganizationsMemberUpdateRequest {
    constructor(name?: Nullable<string>, mfaEnrolled?: Nullable<boolean>, mfaPhoneNumber?: Nullable<string>, untrustedMetadata?: Nullable<KtMap<string, any/* JsonElement */>>, defaultMfaMethod?: Nullable<string>);
    get name(): Nullable<string>;
    get mfaEnrolled(): Nullable<boolean>;
    get mfaPhoneNumber(): Nullable<string>;
    get untrustedMetadata(): Nullable<KtMap<string, any/* JsonElement */>>;
    get defaultMfaMethod(): Nullable<string>;
    copy(name?: Nullable<string>, mfaEnrolled?: Nullable<boolean>, mfaPhoneNumber?: Nullable<string>, untrustedMetadata?: Nullable<KtMap<string, any/* JsonElement */>>, defaultMfaMethod?: Nullable<string>): OrganizationsMemberUpdateRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace OrganizationsMemberUpdateRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => OrganizationsMemberUpdateRequest;
    }
}
export declare class OrganizationsMemberUpdateResponse implements StytchAPIResponse {
    constructor(requestId: string, memberId: string, member: ApiOrganizationV1Member, organization: ApiOrganizationV1Organization, statusCode: number);
    get requestId(): string;
    get memberId(): string;
    get member(): ApiOrganizationV1Member;
    get organization(): ApiOrganizationV1Organization;
    get statusCode(): number;
    copy(requestId?: string, memberId?: string, member?: ApiOrganizationV1Member, organization?: ApiOrganizationV1Organization, statusCode?: number): OrganizationsMemberUpdateResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace OrganizationsMemberUpdateResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => OrganizationsMemberUpdateResponse;
    }
}
export declare class PasswordsAuthenticateRequest {
    constructor(email: string, password: string, sessionDurationMinutes: number, captchaToken?: Nullable<string>, dfpTelemetryId?: Nullable<string>);
    get email(): string;
    get password(): string;
    get sessionDurationMinutes(): number;
    get captchaToken(): Nullable<string>;
    get dfpTelemetryId(): Nullable<string>;
    copy(email?: string, password?: string, sessionDurationMinutes?: number, captchaToken?: Nullable<string>, dfpTelemetryId?: Nullable<string>): PasswordsAuthenticateRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace PasswordsAuthenticateRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => PasswordsAuthenticateRequest;
    }
}
export declare class PasswordsAuthenticateResponse implements StytchAPIResponse/*, AuthenticatedResponse */ {
    constructor(requestId: string, userId: string, sessionToken: string, session: ApiSessionV1Session, sessionJwt: string, user: ApiUserV1User, userDevice: SDKDeviceInfo, statusCode: number);
    get requestId(): string;
    get userId(): string;
    get userDevice(): SDKDeviceInfo;
    get statusCode(): number;
    copy(requestId?: string, userId?: string, sessionToken?: string, session?: ApiSessionV1Session, sessionJwt?: string, user?: ApiUserV1User, userDevice?: SDKDeviceInfo, statusCode?: number): PasswordsAuthenticateResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace PasswordsAuthenticateResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => PasswordsAuthenticateResponse;
    }
}
export declare class PasswordsCreateRequest {
    constructor(email: string, password: string, sessionDurationMinutes: number, captchaToken?: Nullable<string>, dfpTelemetryId?: Nullable<string>);
    get email(): string;
    get password(): string;
    get sessionDurationMinutes(): number;
    get captchaToken(): Nullable<string>;
    get dfpTelemetryId(): Nullable<string>;
    copy(email?: string, password?: string, sessionDurationMinutes?: number, captchaToken?: Nullable<string>, dfpTelemetryId?: Nullable<string>): PasswordsCreateRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace PasswordsCreateRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => PasswordsCreateRequest;
    }
}
export declare class PasswordsCreateResponse implements StytchAPIResponse/*, AuthenticatedResponse */ {
    constructor(requestId: string, userId: string, emailId: string, sessionToken: string, session: ApiSessionV1Session, sessionJwt: string, user: ApiUserV1User, userDevice: SDKDeviceInfo, statusCode: number);
    get requestId(): string;
    get userId(): string;
    get emailId(): string;
    get userDevice(): SDKDeviceInfo;
    get statusCode(): number;
    copy(requestId?: string, userId?: string, emailId?: string, sessionToken?: string, session?: ApiSessionV1Session, sessionJwt?: string, user?: ApiUserV1User, userDevice?: SDKDeviceInfo, statusCode?: number): PasswordsCreateResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace PasswordsCreateResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => PasswordsCreateResponse;
    }
}
export declare class PasswordsEmailResetRequest {
    constructor(token: string, password: string, sessionDurationMinutes: number, captchaToken?: Nullable<string>, dfpTelemetryId?: Nullable<string>, codeVerifier?: Nullable<string>);
    get token(): string;
    get password(): string;
    get sessionDurationMinutes(): number;
    get captchaToken(): Nullable<string>;
    get dfpTelemetryId(): Nullable<string>;
    get codeVerifier(): Nullable<string>;
    copy(token?: string, password?: string, sessionDurationMinutes?: number, captchaToken?: Nullable<string>, dfpTelemetryId?: Nullable<string>, codeVerifier?: Nullable<string>): PasswordsEmailResetRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace PasswordsEmailResetRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => PasswordsEmailResetRequest;
    }
}
export declare class PasswordsEmailResetResponse implements StytchAPIResponse/*, AuthenticatedResponse */ {
    constructor(requestId: string, userId: string, sessionToken: string, session: ApiSessionV1Session, sessionJwt: string, user: ApiUserV1User, userDevice: SDKDeviceInfo, statusCode: number);
    get requestId(): string;
    get userId(): string;
    get userDevice(): SDKDeviceInfo;
    get statusCode(): number;
    copy(requestId?: string, userId?: string, sessionToken?: string, session?: ApiSessionV1Session, sessionJwt?: string, user?: ApiUserV1User, userDevice?: SDKDeviceInfo, statusCode?: number): PasswordsEmailResetResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace PasswordsEmailResetResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => PasswordsEmailResetResponse;
    }
}
export declare class PasswordsEmailResetStartRequest {
    constructor(email: string, loginRedirectUrl?: Nullable<string>, resetPasswordRedirectUrl?: Nullable<string>, resetPasswordExpirationMinutes?: Nullable<number>, codeChallenge?: Nullable<string>, captchaToken?: Nullable<string>, resetPasswordTemplateId?: Nullable<string>, dfpTelemetryId?: Nullable<string>);
    get email(): string;
    get loginRedirectUrl(): Nullable<string>;
    get resetPasswordRedirectUrl(): Nullable<string>;
    get resetPasswordExpirationMinutes(): Nullable<number>;
    get codeChallenge(): Nullable<string>;
    get captchaToken(): Nullable<string>;
    get resetPasswordTemplateId(): Nullable<string>;
    get dfpTelemetryId(): Nullable<string>;
    copy(email?: string, loginRedirectUrl?: Nullable<string>, resetPasswordRedirectUrl?: Nullable<string>, resetPasswordExpirationMinutes?: Nullable<number>, codeChallenge?: Nullable<string>, captchaToken?: Nullable<string>, resetPasswordTemplateId?: Nullable<string>, dfpTelemetryId?: Nullable<string>): PasswordsEmailResetStartRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace PasswordsEmailResetStartRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => PasswordsEmailResetStartRequest;
    }
}
export declare class PasswordsEmailResetStartResponse implements StytchAPIResponse {
    constructor(requestId: string, statusCode: number);
    get requestId(): string;
    get statusCode(): number;
    copy(requestId?: string, statusCode?: number): PasswordsEmailResetStartResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace PasswordsEmailResetStartResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => PasswordsEmailResetStartResponse;
    }
}
export declare class PasswordsExistingPasswordResetRequest {
    constructor(email: string, existingPassword: string, newPassword: string, sessionDurationMinutes?: Nullable<number>, captchaToken?: Nullable<string>, dfpTelemetryId?: Nullable<string>);
    get email(): string;
    get existingPassword(): string;
    get newPassword(): string;
    get sessionDurationMinutes(): Nullable<number>;
    get captchaToken(): Nullable<string>;
    get dfpTelemetryId(): Nullable<string>;
    copy(email?: string, existingPassword?: string, newPassword?: string, sessionDurationMinutes?: Nullable<number>, captchaToken?: Nullable<string>, dfpTelemetryId?: Nullable<string>): PasswordsExistingPasswordResetRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace PasswordsExistingPasswordResetRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => PasswordsExistingPasswordResetRequest;
    }
}
export declare class PasswordsExistingPasswordResetResponse implements StytchAPIResponse/*, AuthenticatedResponse */ {
    constructor(requestId: string, userId: string, sessionToken: string, session: ApiSessionV1Session, sessionJwt: string, user: ApiUserV1User, userDevice: SDKDeviceInfo, statusCode: number);
    get requestId(): string;
    get userId(): string;
    get userDevice(): SDKDeviceInfo;
    get statusCode(): number;
    copy(requestId?: string, userId?: string, sessionToken?: string, session?: ApiSessionV1Session, sessionJwt?: string, user?: ApiUserV1User, userDevice?: SDKDeviceInfo, statusCode?: number): PasswordsExistingPasswordResetResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace PasswordsExistingPasswordResetResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => PasswordsExistingPasswordResetResponse;
    }
}
export declare class PasswordsSessionResetRequest {
    constructor(password: string, sessionDurationMinutes?: Nullable<number>, captchaToken?: Nullable<string>, dfpTelemetryId?: Nullable<string>);
    get password(): string;
    get sessionDurationMinutes(): Nullable<number>;
    get captchaToken(): Nullable<string>;
    get dfpTelemetryId(): Nullable<string>;
    copy(password?: string, sessionDurationMinutes?: Nullable<number>, captchaToken?: Nullable<string>, dfpTelemetryId?: Nullable<string>): PasswordsSessionResetRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace PasswordsSessionResetRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => PasswordsSessionResetRequest;
    }
}
export declare class PasswordsSessionResetResponse implements StytchAPIResponse/*, AuthenticatedResponse */ {
    constructor(requestId: string, userId: string, sessionToken: string, session: ApiSessionV1Session, sessionJwt: string, user: ApiUserV1User, userDevice: SDKDeviceInfo, statusCode: number);
    get requestId(): string;
    get userId(): string;
    get userDevice(): SDKDeviceInfo;
    get statusCode(): number;
    copy(requestId?: string, userId?: string, sessionToken?: string, session?: ApiSessionV1Session, sessionJwt?: string, user?: ApiUserV1User, userDevice?: SDKDeviceInfo, statusCode?: number): PasswordsSessionResetResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace PasswordsSessionResetResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => PasswordsSessionResetResponse;
    }
}
export declare class PasswordsStrengthCheckRequest {
    constructor(password: string, email?: Nullable<string>);
    get password(): string;
    get email(): Nullable<string>;
    copy(password?: string, email?: Nullable<string>): PasswordsStrengthCheckRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace PasswordsStrengthCheckRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => PasswordsStrengthCheckRequest;
    }
}
export declare class PasswordsStrengthCheckResponse implements StytchAPIResponse {
    constructor(requestId: string, validPassword: boolean, score: number, breachedPassword: boolean, feedback: ApiPasswordV1Feedback, strengthPolicy: string, breachDetectionOnCreate: boolean, statusCode: number);
    get requestId(): string;
    get validPassword(): boolean;
    get score(): number;
    get breachedPassword(): boolean;
    get feedback(): ApiPasswordV1Feedback;
    get strengthPolicy(): string;
    get breachDetectionOnCreate(): boolean;
    get statusCode(): number;
    copy(requestId?: string, validPassword?: boolean, score?: number, breachedPassword?: boolean, feedback?: ApiPasswordV1Feedback, strengthPolicy?: string, breachDetectionOnCreate?: boolean, statusCode?: number): PasswordsStrengthCheckResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace PasswordsStrengthCheckResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => PasswordsStrengthCheckResponse;
    }
}
export declare class RevokeConnectedAppResponse implements StytchAPIResponse {
    constructor(requestId: string, statusCode: number);
    get requestId(): string;
    get statusCode(): number;
    copy(requestId?: string, statusCode?: number): RevokeConnectedAppResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace RevokeConnectedAppResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => RevokeConnectedAppResponse;
    }
}
export declare class SCIMRotateTokenCancelRequest {
    constructor(connectionId: string);
    get connectionId(): string;
    copy(connectionId?: string): SCIMRotateTokenCancelRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace SCIMRotateTokenCancelRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => SCIMRotateTokenCancelRequest;
    }
}
export declare class SCIMRotateTokenCancelResponse implements StytchAPIResponse {
    constructor(requestId: string, connection: ApiB2bScimV1SCIMConnection, statusCode: number);
    get requestId(): string;
    get connection(): ApiB2bScimV1SCIMConnection;
    get statusCode(): number;
    copy(requestId?: string, connection?: ApiB2bScimV1SCIMConnection, statusCode?: number): SCIMRotateTokenCancelResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace SCIMRotateTokenCancelResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => SCIMRotateTokenCancelResponse;
    }
}
export declare class SCIMRotateTokenCompleteRequest {
    constructor(connectionId: string);
    get connectionId(): string;
    copy(connectionId?: string): SCIMRotateTokenCompleteRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace SCIMRotateTokenCompleteRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => SCIMRotateTokenCompleteRequest;
    }
}
export declare class SCIMRotateTokenCompleteResponse implements StytchAPIResponse {
    constructor(requestId: string, connection: ApiB2bScimV1SCIMConnection, statusCode: number);
    get requestId(): string;
    get connection(): ApiB2bScimV1SCIMConnection;
    get statusCode(): number;
    copy(requestId?: string, connection?: ApiB2bScimV1SCIMConnection, statusCode?: number): SCIMRotateTokenCompleteResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace SCIMRotateTokenCompleteResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => SCIMRotateTokenCompleteResponse;
    }
}
export declare class SCIMRotateTokenStartRequest {
    constructor(connectionId: string);
    get connectionId(): string;
    copy(connectionId?: string): SCIMRotateTokenStartRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace SCIMRotateTokenStartRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => SCIMRotateTokenStartRequest;
    }
}
export declare class SCIMRotateTokenStartResponse implements StytchAPIResponse {
    constructor(requestId: string, connection: ApiB2bScimV1SCIMConnectionWithNextToken, statusCode: number);
    get requestId(): string;
    get connection(): ApiB2bScimV1SCIMConnectionWithNextToken;
    get statusCode(): number;
    copy(requestId?: string, connection?: ApiB2bScimV1SCIMConnectionWithNextToken, statusCode?: number): SCIMRotateTokenStartResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace SCIMRotateTokenStartResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => SCIMRotateTokenStartResponse;
    }
}
export declare class SDKDeviceInfo {
    constructor(ipAddress?: Nullable<string>, ipAddressDetails?: Nullable<ApiDeviceHistoryV1DeviceAttributeDetails>, ipGeoCity?: Nullable<string>, ipGeoRegion?: Nullable<string>, ipGeoCountry?: Nullable<string>, ipGeoCountryDetails?: Nullable<ApiDeviceHistoryV1DeviceAttributeDetails>);
    get ipAddress(): Nullable<string>;
    get ipAddressDetails(): Nullable<ApiDeviceHistoryV1DeviceAttributeDetails>;
    get ipGeoCity(): Nullable<string>;
    get ipGeoRegion(): Nullable<string>;
    get ipGeoCountry(): Nullable<string>;
    get ipGeoCountryDetails(): Nullable<ApiDeviceHistoryV1DeviceAttributeDetails>;
    copy(ipAddress?: Nullable<string>, ipAddressDetails?: Nullable<ApiDeviceHistoryV1DeviceAttributeDetails>, ipGeoCity?: Nullable<string>, ipGeoRegion?: Nullable<string>, ipGeoCountry?: Nullable<string>, ipGeoCountryDetails?: Nullable<ApiDeviceHistoryV1DeviceAttributeDetails>): SDKDeviceInfo;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace SDKDeviceInfo {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => SDKDeviceInfo;
    }
}
export declare class SDKSIWEParams {
    constructor(uri: string, resources: KtList<string>, chainId?: Nullable<string>, statement?: Nullable<string>, issuedAt?: Nullable<any>/* Nullable<Instant> */, notBefore?: Nullable<string>, messageRequestId?: Nullable<string>);
    get uri(): string;
    get resources(): KtList<string>;
    get chainId(): Nullable<string>;
    get statement(): Nullable<string>;
    get issuedAt(): Nullable<any>/* Nullable<Instant> */;
    get notBefore(): Nullable<string>;
    get messageRequestId(): Nullable<string>;
    copy(uri?: string, resources?: KtList<string>, chainId?: Nullable<string>, statement?: Nullable<string>, issuedAt?: Nullable<any>/* Nullable<Instant> */, notBefore?: Nullable<string>, messageRequestId?: Nullable<string>): SDKSIWEParams;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace SDKSIWEParams {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => SDKSIWEParams;
    }
}
export declare class SSOConnection {
    constructor(connectionId: string, displayName: string, identityProvider: string);
    get connectionId(): string;
    get displayName(): string;
    get identityProvider(): string;
    copy(connectionId?: string, displayName?: string, identityProvider?: string): SSOConnection;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace SSOConnection {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => SSOConnection;
    }
}
export declare class SessionsAccessTokenExchangeRequest {
    constructor(accessToken: string, sessionDurationMinutes: number);
    get accessToken(): string;
    get sessionDurationMinutes(): number;
    copy(accessToken?: string, sessionDurationMinutes?: number): SessionsAccessTokenExchangeRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace SessionsAccessTokenExchangeRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => SessionsAccessTokenExchangeRequest;
    }
}
export declare class SessionsAccessTokenExchangeResponse implements StytchAPIResponse/*, AuthenticatedResponse */ {
    constructor(requestId: string, session: ApiSessionV1Session, sessionToken: string, sessionJwt: string, user: ApiUserV1User, statusCode: number);
    get requestId(): string;
    get statusCode(): number;
    copy(requestId?: string, session?: ApiSessionV1Session, sessionToken?: string, sessionJwt?: string, user?: ApiUserV1User, statusCode?: number): SessionsAccessTokenExchangeResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace SessionsAccessTokenExchangeResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => SessionsAccessTokenExchangeResponse;
    }
}
export declare class SessionsAttestRequest {
    constructor(profileId: string, token: string, sessionDurationMinutes?: Nullable<number>);
    get profileId(): string;
    get token(): string;
    get sessionDurationMinutes(): Nullable<number>;
    copy(profileId?: string, token?: string, sessionDurationMinutes?: Nullable<number>): SessionsAttestRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace SessionsAttestRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => SessionsAttestRequest;
    }
}
export declare class SessionsAttestResponse implements StytchAPIResponse/*, AuthenticatedResponse */ {
    constructor(requestId: string, userId: string, session: ApiSessionV1Session, sessionToken: string, sessionJwt: string, user: ApiUserV1User, statusCode: number);
    get requestId(): string;
    get userId(): string;
    get statusCode(): number;
    copy(requestId?: string, userId?: string, session?: ApiSessionV1Session, sessionToken?: string, sessionJwt?: string, user?: ApiUserV1User, statusCode?: number): SessionsAttestResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace SessionsAttestResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => SessionsAttestResponse;
    }
}
export declare class SessionsAuthenticateRequest {
    constructor(sessionDurationMinutes?: Nullable<number>);
    get sessionDurationMinutes(): Nullable<number>;
    copy(sessionDurationMinutes?: Nullable<number>): SessionsAuthenticateRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace SessionsAuthenticateRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => SessionsAuthenticateRequest;
    }
}
export declare class SessionsAuthenticateResponse implements StytchAPIResponse/*, AuthenticatedResponse */ {
    constructor(requestId: string, session: ApiSessionV1Session, sessionToken: string, sessionJwt: string, user: ApiUserV1User, statusCode: number);
    get requestId(): string;
    get statusCode(): number;
    copy(requestId?: string, session?: ApiSessionV1Session, sessionToken?: string, sessionJwt?: string, user?: ApiUserV1User, statusCode?: number): SessionsAuthenticateResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace SessionsAuthenticateResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => SessionsAuthenticateResponse;
    }
}
export declare class SessionsRevokeResponse implements StytchAPIResponse {
    constructor(requestId: string, statusCode: number);
    get requestId(): string;
    get statusCode(): number;
    copy(requestId?: string, statusCode?: number): SessionsRevokeResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace SessionsRevokeResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => SessionsRevokeResponse;
    }
}
export declare class TOTPsAuthenticateRequest {
    constructor(totpCode: string, sessionDurationMinutes: number, dfpTelemetryId?: Nullable<string>, captchaToken?: Nullable<string>);
    get totpCode(): string;
    get sessionDurationMinutes(): number;
    get dfpTelemetryId(): Nullable<string>;
    get captchaToken(): Nullable<string>;
    copy(totpCode?: string, sessionDurationMinutes?: number, dfpTelemetryId?: Nullable<string>, captchaToken?: Nullable<string>): TOTPsAuthenticateRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace TOTPsAuthenticateRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => TOTPsAuthenticateRequest;
    }
}
export declare class TOTPsAuthenticateResponse implements StytchAPIResponse/*, AuthenticatedResponse */ {
    constructor(requestId: string, userId: string, totpId: string, sessionToken: string, sessionJwt: string, session: ApiSessionV1Session, user: ApiUserV1User, userDevice: SDKDeviceInfo, statusCode: number);
    get requestId(): string;
    get userId(): string;
    get totpId(): string;
    get userDevice(): SDKDeviceInfo;
    get statusCode(): number;
    copy(requestId?: string, userId?: string, totpId?: string, sessionToken?: string, sessionJwt?: string, session?: ApiSessionV1Session, user?: ApiUserV1User, userDevice?: SDKDeviceInfo, statusCode?: number): TOTPsAuthenticateResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace TOTPsAuthenticateResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => TOTPsAuthenticateResponse;
    }
}
export declare class TOTPsCreateRequest {
    constructor(expirationMinutes?: Nullable<number>);
    get expirationMinutes(): Nullable<number>;
    copy(expirationMinutes?: Nullable<number>): TOTPsCreateRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace TOTPsCreateRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => TOTPsCreateRequest;
    }
}
export declare class TOTPsCreateResponse implements StytchAPIResponse {
    constructor(requestId: string, totpId: string, secret: string, qrCode: string, recoveryCodes: KtList<string>, user: ApiUserV1User, userId: string, statusCode: number);
    get requestId(): string;
    get totpId(): string;
    get secret(): string;
    get qrCode(): string;
    get recoveryCodes(): KtList<string>;
    get user(): ApiUserV1User;
    get userId(): string;
    get statusCode(): number;
    copy(requestId?: string, totpId?: string, secret?: string, qrCode?: string, recoveryCodes?: KtList<string>, user?: ApiUserV1User, userId?: string, statusCode?: number): TOTPsCreateResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace TOTPsCreateResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => TOTPsCreateResponse;
    }
}
export declare class TOTPsGetRecoveryCodesResponse implements StytchAPIResponse {
    constructor(requestId: string, userId: string, totps: KtList<ApiTotpV1TOTP>, statusCode: number);
    get requestId(): string;
    get userId(): string;
    get totps(): KtList<ApiTotpV1TOTP>;
    get statusCode(): number;
    copy(requestId?: string, userId?: string, totps?: KtList<ApiTotpV1TOTP>, statusCode?: number): TOTPsGetRecoveryCodesResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace TOTPsGetRecoveryCodesResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => TOTPsGetRecoveryCodesResponse;
    }
}
export declare class TOTPsRecoverRequest {
    constructor(recoveryCode: string, sessionDurationMinutes: number, dfpTelemetryId?: Nullable<string>, captchaToken?: Nullable<string>);
    get recoveryCode(): string;
    get sessionDurationMinutes(): number;
    get dfpTelemetryId(): Nullable<string>;
    get captchaToken(): Nullable<string>;
    copy(recoveryCode?: string, sessionDurationMinutes?: number, dfpTelemetryId?: Nullable<string>, captchaToken?: Nullable<string>): TOTPsRecoverRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace TOTPsRecoverRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => TOTPsRecoverRequest;
    }
}
export declare class TOTPsRecoverResponse implements StytchAPIResponse/*, AuthenticatedResponse */ {
    constructor(requestId: string, userId: string, totpId: string, sessionToken: string, sessionJwt: string, session: ApiSessionV1Session, user: ApiUserV1User, userDevice: SDKDeviceInfo, statusCode: number);
    get requestId(): string;
    get userId(): string;
    get totpId(): string;
    get userDevice(): SDKDeviceInfo;
    get statusCode(): number;
    copy(requestId?: string, userId?: string, totpId?: string, sessionToken?: string, sessionJwt?: string, session?: ApiSessionV1Session, user?: ApiUserV1User, userDevice?: SDKDeviceInfo, statusCode?: number): TOTPsRecoverResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace TOTPsRecoverResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => TOTPsRecoverResponse;
    }
}
export declare class UpdateMeRequest {
    constructor(name?: Nullable<ApiUserV1Name>, emails?: Nullable<KtList<ApiUserV1EmailString>>, phoneNumbers?: Nullable<KtList<ApiUserV1PhoneNumberString>>, cryptoWallets?: Nullable<KtList<ApiUserV1CryptoWalletString>>, trustedMetadata?: Nullable<KtMap<string, any/* JsonElement */>>, untrustedMetadata?: Nullable<KtMap<string, any/* JsonElement */>>);
    get name(): Nullable<ApiUserV1Name>;
    get emails(): Nullable<KtList<ApiUserV1EmailString>>;
    get phoneNumbers(): Nullable<KtList<ApiUserV1PhoneNumberString>>;
    get cryptoWallets(): Nullable<KtList<ApiUserV1CryptoWalletString>>;
    get trustedMetadata(): Nullable<KtMap<string, any/* JsonElement */>>;
    get untrustedMetadata(): Nullable<KtMap<string, any/* JsonElement */>>;
    copy(name?: Nullable<ApiUserV1Name>, emails?: Nullable<KtList<ApiUserV1EmailString>>, phoneNumbers?: Nullable<KtList<ApiUserV1PhoneNumberString>>, cryptoWallets?: Nullable<KtList<ApiUserV1CryptoWalletString>>, trustedMetadata?: Nullable<KtMap<string, any/* JsonElement */>>, untrustedMetadata?: Nullable<KtMap<string, any/* JsonElement */>>): UpdateMeRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace UpdateMeRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => UpdateMeRequest;
    }
}
export declare class UpdateMeResponse implements StytchAPIResponse {
    constructor(requestId: string, userId: string, emails: KtList<ApiUserV1Email>, phoneNumbers: KtList<ApiUserV1PhoneNumber>, cryptoWallets: KtList<ApiUserV1CryptoWallet>, user: ApiUserV1User, statusCode: number);
    get requestId(): string;
    get userId(): string;
    get emails(): KtList<ApiUserV1Email>;
    get phoneNumbers(): KtList<ApiUserV1PhoneNumber>;
    get cryptoWallets(): KtList<ApiUserV1CryptoWallet>;
    get user(): ApiUserV1User;
    get statusCode(): number;
    copy(requestId?: string, userId?: string, emails?: KtList<ApiUserV1Email>, phoneNumbers?: KtList<ApiUserV1PhoneNumber>, cryptoWallets?: KtList<ApiUserV1CryptoWallet>, user?: ApiUserV1User, statusCode?: number): UpdateMeResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace UpdateMeResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => UpdateMeResponse;
    }
}
export declare class UserSearchByEmailRequest {
    constructor(email: string);
    get email(): string;
    copy(email?: string): UserSearchByEmailRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace UserSearchByEmailRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => UserSearchByEmailRequest;
    }
}
export declare class UserSearchByEmailResponse implements StytchAPIResponse {
    constructor(requestId: string, statusCode: number, userType?: Nullable<any>/* Nullable<UserSearchByEmailResponseUserType> */);
    get requestId(): string;
    get statusCode(): number;
    get userType(): Nullable<any>/* Nullable<UserSearchByEmailResponseUserType> */;
    copy(requestId?: string, statusCode?: number, userType?: Nullable<any>/* Nullable<UserSearchByEmailResponseUserType> */): UserSearchByEmailResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace UserSearchByEmailResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => UserSearchByEmailResponse;
    }
}
export declare class WebAuthnAuthenticateRequest {
    constructor(publicKeyCredential: string, sessionDurationMinutes?: Nullable<number>, dfpTelemetryId?: Nullable<string>, captchaToken?: Nullable<string>);
    get publicKeyCredential(): string;
    get sessionDurationMinutes(): Nullable<number>;
    get dfpTelemetryId(): Nullable<string>;
    get captchaToken(): Nullable<string>;
    copy(publicKeyCredential?: string, sessionDurationMinutes?: Nullable<number>, dfpTelemetryId?: Nullable<string>, captchaToken?: Nullable<string>): WebAuthnAuthenticateRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace WebAuthnAuthenticateRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => WebAuthnAuthenticateRequest;
    }
}
export declare class WebAuthnAuthenticateResponse implements StytchAPIResponse/*, AuthenticatedResponse */ {
    constructor(requestId: string, userId: string, webauthnRegistrationId: string, sessionToken: string, session: ApiSessionV1Session, sessionJwt: string, user: ApiUserV1User, userDevice: SDKDeviceInfo, statusCode: number);
    get requestId(): string;
    get userId(): string;
    get webauthnRegistrationId(): string;
    get userDevice(): SDKDeviceInfo;
    get statusCode(): number;
    copy(requestId?: string, userId?: string, webauthnRegistrationId?: string, sessionToken?: string, session?: ApiSessionV1Session, sessionJwt?: string, user?: ApiUserV1User, userDevice?: SDKDeviceInfo, statusCode?: number): WebAuthnAuthenticateResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace WebAuthnAuthenticateResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => WebAuthnAuthenticateResponse;
    }
}
export declare class WebAuthnAuthenticateStartSecondaryRequest {
    constructor(domain: string, returnPasskeyCredentialOptions?: Nullable<boolean>);
    get domain(): string;
    get returnPasskeyCredentialOptions(): Nullable<boolean>;
    copy(domain?: string, returnPasskeyCredentialOptions?: Nullable<boolean>): WebAuthnAuthenticateStartSecondaryRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace WebAuthnAuthenticateStartSecondaryRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => WebAuthnAuthenticateStartSecondaryRequest;
    }
}
export declare class WebAuthnAuthenticateStartSecondaryResponse implements StytchAPIResponse {
    constructor(requestId: string, userId: string, publicKeyCredentialRequestOptions: string, statusCode: number);
    get requestId(): string;
    get userId(): string;
    get publicKeyCredentialRequestOptions(): string;
    get statusCode(): number;
    copy(requestId?: string, userId?: string, publicKeyCredentialRequestOptions?: string, statusCode?: number): WebAuthnAuthenticateStartSecondaryResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace WebAuthnAuthenticateStartSecondaryResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => WebAuthnAuthenticateStartSecondaryResponse;
    }
}
export declare class WebAuthnRegisterRequest {
    constructor(publicKeyCredential: string, sessionDurationMinutes?: Nullable<number>);
    get publicKeyCredential(): string;
    get sessionDurationMinutes(): Nullable<number>;
    copy(publicKeyCredential?: string, sessionDurationMinutes?: Nullable<number>): WebAuthnRegisterRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace WebAuthnRegisterRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => WebAuthnRegisterRequest;
    }
}
export declare class WebAuthnRegisterResponse implements StytchAPIResponse/*, AuthenticatedResponse */ {
    constructor(requestId: string, userId: string, webauthnRegistrationId: string, sessionToken: string, sessionJwt: string, user: ApiUserV1User, session: ApiSessionV1Session, statusCode: number);
    get requestId(): string;
    get userId(): string;
    get webauthnRegistrationId(): string;
    get statusCode(): number;
    copy(requestId?: string, userId?: string, webauthnRegistrationId?: string, sessionToken?: string, sessionJwt?: string, user?: ApiUserV1User, session?: ApiSessionV1Session, statusCode?: number): WebAuthnRegisterResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace WebAuthnRegisterResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => WebAuthnRegisterResponse;
    }
}
export declare class WebAuthnRegisterStartRequest {
    constructor(domain: string, userAgent?: Nullable<string>, authenticatorType?: Nullable<string>, returnPasskeyCredentialOptions?: Nullable<boolean>, overrideId?: Nullable<string>, overrideName?: Nullable<string>, overrideDisplayName?: Nullable<string>);
    get domain(): string;
    get userAgent(): Nullable<string>;
    get authenticatorType(): Nullable<string>;
    get returnPasskeyCredentialOptions(): Nullable<boolean>;
    get overrideId(): Nullable<string>;
    get overrideName(): Nullable<string>;
    get overrideDisplayName(): Nullable<string>;
    copy(domain?: string, userAgent?: Nullable<string>, authenticatorType?: Nullable<string>, returnPasskeyCredentialOptions?: Nullable<boolean>, overrideId?: Nullable<string>, overrideName?: Nullable<string>, overrideDisplayName?: Nullable<string>): WebAuthnRegisterStartRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace WebAuthnRegisterStartRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => WebAuthnRegisterStartRequest;
    }
}
export declare class WebAuthnRegisterStartResponse implements StytchAPIResponse {
    constructor(requestId: string, userId: string, publicKeyCredentialCreationOptions: string, statusCode: number);
    get requestId(): string;
    get userId(): string;
    get publicKeyCredentialCreationOptions(): string;
    get statusCode(): number;
    copy(requestId?: string, userId?: string, publicKeyCredentialCreationOptions?: string, statusCode?: number): WebAuthnRegisterStartResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace WebAuthnRegisterStartResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => WebAuthnRegisterStartResponse;
    }
}
export declare class WebAuthnUpdateRequest {
    constructor(name: string);
    get name(): string;
    copy(name?: string): WebAuthnUpdateRequest;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
}
export declare namespace WebAuthnUpdateRequest {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => WebAuthnUpdateRequest;
    }
}
export declare class WebAuthnUpdateResponse implements StytchAPIResponse {
    constructor(requestId: string, webauthnRegistration: ApiUserV1WebAuthnRegistration, statusCode: number);
    get requestId(): string;
    get webauthnRegistration(): ApiUserV1WebAuthnRegistration;
    get statusCode(): number;
    copy(requestId?: string, webauthnRegistration?: ApiUserV1WebAuthnRegistration, statusCode?: number): WebAuthnUpdateResponse;
    toString(): string;
    hashCode(): number;
    equals(other: Nullable<any>): boolean;
    readonly __doNotUseOrImplementIt: StytchAPIResponse["__doNotUseOrImplementIt"];
}
export declare namespace WebAuthnUpdateResponse {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => WebAuthnUpdateResponse;
    }
}
export declare interface StytchB2B extends StytchClient {
    readonly session: B2BSessionsClient;
    readonly magicLinks: B2BMagicLinksClient;
    readonly otp: B2BOtpClient;
    readonly passwords: B2BPasswordsClient;
    readonly totp: B2BTOTPClient;
    readonly discovery: B2BDiscoveryClient;
    readonly members: B2BMembersClient;
    readonly organizations: B2BOrganizationsClient;
    readonly recoveryCodes: B2BRecoveryCodesClient;
    readonly scim: B2BSCIMClient;
    readonly oauth: B2BOAuthClient;
    readonly sso: B2BSSOClient;
    readonly rbac: B2BRBACClient;
    readonly dfp: DFPClient;
    readonly authenticationStateFlow: any/* StateFlow<B2BAuthenticationState> */;
    authenticationStateObserver(callback: (p0: B2BAuthenticationState) => void): JsCleanup;
    authenticate(url: string, sessionDurationMinutes: Nullable<number>): Promise<DeeplinkAuthenticationStatus>;
    getPKCECodePair(): Promise<Nullable<any>/* Nullable<PKCECodePair> */>;
    parseDeeplink(url: string): Nullable<DeeplinkToken>;
    readonly __doNotUseOrImplementIt: {
        readonly "com.stytch.sdk.b2b.StytchB2B": unique symbol;
    } & StytchClient["__doNotUseOrImplementIt"];
}
export declare function createStytchB2B(configuration: StytchClientConfiguration): StytchB2B;
export declare abstract class B2BAuthenticationState /* implements AuthenticationState */ {
    protected constructor();
}
export declare namespace B2BAuthenticationState {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BAuthenticationState;
    }
    class Loading extends B2BAuthenticationState.$metadata$.constructor {
        constructor();
    }
    namespace Loading {
        /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
        namespace $metadata$ {
            const constructor: abstract new () => Loading;
        }
    }
    class Unauthenticated extends B2BAuthenticationState.$metadata$.constructor {
        constructor();
    }
    namespace Unauthenticated {
        /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
        namespace $metadata$ {
            const constructor: abstract new () => Unauthenticated;
        }
    }
    class Authenticated extends B2BAuthenticationState.$metadata$.constructor {
        constructor(member: ApiOrganizationV1Member, memberSession: ApiB2bSessionV1MemberSession, organization: ApiOrganizationV1Organization, sessionToken: string, sessionJwt: string);
        get member(): ApiOrganizationV1Member;
        get memberSession(): ApiB2bSessionV1MemberSession;
        get organization(): ApiOrganizationV1Organization;
        get sessionToken(): string;
        get sessionJwt(): string;
    }
    namespace Authenticated {
        /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
        namespace $metadata$ {
            const constructor: abstract new () => Authenticated;
        }
    }
}
export declare abstract class B2BTokenType {
    private constructor();
    static get MULTI_TENANT_MAGIC_LINKS(): B2BTokenType & {
        get name(): "MULTI_TENANT_MAGIC_LINKS";
        get ordinal(): 0;
    };
    static get MULTI_TENANT_PASSWORDS(): B2BTokenType & {
        get name(): "MULTI_TENANT_PASSWORDS";
        get ordinal(): 1;
    };
    static get DISCOVERY(): B2BTokenType & {
        get name(): "DISCOVERY";
        get ordinal(): 2;
    };
    static get SSO(): B2BTokenType & {
        get name(): "SSO";
        get ordinal(): 3;
    };
    static get OAUTH(): B2BTokenType & {
        get name(): "OAUTH";
        get ordinal(): 4;
    };
    static get DISCOVERY_OAUTH(): B2BTokenType & {
        get name(): "DISCOVERY_OAUTH";
        get ordinal(): 5;
    };
    static get UNKNOWN(): B2BTokenType & {
        get name(): "UNKNOWN";
        get ordinal(): 6;
    };
    get name(): "MULTI_TENANT_MAGIC_LINKS" | "MULTI_TENANT_PASSWORDS" | "DISCOVERY" | "SSO" | "OAUTH" | "DISCOVERY_OAUTH" | "UNKNOWN";
    get ordinal(): 0 | 1 | 2 | 3 | 4 | 5 | 6;
    static values(): Array<B2BTokenType>;
    static valueOf(value: string): B2BTokenType;
}
export declare namespace B2BTokenType {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => B2BTokenType;
    }
}
export declare abstract class DeeplinkAuthenticationStatus {
    protected constructor();
}
export declare namespace DeeplinkAuthenticationStatus {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => DeeplinkAuthenticationStatus;
    }
    class Authenticated extends DeeplinkAuthenticationStatus.$metadata$.constructor {
        constructor(response: any/* AuthenticatedResponse */);
    }
    namespace Authenticated {
        /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
        namespace $metadata$ {
            const constructor: abstract new () => Authenticated;
        }
    }
    class ManualHandlingRequired extends DeeplinkAuthenticationStatus.$metadata$.constructor {
        constructor(token: string);
    }
    namespace ManualHandlingRequired {
        /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
        namespace $metadata$ {
            const constructor: abstract new () => ManualHandlingRequired;
        }
    }
    class UnknownDeeplink extends DeeplinkAuthenticationStatus.$metadata$.constructor {
        constructor(url: string);
    }
    namespace UnknownDeeplink {
        /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
        namespace $metadata$ {
            const constructor: abstract new () => UnknownDeeplink;
        }
    }
}
export declare class DeeplinkToken {
    constructor(type: B2BTokenType, token: string);
    get type(): B2BTokenType;
    get token(): string;
}
export declare namespace DeeplinkToken {
    /** @deprecated $metadata$ is used for internal purposes, please don't use it in your code, because it can be removed at any moment */
    namespace $metadata$ {
        const constructor: abstract new () => DeeplinkToken;
    }
}
export declare interface DFPClient {
    getTelemetryId(): Promise<string>;
    readonly __doNotUseOrImplementIt: {
        readonly "com.stytch.sdk.b2b.dfp.DFPClient": unique symbol;
    };
}
export declare interface B2BDiscoveryClient {
    readonly organizations: B2BDiscoveryOrganizationsClient;
    readonly intermediateSessions: B2BDiscoveryIntermediateSessionsClient;
    readonly passwords: B2BDiscoveryPasswordsClient;
    readonly __doNotUseOrImplementIt: {
        readonly "com.stytch.sdk.b2b.discovery.B2BDiscoveryClient": unique symbol;
    };
}
export declare interface B2BDiscoveryOrganizationsClient {
    list(request: B2BDiscoveryOrganizationsParameters): Promise<B2BDiscoveryOrganizationsResponse>;
    create(request: B2BDiscoveryOrganizationsCreateParameters): Promise<B2BDiscoveryOrganizationsCreateResponse>;
    readonly __doNotUseOrImplementIt: {
        readonly "com.stytch.sdk.b2b.discovery.B2BDiscoveryOrganizationsClient": unique symbol;
    };
}
export declare interface B2BDiscoveryIntermediateSessionsClient {
    exchange(request: B2BDiscoveryIntermediateSessionsExchangeParameters): Promise<B2BDiscoveryIntermediateSessionsExchangeResponse>;
    readonly __doNotUseOrImplementIt: {
        readonly "com.stytch.sdk.b2b.discovery.B2BDiscoveryIntermediateSessionsClient": unique symbol;
    };
}
export declare interface B2BDiscoveryPasswordsClient {
    authenticate(request: B2BPasswordDiscoveryAuthenticateParameters): Promise<B2BPasswordDiscoveryAuthenticateResponse>;
    resetStart(request: B2BDiscoveryPasswordResetStartParameters): Promise<B2BDiscoveryPasswordResetStartResponse>;
    reset(request: B2BDiscoveryPasswordResetParameters): Promise<B2BDiscoveryPasswordResetResponse>;
    readonly __doNotUseOrImplementIt: {
        readonly "com.stytch.sdk.b2b.discovery.B2BDiscoveryPasswordsClient": unique symbol;
    };
}
export declare interface B2BMagicLinksClient {
    readonly email: B2BEmailMagicLinksClient;
    readonly discovery: B2BMagicLinksDiscoveryClient;
    authenticate(request: B2BMagicLinksAuthenticateParameters): Promise<B2BMagicLinksAuthenticateResponse>;
    readonly __doNotUseOrImplementIt: {
        readonly "com.stytch.sdk.b2b.magicLinks.B2BMagicLinksClient": unique symbol;
    };
}
export declare interface B2BEmailMagicLinksClient {
    loginOrSignup(request: B2BMagicLinksLoginOrSignupParameters): Promise<B2BMagicLinksLoginOrSignupResponse>;
    invite(request: B2BMagicLinksInviteParameters): Promise<B2BMagicLinksInviteResponse>;
    readonly __doNotUseOrImplementIt: {
        readonly "com.stytch.sdk.b2b.magicLinks.B2BEmailMagicLinksClient": unique symbol;
    };
}
export declare interface B2BMagicLinksDiscoveryClient {
    emailSend(request: B2BMagicLinksDiscoveryEmailSendParameters): Promise<B2BMagicLinksDiscoveryEmailSendResponse>;
    authenticate(request: B2BMagicLinksDiscoveryAuthenticateParameters): Promise<B2BMagicLinksDiscoveryAuthenticateResponse>;
    readonly __doNotUseOrImplementIt: {
        readonly "com.stytch.sdk.b2b.magicLinks.B2BMagicLinksDiscoveryClient": unique symbol;
    };
}
export declare interface B2BMembersClient {
    readonly admin: B2BMembersAdminClient;
    me(): Promise<B2BGetMeResponse>;
    update(request: OrganizationsMemberUpdateParameters): Promise<OrganizationsMemberUpdateResponse>;
    search(request: OrganizationsMemberSearchParameters): Promise<OrganizationsMemberSearchResponse>;
    create(request: OrganizationsMemberCreateParameters): Promise<OrganizationsMemberCreateResponse>;
    deleteMFAPhoneNumber(): Promise<OrganizationsMemberDeleteMFAPhoneNumberResponse>;
    deleteMFATOTP(): Promise<OrganizationsMemberDeleteMFATOTPResponse>;
    startEmailUpdate(request: OrganizationsMemberStartEmailUpdateParameters): Promise<OrganizationsMemberStartEmailUpdateResponse>;
    unlinkRetiredEmail(request: OrganizationsMemberUnlinkRetiredEmailParameters): Promise<OrganizationsMemberUnlinkRetiredEmailResponse>;
    readonly __doNotUseOrImplementIt: {
        readonly "com.stytch.sdk.b2b.members.B2BMembersClient": unique symbol;
    };
}
export declare interface B2BMembersAdminClient {
    update(memberId: string, request: OrganizationsAdminMemberUpdateParameters): Promise<OrganizationsAdminMemberUpdateResponse>;
    delete(memberId: string): Promise<OrganizationsAdminMemberDeleteResponse>;
    deleteMFAPhoneNumber(memberId: string): Promise<OrganizationsAdminMemberDeleteMFAPhoneNumberResponse>;
    deleteMFATOTP(memberId: string): Promise<OrganizationsAdminMemberDeleteMFATOTPResponse>;
    deletePassword(memberPasswordId: string): Promise<OrganizationsAdminMemberDeletePasswordResponse>;
    reactivate(memberId: string): Promise<OrganizationsAdminMemberReactivateResponse>;
    startEmailUpdate(memberId: string, request: OrganizationsAdminMemberStartEmailUpdateParameters): Promise<OrganizationsAdminMemberStartEmailUpdateResponse>;
    unlinkRetiredEmail(memberId: string, request: OrganizationsAdminMemberUnlinkRetiredEmailParameters): Promise<OrganizationsAdminMemberUnlinkRetiredEmailResponse>;
    readonly __doNotUseOrImplementIt: {
        readonly "com.stytch.sdk.b2b.members.B2BMembersAdminClient": unique symbol;
    };
}
export declare interface B2BOAuthClient {
    readonly google: B2BOAuthProviderClient;
    readonly microsoft: B2BOAuthProviderClient;
    readonly hubspot: B2BOAuthProviderClient;
    readonly slack: B2BOAuthProviderClient;
    readonly github: B2BOAuthProviderClient;
    readonly discovery: B2BOAuthDiscoveryClient;
    authenticate(request: B2BOAuthAuthenticateParameters): Promise<B2BOAuthAuthenticateResponse>;
    readonly __doNotUseOrImplementIt: {
        readonly "com.stytch.sdk.b2b.oauth.B2BOAuthClient": unique symbol;
    };
}
export declare interface B2BOAuthProviderClient {
    start(parameters: B2BOAuthStartParameters): Promise<any/* AuthenticatedResponse */>;
    readonly discovery: B2BOAuthProviderDiscoveryClient;
    readonly __doNotUseOrImplementIt: {
        readonly "com.stytch.sdk.b2b.oauth.B2BOAuthProviderClient": unique symbol;
    };
}
export declare interface B2BOAuthProviderDiscoveryClient {
    start(parameters: B2BOAuthDiscoveryStartParameters): Promise<B2BOAuthDiscoveryAuthenticateResponse>;
    readonly __doNotUseOrImplementIt: {
        readonly "com.stytch.sdk.b2b.oauth.B2BOAuthProviderDiscoveryClient": unique symbol;
    };
}
export declare interface B2BOAuthDiscoveryClient {
    authenticate(request: B2BOAuthDiscoveryAuthenticateParameters): Promise<B2BOAuthDiscoveryAuthenticateResponse>;
    readonly __doNotUseOrImplementIt: {
        readonly "com.stytch.sdk.b2b.oauth.B2BOAuthDiscoveryClient": unique symbol;
    };
}
export declare interface B2BOrganizationsClient {
    get(): Promise<B2BOrganizationsGetResponse>;
    update(request: B2BOrganizationsUpdateParameters): Promise<B2BOrganizationsUpdateResponse>;
    delete(): Promise<B2BOrganizationsDeleteResponse>;
    readonly __doNotUseOrImplementIt: {
        readonly "com.stytch.sdk.b2b.organizations.B2BOrganizationsClient": unique symbol;
    };
}
export declare interface B2BOtpClient {
    readonly sms: B2BSmsOtpClient;
    readonly email: B2BEmailOtpClient;
    readonly __doNotUseOrImplementIt: {
        readonly "com.stytch.sdk.b2b.otp.B2BOtpClient": unique symbol;
    };
}
export declare interface B2BSmsOtpClient {
    send(request: B2BOTPsSMSSendParameters): Promise<B2BOTPsSMSSendResponse>;
    authenticate(request: B2BOTPsSMSAuthenticateParameters): Promise<B2BOTPsSMSAuthenticateResponse>;
    readonly __doNotUseOrImplementIt: {
        readonly "com.stytch.sdk.b2b.otp.B2BSmsOtpClient": unique symbol;
    };
}
export declare interface B2BEmailOtpClient {
    readonly discovery: B2BEmailOtpDiscoveryClient;
    loginOrSignup(request: B2BOTPsEmailLoginOrSignupParameters): Promise<B2BOTPsEmailLoginOrSignupResponse>;
    authenticate(request: B2BOTPsEmailAuthenticateParameters): Promise<B2BOTPsEmailAuthenticateResponse>;
    readonly __doNotUseOrImplementIt: {
        readonly "com.stytch.sdk.b2b.otp.B2BEmailOtpClient": unique symbol;
    };
}
export declare interface B2BEmailOtpDiscoveryClient {
    send(request: B2BOTPsEmailDiscoverySendParameters): Promise<B2BOTPsEmailDiscoverySendResponse>;
    authenticate(request: B2BOTPsEmailDiscoveryAuthenticateParameters): Promise<B2BOTPsEmailDiscoveryAuthenticateResponse>;
    readonly __doNotUseOrImplementIt: {
        readonly "com.stytch.sdk.b2b.otp.B2BEmailOtpDiscoveryClient": unique symbol;
    };
}
export declare interface B2BPasswordsClient {
    readonly email: B2BPasswordsEmailClient;
    readonly existingPassword: B2BPasswordsExistingPasswordClient;
    readonly session: B2BPasswordsSessionClient;
    authenticate(request: B2BPasswordAuthenticateParameters): Promise<B2BPasswordAuthenticateResponse>;
    strengthCheck(request: B2BPasswordStrengthCheckParameters): Promise<B2BPasswordStrengthCheckResponse>;
    readonly __doNotUseOrImplementIt: {
        readonly "com.stytch.sdk.b2b.passwords.B2BPasswordsClient": unique symbol;
    };
}
export declare interface B2BPasswordsEmailClient {
    resetStart(request: B2BPasswordEmailResetStartParameters): Promise<B2BPasswordEmailResetStartResponse>;
    reset(request: B2BPasswordEmailResetParameters): Promise<B2BPasswordEmailResetResponse>;
    readonly __doNotUseOrImplementIt: {
        readonly "com.stytch.sdk.b2b.passwords.B2BPasswordsEmailClient": unique symbol;
    };
}
export declare interface B2BPasswordsExistingPasswordClient {
    reset(request: B2BPasswordExistingPasswordResetParameters): Promise<B2BPasswordExistingPasswordResetResponse>;
    readonly __doNotUseOrImplementIt: {
        readonly "com.stytch.sdk.b2b.passwords.B2BPasswordsExistingPasswordClient": unique symbol;
    };
}
export declare interface B2BPasswordsSessionClient {
    reset(request: B2BPasswordSessionResetParameters): Promise<B2BPasswordSessionResetResponse>;
    readonly __doNotUseOrImplementIt: {
        readonly "com.stytch.sdk.b2b.passwords.B2BPasswordsSessionClient": unique symbol;
    };
}
export declare interface B2BRBACClient {
    isAuthorizedSync(resourceId: string, action: string): boolean;
    isAuthorized(resourceId: string, action: string): Promise<boolean>;
    allPermissions(): Promise<KtMap<string, KtMap<string, boolean>>>;
    readonly __doNotUseOrImplementIt: {
        readonly "com.stytch.sdk.b2b.rbac.B2BRBACClient": unique symbol;
    };
}
export declare interface B2BRecoveryCodesClient {
    get(): Promise<B2BRecoveryCodesGetResponse>;
    recover(request: B2BRecoveryCodesRecoverParameters): Promise<B2BRecoveryCodesRecoverResponse>;
    rotate(request: B2BRecoveryCodesRotateParameters): Promise<B2BRecoveryCodesRotateResponse>;
    readonly __doNotUseOrImplementIt: {
        readonly "com.stytch.sdk.b2b.recoveryCodes.B2BRecoveryCodesClient": unique symbol;
    };
}
export declare interface B2BSCIMClient {
    getConnection(): Promise<B2BGetSCIMConnectionResponse>;
    getConnectionGroups(request: B2BGetSCIMConnectionGroupsParameters): Promise<B2BGetSCIMConnectionGroupsResponse>;
    createConnection(request: B2BSCIMCreateConnectionParameters): Promise<B2BSCIMCreateConnectionResponse>;
    deleteConnection(connectionId: string): Promise<B2BSCIMDeleteConnectionResponse>;
    updateConnection(connectionId: string, request: B2BSCIMUpdateConnectionParameters): Promise<B2BSCIMUpdateConnectionResponse>;
    rotateTokenStart(request: SCIMRotateTokenStartParameters): Promise<SCIMRotateTokenStartResponse>;
    rotateTokenComplete(request: SCIMRotateTokenCompleteParameters): Promise<SCIMRotateTokenCompleteResponse>;
    rotateTokenCancel(request: SCIMRotateTokenCancelParameters): Promise<SCIMRotateTokenCancelResponse>;
    readonly __doNotUseOrImplementIt: {
        readonly "com.stytch.sdk.b2b.scim.B2BSCIMClient": unique symbol;
    };
}
export declare interface B2BSessionsClient {
    authenticate(request: B2BSessionsAuthenticateParameters): Promise<B2BSessionsAuthenticateResponse>;
    revoke(): Promise<B2BSessionsRevokeResponse>;
    exchange(request: B2BSessionsExchangeParameters): Promise<B2BSessionsExchangeResponse>;
    exchangeAccessToken(request: B2BSessionsAccessTokenExchangeParameters): Promise<B2BSessionsAccessTokenExchangeResponse>;
    attest(request: B2BSessionsAttestParameters): Promise<B2BSessionsAttestResponse>;
    readonly __doNotUseOrImplementIt: {
        readonly "com.stytch.sdk.b2b.session.B2BSessionsClient": unique symbol;
    };
}
export declare interface B2BSSOClient {
    start(parameters: B2BSSOStartParameters): Promise<any/* AuthenticatedResponse */>;
    authenticate(request: B2BSSOAuthEnticateParameters): Promise<B2BSSOAuthEnticateResponse>;
    getConnections(): Promise<B2BGetSSOConnectionsResponse>;
    deleteConnection(connectionId: string): Promise<B2BDeleteSSOConnectionResponse>;
    readonly saml: B2BSSOSAMLClient;
    readonly oidc: B2BSSOOIDCClient;
    readonly external: B2BSSOExternalClient;
    readonly __doNotUseOrImplementIt: {
        readonly "com.stytch.sdk.b2b.sso.B2BSSOClient": unique symbol;
    };
}
export declare interface B2BSSOSAMLClient {
    createConnection(request: B2BCreateSAMLConnectionParameters): Promise<B2BCreateSAMLConnectionResponse>;
    updateConnection(connectionId: string, request: B2BUpdateSAMLConnectionParameters): Promise<B2BUpdateSAMLConnectionResponse>;
    updateConnectionByUrl(connectionId: string, request: B2BUpdateSAMLConnectionByURLParameters): Promise<B2BUpdateSAMLConnectionByURLResponse>;
    deleteVerificationCertificate(connectionId: string, certificateId: string): Promise<B2BDeleteSAMLVerificationCertificateResponse>;
    readonly __doNotUseOrImplementIt: {
        readonly "com.stytch.sdk.b2b.sso.B2BSSOSAMLClient": unique symbol;
    };
}
export declare interface B2BSSOOIDCClient {
    createConnection(request: B2BCreateOIDCConnectionParameters): Promise<B2BCreateOIDCConnectionResponse>;
    updateConnection(connectionId: string, request: B2BUpdateOIDCConnectionParameters): Promise<B2BUpdateOIDCConnectionResponse>;
    readonly __doNotUseOrImplementIt: {
        readonly "com.stytch.sdk.b2b.sso.B2BSSOOIDCClient": unique symbol;
    };
}
export declare interface B2BSSOExternalClient {
    createConnection(request: B2BCreateExternalConnectionParameters): Promise<B2BCreateExternalConnectionResponse>;
    updateConnection(connectionId: string, request: B2BUpdateExternalConnectionParameters): Promise<B2BUpdateExternalConnectionResponse>;
    readonly __doNotUseOrImplementIt: {
        readonly "com.stytch.sdk.b2b.sso.B2BSSOExternalClient": unique symbol;
    };
}
export declare interface B2BTOTPClient {
    create(request: B2BTOTPsCreateParameters): Promise<B2BTOTPsCreateResponse>;
    authenticate(request: B2BTOTPsAuthenticateParameters): Promise<B2BTOTPsAuthenticateResponse>;
    readonly __doNotUseOrImplementIt: {
        readonly "com.stytch.sdk.b2b.totp.B2BTOTPClient": unique symbol;
    };
}