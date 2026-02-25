package com.stytch.sdk.data

import kotlinx.serialization.Serializable

@Serializable
public sealed class SSOError(
    override val message: String,
) : Exception(message) {
    public class NoBrowserFound : SSOError("No supported browser was found on this device")

    public class NoURIFound : SSOError("No OAuth URI could be found in the bundle")

    public class NoTokenFound : SSOError("No token could be found in the bundle")

    public class UserCanceled : SSOError("The user canceled the OAuth flow")

    public class UnknownError(
        override val message: String,
    ) : SSOError(message = message)
}
