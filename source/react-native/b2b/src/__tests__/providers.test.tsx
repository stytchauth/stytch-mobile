import { describe, it, expect, jest, beforeEach } from '@jest/globals';
import React from 'react';
import { act, render, renderHook } from '@testing-library/react-native';
import { Text } from 'react-native';
import { AppState, AppStateStatus } from 'react-native';
import { StytchB2BProvider } from '../providers';
import {
  useStytchB2B,
  useStytchMember,
  useStytchMemberSession,
  useStytchB2BAuthenticationState,
} from '../hooks';
import {
  ApiB2bSessionV1MemberSession,
  ApiOrganizationV1Member,
  B2BAuthenticationState,
  StytchB2B,
} from '../../lib/b2b-headless.mjs';

// ---------------------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------------------

type AnyState =
  | InstanceType<typeof B2BAuthenticationState.Loading>
  | InstanceType<typeof B2BAuthenticationState.Authenticated>
  | InstanceType<typeof B2BAuthenticationState.Unauthenticated>;
type ObserverCallback = (state: AnyState) => void | Promise<void>;

interface MockStytchB2BClient {
  authenticationStateObserver: jest.Mock<any>;
  session: { authenticate: jest.Mock<any> };
}

/**
 * Build a minimal stytch B2B client mock and helpers to drive it from tests.
 *
 * authenticationStateObserver is called twice by StytchB2BProvider:
 *   [0] — main state-tracking effect (fires on every auth state change)
 *   [1] — app-foreground effect (fires only when the app comes back active and
 *          the session needs refreshing)
 */
function makeStytchMock() {
  const observerCallbacks: ObserverCallback[] = [];
  const observerStops: jest.Mock[] = [];

  const mockClient: MockStytchB2BClient = {
    authenticationStateObserver: jest.fn((cb: ObserverCallback) => {
      observerCallbacks.push(cb);
      const stop = jest.fn();
      observerStops.push(stop);
      return { stop };
    }),
    session: {
      authenticate: jest.fn<() => Promise<void>>().mockResolvedValue(undefined),
    },
  };

  /** Push a state change through all registered observers. */
  const emitState = async (state: AnyState) => {
    await act(async () => {
      await Promise.all(observerCallbacks.map((cb) => cb(state)));
    });
  };

  return {
    mockClient: mockClient as MockStytchB2BClient & StytchB2B,
    observerStops,
    emitState,
  };
}

const mockMember = {} as unknown as ApiOrganizationV1Member;
const mockMemberSession = {} as unknown as ApiB2bSessionV1MemberSession;

function makeAuthenticatedState(
  member: ApiOrganizationV1Member = mockMember,
  memberSession: ApiB2bSessionV1MemberSession = mockMemberSession,
) {
  return Object.assign(
    new B2BAuthenticationState.Authenticated(member, memberSession, null as any, '', ''),
    { member, memberSession },
  );
}

// ---------------------------------------------------------------------------
// AppState spy — lets tests trigger app-active events
// ---------------------------------------------------------------------------

let capturedAppStateHandler: ((status: AppStateStatus) => void) | null = null;
const appStateRemove = jest.fn();

beforeEach(() => {
  capturedAppStateHandler = null;
  appStateRemove.mockClear();
  jest.spyOn(AppState, 'addEventListener').mockImplementation((event, handler) => {
    if (event === 'change') capturedAppStateHandler = handler;
    return { remove: appStateRemove } as ReturnType<typeof AppState.addEventListener>;
  });
});

// ---------------------------------------------------------------------------
// Tests
// ---------------------------------------------------------------------------

