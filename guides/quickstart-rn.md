# React Native Quickstart

This guide walks you through adding Stytch authentication to a React Native app using `@stytch/react-native-consumer` or `@stytch/react-native-b2b`.

**Requirements:**
- React Native **0.80.x** or later
- React Native's [New Architecture (TurboModules)](https://reactnative.dev/architecture/landing-page) enabled

---

## 1. Install the SDK

```sh
# Consumer (B2C apps)
npm install @stytch/react-native-consumer
# or
yarn add @stytch/react-native-consumer

# B2B (organizations/members)
npm install @stytch/react-native-b2b
# or
yarn add @stytch/react-native-b2b
```

---

## 2. Initialize the Client and Wrap Your App

Create the client once at the top level of your app and wrap your component tree with `StytchProvider`. The client is a singleton; calling `createStytchConsumer` again returns the same instance.

```tsx
import {
  createStytchConsumer,
  StytchClientConfiguration,
  StytchProvider,
} from '@stytch/react-native-consumer';

const stytch = createStytchConsumer(
  new StytchClientConfiguration('public-token-live-xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx')
);

export default function App() {
  return (
    <StytchProvider stytch={stytch}>
      <YourNavigator />
    </StytchProvider>
  );
}
```

For B2B apps, swap in `createStytchB2B`, `StytchClientConfiguration`, and `StytchB2BProvider` from `@stytch/react-native-b2b`.

