package com.stytch.sdk.oauth

import android.app.Activity.RESULT_OK
import android.app.Application
import android.content.Intent
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.stytch.sdk.data.GoogleCredentialConfiguration
import com.stytch.sdk.data.PublicTokenInfo
import com.stytch.sdk.data.SSOError
import com.stytch.sdk.data.StytchDispatchers
import com.stytch.sdk.oauth.SSOManagerActivity.Companion.URI_KEY
import com.stytch.sdk.pkce.PKCEClient
import io.ktor.http.URLBuilder
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.Serializable
import kotlin.coroutines.resume

public actual class OAuthProvider(
    private val application: Application,
    internal val googleCredentialConfiguration: GoogleCredentialConfiguration? = null,
) {
    public actual val isSupported: Boolean = true

    public actual suspend fun getOAuthToken(
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
        val codePair = pkceClient.create()
        val finalParameters =
            mutableMapOf(
                "public_token" to publicTokenInfo.publicToken,
                "code_challenge" to codePair.challenge,
                "login_redirect_url" to parameters.loginRedirectUrl,
                "signup_redirect_url" to parameters.signupRedirectUrl,
                "custom_scopes" to parameters.customScopes?.joinToString(" "),
                "oauth_attach_token" to parameters.oauthAttachToken,
            )
        parameters.providerParams?.entries?.forEach { (key, value) ->
            finalParameters["provider_$key"] = value
        }
        val uri = URLBuilder(baseUrl)
        finalParameters.forEach { (key, value) ->
            if (value?.isNotEmpty() == true) {
                uri.parameters.append(key, value)
            }
        }
        return suspendCancellableCoroutine { continuation ->
            val launcher =
                parameters.activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                    val token = result.data?.data?.getQueryParameter("token")
                    val response =
                        if (result.resultCode == RESULT_OK && token != null) {
                            OAuthResult.ClassicToken(token = token)
                        } else {
                            OAuthResult.Error(
                                OAuthFailedException(
                                    resultCode = result.resultCode,
                                    message =
                                        if (token == null) {
                                            "Missing Token"
                                        } else {
                                            "SSO/OAuth Failed"
                                        },
                                    cause = result.data?.getSerializable("StytchSSOError", SSOError::class.java),
                                ),
                            )
                        }
                    continuation.resume(response)
                }
            val intent = SSOManagerActivity.createBaseIntent(parameters.activity)
            intent.putExtra(URI_KEY, uri.build().toString())
            launcher.launch(intent)
        }
    }
}

private fun <T : Serializable?> Intent.getSerializable(
    key: String,
    clazz: Class<T>,
): T? =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        this.getSerializableExtra(key, clazz)
    } else {
        this.getSerializableExtra(key) as T
    }
