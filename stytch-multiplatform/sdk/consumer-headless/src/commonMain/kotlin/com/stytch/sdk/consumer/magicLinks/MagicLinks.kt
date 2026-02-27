package com.stytch.sdk.consumer.magicLinks

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
import com.stytch.sdk.pkce.PKCEClient
import kotlinx.coroutines.withContext
import kotlin.js.JsExport

@JsExport
public interface MagicLinksClient {
    public val email: EmailMagicLinksClient

    public suspend fun authenticate(request: IMagicLinksAuthenticateParameters): MagicLinksAuthenticateResponse
}

@JsExport
public interface EmailMagicLinksClient {
    public suspend fun loginOrCreate(request: IMagicLinksEmailLoginOrCreateParameters): MagicLinksEmailLoginOrCreateResponse

    public suspend fun send(request: IMagicLinksEmailSendSecondaryParameters): MagicLinksEmailSendSecondaryResponse
}

internal class MagicLinksImpl(
    private val dispatchers: StytchDispatchers,
    private val networkingClient: ConsumerNetworkingClient,
    private val pkceClient: PKCEClient,
    private val sessionManager: StytchConsumerAuthenticationStateManager,
) : MagicLinksClient {
    override val email: EmailMagicLinksClient = EmailMagicLinksImpl(dispatchers, networkingClient, pkceClient, sessionManager)

    override suspend fun authenticate(request: IMagicLinksAuthenticateParameters): MagicLinksAuthenticateResponse =
        withContext(dispatchers.ioDispatcher) {
            val codePair = pkceClient.retrieve() ?: throw IllegalStateException("PKCE is missing")
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
    override suspend fun loginOrCreate(request: IMagicLinksEmailLoginOrCreateParameters): MagicLinksEmailLoginOrCreateResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request {
                val codePair = pkceClient.create()
                networkingClient.api.magicLinksEmailLoginOrCreate(request.toNetworkModel(codeChallenge = codePair.challenge))
            }
        }

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
