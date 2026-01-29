import { createContext } from 'react';
import { StytchConsumer, User, Session, ConsumerAuthenticationState } from '../lib/@stytch/react-native-consumer.mjs'

export const StytchUserContext = createContext<User | undefined>(undefined);
export const StytchSessionContext = createContext<Session | undefined>(undefined);
export const StytchContext = createContext<StytchConsumer>({} as StytchConsumer);
export const StytchAuthenticationStateContext = createContext<ConsumerAuthenticationState>(ConsumerAuthenticationState.Loading);