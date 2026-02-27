import CryptoKit
import Foundation
internal import RecaptchaEnterprise
internal import StytchDFP

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

    @objc public func deleteEncryptionKey(name: String) {
        let query: [CFString: Any] = baseKeyQuery(name: name)
        let status = SecItemDelete(query as CFDictionary)
        // TODO: validate status, handle failures
    }

    @objc public func persistBiometricKeyData(name: String, keyData: Data) {
        var error: Unmanaged<CFError>?
        defer {
            error?.release()
        }
        let accessControl = SecAccessControlCreateWithFlags(nil, kSecAttrAccessibleWhenPasscodeSetThisDeviceOnly, [.biometryCurrentSet], &error)
        persistNewKeyDataToKeychain(name: name, newKeyData: keyData, accessControl: accessControl)
    }

    @objc public func getBiometricKeyData(name: String) -> Data? {
        var error: Unmanaged<CFError>?
        defer {
            error?.release()
        }
        let accessControl = SecAccessControlCreateWithFlags(nil, kSecAttrAccessibleWhenPasscodeSetThisDeviceOnly, [.biometryCurrentSet], &error)
        return getKeyDataFromKeychain(name: name, accessControl: accessControl)
    }

    private func getKeyDataFromKeychain(name: String, accessControl: SecAccessControl? = nil) -> Data? {
        var query = baseKeyQuery(name: name).merging(
            [
                kSecReturnData: true,
                kSecReturnAttributes: true,
                kSecMatchLimit: kSecMatchLimitOne,
                kSecAttrSynchronizable: kSecAttrSynchronizableAny,
                kSecUseAuthenticationUI: kSecUseAuthenticationUISkip,
            ]
        ) { $1 } as [CFString: Any]
        if let accessControl = accessControl {
            query[kSecAttrAccessControl] = accessControl
        }
        var ref: CFTypeRef?
        let status = SecItemCopyMatching(query as CFDictionary, &ref)
        return if status == errSecSuccess, let result = ref, CFGetTypeID(result) == CFDictionaryGetTypeID() {
            result[kSecValueData] as? Data
        } else {
            nil
        }
    }

    private func persistNewKeyDataToKeychain(name: String, newKeyData: Data, accessControl: SecAccessControl? = nil) {
        var query = baseKeyQuery(name: name).merging(
            [
                kSecValueData: newKeyData,
                kSecAttrAccessible: kSecAttrAccessibleAfterFirstUnlock,
            ]
        ) { $1 } as [CFString: Any]
        if let accessControl = accessControl {
            query[kSecAttrAccessControl] = accessControl
        }
        let status = SecItemAdd(query as CFDictionary, nil)
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
