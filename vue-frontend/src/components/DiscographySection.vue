<script setup>
import { ref, onMounted } from 'vue'

const albums = ref([])
const loading = ref(true)

const fetchDiscography = async () => {
  try {
    const response = await fetch('/api/public/discography')
    albums.value = await response.json()
  } catch (err) {
    console.error('Error fetching discography:', err)
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

      <div v-else class="albums-grid">
        <div v-for="album in albums" :key="album.id" class="album-card">
          <img :src="album.coverUrl" :alt="album.title" />
          <h3>{{ album.title }}</h3>
          <p class="year">{{ album.year }}</p>
          <p class="description">{{ album.description }}</p>

          <div v-if="album.tracks && album.tracks.length" class="tracks">
            <h4>Morceaux</h4>
            <div v-for="track in album.tracks" :key="track.id" class="track">
              <span>{{ track.title }}</span>
            </div>
          </div>
        </div>
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
  margin-bottom: 1rem;
  color: #4a90e2;
}

.track {
  padding: 0.5rem 0;
  border-bottom: 1px solid #333;
  color: #ccc;
}
</style>
