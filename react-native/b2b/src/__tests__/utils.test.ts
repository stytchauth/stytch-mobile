import { describe, it, expect } from '@jest/globals';
import { mergeWithStableProps } from '../utils';

describe('mergeWithStableProps', () => {
  it('returns the same reference when objects are referentially equal', () => {
    const value = { a: 1, b: 'hello' } as Record<string, unknown>;
    expect(mergeWithStableProps(value, value)).toBe(value);
  });

  it('preserves old references for deeply equal values', () => {
    const nested = { x: 1 };
    const old = { a: nested, b: 'hello' } as Record<string, unknown>;
    const next = { a: { x: 1 }, b: 'world' } as Record<string, unknown>;
    const result = mergeWithStableProps(old, next);
    expect(result['a']).toBe(nested);
    expect(result['b']).toBe('world');
  });

  it('uses the new value when a property has changed', () => {
    const old = { count: 1 } as Record<string, unknown>;
    const next = { count: 2 } as Record<string, unknown>;
    const result = mergeWithStableProps(old, next);
    expect(result['count']).toBe(2);
  });

  it('preserves null values', () => {
    const old = { a: null } as Record<string, unknown>;
    const next = { a: null } as Record<string, unknown>;
    const result = mergeWithStableProps(old, next);
    expect(result['a']).toBeNull();
  });

  it('includes keys present only in newValue', () => {
    const old = { a: 1 } as Record<string, unknown>;
    const next = { a: 1, b: 2 } as Record<string, unknown>;
    const result = mergeWithStableProps(old, next);
    expect(result['b']).toBe(2);
  });
});
