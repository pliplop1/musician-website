// Basic test setup for jsdom
import { afterEach, vi } from 'vitest'

// Ensure minimal cleanup between tests
afterEach(() => {
  document.body.innerHTML = ''
  vi.restoreAllMocks()
})

// Mock fetch by default; tests can override
if (!globalThis.fetch) {
  globalThis.fetch = async () => ({ ok: true, json: async () => ({}) })
}
