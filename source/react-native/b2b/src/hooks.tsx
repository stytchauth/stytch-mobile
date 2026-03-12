import {useContext} from 'react';
import { StytchMemberContext, StytchMemberSessionContext, StytchB2BContext, StytchB2BAuthenticationStateContext } from './contexts';
import { ApiOrganizationV1Member, ApiB2bSessionV1MemberSession, StytchB2B, B2BAuthenticationState } from '../lib/b2b-headless.mjs';

export const useStytchMember = (): ApiOrganizationV1Member | undefined => {
  return useContext(StytchMemberContext);
};
export const useStytchMemberSession = (): ApiB2bSessionV1MemberSession | undefined => {
  return useContext(StytchMemberSessionContext);
};
export const useStytchB2B = (): StytchB2B => {
  const client = useContext(StytchB2BContext);
  if (client === null) {
    throw new Error('useStytchB2B() must be called within a <StytchProvider>.');
  }
  return client;
};
export const useStytchB2BAuthenticationState = (): B2BAuthenticationState => {
  return useContext(StytchB2BAuthenticationStateContext);
}