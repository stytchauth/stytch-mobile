package com.stytch.sdk.pkce

import com.stytch.sdk.data.PKCECodePair
import com.stytch.sdk.encryption.StytchEncryptionClient
import com.stytch.sdk.persistence.StytchPersistenceClient
import io.ktor.util.encodeBase64

public class PKCEClient(
    private val encryptionClient: StytchEncryptionClient,
    private val persistenceClient: StytchPersistenceClient,
) {
    public suspend fun create(): PKCECodePair {
        val codeVerifier = encryptionClient.generateCodeVerifier().encodeBase64().stytchUrlEncode()
        val codeChallenge =
            encryptionClient
                .generateCodeChallenge(codeVerifier.encodeToByteArray())
                .encodeBase64()
                .stytchUrlEncode()
        persistenceClient.save(PKCE_CODE_CHALLENGE_KEY, codeChallenge)
        persistenceClient.save(PKCE_CODE_VERIFIER_KEY, codeVerifier)
        return PKCECodePair(
            challenge = codeChallenge,
            verifier = codeVerifier,
        )
    }

    public suspend fun revoke() {
        persistenceClient.remove(PKCE_CODE_CHALLENGE_KEY)
        persistenceClient.remove(PKCE_CODE_VERIFIER_KEY)
    }

    public suspend fun retrieve(): PKCECodePair? {
        val challenge = persistenceClient.get<String>(PKCE_CODE_CHALLENGE_KEY, null) ?: return null
        val verifier = persistenceClient.get<String>(PKCE_CODE_VERIFIER_KEY, null) ?: return null
        return PKCECodePair(challenge, verifier)
    }

    private companion object {
        private const val PKCE_CODE_CHALLENGE_KEY = "PKCE_CODE_CHALLENGE"
        private const val PKCE_CODE_VERIFIER_KEY = "PKCE_CODE_VERIFIER"
    }
}

private fun String.stytchUrlEncode(): String =
    this
        .replace("+", "-")
        .replace("/", "_")
        .replace("=", "")
