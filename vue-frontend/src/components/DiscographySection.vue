<script setup>
import { ref, onMounted } from 'vue'

const albums = ref([])
const loading = ref(true)

const fetchDiscography = async () => {
  try {
    const response = await fetch('/api/public/discography')
    albums.value = await response.json()
  } catch (err) {
    // Error handling
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchDiscography()
})
</script>

<template>
  <section class="discography-section">
    <div class="container">
      <h2 class="section-title">Discographie</h2>

      <div v-if="loading" class="loading">Chargement...</div>

      <div v-else class="albums-grid" role="list" aria-label="Albums de discographie">
        <div v-for="album in albums" :key="album.id" class="album-card" role="listitem">
          <img :src="album.coverUrl" :alt="album.title" />
          <h3>{{ album.title }}</h3>
          <p class="year">{{ album.year }}</p>
          <p class="description">{{ album.description }}</p>

          <div v-if="album.tracks && album.tracks.length" class="tracks">
            <h4>Morceaux</h4>
            <div v-for="track in album.tracks" :key="track.id" class="track-item">
              <div class="track-header">
                <span class="track-number">{{ track.trackNumber || '•' }}</span>
                <span class="track-title">{{ track.title }}</span>
                <span v-if="track.duration" class="track-duration">{{ track.duration }}</span>
              </div>

              <!-- Lecteur audio si fichier local -->
              <div v-if="track.audioUrl" class="track-audio">
                <audio controls class="audio-player">
                  <source :src="track.audioUrl" type="audio/mpeg">
                  Votre navigateur ne supporte pas la lecture audio.
                </audio>
              </div>

              <!-- Embed Spotify si disponible -->
              <div v-else-if="track.spotifyUrl" class="track-spotify" v-html="track.spotifyUrl"></div>

              <!-- Message si pas de source audio -->
              <div v-else class="track-no-audio">
                <i class="fas fa-info-circle"></i>
                <span>Audio à venir</span>
              </div>
            </div>
          </div>

          <!-- Bouton pour voir toutes les musiques -->
          <a href="/musique" class="btn-listen">
            <i class="fas fa-compact-disc"></i>
            Voir toutes les musiques
          </a>
        </div>
      </div>

      <!-- Bouton global pour voir toutes les musiques -->
      <div v-if="albums.length > 0" class="view-all-music">
        <a href="/musique" class="btn-view-all-music">
          <i class="fas fa-compact-disc"></i>
          Découvrir toute notre musique
        </a>
      </div>
    </div>
  </section>
</template>

<style scoped>
.discography-section {
  padding: 5rem 2rem;
  background: #0a0a0a;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
}

.section-title {
  font-size: 3rem;
  text-align: center;
  margin-bottom: 3rem;
  text-transform: uppercase;
  letter-spacing: 5px;
}

.albums-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 2rem;
}

.album-card {
  background: #1a1a1a;
  border-radius: 10px;
  padding: 2rem;
  transition: transform 0.3s ease;
}

.album-card:hover {
  transform: translateY(-10px);
}

.album-card img {
  width: 100%;
  border-radius: 5px;
  margin-bottom: 1rem;
}

.album-card h3 {
  font-size: 1.5rem;
  margin-bottom: 0.5rem;
}

.year {
  color: #4a90e2;
  font-weight: bold;
  margin-bottom: 1rem;
}

.description {
  color: #888;
  margin-bottom: 1.5rem;
}

.tracks h4 {
  font-size: 1.1rem;
  margin-bottom: 1.5rem;
  color: #4a90e2;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.track-item {
  margin-bottom: 1.5rem;
  padding: 1rem;
  background: rgba(255, 255, 255, 0.03);
  border-radius: 8px;
  border: 1px solid rgba(255, 255, 255, 0.05);
  transition: all 0.3s ease;
}

.track-item:hover {
  background: rgba(255, 255, 255, 0.05);
  border-color: rgba(74, 144, 226, 0.3);
}

.track-header {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 0.75rem;
}

.track-number {
  font-size: 0.9rem;
  color: #4a90e2;
  font-weight: bold;
  min-width: 30px;
}

.track-title {
  flex: 1;
  font-size: 1rem;
  color: #fff;
  font-weight: 500;
}

.track-duration {
  font-size: 0.9rem;
  color: #888;
}

.track-audio {
  margin-top: 0.75rem;
}

.audio-player {
  width: 100%;
  height: 40px;
  border-radius: 8px;
  background: rgba(0, 0, 0, 0.3);
}

.audio-player::-webkit-media-controls-panel {
  background: linear-gradient(135deg, rgba(74, 144, 226, 0.2) 0%, rgba(53, 122, 189, 0.2) 100%);
}

.track-spotify {
  margin-top: 0.75rem;
}

.track-spotify :deep(iframe) {
  width: 100%;
  border-radius: 8px;
}

.track-no-audio {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.75rem;
  background: rgba(255, 193, 7, 0.1);
  border: 1px solid rgba(255, 193, 7, 0.3);
  border-radius: 6px;
  color: #ffc107;
  font-size: 0.9rem;
  margin-top: 0.75rem;
}

.track-no-audio i {
  font-size: 1rem;
}

/* Bouton Écouter dans chaque carte */
.btn-listen {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  margin-top: 1.5rem;
  padding: 0.75rem 1.5rem;
  background: linear-gradient(135deg, #4a90e2 0%, #357abd 100%);
  color: #fff;
  text-decoration: none;
  border-radius: 25px;
  font-weight: 600;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(74, 144, 226, 0.3);
  width: 100%;
  justify-content: center;
}

.btn-listen:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(74, 144, 226, 0.5);
  background: linear-gradient(135deg, #357abd 0%, #2868a8 100%);
}

.btn-listen i {
  font-size: 1.2rem;
}

/* Section Voir toutes les musiques */
.view-all-music {
  margin-top: 4rem;
  padding-top: 3rem;
  border-top: 2px solid rgba(74, 144, 226, 0.2);
  text-align: center;
}

.btn-view-all-music {
  display: inline-flex;
  align-items: center;
  gap: 0.75rem;
  padding: 1.25rem 3rem;
  background: linear-gradient(135deg, #22c55e 0%, #16a34a 100%);
  color: #fff;
  text-decoration: none;
  border-radius: 35px;
  font-size: 1.2rem;
  font-weight: 700;
  transition: all 0.3s ease;
  box-shadow: 0 6px 25px rgba(34, 197, 94, 0.4);
  text-transform: uppercase;
  letter-spacing: 1px;
}

.btn-view-all-music:hover {
  transform: translateY(-4px);
  box-shadow: 0 10px 35px rgba(34, 197, 94, 0.6);
  background: linear-gradient(135deg, #16a34a 0%, #15803d 100%);
}

.btn-view-all-music i {
  font-size: 1.5rem;
  animation: spin 3s linear infinite;
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

/* Responsive */
@media (max-width: 768px) {
  .discography-section {
    padding: 4rem 1rem;
  }

  .section-title {
    font-size: 2rem;
    letter-spacing: 3px;
  }

  .albums-grid {
    grid-template-columns: 1fr;
  }

  .btn-view-all-music {
    padding: 1rem 2rem;
    font-size: 1rem;
  }

  .btn-listen {
    font-size: 0.9rem;
    padding: 0.625rem 1.25rem;
  }

  .track-item {
    padding: 0.75rem;
  }

  .track-header {
    flex-wrap: wrap;
  }

  .track-number {
    min-width: 20px;
  }

  .track-title {
    font-size: 0.9rem;
  }

  .track-no-audio {
    font-size: 0.85rem;
    padding: 0.5rem;
  }
}
</style>
