import { ref } from 'vue'

/**
 * Composable pour gérer la logique du lecteur média (modal, détection format, liens externes)
 *
 * @returns {object} - Fonctions et états pour le lecteur
 */
export function useMediaPlayer() {
  const selectedItem = ref(null)
  const showModal = ref(false)

  /**
   * Détecter si c'est du HTML complet ou une URL simple
   */
  const isHtmlEmbed = (embedCode) => {
    if (!embedCode) return false
    return embedCode.trim().startsWith('<iframe') || embedCode.trim().startsWith('<')
  }

  /**
   * Déterminer le type de lecteur et l'icône associée
   */
  const getPlayerType = (item) => {
    // Pour les tracks
    if (item.audioUrl) {
      return { icon: 'fas fa-file-audio', label: 'Audio Local' }
    }
    if (item.spotifyUrl) {
      if (item.spotifyUrl.includes('soundcloud.com')) {
        return { icon: 'fab fa-soundcloud', label: 'SoundCloud' }
      }
      return { icon: 'fab fa-spotify', label: 'Spotify' }
    }

    // Pour les vidéos
    if (item.videoType === 'EMBED' || item.embedCode) {
      return { icon: 'fab fa-youtube', label: 'YouTube' }
    }
    if (item.videoType === 'UPLOADED_FILE' || item.videoUrl) {
      return { icon: 'fas fa-video', label: 'Vidéo locale' }
    }

    return { icon: 'fas fa-play', label: 'Média' }
  }

  /**
   * Obtenir le lien externe pour ouvrir dans le service natif
   */
  const getExternalLink = (item) => {
    // Pour les tracks avec Spotify
    if (item.spotifyUrl) {
      // Spotify: convertir embed en lien normal
      if (item.spotifyUrl.includes('spotify.com/embed/track/')) {
        return item.spotifyUrl.replace('/embed/track/', '/track/')
      }

      // SoundCloud: extraire l'URL du track depuis l'iframe
      if (item.spotifyUrl.includes('soundcloud.com')) {
        const match = item.spotifyUrl.match(/url=([^&]+)/)
        if (match) {
          return decodeURIComponent(match[1])
        }
      }

      return item.spotifyUrl
    }

    // Pour les vidéos avec YouTube
    if (item.embedCode && item.embedCode.includes('youtube.com/embed/')) {
      const match = item.embedCode.match(/youtube\.com\/embed\/([a-zA-Z0-9_-]+)/)
      if (match) {
        return `https://www.youtube.com/watch?v=${match[1]}`
      }
    }

    return null
  }

  /**
   * Ouvrir le modal avec un item
   */
  const openModal = (item) => {
    selectedItem.value = item
    showModal.value = true
    document.body.style.overflow = 'hidden'
  }

  /**
   * Fermer le modal
   */
  const closeModal = () => {
    showModal.value = false
    selectedItem.value = null
    document.body.style.overflow = ''
  }

  return {
    selectedItem,
    showModal,
    isHtmlEmbed,
    getPlayerType,
    getExternalLink,
    openModal,
    closeModal
  }
}
