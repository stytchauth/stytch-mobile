# ``StytchConsumerSDK``

Stytch Consumer SDK — headless authentication for iOS.

## Overview

The Stytch Consumer SDK provides a complete set of authentication methods for consumer-facing iOS applications, including magic links, OTP, OAuth, passkeys, biometrics, passwords, and TOTP.

Initialize the SDK once at app launch with your Stytch public token, then use the top-level ``StytchConsumer`` object to access authentication methods and session state.

## Topics

### Entry Point

- ``StytchConsumer``

### Authentication Methods

- ``MagicLinksClient``
- ``OtpClient``
- ``OAuthClient``
- ``PasskeysClient``
- ``BiometricsClient``
- ``PasswordsClient``
- ``TotpClient``
- ``CryptoClient``

### Session & User

- ``SessionClient``
- ``UserClient``

### Device Fingerprinting

- ``DFPClient``
