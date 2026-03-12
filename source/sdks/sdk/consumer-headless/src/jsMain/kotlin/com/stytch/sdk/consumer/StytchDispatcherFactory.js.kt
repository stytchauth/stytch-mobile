package com.stytch.sdk.consumer

import com.stytch.sdk.data.StytchDispatchers
import kotlinx.coroutines.Dispatchers

public actual fun createStytchDispatchers(): StytchDispatchers =
    StytchDispatchers(
        ioDispatcher = Dispatchers.Default,
        mainDispatcher = Dispatchers.Main,
    )
