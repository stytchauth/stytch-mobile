import CryptoKit
import Foundation

@objc(StytchEncryptionManagerSwift)
public actor StytchEncryptionManagerSwift: NSObject {
    @objc public static let shared = StytchEncryptionManagerSwift()

    @objc public func getEncryptionKey(name: String) async throws -> Data {
        guard let existingKeyData = getKeyFromKeychain(name: name) else {
            return SymmetricKey(size: .bits256).withUnsafeBytes {
                Data(Array($0))
            }
        }
        return existingKeyData
    }

    @objc public func encryptData(plainText: Data, withKeyData: Data) async throws -> Data? {
        let encryptionKey = SymmetricKey(data: withKeyData)
        let sealedBox = try AES.GCM.seal(plainText, using: encryptionKey)
        return sealedBox.combined
    }

    @objc public func decryptData(encryptedData: Data, withKeyData: Data) async throws -> Data? {
        let encryptionKey = SymmetricKey(data: withKeyData)
        let sealedBox = try AES.GCM.SealedBox(combined: encryptedData)
        return try AES.GCM.open(sealedBox, using: encryptionKey)
    }

    private func getKeyFromKeychain(name: String) -> Data? {
        var query: [CFString: Any] {
            [
                kSecClass: kSecClassGenericPassword,
                kSecAttrService: name,
                kSecUseDataProtectionKeychain: true,
                kSecReturnData: true,
                kSecReturnAttributes: true,
                kSecMatchLimit: kSecMatchLimitAll,
                kSecAttrSynchronizable: kSecAttrSynchronizableAny,
                kSecUseAuthenticationUI: kSecUseAuthenticationUISkip,
            ]
        }
        var result: CFTypeRef?
        let status = SecItemCopyMatching(query as CFDictionary, &result)
        return if status == errSecSuccess, let result = result as? Data {
            result
        } else {
            nil
        }
    }
}
