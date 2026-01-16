import {useContext} from 'react';
import { StytchUserContext, StytchSessionContext, StytchContext, StytchAuthenticationStateContext } from './contexts';
import { User, Session, StytchConsumer, ConsumerAuthenticationState } from '../lib/@stytch/react-native-consumer.mjs';

export const useStytchUser = (): User | undefined => {
  return useContext(StytchUserContext);
};
export const useStytchSession = (): Session | undefined => {
  return useContext(StytchSessionContext);
};
export const useStytch = (): StytchConsumer => {
  return useContext(StytchContext);
};
export const useStytchAuthenticationState = (): ConsumerAuthenticationState => {
  return useContext(StytchAuthenticationStateContext);
}