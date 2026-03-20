import XCTest
@testable import StytchSwiftUtils

final class StytchEncryptionManagerTests: XCTestCase {
    let manager = StytchEncryptionManagerSwift()
    let keyName = "com.stytch.test.key"

    override func tearDown() {
        try? manager.deleteEncryptionKey(name: keyName)
        super.tearDown()
    }

    // MARK: - Keychain key management

    func testGetEncryptionKeyReturns32Bytes() throws {
        let key = try manager.getEncryptionKey(name: keyName)
        XCTAssertEqual(key.count, 32) // AES-256
    }

    func testGetEncryptionKeyIsIdempotent() throws {
        let key1 = try manager.getEncryptionKey(name: keyName)
        let key2 = try manager.getEncryptionKey(name: keyName)
        XCTAssertEqual(key1, key2)
    }

    // MARK: - Encrypt / Decrypt

    func testEncryptDecryptRoundTrip() throws {
        let key = try manager.getEncryptionKey(name: keyName)
        let plaintext = "hello, stytch!".data(using: .utf8)!
        let encrypted = try manager.encryptData(plainText: plaintext, withKeyData: key)
        let decrypted = try manager.decryptData(encryptedData: encrypted, withKeyData: key)
        XCTAssertEqual(plaintext, decrypted)
    }

    func testEncryptedOutputDiffersFromPlaintext() throws {
        let key = try manager.getEncryptionKey(name: keyName)
        let plaintext = "hello, stytch!".data(using: .utf8)!
        XCTAssertNotEqual(plaintext, try manager.encryptData(plainText: plaintext, withKeyData: key))
    }

    func testEncryptProducesUniqueOutputsForSamePlaintext() throws {
        let key = try manager.getEncryptionKey(name: keyName)
        let plaintext = "hello, stytch!".data(using: .utf8)!
        let e1 = try manager.encryptData(plainText: plaintext, withKeyData: key)
        let e2 = try manager.encryptData(plainText: plaintext, withKeyData: key)
        XCTAssertNotEqual(e1, e2) // random IV per encryption
    }

    func testEncryptRoundTripsEmptyData() throws {
        let key = try manager.getEncryptionKey(name: keyName)
        let plaintext = Data()
        let decrypted = try manager.decryptData(
            encryptedData: try manager.encryptData(plainText: plaintext, withKeyData: key),
            withKeyData: key
        )
        XCTAssertEqual(plaintext, decrypted)
    }

    // MARK: - generateCodeVerifier / generateCodeChallenge

    func testGenerateCodeVerifierReturns32Bytes() {
        XCTAssertEqual(manager.generateCodeVerifier().count, 32)
    }

    func testGenerateCodeVerifierProducesUniqueValues() {
        XCTAssertNotEqual(manager.generateCodeVerifier(), manager.generateCodeVerifier())
    }

    func testGenerateCodeChallengeReturns32Bytes() {
        XCTAssertEqual(manager.generateCodeChallenge(challenge: manager.generateCodeVerifier()).count, 32)
    }

    func testGenerateCodeChallengeIsDeterministic() {
        let verifier = manager.generateCodeVerifier()
        XCTAssertEqual(
            manager.generateCodeChallenge(challenge: verifier),
            manager.generateCodeChallenge(challenge: verifier)
        )
    }

    // MARK: - Ed25519

    func testGenerateEd25519KeyPairReturns32ByteKeys() {
        let pair = manager.generateEd25519KeyPair()
        XCTAssertEqual((pair["publicKey"] as? Data)?.count, 32)
        XCTAssertEqual((pair["privateKey"] as? Data)?.count, 32)
    }

    func testGenerateEd25519KeyPairProducesUniqueKeys() {
        XCTAssertNotEqual(
            manager.generateEd25519KeyPair()["privateKey"] as? Data,
            manager.generateEd25519KeyPair()["privateKey"] as? Data
        )
    }

    func testDeriveEd25519PublicKeyMatchesGeneratedPublicKey() throws {
        let pair = manager.generateEd25519KeyPair()
        let privateKey = try XCTUnwrap(pair["privateKey"] as? Data)
        let publicKey = try XCTUnwrap(pair["publicKey"] as? Data)
        let derived = try manager.deriveEd25519PublicKeyFromPrivateKeyBytes(privateKeyData: privateKey)
        XCTAssertEqual(publicKey, derived)
    }

    func testSignEd25519ProducesA64ByteSignature() throws {
        let pair = manager.generateEd25519KeyPair()
        let privateKey = try XCTUnwrap(pair["privateKey"] as? Data)
        let sig = try manager.signEd25519(key: privateKey, challenge: "test data".data(using: .utf8)!)
        XCTAssertEqual(sig.count, 64)
    }

    func testSignEd25519ProducesDifferentSignaturesForDifferentData() throws {
        let pair = manager.generateEd25519KeyPair()
        let privateKey = try XCTUnwrap(pair["privateKey"] as? Data)
        let sig1 = try manager.signEd25519(key: privateKey, challenge: "data one".data(using: .utf8)!)
        let sig2 = try manager.signEd25519(key: privateKey, challenge: "data two".data(using: .utf8)!)
        XCTAssertNotEqual(sig1, sig2)
    }
}
