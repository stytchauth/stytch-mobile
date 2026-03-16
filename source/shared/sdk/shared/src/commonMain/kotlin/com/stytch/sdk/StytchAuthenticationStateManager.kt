package com.stytch.sdk

import com.stytch.sdk.data.AuthenticationState
import kotlinx.coroutines.flow.StateFlow

public interface StytchAuthenticationStateManager {
    public val authenticationStateFlow: StateFlow<AuthenticationState>
    public val currentSessionToken: String?

    public suspend fun <T> update(response: T)

    public suspend fun revoke()
}
