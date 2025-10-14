import { describe, it, expect, vi, beforeEach } from 'vitest'
import { useFeaturedContent } from './useFeaturedContent'

// Helper to mock fetch with routes
const mockFetch = (handlers) => {
  global.fetch = vi.fn(async (url) => {
    const handler = handlers.find((h) => (typeof h.match === 'string' ? url.includes(h.match) : h.match.test(url)))
    if (!handler) return { ok: true, json: async () => ({}) }
    const res = typeof handler.reply === 'function' ? handler.reply(url) : handler.reply
    if (res && res.status && res.body !== undefined) {
      return { ok: res.status >= 200 && res.status < 300, status: res.status, json: async () => res.body }
    }
    return { ok: true, json: async () => res }
  })
}

describe('useFeaturedContent', () => {
  beforeEach(() => {
    vi.restoreAllMocks()
    localStorage.clear()
  })

  it('selectRandomItems returns array of requested size or all if smaller', () => {
    const { selectRandomItems } = useFeaturedContent({
      contentType: 'photos',
      allItemsEndpoint: '/api/public/photos',
      featuredEndpoint: '/api/public/featured/photos',
      cacheKey: 'featuredPhotos',
      autoRotationFieldName: 'autoRotationEnabledGallery'
    })
    expect(selectRandomItems([], 3)).toEqual([])
    expect(selectRandomItems([{ id: 1 }], 3).length).toBe(1)
    expect(selectRandomItems([{ id: 1 }, { id: 2 }, { id: 3 }], 3).length).toBe(3)
  })

  it('loads manual featured items when auto-rotation disabled', async () => {
    mockFetch([
      { match: /homepage-settings/, reply: { autoRotationEnabledGallery: false } },
      { match: /featured\/photos/, reply: [{ id: 1 }, { id: 2 }, { id: 3 }] }
    ])

    const { items, loading, error, loadFeaturedContent, isAutoRotationMode } = useFeaturedContent({
      contentType: 'photos',
      allItemsEndpoint: '/api/public/photos',
      featuredEndpoint: '/api/public/featured/photos',
      cacheKey: 'featuredPhotos',
      autoRotationFieldName: 'autoRotationEnabledGallery'
    })

    await loadFeaturedContent()
    expect(isAutoRotationMode.value).toBe(false)
    expect(items.value.length).toBe(3)
    expect(loading.value).toBe(false)
    expect(error.value).toBeNull()
  })

  it('loads random items with cache when auto-rotation enabled', async () => {
    mockFetch([
      { match: /homepage-settings/, reply: { autoRotationEnabledGallery: true } },
      { match: /public\/photos$/, reply: [{ id: 1 }, { id: 2 }, { id: 3 }, { id: 4 }] }
    ])

    const { items, loadFeaturedContent, isAutoRotationMode } = useFeaturedContent({
      contentType: 'photos',
      allItemsEndpoint: '/api/public/photos',
      featuredEndpoint: '/api/public/featured/photos',
      cacheKey: 'featuredPhotos',
      autoRotationFieldName: 'autoRotationEnabledGallery'
    })

    await loadFeaturedContent()
    expect(isAutoRotationMode.value).toBe(true)
    expect(items.value.length).toBe(3)
    // After first load, cache should be set
    const cached = JSON.parse(localStorage.getItem('featuredPhotos_data'))
    expect(Array.isArray(cached)).toBe(true)
  })
})

