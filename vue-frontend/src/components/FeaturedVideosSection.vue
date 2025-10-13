<script setup>
import { ref, onMounted } from 'vue'
import { useFeaturedContent } from '../composables/useFeaturedContent'
import { useRotationCache } from '../composables/useRotationCache'
import axios from 'axios'

// Utilisation du composable pour la logique auto/manuel + cache 24h
const {
  items: videos,
  loading,
  error,
  loadFeaturedContent
} = useFeaturedContent({
  contentType: 'videos',
  allItemsEndpoint: '/api/public/videos',
  featuredEndpoint: '/api/public/featured/videos',
  cacheKey: 'featuredVideos',
  autoRotationFieldName: 'autoRotationEnabledVideos'
})

// Gestion du cache pour mettre à jour après un like/unlike
const { getCachedData, setCachedData } = useRotationCache('featuredVideos')

const selectedVideo = ref(null)
const showModal = ref(false)
const isAuthenticated = ref(false)
const likedVideos = ref(new Set())

// Extraire l'ID YouTube depuis l'URL embed
const getYouTubeId = (embedCode) => {
  if (!embedCode) return null
  const match = embedCode.match(/youtube\.com\/embed\/([a-zA-Z0-9_-]+)/)
  return match ? match[1] : null
}

// Générer l'URL de la miniature YouTube
const getThumbnailUrl = (video) => {
  if (video.videoType === 'EMBED' && video.embedCode) {
    const videoId = getYouTubeId(video.embedCode)
    if (videoId) {
      return `https://img.youtube.com/vi/${videoId}/maxresdefault.jpg`
    }
  }
  return '/images/video-placeholder.jpg'
}

// Ouvrir la vidéo dans un modal
const openVideo = (video) => {
  selectedVideo.value = video
  showModal.value = true
  document.body.style.overflow = 'hidden'
}

// Fermer le modal
const closeModal = () => {
  showModal.value = false
  selectedVideo.value = null
  document.body.style.overflow = ''
}

// Liker/unliker une vidéo
const toggleLike = async (video, event) => {
  event.stopPropagation() // Empêcher l'ouverture du modal

  if (!isAuthenticated.value) {
    alert('Vous devez être connecté pour liker une vidéo')
    return
  }

  try {
    const isLiked = likedVideos.value.has(video.id)

    if (isLiked) {
      await axios.delete(`/api/videos/${video.id}/like`)
      likedVideos.value.delete(video.id)
    } else {
      await axios.post(`/api/videos/${video.id}/like`)
      likedVideos.value.add(video.id)
    }

    // Recharger les données depuis le serveur pour avoir le compteur à jour
    const response = await axios.get(`/api/public/videos/${video.id}`)
    video.likeCount = response.data.likeCount

    // Mettre à jour le cache avec le nouveau likeCount (sans supprimer le cache)
    const cachedData = getCachedData()
    if (cachedData && Array.isArray(cachedData)) {
      const updatedCache = cachedData.map(v =>
        v.id === video.id ? { ...v, likeCount: response.data.likeCount } : v
      )
      setCachedData(updatedCache)
      console.log('💾 Cache mis à jour avec le nouveau likeCount')
    }
  } catch (error) {
    console.error('Erreur lors du like:', error)
    if (error.response?.status === 401) {
      isAuthenticated.value = false
      alert('Session expirée, veuillez vous reconnecter')
    }
  }
}

// Incrémenter le compteur de vues
const incrementView = async (video) => {
  try {
    await axios.post(`/api/videos/${video.id}/view`)

    // Recharger le viewCount depuis le serveur
    const response = await axios.get(`/api/public/videos/${video.id}`)
    video.viewCount = response.data.viewCount

    // Mettre à jour le cache avec le nouveau viewCount
    const cachedData = getCachedData()
    if (cachedData && Array.isArray(cachedData)) {
      const updatedCache = cachedData.map(v =>
        v.id === video.id ? { ...v, viewCount: response.data.viewCount } : v
      )
      setCachedData(updatedCache)
      console.log('💾 Cache mis à jour avec le nouveau viewCount')
    }
  } catch (error) {
    console.error('Erreur lors de l\'incrémentation des vues:', error)
  }
}

