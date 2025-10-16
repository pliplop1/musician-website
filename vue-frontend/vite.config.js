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
  build: {
    // Code splitting for better caching
    rollupOptions: {
      output: {
        manualChunks: {
          'vendor-vue': ['vue'],
          'vendor-axios': ['axios']
        }
      }
    },
    // Generate source maps for production debugging (optional, remove if not needed)
    sourcemap: false,
    // Minify with esbuild (faster than terser, built-in)
    minify: 'esbuild',
    // Increase chunk size warning limit
    chunkSizeWarningLimit: 1000,
    // Optimize CSS
    cssCodeSplit: true,
    // Asset inlining threshold
    assetsInlineLimit: 4096 // 4kb
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
