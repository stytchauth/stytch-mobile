package com.stytch.sdk.oauth

import android.app.Activity
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
        try {
            if (type == OAuthProviderType.GOOGLE && googleCredentialConfiguration != null) {
                try {
                    attemptGoogleIdTokenAuthentication(application, pkceClient, googleCredentialConfiguration, dispatchers)
                } catch (e: Throwable) {
                    // If GCM fails, fallback to regular redirect OAuth
                    attemptStandardOAuthAuthentication(pkceClient, parameters, baseUrl, publicTokenInfo)
                }
            } else {
                attemptStandardOAuthAuthentication(pkceClient, parameters, baseUrl, publicTokenInfo)
            }
        } catch (e: Throwable) {
            OAuthResult.Error(e.message ?: e.toString())
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
                return@withContext OAuthResult.Error(
                    UnexpectedCredentialType(credentialResponse.credential.type).message ?: credentialResponse.credential.type,
                )
            }
            val idTokenResponse = GoogleIdTokenCredential.createFrom(credentialResponse.credential.data)
            OAuthResult.IDToken(
                token = idTokenResponse.idToken,
                nonce = nonce,
            )
        }

    public actual override suspend fun startBrowserFlow(
        url: String,
        parameters: OAuthStartParameters,
        dispatchers: StytchDispatchers,
    ): OAuthResult = launchSSOManagerActivity(url, parameters.activity)

    private suspend fun launchSSOManagerActivity(
        url: String,
        activity: Activity,
    ): OAuthResult =
        suspendCancellableCoroutine { continuation ->
            SSOManagerActivity.pendingResult = { result -> continuation.resume(result) }
            val intent = SSOManagerActivity.createBaseIntent(activity)
            intent.putExtra(URI_KEY, url)
            activity.startActivity(intent)
        }

    private suspend fun attemptStandardOAuthAuthentication(
        pkceClient: PKCEClient,
        parameters: OAuthStartParameters,
        baseUrl: String,
        publicTokenInfo: PublicTokenInfo,
    ): OAuthResult {
        val uri = generateOAuthStartUrl(packageName, baseUrl, publicTokenInfo, parameters, pkceClient)
        return launchSSOManagerActivity(uri, parameters.activity)
    }
}
