<script setup>
import { ref } from 'vue'
import { useFeaturedContent } from '../composables/useFeaturedContent'
import { useRotationCache } from '../composables/useRotationCache'
import axios from 'axios'

// Utilisation du composable pour la logique auto/manuel + cache 24h
const {
  items: photos,
  loading,
  error,
  loadFeaturedContent
} = useFeaturedContent({
  contentType: 'photos',
  allItemsEndpoint: '/api/public/photos',
  featuredEndpoint: '/api/public/featured/photos',
  cacheKey: 'featuredPhotos',
  autoRotationFieldName: 'autoRotationEnabledGallery'
})

// Gestion du cache pour mettre à jour après un like/unlike
const { getCachedData, setCachedData } = useRotationCache('featuredPhotos')

const selectedPhoto = ref(null)
const showModal = ref(false)
const isAuthenticated = ref(false)
const likedPhotos = ref(new Set())

// Incrémenter le compteur de vues
const incrementView = async (photo) => {
  try {
    await axios.post(`/api/photos/${photo.id}/view`)

    // Recharger le viewCount depuis le serveur
    const response = await axios.get(`/api/public/photos/${photo.id}`)

    // Forcer la réactivité Vue en trouvant l'index et mettant à jour via le tableau
    const index = photos.value.findIndex(p => p.id === photo.id)
    if (index !== -1) {
      photos.value[index] = { ...photos.value[index], viewCount: response.data.viewCount }
    }

    // Invalider TOUS les caches pour synchronisation
    localStorage.removeItem('featuredTracks')
    localStorage.removeItem('featuredVideos')
    localStorage.removeItem('featuredPhotos')
    localStorage.removeItem('featuredTracks_timestamp')
    localStorage.removeItem('featuredVideos_timestamp')
    localStorage.removeItem('featuredPhotos_timestamp')

    // Mettre à jour le cache avec le nouveau viewCount
    const cachedData = getCachedData()
    if (cachedData && Array.isArray(cachedData)) {
      const updatedCache = cachedData.map(p =>
        p.id === photo.id ? { ...p, viewCount: response.data.viewCount } : p
      )
      setCachedData(updatedCache)
    }
  } catch (error) {
    // Error handling
  }
}

// Ouvrir la photo dans un modal
const openPhoto = (photo) => {
  incrementView(photo)
  selectedPhoto.value = photo
  showModal.value = true
  document.body.style.overflow = 'hidden'
}

// Fermer le modal
const closeModal = () => {
  showModal.value = false
  selectedPhoto.value = null
  document.body.style.overflow = ''
}

// Liker/unliker une photo
const toggleLike = async (photo, event) => {
  event.stopPropagation()

  if (!isAuthenticated.value) {
    alert('Vous devez être connecté pour liker une photo')
    return
  }

  try {
    const isLiked = likedPhotos.value.has(photo.id)

    if (isLiked) {
      await axios.delete(`/api/photos/${photo.id}/like`)
      likedPhotos.value.delete(photo.id)
    } else {
      await axios.post(`/api/photos/${photo.id}/like`)
      likedPhotos.value.add(photo.id)
    }

    // Recharger les données depuis le serveur pour avoir le compteur à jour
    const response = await axios.get(`/api/public/photos/${photo.id}`)

    // Forcer la réactivité Vue en trouvant l'index et mettant à jour via le tableau
    const index = photos.value.findIndex(p => p.id === photo.id)
    if (index !== -1) {
      photos.value[index] = { ...photos.value[index], likeCount: response.data.likeCount }
    }

    // Invalider TOUS les caches pour forcer le rechargement sur toutes les pages
    localStorage.removeItem('featuredTracks')
    localStorage.removeItem('featuredVideos')
    localStorage.removeItem('featuredPhotos')
    localStorage.removeItem('featuredTracks_timestamp')
    localStorage.removeItem('featuredVideos_timestamp')
    localStorage.removeItem('featuredPhotos_timestamp')

    // Mettre à jour le cache avec le nouveau likeCount
    const cachedData = getCachedData()
    if (cachedData && Array.isArray(cachedData)) {
      const updatedCache = cachedData.map(p =>
        p.id === photo.id ? { ...p, likeCount: response.data.likeCount } : p
      )
      setCachedData(updatedCache)
    }
  } catch (error) {
    // Error handling
    if (error.response?.status === 401) {
      isAuthenticated.value = false
      alert('Session expirée, veuillez vous reconnecter')
    }
  }
}

