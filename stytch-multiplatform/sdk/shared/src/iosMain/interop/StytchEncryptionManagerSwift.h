#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN;
@interface StytchEncryptionManagerSwift : NSObject;
+ (StytchEncryptionManagerSwift *)shared;
-(void)getEncryptionKeyWithName:(NSString *)name
        completionHandler:(void (^ _Nonnull)(NSData * _Nullable keyData,
        NSError * _Nullable error))completionHandler;
-(void)encryptDataWithPlainText:(NSData *)plainText
        withKeyData:(NSData *)withKeyData
        completionHandler:(void (^ _Nonnull)(NSData * _Nullable encryptedData,
        NSError * _Nullable error))completionHandler;
-(void)decryptDataWithEncryptedData:(NSData *)encryptedData
        withKeyData:(NSData *)withKeyData
        completionHandler:(void (^ _Nonnull)(NSData * _Nullable plainText,
        NSError * _Nullable error))completionHandler;
@end
NS_ASSUME_NONNULL_END;
