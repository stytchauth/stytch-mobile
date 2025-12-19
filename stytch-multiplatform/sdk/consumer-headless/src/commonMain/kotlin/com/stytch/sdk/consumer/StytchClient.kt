package com.stytch.sdk.consumer

import com.stytch.sdk.common.CommonDataClass

public object StytchClient {
    public fun configure(options: CommonDataClass): String = options.name
}