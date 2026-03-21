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
    /**
     * Retrieves the organization's SCIM connection. Calls the `GET /sdk/v1/b2b/scim` endpoint.
     * Requires an active session.
     *
     * **Kotlin:**
     * ```kotlin
     * val response = StytchB2B.scim.getConnection()
     * ```
     *
     * **iOS:**
     * ```swift
     * let response = try await StytchB2B.scim.getConnection()
     * ```
     *
     * **React Native:**
     * ```js
     * StytchB2B.scim.getConnection()
     * ```
     *
     * @return [B2BGetSCIMConnectionResponse] containing the organization's SCIM connection.
     *
     * @throws [StytchError] if no SCIM connection exists or the request fails.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun getConnection(): B2BGetSCIMConnectionResponse

    /**
     * Retrieves the groups associated with the organization's SCIM connection.
     * Calls the `POST /sdk/v1/b2b/scim/groups` endpoint. Requires an active session.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchB2B.scim.getConnectionGroups(
     *     B2BGetSCIMConnectionGroupsParameters(limit = 100)
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = B2BGetSCIMConnectionGroupsParameters(limit: 100)
     * let response = try await StytchB2B.scim.getConnectionGroups(params)
     * ```
     *
     * **React Native:**
     * ```js
     * StytchB2B.scim.getConnectionGroups({ limit: 100 })
     * ```
     *
     * @param request - [IB2BGetSCIMConnectionGroupsParameters]
     *   - `cursor?` — Pagination cursor from a previous response.
     *   - `limit?` — Maximum number of groups to return.
     *
     * @return [B2BGetSCIMConnectionGroupsResponse] containing the SCIM groups.
     *
     * @throws [StytchError] if the request fails.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun getConnectionGroups(request: IB2BGetSCIMConnectionGroupsParameters): B2BGetSCIMConnectionGroupsResponse

    /**
     * Creates a new SCIM connection for the organization.
     * Calls the `POST /sdk/v1/b2b/scim` endpoint. Requires an active session.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchB2B.scim.createConnection(
     *     B2BSCIMCreateConnectionParameters(displayName = "Okta SCIM")
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = B2BSCIMCreateConnectionParameters(displayName: "Okta SCIM")
     * let response = try await StytchB2B.scim.createConnection(params)
     * ```
     *
     * **React Native:**
     * ```js
     * StytchB2B.scim.createConnection({ displayName: "Okta SCIM" })
     * ```
     *
     * @param request - [IB2BSCIMCreateConnectionParameters]
     *   - `displayName` — Human-readable label for the SCIM connection.
     *   - `identityProvider?` — The identity provider type (e.g. `"okta"`, `"generic"`).
     *
     * @return [B2BSCIMCreateConnectionResponse] containing the newly created SCIM connection and its bearer token.
     *
     * @throws [StytchError] if the request fails.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun createConnection(request: IB2BSCIMCreateConnectionParameters): B2BSCIMCreateConnectionResponse

    /**
     * Deletes the specified SCIM connection.
     * Calls the `DELETE /sdk/v1/b2b/scim/{connection_id}` endpoint. Requires an active session.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchB2B.scim.deleteConnection("scim-connection-test-d5a3b680-e8a3-40c0-b815-ab79986666d0")
     * ```
     *
     * **iOS:**
     * ```swift
     * let response = try await StytchB2B.scim.deleteConnection("scim-connection-test-d5a3b680-e8a3-40c0-b815-ab79986666d0")
     * ```
     *
     * **React Native:**
     * ```js
     * StytchB2B.scim.deleteConnection("scim-connection-test-d5a3b680-e8a3-40c0-b815-ab79986666d0")
     * ```
     *
     * @param connectionId The ID of the SCIM connection to delete.
     *
     * @return [B2BSCIMDeleteConnectionResponse] confirming the deletion.
     *
     * @throws [StytchError] if the connection is not found or the request fails.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun deleteConnection(connectionId: String): B2BSCIMDeleteConnectionResponse

    /**
     * Updates settings on the specified SCIM connection.
     * Calls the `PUT /sdk/v1/b2b/scim/{connection_id}` endpoint. Requires an active session.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchB2B.scim.updateConnection(
     *     connectionId = "scim-connection-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *     request = B2BSCIMUpdateConnectionParameters(
     *         displayName = "Updated SCIM Connection",
     *     ),
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = B2BSCIMUpdateConnectionParameters(displayName: "Updated SCIM Connection")
     * let response = try await StytchB2B.scim.updateConnection(
     *     connectionId: "scim-connection-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *     request: params
     * )
     * ```
     *
     * **React Native:**
     * ```js
     * StytchB2B.scim.updateConnection(
     *     "scim-connection-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *     { displayName: "Updated SCIM Connection" },
     * )
     * ```
     *
     * @param connectionId The ID of the SCIM connection to update.
     * @param request - [IB2BSCIMUpdateConnectionParameters]
     *   - `identityProvider?` — The identity provider type.
     *   - `displayName?` — Human-readable label for the connection.
     *   - `scimGroupImplicitRoleAssignments?` — Roles assigned based on SCIM group membership.
     *
     * @return [B2BSCIMUpdateConnectionResponse] containing the updated connection.
     *
     * @throws [StytchError] if the connection is not found or the request fails.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun updateConnection(
        connectionId: String,
        request: IB2BSCIMUpdateConnectionParameters,
    ): B2BSCIMUpdateConnectionResponse

    /**
     * Initiates a SCIM bearer token rotation for the specified connection. The new token is generated
     * in a pending state; call [rotateTokenComplete] to activate it. Calls the
     * `POST /sdk/v1/b2b/scim/rotate/start` endpoint. Requires an active session.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchB2B.scim.rotateTokenStart(
     *     SCIMRotateTokenStartParameters(
     *         connectionId = "scim-connection-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *     )
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = SCIMRotateTokenStartParameters(connectionId: "scim-connection-test-d5a3b680-e8a3-40c0-b815-ab79986666d0")
     * let response = try await StytchB2B.scim.rotateTokenStart(params)
     * ```
     *
     * **React Native:**
     * ```js
     * StytchB2B.scim.rotateTokenStart({ connectionId: "scim-connection-test-d5a3b680-e8a3-40c0-b815-ab79986666d0" })
     * ```
     *
     * @param request - [ISCIMRotateTokenStartParameters]
     *   - `connectionId` — The ID of the SCIM connection whose token to rotate.
     *
     * @return [SCIMRotateTokenStartResponse] containing the pending new token.
     *
     * @throws [StytchError] if the connection is not found or the request fails.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun rotateTokenStart(request: ISCIMRotateTokenStartParameters): SCIMRotateTokenStartResponse

    /**
     * Completes a SCIM bearer token rotation, activating the new token and invalidating the old one.
     * Must be called after [rotateTokenStart]. Calls the `POST /sdk/v1/b2b/scim/rotate/complete`
     * endpoint. Requires an active session.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchB2B.scim.rotateTokenComplete(
     *     SCIMRotateTokenCompleteParameters(
     *         connectionId = "scim-connection-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *     )
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = SCIMRotateTokenCompleteParameters(connectionId: "scim-connection-test-d5a3b680-e8a3-40c0-b815-ab79986666d0")
     * let response = try await StytchB2B.scim.rotateTokenComplete(params)
     * ```
     *
     * **React Native:**
     * ```js
     * StytchB2B.scim.rotateTokenComplete({ connectionId: "scim-connection-test-d5a3b680-e8a3-40c0-b815-ab79986666d0" })
     * ```
     *
     * @param request - [ISCIMRotateTokenCompleteParameters]
     *   - `connectionId` — The ID of the SCIM connection whose rotation to complete.
     *
     * @return [SCIMRotateTokenCompleteResponse] confirming the new token is active.
     *
     * @throws [StytchError] if no rotation is in progress or the request fails.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
    @Throws(StytchError::class, CancellationException::class)
    public suspend fun rotateTokenComplete(request: ISCIMRotateTokenCompleteParameters): SCIMRotateTokenCompleteResponse

    /**
     * Cancels an in-progress SCIM bearer token rotation, keeping the existing token active.
     * Calls the `POST /sdk/v1/b2b/scim/rotate/cancel` endpoint. Requires an active session.
     *
     * **Kotlin:**
     * ```kotlin
     * StytchB2B.scim.rotateTokenCancel(
     *     SCIMRotateTokenCancelParameters(
     *         connectionId = "scim-connection-test-d5a3b680-e8a3-40c0-b815-ab79986666d0",
     *     )
     * )
     * ```
     *
     * **iOS:**
     * ```swift
     * let params = SCIMRotateTokenCancelParameters(connectionId: "scim-connection-test-d5a3b680-e8a3-40c0-b815-ab79986666d0")
     * let response = try await StytchB2B.scim.rotateTokenCancel(params)
     * ```
     *
     * **React Native:**
     * ```js
     * StytchB2B.scim.rotateTokenCancel({ connectionId: "scim-connection-test-d5a3b680-e8a3-40c0-b815-ab79986666d0" })
     * ```
     *
     * @param request - [ISCIMRotateTokenCancelParameters]
     *   - `connectionId` — The ID of the SCIM connection whose rotation to cancel.
     *
     * @return [SCIMRotateTokenCancelResponse] confirming the rotation was cancelled.
     *
     * @throws [StytchError] if no rotation is in progress or the request fails.
     * @throws [CancellationException] if the coroutine is cancelled.
     */
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
