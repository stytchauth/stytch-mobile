package com.stytch.sdk.networking

import com.stytch.sdk.data.BootstrapResponse
import com.stytch.sdk.data.StytchDataResponse
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path

internal interface SharedAPI {
    @GET("sdk/v1/projects/bootstrap/{publicToken}")
    suspend fun getBootstrapData(
        @Path(value = "publicToken") publicToken: String,
    ): StytchDataResponse<BootstrapResponse>
}
