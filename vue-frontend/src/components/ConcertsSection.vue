<script setup>
import { ref, onMounted } from 'vue'

const concerts = ref([])
const loading = ref(true)

const fetchConcerts = async () => {
  try {
    const response = await fetch('http://localhost:8106/api/public/concerts/upcoming')
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`)
    }
    concerts.value = await response.json()
  } catch (err) {
    console.error('Error fetching concerts:', err)
    concerts.value = []
  } finally {
    loading.value = false
  }
}

const formatDate = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleDateString('fr-FR', {
    day: 'numeric',
    month: 'long',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

onMounted(() => {
  fetchConcerts()
})
</script>

<template>
  <section class="concerts-section" aria-labelledby="concerts-heading">
    <div class="container">
      <h2 id="concerts-heading" class="section-title">Prochains Concerts</h2>

      <div v-if="loading" class="loading" role="status" aria-live="polite">Chargement des concerts...</div>

      <div v-else-if="concerts.length === 0" class="no-concerts" role="status">
        <p>Aucun concert prévu pour le moment. Revenez bientôt!</p>
      </div>

      <div v-else class="concerts-list" role="list">
        <article v-for="concert in concerts" :key="concert.id" class="concert-card" role="listitem">
          <div class="concert-date" aria-label="Date du concert">
            <div class="date-day" aria-label="Jour">{{ new Date(concert.date).getDate() }}</div>
            <div class="date-month" aria-label="Mois">{{ new Date(concert.date).toLocaleDateString('fr-FR', { month: 'short' }) }}</div>
          </div>
          <div class="concert-info">
            <h3>{{ concert.location }}</h3>
            <p class="venue">{{ concert.venue }}</p>
            <p class="description">{{ concert.description }}</p>
            <p v-if="concert.daysUntil !== null" class="countdown">
              Dans {{ concert.daysUntil }} jour{{ concert.daysUntil > 1 ? 's' : '' }}
            </p>
          </div>
          <div class="concert-action">
            <a v-if="concert.ticketUrl" :href="concert.ticketUrl" target="_blank" rel="noopener noreferrer" class="btn-ticket" :aria-label="`Acheter des billets pour le concert à ${concert.location} (ouvre dans un nouvel onglet)`">
              Billets
            </a>
          </div>
        </article>
      </div>
    </div>
  </section>
</template>

<style scoped>
.concerts-section {
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

.no-concerts {
  text-align: center;
  font-size: 1.2rem;
  color: #888;
  padding: 3rem;
}

.concerts-list {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.concert-card {
  display: flex;
  align-items: center;
  gap: 2rem;
  background: #1a1a1a;
  border-radius: 10px;
  padding: 2rem;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.concert-card:hover {
  transform: translateX(10px);
  box-shadow: 0 8px 30px rgba(74, 144, 226, 0.3);
}

.concert-date {
  text-align: center;
  min-width: 80px;
}

.date-day {
  font-size: 3rem;
  font-weight: bold;
  color: #4a90e2;
  line-height: 1;
}

.date-month {
  font-size: 1.2rem;
  text-transform: uppercase;
  color: #888;
}

.concert-info {
  flex: 1;
}

.concert-info h3 {
  font-size: 1.8rem;
  margin-bottom: 0.5rem;
}

.venue {
  color: #4a90e2;
  margin-bottom: 0.5rem;
}

.description {
  color: #888;
  margin-bottom: 0.5rem;
}

.countdown {
  color: #1db954;
  font-weight: bold;
}

.concert-action {
  min-width: 120px;
  text-align: center;
}

.btn-ticket {
  display: inline-block;
  padding: 0.75rem 1.5rem;
  background: #4a90e2;
  color: white;
  text-decoration: none;
  border-radius: 25px;
  font-weight: bold;
  transition: all 0.3s ease;
}

.btn-ticket:hover {
  background: #357abd;
  transform: scale(1.05);
}

@media (max-width: 768px) {
  .concert-card {
    flex-direction: column;
    text-align: center;
  }
}
</style>