// Vérifier l'authentification et charger les likes
const checkAuth = async () => {
  try {
    const response = await axios.get('/api/user/current')
    isAuthenticated.value = !!response.data

    // Charger les status de like pour chaque vidéo
    if (isAuthenticated.value) {
      for (const video of videos.value) {
        try {
          const likeStatus = await axios.get(`/api/videos/${video.id}/like-status`)
          if (likeStatus.data.liked) {
            likedVideos.value.add(video.id)
          }
        } catch (err) {
          console.error('Erreur chargement status like:', err)
        }
      }
    }
  } catch (error) {
    isAuthenticated.value = false
  }
}

// Ouvrir vidéo et incrémenter vues
const openVideoWithView = (video) => {
  incrementView(video)
  openVideo(video)
}

onMounted(async () => {
  await loadFeaturedContent()
  await checkAuth()
})
</script>

<template>
  <section v-if="videos.length > 0" class="videos-section" id="featured-videos">
    <div class="container">
      <h2 class="section-title">
        <i class="fas fa-film"></i> Nos Vidéos
      </h2>
      <p class="section-subtitle">Découvrez quelques-unes de nos meilleures performances</p>

      <div v-if="loading" class="loading">
        <div class="spinner"></div>
        <p>Chargement des vidéos...</p>
      </div>

      <div v-else-if="error" class="error-message">
        <i class="fas fa-exclamation-triangle"></i>
        {{ error }}
      </div>

      <div v-else class="videos-grid">
        <div
          v-for="video in videos"
          :key="video.id"
          class="video-card"
          @click="openVideoWithView(video)">
          <div class="video-thumbnail">
            <img :src="getThumbnailUrl(video)" :alt="video.title" />
            <div class="play-overlay">
              <i class="fas fa-play-circle"></i>
            </div>
            <!-- Stats overlay -->
            <div class="stats-overlay">
              <span class="stat-item">
                <i class="fas fa-eye"></i>
                {{ video.viewCount || 0 }}
              </span>
            </div>
          </div>
          <div class="video-card-footer">
            <h3>{{ video.title }}</h3>
            <div class="video-meta">
              <span class="video-type-badge">
                <i class="fas fa-video"></i>
                {{ video.videoType === 'EMBED' ? 'YouTube' : 'Vidéo locale' }}
              </span>
              <button
                @click="toggleLike(video, $event)"
                :class="['like-button', { 'liked': likedVideos.has(video.id) }]"
                :title="likedVideos.has(video.id) ? 'Retirer le like' : 'Aimer cette vidéo'">
                <i :class="likedVideos.has(video.id) ? 'fas fa-heart' : 'far fa-heart'"></i>
                <span>{{ video.likeCount || 0 }}</span>
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Bouton pour voir toutes les vidéos -->
      <div class="view-all-videos">
        <a href="http://localhost:8106/videos" class="btn-view-all">
          <i class="fas fa-film"></i>
          <span>Voir toutes nos vidéos</span>
          <i class="fas fa-arrow-right"></i>
        </a>
      </div>
    </div>

    <!-- Modal pour afficher la vidéo -->
    <Teleport to="body">
      <div v-if="showModal" class="video-modal" @click.self="closeModal">
        <div class="modal-content">
          <button class="modal-close" @click="closeModal">
            <i class="fas fa-times"></i>
          </button>
          <div class="modal-video-wrapper">
            <!-- Video EMBED (YouTube/Vimeo) -->
            <div
              v-if="selectedVideo && selectedVideo.videoType === 'EMBED' && selectedVideo.embedCode"
              v-html="selectedVideo.embedCode"
              class="embed-container">
            </div>

            <!-- Video UPLOADED -->
            <video
              v-else-if="selectedVideo && selectedVideo.videoType === 'UPLOADED_FILE' && selectedVideo.videoUrl"
              controls
              autoplay
              class="video-player">
              <source :src="selectedVideo.videoUrl" type="video/mp4">
              Votre navigateur ne supporte pas la lecture de vidéos.
            </video>
          </div>
          <div class="modal-title">
            <h2>{{ selectedVideo?.title }}</h2>
          </div>
        </div>
      </div>
    </Teleport>
  </section>
