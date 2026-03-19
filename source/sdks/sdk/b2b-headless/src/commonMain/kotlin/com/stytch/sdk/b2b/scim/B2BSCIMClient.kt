package com.stytch.sdk.b2b.scim

import com.stytch.sdk.StytchApi
import com.stytch.sdk.b2b.networking.B2BNetworkingClient
import com.stytch.sdk.b2b.networking.models.B2BGetSCIMConnectionGroupsResponse
import com.stytch.sdk.b2b.networking.models.B2BGetSCIMConnectionResponse
import com.stytch.sdk.b2b.networking.models.B2BSCIMCreateConnectionResponse
import com.stytch.sdk.b2b.networking.models.B2BSCIMDeleteConnectionResponse
import com.stytch.sdk.b2b.networking.models.B2BSCIMUpdateConnectionResponse
import com.stytch.sdk.b2b.networking.models.IB2BGetSCIMConnectionGroupsParameters
import com.stytch.sdk.b2b.networking.models.IB2BSCIMCreateConnectionParameters
import com.stytch.sdk.b2b.networking.models.IB2BSCIMUpdateConnectionParameters
import com.stytch.sdk.b2b.networking.models.ISCIMRotateTokenCancelParameters
import com.stytch.sdk.b2b.networking.models.ISCIMRotateTokenCompleteParameters
import com.stytch.sdk.b2b.networking.models.ISCIMRotateTokenStartParameters
import com.stytch.sdk.b2b.networking.models.SCIMRotateTokenCancelResponse
import com.stytch.sdk.b2b.networking.models.SCIMRotateTokenCompleteResponse
import com.stytch.sdk.b2b.networking.models.SCIMRotateTokenStartResponse
import com.stytch.sdk.b2b.networking.models.toNetworkModel
import com.stytch.sdk.data.StytchDispatchers
import com.stytch.sdk.data.StytchError
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException
import kotlin.js.JsExport

/** SCIM (System for Cross-domain Identity Management) provisioning methods. */
@StytchApi
@JsExport
public interface B2BSCIMClient {
    /** Retrieves the organization's SCIM connection. */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun getConnection(): B2BGetSCIMConnectionResponse

    /** Retrieves the groups associated with the organization's SCIM connection. */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun getConnectionGroups(request: IB2BGetSCIMConnectionGroupsParameters): B2BGetSCIMConnectionGroupsResponse

    /** Creates a new SCIM connection for the organization. */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun createConnection(request: IB2BSCIMCreateConnectionParameters): B2BSCIMCreateConnectionResponse

    /** Deletes the specified SCIM connection. */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun deleteConnection(connectionId: String): B2BSCIMDeleteConnectionResponse

    /** Updates settings on the specified SCIM connection. */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun updateConnection(
        connectionId: String,
        request: IB2BSCIMUpdateConnectionParameters,
    ): B2BSCIMUpdateConnectionResponse

    /** Initiates a SCIM bearer token rotation, returning the new token in a pending state. */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun rotateTokenStart(request: ISCIMRotateTokenStartParameters): SCIMRotateTokenStartResponse

    /** Completes a SCIM bearer token rotation, activating the new token and invalidating the old one. */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun rotateTokenComplete(request: ISCIMRotateTokenCompleteParameters): SCIMRotateTokenCompleteResponse

    /** Cancels an in-progress SCIM bearer token rotation, keeping the existing token active. */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun rotateTokenCancel(request: ISCIMRotateTokenCancelParameters): SCIMRotateTokenCancelResponse
}

internal class B2BSCIMClientImpl(
    private val dispatchers: StytchDispatchers,
    private val networkingClient: B2BNetworkingClient,
) : B2BSCIMClient {
    @Throws(StytchError::class, CancellationException::class)
    override suspend fun getConnection(): B2BGetSCIMConnectionResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request { networkingClient.api.b2BGetSCIMConnection() }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun getConnectionGroups(request: IB2BGetSCIMConnectionGroupsParameters): B2BGetSCIMConnectionGroupsResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request { networkingClient.api.b2BGetSCIMConnectionGroups(request.toNetworkModel()) }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun createConnection(request: IB2BSCIMCreateConnectionParameters): B2BSCIMCreateConnectionResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request { networkingClient.api.b2BSCIMCreateConnection(request.toNetworkModel()) }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun deleteConnection(connectionId: String): B2BSCIMDeleteConnectionResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request { networkingClient.api.b2BSCIMDeleteConnection(connectionId) }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun updateConnection(
        connectionId: String,
        request: IB2BSCIMUpdateConnectionParameters,
    ): B2BSCIMUpdateConnectionResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request { networkingClient.api.b2BSCIMUpdateConnection(connectionId, request.toNetworkModel()) }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun rotateTokenStart(request: ISCIMRotateTokenStartParameters): SCIMRotateTokenStartResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request { networkingClient.api.sCIMRotateTokenStart(request.toNetworkModel()) }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun rotateTokenComplete(request: ISCIMRotateTokenCompleteParameters): SCIMRotateTokenCompleteResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request { networkingClient.api.sCIMRotateTokenComplete(request.toNetworkModel()) }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun rotateTokenCancel(request: ISCIMRotateTokenCancelParameters): SCIMRotateTokenCancelResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request { networkingClient.api.sCIMRotateTokenCancel(request.toNetworkModel()) }
        }
}
