# Migration Guide: @stytch/react-native → stytch-mobile (React Native)

This guide covers what changed when moving from `@stytch/react-native` (the original TypeScript-based RN SDK) to `@stytch/react-native-consumer` / `@stytch/react-native-b2b` (the new Kotlin Multiplatform-backed SDK).

---

## What Changed at a Glance

| | Old SDK (`@stytch/react-native`) | New SDK (`@stytch/react-native-consumer`) |
|---|---|---|
| **Package** | `@stytch/react-native` | `@stytch/react-native-consumer` or `@stytch/react-native-b2b` |
| **B2B import** | `@stytch/react-native/b2b` | Separate package `@stytch/react-native-b2b` |
| **Architecture** | Pure TypeScript | Kotlin Multiplatform core + TypeScript bridge |
| **New Architecture** | Not required | **Required** (TurboModules) |
| **Client init** | `new StytchClient(publicToken, ...)` | `createStytchConsumer(new StytchClientConfiguration(publicToken))` |
| **Provider** | `<StytchProvider>` | `<StytchProvider>` (same name, new import) |
| **`useStytchUser()`** | Returns `{ user, fromCache }` | Returns `User \| undefined` directly |
| **`useStytchSession()`** | Returns `{ session, fromCache }` | Returns `Session \| undefined` directly |
| **Auth state** | `stytch.onStateChange(callback)` | `useStytchAuthenticationState()` + `stytch.authenticationStateObserver()` |
| **OTP namespace** | `stytch.otps.*` | `stytch.otp.*` |
| **OTP params** | `snake_case` | `camelCase` |
| **OAuth** | `start()` or `startWithRedirect()` returns a token; call `authenticate()` separately | `start()` returns the full auth response directly |
| **Google OneTap** | `stytch.oauth.googleOneTap({ onCompleteCallback })` | `stytch.oauth.google.start(...)` with `GoogleCredentialConfiguration` |
| **Biometrics** | `getBiometricRegistrationId()` then `register()` | `getAvailability()` returns an enum, then `register()` or `authenticate()` |
| **Session migration** | — | **Automatic** — existing sessions are migrated on first launch |

---

## New Architecture Requirement

The new SDK uses React Native's [New Architecture (TurboModules)](https://reactnative.dev/architecture/landing-page), which is the default in RN 0.71+ and mandatory from RN 0.80+. If your project is not yet on the New Architecture, you'll need to enable it before upgrading.

---

## Installation

```sh
# Before
npm install @stytch/react-native

# After (Consumer)
npm install @stytch/react-native-consumer

# After (B2B)
npm install @stytch/react-native-b2b
```

Remove the old package from your `package.json` before adding the new one.

---

## Client Initialization

### Before

```tsx
import { StytchClient, StytchProvider } from '@stytch/react-native';

const stytch = new StytchClient('public-token-live-...');

export default function App() {
  return (
    <StytchProvider stytch={stytch}>
      <Nav />
    </StytchProvider>
  );
}
```

### After

```tsx
import {
  createStytchConsumer,
  StytchClientConfiguration,
  StytchProvider,
} from '@stytch/react-native-consumer';

const stytch = createStytchConsumer(
  new StytchClientConfiguration('public-token-live-...')
);

export default function App() {
  return (
    <StytchProvider stytch={stytch}>
      <Nav />
    </StytchProvider>
  );
}
```

The `StytchClientConfiguration` constructor takes optional second and third arguments for `endpointOptions` and `defaultSessionDuration`.

---

## Hooks

### `useStytchUser()` and `useStytchSession()`

The old SDK wrapped these values in a SWR object to indicate whether the value was loaded from cache. The new SDK returns the values directly.

```tsx
// Before
const { user, fromCache } = useStytchUser();
const { session, fromCache } = useStytchSession();
const isLoggedIn = user !== null;

// After
const user = useStytchUser();       // User | undefined
const session = useStytchSession(); // Session | undefined
const isLoggedIn = user !== undefined;
```

