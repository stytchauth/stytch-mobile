package com.stytch.sdk.data

import kotlinx.serialization.Serializable

@Serializable
public data class StytchDataResponse<T>(
    val data: T,
)
