package com.stytch.sdk.data

import kotlin.js.JsExport

@JsExport
public class Ed25519KeyPair(
    public val publicKey: ByteArray,
    public val privateKey: ByteArray,
    public val encryptedPrivateKey: ByteArray? = null,
)
