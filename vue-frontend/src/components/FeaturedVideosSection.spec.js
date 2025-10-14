import { describe, it, expect, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import { ref } from 'vue'

vi.mock('../composables/useFeaturedContent', () => ({
  useFeaturedContent: () => ({
    items: ref([{ id: 1, title: 'Video 1', likeCount: 0, viewCount: 0 }]),
    loading: ref(false),
    error: ref(null),
    loadFeaturedContent: vi.fn()
  })
}))

vi.mock('../composables/useRotationCache', () => ({
  useRotationCache: () => ({ getCachedData: () => null, setCachedData: () => {} })
}))

vi.mock('axios', () => ({ default: { post: vi.fn(), get: vi.fn(), delete: vi.fn() } }))

import FeaturedVideosSection from './FeaturedVideosSection.vue'

describe('FeaturedVideosSection', () => {
  it('renders featured videos section when items exist', async () => {
    const wrapper = mount(FeaturedVideosSection)
    const section = wrapper.find('#featured-videos')
    expect(section.exists()).toBe(true)
  })

  it('opens video modal and toggles like', async () => {
    const axios = (await import('axios')).default
    axios.get.mockImplementation(async (url) => {
      if (url.includes('/api/user/current')) return { data: { id: 1 } }
      if (url.includes('/api/public/videos/')) return { data: { likeCount: 1, viewCount: 1 } }
      return { data: {} }
    })
    axios.post.mockResolvedValue({})
    axios.delete.mockResolvedValue({})

    const wrapper = mount(FeaturedVideosSection)
    await wrapper.find('.video-card').trigger('click')
    expect(document.body.querySelector('.video-modal')).not.toBeNull()

    const likeBtn = wrapper.find('.like-button')
    expect(likeBtn.exists()).toBe(true)
    await likeBtn.trigger('click', { stopPropagation: () => {} })
    expect(axios.post).toHaveBeenCalled()
  })
})
