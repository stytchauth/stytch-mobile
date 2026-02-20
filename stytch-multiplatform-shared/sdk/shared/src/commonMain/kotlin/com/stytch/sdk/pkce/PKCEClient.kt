package com.stytch.sdk.pkce

import com.stytch.sdk.data.PKCECodePair
import com.stytch.sdk.encryption.StytchEncryptionClient
import com.stytch.sdk.persistence.StytchPersistenceClient
import io.ktor.util.encodeBase64

public class PKCEClient(
    private val encryptionClient: StytchEncryptionClient,
    private val persistenceClient: StytchPersistenceClient,
) {
    public fun create(): PKCECodePair {
        val codeVerifierData = encryptionClient.generateCodeVerifier()
        val codeVerifierString = codeVerifierData.encodeBase64()
        val codeChallenge =
            encryptionClient
                .generateCodeChallenge(codeVerifierData)
                .joinToString("") { it.toInt().toString(16).padStart(2, '0') }
                .encodeBase64()
        persistenceClient.save(PKCE_CODE_CHALLENGE_KEY, codeChallenge)
        persistenceClient.save(PKCE_CODE_VERIFIER_KEY, codeVerifierString)
        return PKCECodePair(
            challenge = codeChallenge,
            verifier = codeVerifierString,
        )
    }

    public fun revoke() {
        persistenceClient.remove(PKCE_CODE_CHALLENGE_KEY)
        persistenceClient.remove(PKCE_CODE_VERIFIER_KEY)
    }

    public fun retrieve(): PKCECodePair? {
        val challenge = persistenceClient.get<String>(PKCE_CODE_CHALLENGE_KEY, null) ?: return null
        val verifier = persistenceClient.get<String>(PKCE_CODE_VERIFIER_KEY, null) ?: return null
        return PKCECodePair(challenge, verifier)
    }

    private companion object {
        private const val PKCE_CODE_CHALLENGE_KEY = "PKCE_CODE_CHALLENGE"
        private const val PKCE_CODE_VERIFIER_KEY = "PKCE_CODE_VERIFIER"
    }
}