### `useStytch()`

Unchanged in name; the returned client type has changed from `StytchClient` to `StytchConsumer`, which has a slightly different method surface (see below).

### New: `useStytchAuthenticationState()`

The new SDK adds a dedicated hook for observing full authentication state. This is the replacement for `stytch.onStateChange()`:

```tsx
import {
  useStytchAuthenticationState,
} from '@stytch/react-native-consumer';
import { ConsumerAuthenticationState } from '@stytch/react-native-consumer';

function AuthGate() {
  const authState = useStytchAuthenticationState();

  if (authState instanceof ConsumerAuthenticationState.Loading) {
    return <Splash />;
  }
  if (authState instanceof ConsumerAuthenticationState.Authenticated) {
    return <AppNavigator />;
  }
  return <AuthNavigator />;
}
```

---

## Authentication State Observation

### Before

```tsx
// In a component or effect
stytch.onStateChange(() => {
  const user = stytch.user.getInfo().user;
  const session = stytch.session.getInfo().session;
  setIsLoggedIn(session !== null);
});
```

### After

The `StytchProvider` handles observation internally — in most cases you don't need to call the observer directly. Just use the hooks. If you do need to observe outside of React (e.g. in a navigation guard), use `authenticationStateObserver`:

```tsx
const cleanup = stytch.authenticationStateObserver((state) => {
  if (state instanceof ConsumerAuthenticationState.Authenticated) {
    // user is logged in
  }
});

// When done:
cleanup.stop();
```

---

## OTP

### Before

```tsx
// snake_case params, `otps` namespace
const res = await stytch.otps.sms.loginOrCreate(phoneNumber, {
  expiration_minutes: 5,
});
setMethodId(res.method_id);

await stytch.otps.authenticate(code, methodId, {
  session_duration_minutes: 60,
});
```

### After

```tsx
// camelCase params, `otp` namespace
const res = await stytch.otp.sms.loginOrCreate({ phoneNumber });
setMethodId(res.methodId);

await stytch.otp.authenticate({
  token: code,
  methodId,
  sessionDurationMinutes: 60,
});
```

Key changes:
- `otps` → `otp`
- `method_id` → `methodId`
- `session_duration_minutes` → `sessionDurationMinutes`
- `authenticate()` now takes a single object, not positional arguments

---

## Email Magic Links

```tsx
// Before
await stytch.magicLinks.email.loginOrCreate(email, {
  login_magic_link_url: redirectUrl,
  signup_magic_link_url: redirectUrl,
});

// After
await stytch.magicLinks.email.loginOrCreate({
  email,
  loginMagicLinkUrl: redirectUrl,
  signupMagicLinkUrl: redirectUrl,
});
```

---

## OAuth

The OAuth flow has been simplified. The old SDK returned an intermediate token from `start()` or `startWithRedirect()` that you then had to pass to `oauth.authenticate()`. The new SDK does everything — browser, redirect, token exchange — inside a single `start()` call.

### Before

```tsx
// Using start() which opens a browser tab and returns a token
const token = await stytch.oauth.github.start({
  login_redirect_url: redirectUrl,
  signup_redirect_url: redirectUrl,
});
await stytch.oauth.authenticate(token, { session_duration_minutes: 60 });
```

### After

```tsx
// One call — opens browser, handles redirect, returns full auth response
const response = await stytch.oauth.github.start({
  loginRedirectUrl: redirectUrl,
  signupRedirectUrl: redirectUrl,
  sessionDurationMinutes: 60,
});
```

No separate `oauth.authenticate()` call needed. The `start()` method accepts a flat parameters object with camelCase keys.

### Google OneTap / Google Credential Manager

The old SDK exposed a dedicated `stytch.oauth.googleOneTap()` method for native Google login. In the new SDK, this is unified into `stytch.oauth.google.start()` — the SDK automatically uses Google Credential Manager when a `GoogleCredentialConfiguration` is provided at initialization.

