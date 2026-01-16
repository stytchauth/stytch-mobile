import CryptoKit
import Foundation

@objc(StytchEncryptionManagerSwift)
public class StytchEncryptionManagerSwift: NSObject {
    @MainActor @objc public static let shared = StytchEncryptionManagerSwift()

    @objc public func getEncryptionKey(name: String) -> Data {
        let existingKeyData = getKeyDataFromKeychain(name: name)
        guard let existingKeyData = existingKeyData else {
            let newKeyData = SymmetricKey(size: .bits256).withUnsafeBytes {
                Data(Array($0))
            }
            persistNewKeyDataToKeychain(name: name, newKeyData: newKeyData)
            return newKeyData
        }
        return existingKeyData
    }

    @objc public func encryptData(plainText: Data, withKeyData: Data) -> Data? {
        do {
            let encryptionKey = SymmetricKey(data: withKeyData)
            let sealedBox = try AES.GCM.seal(plainText, using: encryptionKey)
            return sealedBox.combined
        } catch {
            return nil
        }
    }

    @objc public func decryptData(encryptedData: Data, withKeyData: Data) -> Data? {
        do {
            let encryptionKey = SymmetricKey(data: withKeyData)
            let sealedBox = try AES.GCM.SealedBox(combined: encryptedData)
            return try AES.GCM.open(sealedBox, using: encryptionKey)
        } catch {
            return nil
        }
    }

    private func getKeyDataFromKeychain(name: String) -> Data? {
        let query = baseKeyQuery(name: name).merging(
            [
                kSecReturnData: true,
                kSecReturnAttributes: true,
                kSecMatchLimit: kSecMatchLimitOne,
                kSecAttrSynchronizable: kSecAttrSynchronizableAny,
                kSecUseAuthenticationUI: kSecUseAuthenticationUISkip,
            ]
        ) { $1 } as CFDictionary
        var ref: CFTypeRef?
        let status = SecItemCopyMatching(query as CFDictionary, &ref)
        return if status == errSecSuccess, let result = ref, CFGetTypeID(result) == CFDictionaryGetTypeID() {
            result[kSecValueData] as? Data
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
        let status = SecItemAdd(query, nil)
        // TODO: validate status, handle failures
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
