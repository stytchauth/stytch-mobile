package com.stytch.sdk.consumer.passwords

import com.stytch.sdk.consumer.networking.ConsumerNetworkingClient
import com.stytch.sdk.consumer.networking.models.IPasswordsAuthenticateParameters
import com.stytch.sdk.consumer.networking.models.IPasswordsCreateParameters
import com.stytch.sdk.consumer.networking.models.IPasswordsEmailResetParameters
import com.stytch.sdk.consumer.networking.models.IPasswordsEmailResetStartParameters
import com.stytch.sdk.consumer.networking.models.IPasswordsExistingPasswordResetParameters
import com.stytch.sdk.consumer.networking.models.IPasswordsSessionResetParameters
import com.stytch.sdk.consumer.networking.models.IPasswordsStrengthCheckParameters
import com.stytch.sdk.consumer.networking.models.ITOTPsAuthenticateParameters
import com.stytch.sdk.consumer.networking.models.ITOTPsCreateParameters
import com.stytch.sdk.consumer.networking.models.ITOTPsRecoverParameters
import com.stytch.sdk.consumer.networking.models.PasswordsAuthenticateResponse
import com.stytch.sdk.consumer.networking.models.PasswordsCreateResponse
import com.stytch.sdk.consumer.networking.models.PasswordsEmailResetResponse
import com.stytch.sdk.consumer.networking.models.PasswordsEmailResetStartResponse
import com.stytch.sdk.consumer.networking.models.PasswordsExistingPasswordResetResponse
import com.stytch.sdk.consumer.networking.models.PasswordsSessionResetResponse
import com.stytch.sdk.consumer.networking.models.PasswordsStrengthCheckResponse
import com.stytch.sdk.consumer.networking.models.TOTPsAuthenticateResponse
import com.stytch.sdk.consumer.networking.models.TOTPsCreateResponse
import com.stytch.sdk.consumer.networking.models.TOTPsGetRecoveryCodesResponse
import com.stytch.sdk.consumer.networking.models.TOTPsRecoverResponse
import com.stytch.sdk.consumer.networking.models.toNetworkModel
import com.stytch.sdk.data.StytchDispatchers
import com.stytch.sdk.pkce.PKCEClient
import kotlinx.coroutines.withContext
import kotlin.js.JsExport

@JsExport
public interface PasswordsClient {
    public suspend fun authenticate(request: IPasswordsAuthenticateParameters): PasswordsAuthenticateResponse

    public suspend fun create(request: IPasswordsCreateParameters): PasswordsCreateResponse

    public suspend fun resetByEmailStart(request: IPasswordsEmailResetStartParameters): PasswordsEmailResetStartResponse

    public suspend fun resetByEmail(request: IPasswordsEmailResetParameters): PasswordsEmailResetResponse

    public suspend fun resetByExistingPassword(request: IPasswordsExistingPasswordResetParameters): PasswordsExistingPasswordResetResponse

    public suspend fun resetBySession(request: IPasswordsSessionResetParameters): PasswordsSessionResetResponse

    public suspend fun strengthCheck(request: IPasswordsStrengthCheckParameters): PasswordsStrengthCheckResponse
}

internal class PasswordsClientImpl(
    private val dispatchers: StytchDispatchers,
    private val networkingClient: ConsumerNetworkingClient,
    private val pkceClient: PKCEClient,
) : PasswordsClient {
    override suspend fun authenticate(request: IPasswordsAuthenticateParameters): PasswordsAuthenticateResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.passwordsAuthenticate(request.toNetworkModel())
            }
        }

    override suspend fun create(request: IPasswordsCreateParameters): PasswordsCreateResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.passwordsCreate(request.toNetworkModel())
            }
        }

    override suspend fun resetByEmailStart(request: IPasswordsEmailResetStartParameters): PasswordsEmailResetStartResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                val codePair = pkceClient.create()
                networkingClient.api.passwordsEmailResetStart(request.toNetworkModel(codeChallenge = codePair.challenge))
            }
        }

    override suspend fun resetByEmail(request: IPasswordsEmailResetParameters): PasswordsEmailResetResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                val codePair = pkceClient.retrieve() ?: throw IllegalStateException("PKCE is missing")
                networkingClient.api.passwordsEmailReset(request.toNetworkModel(codeVerifier = codePair.verifier))
            }
        }

    override suspend fun resetByExistingPassword(
        request: IPasswordsExistingPasswordResetParameters,
    ): PasswordsExistingPasswordResetResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.passwordsExistingPasswordReset(request.toNetworkModel())
            }
        }

    override suspend fun resetBySession(request: IPasswordsSessionResetParameters): PasswordsSessionResetResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.passwordsSessionReset(request.toNetworkModel())
            }
        }

    override suspend fun strengthCheck(request: IPasswordsStrengthCheckParameters): PasswordsStrengthCheckResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.passwordsStrengthCheck(request.toNetworkModel())
            }
        }
}
