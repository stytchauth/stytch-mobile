#import "StytchBridge.h"
#import <StytchB2BSDK/StytchB2BSDK.h>
#import <StytchB2BSDK/StytchB2BSDK-Swift.h>
SCSDKStytchEncryptionClient *encryptionClient = [[SCSDKStytchEncryptionClient alloc] init];
SCSDKStytchPlatformPersistenceClient *platformPersistenceClient = [[SCSDKStytchPlatformPersistenceClient alloc] init];
SCSDKCAPTCHAProviderImpl *captchaClient = [[SCSDKCAPTCHAProviderImpl alloc] init];
SCSDKDFPProviderImpl *dfpClient;
SCSDKBiometricsProvider *biometricsProvider = [[SCSDKBiometricsProvider alloc] initWithEncryptionClient:encryptionClient persistenceClient:platformPersistenceClient];
SCSDKPasskeyProvider *passkeysProvider = [[SCSDKPasskeyProvider alloc] init];
SCSDKStytchDispatchers *stytchDispatchers = [SCSDKStytchDispatcherFactory_iosKt createStytchDispatchers];
SCSDKStytchPersistenceClient *persistenceClient = [[SCSDKStytchPersistenceClient alloc] initWithDispatcher:[stytchDispatchers ioDispatcher] encryptionClient:encryptionClient platformPersistenceClient:platformPersistenceClient];
SCSDKPKCEClient *pkceClient = [[SCSDKPKCEClient alloc] initWithEncryptionClient:encryptionClient persistenceClient:persistenceClient];
SCSDKOAuthProvider *oauthProvider = [[SCSDKOAuthProvider alloc] initWithPackageName:[[NSBundle mainBundle] bundleIdentifier] encryptionClient:encryptionClient];

@implementation StytchBridge

- (std::shared_ptr<facebook::react::TurboModule>)getTurboModule:
    (const facebook::react::ObjCTurboModule::InitParams &)params
{
    return std::make_shared<facebook::react::NativeStytchBridgeSpecJSI>(params);
}

+ (NSString *)moduleName
{
  return @"StytchBridge";
}

- (nonnull NSString *)getDeviceInfo {
    NSBundle *mainBundle = [NSBundle mainBundle];
    UIDevice *currentDevice = [UIDevice currentDevice];
    CGRect screenBounds = [[UIScreen mainScreen] bounds];
    NSDictionary *jsonDictionary = @{
        @"applicationPackageName": [mainBundle bundleIdentifier],
        @"applicationVersion": [mainBundle objectForInfoDictionaryKey:@"CFBundleShortVersionString"],
        @"osName": [currentDevice systemName],
        @"osVersion": [[NSProcessInfo processInfo] operatingSystemVersionString],
        @"deviceName": [[currentDevice model] lowercaseString],
        @"screenSize": [NSString stringWithFormat:@"(%f,%f)", screenBounds.size.width, screenBounds.size.height],
    };
    NSError *error;
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:jsonDictionary options:NSJSONWritingPrettyPrinted error:&error];
    return [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
}

// Begin Persistence stuff
- (void)saveData:(nonnull NSString *)key data:(nonnull NSString *)data {
    [platformPersistenceClient saveDataKey:key data:data];
}

- (NSString * _Nullable)getData:(nonnull NSString *)key {
    return [platformPersistenceClient getDataKey:key];
}

- (void)removeData:(nonnull NSString *)key {
    [platformPersistenceClient removeDataKey:key];
}

- (void)resetPreferences {
    [platformPersistenceClient reset];
}
// End Persistence Stuff

// Begin Encryption stuff
- (nonnull NSString *)encryptData:(nonnull NSString *)data {
    // B64 String -> NSData -> KotlinByteArray -> Encrypted KotlinByteArray -> Encrypted NSData -> B64 String
    NSData *decoded = [[NSData alloc] initWithBase64EncodedString:data options:NSDataBase64DecodingIgnoreUnknownCharacters];
    SCSDKKotlinByteArray *decodedByteArray = [SCSDKStytchEncryptionClient_iosKt toByteArray:decoded];
    SCSDKKotlinByteArray *encryptedByteArray = [encryptionClient encryptData:decodedByteArray];
    NSData *encryptedNSData = [encryptedByteArray toNSData];
    return [encryptedNSData base64EncodedStringWithOptions:0];
}

