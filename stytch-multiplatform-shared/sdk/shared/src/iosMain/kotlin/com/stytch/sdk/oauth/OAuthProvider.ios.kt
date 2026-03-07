package com.stytch.sdk.oauth

import com.stytch.sdk.data.PublicTokenInfo
import com.stytch.sdk.data.SSOError
import com.stytch.sdk.data.StytchDispatchers
import com.stytch.sdk.pkce.PKCEClient
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import platform.AuthenticationServices.ASAuthorization
import platform.AuthenticationServices.ASAuthorizationAppleIDCredential
import platform.AuthenticationServices.ASAuthorizationAppleIDProvider
import platform.AuthenticationServices.ASAuthorizationController
import platform.AuthenticationServices.ASAuthorizationControllerDelegateProtocol
import platform.AuthenticationServices.ASAuthorizationControllerPresentationContextProvidingProtocol
import platform.AuthenticationServices.ASAuthorizationScopeEmail
import platform.AuthenticationServices.ASAuthorizationScopeFullName
import platform.AuthenticationServices.ASPresentationAnchor
import platform.AuthenticationServices.ASWebAuthenticationPresentationContextProvidingProtocol
import platform.AuthenticationServices.ASWebAuthenticationSession
import platform.Foundation.NSError
import platform.Foundation.NSURL
import platform.Foundation.NSURLComponents
import platform.Foundation.NSURLQueryItem
import platform.darwin.NSObject
import kotlin.coroutines.resume

public actual class OAuthProvider(
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
        if (type == OAuthProviderType.APPLE) {
            attemptAppleIdTokenAuthentication(pkceClient, parameters, dispatchers)
        } else {
            attemptStandardOAuthAuthentication(pkceClient, parameters, dispatchers, baseUrl, publicTokenInfo)
        }

    private suspend fun attemptAppleIdTokenAuthentication(
        pkceClient: PKCEClient,
        parameters: OAuthStartParameters,
        dispatchers: StytchDispatchers,
    ): OAuthResult =
        withContext(dispatchers.ioDispatcher) {
            val nonce = pkceClient.create().challenge
            val provider = ASAuthorizationAppleIDProvider()
            val request = provider.createRequest()
            request.requestedScopes = listOf(ASAuthorizationScopeEmail, ASAuthorizationScopeFullName)
            request.nonce = nonce
            withContext(dispatchers.mainDispatcher) {
                suspendCancellableCoroutine { continuation ->
                    val delegate = AppleIdTokenDelegate(nonce, continuation)
                    val controller = ASAuthorizationController(authorizationRequests = listOf(request))
                    controller.presentationContextProvider = parameters.applePresentationContextProvider ?: DefaultPresenterContext()
                    controller.delegate = delegate
                    // Keep strong references alive until the coroutine completes —
                    // ASAuthorizationController.delegate is a weak ObjC reference, so without
                    // this the delegate gets GC'd before the callback fires.
                    continuation.invokeOnCancellation {
                        delegate
                        controller.cancel()
                    }
                    controller.performRequests()
                }
            }
        }

    private class AppleIdTokenDelegate(
        val nonce: String,
        val continuation: CancellableContinuation<OAuthResult>,
    ) : NSObject(),
        ASAuthorizationControllerDelegateProtocol {
        override fun authorizationController(
            controller: ASAuthorizationController,
            didCompleteWithAuthorization: ASAuthorization,
        ) {
            val credential =
                didCompleteWithAuthorization.credential as? ASAuthorizationAppleIDCredential ?: return continuation.resume(
                    OAuthResult.Error(InvalidAuthorizationCredential()),
                )
            val idToken =
                credential.identityToken?.toString()
                    ?: return continuation.resume(OAuthResult.Error(MissingAuthorizationCredentialIDToken()))
            val name =
                listOf(
                    credential.fullName?.givenName,
                    credential.fullName?.middleName,
                    credential.fullName?.familyName,
                ).joinToString(" ")
            continuation.resume(
                OAuthResult.IDToken(
                    token = idToken,
                    name = name,
                    nonce = nonce,
                ),
            )
        }

        override fun authorizationController(
            controller: ASAuthorizationController,
            didCompleteWithError: NSError,
        ) {
            continuation.resume(OAuthResult.Error(SSOError.UnknownError(didCompleteWithError.localizedDescription)))
        }
    }

    private suspend fun attemptStandardOAuthAuthentication(
        pkceClient: PKCEClient,
        parameters: OAuthStartParameters,
        dispatchers: StytchDispatchers,
        baseUrl: String,
        publicTokenInfo: PublicTokenInfo,
    ): OAuthResult =
        withContext(dispatchers.ioDispatcher) {
            val uri = generateOAuthStartUrl(packageName, baseUrl, publicTokenInfo, parameters, pkceClient)
            val scheme = getCallbackUrlScheme(parameters)
            withContext(dispatchers.mainDispatcher) {
                suspendCancellableCoroutine { continuation ->
                    NSURL.URLWithString(uri)?.let { nsUrl ->
                        val session =
                            ASWebAuthenticationSession(nsUrl, callbackURLScheme = scheme) { nsUrl, nsError ->
                                if (nsError != null) {
                                    return@ASWebAuthenticationSession continuation.resume(
                                        OAuthResult.Error(
                                            SSOError.UnknownError(nsError.localizedDescription),
                                        ),
                                    )
                                }
                                if (nsUrl == null) {
                                    return@ASWebAuthenticationSession continuation.resume(OAuthResult.Error(SSOError.NoURIFound()))
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
                        session.presentationContextProvider = parameters.oauthPresentationContextProvider ?: DefaultPresenterContext()
                        session.start()
                    } ?: run {
                        continuation.resume(OAuthResult.Error(SSOError.NoURIFound()))
                    }
                }
            }
        }

    private class DefaultPresenterContext :
        NSObject(),
        ASWebAuthenticationPresentationContextProvidingProtocol,
        ASAuthorizationControllerPresentationContextProvidingProtocol {
        override fun presentationAnchorForWebAuthenticationSession(session: ASWebAuthenticationSession): ASPresentationAnchor =
            ASPresentationAnchor()

        override fun presentationAnchorForAuthorizationController(controller: ASAuthorizationController): ASPresentationAnchor =
            ASPresentationAnchor()
    }

    private fun getCallbackUrlScheme(parameters: OAuthStartParameters): String {
        val loginScheme = parameters.loginRedirectUrl?.let { NSURL.URLWithString(it)?.scheme() }
        val signupScheme = parameters.signupRedirectUrl?.let { NSURL.URLWithString(it)?.scheme() }
        return loginScheme ?: signupScheme ?: "https"
    }
}
