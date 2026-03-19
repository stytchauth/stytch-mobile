package com.stytch.sdk.b2b

import com.stytch.sdk.data.StytchDispatchers

/** Creates the platform-specific [StytchDispatchers] used for coroutine dispatch within the SDK. */
public expect fun createStytchDispatchers(): StytchDispatchers