Your public token is in the [Stytch Dashboard](https://stytch.com/dashboard/api-keys). Make sure you've also enabled the auth methods you want to use under **SDK Configuration**.

---

## 3. Access the Client and State in Components

```tsx
import {
  useStytch,
  useStytchUser,
  useStytchSession,
  useStytchAuthenticationState,
} from '@stytch/react-native-consumer';
import { ConsumerAuthenticationState } from '@stytch/react-native-consumer';

function AuthStateDisplay() {
  const user = useStytchUser();           // ApiUserV1User | undefined
  const session = useStytchSession();     // ApiSessionV1Session | undefined
  const authState = useStytchAuthenticationState();

  if (authState instanceof ConsumerAuthenticationState.Loading) {
    return <Text>Loading...</Text>;
  }
  if (authState instanceof ConsumerAuthenticationState.Authenticated) {
    return <Text>Logged in as {authState.user.emails?.[0]?.email}</Text>;
  }
  return <Text>Not logged in</Text>;
}
```

`useStytchUser()` and `useStytchSession()` are convenient shortcuts that return the values directly from the authenticated state — they return `undefined` when the user is not logged in.

---

## 4. Auth Methods

All SDK methods are `async` and throw on error. Wrap them in `try/catch`.

### SMS OTP

```tsx
import { useStytch } from '@stytch/react-native-consumer';

function SmsOtpScreen() {
  const stytch = useStytch();
  const [methodId, setMethodId] = useState('');

  const sendOtp = async (phoneNumber: string) => {
    try {
      const response = await stytch.otp.sms.loginOrCreate({ phoneNumber });
      setMethodId(response.methodId);
    } catch (e) {
      console.error(e);
    }
  };

  const verifyOtp = async (code: string) => {
    try {
      await stytch.otp.authenticate({
        token: code,
        methodId,
        sessionDurationMinutes: 30,
      });
    } catch (e) {
      console.error(e);
    }
  };
}
```

The SDK also supports email OTP (`stytch.otp.email.loginOrCreate(...)`) and WhatsApp OTP (`stytch.otp.whatsapp.loginOrCreate(...)`).

### Email Magic Links

Magic links redirect back to your app — configure a URL scheme in your app and register the redirect URL in the [Stytch Dashboard](https://stytch.com/dashboard/redirect-urls).

```tsx
const sendMagicLink = async (email: string) => {
  try {
    await stytch.magicLinks.email.loginOrCreate({
      email,
      loginMagicLinkUrl: 'myapp://auth',
      signupMagicLinkUrl: 'myapp://auth',
    });
    // Tell the user to check their inbox
  } catch (e) {
    console.error(e);
  }
};
```

Handle the incoming URL with React Navigation's deep linking or `Linking.addEventListener` — see [Step 5](#5-handle-deeplinks).

### OAuth (Browser-Based)

`start()` opens a browser session, handles the OAuth redirect, and returns the authenticated response in one call:

```tsx
const loginWithGoogle = async () => {
  try {
    const response = await stytch.oauth.google.start({
      loginRedirectUrl: 'myapp://oauth',
      signupRedirectUrl: 'myapp://oauth',
      sessionDurationMinutes: 30,
    });
  } catch (e) {
    console.error(e);
  }
};
```

Replace `.google` with any supported provider: `.apple`, `.github`, `.microsoft`, `.facebook`, `.amazon`, `.slack`, and more.

### Google Credential Manager (Native, Android)

To use Google's native credential dialog on Android instead of a browser, pass a `GoogleCredentialConfiguration` when initializing:

```tsx
import { GoogleCredentialConfiguration } from '@stytch/react-native-consumer';

const stytch = createStytchConsumer(
  new StytchClientConfiguration(
    'public-token-live-...',
    undefined,          // endpointOptions
    undefined,          // defaultSessionDuration
    new GoogleCredentialConfiguration('YOUR_ANDROID_GOOGLE_CLIENT_ID'),
  )
);
```

Then call `stytch.oauth.google.start(...)` as usual — the SDK automatically uses Credential Manager when this configuration is present, falling back to browser based as needed.

### Passwords

```tsx
// Check strength first
const strength = await stytch.passwords.strengthCheck({
  email: 'user@example.com',
  password: 'mypassword',
});

// Create a new password user
await stytch.passwords.create({
  email: 'user@example.com',
  password: 'mypassword',
});

// Authenticate with email + password
await stytch.passwords.authenticate({
  email: 'user@example.com',
  password: 'mypassword',
  sessionDurationMinutes: 30,
});
```

---

## 5. Handle Deeplinks

Magic links and password reset emails redirect back to your app. Set up React Navigation's [deep linking](https://reactnavigation.org/docs/deep-linking/) with your URL scheme, then authenticate the token on arrival:

```tsx
import { Linking } from 'react-native';

useEffect(() => {
  const handleUrl = async ({ url }: { url: string }) => {
    try {
      const result = await stytch.authenticate(url, 30);
      // Handle result — see DeeplinkAuthenticationStatus
    } catch (e) {
      console.error(e);
    }
  };
  const sub = Linking.addEventListener('url', handleUrl);
  return () => sub.remove();
}, [stytch]);
```

---

## 6. Session Management

Sessions are automatically persisted and validated on startup.

```tsx
// Manually validate (and optionally extend) the current session
await stytch.session.authenticate({ sessionDurationMinutes: 30 });

// Sign out
await stytch.session.revoke();
```

---

## B2B Quick Reference

The B2B package follows the same patterns with B2B-specific names:

```tsx
import {
  createStytchB2B,
  StytchClientConfiguration,
  StytchB2BProvider,
  useStytchB2B,
  useStytchMember,
  useStytchMemberSession,
  useStytchOrganization,
  useStytchB2BAuthenticationState,
} from '@stytch/react-native-b2b';
import { B2BAuthenticationState } from '@stytch/react-native-b2b';

const stytch = createStytchB2B(
  new StytchClientConfiguration('public-token-live-...')
);

function App() {
  return (
    <StytchB2BProvider stytch={stytch}>
      <YourNavigator />
    </StytchB2BProvider>
  );
}

function MemberDisplay() {
  const member = useStytchMember();               // ApiOrganizationV1Member | undefined
  const memberSession = useStytchMemberSession(); // ApiB2bSessionV1MemberSession | undefined
  const organization = useStytchOrganization();   // ApiOrganizationV1Organization | undefined
  const authState = useStytchB2BAuthenticationState();

  if (authState instanceof B2BAuthenticationState.Authenticated) {
    return <Text>{authState.member.emailAddress} — {authState.organization?.organizationName}</Text>;
  }
  return null;
}
```
