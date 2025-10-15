import { describe, it, expect, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import { nextTick } from 'vue'
import HeroSection from './HeroSection.vue'

describe('HeroSection', () => {
  it('renders hero title and tagline from API', async () => {
    const mockSettings = {
      heroTitle: 'DUO BW',
      heroSubtitle: 'Un son élégant',
      heroBackgroundVideoUrl: '/uploaded-videos/bg.mp4',
      welcomeMessage: 'Bienvenue sur le site officiel'
    }

    const originalFetch = globalThis.fetch
    globalThis.fetch = vi.fn(async () => ({ ok: true, json: async () => mockSettings }))

    try {
      const wrapper = mount(HeroSection)

      // wait for onMounted -> fetch -> state update -> render
      await new Promise((r) => setTimeout(r, 0))
      await nextTick()

      const title = wrapper.find('.hero-title')
      const tagline = wrapper.find('.hero-tagline')

      expect(title.exists()).toBe(true)
      expect(tagline.exists()).toBe(true)
      expect(title.text()).toBe('DUO BW')
      expect(tagline.text()).toBe('Un son élégant')

      // background video is rendered when URL provided
      expect(wrapper.find('video.hero-video').exists()).toBe(true)
    } finally {
      globalThis.fetch = originalFetch
    }
  })
})
