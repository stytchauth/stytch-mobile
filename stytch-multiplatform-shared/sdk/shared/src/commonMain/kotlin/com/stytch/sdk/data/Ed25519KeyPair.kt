package com.stytch.sdk.data

import kotlinx.serialization.Serializable

@Serializable
public class Ed25519KeyPair(
    public val publicKey: ByteArray,
    public val privateKey: ByteArray,
    public val encryptedPrivateKey: ByteArray? = null,
)
