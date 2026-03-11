package com.stytch.sdk.oauth

import com.stytch.sdk.data.PublicTokenInfo
import com.stytch.sdk.data.SSOError
import com.stytch.sdk.data.StytchDispatchers
import com.stytch.sdk.encryption.StytchEncryptionClient
import com.stytch.sdk.encryption.toByteArray
import com.stytch.sdk.pkce.PKCEClient
import io.ktor.util.encodeBase64
import io.ktor.utils.io.core.toByteArray
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
import kotlin.toString

public actual class OAuthProvider(
    private val packageName: String,
    private val encryptionClient: StytchEncryptionClient,
) : IOAuthProvider {
    public actual override val isSupported: Boolean = true

    // Strong references to keep SIWA objects alive past coroutine cancellation.
    // ASAuthorizationController.delegate is a weak ObjC ref, and React Native's
    // app-inactive lifecycle event can cancel the coroutine right as Apple fires
    // the callback — releasing the delegate before the callback lands.
    private var activeAppleDelegate: AppleIdTokenDelegate? = null
    private var activeAppleController: ASAuthorizationController? = null

    public actual override suspend fun getOAuthToken(
        parameters: OAuthStartParameters,
        pkceClient: PKCEClient,
        dispatchers: StytchDispatchers,
        type: OAuthProviderType,
        baseUrl: String,
        publicTokenInfo: PublicTokenInfo,
    ): OAuthResult =
        try {
            if (type == OAuthProviderType.APPLE) {
                attemptAppleIdTokenAuthentication(parameters, dispatchers)
            } else {
                attemptStandardOAuthAuthentication(pkceClient, parameters, dispatchers, baseUrl, publicTokenInfo)
            }
        } catch (e: Throwable) {
            OAuthResult.Error(e.message ?: e.toString())
        }

    private suspend fun attemptAppleIdTokenAuthentication(
        parameters: OAuthStartParameters,
        dispatchers: StytchDispatchers,
    ): OAuthResult =
        withContext(dispatchers.ioDispatcher) {
            /* Google and Apple nonces are handled differently by the API, and it ALWAYS messes me up
             * For Google, we do nonce => S256 hash => B64, send the same string to Google and the API, and compare them
             * For Apple, we do nonce => hex string, and send THAT to API; but send the S256+B64 string to Apple. Then API does the same S256+B64 for comparison
             * API implementations:
             * https://github.com/stytchauth/api/blob/main/pkg/oauth/internal/consumer/google.go#L130
             * https://github.com/stytchauth/api/blob/main/pkg/oauth/internal/consumer/apple.go#L133
             */
            val rawNonce = encryptionClient.generateCodeVerifier().toHexString() // send to API
            val encodedNonce = encryptionClient.generateCodeChallenge(rawNonce.toByteArray()).encodeBase64() // send to Apple
            val provider = ASAuthorizationAppleIDProvider()
            val request = provider.createRequest()
            request.requestedScopes = listOf(ASAuthorizationScopeEmail, ASAuthorizationScopeFullName)
            request.nonce = encodedNonce
            withContext(dispatchers.mainDispatcher) {
                suspendCancellableCoroutine { continuation ->
                    val delegate = AppleIdTokenDelegate(rawNonce, continuation)
                    val controller = ASAuthorizationController(authorizationRequests = listOf(request))
                    controller.presentationContextProvider = parameters.applePresentationContextProvider ?: DefaultPresenterContext()
                    controller.delegate = delegate
                    activeAppleDelegate = delegate
                    activeAppleController = controller
                    continuation.invokeOnCancellation {
                        activeAppleController?.cancel()
                    }
                    controller.performRequests()
                }
            }.also {
                activeAppleDelegate = null
                activeAppleController = null
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
                    OAuthResult.Error("Invalid authorization credential"),
                )
            val idToken =
                credential.identityToken ?: return continuation.resume(OAuthResult.Error("Missing authorization credential ID token"))
            val name =
                listOf(
                    credential.fullName?.givenName,
                    credential.fullName?.middleName,
                    credential.fullName?.familyName,
                ).joinToString(" ")
            continuation.resume(
                OAuthResult.IDToken(
                    token = idToken.toByteArray().decodeToString(),
                    name = name,
                    nonce = nonce,
                ),
            )
        }

        override fun authorizationController(
            controller: ASAuthorizationController,
            didCompleteWithError: NSError,
        ) {
            continuation.resume(OAuthResult.Error(didCompleteWithError.localizedDescription))
        }
    }

    public actual override suspend fun startBrowserFlow(
        url: String,
        parameters: OAuthStartParameters,
        dispatchers: StytchDispatchers,
    ): OAuthResult =
        withContext(dispatchers.ioDispatcher) {
            launchWebAuthSession(url, packageName, parameters.oauthPresentationContextProvider ?: DefaultPresenterContext(), dispatchers)
        }

    private suspend fun launchWebAuthSession(
        url: String,
        scheme: String,
        presentationContextProvider: ASWebAuthenticationPresentationContextProvidingProtocol,
        dispatchers: StytchDispatchers,
    ): OAuthResult =
        withContext(dispatchers.mainDispatcher) {
            suspendCancellableCoroutine { continuation ->
                NSURL.URLWithString(url)?.let { nsUrl ->
                    val session =
                        ASWebAuthenticationSession(nsUrl, callbackURLScheme = scheme) { nsUrl, nsError ->
                            if (nsError != null) {
                                return@ASWebAuthenticationSession continuation.resume(
                                    OAuthResult.Error(nsError.localizedDescription),
                                )
                            }
                            if (nsUrl == null) {
                                return@ASWebAuthenticationSession continuation.resume(OAuthResult.Error(SSOError.NoURIFound().message))
                            }
                            val components = NSURLComponents(nsUrl, true)
                            val items = components.queryItems?.mapNotNull { it as NSURLQueryItem }
                            val token = items?.firstOrNull { it.name == "token" }?.value
                            if (token != null) {
                                continuation.resume(OAuthResult.ClassicToken(token = token))
                            } else {
                                continuation.resume(OAuthResult.Error(SSOError.NoTokenFound().message))
                            }
                        }
                    session.presentationContextProvider = presentationContextProvider
                    session.start()
                } ?: run {
                    continuation.resume(OAuthResult.Error(SSOError.NoURIFound().message))
                }
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
            launchWebAuthSession(uri, packageName, parameters.oauthPresentationContextProvider ?: DefaultPresenterContext(), dispatchers)
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
}

private fun ByteArray.toHexString(): String = joinToString("") { (it.toInt() and 0xff).toString(16).padStart(2, '0') }
