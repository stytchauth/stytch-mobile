package com.stytch.sdk.consumer.magicLinks

import com.stytch.sdk.StytchApi
import com.stytch.sdk.consumer.StytchConsumerAuthenticationStateManager
import com.stytch.sdk.consumer.networking.ConsumerNetworkingClient
import com.stytch.sdk.consumer.networking.models.IMagicLinksAuthenticateParameters
import com.stytch.sdk.consumer.networking.models.IMagicLinksEmailLoginOrCreateParameters
import com.stytch.sdk.consumer.networking.models.IMagicLinksEmailSendSecondaryParameters
import com.stytch.sdk.consumer.networking.models.MagicLinksAuthenticateResponse
import com.stytch.sdk.consumer.networking.models.MagicLinksEmailLoginOrCreateResponse
import com.stytch.sdk.consumer.networking.models.MagicLinksEmailSendSecondaryResponse
import com.stytch.sdk.consumer.networking.models.toNetworkModel
import com.stytch.sdk.data.StytchDispatchers
import com.stytch.sdk.data.StytchError
import com.stytch.sdk.pkce.MissingPKCEException
import com.stytch.sdk.pkce.PKCEClient
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException
import kotlin.js.JsExport

/** Magic link authentication methods. */
@StytchApi
@JsExport
public interface MagicLinksClient {
    /** Email magic link methods. */
    public val email: EmailMagicLinksClient

    /** Authenticates a magic link token received via deeplink. */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun authenticate(request: IMagicLinksAuthenticateParameters): MagicLinksAuthenticateResponse
}

/** Email magic link methods. */
@StytchApi
@JsExport
public interface EmailMagicLinksClient {
    /** Sends a magic link email to the provided address, creating a new user if one does not exist. */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun loginOrCreate(request: IMagicLinksEmailLoginOrCreateParameters): MagicLinksEmailLoginOrCreateResponse

    /** Sends a magic link email to an existing user's email address. */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun send(request: IMagicLinksEmailSendSecondaryParameters): MagicLinksEmailSendSecondaryResponse
}

internal class MagicLinksImpl(
    private val dispatchers: StytchDispatchers,
    private val networkingClient: ConsumerNetworkingClient,
    private val pkceClient: PKCEClient,
    private val sessionManager: StytchConsumerAuthenticationStateManager,
) : MagicLinksClient {
    override val email: EmailMagicLinksClient = EmailMagicLinksImpl(dispatchers, networkingClient, pkceClient, sessionManager)

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun authenticate(request: IMagicLinksAuthenticateParameters): MagicLinksAuthenticateResponse =
        withContext(dispatchers.ioDispatcher) {
            val codePair = pkceClient.retrieve() ?: throw MissingPKCEException()
            networkingClient.request {
                networkingClient.api.magicLinksAuthenticate(request.toNetworkModel(codeVerifier = codePair.verifier)).also {
                    pkceClient.revoke()
                }
            }
        }
}

internal class EmailMagicLinksImpl(
    private val dispatchers: StytchDispatchers,
    private val networkingClient: ConsumerNetworkingClient,
    private val pkceClient: PKCEClient,
    private val sessionManager: StytchConsumerAuthenticationStateManager,
) : EmailMagicLinksClient {
    @Throws(StytchError::class, CancellationException::class)
    override suspend fun loginOrCreate(request: IMagicLinksEmailLoginOrCreateParameters): MagicLinksEmailLoginOrCreateResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                val codePair = pkceClient.create()
                networkingClient.api.magicLinksEmailLoginOrCreate(request.toNetworkModel(codeChallenge = codePair.challenge))
            }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun send(request: IMagicLinksEmailSendSecondaryParameters): MagicLinksEmailSendSecondaryResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                val codePair = pkceClient.create()
                val parameters = request.toNetworkModel(codeChallenge = codePair.challenge)
                if (sessionManager.currentSessionToken.isNullOrEmpty()) {
                    networkingClient.api.magicLinksEmailSendPrimary(parameters)
                } else {
                    networkingClient.api.magicLinksEmailSendSecondary(parameters)
                }
            }
        }
}
