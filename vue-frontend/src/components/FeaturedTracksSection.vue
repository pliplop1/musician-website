<script setup>
import { ref, onMounted } from 'vue'

const tracks = ref([])
const loading = ref(true)
const error = ref(null)

const fetchTracks = async () => {
  try {
    const response = await fetch('/api/public/featured/tracks')
    if (!response.ok) {
      throw new Error('Erreur lors du chargement des morceaux')
    }
    tracks.value = await response.json()
  } catch (err) {
    error.value = err.message
    console.error('Error fetching tracks:', err)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchTracks()
})
</script>

<template>
  <section class="tracks-section" id="featured-tracks">
    <div class="container">
      <h2 class="section-title">
        <i class="fas fa-headphones-alt"></i> Notre Musique
      </h2>
      <p class="section-subtitle">Découvrez notre univers musical</p>

      <div v-if="loading" class="loading">
        <div class="spinner"></div>
        <p>Chargement des morceaux...</p>
      </div>

      <div v-else-if="error" class="error-message">
        <i class="fas fa-exclamation-triangle"></i>
        {{ error }}
      </div>

      <div v-else-if="tracks.length > 0" class="tracks-grid">
        <div
          v-for="track in tracks"
          :key="track.id"
          class="track-card">
          <div class="track-card-header">
            <div class="track-icon">
              <i class="fas fa-music"></i>
            </div>
            <div class="track-info">
              <h3>{{ track.title }}</h3>
              <span class="track-type-badge">
                <i :class="track.spotifyUrl ? 'fab fa-spotify' : 'fas fa-file-audio'"></i>
                {{ track.spotifyUrl ? 'Spotify' : 'Audio Local' }}
              </span>
            </div>
          </div>

          <div class="track-player">
            <!-- Spotify Embed -->
            <div
              v-if="track.spotifyUrl"
              v-html="track.spotifyUrl"
              class="spotify-container">
            </div>

            <!-- Fichier audio uploadé -->
            <div v-else-if="track.audioUrl" class="audio-player-wrapper">
              <audio controls class="audio-player">
                <source :src="track.audioUrl" type="audio/mpeg">
                Votre navigateur ne supporte pas la lecture audio.
              </audio>
              <div class="audio-visualizer">
                <div class="wave-bar" v-for="i in 20" :key="i"></div>
              </div>
            </div>

            <!-- Fallback si pas de source audio -->
            <div v-else class="audio-unavailable">
              <i class="fas fa-music"></i>
              <p>Audio temporairement indisponible</p>
            </div>
          </div>
        </div>
      </div>

      <div v-else class="no-tracks-message">
        <i class="fas fa-music"></i>
        <p>Aucun morceau en vedette pour le moment</p>
      </div>

      <!-- Bouton pour voir toutes les musiques -->
      <div class="view-all-container">
        <a href="http://localhost:8106/musique" class="btn-view-all">
          <i class="fas fa-compact-disc"></i>
          Découvrir toute notre musique
        </a>
      </div>
    </div>
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
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
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
}

.track-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 12px 40px rgba(34, 197, 94, 0.3);
  border-color: rgba(34, 197, 94, 0.3);
}

.track-card-header {
  padding: 1.5rem;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  display: flex;
  align-items: center;
  gap: 1rem;
}

.track-icon {
  width: 50px;
  height: 50px;
  background: linear-gradient(135deg, #22c55e 0%, #16a34a 100%);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.5rem;
  color: white;
  flex-shrink: 0;
}

.track-info {
  flex: 1;
  min-width: 0;
}

.track-info h3 {
  font-size: 1.25rem;
  margin: 0 0 0.5rem 0;
  color: #fff;
  font-weight: 600;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.track-type-badge {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.4rem 0.8rem;
  background: rgba(34, 197, 94, 0.15);
  border: 1px solid rgba(34, 197, 94, 0.3);
  border-radius: 20px;
  color: #4ade80;
  font-size: 0.85rem;
}

.track-player {
  position: relative;
  background: #000;
}

/* Spotify container */
.spotify-container {
  position: relative;
  width: 100%;
  min-height: 152px;
}

.spotify-container :deep(iframe) {
  width: 100%;
  border: none;
  border-radius: 0;
}

/* Audio player local */
.audio-player-wrapper {
  padding: 2rem;
  position: relative;
}

.audio-player {
  width: 100%;
  height: 40px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.05);
}

.audio-player::-webkit-media-controls-panel {
  background: linear-gradient(135deg, rgba(34, 197, 94, 0.2) 0%, rgba(22, 163, 74, 0.2) 100%);
}

.audio-visualizer {
  display: flex;
  justify-content: center;
  align-items: flex-end;
  gap: 3px;
  height: 60px;
  margin-top: 1.5rem;
}

.wave-bar {
  width: 4px;
  background: linear-gradient(180deg, #22c55e 0%, #16a34a 100%);
  border-radius: 2px;
  animation: wave 1.2s ease-in-out infinite;
  opacity: 0.6;
}

.wave-bar:nth-child(odd) {
  animation-delay: 0.1s;
}

.wave-bar:nth-child(even) {
  animation-delay: 0.2s;
}

@keyframes wave {
  0%, 100% {
    height: 10px;
  }
  50% {
    height: 40px;
  }
}

.audio-unavailable {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 4rem 2rem;
  color: #6b7280;
}

.audio-unavailable i {
  font-size: 3rem;
  margin-bottom: 1rem;
  opacity: 0.5;
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

/* No tracks message */
.no-tracks-message {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 4rem 2rem;
  color: #9ca3af;
}

.no-tracks-message i {
  font-size: 4rem;
  margin-bottom: 1.5rem;
  opacity: 0.4;
  color: #22c55e;
}

.no-tracks-message p {
  font-size: 1.2rem;
}

/* View All Button */
.view-all-container {
  display: flex;
  justify-content: center;
  margin-top: 3rem;
  padding-top: 3rem;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.btn-view-all {
  display: inline-flex;
  align-items: center;
  gap: 0.75rem;
  padding: 1rem 2.5rem;
  background: linear-gradient(135deg, #22c55e 0%, #16a34a 100%);
  color: #fff;
  text-decoration: none;
  border-radius: 30px;
  font-size: 1.1rem;
  font-weight: 600;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(34, 197, 94, 0.3);
}

.btn-view-all:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 25px rgba(34, 197, 94, 0.5);
  background: linear-gradient(135deg, #16a34a 0%, #15803d 100%);
}

.btn-view-all i {
  font-size: 1.3rem;
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

  .track-card-header {
    padding: 1rem;
  }

  .track-icon {
    width: 40px;
    height: 40px;
    font-size: 1.25rem;
  }

  .track-info h3 {
    font-size: 1.1rem;
  }

  .track-type-badge {
    font-size: 0.75rem;
    padding: 0.3rem 0.6rem;
  }

  .audio-player-wrapper {
    padding: 1.5rem;
  }

  .btn-view-all {
    padding: 0.875rem 2rem;
    font-size: 1rem;
  }

  .no-tracks-message i {
    font-size: 3rem;
  }

  .no-tracks-message p {
    font-size: 1rem;
  }
}
</style>
