package com.stytch.sdk.pkce

import com.stytch.sdk.data.PKCECodePair
import com.stytch.sdk.persistence.StytchPersistenceClient

public class PKCEClient(
    private val persistenceClient: StytchPersistenceClient,
) {
    public fun create(): PKCECodePair {
        val codeVerifier = persistenceClient.encryptionClient.generateCodeVerifier()
        val codeChallenge = persistenceClient.encryptionClient.generateCodeChallenge(codeVerifier)
        persistenceClient.platformPersistenceClient.saveData(PKCE_CODE_CHALLENGE_KEY, codeChallenge)
        persistenceClient.platformPersistenceClient.saveData(PKCE_CODE_VERIFIER_KEY, codeVerifier)
        return PKCECodePair(
            challenge = codeChallenge,
            verifier = codeVerifier,
        )
    }

    public fun revoke() {
        persistenceClient.platformPersistenceClient.removeData(PKCE_CODE_CHALLENGE_KEY)
        persistenceClient.platformPersistenceClient.removeData(PKCE_CODE_VERIFIER_KEY)
    }

    public fun retrieve(): PKCECodePair? {
        val challenge = persistenceClient.platformPersistenceClient.getData(PKCE_CODE_CHALLENGE_KEY) ?: return null
        val verifier = persistenceClient.platformPersistenceClient.getData(PKCE_CODE_VERIFIER_KEY) ?: return null
        return PKCECodePair(challenge, verifier)
    }

    private companion object {
        private const val PKCE_CODE_CHALLENGE_KEY = "PKCE_CODE_CHALLENGE"
        private const val PKCE_CODE_VERIFIER_KEY = "PKCE_CODE_VERIFIER"
    }
}
