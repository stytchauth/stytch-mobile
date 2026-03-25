import { describe, it, expect, jest, beforeEach } from '@jest/globals';
import React from 'react';
import { act, render, renderHook } from '@testing-library/react-native';
import { Text } from 'react-native';
import { AppState } from 'react-native';
import { StytchProvider } from '../providers';
import { useStytch, useStytchUser, useStytchSession, useStytchAuthenticationState } from '../hooks';
import {
  ApiSessionV1Session,
  ApiUserV1User,
  ConsumerAuthenticationState,
  StytchConsumer,
} from '../../lib/consumer-headless.mjs';

// ---------------------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------------------

type AnyState =
  | InstanceType<typeof ConsumerAuthenticationState.Loading>
  | InstanceType<typeof ConsumerAuthenticationState.Authenticated>
  | InstanceType<typeof ConsumerAuthenticationState.Unauthenticated>;
type ObserverCallback = (state: AnyState) => void | Promise<void>;

interface MockStytchClient {
  authenticationStateObserver: jest.Mock;
  session: { authenticate: jest.Mock };
}

/**
 * Build a minimal stytch client mock and helpers to drive it from tests.
 *
 * authenticationStateObserver is called twice by StytchProvider:
 *   [0] — main state-tracking effect (fires on every auth state change)
 *   [1] — app-foreground effect (fires only when the app comes back active and
 *          the session needs refreshing)
 */

function makeStytchMock() {
  const observerCallbacks: ObserverCallback[] = [];
  const observerStops: jest.Mock[] = [];

  const mockClient: MockStytchClient = {
    authenticationStateObserver: jest.fn((cb: ObserverCallback) => {
      observerCallbacks.push(cb);
      const stop = jest.fn();
      observerStops.push(stop);
      return { stop };
    }),
    session: {
      authenticate: jest.fn().mockResolvedValue({}),
    },
  };

  /** Push a state change through all registered observers. */
  const emitState = async (state: AnyState) => {
    await act(async () => {
      await Promise.all(observerCallbacks.map((cb) => cb(state)));
    });
  };

  return {
    mockClient: mockClient as MockStytchClient & StytchConsumer,
    observerStops,
    emitState,
  };
}

function makeAuthenticatedState(
  user: ApiUserV1User = new ApiUserV1User(),
  session: ApiSessionV1Session = new ApiSessionV1Session(),
) {
  return Object.assign(new ConsumerAuthenticationState.Authenticated(), { user, session });
}

// ---------------------------------------------------------------------------
// AppState spy — lets tests trigger app-active events
// ---------------------------------------------------------------------------

let capturedAppStateHandler: ((status: string) => void) | null = null;
const appStateRemove = jest.fn();

beforeEach(() => {
  capturedAppStateHandler = null;
  appStateRemove.mockClear();
  jest.spyOn(AppState, 'addEventListener').mockImplementation((event: string, handler: (status: string) => void) => {
    if (event === 'change') capturedAppStateHandler = handler;
    return { remove: appStateRemove } as ReturnType<typeof AppState.addEventListener>;
  });
});

// ---------------------------------------------------------------------------
// Tests
// ---------------------------------------------------------------------------

