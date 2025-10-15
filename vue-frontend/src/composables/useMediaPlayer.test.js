import { describe, it, expect, beforeEach, vi } from 'vitest'
import { useMediaPlayer } from './useMediaPlayer'

describe('useMediaPlayer', () => {
  beforeEach(() => {
    // reset body overflow between tests
    document.body.style.overflow = ''
  })

  it('detects HTML embed strings', () => {
    const { isHtmlEmbed } = useMediaPlayer()
    expect(isHtmlEmbed('<iframe src="x"></iframe>')).toBe(true)
    expect(isHtmlEmbed('<div>custom</div>')).toBe(true)
    expect(isHtmlEmbed('')).toBe(false)
    expect(isHtmlEmbed(null)).toBe(false)
    expect(isHtmlEmbed('https://example.com')).toBe(false)
  })

  it('returns correct player type for tracks and videos', () => {
    const { getPlayerType } = useMediaPlayer()

    expect(getPlayerType({ audioUrl: '/uploaded-music/a.mp3' })).toEqual({ icon: 'fas fa-file-audio', label: 'Audio Local' })
    expect(getPlayerType({ spotifyUrl: 'https://open.spotify.com/track/123' })).toEqual({ icon: 'fab fa-spotify', label: 'Spotify' })
    expect(getPlayerType({ spotifyUrl: 'https://w.soundcloud.com/player?url=https%3A%2F%2Fsoundcloud.com%2Fartist%2Ftrack' })).toEqual({ icon: 'fab fa-soundcloud', label: 'SoundCloud' })

    expect(getPlayerType({ videoType: 'EMBED', embedCode: '<iframe src="..." />' })).toEqual({ icon: 'fab fa-youtube', label: 'YouTube' })
    expect(getPlayerType({ videoType: 'UPLOADED_FILE', videoUrl: '/uploaded-videos/v.mp4' })).toEqual({ icon: 'fas fa-video', label: 'Vidéo locale' })

    expect(getPlayerType({})).toEqual({ icon: 'fas fa-play', label: 'Média' })
  })

  it('computes external links for Spotify, SoundCloud and YouTube', () => {
    const { getExternalLink } = useMediaPlayer()

    // Spotify embed -> open track
    expect(
      getExternalLink({ spotifyUrl: 'https://open.spotify.com/embed/track/abcdef' })
    ).toBe('https://open.spotify.com/track/abcdef')

    // SoundCloud iframe URL param -> extract
    const sc = 'https://w.soundcloud.com/player?url=' + encodeURIComponent('https://soundcloud.com/artist/track')
    expect(getExternalLink({ spotifyUrl: sc })).toBe('https://soundcloud.com/artist/track')

    // YouTube embed code -> watch URL
    const yt = '<iframe src="https://www.youtube.com/embed/VIDEO_ID" />'
    expect(getExternalLink({ embedCode: yt })).toBe('https://www.youtube.com/watch?v=VIDEO_ID')

    // Fallback
    expect(getExternalLink({})).toBeNull()
  })

  it('opens and closes modal while toggling body overflow', () => {
    const { openModal, closeModal, selectedItem, showModal } = useMediaPlayer()

    const item = { id: 1, title: 'Test' }
    openModal(item)
    expect(showModal.value).toBe(true)
    expect(selectedItem.value).toEqual(item)
    expect(document.body.style.overflow).toBe('hidden')

    closeModal()
    expect(showModal.value).toBe(false)
    expect(selectedItem.value).toBeNull()
    expect(document.body.style.overflow).toBe('')
  })
})

