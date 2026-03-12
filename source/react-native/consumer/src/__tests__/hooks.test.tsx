import React from 'react';
import { describe, it, expect } from 'vitest';
import { renderHook } from '@testing-library/react';
import { useStytch, useStytchUser, useStytchSession, useStytchAuthenticationState } from '../hooks';
import {
  StytchContext,
  StytchUserContext,
  StytchSessionContext,
  StytchAuthenticationStateContext,
} from '../contexts';

describe('useStytch', () => {
  it('throws when called outside a StytchProvider', () => {
    expect(() => renderHook(() => useStytch())).toThrow(
      'useStytch() must be called within a <StytchProvider>.',
    );
  });

  it('returns the client when inside a StytchProvider', () => {
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    const mockClient = { otp: {} } as any;
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
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    const mockUser = { userId: 'user-123' } as any;
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
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    const mockSession = { sessionId: 'session-123' } as any;
    const wrapper = ({ children }: { children: React.ReactNode }) => (
      <StytchSessionContext.Provider value={mockSession}>{children}</StytchSessionContext.Provider>
    );
    const { result } = renderHook(() => useStytchSession(), { wrapper });
    expect(result.current).toBe(mockSession);
  });
});

describe('useStytchAuthenticationState', () => {
  it('returns the provided authentication state', () => {
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    const mockState = { type: 'Loading' } as any;
    const wrapper = ({ children }: { children: React.ReactNode }) => (
      <StytchAuthenticationStateContext.Provider value={mockState}>{children}</StytchAuthenticationStateContext.Provider>
    );
    const { result } = renderHook(() => useStytchAuthenticationState(), { wrapper });
    expect(result.current).toBe(mockState);
  });
});
