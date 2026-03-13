import { describe, it, expect, jest } from '@jest/globals';
import React from 'react';
import { renderHook } from '@testing-library/react-native';
import {
  useStytchB2B,
  useStytchMember,
  useStytchMemberSession,
  useStytchB2BAuthenticationState,
} from '../hooks';
import {
  StytchB2BContext,
  StytchMemberContext,
  StytchMemberSessionContext,
  StytchB2BAuthenticationStateContext,
} from '../contexts';

jest.mock('../../lib/b2b-headless.mjs', () => ({
  B2BAuthenticationState: { Loading: class Loading {} },
}));

describe('useStytchB2B', () => {
  it('throws when called outside a StytchProvider', () => {
    expect(() => renderHook(() => useStytchB2B())).toThrow(
      'useStytchB2B() must be called within a <StytchProvider>.'
    );
  });

  it('returns the client when inside a StytchProvider', () => {
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    const mockClient = { session: {} } as any;
    const wrapper = ({ children }: { children: React.ReactNode }) => (
      <StytchB2BContext.Provider value={mockClient}>{children}</StytchB2BContext.Provider>
    );
    const { result } = renderHook(() => useStytchB2B(), { wrapper });
    expect(result.current).toBe(mockClient);
  });
});

describe('useStytchMember', () => {
  it('returns undefined when no member is provided', () => {
    const { result } = renderHook(() => useStytchMember());
    expect(result.current).toBeUndefined();
  });

  it('returns the member when provided', () => {
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    const mockMember = { memberId: 'member-123' } as any;
    const wrapper = ({ children }: { children: React.ReactNode }) => (
      <StytchMemberContext.Provider value={mockMember}>{children}</StytchMemberContext.Provider>
    );
    const { result } = renderHook(() => useStytchMember(), { wrapper });
    expect(result.current).toBe(mockMember);
  });
});

describe('useStytchMemberSession', () => {
  it('returns undefined when no member session is provided', () => {
    const { result } = renderHook(() => useStytchMemberSession());
    expect(result.current).toBeUndefined();
  });

  it('returns the member session when provided', () => {
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    const mockSession = { memberSessionId: 'session-123' } as any;
    const wrapper = ({ children }: { children: React.ReactNode }) => (
      <StytchMemberSessionContext.Provider value={mockSession}>
        {children}
      </StytchMemberSessionContext.Provider>
    );
    const { result } = renderHook(() => useStytchMemberSession(), { wrapper });
    expect(result.current).toBe(mockSession);
  });
});

describe('useStytchB2BAuthenticationState', () => {
  it('returns the provided authentication state', () => {
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    const mockState = { type: 'Loading' } as any;
    const wrapper = ({ children }: { children: React.ReactNode }) => (
      <StytchB2BAuthenticationStateContext.Provider value={mockState}>
        {children}
      </StytchB2BAuthenticationStateContext.Provider>
    );
    const { result } = renderHook(() => useStytchB2BAuthenticationState(), { wrapper });
    expect(result.current).toBe(mockState);
  });
});
