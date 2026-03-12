import React, { useState, useEffect } from 'react';
import {
  StytchConsumer,
  ApiUserV1User,
  ApiSessionV1Session,
  ConsumerAuthenticationState,
} from '../lib/consumer-headless.mjs';
import { AppState, AppStateStatus } from 'react-native';
import { useStytch, useStytchUser, useStytchSession, useStytchAuthenticationState } from './hooks';
import {
  StytchContext,
  StytchUserContext,
  StytchSessionContext,
  StytchAuthenticationStateContext,
} from './contexts';
import { mergeWithStableProps } from './utils';

export const withStytch = <T extends object>(
  Component: React.ComponentType<T & { stytch: StytchConsumer }>
): React.ComponentType<T> => {
  const WithStytch: React.ComponentType<T> = (props) => {
    return <Component {...props} stytch={useStytch()} />;
  };
  WithStytch.displayName = `withStytch(${Component.displayName || Component.name || 'Component'})`;
  return WithStytch;
};
export const withStytchUser = <T extends object>(
  Component: React.ComponentType<T & { stytchUser: ApiUserV1User | undefined }>
): React.ComponentType<T> => {
  const WithStytchUser: React.ComponentType<T> = (props) => {
    const user = useStytchUser();
    return <Component {...props} stytchUser={user} />;
  };
  WithStytchUser.displayName = `withStytchUser(${Component.displayName || Component.name || 'Component'})`;
  return WithStytchUser;
};
export const withStytchSession = <T extends object>(
  Component: React.ComponentType<T & { stytchSession: ApiSessionV1Session | undefined }>
): React.ComponentType<T> => {
  const WithStytchSession: React.ComponentType<T> = (props) => {
    const session = useStytchSession();
    return <Component {...props} stytchSession={session} />;
  };
  WithStytchSession.displayName = `withStytchSession(${Component.displayName || Component.name || 'Component'})`;
  return WithStytchSession;
};
export const withStytchAuthenticationState = <T extends object>(
  Component: React.ComponentType<T & ConsumerAuthenticationState>
): React.ComponentType<T> => {
  const WithStytchAuthenticationState: React.ComponentType<T> = (props) => {
    const state = useStytchAuthenticationState();
    return <Component {...props} stytchAuthenticationState={state} />;
  };
  WithStytchAuthenticationState.displayName = `withStytchAuthenticationState(${Component.displayName || Component.name || 'Component'})`;
  return WithStytchAuthenticationState;
};

export type StytchProviderProps = {
  stytch: StytchConsumer;
  children?: React.ReactNode;
};

export const StytchProvider = ({ stytch, children }: StytchProviderProps): React.JSX.Element => {
  const [{ user, session }, setClientState] = useState<{
    user: ApiUserV1User | undefined;
    session: ApiSessionV1Session | undefined;
  }>({
    session: undefined,
    user: undefined,
  });
  const [authenticationState, setAuthenticationState] = useState<ConsumerAuthenticationState>(
    new ConsumerAuthenticationState.Loading()
  );

  useEffect(() => {
    const handleAppStateChange = async (appState: AppStateStatus) => {
      if (appState === 'active') {
        tryAuthenticate();
      }
    };
    const appStateSubscription = AppState.addEventListener('change', handleAppStateChange);
    const tryAuthenticate = async () => {
      const observationJob = stytch.authenticationStateObserver(
        async (state: ConsumerAuthenticationState) => {
          if (state instanceof ConsumerAuthenticationState.Authenticated) {
            try {
              await stytch.session.authenticate({ sessionDurationMinutes: null });
            } catch {
              // log it
            }
          }
          observationJob.stop();
        }
      );
    };
    return () => {
      appStateSubscription.remove();
    };
  }, [stytch]);

  useEffect(() => {
    const observationJob = stytch.authenticationStateObserver(
      (state: ConsumerAuthenticationState) => {
        let newUser: ApiUserV1User | undefined = undefined;
        let newSession: ApiSessionV1Session | undefined = undefined;
        if (state instanceof ConsumerAuthenticationState.Authenticated) {
          newUser = (state as ConsumerAuthenticationState.Authenticated).user;
          newSession = (state as ConsumerAuthenticationState.Authenticated).session;
        }
        setClientState((oldState) => {
          const newState = { user: newUser, session: newSession };
          return mergeWithStableProps(oldState, newState);
        });
        setAuthenticationState(state);
      }
    );
    return () => {
      observationJob.stop();
    };
  }, [setClientState, stytch]);

  return (
    <StytchContext.Provider value={stytch}>
      <StytchUserContext.Provider value={user}>
        <StytchSessionContext.Provider value={session}>
          <StytchAuthenticationStateContext.Provider value={authenticationState}>
            {children}
          </StytchAuthenticationStateContext.Provider>
        </StytchSessionContext.Provider>
      </StytchUserContext.Provider>
    </StytchContext.Provider>
  );
};