- (nonnull NSString *)decryptData:(nonnull NSString *)data {
    // B64 String -> NSData -> KotlinByteArray -> Decrypted KotlinByteArray -> Decrypted NSData -> B64 String
    NSData *decoded = [[NSData alloc] initWithBase64EncodedString:data options:NSDataBase64DecodingIgnoreUnknownCharacters];
    SCSDKKotlinByteArray *decodedByteArray = [SCSDKStytchEncryptionClient_iosKt toByteArray:decoded];
    SCSDKKotlinByteArray *decryptedByteArray = [encryptionClient decryptData:decodedByteArray];
    NSData *decryptedNSData = [decryptedByteArray toNSData];
    return [decryptedNSData base64EncodedStringWithOptions:0];
}

- (void)deleteKey {
    [encryptionClient deleteKey];
}

- (nonnull NSString *)deriveEd25519PublicKeyFromPrivateKeyBytes:(nonnull NSString *)privateKeyBytes {
    NSData *decoded = [[NSData alloc] initWithBase64EncodedString:privateKeyBytes options:NSDataBase64DecodingIgnoreUnknownCharacters];
    SCSDKKotlinByteArray *decodedByteArray = [SCSDKStytchEncryptionClient_iosKt toByteArray:decoded];
    SCSDKKotlinByteArray *publicKeyByteArray = [encryptionClient deriveEd25519PublicKeyFromPrivateKeyBytesPrivateKeyBytes:decodedByteArray];
    NSData *publicKeyData = [publicKeyByteArray toNSData];
    return [publicKeyData base64EncodedStringWithOptions:0];
}


- (nonnull NSString *)generateCodeChallenge:(nonnull NSString *)verifier {
    NSData *verifierData = [[NSData alloc] initWithBase64EncodedString:verifier options:NSDataBase64DecodingIgnoreUnknownCharacters];
    SCSDKKotlinByteArray *verifierByteArray = [SCSDKStytchEncryptionClient_iosKt toByteArray:verifierData];
    SCSDKKotlinByteArray *challengeByteArray = [encryptionClient generateCodeChallengeCodeVerifier:verifierByteArray];
    NSData *challengeData = [challengeByteArray toNSData];
    return [challengeData base64EncodedStringWithOptions:0];
}


- (nonnull NSString *)generateCodeVerifier {
    SCSDKKotlinByteArray *codeVerifierByteArray = [encryptionClient generateCodeVerifier];
    NSData *codeVerifierData = [codeVerifierByteArray toNSData];
    return [codeVerifierData base64EncodedStringWithOptions:0];
}


- (nonnull NSArray<NSString *> *)generateEd25519KeyPair {
    SCSDKEd25519KeyPair *keyPair = [encryptionClient generateEd25519KeyPair];
    NSData *publicKeyData = [keyPair.publicKey toNSData];
    NSData *privateKeyData = [keyPair.privateKey toNSData];
    NSMutableArray *outArray = [NSMutableArray array];
    [outArray addObject:[publicKeyData base64EncodedStringWithOptions:0]];
    [outArray addObject:[privateKeyData base64EncodedStringWithOptions:0]];
    return outArray;
}


- (nonnull NSString *)signEd25519:(nonnull NSString *)key data:(nonnull NSString *)data {
    NSData *keyData = [[NSData alloc] initWithBase64EncodedString:key options:NSDataBase64DecodingIgnoreUnknownCharacters];
    NSData *dataData = [[NSData alloc] initWithBase64EncodedString:data options:NSDataBase64DecodingIgnoreUnknownCharacters];
    SCSDKKotlinByteArray *keyByteArray = [SCSDKStytchEncryptionClient_iosKt toByteArray:keyData];
    SCSDKKotlinByteArray *dataByteArray = [SCSDKStytchEncryptionClient_iosKt toByteArray:dataData];
    SCSDKKotlinByteArray *signatureByteArray = [encryptionClient signEd25519Key:keyByteArray data:dataByteArray];
    NSData *signatureData = [signatureByteArray toNSData];
    return [signatureData base64EncodedStringWithOptions:0];
}
// End Encryption Stuff

// Begin DFP stuff
- (void)configureDfp:(nonnull NSString *)publicToken dfppaDomain:(nonnull NSString *)dfppaDomain {
    dfpClient = [[SCSDKDFPProviderImpl alloc] initWithPublicToken:publicToken dfppaDomain:dfppaDomain];
}

