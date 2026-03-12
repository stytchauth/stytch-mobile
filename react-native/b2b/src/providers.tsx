import React, { useState, useEffect } from 'react';
import {
  StytchB2B,
  ApiOrganizationV1Member,
  ApiB2bSessionV1MemberSession,
  B2BAuthenticationState,
} from '../lib/b2b-headless.mjs';
import { AppState, AppStateStatus } from 'react-native';
import {
  useStytchB2B,
  useStytchMember,
  useStytchMemberSession,
  useStytchB2BAuthenticationState,
} from './hooks';
import {
  StytchB2BContext,
  StytchMemberContext,
  StytchMemberSessionContext,
  StytchB2BAuthenticationStateContext,
} from './contexts';
import { mergeWithStableProps } from './utils';

export const withStytchB2B = <T extends object>(
  Component: React.ComponentType<T & { stytch: StytchB2B }>
): React.ComponentType<T> => {
  const WithStytch: React.ComponentType<T> = (props) => {
    return <Component {...props} stytch={useStytchB2B()} />;
  };
  WithStytch.displayName = `withStytchB2B(${Component.displayName || Component.name || 'Component'})`;
  return WithStytch;
};
export const withStytchMember = <T extends object>(
  Component: React.ComponentType<T & { stytchMember: ApiOrganizationV1Member | undefined }>
): React.ComponentType<T> => {
  const WithStytchMember: React.ComponentType<T> = (props) => {
    const member = useStytchMember();
    return <Component {...props} stytchMember={member} />;
  };
  WithStytchMember.displayName = `withStytchMember(${Component.displayName || Component.name || 'Component'})`;
  return WithStytchMember;
};
export const withStytchMemberSession = <T extends object>(
  Component: React.ComponentType<T & { stytchSession: ApiB2bSessionV1MemberSession | undefined }>
): React.ComponentType<T> => {
  const WithStytchMemberSession: React.ComponentType<T> = (props) => {
    const memberSession = useStytchMemberSession();
    return <Component {...props} stytchSession={memberSession} />;
  };
  WithStytchMemberSession.displayName = `withStytchMemberSession(${Component.displayName || Component.name || 'Component'})`;
  return WithStytchMemberSession;
};
export const withStytchB2BAuthenticationState = <T extends object>(
  Component: React.ComponentType<T & B2BAuthenticationState>
): React.ComponentType<T> => {
  const WithStytchB2BAuthenticationState: React.ComponentType<T> = (props) => {
    const state = useStytchB2BAuthenticationState();
    return <Component {...props} stytchAuthenticationState={state} />;
  };
  WithStytchB2BAuthenticationState.displayName = `withStytchB2BAuthenticationState(${Component.displayName || Component.name || 'Component'})`;
  return WithStytchB2BAuthenticationState;
};

export type StytchB2BProviderProps = {
  stytch: StytchB2B;
  children?: React.ReactNode;
};

export const StytchProvider = ({ stytch, children }: StytchB2BProviderProps): React.JSX.Element => {
  const [{ member, memberSession }, setClientState] = useState<{
    member: ApiOrganizationV1Member | undefined;
    memberSession: ApiB2bSessionV1MemberSession | undefined;
  }>({
    member: undefined,
    memberSession: undefined,
  });
  const [authenticationState, setAuthenticationState] = useState<B2BAuthenticationState>(
    new B2BAuthenticationState.Loading()
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
        async (state: B2BAuthenticationState) => {
          if (state instanceof B2BAuthenticationState.Authenticated) {
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
    const observationJob = stytch.authenticationStateObserver((state: B2BAuthenticationState) => {
      let newMember: ApiOrganizationV1Member | undefined = undefined;
      let newMemberSession: ApiB2bSessionV1MemberSession | undefined = undefined;
      if (state instanceof B2BAuthenticationState.Authenticated) {
        newMember = (state as B2BAuthenticationState.Authenticated).member;
        newMemberSession = (state as B2BAuthenticationState.Authenticated).memberSession;
      }
      setClientState((oldState) => {
        const newState = { member: newMember, memberSession: newMemberSession };
        return mergeWithStableProps(oldState, newState);
      });
      setAuthenticationState(state);
    });
    return () => {
      observationJob.stop();
    };
  }, [setClientState, stytch]);

  return (
    <StytchB2BContext.Provider value={stytch}>
      <StytchMemberContext.Provider value={member}>
        <StytchMemberSessionContext.Provider value={memberSession}>
          <StytchB2BAuthenticationStateContext.Provider value={authenticationState}>
            {children}
          </StytchB2BAuthenticationStateContext.Provider>
        </StytchMemberSessionContext.Provider>
      </StytchMemberContext.Provider>
    </StytchB2BContext.Provider>
  );
};
