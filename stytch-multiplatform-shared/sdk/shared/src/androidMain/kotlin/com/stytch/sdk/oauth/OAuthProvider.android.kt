package com.stytch.sdk.oauth

import android.app.Application
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.stytch.sdk.data.GoogleCredentialConfiguration
import com.stytch.sdk.data.PublicTokenInfo
import com.stytch.sdk.data.StytchDispatchers
import com.stytch.sdk.oauth.SSOManagerActivity.Companion.URI_KEY
import com.stytch.sdk.pkce.PKCEClient
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

public actual class OAuthProvider(
    private val application: Application,
    internal val googleCredentialConfiguration: GoogleCredentialConfiguration? = null,
    private val packageName: String,
) : IOAuthProvider {
    public actual override val isSupported: Boolean = true

    public actual override suspend fun getOAuthToken(
        parameters: OAuthStartParameters,
        pkceClient: PKCEClient,
        dispatchers: StytchDispatchers,
        type: OAuthProviderType,
        baseUrl: String,
        publicTokenInfo: PublicTokenInfo,
    ): OAuthResult =
        if (type == OAuthProviderType.GOOGLE && googleCredentialConfiguration != null) {
            attemptGoogleIdTokenAuthentication(application, pkceClient, googleCredentialConfiguration, dispatchers)
        } else {
            attemptStandardOAuthAuthentication(pkceClient, parameters, baseUrl, publicTokenInfo)
        }

    private suspend fun attemptGoogleIdTokenAuthentication(
        application: Application,
        pkceClient: PKCEClient,
        googleCredentialConfiguration: GoogleCredentialConfiguration,
        dispatchers: StytchDispatchers,
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
        parameters: OAuthStartParameters,
        baseUrl: String,
        publicTokenInfo: PublicTokenInfo,
    ): OAuthResult {
        if (parameters.activity == null) throw MissingActivityException()
        val uri = generateOAuthStartUrl(packageName, baseUrl, publicTokenInfo, parameters, pkceClient)
        return suspendCancellableCoroutine { continuation ->
            SSOManagerActivity.pendingResult = { result -> continuation.resume(result) }
            val intent = SSOManagerActivity.createBaseIntent(parameters.activity)
            intent.putExtra(URI_KEY, uri)
            parameters.activity.startActivity(intent)
        }
    }
}