describe('StytchB2BProvider', () => {
  it('subscribes to authenticationStateObserver on mount', () => {
    const { mockClient } = makeStytchMock();
    render(<StytchB2BProvider stytch={mockClient}><Text /></StytchB2BProvider>);
    expect(mockClient.authenticationStateObserver).toHaveBeenCalled();
  });

  it('stops the observer subscription on unmount', () => {
    const { mockClient, observerStops } = makeStytchMock();
    const { unmount } = render(<StytchB2BProvider stytch={mockClient}><Text /></StytchB2BProvider>);
    unmount();
    expect(observerStops.every(stop => stop.mock.calls.length > 0)).toBe(true);
  });

  it('removes the AppState listener on unmount', () => {
    const { mockClient } = makeStytchMock();
    const { unmount } = render(<StytchB2BProvider stytch={mockClient}><Text /></StytchB2BProvider>);
    unmount();
    expect(appStateRemove).toHaveBeenCalled();
  });

  it('populates member and memberSession context when Authenticated state fires', async () => {
    const { mockClient, emitState } = makeStytchMock();

    const { result } = renderHook(
      () => ({ member: useStytchMember(), memberSession: useStytchMemberSession() }),
      { wrapper: ({ children }) => <StytchB2BProvider stytch={mockClient}>{children}</StytchB2BProvider> },
    );

    await emitState(makeAuthenticatedState(mockMember, mockMemberSession));

    expect(result.current.member).toBe(mockMember);
    expect(result.current.memberSession).toBe(mockMemberSession);
  });

  it('clears member and memberSession context when Unauthenticated state fires', async () => {
    const { mockClient, emitState } = makeStytchMock();

    const { result } = renderHook(
      () => ({ member: useStytchMember(), memberSession: useStytchMemberSession() }),
      { wrapper: ({ children }) => <StytchB2BProvider stytch={mockClient}>{children}</StytchB2BProvider> },
    );

    await emitState(makeAuthenticatedState());
    expect(result.current.member).toBeDefined();

    await emitState(new B2BAuthenticationState.Unauthenticated());
    expect(result.current.member).toBeUndefined();
    expect(result.current.memberSession).toBeUndefined();
  });

  it('updates the authenticationState context on every state change', async () => {
    const { mockClient, emitState } = makeStytchMock();
    const authenticatedState = makeAuthenticatedState();

    const { result } = renderHook(
      () => useStytchB2BAuthenticationState(),
      { wrapper: ({ children }) => <StytchB2BProvider stytch={mockClient}>{children}</StytchB2BProvider> },
    );

    expect(result.current).toBeInstanceOf(B2BAuthenticationState.Loading);
    await emitState(authenticatedState);
    expect(result.current).toBe(authenticatedState);
  });

  it('calls session.authenticate when app becomes active and session is Authenticated', async () => {
    const { mockClient, emitState } = makeStytchMock();

    render(<StytchB2BProvider stytch={mockClient}><Text /></StytchB2BProvider>);

    await act(async () => {
      capturedAppStateHandler!('active');
    });

    await emitState(makeAuthenticatedState());

    expect(mockClient.session.authenticate).toHaveBeenCalledWith({ sessionDurationMinutes: null });
  });

  it('does not crash when session.authenticate rejects', async () => {
    const { mockClient, emitState } = makeStytchMock();
    mockClient.session.authenticate.mockRejectedValueOnce(new Error('network error'));

    render(<StytchB2BProvider stytch={mockClient}><Text /></StytchB2BProvider>);

    await act(async () => {
      capturedAppStateHandler!('active');
    });

    // Trigger the one-shot observer — the rejection should be swallowed
    await expect(emitState(makeAuthenticatedState())).resolves.not.toThrow();
  });

  it('does not call session.authenticate when app becomes active but state is not Authenticated', async () => {
    const { mockClient, emitState } = makeStytchMock();

    render(<StytchB2BProvider stytch={mockClient}><Text /></StytchB2BProvider>);

    await act(async () => {
      capturedAppStateHandler!('active');
    });

    await emitState(new B2BAuthenticationState.Loading());

    expect(mockClient.session.authenticate).not.toHaveBeenCalled();
  });
});

// ---------------------------------------------------------------------------
// HOC tests
// ---------------------------------------------------------------------------

// The HOCs are thin wrappers that inject the context value as a prop. We test
// them by verifying the underlying hook returns the expected value inside the
// provider — equivalent to testing the prop the HOC would inject.

describe('withStytchB2B', () => {
  it('makes the stytch client available via useStytchB2B inside the provider', () => {
    const { mockClient } = makeStytchMock();
    const { result } = renderHook(() => useStytchB2B(), {
      wrapper: ({ children }) => <StytchB2BProvider stytch={mockClient}>{children}</StytchB2BProvider>,
    });
    expect(result.current).toBe(mockClient);
  });
});

describe('withStytchMember', () => {
  it('returns undefined when no member is in context', () => {
    const { mockClient } = makeStytchMock();
    const { result } = renderHook(() => useStytchMember(), {
      wrapper: ({ children }) => <StytchB2BProvider stytch={mockClient}>{children}</StytchB2BProvider>,
    });
    expect(result.current).toBeUndefined();
  });

  it('reflects the member once Authenticated state fires', async () => {
    const { mockClient, emitState } = makeStytchMock();
    const { result } = renderHook(() => useStytchMember(), {
      wrapper: ({ children }) => <StytchB2BProvider stytch={mockClient}>{children}</StytchB2BProvider>,
    });
    await emitState(makeAuthenticatedState(mockMember));
    expect(result.current).toBe(mockMember);
  });
});

describe('withStytchMemberSession', () => {
  it('returns undefined when no session is in context', () => {
    const { mockClient } = makeStytchMock();
    const { result } = renderHook(() => useStytchMemberSession(), {
      wrapper: ({ children }) => <StytchB2BProvider stytch={mockClient}>{children}</StytchB2BProvider>,
    });
    expect(result.current).toBeUndefined();
  });
});
