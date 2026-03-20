/** @type {import('jest').Config} */
export default {
  preset: 'react-native',
  setupFilesAfterEnv: ['./jest.setup.js'],
  testMatch: ['<rootDir>/src/**/__tests__/**/*.?([mc])[jt]s?(x)'],
  transform: {
    '\\.[jt]sx?$': 'babel-jest',
  },
  transformIgnorePatterns: [
    'node_modules/(?!(react-native|@react-native|@testing-library/react-native)/)',
  ],
  moduleNameMapper: {
    'consumer-headless\\.mjs$': '<rootDir>/src/__mocks__/consumer-headless.ts',
    'b2b-headless\\.mjs$': '<rootDir>/src/__mocks__/b2b-headless.ts',
  },
};
