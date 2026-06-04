package com.stytch.sdk.utils

import kotlinx.cinterop.BetaInteropApi
import platform.Foundation.NSData
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.create

@OptIn(BetaInteropApi::class)
internal fun NSData.toUtf8String(): String? = NSString.create(data = this, encoding = NSUTF8StringEncoding) as String?
