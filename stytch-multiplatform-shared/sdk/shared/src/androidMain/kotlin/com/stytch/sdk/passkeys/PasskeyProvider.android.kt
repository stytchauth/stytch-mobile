package com.stytch.sdk.passkeys

import androidx.credentials.CreatePublicKeyCredentialRequest
import androidx.credentials.CreatePublicKeyCredentialResponse
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetPublicKeyCredentialOption
import androidx.credentials.PublicKeyCredential
import com.stytch.sdk.data.StytchDispatchers
import kotlinx.coroutines.withContext

public actual class PasskeyProvider : IPasskeyProvider {
    public actual override val isSupported: Boolean = true

    public actual override suspend fun createPublicKeyCredential(
        parameters: PasskeysParameters,
        dispatchers: StytchDispatchers,
        json: String,
    ): String {
        val createPublicKeyCredentialRequest =
            CreatePublicKeyCredentialRequest(
                requestJson = json,
                preferImmediatelyAvailableCredentials = parameters.preferImmediatelyAvailableCredentials,
            )
        val credentialManager = CredentialManager.create(parameters.activity)
        val response =
            withContext(dispatchers.mainDispatcher) {
                credentialManager.createCredential(
                    context = parameters.activity,
                    request = createPublicKeyCredentialRequest,
                )
            } as CreatePublicKeyCredentialResponse
        return response.registrationResponseJson
    }

    public actual override suspend fun getPublicKeyCredential(
        parameters: PasskeysParameters,
        dispatchers: StytchDispatchers,
        json: String,
    ): String {
        val getPublicKeyCredentialOption = GetPublicKeyCredentialOption(requestJson = json)
        val credentialManager = CredentialManager.create(parameters.activity)
        val response =
            withContext(dispatchers.mainDispatcher) {
                credentialManager.getCredential(
                    context = parameters.activity,
                    request =
                        GetCredentialRequest(
                            credentialOptions = listOf(getPublicKeyCredentialOption),
                            preferImmediatelyAvailableCredentials = parameters.preferImmediatelyAvailableCredentials,
                        ),
                )
            }.credential as PublicKeyCredential
        return response.authenticationResponseJson
    }
}
