import React, { useState, useEffect } from 'react';
import { StytchConsumer, User, Session, ConsumerAuthenticationState, SessionsAuthenticateRequest } from '../lib/@stytch/react-native-consumer.mjs'
import { AppState, AppStateStatus } from 'react-native';
import { useStytch, useStytchUser, useStytchSession } from './hooks';
import { StytchContext, StytchUserContext, StytchSessionContext } from './contexts';
import { mergeWithStableProps } from './utils';

export const withStytch = <T extends object>(Component: React.ComponentType<T & { stytch: StytchConsumer }>): React.ComponentType<T> => {
  const WithStytch: React.ComponentType<T> = (props) => {
    return <Component {...props} stytch={useStytch()} />;
  };
  WithStytch.displayName = `withStytch(${Component.displayName || Component.name || 'Component'})`;
  return WithStytch;
};
export const withStytchUser = <T extends object>(
  Component: React.ComponentType<T & { stytchUser: User | undefined }>,
): React.ComponentType<T> => {
  const WithStytchUser: React.ComponentType<T> = (props) => {
    const user = useStytchUser();
    return <Component {...props} stytchUser={user} />;
  };
  WithStytchUser.displayName = `withStytchUser(${Component.displayName || Component.name || 'Component'})`;
  return WithStytchUser;
};
export const withStytchSession = <T extends object>(
  Component: React.ComponentType<T & { stytchSession: Session | undefined }>,
): React.ComponentType<T> => {
  const WithStytchSession: React.ComponentType<T> = (props) => {
    const session = useStytchSession();
    return <Component {...props} stytchSession={session} />;
  };
  WithStytchSession.displayName = `withStytchSession(${Component.displayName || Component.name || 'Component'})`;
  return WithStytchSession;
};

export type StytchProviderProps = {
  stytch: StytchConsumer;
  children?: React.ReactNode;
};

export const StytchProvider = ({
  stytch,
  children,
}: StytchProviderProps): React.JSX.Element => {
  const [{ user, session }, setClientState] = useState<{ user: User | undefined, session: Session | undefined}>({
    session: undefined,
    user: undefined,
  });

  useEffect(() => {
    const handleAppStateChange = async (appState: AppStateStatus) => {
      if (appState === 'active') {
        tryAuthenticate();
      }
    };
    const appStateSubscription = AppState.addEventListener('change', handleAppStateChange);
    const tryAuthenticate = async () => {
      const observationJob = stytch.authenticationStateObserver(async (state: ConsumerAuthenticationState) => {
        if (state == ConsumerAuthenticationState.Authenticated) {
          try {
            await stytch.session.authenticate(new SessionsAuthenticateRequest(null));
          } catch {
            // log it
          }
        }
        observationJob.stop();
      });
    };
    return () => {
      appStateSubscription.remove();
    };
  }, [stytch]);

  useEffect(
    () => {
      const observationJob = stytch.authenticationStateObserver((state: ConsumerAuthenticationState) => {
        let newUser: User | undefined = undefined
        let newSession: Session | undefined = undefined
        if (state == ConsumerAuthenticationState.Authenticated) {
          newUser = (state as ConsumerAuthenticationState.Authenticated).user
          newSession = (state as ConsumerAuthenticationState.Authenticated).session
        }
        console.log("OBESERVER FIRED?")
        setClientState((oldState) => {
          const newState = { user: newUser, session: newSession };
          return mergeWithStableProps(oldState, newState);
        });
      });
      return () => { observationJob.stop() }
    },
    [setClientState, stytch],
  );

  return (
    <StytchContext.Provider value={stytch}>
      <StytchUserContext.Provider value={user}>
        <StytchSessionContext.Provider value={session}>{children}</StytchSessionContext.Provider>
      </StytchUserContext.Provider>
    </StytchContext.Provider>
  );
};