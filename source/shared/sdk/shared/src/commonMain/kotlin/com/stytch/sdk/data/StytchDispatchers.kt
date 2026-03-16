package com.stytch.sdk.data

import kotlinx.coroutines.CoroutineDispatcher

public class StytchDispatchers(
    public val ioDispatcher: CoroutineDispatcher,
    public val mainDispatcher: CoroutineDispatcher,
)
