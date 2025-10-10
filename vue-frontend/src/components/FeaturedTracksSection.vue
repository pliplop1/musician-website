<script setup>
import { ref, onMounted, computed } from 'vue'

const tracks = ref([])
const loading = ref(true)
const error = ref(null)
const selectedTrack = ref(null)
const showModal = ref(false)

const fetchTracks = async () => {
  try {
    const CACHE_KEY = 'featuredTracks_v2'
    const CACHE_TIMESTAMP_KEY = 'featuredTracksTimestamp_v2'
    const TWENTY_FOUR_HOURS = 24 * 60 * 60 * 1000 // 24 heures en millisecondes

    // Vérifier le cache localStorage
    const cachedTracks = localStorage.getItem(CACHE_KEY)
    const cachedTimestamp = localStorage.getItem(CACHE_TIMESTAMP_KEY)

    if (cachedTracks && cachedTimestamp) {
      const now = Date.now()
      const timeDiff = now - parseInt(cachedTimestamp)

      // Si moins de 24h se sont écoulées, utiliser le cache
      if (timeDiff < TWENTY_FOUR_HOURS) {
        console.log('Utilisation des morceaux en cache (valides pour encore', Math.round((TWENTY_FOUR_HOURS - timeDiff) / (60 * 60 * 1000)), 'heures)')
        tracks.value = JSON.parse(cachedTracks)
        loading.value = false
        return
      }
    }

    // Sinon, récupérer de nouveaux morceaux depuis l'API
    console.log('Récupération de nouveaux morceaux aléatoires depuis l\'API')
    const response = await fetch('/api/public/tracks')
    if (!response.ok) {
      throw new Error('Erreur lors du chargement des morceaux')
    }
    const allTracks = await response.json()

    // Sélectionner 3 morceaux aléatoires
    const shuffled = [...allTracks].sort(() => 0.5 - Math.random())
    const selectedTracks = shuffled.slice(0, 3)

    // Stocker dans le cache avec le timestamp actuel
    localStorage.setItem(CACHE_KEY, JSON.stringify(selectedTracks))
    localStorage.setItem(CACHE_TIMESTAMP_KEY, Date.now().toString())

    tracks.value = selectedTracks
  } catch (err) {
    error.value = err.message
    console.error('Error fetching tracks:', err)
  } finally {
    loading.value = false
  }
}

// Détecter si c'est du HTML ou une URL
const isHtmlEmbed = (embedCode) => {
  if (!embedCode) return false
  return embedCode.trim().startsWith('<iframe') || embedCode.trim().startsWith('<')
}

// Extraire l'ID Spotify depuis l'URL embed
const getSpotifyId = (embedCode) => {
  if (!embedCode) return null
  const match = embedCode.match(/spotify\.com\/embed\/track\/([a-zA-Z0-9]+)/)
  return match ? match[1] : null
}

// Déterminer le type de lecteur
const getPlayerType = (track) => {
  if (track.audioUrl) return { icon: 'fas fa-file-audio', label: 'Audio Local' }
  if (track.spotifyUrl) {
    if (track.spotifyUrl.includes('soundcloud.com')) {
      return { icon: 'fab fa-soundcloud', label: 'SoundCloud' }
    }
    return { icon: 'fab fa-spotify', label: 'Spotify' }
  }
  return { icon: 'fas fa-music', label: 'Audio' }
}

// Obtenir le lien externe pour ouvrir dans le service
const getExternalLink = (track) => {
  if (!track.spotifyUrl) return null

  // Spotify: convertir embed en lien normal
  if (track.spotifyUrl.includes('spotify.com/embed/track/')) {
    return track.spotifyUrl.replace('/embed/track/', '/track/')
  }

  // SoundCloud: extraire l'URL du track depuis l'iframe
  if (track.spotifyUrl.includes('soundcloud.com')) {
    const match = track.spotifyUrl.match(/url=([^&]+)/)
    if (match) {
      return decodeURIComponent(match[1])
    }
  }

  // Autres cas: retourner l'URL telle quelle
  return track.spotifyUrl
}

// Générer l'URL de la miniature (utiliser une image par défaut musicale)
const getThumbnailUrl = (track) => {
  // Pour Spotify, on utilise une vignette musicale stylisée
  return '/images/music-placeholder.jpg'
}

// Ouvrir le morceau dans un modal
const openTrack = (track) => {
  console.log('=== DEBUG openTrack ===')
  console.log('Track data:', track)
  console.log('Has spotifyUrl?', !!track.spotifyUrl)
  console.log('spotifyUrl value:', track.spotifyUrl)
  console.log('Has audioUrl?', !!track.audioUrl)
  console.log('audioUrl value:', track.audioUrl)
  console.log('======================')

  selectedTrack.value = track
  showModal.value = true
  // Empêcher le scroll du body
  document.body.style.overflow = 'hidden'
}

// Fermer le modal
const closeModal = () => {
  showModal.value = false
  selectedTrack.value = null
  // Réactiver le scroll du body
  document.body.style.overflow = ''
}