// Vérifier l'authentification et charger les likes
const checkAuth = async () => {
  try {
    const response = await axios.get('/api/user/current')
    isAuthenticated.value = !!response.data

    if (isAuthenticated.value) {
      for (const photo of photos.value) {
        try {
          const likeStatus = await axios.get(`/api/photos/${photo.id}/like-status`)
          if (likeStatus.data.liked) {
            likedPhotos.value.add(photo.id)
          }
        } catch (err) {
          // Error handling
        }
      }
    }
  } catch {
    isAuthenticated.value = false
  }
}

// Gestion de la touche Échap pour fermer le modal
const handleKeydown = (event) => {
  if (event.key === 'Escape' && showModal.value) {
    closeModal()
  }
}

// Ajouter l'écouteur d'événement au montage
import { onMounted, onUnmounted } from 'vue'

onMounted(async () => {
  window.addEventListener('keydown', handleKeydown)
  await loadFeaturedContent()
  await checkAuth()
})

onUnmounted(() => {
  window.removeEventListener('keydown', handleKeydown)
})
</script>

<template>
  <section v-if="photos.length > 0" class="photos-section" id="featured-photos">
    <div class="container">
      <h2 class="section-title">
        <i class="fas fa-images"></i> Galerie Photos
      </h2>
      <p class="section-subtitle">Revivez nos meilleurs moments en images</p>

      <div v-if="loading" class="loading">
        <div class="spinner"></div>
        <p>Chargement des photos...</p>
      </div>

      <div v-else-if="error" class="error-message">
        <i class="fas fa-exclamation-triangle"></i>
        {{ error }}
      </div>

      <div v-else class="photos-grid" role="list" aria-label="Liste des photos à la une">
        <div
          v-for="photo in photos"
          :key="photo.id"
          class="photo-card"
          role="listitem"
          tabindex="0"
          @click="openPhoto(photo)"
          @keydown.enter="openPhoto(photo)"
          @keydown.space.prevent="openPhoto(photo)"
          :aria-label="`Voir ${photo.caption || 'photo'}, ${photo.viewCount || 0} vues, ${photo.likeCount || 0} j'aime`">
          <div class="photo-thumbnail">
            <img :src="photo.url" :alt="photo.caption || 'Photo de galerie'" loading="lazy" decoding="async" />
            <div class="hover-overlay">
              <i class="fas fa-search-plus"></i>
            </div>
          </div>
          <div class="photo-card-footer">
            <span class="view-count-badge" :title="`${photo.viewCount || 0} vues`">
              <i class="fas fa-eye"></i>
              <span>{{ photo.viewCount || 0 }}</span>
            </span>
            <button
              @click="toggleLike(photo, $event)"
              :class="['like-button', { 'liked': likedPhotos.has(photo.id) }]"
              :title="likedPhotos.has(photo.id) ? 'Retirer le like' : 'Aimer cette photo'">
              <i :class="likedPhotos.has(photo.id) ? 'fas fa-heart' : 'far fa-heart'"></i>
              <span>{{ photo.likeCount || 0 }}</span>
            </button>
          </div>
        </div>
      </div>

      <!-- Bouton pour voir toutes les photos -->
      <div class="view-all-photos">
        <a href="/galerie" class="btn-view-all">
          <i class="fas fa-images"></i>
          <span>Voir toute la galerie</span>
          <i class="fas fa-arrow-right"></i>
        </a>
      </div>
    </div>

    <!-- Modal pour afficher la photo en plein écran -->
    <Teleport to="body">
      <div
        v-if="showModal"
        class="photo-modal"
        @click.self="closeModal"
        role="dialog"
        aria-modal="true"
        aria-labelledby="modal-photo-title">
        <div class="modal-content">
          <button
            class="modal-close"
            @click="closeModal"
            aria-label="Fermer la photo (Échap)">
            <i class="fas fa-times" aria-hidden="true"></i>
          </button>
          <div class="modal-photo-wrapper">
            <h2 id="modal-photo-title" class="sr-only">
              {{ selectedPhoto?.caption || 'Photo en grand format' }}
            </h2>
            <img
              v-if="selectedPhoto"
              :src="selectedPhoto.url"
              :alt="selectedPhoto.caption || 'Photo en grand format'"
              class="modal-photo"
              decoding="async"
            />
          </div>
        </div>
      </div>
    </Teleport>
  </section>
</template>

<style scoped>
.photos-section {
  padding: 6rem 2rem;
  background: linear-gradient(135deg, #0f0f23 0%, #1a1a2e 100%);
  position: relative;
  overflow: hidden;
}

/* Effet de fond animé */
.photos-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: radial-gradient(circle at 80% 20%, rgba(168, 85, 247, 0.1) 0%, transparent 50%),
              radial-gradient(circle at 20% 80%, rgba(236, 72, 153, 0.1) 0%, transparent 50%);
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
  background: linear-gradient(135deg, #a855f7 0%, #ec4899 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  font-weight: 900;
}

.section-title i {
  margin-right: 1rem;
  color: #a855f7;
  -webkit-text-fill-color: #a855f7;
}

.section-subtitle {
  text-align: center;
  color: #9ca3af;
  font-size: 1.25rem;
  margin-bottom: 3rem;
}

.photos-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 2rem;
}

