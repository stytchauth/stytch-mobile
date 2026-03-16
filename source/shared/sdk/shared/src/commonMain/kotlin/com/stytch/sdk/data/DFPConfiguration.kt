package com.stytch.sdk.data

public data class DFPConfiguration(
    val dfpProtectedAuthEnabled: Boolean = false,
    val dfpProtectedAuthMode: DFPProtectedAuthMode = DFPProtectedAuthMode.OBSERVATION,
)
