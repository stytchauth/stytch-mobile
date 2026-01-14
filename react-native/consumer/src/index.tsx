import * as React from 'react';
import { AppState, AppStateStatus } from 'react-native';
import { StytchConsumer, User, Session, SessionsAuthenticateRequest, ConsumerAuthenticationState } from '../lib/@stytch/react-native-consumer.mjs'

const deepEqual = (a: any, b: any): boolean => {
    // Ensures type is the same
    if (typeof a !== typeof b) return false;
    // arrays, null, and objects all have type 'object'
    if (a === null || b === null) return a === b;
    if (typeof a === 'object') {
        if (Object.keys(a).length !== Object.keys(b).length || Object.keys(a).some((k) => !(k in b))) return false;
        return Object.entries(a).every(([k, v]) => deepEqual(v, b[k]));
    }
    // boolean, string, number, undefined
    return a === b;
};

const mergeWithStableProps = <T extends Record<string, unknown>, U extends Record<string, unknown> = T>(
  oldValue: U,
  newValue: T,
): T => {
  // If the values are already referentially the same, just return the new value
  if ((oldValue as unknown) === newValue) {
    return newValue;
  }

  return Object.keys(oldValue).reduce(
    (acc, key) => {
      if (key in newValue && deepEqual(oldValue[key], newValue[key])) {
        acc[key as keyof T] = oldValue[key] as unknown as T[keyof T];
      }
      return acc;
    },
    { ...newValue },
  );
};

export const StytchUserContext = React.createContext<User | undefined>(undefined);
export const StytchSessionContext = React.createContext<Session | undefined>(undefined);
export const StytchContext = React.createContext<StytchConsumer>({} as StytchConsumer);

export const useStytchUser = (): User | undefined => {
  return React.useContext(StytchUserContext);
};
export const useStytchSession = (): Session | undefined => {
  return React.useContext(StytchSessionContext);
};
export const useStytch = (): StytchConsumer => {
  return React.useContext(StytchContext);
};

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
  const [{ user, session }, setClientState] = React.useState<{ user: User | undefined, session: Session | undefined}>({
    session: undefined,
    user: undefined,
  });

  React.useEffect(() => {
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
  }, [stytch.session]);

  React.useEffect(
    () => {
      const observationJob = stytch.authenticationStateObserver((state: ConsumerAuthenticationState) => {
        var newUser: User | undefined = undefined
        var newSession: Session | undefined = undefined
        if (state == ConsumerAuthenticationState.Authenticated) {
          newUser = (state as ConsumerAuthenticationState.Authenticated).user
          newSession = (state as ConsumerAuthenticationState.Authenticated).session
        }
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


// export everything from the KMP library for consumption in the client
export * from '../lib/@stytch/react-native-consumer.mjs';