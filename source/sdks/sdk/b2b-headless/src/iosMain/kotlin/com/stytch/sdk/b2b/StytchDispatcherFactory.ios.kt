package com.stytch.sdk.b2b

import com.stytch.sdk.data.StytchDispatchers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

public actual fun createStytchDispatchers(): StytchDispatchers =
    StytchDispatchers(
        ioDispatcher = Dispatchers.IO,
        mainDispatcher = Dispatchers.Main,
    )
