package com.stytch.sdk.data

import kotlinx.serialization.Serializable

@Serializable
public class StytchDataResponse<T>(
    public val data: T,
)
