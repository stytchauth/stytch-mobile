package com.stytch.sdk.consumer.data

import com.stytch.sdk.consumer.networking.Session
import com.stytch.sdk.consumer.networking.User
import com.stytch.sdk.data.AuthenticationState

public sealed class ConsumerAuthenticationState : AuthenticationState {
    public data object Loading : ConsumerAuthenticationState()

    public data object Unauthenticated : ConsumerAuthenticationState()

    public data class Authenticated(
        val user: User,
        val session: Session,
        val sessionToken: String,
        val sessionJwt: String,
    ) : ConsumerAuthenticationState()
}