- (void)getTelemetryId:(nonnull RCTPromiseResolveBlock)resolve reject:(nonnull RCTPromiseRejectBlock)reject {
    [dfpClient getTelemetryIdWithCompletionHandler:^(NSString * telemetryId, NSError * _Nullable error) {
        if (error == nil) {
            resolve(telemetryId);
        } else {
            reject(@"", [error description], error);
        }
    }];
}
// End DFP stuff

// Begin CAPTCHA stuff
- (void)configureCaptcha:(nonnull NSString *)siteKey {
    [captchaClient initializeSiteKey:siteKey completionHandler:^(NSError * _Nullable error) {}];
}

- (void)getCAPTCHAToken:(nonnull RCTPromiseResolveBlock)resolve reject:(nonnull RCTPromiseRejectBlock)reject {
    [captchaClient getCAPTCHATokenWithCompletionHandler:^(NSString * token, NSError * _Nullable error) {
        if (error == nil) {
            resolve(token);
        } else {
            reject(@"", [error description], error);
        }
    }];
}
- (NSNumber *)isCaptchaConfigured {
    BOOL result = [captchaClient isConfigured];
    return [NSNumber numberWithBool:result];
}
// End CAPTCHA stuff

// Begin Biometrics stuff
- (void)authenticateBiometrics:(double)sessionDurationMinutes androidAllowDeviceCredentials:(nonnull NSNumber *)androidAllowDeviceCredentials androidTitle:(nonnull NSString *)androidTitle androidSubTitle:(nonnull NSString *)androidSubTitle androidNegativeButtonText:(nonnull NSString *)androidNegativeButtonText androidAllowFallbackToCleartext:(nonnull NSNumber *)androidAllowFallbackToCleartext iosReason:(nonnull NSString *)iosReason iosFallbackTitle:(nonnull NSString *)iosFallbackTitle iosCancelTitle:(nonnull NSString *)iosCancelTitle resolve:(nonnull RCTPromiseResolveBlock)resolve reject:(nonnull RCTPromiseRejectBlock)reject {
    SCSDKBiometricPromptData *promptData = [[SCSDKBiometricPromptData alloc] initWithReason:iosReason fallbackTitle:iosFallbackTitle cancelTitle:iosCancelTitle];
    SCSDKBiometricsParameters *params = [[SCSDKBiometricsParameters alloc] initWithSessionDurationMinutes:sessionDurationMinutes promptData:promptData];
    [biometricsProvider authenticateParameters:params completionHandler:^(SCSDKEd25519KeyPair * _Nullable keyPair, NSError * _Nullable error) {
        if (error == nil) {
            NSError *encodeError = nil;
            NSString *asString = [[SCSDKJsonSerDeHelper alloc] encodeEd25519KeyPairData:keyPair error:&encodeError];
            if (encodeError != nil) { reject(@"", [encodeError description], encodeError); return; }
            resolve(asString);
        } else {
            reject(@"", [error description], error);
        }
    }];
}


- (void)getBiometricsAvailability:(double)sessionDurationMinutes androidAllowDeviceCredentials:(nonnull NSNumber *)androidAllowDeviceCredentials androidTitle:(nonnull NSString *)androidTitle androidSubTitle:(nonnull NSString *)androidSubTitle androidNegativeButtonText:(nonnull NSString *)androidNegativeButtonText androidAllowFallbackToCleartext:(nonnull NSNumber *)androidAllowFallbackToCleartext iosReason:(nonnull NSString *)iosReason iosFallbackTitle:(nonnull NSString *)iosFallbackTitle iosCancelTitle:(nonnull NSString *)iosCancelTitle resolve:(nonnull RCTPromiseResolveBlock)resolve reject:(nonnull RCTPromiseRejectBlock)reject {
    SCSDKBiometricPromptData *promptData = [[SCSDKBiometricPromptData alloc] initWithReason:iosReason fallbackTitle:iosFallbackTitle cancelTitle:iosCancelTitle];
    SCSDKBiometricsParameters *params = [[SCSDKBiometricsParameters alloc] initWithSessionDurationMinutes:sessionDurationMinutes promptData:promptData];
    [biometricsProvider getAvailabilityParameters:params completionHandler:^(SCSDKBiometricsAvailability * _Nullable availability, NSError * _Nullable error) {
        if (error == nil) {
            NSError *encodeError = nil;
            NSString *asString = [[SCSDKJsonSerDeHelper alloc] encodeBiometricsAvailabilityData:availability error:&encodeError];
            if (encodeError != nil) { reject(@"", [encodeError description], encodeError); return; }
            resolve(asString);
        } else {
            reject(@"", [error description], error);
        }
    }];
}


