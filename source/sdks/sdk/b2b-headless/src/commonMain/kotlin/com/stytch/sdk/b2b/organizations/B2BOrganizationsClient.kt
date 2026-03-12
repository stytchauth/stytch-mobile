package com.stytch.sdk.b2b.organizations

import com.stytch.sdk.b2b.networking.B2BNetworkingClient
import com.stytch.sdk.b2b.networking.models.B2BOrganizationsDeleteResponse
import com.stytch.sdk.b2b.networking.models.B2BOrganizationsGetResponse
import com.stytch.sdk.b2b.networking.models.B2BOrganizationsUpdateResponse
import com.stytch.sdk.b2b.networking.models.IB2BOrganizationsUpdateParameters
import com.stytch.sdk.b2b.networking.models.toNetworkModel
import com.stytch.sdk.data.StytchDispatchers
import com.stytch.sdk.data.StytchError
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException
import kotlin.js.JsExport

@JsExport
public interface B2BOrganizationsClient {
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun get(): B2BOrganizationsGetResponse

    @Throws(StytchError::class, CancellationException::class)
    public suspend fun update(request: IB2BOrganizationsUpdateParameters): B2BOrganizationsUpdateResponse

    @Throws(StytchError::class, CancellationException::class)
    public suspend fun delete(): B2BOrganizationsDeleteResponse
}

internal class B2BOrganizationsClientImpl(
    private val dispatchers: StytchDispatchers,
    private val networkingClient: B2BNetworkingClient,
) : B2BOrganizationsClient {
    @Throws(StytchError::class, CancellationException::class)
    override suspend fun get(): B2BOrganizationsGetResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request { networkingClient.api.b2BOrganizationsGet() }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun update(request: IB2BOrganizationsUpdateParameters): B2BOrganizationsUpdateResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request { networkingClient.api.b2BOrganizationsUpdate(request.toNetworkModel()) }
        }

    @Throws(StytchError::class, CancellationException::class)
    override suspend fun delete(): B2BOrganizationsDeleteResponse =
        withContext(dispatchers.ioDispatcher) {
            networkingClient.request { networkingClient.api.b2BOrganizationsDelete() }
        }
}