describe('StytchProvider', () => {
  it('renders its children', () => {
    const { mockClient } = makeStytchMock();
    const { getByText } = render(
      <StytchProvider stytch={mockClient}>
        <Text>hello</Text>
      </StytchProvider>,
    );
    expect(getByText('hello')).toBeTruthy();
  });

  it('subscribes to authenticationStateObserver on mount', () => {
    const { mockClient } = makeStytchMock();
    render(
      <StytchProvider stytch={mockClient}>
        <Text />
      </StytchProvider>,
    );
    expect(mockClient.authenticationStateObserver).toHaveBeenCalled();
  });

  it('stops the observer subscription on unmount', () => {
    const { mockClient, observerStops } = makeStytchMock();
    const { unmount } = render(
      <StytchProvider stytch={mockClient}>
        <Text />
      </StytchProvider>,
    );
    unmount();
    expect(observerStops.every((stop) => stop.mock.calls.length > 0)).toBe(true);
  });

  it('removes the AppState listener on unmount', () => {
    const { mockClient } = makeStytchMock();
    const { unmount } = render(
      <StytchProvider stytch={mockClient}>
        <Text />
      </StytchProvider>,
    );
    unmount();
    expect(appStateRemove).toHaveBeenCalled();
  });

  it('populates user and session context when Authenticated state fires', async () => {
    const { mockClient, emitState } = makeStytchMock();
    const mockUser = new ApiUserV1User();
    const mockSession = new ApiSessionV1Session();

    const { result } = renderHook(() => ({ user: useStytchUser(), session: useStytchSession() }), {
      wrapper: ({ children }) => <StytchProvider stytch={mockClient}>{children}</StytchProvider>,
    });

    await emitState(makeAuthenticatedState(mockUser, mockSession));

    expect(result.current.user).toBe(mockUser);
    expect(result.current.session).toBe(mockSession);
  });

  it('clears user and session context when Unauthenticated state fires', async () => {
    const { mockClient, emitState } = makeStytchMock();

    const { result } = renderHook(() => ({ user: useStytchUser(), session: useStytchSession() }), {
      wrapper: ({ children }) => <StytchProvider stytch={mockClient}>{children}</StytchProvider>,
    });

    await emitState(makeAuthenticatedState());
    expect(result.current.user).toBeDefined();

    await emitState(new ConsumerAuthenticationState.Unauthenticated());
    expect(result.current.user).toBeUndefined();
    expect(result.current.session).toBeUndefined();
  });

  it('updates the authenticationState context on every state change', async () => {
    const { mockClient, emitState } = makeStytchMock();
    const authenticatedState = makeAuthenticatedState();

    const { result } = renderHook(() => useStytchAuthenticationState(), {
      wrapper: ({ children }) => <StytchProvider stytch={mockClient}>{children}</StytchProvider>,
    });

    expect(result.current).toBeInstanceOf(ConsumerAuthenticationState.Loading);
    await emitState(authenticatedState);
    expect(result.current).toBe(authenticatedState);
  });

  it('calls session.authenticate when app becomes active and session is Authenticated', async () => {
    const { mockClient, emitState } = makeStytchMock();

    render(
      <StytchProvider stytch={mockClient}>
        <Text />
      </StytchProvider>,
    );

    await act(async () => {
      capturedAppStateHandler?.('active');
    });

    await emitState(makeAuthenticatedState());

    expect(mockClient.session.authenticate).toHaveBeenCalledWith({ sessionDurationMinutes: null });
  });

  it('does not crash when session.authenticate rejects', async () => {
    const { mockClient, emitState } = makeStytchMock();
    mockClient.session.authenticate.mockRejectedValueOnce(new Error('network error'));

    render(
      <StytchProvider stytch={mockClient}>
        <Text />
      </StytchProvider>,
    );

    await act(async () => {
      capturedAppStateHandler?.('active');
    });

    // Trigger the one-shot observer — the rejection should be swallowed
    await expect(emitState(makeAuthenticatedState())).resolves.not.toThrow();
  });

  it('does not call session.authenticate when app becomes active but state is not Authenticated', async () => {
    const { mockClient, emitState } = makeStytchMock();

    render(
      <StytchProvider stytch={mockClient}>
        <Text />
      </StytchProvider>,
    );

    await act(async () => {
      capturedAppStateHandler?.('active');
    });

    await emitState(new ConsumerAuthenticationState.Loading());

    expect(mockClient.session.authenticate).not.toHaveBeenCalled();
  });
});

// ---------------------------------------------------------------------------
// HOC tests
// ---------------------------------------------------------------------------

// The HOCs are thin wrappers that inject the context value as a prop. We test
// them by verifying the underlying hook returns the expected value inside the
// provider — equivalent to testing the prop the HOC would inject.

describe('withStytch', () => {
  it('makes the stytch client available via useStytch inside the provider', () => {
    const { mockClient } = makeStytchMock();
    const { result } = renderHook(() => useStytch(), {
      wrapper: ({ children }) => <StytchProvider stytch={mockClient}>{children}</StytchProvider>,
    });
    expect(result.current).toBe(mockClient);
  });
});

describe('withStytchUser', () => {
  it('returns undefined when no user is in context', () => {
    const { mockClient } = makeStytchMock();
    const { result } = renderHook(() => useStytchUser(), {
      wrapper: ({ children }) => <StytchProvider stytch={mockClient}>{children}</StytchProvider>,
    });
    expect(result.current).toBeUndefined();
  });

  it('reflects the user once Authenticated state fires', async () => {
    const { mockClient, emitState } = makeStytchMock();
    const mockUser = new ApiUserV1User();
    const { result } = renderHook(() => useStytchUser(), {
      wrapper: ({ children }) => <StytchProvider stytch={mockClient}>{children}</StytchProvider>,
    });
    await emitState(makeAuthenticatedState(mockUser));
    expect(result.current).toBe(mockUser);
  });
});

describe('withStytchSession', () => {
  it('returns undefined when no session is in context', () => {
    const { mockClient } = makeStytchMock();
    const { result } = renderHook(() => useStytchSession(), {
      wrapper: ({ children }) => <StytchProvider stytch={mockClient}>{children}</StytchProvider>,
    });
    expect(result.current).toBeUndefined();
  });
});
