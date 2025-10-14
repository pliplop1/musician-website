import { describe, it, expect, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import { ref } from 'vue'

vi.mock('../composables/useFeaturedContent', () => ({
  useFeaturedContent: () => ({
    items: ref([{ id: 1, url: '/p1.jpg', likeCount: 0, viewCount: 0 }]),
    loading: ref(false),
    error: ref(null),
    loadFeaturedContent: vi.fn()
  })
}))

vi.mock('../composables/useRotationCache', () => ({
  useRotationCache: () => ({ getCachedData: () => null, setCachedData: () => {} })
}))

vi.mock('axios', () => ({ default: { post: vi.fn(), get: vi.fn(), delete: vi.fn() } }))

import FeaturedPhotosSection from './FeaturedPhotosSection.vue'

describe('FeaturedPhotosSection', () => {
  it('renders featured photos section when items exist', async () => {
    const wrapper = mount(FeaturedPhotosSection)
    const section = wrapper.find('#featured-photos')
    expect(section.exists()).toBe(true)
  })

  it('opens modal on photo click and toggles like', async () => {
    const axios = (await import('axios')).default
    axios.get.mockImplementation(async (url) => {
      if (url.includes('/api/user/current')) return { data: { id: 1 } }
      if (url.includes('/api/public/photos/')) return { data: { likeCount: 1, viewCount: 1 } }
      return { data: {} }
    })
    axios.post.mockResolvedValue({})
    axios.delete.mockResolvedValue({})

    const wrapper = mount(FeaturedPhotosSection)

    // open modal by clicking first card
    await wrapper.find('.photo-card').trigger('click')
    expect(document.body.querySelector('.photo-modal')).not.toBeNull()

    // click like button
    const likeBtn = wrapper.find('.like-button')
    expect(likeBtn.exists()).toBe(true)
    await likeBtn.trigger('click', { stopPropagation: () => {} })

    expect(axios.post).toHaveBeenCalled()
  })
})
