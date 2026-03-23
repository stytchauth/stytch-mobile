# ``StytchB2BSDK``

Stytch B2B SDK — headless authentication for iOS B2B applications.

## Overview

The Stytch B2B SDK provides organization-aware authentication for iOS applications, including magic links, OTP, OAuth, SSO, passwords, TOTP, SCIM, RBAC, and cross-organization discovery flows.

Create a client instance once at app launch using ``createStytchB2B(configuration:)`` with your Stytch public token, then use the returned ``StytchB2B`` to access authentication methods and session state.

## Topics

### Entry Point

- ``StytchB2B``

### Authentication Methods

- ``B2BMagicLinksClient``
- ``B2BOtpClient``
- ``B2BOAuthClient``
- ``B2BSSOClient``
- ``B2BPasswordsClient``
- ``B2BTOTPClient``

### Organizations & Members

- ``B2BOrganizationsClient``
- ``B2BMembersClient``

### Discovery

- ``B2BDiscoveryClient``

### Session

- ``B2BSessionsClient``

### Access Control

- ``B2BRBACClient``

### Provisioning

- ``B2BSCIMClient``

### Recovery

- ``B2BRecoveryCodesClient``

### Device Fingerprinting

- ``DFPClient``
