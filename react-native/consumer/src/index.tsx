// the Kotlin/JS code depends on window.crypto for generating UUIDs. This is a polyfill
import "react-native-get-random-values";

// Our bridge exports a global object that Kotlin/JS expects which ties into the RN bridge
import './NativeStytchBridge'

// export any relevant stuff that a consuming app might need
export * from './contexts'
export * from './hooks'
export * from './providers'

// export everything generated from the KMP library for client consumption
export * from '../lib/consumer-headless.mjs';