package com.stytch.sdk.oauth

import android.app.Application
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.stytch.sdk.data.GoogleCredentialConfiguration
import com.stytch.sdk.data.StytchDispatchers
import com.stytch.sdk.pkce.PKCEClient
import kotlinx.coroutines.withContext

public actual class OAuthProvider(
    private val application: Application,
    internal val googleCredentialConfiguration: GoogleCredentialConfiguration? = null,
) {
    public actual val isSupported: Boolean = true

    public actual suspend fun getOAuthTokenFromUrl(
        pkceClient: PKCEClient,
        dispatchers: StytchDispatchers,
        type: OAuthProviderType,
        url: String,
    ): OAuthResult =
        if (type == OAuthProviderType.GOOGLE && googleCredentialConfiguration != null) {
            attemptGoogleIdTokenAuthentication(application, pkceClient, googleCredentialConfiguration, dispatchers, url)
        } else {
            attemptStandardOAuthAuthentication(pkceClient, dispatchers, url)
        }

    private suspend fun attemptGoogleIdTokenAuthentication(
        application: Application,
        pkceClient: PKCEClient,
        googleCredentialConfiguration: GoogleCredentialConfiguration,
        dispatchers: StytchDispatchers,
        url: String,
    ): OAuthResult =
        withContext(dispatchers.ioDispatcher) {
            val nonce = pkceClient.create().challenge
            val credentialResponse =
                withContext(dispatchers.mainDispatcher) {
                    val credentialManager = CredentialManager.create(application)
                    val option: GetGoogleIdOption =
                        GetGoogleIdOption
                            .Builder()
                            .setServerClientId(googleCredentialConfiguration.googleClientId)
                            .setNonce(nonce)
                            .setAutoSelectEnabled(googleCredentialConfiguration.autoSelectEnabled)
                            .build()
                    val request: GetCredentialRequest =
                        GetCredentialRequest
                            .Builder()
                            .addCredentialOption(option)
                            .setPreferImmediatelyAvailableCredentials(true)
                            .build()
                    credentialManager.getCredential(application, request)
                }
            if (credentialResponse.credential.type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                return@withContext OAuthResult.Error(UnexpectedCredentialType(credentialResponse.credential.type))
            }
            val idTokenResponse = GoogleIdTokenCredential.createFrom(credentialResponse.credential.data)
            OAuthResult.IDToken(
                token = idTokenResponse.idToken,
                nonce = nonce,
            )
        }

    private suspend fun attemptStandardOAuthAuthentication(
        pkceClient: PKCEClient,
        dispatchers: StytchDispatchers,
        url: String,
    ): OAuthResult = TODO()
}