onMounted(() => {
  fetchTracks()
})
</script>

<template>
  <section v-if="tracks.length > 0" class="tracks-section" id="music">
    <div class="container">
      <h2 class="section-title">
        <i class="fas fa-music"></i> Notre Musique
      </h2>
      <p class="section-subtitle">Découvrez quelques-uns de nos meilleurs morceaux</p>

      <div v-if="loading" class="loading">
        <div class="spinner"></div>
        <p>Chargement des morceaux...</p>
      </div>

      <div v-else-if="error" class="error-message">
        <i class="fas fa-exclamation-triangle"></i>
        {{ error }}
      </div>

      <div v-else class="tracks-grid">
        <div
          v-for="track in tracks"
          :key="track.id"
          class="track-card"
          @click="openTrack(track)">
          <div class="track-thumbnail">
            <div class="music-icon-overlay">
              <i class="fas fa-music"></i>
            </div>
            <div class="play-overlay">
              <i class="fas fa-play-circle"></i>
            </div>
          </div>
          <div class="track-card-footer">
            <h3>{{ track.title }}</h3>
            <span class="track-type-badge">
              <i :class="getPlayerType(track).icon"></i>
              {{ getPlayerType(track).label }}
            </span>
          </div>
        </div>
      </div>

      <!-- Bouton pour voir toutes les musiques -->
      <div class="view-all-tracks">
        <a href="http://localhost:8106/musique" class="btn-view-all">
          <i class="fas fa-compact-disc"></i>
          <span>Voir toute notre musique</span>
          <i class="fas fa-arrow-right"></i>
        </a>
      </div>
    </div>

    <!-- Modal pour afficher le lecteur -->
    <Teleport to="body">
      <div v-if="showModal" class="track-modal" @click.self="closeModal">
        <div class="modal-content">
          <button class="modal-close" @click="closeModal">
            <i class="fas fa-times"></i>
          </button>
          <div class="modal-player-wrapper">
            <!-- Embed avec HTML complet (SoundCloud, etc.) -->
            <div
              v-if="selectedTrack && selectedTrack.spotifyUrl && isHtmlEmbed(selectedTrack.spotifyUrl)"
              v-html="selectedTrack.spotifyUrl"
              class="embed-html-container">
            </div>

            <!-- Embed avec URL simple (Spotify) -->
            <div
              v-else-if="selectedTrack && selectedTrack.spotifyUrl"
              class="spotify-container">
              <iframe
                :src="selectedTrack.spotifyUrl"
                width="100%"
                height="352"
                frameborder="0"
                allowtransparency="true"
                allow="encrypted-media">
              </iframe>
            </div>

            <!-- Fichier audio uploadé -->
            <audio
              v-else-if="selectedTrack && selectedTrack.audioUrl"
              controls
              autoplay
              class="audio-player">
              <source :src="selectedTrack.audioUrl" type="audio/mpeg">
              Votre navigateur ne supporte pas la lecture audio.
            </audio>
          </div>
          <div class="modal-title">
            <h2>{{ selectedTrack?.title }}</h2>
          </div>

          <!-- Bouton pour ouvrir dans le service -->
          <div v-if="selectedTrack && getExternalLink(selectedTrack)" class="modal-actions">
            <a
              :href="getExternalLink(selectedTrack)"
              target="_blank"
              rel="noopener noreferrer"
              class="btn-open-external">
              <i :class="getPlayerType(selectedTrack).icon"></i>
              <span>Ouvrir dans {{ getPlayerType(selectedTrack).label }}</span>
              <i class="fas fa-external-link-alt"></i>
            </a>
            <p class="help-text">Pour contrôler le volume et la navigation complète</p>
          </div>
        </div>
      </div>
    </Teleport>
  </section>
</template>

<style scoped>
.tracks-section {
  padding: 6rem 2rem;
  background: linear-gradient(135deg, #1a1a2e 0%, #0f0f23 100%);
  position: relative;
  overflow: hidden;
}

/* Effet de fond animé */
.tracks-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: radial-gradient(circle at 80% 20%, rgba(34, 197, 94, 0.1) 0%, transparent 50%),
              radial-gradient(circle at 20% 80%, rgba(168, 85, 247, 0.1) 0%, transparent 50%);
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
  background: linear-gradient(135deg, #22c55e 0%, #16a34a 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  font-weight: 900;
}

.section-title i {
  margin-right: 1rem;
  color: #22c55e;
  -webkit-text-fill-color: #22c55e;
}

.section-subtitle {
  text-align: center;
  color: #9ca3af;
  font-size: 1.25rem;
  margin-bottom: 3rem;
}

.tracks-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 2rem;
}

.track-card {
  background: rgba(255, 255, 255, 0.03);
  backdrop-filter: blur(10px);
  border-radius: 15px;
  overflow: hidden;
  transition: all 0.4s ease;
  border: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
  cursor: pointer;
}

