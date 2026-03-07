import CryptoKit
import Foundation
internal import RecaptchaEnterprise
internal import StytchDFP

@objc(StytchEncryptionManagerSwift)
public class StytchEncryptionManagerSwift: NSObject {
    @MainActor @objc public static let shared = StytchEncryptionManagerSwift()

    @objc public func getEncryptionKey(name: String) throws -> Data {
        let existingKeyData = getKeyDataFromKeychain(name: name)
        guard let existingKeyData = existingKeyData else {
            let newKeyData = SymmetricKey(size: .bits256).withUnsafeBytes {
                Data(Array($0))
            }
            try persistNewKeyDataToKeychain(name: name, newKeyData: newKeyData)
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

    @objc public func deleteEncryptionKey(name: String) {
        let query: [CFString: Any] = baseKeyQuery(name: name)
        let status = SecItemDelete(query as CFDictionary)
        // TODO: validate status, handle failures
    }

    @objc public func persistBiometricKeyData(name: String, keyData: Data) throws {
        var error: Unmanaged<CFError>?
        guard let accessControl = SecAccessControlCreateWithFlags(
          nil,
          kSecAttrAccessibleWhenPasscodeSetThisDeviceOnly,
          [.biometryCurrentSet],
          &error
        ) else {
          throw error?.takeRetainedValue() ?? NSError(
              domain: "com.stytch.swift.encryption", code: 0,
              userInfo: ["Error": "Failed to create access control"]
          )
        }
        var query = baseKeyQuery(name: name)
        query[kSecValueData] = keyData
        query[kSecAttrAccessControl] = accessControl
        let status = SecItemAdd(query as CFDictionary, nil)
        if status != errSecSuccess {
          throw NSError(
              domain: "com.stytch.swift.encryption", code: Int(status),
              userInfo: ["Error Saving Key": "Status=\(status)"]
          )
        }
    }

    @objc public func getBiometricKeyData(name: String) throws -> Data {
        var query = baseKeyQuery(name: name)
        query[kSecReturnData] = true
        query[kSecMatchLimit] = kSecMatchLimitOne
        var ref: CFTypeRef?
        let status = SecItemCopyMatching(query as CFDictionary, &ref)
        guard status == errSecSuccess, let data = ref as? Data else {
          throw NSError(
              domain: "com.stytch.swift.encryption", code: Int(status),
              userInfo: ["BiometricKeyException": status == errSecItemNotFound ? "Key not found" : "Status=\(status)"]
          )
        }
        return data
    }

    @objc public func generateCodeVerifier() -> Data {
        var buffer = [UInt8](repeating: 0, count: Int(32))
        let _ = SecRandomCopyBytes(kSecRandomDefault, buffer.count, &buffer)
        return .init(buffer)
    }

    @objc public func generateCodeChallenge(challenge: Data) -> Data {
        return .init(SHA256.hash(data: challenge))
    }

    @objc public func generateEd25519KeyPair() -> [String:Data] {
        let privateKey = Curve25519.Signing.PrivateKey()
        return [
            "publicKey": privateKey.publicKey.rawRepresentation,
            "privateKey": privateKey.rawRepresentation,
        ]
    }

    @objc public func signEd25519(key: Data, challenge: Data) throws -> Data {
        return try Curve25519.Signing.PrivateKey(rawRepresentation: key).signature(for: challenge)
    }

    @objc public func deriveEd25519PublicKeyFromPrivateKeyBytes(privateKeyData: Data) throws -> Data {
        return try Curve25519.Signing.PrivateKey(rawRepresentation: privateKeyData).publicKey.rawRepresentation
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
        let status = SecItemCopyMatching(query, &ref)
        return if status == errSecSuccess, let result = ref, CFGetTypeID(result) == CFDictionaryGetTypeID() {
            result[kSecValueData] as? Data
        } else {
            nil
        }
    }

    private func persistNewKeyDataToKeychain(name: String, newKeyData: Data) throws {
        let query = baseKeyQuery(name: name).merging(
            [
                kSecValueData: newKeyData,
                kSecAttrAccessible: kSecAttrAccessibleAfterFirstUnlock,
            ]
        ) { $1 } as CFDictionary
        let status = SecItemAdd(query, nil)
        if status != errSecSuccess {
            throw NSError(domain: "com.stytch.swift.encryption", code: 0, userInfo: ["Error Saving Key": "Status=\(status)"])
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

@objc(StytchCAPTCHAProvider)
public class StytchCAPTCHAProvider: NSObject {
    @MainActor @objc public static let shared = StytchCAPTCHAProvider()
    private var recaptchaClient: RecaptchaClient?

    @objc public func isConfigured() -> Bool {
        recaptchaClient != nil
    }

    @objc public func executeRecaptcha() async -> String {
        guard let recaptchaClient = recaptchaClient else {
            return ""
        }
        do {
            return try await recaptchaClient.execute(withAction: RecaptchaAction.login)
        } catch let error as RecaptchaError {
            print("RecaptchaClient execute error: \(String(describing: error.errorMessage)).")
            return ""
        } catch {
            print("RecaptchaClient execute error: \(String(describing: error)).")
            return ""
        }
    }

    @objc public func setCaptchaClient(siteKey: String) async {
        do {
            recaptchaClient = try await Recaptcha.fetchClient(withSiteKey: siteKey)
        } catch let error as RecaptchaError {
            print("RecaptchaClient creation error: \(String(describing: error.errorMessage)).")
        } catch {
            print("RecaptchaClient creation error: \(String(describing: error))")
        }
    }
}

@objc(StytchDFPProvider)
public class StytchDFPProvider: NSObject {
    @MainActor @objc public static let shared = StytchDFPProvider()
    private let stytchDFP = StytchDFP()

    @objc public func configure(publicToken: String, dfppaDomain: String?) {
        stytchDFP.configure(withPublicToken: publicToken, submitURL: dfppaDomain)
    }

    @objc public func getTelemetryId() async -> String {
        await withCheckedContinuation { continuation in
            stytchDFP.getTelemetryID { telemetryId in
                continuation.resume(returning: telemetryId)
            }
        }
    }
}
