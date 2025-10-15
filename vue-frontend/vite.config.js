import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue()],
  test: {
    environment: 'jsdom',
    setupFiles: './vitest.setup.js',
    include: [
      'src/**/*.{test,spec}.{js,ts,jsx,tsx}'
    ]
  },
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8106',
        changeOrigin: true,
        secure: false
      },
      '/uploaded-photos': {
        target: 'http://localhost:8106',
        changeOrigin: true,
        secure: false
      },
      '/uploaded-music': {
        target: 'http://localhost:8106',
        changeOrigin: true,
        secure: false
      },
      '/uploaded-avatars': {
        target: 'http://localhost:8106',
        changeOrigin: true,
        secure: false
      },
      '/uploaded-videos': {
        target: 'http://localhost:8106',
        changeOrigin: true,
        secure: false
      }
    }
  }
})
