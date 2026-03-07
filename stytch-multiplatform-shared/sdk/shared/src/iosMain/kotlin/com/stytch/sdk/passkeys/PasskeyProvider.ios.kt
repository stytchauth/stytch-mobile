package com.stytch.sdk.passkeys

import com.stytch.sdk.data.StytchDispatchers
import com.stytch.sdk.encryption.toNSData
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.serialization.json.Json
import platform.AuthenticationServices.ASAuthorization
import platform.AuthenticationServices.ASAuthorizationController
import platform.AuthenticationServices.ASAuthorizationControllerDelegateProtocol
import platform.AuthenticationServices.ASAuthorizationControllerRequestOptionPreferImmediatelyAvailableCredentials
import platform.AuthenticationServices.ASAuthorizationCredentialProtocol
import platform.AuthenticationServices.ASAuthorizationPlatformPublicKeyCredentialProvider
import platform.AuthenticationServices.ASAuthorizationPublicKeyCredentialAssertionProtocol
import platform.AuthenticationServices.ASAuthorizationPublicKeyCredentialRegistrationProtocol
import platform.AuthenticationServices.ASAuthorizationRequest
import platform.Foundation.NSError
import platform.Foundation.base64Encoding
import platform.darwin.NSObject
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

public actual class PasskeyProvider : IPasskeyProvider {
    private val jsonProvider =
        Json {
            ignoreUnknownKeys = true
        }
    public actual override val isSupported: Boolean = true

    public actual override suspend fun createPublicKeyCredential(
        parameters: PasskeysParameters,
        dispatchers: StytchDispatchers,
        json: String,
    ): String {
        val platformProvider = ASAuthorizationPlatformPublicKeyCredentialProvider(relyingPartyIdentifier = parameters.domain)
        val request = jsonProvider.decodeFromString<PasskeysRegisterResponse>(json)
        val credentialRequest =
            platformProvider.createCredentialRegistrationRequestWithChallenge(
                challenge = request.challenge.encodeToByteArray().toNSData(),
                name = request.user.displayName,
                userID =
                    request.user.id
                        .encodeToByteArray()
                        .toNSData(),
            )
        val credential =
            getCredentialResponse(
                listOf(credentialRequest),
                parameters.preferImmediatelyAvailableCredentials,
            ) as ASAuthorizationPublicKeyCredentialRegistrationProtocol ?: throw InvalidPasskeyCredentialError()
        return credential.rawClientDataJSON.base64Encoding()
    }

    public actual override suspend fun getPublicKeyCredential(
        parameters: PasskeysParameters,
        dispatchers: StytchDispatchers,
        json: String,
    ): String {
        val platformProvider = ASAuthorizationPlatformPublicKeyCredentialProvider(relyingPartyIdentifier = parameters.domain)
        val request = jsonProvider.decodeFromString<PasskeysAuthenticateResponse>(json)
        val credentialRequest =
            platformProvider.createCredentialAssertionRequestWithChallenge(
                challenge = request.challenge.encodeToByteArray().toNSData(),
            )
        val credential =
            getCredentialResponse(
                listOf(credentialRequest),
                parameters.preferImmediatelyAvailableCredentials,
            ) as ASAuthorizationPublicKeyCredentialAssertionProtocol ?: throw InvalidPasskeyCredentialError()
        return credential.rawClientDataJSON.base64Encoding()
    }

    private suspend fun getCredentialResponse(
        requests: List<ASAuthorizationRequest>,
        preferImmediatelyAvailableCredentials: Boolean,
    ): ASAuthorizationCredentialProtocol {
        val controller = ASAuthorizationController(authorizationRequests = requests)
        val credential =
            suspendCancellableCoroutine { continuation ->
                controller.delegate = PasskeysDelegate(continuation)
                if (preferImmediatelyAvailableCredentials) {
                    controller.performRequestsWithOptions(
                        ASAuthorizationControllerRequestOptionPreferImmediatelyAvailableCredentials,
                    )
                } else {
                    controller.performRequests()
                }
            }
        return credential
    }

    private class PasskeysDelegate(
        private val continuation: Continuation<ASAuthorizationCredentialProtocol>,
    ) : NSObject(),
        ASAuthorizationControllerDelegateProtocol {
        override fun authorizationController(
            controller: ASAuthorizationController,
            didCompleteWithAuthorization: ASAuthorization,
        ) {
            continuation.resume(didCompleteWithAuthorization.credential)
        }

        override fun authorizationController(
            controller: ASAuthorizationController,
            didCompleteWithError: NSError,
        ) {
            continuation.resumeWithException(PasskeyAuthorizationFailedError(didCompleteWithError.localizedDescription))
        }
    }
}
