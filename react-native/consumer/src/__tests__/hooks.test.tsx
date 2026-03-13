import { describe, it, expect, jest } from '@jest/globals';
import React from 'react';
import { renderHook } from '@testing-library/react-native';
import { useStytch, useStytchUser, useStytchSession, useStytchAuthenticationState } from '../hooks';
import {
  StytchContext,
  StytchUserContext,
  StytchSessionContext,
  StytchAuthenticationStateContext,
} from '../contexts';

type StytchConsumer = NonNullable<React.ContextType<typeof StytchContext>>;
type ApiUserV1User = NonNullable<React.ContextType<typeof StytchUserContext>>;
type ApiSessionV1Session = NonNullable<React.ContextType<typeof StytchSessionContext>>;
type ConsumerAuthenticationState = React.ContextType<typeof StytchAuthenticationStateContext>;

jest.mock('../../lib/consumer-headless.mjs', () => ({
  ConsumerAuthenticationState: { Loading: class Loading {} },
}));

describe('useStytch', () => {
  it('throws when called outside a StytchProvider', () => {
    expect(() => renderHook(() => useStytch())).toThrow(
      'useStytch() must be called within a <StytchProvider>.'
    );
  });

  it('returns the client when inside a StytchProvider', () => {
    const mockClient = { otp: {} } as StytchConsumer;
    const wrapper = ({ children }: { children: React.ReactNode }) => (
      <StytchContext.Provider value={mockClient}>{children}</StytchContext.Provider>
    );
    const { result } = renderHook(() => useStytch(), { wrapper });
    expect(result.current).toBe(mockClient);
  });
});

describe('useStytchUser', () => {
  it('returns undefined when no user is provided', () => {
    const { result } = renderHook(() => useStytchUser());
    expect(result.current).toBeUndefined();
  });

  it('returns the user when provided', () => {
    const mockUser = { userId: 'user-123' } as ApiUserV1User;
    const wrapper = ({ children }: { children: React.ReactNode }) => (
      <StytchUserContext.Provider value={mockUser}>{children}</StytchUserContext.Provider>
    );
    const { result } = renderHook(() => useStytchUser(), { wrapper });
    expect(result.current).toBe(mockUser);
  });
});

describe('useStytchSession', () => {
  it('returns undefined when no session is provided', () => {
    const { result } = renderHook(() => useStytchSession());
    expect(result.current).toBeUndefined();
  });

  it('returns the session when provided', () => {
    const mockSession = { sessionId: 'session-123' } as ApiSessionV1Session;
    const wrapper = ({ children }: { children: React.ReactNode }) => (
      <StytchSessionContext.Provider value={mockSession}>{children}</StytchSessionContext.Provider>
    );
    const { result } = renderHook(() => useStytchSession(), { wrapper });
    expect(result.current).toBe(mockSession);
  });
});

describe('useStytchAuthenticationState', () => {
  it('returns the provided authentication state', () => {
    const mockState = { type: 'Loading' } as ConsumerAuthenticationState;
    const wrapper = ({ children }: { children: React.ReactNode }) => (
      <StytchAuthenticationStateContext.Provider value={mockState}>
        {children}
      </StytchAuthenticationStateContext.Provider>
    );
    const { result } = renderHook(() => useStytchAuthenticationState(), { wrapper });
    expect(result.current).toBe(mockState);
  });
});
