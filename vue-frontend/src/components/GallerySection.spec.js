import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import GallerySection from './GallerySection.vue'

describe('GallerySection', () => {
  beforeEach(() => {
    global.fetch = vi.fn(async () => ({
      ok: true,
      json: async () => ([
        { id: 1, url: '/uploaded-photos/p1.jpg', caption: 'Photo 1' }
      ])
    }))
  })

  it('renders images with lazy loading and async decoding', async () => {
    const wrapper = mount(GallerySection)
    // wait for fetch + render
    await new Promise((r) => setTimeout(r, 0))
    await new Promise((r) => setTimeout(r, 0))
    const img = wrapper.find('img')
    expect(img.exists()).toBe(true)
    expect(img.attributes('loading')).toBe('lazy')
    expect(img.attributes('decoding')).toBe('async')
  })
})
