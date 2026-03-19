package com.stytch.sdk.b2b

import com.stytch.sdk.data.StytchDispatchers
import kotlinx.coroutines.Dispatchers

/** @suppress */
public actual fun createStytchDispatchers(): StytchDispatchers =
    StytchDispatchers(
        ioDispatcher = Dispatchers.IO,
        mainDispatcher = Dispatchers.Main,
    )
