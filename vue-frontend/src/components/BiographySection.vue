<script setup>
import { ref, onMounted } from 'vue'

const biography = ref(null)
const loading = ref(true)

const fetchBiography = async () => {
  try {
    const response = await fetch('/api/public/biography')
    biography.value = await response.json()
  } catch (err) {
    console.error('Error fetching biography:', err)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchBiography()
})
</script>

<template>
  <section class="biography-section">
    <div class="container">
      <h2 class="section-title">Biographie</h2>

      <div v-if="loading" class="loading">Chargement...</div>

      <div v-else-if="biography" class="biography-content">
        <div class="bio-text">
          <p>{{ biography.content }}</p>
        </div>

        <div v-if="biography.timeline && biography.timeline.length" class="timeline">
          <h3>Parcours</h3>
          <div v-for="event in biography.timeline" :key="event.year" class="timeline-event">
            <div class="timeline-year">{{ event.year }}</div>
            <div class="timeline-info">
              <h4>{{ event.title }}</h4>
              <p>{{ event.description }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<style scoped>
.biography-section {
  padding: 5rem 2rem;
  background: #111;
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

.biography-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 3rem;
}

.bio-text p {
  font-size: 1.1rem;
  line-height: 1.8;
  color: #ccc;
}

.timeline h3 {
  font-size: 1.5rem;
  margin-bottom: 2rem;
  color: #4a90e2;
}

.timeline-event {
  display: flex;
  gap: 2rem;
  margin-bottom: 2rem;
  padding-bottom: 2rem;
  border-bottom: 1px solid #333;
}

.timeline-year {
  font-size: 2rem;
  font-weight: bold;
  color: #4a90e2;
  min-width: 80px;
}

.timeline-info h4 {
  font-size: 1.2rem;
  margin-bottom: 0.5rem;
}

.timeline-info p {
  color: #888;
}

@media (max-width: 768px) {
  .biography-content {
    grid-template-columns: 1fr;
  }
}
</style>