- (void)persistBiometricRegistration:(nonnull NSString *)registrationId privateKeyData:(nonnull NSString *)privateKeyData resolve:(nonnull RCTPromiseResolveBlock)resolve reject:(nonnull RCTPromiseRejectBlock)reject {
    [biometricsProvider persistRegistrationRegistrationId:registrationId privateKeyData:privateKeyData completionHandler:^(NSError * _Nullable error) {
        if (error == nil) {
            resolve(nil);
        } else {
            reject(@"", [error description], error);
        }
    }];
}


- (void)registerBiometrics:(double)sessionDurationMinutes androidAllowDeviceCredentials:(nonnull NSNumber *)androidAllowDeviceCredentials androidTitle:(nonnull NSString *)androidTitle androidSubTitle:(nonnull NSString *)androidSubTitle androidNegativeButtonText:(nonnull NSString *)androidNegativeButtonText androidAllowFallbackToCleartext:(nonnull NSNumber *)androidAllowFallbackToCleartext iosReason:(nonnull NSString *)iosReason iosFallbackTitle:(nonnull NSString *)iosFallbackTitle iosCancelTitle:(nonnull NSString *)iosCancelTitle resolve:(nonnull RCTPromiseResolveBlock)resolve reject:(nonnull RCTPromiseRejectBlock)reject {
    SCSDKBiometricPromptData *promptData = [[SCSDKBiometricPromptData alloc] initWithReason:iosReason fallbackTitle:iosFallbackTitle cancelTitle:iosCancelTitle];
    SCSDKBiometricsParameters *params = [[SCSDKBiometricsParameters alloc] initWithSessionDurationMinutes:sessionDurationMinutes promptData:promptData];
    [biometricsProvider registerParameters:params completionHandler:^(SCSDKEd25519KeyPair * _Nullable keyPair, NSError * _Nullable error) {
        if (error == nil) {
            NSError *encodeError = nil;
            NSString *asString = [[SCSDKJsonSerDeHelper alloc] encodeEd25519KeyPairData:keyPair error:&encodeError];
            if (encodeError != nil) { reject(@"", [encodeError description], encodeError); return; }
            resolve(asString);
        } else {
            reject(@"", [error description], error);
        }
    }];
}

- (void)removeBiometricRegistration:(nonnull RCTPromiseResolveBlock)resolve reject:(nonnull RCTPromiseRejectBlock)reject {
    [biometricsProvider removeRegistrationWithCompletionHandler:^(NSError * _Nullable error) {
        if (error == nil) {
            resolve(nil);
        } else {
            reject(@"", [error description], error);
        }
    }];
}
// End Biometrics stuff

// Begin Passkeys stuff
- (void)createPublicKeyCredential:(nonnull NSString *)domain preferImmediatelyAvailableCredentials:(BOOL)preferImmediatelyAvailableCredentials json:(nonnull NSString *)json sessionDurationMinutes:(nonnull NSNumber *)sessionDurationMinutes resolve:(nonnull RCTPromiseResolveBlock)resolve reject:(nonnull RCTPromiseRejectBlock)reject {
    SCSDKPasskeysParameters *params = [[SCSDKPasskeysParameters alloc] initWithDomain:domain sessionDurationMinutes:sessionDurationMinutes preferImmediatelyAvailableCredentials:preferImmediatelyAvailableCredentials];
    [passkeysProvider createPublicKeyCredentialParameters:params dispatchers:stytchDispatchers json:json completionHandler:^(NSString * _Nullable credentials, NSError * _Nullable error) {
        if (error == nil) {
            resolve(credentials);
        } else {
            reject(@"", [error description], error);
        }
    }];
}


