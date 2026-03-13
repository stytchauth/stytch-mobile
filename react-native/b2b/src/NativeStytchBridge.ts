import { TurboModule, TurboModuleRegistry } from 'react-native';

// RN ain't so great with "complex" data types across the bridge, so we're always encoding/decoding to strings when going back and forth :/
export interface Spec extends TurboModule {
  getDeviceInfo(): string;
  saveData(key: string, data: string): void;
  getData(key: string): string | undefined;
  removeData(key: string): void;
  encryptData(data: string): string;
  decryptData(data: string): string;
  deleteKey(): void;
  resetPreferences(): void;
  configureDfp(publicToken: string, dfppaDomain: string): void;
  getTelemetryId(): Promise<string>;
  configureCaptcha(siteKey: string): void;
  getCAPTCHAToken(): Promise<string>;
  isCaptchaConfigured(): boolean;
  generateCodeVerifier(): string;
  generateCodeChallenge(verifier: string): string;
  signEd25519(key: string, data: string): string;
  generateEd25519KeyPair(): string[];
  deriveEd25519PublicKeyFromPrivateKeyBytes(privateKeyBytes: string): string;
  getBiometricsAvailability(
    sessionDurationMinutes: number,
    androidAllowDeviceCredentials?: boolean,
    androidTitle?: string,
    androidSubTitle?: string,
    androidNegativeButtonText?: string,
    androidAllowFallbackToCleartext?: boolean,
    iosReason?: string,
    iosFallbackTitle?: string,
    iosCancelTitle?: string,
  ): Promise<string>;
  registerBiometrics(
    sessionDurationMinutes: number,
    androidAllowDeviceCredentials?: boolean,
    androidTitle?: string,
    androidSubTitle?: string,
    androidNegativeButtonText?: string,
    androidAllowFallbackToCleartext?: boolean,
    iosReason?: string,
    iosFallbackTitle?: string,
    iosCancelTitle?: string,
  ): Promise<string>;
  authenticateBiometrics(
    sessionDurationMinutes: number,
    androidAllowDeviceCredentials?: boolean,
    androidTitle?: string,
    androidSubTitle?: string,
    androidNegativeButtonText?: string,
    androidAllowFallbackToCleartext?: boolean,
    iosReason?: string,
    iosFallbackTitle?: string,
    iosCancelTitle?: string,
  ): Promise<string>;
  persistBiometricRegistration(registrationId: string, privateKeyData: string): Promise<void>;
  removeBiometricRegistration(): Promise<void>;
  createPublicKeyCredential(
    domain: string,
    preferImmediatelyAvailableCredentials: boolean,
    json: string,
    sessionDurationMinutes?: number,
  ): Promise<string>;
  getPublicKeyCredential(
    domain: string,
    preferImmediatelyAvailableCredentials: boolean,
    json: string,
    sessionDurationMinutes?: number,
  ): Promise<string>;
  getOAuthToken(
    type: string,
    baseUrl: string,
    publicToken: string,
    loginRedirectUrl?: string,
    signupRedirectUrl?: string,
    customScopes?: string[],
    providerParams?: string,
    oauthAttachToken?: string,
    sessionDurationMinutes?: number,
    googleCredentialConfiguration?: string,
  ): Promise<string>;
  startBrowserFlow(url: string): Promise<string>;
}

// create an instance of the module
const module = TurboModuleRegistry.get<Spec>('StytchBridge');

// export it for use within the RN/JS side
export default module;

// This is where the magic happens. Expose a global var with a name/shape that we told Kotlin about, and it "just works"
declare global {
  var StytchBridge: Spec;
}
global.StytchBridge = module!;
