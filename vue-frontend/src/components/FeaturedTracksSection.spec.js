import { describe, it, expect, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import { ref } from 'vue'

vi.mock('../composables/useFeaturedContent', () => ({
  useFeaturedContent: () => ({
    items: ref([{ id: 1, title: 'Track 1', likeCount: 0, viewCount: 0 }]),
    loading: ref(false),
    error: ref(null),
    loadFeaturedContent: vi.fn()
  })
}))

vi.mock('../composables/useRotationCache', () => ({
  useRotationCache: () => ({ getCachedData: () => null, setCachedData: () => {} })
}))

vi.mock('axios', () => ({ default: { post: vi.fn(), get: vi.fn(), delete: vi.fn() } }))

import FeaturedTracksSection from './FeaturedTracksSection.vue'

describe('FeaturedTracksSection', () => {
  it('renders featured tracks section when items exist', async () => {
    const wrapper = mount(FeaturedTracksSection)
    const section = wrapper.find('#music')
    expect(section.exists()).toBe(true)
  })

  it('opens track modal and increments play + like', async () => {
    const axios = (await import('axios')).default
    axios.get.mockImplementation(async (url) => {
      if (url.includes('/api/user/current')) return { data: { id: 1 } }
      if (url.includes('/api/public/tracks/')) return { data: { likeCount: 1, playCount: 1 } }
      return { data: {} }
    })
    axios.post.mockResolvedValue({})
    axios.delete.mockResolvedValue({})

    const wrapper = mount(FeaturedTracksSection)
    await wrapper.find('.track-card').trigger('click')
    expect(document.body.querySelector('.track-modal')).not.toBeNull()

    const likeBtn = wrapper.find('.like-button')
    expect(likeBtn.exists()).toBe(true)
    await likeBtn.trigger('click', { stopPropagation: () => {} })
    expect(axios.post).toHaveBeenCalled()
  })
})
