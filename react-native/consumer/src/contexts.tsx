import { createContext } from 'react';
import { StytchConsumer, ApiUserV1User, ApiSessionV1Session, ConsumerAuthenticationState } from '../lib/@stytch/react-native-consumer.mjs'

export const StytchUserContext = createContext<ApiUserV1User | undefined>(undefined);
export const StytchSessionContext = createContext<ApiSessionV1Session | undefined>(undefined);
export const StytchContext = createContext<StytchConsumer>({} as StytchConsumer);
export const StytchAuthenticationStateContext = createContext<ConsumerAuthenticationState>(ConsumerAuthenticationState.Loading);