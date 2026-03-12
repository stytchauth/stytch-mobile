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
        find: /.*\/lib\/consumer-headless\.mjs$/,
        replacement: path.resolve(__dirname, 'src/__mocks__/consumer-headless.ts'),
      },
    ],
  },
});
