import path from 'path';
import { defineConfig } from 'vitest/config';

export default defineConfig({
  test: {
    environment: 'jsdom',
    setupFiles: ['./vitest.setup.ts'],
  },
  resolve: {
    alias: [
      {
        find: /.*\/lib\/b2b-headless\.mjs$/,
        replacement: path.resolve(__dirname, 'src/__mocks__/b2b-headless.ts'),
      },
    ],
  },
});
