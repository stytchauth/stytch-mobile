# Stytch React Native SDK

[![npm (consumer)](https://img.shields.io/npm/v/@stytch/react-native-consumer?label=%40stytch%2Freact-native-consumer&color=red)](https://www.npmjs.com/package/@stytch/react-native-consumer)
[![npm (b2b)](https://img.shields.io/npm/v/@stytch/react-native-b2b?label=%40stytch%2Freact-native-b2b&color=red)](https://www.npmjs.com/package/@stytch/react-native-b2b)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://github.com/stytchauth/stytch-mobile/blob/main/LICENSE)
![React Native](https://img.shields.io/badge/React%20Native-0.80%2B-61DAFB?logo=react&logoColor=white)

Headless authentication SDK for React Native, built on a shared [Kotlin Multiplatform](https://www.jetbrains.com/kotlin-multiplatform/) core. Bring your own UI — Stytch handles the auth.

---

## Requirements

- React Native **0.80.x** or later
- React Native's [New Architecture (TurboModules)](https://reactnative.dev/docs/new-architecture-intro) must be enabled

---

## Consumer vs. B2B: What's Right for You?

| | Consumer SDK | B2B SDK |
|---|---|---|
| **Use case** | B2C apps — end users authenticate directly | B2B SaaS — members authenticate within Organizations |
| **Auth methods** | OTP, magic links, passwords, OAuth, passkeys, TOTP, crypto wallets | OTP, magic links, passwords, OAuth, SSO (SAML/OIDC), TOTP |
| **Additional features** | Session + user management, DFP | Organizations, members, RBAC, SCIM, discovery flows, recovery codes |
| **Package** | `@stytch/react-native-consumer` | `@stytch/react-native-b2b` |

If you're building a consumer-facing app, you want the **Consumer SDK**. If you're building a B2B SaaS product where your customers belong to organizations, you want the **B2B SDK**. For a deeper comparison, see the [docs](https://stytch.com/docs).

---

## Installation

**Consumer**
```sh
npm install @stytch/react-native-consumer
# or
yarn add @stytch/react-native-consumer
```

**B2B**
```sh
npm install @stytch/react-native-b2b
# or
yarn add @stytch/react-native-b2b
```

---

## Quick Start

The examples below use the **Consumer SDK** with an SMS OTP login flow. The **B2B SDK** follows the same pattern — swap in `@stytch/react-native-b2b` and use the equivalent B2B endpoints.

**1. Initialize the client and wrap your app**

```tsx
import {
    createStytchConsumer,
    StytchClientConfiguration,
    StytchProvider,
} from '@stytch/react-native-consumer';

const stytch = createStytchConsumer(
    new StytchClientConfiguration("public-token-live-xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx")
);

export default function App() {
    return (
        <StytchProvider stytch={stytch}>
            <YourNavigator />
        </StytchProvider>
    );
}
```

**2. Access the client and state in components**

```tsx
import { useStytch, useStytchUser, useStytchSession } from '@stytch/react-native-consumer';

function ProfileHeader() {
    const user = useStytchUser();
    const session = useStytchSession();
    return (
        <Text>{user ? `Logged in as ${user.emails[0]?.email}` : 'Not logged in'}</Text>
    );
}
```

**3. Send and verify an SMS OTP**

```tsx
import { useStytch } from '@stytch/react-native-consumer';

function SmsOtpScreen() {
    const stytch = useStytch();
    const [methodId, setMethodId] = useState<string | null>(null);

    const sendOtp = async (phoneNumber: string) => {
        const response = await stytch.otp.sms.loginOrCreate({ phoneNumber });
        setMethodId(response.methodId);
    };

    const verifyOtp = async (code: string) => {
        await stytch.otp.authenticate({
            token: code,
            methodId: methodId!,
            sessionDurationMinutes: 30,
        });
    };

    // ...
}
```

---

## Support

If you have questions, found a bug or want help troubleshooting, join us in [Slack](https://stytch.com/docs/resources/support/overview) or email [support@stytch.com](mailto:support@stytch.com).

If you've found a security vulnerability, please follow our [responsible disclosure instructions](https://stytch.com/docs/resources/security-and-trust/security#:~:text=Responsible%20disclosure%20program).
