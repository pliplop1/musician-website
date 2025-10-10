<script setup>
import { ref, onMounted } from 'vue'

const heroData = ref(null)
const loading = ref(true)
const error = ref(null)

// Fetch hero data from API
const fetchHeroData = async () => {
  try {
    const response = await fetch('/api/public/hero')
    if (!response.ok) throw new Error('Failed to fetch hero data')
    heroData.value = await response.json()
  } catch (err) {
    error.value = err.message
    console.error('Error fetching hero data:', err)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchHeroData()
})
</script>

<template>
  <section class="hero-section">
    <!-- Video Background (placeholder for now) -->
    <div class="hero-background">
      <div class="hero-overlay"></div>
    </div>

    <!-- Hero Content -->
    <div class="hero-content">
      <div v-if="loading" class="loading">
        <p>Chargement...</p>
      </div>

      <div v-else-if="error" class="error">
        <p>Erreur: {{ error }}</p>
      </div>

      <div v-else-if="heroData" class="hero-text">
        <h1 class="hero-title">{{ heroData.artistName }}</h1>
        <p class="hero-tagline">{{ heroData.tagline }}</p>

        <!-- Latest Release -->
        <div v-if="heroData.latestRelease" class="latest-release">
          <h3>Dernier Album</h3>
          <div class="release-card">
            <img :src="heroData.latestRelease.coverUrl" :alt="heroData.latestRelease.title" />
            <div class="release-info">
              <h4>{{ heroData.latestRelease.title }}</h4>
              <div class="release-links">
                <a v-if="heroData.latestRelease.spotifyUrl"
                   :href="heroData.latestRelease.spotifyUrl"
                   target="_blank"
                   class="btn-stream">
                  Écouter sur Spotify
                </a>
                <a v-if="heroData.latestRelease.appleMusicUrl"
                   :href="heroData.latestRelease.appleMusicUrl"
                   target="_blank"
                   class="btn-stream">
                  Apple Music
                </a>
              </div>
            </div>
          </div>
        </div>

        <!-- Scroll Indicator -->
        <div class="scroll-indicator">
          <span>Découvrir</span>
          <div class="arrow-down"></div>
        </div>
      </div>
    </div>
  </section>
</template>

<style scoped>
.hero-section {
  position: relative;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.hero-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.hero-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.4);
}

.hero-content {
  position: relative;
  z-index: 10;
  text-align: center;
  padding: 2rem;
  max-width: 1200px;
  margin: 0 auto;
}

.hero-text {
  animation: fadeInUp 1s ease-out;
}

.hero-title {
  font-size: 5rem;
  font-weight: 900;
  margin-bottom: 1rem;
  letter-spacing: 10px;
  text-shadow: 2px 2px 20px rgba(0, 0, 0, 0.5);
}

.hero-tagline {
  font-size: 1.5rem;
  margin-bottom: 3rem;
  opacity: 0.9;
  letter-spacing: 2px;
}

/* Latest Release */
.latest-release {
  margin: 3rem auto;
  max-width: 500px;
}

.latest-release h3 {
  font-size: 1.2rem;
  margin-bottom: 1.5rem;
  text-transform: uppercase;
  letter-spacing: 3px;
}

.release-card {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border-radius: 15px;
  padding: 1.5rem;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
}

.release-card img {
  width: 100%;
  max-width: 300px;
  border-radius: 10px;
  margin-bottom: 1.5rem;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.5);
}

.release-info h4 {
  font-size: 1.5rem;
  margin-bottom: 1rem;
}

.release-links {
  display: flex;
  gap: 1rem;
  justify-content: center;
  flex-wrap: wrap;
}

.btn-stream {
  padding: 0.75rem 1.5rem;
  background: #1db954;
  color: white;
  text-decoration: none;
  border-radius: 25px;
  font-weight: bold;
  transition: all 0.3s ease;
}

.btn-stream:hover {
  background: #1ed760;
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(29, 185, 84, 0.4);
}

/* Scroll Indicator */
.scroll-indicator {
  position: absolute;
  bottom: 2rem;
  left: 50%;
  transform: translateX(-50%);
  text-align: center;
  animation: bounce 2s infinite;
}

.scroll-indicator span {
  display: block;
  margin-bottom: 0.5rem;
  font-size: 0.9rem;
  letter-spacing: 2px;
  text-transform: uppercase;
}

.arrow-down {
  width: 30px;
  height: 30px;
  border-left: 2px solid #fff;
  border-bottom: 2px solid #fff;
  transform: rotate(-45deg);
  margin: 0 auto;
}

/* Loading & Error States */
.loading, .error {
  font-size: 1.5rem;
}

.error {
  color: #ff6b6b;
}

/* Animations */
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

@keyframes bounce {
  0%, 20%, 50%, 80%, 100% {
    transform: translateX(-50%) translateY(0);
  }
  40% {
    transform: translateX(-50%) translateY(-10px);
  }
  60% {
    transform: translateX(-50%) translateY(-5px);
  }
}

/* Responsive */
@media (max-width: 768px) {
  .hero-title {
    font-size: 3rem;
    letter-spacing: 5px;
  }

  .hero-tagline {
    font-size: 1.2rem;
  }

  .release-card {
    padding: 1rem;
  }
}
</style>