.track-card:hover {
  transform: translateY(-10px) scale(1.05);
  box-shadow: 0 12px 40px rgba(34, 197, 94, 0.3);
  border-color: rgba(34, 197, 94, 0.3);
}

.track-card:hover .play-overlay {
  opacity: 1;
  transform: translate(-50%, -50%) scale(1.2);
}

.track-thumbnail {
  position: relative;
  width: 100%;
  aspect-ratio: 16/9;
  overflow: hidden;
  background: linear-gradient(135deg, #1a1a2e 0%, #22c55e 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.music-icon-overlay {
  font-size: 5rem;
  color: rgba(255, 255, 255, 0.1);
  position: absolute;
  z-index: 1;
}

.play-overlay {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 4rem;
  color: #22c55e;
  opacity: 0.8;
  transition: all 0.3s ease;
  pointer-events: none;
  text-shadow: 0 0 20px rgba(0, 0, 0, 0.5);
  z-index: 2;
}

.track-card-footer {
  padding: 1.5rem;
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.track-card-footer h3 {
  font-size: 1.1rem;
  margin: 0;
  color: #fff;
  font-weight: 600;
  line-height: 1.4;
}

.track-type-badge {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.4rem 0.8rem;
  background: rgba(34, 197, 94, 0.15);
  border: 1px solid rgba(34, 197, 94, 0.3);
  border-radius: 15px;
  color: #4ade80;
  font-size: 0.8rem;
  width: fit-content;
}

/* Bouton Voir toute la musique */
.view-all-tracks {
  margin-top: 4rem;
  text-align: center;
  padding-top: 3rem;
  border-top: 2px solid rgba(34, 197, 94, 0.2);
}

.btn-view-all {
  display: inline-flex;
  align-items: center;
  gap: 1rem;
  padding: 1.25rem 3rem;
  background: linear-gradient(135deg, #22c55e 0%, #16a34a 100%);
  color: #fff;
  text-decoration: none;
  border-radius: 50px;
  font-size: 1.25rem;
  font-weight: 700;
  transition: all 0.4s ease;
  box-shadow: 0 8px 30px rgba(34, 197, 94, 0.4);
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
  box-shadow: 0 12px 40px rgba(34, 197, 94, 0.6);
  background: linear-gradient(135deg, #16a34a 0%, #15803d 100%);
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

/* Modal lecteur */
.track-modal {
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
  max-width: 800px;
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
  background: rgba(34, 197, 94, 0.2);
  border: 2px solid #22c55e;
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
  background: #22c55e;
  transform: rotate(90deg);
}

.modal-player-wrapper {
  position: relative;
  width: 100%;
  background: #000;
  border-radius: 10px;
  overflow: hidden;
}

.modal-player-wrapper .embed-html-container {
  width: 100%;
  min-height: 166px;
}

.modal-player-wrapper .embed-html-container :deep(iframe) {
  width: 100%;
  border: none;
  border-radius: 10px;
}

.modal-player-wrapper .spotify-container {
  width: 100%;
  min-height: 352px;
}

.modal-player-wrapper .spotify-container iframe {
  width: 100%;
  height: 352px;
  border: none;
  border-radius: 10px;
}

.modal-player-wrapper .audio-player {
  width: 100%;
  display: block;
  padding: 2rem;
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

/* Bouton ouvrir dans le service */
.modal-actions {
  margin-top: 2rem;
  text-align: center;
}

.btn-open-external {
  display: inline-flex;
  align-items: center;
  gap: 0.75rem;
  padding: 1rem 2rem;
  background: linear-gradient(135deg, #22c55e 0%, #16a34a 100%);
  color: #fff;
  text-decoration: none;
  border-radius: 50px;
  font-size: 1.1rem;
  font-weight: 600;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(34, 197, 94, 0.3);
}

.btn-open-external:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(34, 197, 94, 0.5);
  background: linear-gradient(135deg, #16a34a 0%, #15803d 100%);
}

.btn-open-external i:first-child {
  font-size: 1.3rem;
}

.btn-open-external i:last-child {
  font-size: 0.9rem;
  opacity: 0.8;
}

.help-text {
  margin-top: 0.75rem;
  color: #9ca3af;
  font-size: 0.9rem;
  font-style: italic;
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
  border: 4px solid rgba(34, 197, 94, 0.2);
  border-top-color: #22c55e;
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
.track-card {
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
.track-card:nth-child(1) {
  animation-delay: 0.1s;
}

.track-card:nth-child(2) {
  animation-delay: 0.2s;
}

.track-card:nth-child(3) {
  animation-delay: 0.3s;
}

/* Responsive */
@media (max-width: 768px) {
  .tracks-section {
    padding: 4rem 1rem;
  }

  .section-title {
    font-size: 2rem;
  }

  .section-subtitle {
    font-size: 1rem;
  }

  .tracks-grid {
    grid-template-columns: 1fr;
    gap: 1.5rem;
  }

  .track-type-badge {
    font-size: 0.75rem;
    padding: 0.4rem 0.8rem;
  }
}
</style>