```tsx
// Before
stytch.oauth.googleOneTap({
  onCompleteCallback: () => navigation.navigate('Profile'),
});

// After (step 1: configure at initialization time)
const stytch = createStytchConsumer(
  new StytchClientConfiguration(
    'public-token-live-...',
    undefined,          // endpointOptions
    undefined,          // defaultSessionDuration
    new GoogleCredentialConfiguration('YOUR_ANDROID_GOOGLE_CLIENT_ID'),
  )
);

// After (step 2: same call as any other provider)
await stytch.oauth.google.start({ sessionDurationMinutes: 60 });
```

If no `GoogleCredentialConfiguration` is provided,  or if Google Credential manager fails on the user's device, `oauth.google.start()` falls back to browser-based OAuth.

---

## Biometrics

The old SDK required you to call `getBiometricRegistrationId()` to check whether a registration existed, then branch manually. The new SDK replaces this with `getAvailability()`, which returns a typed enum, with more explicit statuses.

### Before

```tsx
const regId = await stytch.biometrics.getBiometricRegistrationId();

if (regId) {
  await stytch.biometrics.authenticate({ sessionDurationMinutes: 30 });
} else {
  await stytch.biometrics.register({ sessionDurationMinutes: 30 });
}
```

### After

```tsx
import { BiometricsAvailability } from '@stytch/react-native-consumer';

const availability = await stytch.biometrics.getAvailability({
  sessionDurationMinutes: 30,
});

if (availability === BiometricsAvailability.AlreadyRegistered) {
  await stytch.biometrics.authenticate({ sessionDurationMinutes: 30 });
} else if (availability === BiometricsAvailability.Available) {
  await stytch.biometrics.register({ sessionDurationMinutes: 30 });
}
// Also: BiometricsAvailability.Unavailable, BiometricsAvailability.RegistrationRevoked, etc
```

Biometric option parameters have also been restructured:

```tsx
// Before
await stytch.biometrics.register({
  sessionDurationMinutes: 30,
  androidAllowDeviceCredentials: false,
  androidTitle: 'Authenticate',
  iosReason: 'Authenticate',
});

// After
await stytch.biometrics.register({
  sessionDurationMinutes: 30,
  androidBiometricOptions: {
    allowDeviceCredentials: false,
    title: 'Authenticate',
  },
  iosBiometricOptions: {
    reason: 'Authenticate',
  },
});
```

---

## Session Management

```tsx
// Before
await stytch.session.authenticate({ session_duration_minutes: 30 });
await stytch.session.revoke();

// After
await stytch.session.authenticate({ sessionDurationMinutes: 30 });
await stytch.session.revoke();
```

---

## B2B SDK

The B2B SDK has moved from a sub-path export to a fully separate package.

```tsx
// Before
import { StytchB2BClient } from '@stytch/react-native/b2b';
const b2bClient = new StytchB2BClient('public-token-live-...');

// After
import {
  createStytchB2B,
  StytchClientConfiguration,
} from '@stytch/react-native-b2b';

const b2bClient = createStytchB2B(
  new StytchClientConfiguration('public-token-live-...')
);
```

Hook changes mirror the consumer SDK:

| Old | New |
|---|---|
| `const { member, fromCache } = useStytchMember()` | `const member = useStytchMember()` |
| `const { session, fromCache } = useStytchMemberSession()` | `const memberSession = useStytchMemberSession()` |
| `const { organization, fromCache } = useStytchOrganization()` | `const organization = useStytchOrganization()` |
| `useStytchB2BClient()` | `useStytchB2B()` |
| — | `useStytchB2BAuthenticationState()` (new) |

---

## Automatic Session Migration

There's nothing you need to do. On first launch after upgrading, the new SDK automatically reads your users' existing sessions from the old SDK's storage and migrates them into the new format. Users who were logged in will remain logged in.
