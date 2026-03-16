package com.stytch.sdk.consumer.passwords

import com.stytch.sdk.consumer.networking.ConsumerNetworkingClient
import com.stytch.sdk.consumer.networking.models.IPasswordsAuthenticateParameters
import com.stytch.sdk.consumer.networking.models.IPasswordsCreateParameters
import com.stytch.sdk.consumer.networking.models.IPasswordsEmailResetParameters
import com.stytch.sdk.consumer.networking.models.IPasswordsEmailResetStartParameters
import com.stytch.sdk.consumer.networking.models.IPasswordsExistingPasswordResetParameters
import com.stytch.sdk.consumer.networking.models.IPasswordsSessionResetParameters
import com.stytch.sdk.consumer.networking.models.IPasswordsStrengthCheckParameters
import com.stytch.sdk.consumer.networking.models.PasswordsAuthenticateResponse
import com.stytch.sdk.consumer.networking.models.PasswordsCreateResponse
import com.stytch.sdk.consumer.networking.models.PasswordsEmailResetResponse
import com.stytch.sdk.consumer.networking.models.PasswordsEmailResetStartResponse
import com.stytch.sdk.consumer.networking.models.PasswordsExistingPasswordResetResponse
import com.stytch.sdk.consumer.networking.models.PasswordsSessionResetResponse
import com.stytch.sdk.consumer.networking.models.PasswordsStrengthCheckResponse
import com.stytch.sdk.consumer.networking.models.toNetworkModel
import com.stytch.sdk.data.StytchDispatchers
import com.stytch.sdk.data.StytchError
import com.stytch.sdk.pkce.MissingPKCEException
import com.stytch.sdk.pkce.PKCEClient
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException
import kotlin.js.JsExport
import com.stytch.sdk.StytchApi

@StytchApi
@JsExport
public interface PasswordsClient {
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun authenticate(request: IPasswordsAuthenticateParameters): PasswordsAuthenticateResponse

    @Throws(StytchError::class, CancellationException::class)
    public suspend fun create(request: IPasswordsCreateParameters): PasswordsCreateResponse

    @Throws(StytchError::class, CancellationException::class)
    public suspend fun resetByEmailStart(request: IPasswordsEmailResetStartParameters): PasswordsEmailResetStartResponse

    @Throws(StytchError::class, CancellationException::class)
    public suspend fun resetByEmail(request: IPasswordsEmailResetParameters): PasswordsEmailResetResponse

    @Throws(StytchError::class, CancellationException::class)
    public suspend fun resetByExistingPassword(request: IPasswordsExistingPasswordResetParameters): PasswordsExistingPasswordResetResponse

    @Throws(StytchError::class, CancellationException::class)
    public suspend fun resetBySession(request: IPasswordsSessionResetParameters): PasswordsSessionResetResponse

    @Throws(StytchError::class, CancellationException::class)
    public suspend fun strengthCheck(request: IPasswordsStrengthCheckParameters): PasswordsStrengthCheckResponse
}

internal class PasswordsClientImpl(
    private val dispatchers: StytchDispatchers,
    private val networkingClient: ConsumerNetworkingClient,
    private val pkceClient: PKCEClient,
) : PasswordsClient {
    @Throws(StytchError::class, CancellationException::class)
    override suspend fun authenticate(request: IPasswordsAuthenticateParameters): PasswordsAuthenticateResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.passwordsAuthenticate(request.toNetworkModel())
            }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun create(request: IPasswordsCreateParameters): PasswordsCreateResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.passwordsCreate(request.toNetworkModel())
            }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun resetByEmailStart(request: IPasswordsEmailResetStartParameters): PasswordsEmailResetStartResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                val codePair = pkceClient.create()
                networkingClient.api.passwordsEmailResetStart(request.toNetworkModel(codeChallenge = codePair.challenge))
            }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun resetByEmail(request: IPasswordsEmailResetParameters): PasswordsEmailResetResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                val codePair = pkceClient.retrieve() ?: throw MissingPKCEException()
                networkingClient.api.passwordsEmailReset(request.toNetworkModel(codeVerifier = codePair.verifier))
            }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun resetByExistingPassword(
        request: IPasswordsExistingPasswordResetParameters,
    ): PasswordsExistingPasswordResetResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.passwordsExistingPasswordReset(request.toNetworkModel())
            }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun resetBySession(request: IPasswordsSessionResetParameters): PasswordsSessionResetResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.passwordsSessionReset(request.toNetworkModel())
            }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun strengthCheck(request: IPasswordsStrengthCheckParameters): PasswordsStrengthCheckResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.passwordsStrengthCheck(request.toNetworkModel())
            }
        }
}
