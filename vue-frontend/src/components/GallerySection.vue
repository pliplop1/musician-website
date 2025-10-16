<script setup>
import { ref, onMounted } from 'vue'

const photos = ref([])
const loading = ref(true)

const fetchGallery = async () => {
  try {
    const response = await fetch('/api/public/gallery')
    photos.value = await response.json()
  } catch (err) {
    // Error handling
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchGallery()
})
</script>

<template>
  <section class="gallery-section">
    <div class="container">
      <h2 class="section-title">Galerie</h2>

      <div v-if="loading" class="loading">Chargement...</div>

      <div v-else class="gallery-grid" role="list" aria-label="Galerie complète">
        <div v-for="photo in photos" :key="photo.id" class="photo-item" role="listitem" tabindex="0" :aria-label="`Photo : ${photo.caption || 'Sans description'}`">
          <img :src="photo.url" :alt="photo.caption || 'Photo'" loading="lazy" decoding="async" />
          <div v-if="photo.caption" class="photo-caption">
            {{ photo.caption }}
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<style scoped>
.gallery-section {
  padding: 5rem 2rem;
  background: #111;
}

.container {
  max-width: 1400px;
  margin: 0 auto;
}

.section-title {
  font-size: 3rem;
  text-align: center;
  margin-bottom: 3rem;
  text-transform: uppercase;
  letter-spacing: 5px;
}

.gallery-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 1.5rem;
}

.photo-item {
  position: relative;
  overflow: hidden;
  border-radius: 10px;
  cursor: pointer;
  transition: transform 0.3s ease;
}

.photo-item:hover {
  transform: scale(1.05);
}

.photo-item img {
  width: 100%;
  height: 300px;
  object-fit: cover;
  display: block;
}

.photo-caption {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: rgba(0, 0, 0, 0.8);
  padding: 1rem;
  color: white;
  transform: translateY(100%);
  transition: transform 0.3s ease;
}

.photo-item:hover .photo-caption {
  transform: translateY(0);
}
</style>
