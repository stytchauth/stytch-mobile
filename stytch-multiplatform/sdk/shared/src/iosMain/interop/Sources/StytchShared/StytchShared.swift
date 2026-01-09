import CryptoKit
import Foundation

@objc(StytchEncryptionManagerSwift)
public actor StytchEncryptionManagerSwift: NSObject {
    @objc public static let shared = StytchEncryptionManagerSwift()

    @objc public func getEncryptionKey(name: String) async throws -> Data {
        let existingKeyData = getKeyDataFromKeychain(name: name)
        print("JORDAN >> whaddya got? \(String(describing: existingKeyData))")
        guard let existingKeyData = existingKeyData else {
            print("JORDAN > Creating new key")
            let newKeyData = SymmetricKey(size: .bits256).withUnsafeBytes {
                Data(Array($0))
            }
            persistNewKeyDataToKeychain(name: name, newKeyData: newKeyData)
            return newKeyData
        }
        print("JORDAN > Reusing old key")
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

    private func getKeyDataFromKeychain(name: String) -> Data? {
        let query = baseKeyQuery(name: name).merging(
            [
                kSecReturnData: true,
                kSecReturnAttributes: true,
                kSecMatchLimit: kSecMatchLimitAll,
                kSecAttrSynchronizable: kSecAttrSynchronizableAny,
                kSecUseAuthenticationUI: kSecUseAuthenticationUISkip,
            ]
        ) { $1 } as CFDictionary
        var ref: CFTypeRef?
        let status = SecItemCopyMatching(query as CFDictionary, &result)
        return if status == errSecSuccess, let result = result as? Data {
            result
        } else {
            nil
        }
    }

    private func persistNewKeyDataToKeychain(name: String, newKeyData: Data) {
        let query = baseKeyQuery(name: name).merging(
            [
                kSecValueData: newKeyData,
                kSecAttrAccessible: kSecAttrAccessibleAfterFirstUnlock,
            ]
        ) { $1 } as CFDictionary
        print("SAVE QUERY: \(query)")
        let status = SecItemAdd(query, nil)
        print("JORDAN > Result of save: \(status)")
        if status != errSecSuccess {
            print("JORDAN >>>> SAVE FAILED. DELETE AND TRY AGAIN?")
            let deleteStatus = SecItemDelete(query)
            print("JORDAN > Result of delete: \(deleteStatus)")
        }
    }

    private func baseKeyQuery(name: String) -> [CFString: Any] {
        var query: [CFString: Any] {
            [
                kSecAttrAccount: name,
                kSecClass: kSecClassGenericPassword,
                kSecAttrService: name,
                kSecUseDataProtectionKeychain: true,
            ]
        }
        return query
    }
}