</template>

<style scoped>
.videos-section {
  padding: 6rem 2rem;
  background: linear-gradient(135deg, #0f0f23 0%, #1a1a2e 100%);
  position: relative;
  overflow: hidden;
}

/* Effet de fond animé */
.videos-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: radial-gradient(circle at 20% 50%, rgba(239, 68, 68, 0.1) 0%, transparent 50%),
              radial-gradient(circle at 80% 80%, rgba(59, 130, 246, 0.1) 0%, transparent 50%);
  pointer-events: none;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  position: relative;
  z-index: 1;
}

.section-title {
  text-align: center;
  font-size: 3rem;
  margin-bottom: 1rem;
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  font-weight: 900;
}

.section-title i {
  margin-right: 1rem;
  color: #ef4444;
  -webkit-text-fill-color: #ef4444;
}

.section-subtitle {
  text-align: center;
  color: #9ca3af;
  font-size: 1.25rem;
  margin-bottom: 3rem;
}

.videos-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 2rem;
}

.video-card {
  background: rgba(255, 255, 255, 0.03);
  backdrop-filter: blur(10px);
  border-radius: 15px;
  overflow: hidden;
  transition: all 0.4s ease;
  border: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
  cursor: pointer;
}

.video-card:hover {
  transform: translateY(-10px) scale(1.05);
  box-shadow: 0 12px 40px rgba(239, 68, 68, 0.3);
  border-color: rgba(239, 68, 68, 0.3);
}

.video-card:hover .play-overlay {
  opacity: 1;
  transform: translate(-50%, -50%) scale(1.2);
}

.video-thumbnail {
  position: relative;
  width: 100%;
  aspect-ratio: 16/9;
  overflow: hidden;
  background: #000;
}

.video-thumbnail img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.video-card:hover .video-thumbnail img {
  transform: scale(1.15);
}

.play-overlay {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 4rem;
  color: #ef4444;
  opacity: 0.8;
  transition: all 0.3s ease;
  pointer-events: none;
  text-shadow: 0 0 20px rgba(0, 0, 0, 0.5);
}

.video-card-footer {
  padding: 1.5rem;
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.video-card-footer h3 {
  font-size: 1.1rem;
  margin: 0;
  color: #fff;
  font-weight: 600;
  line-height: 1.4;
}

.video-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 0.75rem;
}

.video-type-badge {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.4rem 0.8rem;
  background: rgba(239, 68, 68, 0.15);
  border: 1px solid rgba(239, 68, 68, 0.3);
  border-radius: 15px;
  color: #f87171;
  font-size: 0.8rem;
  width: fit-content;
}

.stats-overlay {
  position: absolute;
  top: 10px;
  right: 10px;
  background: rgba(0, 0, 0, 0.75);
  backdrop-filter: blur(5px);
  padding: 0.4rem 0.8rem;
  border-radius: 20px;
  display: flex;
  gap: 0.5rem;
  z-index: 2;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 0.4rem;
  color: #fff;
  font-size: 0.85rem;
  font-weight: 600;
}

.stat-item i {
  color: #9ca3af;
  font-size: 0.9rem;
}

.like-button {
  display: inline-flex;
  align-items: center;
  gap: 0.4rem;
  padding: 0.4rem 0.8rem;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 15px;
  color: #9ca3af;
  font-size: 0.85rem;
  cursor: pointer;
  transition: all 0.3s ease;
  font-weight: 600;
}

.like-button:hover {
  background: rgba(239, 68, 68, 0.15);
  border-color: rgba(239, 68, 68, 0.3);
  color: #ef4444;
  transform: scale(1.05);
}

.like-button.liked {
  background: rgba(239, 68, 68, 0.2);
  border-color: rgba(239, 68, 68, 0.4);
  color: #ef4444;
}

.like-button.liked i {
  color: #ef4444;
  animation: heartBeat 0.5s ease;
}

@keyframes heartBeat {
  0%, 100% {
    transform: scale(1);
  }
  25% {
    transform: scale(1.3);
  }
  50% {
    transform: scale(1.1);
  }
  75% {
    transform: scale(1.25);
  }
}

