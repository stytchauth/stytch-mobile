package com.stytch.sdk.oauth

import com.stytch.sdk.data.PublicTokenInfo
import com.stytch.sdk.data.SSOError
import com.stytch.sdk.data.StytchDispatchers
import com.stytch.sdk.pkce.PKCEClient
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import platform.AuthenticationServices.ASWebAuthenticationSession
import platform.Foundation.NSURL
import platform.Foundation.NSURLComponents
import platform.Foundation.NSURLQueryItem
import kotlin.coroutines.resume

public actual class OAuthProvider {
    public actual val isSupported: Boolean = true

    public actual suspend fun getOAuthToken(
        parameters: OAuthStartParameters,
        pkceClient: PKCEClient,
        dispatchers: StytchDispatchers,
        type: OAuthProviderType,
        baseUrl: String,
        publicTokenInfo: PublicTokenInfo,
    ): OAuthResult =
        if (type == OAuthProviderType.APPLE) {
            attemptAppleIdTokenAuthentication()
        } else {
            attemptStandardOAuthAuthentication(pkceClient, parameters, dispatchers, baseUrl, publicTokenInfo)
        }

    private suspend fun attemptAppleIdTokenAuthentication(): OAuthResult {
        TODO()
    }

    private suspend fun attemptStandardOAuthAuthentication(
        pkceClient: PKCEClient,
        parameters: OAuthStartParameters,
        dispatchers: StytchDispatchers,
        baseUrl: String,
        publicTokenInfo: PublicTokenInfo,
    ): OAuthResult =
        withContext(dispatchers.ioDispatcher) {
            val uri = generateOAuthStartUrl(baseUrl, publicTokenInfo, parameters, pkceClient)
            val scheme = getCallbackUrlScheme(parameters)
            suspendCancellableCoroutine { continuation ->
                NSURL.URLWithString(uri)?.let { nsUrl ->
                    val session =
                        ASWebAuthenticationSession(nsUrl, callbackURLScheme = scheme) { nsUrl, nsError ->
                            if (nsUrl == null) {
                                return@ASWebAuthenticationSession continuation.resume(OAuthResult.Error(SSOError.NoURIFound()))
                            }
                            if (nsError != null) {
                                return@ASWebAuthenticationSession continuation.resume(
                                    OAuthResult.Error(
                                        SSOError.UnknownError(nsError.localizedDescription),
                                    ),
                                )
                            }
                            val components = NSURLComponents(nsUrl, true)
                            val items = components.queryItems?.mapNotNull { it as NSURLQueryItem }
                            val token = items?.firstOrNull { it.name == "token" }?.value
                            if (token != null) {
                                continuation.resume(OAuthResult.ClassicToken(token = token))
                            } else {
                                continuation.resume(OAuthResult.Error(SSOError.NoTokenFound()))
                            }
                        }
                    session.presentationContextProvider = parameters.presentationContextProvider
                    session.start()
                } ?: run {
                    continuation.resume(OAuthResult.Error(SSOError.NoURIFound()))
                }
            }
        }

    private fun getCallbackUrlScheme(parameters: OAuthStartParameters): String {
        val loginScheme = parameters.loginRedirectUrl?.let { NSURL.URLWithString(it)?.scheme() }
        val signupScheme = parameters.signupRedirectUrl?.let { NSURL.URLWithString(it)?.scheme() }
        return loginScheme ?: signupScheme ?: "https"
    }
}
