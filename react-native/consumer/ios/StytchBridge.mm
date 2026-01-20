#import "StytchBridge.h"
#import "StytchConsumerSDK.h"
#import "StytchConsumerSDK-Swift.h"

SCSDKStytchEncryptionClient *encryptionClient = [SCSDKStytchEncryptionClient init];
SCSDKStytchPlatformPersistenceClient *platformPersistenceClient = [SCSDKStytchPlatformPersistenceClient init];

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
// End Encryption Stuff
@end
