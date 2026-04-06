// Minimal mock of the KMP-generated lib — used by vitest so tests don't require a built artifact.
// Keep exports in sync with what src/ files actually import from ../lib/b2b-headless.mjs.

export class ApiOrganizationV1Member {}
export class ApiB2bSessionV1MemberSession {}
export class StytchB2B {}
export const B2BAuthenticationState = {
  Loading: class Loading {},
  Authenticated: class Authenticated {
    constructor(public member: ApiOrganizationV1Member, public memberSession: ApiB2bSessionV1MemberSession) {}
  },
  Unauthenticated: class Unauthenticated {},
};
export class B2BSessionsAuthenticateParameters {}
export class B2BSessionsAuthenticateResponse {}
export class ApiOrganizationV1Organization {}