/* Bouton Voir toutes les vidéos */
.view-all-videos {
  margin-top: 4rem;
  text-align: center;
  padding-top: 3rem;
  border-top: 2px solid rgba(239, 68, 68, 0.2);
}

.btn-view-all {
  display: inline-flex;
  align-items: center;
  gap: 1rem;
  padding: 1.25rem 3rem;
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
  color: #fff;
  text-decoration: none;
  border-radius: 50px;
  font-size: 1.25rem;
  font-weight: 700;
  transition: all 0.4s ease;
  box-shadow: 0 8px 30px rgba(239, 68, 68, 0.4);
  text-transform: uppercase;
  letter-spacing: 1px;
  position: relative;
  overflow: hidden;
}

.btn-view-all::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  transition: left 0.5s;
}

.btn-view-all:hover::before {
  left: 100%;
}

.btn-view-all:hover {
  transform: translateY(-5px) scale(1.05);
  box-shadow: 0 12px 40px rgba(239, 68, 68, 0.6);
  background: linear-gradient(135deg, #dc2626 0%, #b91c1c 100%);
}

.btn-view-all i:first-child {
  font-size: 1.5rem;
}

.btn-view-all i:last-child {
  font-size: 1.2rem;
  transition: transform 0.3s ease;
}

.btn-view-all:hover i:last-child {
  transform: translateX(5px);
}

/* Modal vidéo */
.video-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.95);
  z-index: 9999;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2rem;
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.modal-content {
  width: 100%;
  max-width: 1200px;
  position: relative;
  animation: slideUp 0.3s ease;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.modal-close {
  position: absolute;
  top: -3rem;
  right: 0;
  background: rgba(239, 68, 68, 0.2);
  border: 2px solid #ef4444;
  color: #fff;
  font-size: 1.5rem;
  width: 3rem;
  height: 3rem;
  border-radius: 50%;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10001;
}

.modal-close:hover {
  background: #ef4444;
  transform: rotate(90deg);
}

.modal-video-wrapper {
  position: relative;
  width: 100%;
  background: #000;
  border-radius: 10px;
  overflow: hidden;
}

.modal-video-wrapper .embed-container {
  position: relative;
  padding-bottom: 56.25%;
  height: 0;
  overflow: hidden;
}

.modal-video-wrapper .embed-container :deep(iframe) {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  border: none;
}

.modal-video-wrapper .video-player {
  width: 100%;
  display: block;
}

.modal-title {
  margin-top: 1.5rem;
  text-align: center;
}

.modal-title h2 {
  color: #fff;
  font-size: 1.5rem;
  margin: 0;
}

/* Loading */
.loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 4rem 2rem;
  color: #9ca3af;
}

.spinner {
  width: 50px;
  height: 50px;
  border: 4px solid rgba(239, 68, 68, 0.2);
  border-top-color: #ef4444;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 1rem;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* Error */
.error-message {
  text-align: center;
  padding: 2rem;
  background: rgba(239, 68, 68, 0.1);
  border: 1px solid rgba(239, 68, 68, 0.3);
  border-radius: 10px;
  color: #f87171;
}

.error-message i {
  font-size: 2rem;
  margin-bottom: 1rem;
  display: block;
}

/* Animations */
.video-card {
  animation: fadeInUp 0.6s ease-out;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* Animation en cascade pour les cartes */
.video-card:nth-child(1) {
  animation-delay: 0.1s;
}

.video-card:nth-child(2) {
  animation-delay: 0.2s;
}

.video-card:nth-child(3) {
  animation-delay: 0.3s;
}

/* Responsive */
@media (max-width: 768px) {
  .videos-section {
    padding: 4rem 1rem;
  }

  .section-title {
    font-size: 2rem;
  }

  .section-subtitle {
    font-size: 1rem;
  }

  .videos-grid {
    grid-template-columns: 1fr;
    gap: 1.5rem;
  }

  .video-card-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .video-type-badge {
    font-size: 0.75rem;
    padding: 0.4rem 0.8rem;
  }
}
</style>
