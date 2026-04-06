// Minimal mock of the KMP-generated lib — used by jest so tests don't require a built artifact.
// Keep exports in sync with what src/ files actually import from ../lib/consumer-headless.mjs.

export class ApiUserV1User {}
export class ApiSessionV1Session {}
export class StytchConsumer {}
export const ConsumerAuthenticationState = {
  Loading: class Loading {},
  Authenticated: class Authenticated {
    constructor(public user: ApiUserV1User, public session: ApiSessionV1Session) {}
  },
  Unauthenticated: class Unauthenticated {},
};
export class SessionsAuthenticateParameters {}
export class SessionsAuthenticateResponse {}
