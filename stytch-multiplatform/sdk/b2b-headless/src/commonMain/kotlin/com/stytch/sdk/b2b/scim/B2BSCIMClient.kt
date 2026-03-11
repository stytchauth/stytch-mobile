package com.stytch.sdk.b2b.scim

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

@JsExport
public interface B2BSCIMClient {
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun getConnection(): B2BGetSCIMConnectionResponse

    @Throws(StytchError::class, CancellationException::class)
    public suspend fun getConnectionGroups(request: IB2BGetSCIMConnectionGroupsParameters): B2BGetSCIMConnectionGroupsResponse

    @Throws(StytchError::class, CancellationException::class)
    public suspend fun createConnection(request: IB2BSCIMCreateConnectionParameters): B2BSCIMCreateConnectionResponse

    @Throws(StytchError::class, CancellationException::class)
    public suspend fun deleteConnection(connectionId: String): B2BSCIMDeleteConnectionResponse

    @Throws(StytchError::class, CancellationException::class)
    public suspend fun updateConnection(
        connectionId: String,
        request: IB2BSCIMUpdateConnectionParameters,
    ): B2BSCIMUpdateConnectionResponse

    @Throws(StytchError::class, CancellationException::class)
    public suspend fun rotateTokenStart(request: ISCIMRotateTokenStartParameters): SCIMRotateTokenStartResponse

    @Throws(StytchError::class, CancellationException::class)
    public suspend fun rotateTokenComplete(request: ISCIMRotateTokenCompleteParameters): SCIMRotateTokenCompleteResponse

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
