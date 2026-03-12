// eslint-disable-next-line @typescript-eslint/no-explicit-any
const deepEqual = (a: any, b: any): boolean => {
  // Ensures type is the same
  if (typeof a !== typeof b) return false;
  // arrays, null, and objects all have type 'object'
  if (a === null || b === null) return a === b;
  if (typeof a === 'object') {
    if (Object.keys(a).length !== Object.keys(b).length || Object.keys(a).some((k) => !(k in b)))
      return false;
    return Object.entries(a).every(([k, v]) => deepEqual(v, b[k]));
  }
  // boolean, string, number, undefined
  return a === b;
};

export const mergeWithStableProps = <
  T extends Record<string, unknown>,
  U extends Record<string, unknown> = T,
>(
  oldValue: U,
  newValue: T
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
    { ...newValue }
  );
};