- (void)getPublicKeyCredential:(nonnull NSString *)domain preferImmediatelyAvailableCredentials:(BOOL)preferImmediatelyAvailableCredentials json:(nonnull NSString *)json sessionDurationMinutes:(nonnull NSNumber *)sessionDurationMinutes resolve:(nonnull RCTPromiseResolveBlock)resolve reject:(nonnull RCTPromiseRejectBlock)reject {
    SCSDKPasskeysParameters *params = [[SCSDKPasskeysParameters alloc] initWithDomain:domain sessionDurationMinutes:sessionDurationMinutes preferImmediatelyAvailableCredentials:preferImmediatelyAvailableCredentials];
    [passkeysProvider getPublicKeyCredentialParameters:params dispatchers:stytchDispatchers json:json completionHandler:^(NSString * _Nullable credentials, NSError * _Nullable error) {
        if (error == nil) {
            resolve(credentials);
        } else {
            reject(@"", [error description], error);
        }
    }];
}
// End Passkeys stuff

// Begin OAuth stuff
- (void)getOAuthToken:(nonnull NSString *)type baseUrl:(nonnull NSString *)baseUrl publicToken:(nonnull NSString *)publicToken loginRedirectUrl:(nonnull NSString *)loginRedirectUrl signupRedirectUrl:(nonnull NSString *)signupRedirectUrl customScopes:(nonnull NSArray *)customScopes providerParams:(nonnull NSString *)providerParams oauthAttachToken:(nonnull NSString *)oauthAttachToken sessionDurationMinutes:(nonnull NSNumber *)sessionDurationMinutes googleCredentialConfiguration:(nonnull NSString *)googleCredentialConfiguration resolve:(nonnull RCTPromiseResolveBlock)resolve reject:(nonnull RCTPromiseRejectBlock)reject {
    NSError *decodeError = nil;
    SCSDKOAuthProviderType *oauthProviderType = [[SCSDKJsonSerDeHelper alloc] decodeOAuthProviderTypeData:type error:&decodeError];
    if (decodeError != nil) { reject(@"", [decodeError description], decodeError); return; }
    NSMutableDictionary *providerParamsDict = [[NSMutableDictionary alloc] init];
    NSArray *pairs = [providerParams componentsSeparatedByString:@"&"];
    for (NSString *pair in pairs) {
        NSArray *elements = [pair componentsSeparatedByString:@"="];
        if (elements.count == 2) {
            [providerParamsDict setObject:elements[1] forKey:elements[0]];
        }
    }
    SCSDKOAuthStartParameters *params = [[SCSDKOAuthStartParameters alloc] initWithLoginRedirectUrl:loginRedirectUrl signupRedirectUrl:signupRedirectUrl customScopes:customScopes providerParams:providerParamsDict oauthAttachToken:oauthAttachToken sessionDurationMinutes:[sessionDurationMinutes intValue]];
    SCSDKPublicTokenInfo *publicTokenInfo = [SCSDKStytchClientConfigurationKt getPublicTokenInfoPublicToken:publicToken];
    [oauthProvider getOAuthTokenParameters:params pkceClient:pkceClient dispatchers:stytchDispatchers type:oauthProviderType baseUrl:baseUrl publicTokenInfo:publicTokenInfo completionHandler:^(SCSDKOAuthResult * _Nullable result, NSError * _Nullable error) {
        if (error == nil) {
            NSError *encodeError = nil;
            NSString *tokenResult = [[SCSDKJsonSerDeHelper alloc] encodeOAuthResultData:result error:&encodeError];
            if (encodeError != nil) { reject(@"", [encodeError description], encodeError); return; }
            resolve(tokenResult);
        } else {
            reject(@"", [error description], error);
        }
    }];
}
- (void)startBrowserFlow:(nonnull NSString *)url resolve:(nonnull RCTPromiseResolveBlock)resolve reject:(nonnull RCTPromiseRejectBlock)reject {
    SCSDKOAuthStartParameters *params = [[SCSDKOAuthStartParameters alloc] init];
    [oauthProvider startBrowserFlowWithUrl:url parameters:params dispatchers:stytchDispatchers completionHandler:^(SCSDKOAuthResult * _Nullable result, NSError * _Nullable error) {
        if (error == nil) {
            NSError *encodeError = nil;
            NSString *tokenResult = [[SCSDKJsonSerDeHelper alloc] encodeOAuthResultData:result error:&encodeError];
            if (encodeError != nil) { reject(@"", [encodeError description], encodeError); return; }
            resolve(tokenResult);
        } else {
            reject(@"", [error description], error);
        }
    }];
}
// End OAuth stuff

@end
