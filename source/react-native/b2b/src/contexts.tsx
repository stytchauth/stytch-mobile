import { createContext } from 'react';
import {
  StytchB2B,
  ApiOrganizationV1Member,
  ApiOrganizationV1Organization,
  ApiB2bSessionV1MemberSession,
  B2BAuthenticationState,
} from '../lib/b2b-headless.mjs';

export const StytchMemberContext = createContext<ApiOrganizationV1Member | undefined>(undefined);
export const StytchMemberSessionContext = createContext<ApiB2bSessionV1MemberSession | undefined>(
  undefined,
);
export const StytchOrganizationContext = createContext<ApiOrganizationV1Organization | undefined>(
  undefined
);
export const StytchB2BContext = createContext<StytchB2B | null>(null);
export const StytchB2BAuthenticationStateContext = createContext<B2BAuthenticationState>(
  B2BAuthenticationState.Loading,
);
