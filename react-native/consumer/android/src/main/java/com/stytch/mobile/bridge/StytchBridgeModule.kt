package com.stytch.mobile.bridge

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.module.annotations.ReactModule

@ReactModule(name = StytchBridgeModule.NAME)
class StytchBridgeModule(reactContext: ReactApplicationContext) :
  NativeStytchBridgeSpec(reactContext) {

  override fun getName(): String {
    return NAME
  }

  
  companion object {
    const val NAME = "StytchBridge"
  }
}
