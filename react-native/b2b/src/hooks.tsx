import {useContext} from 'react';
import { StytchUserContext, StytchSessionContext, StytchContext, StytchAuthenticationStateContext } from './contexts';
import { ApiUserV1User, ApiSessionV1Session, StytchConsumer, ConsumerAuthenticationState } from '../lib/consumer-headless.mjs';

export const useStytchUser = (): ApiUserV1User | undefined => {
  return useContext(StytchUserContext);
};
export const useStytchSession = (): ApiSessionV1Session | undefined => {
  return useContext(StytchSessionContext);
};
export const useStytch = (): StytchConsumer => {
  return useContext(StytchContext);
};
export const useStytchAuthenticationState = (): ConsumerAuthenticationState => {
  return useContext(StytchAuthenticationStateContext);
}