.photo-card {
  background: rgba(255, 255, 255, 0.03);
  backdrop-filter: blur(10px);
  border-radius: 15px;
  overflow: hidden;
  transition: all 0.4s ease;
  border: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
  cursor: pointer;
}

.photo-card:hover {
  transform: translateY(-10px) scale(1.05);
  box-shadow: 0 12px 40px rgba(168, 85, 247, 0.3);
  border-color: rgba(168, 85, 247, 0.3);
}

.photo-thumbnail {
  position: relative;
  width: 100%;
  aspect-ratio: 4/3;
  overflow: hidden;
  background: #000;
}

.photo-thumbnail img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.photo-card:hover .photo-thumbnail img {
  transform: scale(1.15);
}

.hover-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(168, 85, 247, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.photo-card:hover .hover-overlay {
  opacity: 1;
}

.hover-overlay i {
  font-size: 3rem;
  color: #fff;
}

.photo-card-footer {
  padding: 1rem;
  display: flex;
  gap: 0.5rem;
  justify-content: flex-end;
  align-items: center;
}

/* Badge de compteur de vues */
.view-count-badge {
  display: inline-flex;
  align-items: center;
  gap: 0.4rem;
  padding: 0.4rem 0.8rem;
  background: rgba(59, 130, 246, 0.15);
  border: 1px solid rgba(59, 130, 246, 0.3);
  border-radius: 15px;
  color: #60a5fa;
  font-size: 0.85rem;
  font-weight: 600;
  transition: all 0.3s ease;
}

.view-count-badge:hover {
  background: rgba(59, 130, 246, 0.25);
  transform: scale(1.05);
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
  background: rgba(168, 85, 247, 0.15);
  border-color: rgba(168, 85, 247, 0.3);
  color: #a855f7;
  transform: scale(1.05);
}

.like-button.liked {
  background: rgba(168, 85, 247, 0.2);
  border-color: rgba(168, 85, 247, 0.4);
  color: #a855f7;
}

.like-button.liked i {
  color: #a855f7;
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

/* Bouton Voir toute la galerie */
.view-all-photos {
  margin-top: 4rem;
  text-align: center;
  padding-top: 3rem;
  border-top: 2px solid rgba(168, 85, 247, 0.2);
}

.btn-view-all {
  display: inline-flex;
  align-items: center;
  gap: 1rem;
  padding: 1.25rem 3rem;
  background: linear-gradient(135deg, #a855f7 0%, #ec4899 100%);
  color: #fff;
  text-decoration: none;
  border-radius: 50px;
  font-size: 1.25rem;
  font-weight: 700;
  transition: all 0.4s ease;
  box-shadow: 0 8px 30px rgba(168, 85, 247, 0.4);
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
  box-shadow: 0 12px 40px rgba(168, 85, 247, 0.6);
  background: linear-gradient(135deg, #9333ea 0%, #db2777 100%);
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

/* Modal photo */
.photo-modal {
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
  max-width: 1400px;
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
  background: rgba(168, 85, 247, 0.2);
  border: 2px solid #a855f7;
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
  background: #a855f7;
  transform: rotate(90deg);
}

.modal-photo-wrapper {
  position: relative;
  width: 100%;
  background: #000;
  border-radius: 10px;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}

.modal-photo {
  max-width: 100%;
  max-height: 85vh;
  width: auto;
  height: auto;
  object-fit: contain;
  display: block;
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
  border: 4px solid rgba(168, 85, 247, 0.2);
  border-top-color: #a855f7;
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
.photo-card {
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
.photo-card:nth-child(1) {
  animation-delay: 0.1s;
}

.photo-card:nth-child(2) {
  animation-delay: 0.2s;
}

.photo-card:nth-child(3) {
  animation-delay: 0.3s;
}

/* Classe pour lecteurs d'écran uniquement (RGAA) */
.sr-only {
  position: absolute;
  width: 1px;
  height: 1px;
  padding: 0;
  margin: -1px;
  overflow: hidden;
  clip: rect(0, 0, 0, 0);
  white-space: nowrap;
  border-width: 0;
}

/* Responsive */
@media (max-width: 768px) {
  .photos-section {
    padding: 4rem 1rem;
  }

  .section-title {
    font-size: 2rem;
  }

  .section-subtitle {
    font-size: 1rem;
  }

  .photos-grid {
    grid-template-columns: 1fr;
    gap: 1.5rem;
  }

  .modal-photo {
    max-height: 70vh;
  }
}
</style>
