#import "StytchBridge.h"
#import <StytchConsumerSDK/StytchConsumerSDK.h>
#import <StytchConsumerSDK/StytchConsumerSDK-Swift.h>
SCSDKStytchEncryptionClient *encryptionClient = [[SCSDKStytchEncryptionClient alloc] init];
SCSDKStytchPlatformPersistenceClient *platformPersistenceClient = [[SCSDKStytchPlatformPersistenceClient alloc] init];
SCSDKCAPTCHAProviderImpl *captchaClient = [[SCSDKCAPTCHAProviderImpl alloc] init];
SCSDKDFPProviderImpl *dfpClient;

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

@end
