package com.stytch.sdk.b2b.passwords

import com.stytch.sdk.StytchApi
import com.stytch.sdk.b2b.StytchB2BAuthenticationStateManager
import com.stytch.sdk.b2b.networking.B2BNetworkingClient
import com.stytch.sdk.b2b.networking.models.B2BPasswordAuthenticateResponse
import com.stytch.sdk.b2b.networking.models.B2BPasswordEmailResetResponse
import com.stytch.sdk.b2b.networking.models.B2BPasswordEmailResetStartResponse
import com.stytch.sdk.b2b.networking.models.B2BPasswordExistingPasswordResetResponse
import com.stytch.sdk.b2b.networking.models.B2BPasswordSessionResetResponse
import com.stytch.sdk.b2b.networking.models.B2BPasswordStrengthCheckResponse
import com.stytch.sdk.b2b.networking.models.IB2BPasswordAuthenticateParameters
import com.stytch.sdk.b2b.networking.models.IB2BPasswordEmailResetParameters
import com.stytch.sdk.b2b.networking.models.IB2BPasswordEmailResetStartParameters
import com.stytch.sdk.b2b.networking.models.IB2BPasswordExistingPasswordResetParameters
import com.stytch.sdk.b2b.networking.models.IB2BPasswordSessionResetParameters
import com.stytch.sdk.b2b.networking.models.IB2BPasswordStrengthCheckParameters
import com.stytch.sdk.b2b.networking.models.toNetworkModel
import com.stytch.sdk.data.StytchDispatchers
import com.stytch.sdk.data.StytchError
import com.stytch.sdk.pkce.MissingPKCEException
import com.stytch.sdk.pkce.PKCEClient
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException
import kotlin.js.JsExport

/** B2B password-based authentication methods. */
@StytchApi
@JsExport
public interface B2BPasswordsClient {
    /** Email password reset methods. */
    public val email: B2BPasswordsEmailClient

    /** Existing-password reset methods. */
    public val existingPassword: B2BPasswordsExistingPasswordClient

    /** Session-based password reset methods. */
    public val session: B2BPasswordsSessionClient

    /** Authenticates a member with their email address and password. */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun authenticate(request: IB2BPasswordAuthenticateParameters): B2BPasswordAuthenticateResponse

    /** Checks the strength of a provided password and returns feedback. */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun strengthCheck(request: IB2BPasswordStrengthCheckParameters): B2BPasswordStrengthCheckResponse
}

/** Email-based password reset methods. */
@StytchApi
@JsExport
public interface B2BPasswordsEmailClient {
    /** Initiates an email-based password reset by sending a reset link to the member's email. */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun resetStart(request: IB2BPasswordEmailResetStartParameters): B2BPasswordEmailResetStartResponse

    /** Completes the email password reset flow, updating the password using the token from the reset link. */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun reset(request: IB2BPasswordEmailResetParameters): B2BPasswordEmailResetResponse
}

/** Existing-password reset — updates the password using the current password for verification. */
@StytchApi
@JsExport
public interface B2BPasswordsExistingPasswordClient {
    /** Resets a member's password using their existing password for verification. */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun reset(request: IB2BPasswordExistingPasswordResetParameters): B2BPasswordExistingPasswordResetResponse
}

/** Session-based password reset — updates the password using an active session for verification. */
@StytchApi
@JsExport
public interface B2BPasswordsSessionClient {
    /** Resets a member's password using their active session for verification (no existing password required). */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun reset(request: IB2BPasswordSessionResetParameters): B2BPasswordSessionResetResponse
}

internal class B2BPasswordsClientImpl(
    private val dispatchers: StytchDispatchers,
    private val networkingClient: B2BNetworkingClient,
    private val pkceClient: PKCEClient,
    private val sessionManager: StytchB2BAuthenticationStateManager,
) : B2BPasswordsClient {
    override val email: B2BPasswordsEmailClient =
        B2BPasswordsEmailClientImpl(dispatchers, networkingClient, pkceClient, sessionManager)
    override val existingPassword: B2BPasswordsExistingPasswordClient =
        B2BPasswordsExistingPasswordClientImpl(dispatchers, networkingClient)
    override val session: B2BPasswordsSessionClient =
        B2BPasswordsSessionClientImpl(dispatchers, networkingClient)

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun authenticate(request: IB2BPasswordAuthenticateParameters): B2BPasswordAuthenticateResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.b2BPasswordAuthenticate(
                    request.toNetworkModel(intermediateSessionToken = sessionManager.intermediateSessionToken),
                )
            }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun strengthCheck(request: IB2BPasswordStrengthCheckParameters): B2BPasswordStrengthCheckResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.b2BPasswordStrengthCheck(request.toNetworkModel())
            }
        }
}

internal class B2BPasswordsEmailClientImpl(
    private val dispatchers: StytchDispatchers,
    private val networkingClient: B2BNetworkingClient,
    private val pkceClient: PKCEClient,
    private val sessionManager: StytchB2BAuthenticationStateManager,
) : B2BPasswordsEmailClient {
    @Throws(StytchError::class, CancellationException::class)
    override suspend fun resetStart(request: IB2BPasswordEmailResetStartParameters): B2BPasswordEmailResetStartResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                val codePair = pkceClient.create()
                networkingClient.api.b2BPasswordEmailResetStart(request.toNetworkModel(codeChallenge = codePair.challenge))
            }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun reset(request: IB2BPasswordEmailResetParameters): B2BPasswordEmailResetResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                val codePair = pkceClient.retrieve() ?: throw MissingPKCEException()
                networkingClient.api
                    .b2BPasswordEmailReset(
                        request.toNetworkModel(
                            codeVerifier = codePair.verifier,
                            intermediateSessionToken = sessionManager.intermediateSessionToken,
                        ),
                    ).also { pkceClient.revoke() }
            }
        }
}

internal class B2BPasswordsExistingPasswordClientImpl(
    private val dispatchers: StytchDispatchers,
    private val networkingClient: B2BNetworkingClient,
) : B2BPasswordsExistingPasswordClient {
    @Throws(StytchError::class, CancellationException::class)
    override suspend fun reset(request: IB2BPasswordExistingPasswordResetParameters): B2BPasswordExistingPasswordResetResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.b2BPasswordExistingPasswordReset(request.toNetworkModel())
            }
        }
}

internal class B2BPasswordsSessionClientImpl(
    private val dispatchers: StytchDispatchers,
    private val networkingClient: B2BNetworkingClient,
) : B2BPasswordsSessionClient {
    @Throws(StytchError::class, CancellationException::class)
    override suspend fun reset(request: IB2BPasswordSessionResetParameters): B2BPasswordSessionResetResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                networkingClient.api.b2BPasswordSessionReset(request.toNetworkModel())
            }
        }
}
