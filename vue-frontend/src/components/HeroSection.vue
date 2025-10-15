<script setup>
import { ref, onMounted } from 'vue'

const heroData = ref(null)
const loading = ref(true)
const error = ref(null)

// Fetch hero data from homepage settings API
const fetchHeroData = async () => {
  try {
    const response = await fetch('/api/public/homepage-settings')
    if (!response.ok) throw new Error('Failed to fetch hero data')
    const settings = await response.json()

    // Mapper les données de settings vers heroData
    heroData.value = {
      artistName: settings.heroTitle || 'DUO BLACK & WHITE',
      tagline: settings.heroSubtitle || 'La musique qui vous transporte',
      backgroundVideoUrl: settings.heroBackgroundVideoUrl,
      welcomeMessage: settings.welcomeMessage
    }
  } catch (err) {
    error.value = err.message
    console.error('Error fetching hero data:', err)
    // Données par défaut en cas d'erreur
    heroData.value = {
      artistName: 'DUO BLACK & WHITE',
      tagline: 'La musique qui vous transporte • France & Belgique'
    }
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
    <!-- Video Background -->
    <div class="hero-background">
      <!-- Vidéo de fond si définie par l'admin -->
      <video
        v-if="heroData && heroData.backgroundVideoUrl"
        autoplay
        muted
        loop
        playsinline
        preload="metadata"
        aria-label="Vidéo d'ambiance montrant une performance du Duo Black & White"
        class="hero-video">
        <source :src="heroData.backgroundVideoUrl" type="video/mp4">
      </video>
      <!-- Overlay pour améliorer la lisibilité -->
      <div class="hero-overlay"></div>
    </div>

    <!-- Hero Content -->
    <div class="hero-content">
      <div v-if="loading" class="loading">
        <i class="fas fa-spinner fa-spin"></i>
        <p>Chargement...</p>
      </div>

      <div v-else-if="error" class="error">
        <i class="fas fa-exclamation-triangle"></i>
        <p>{{ error }}</p>
      </div>

      <div v-else-if="heroData" class="hero-text">
        <h1 class="hero-title">{{ heroData.artistName }}</h1>
        <p class="hero-tagline">{{ heroData.tagline }}</p>

        <!-- Message d'accueil personnalisé -->
        <div v-if="heroData.welcomeMessage" class="welcome-message">
          <p>{{ heroData.welcomeMessage }}</p>
        </div>

        <!-- CTA Buttons -->
        <div class="hero-actions">
          <a href="#videos" class="btn btn-primary" aria-label="Accéder à la section vidéos">
            <i class="fas fa-play-circle" aria-hidden="true"></i> Découvrir nos vidéos
          </a>
          <a href="#music" class="btn btn-secondary" aria-label="Accéder à la section musique">
            <i class="fas fa-music" aria-hidden="true"></i> Écouter notre musique
          </a>
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

.hero-video {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  z-index: 0;
}

.hero-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  z-index: 1;
}

.hero-content {
  position: relative;
  z-index: 10;
  text-align: center;
  padding: 2rem;
  max-width: 1200px;
  margin: 0 auto;
}

.welcome-message {
  margin: 2rem auto;
  max-width: 700px;
  padding: 1.5rem;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border-radius: 15px;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.welcome-message p {
  margin: 0;
  font-size: 1.1rem;
  line-height: 1.6;
  color: #e0e0e0;
}

.hero-actions {
  display: flex;
  gap: 1rem;
  justify-content: center;
  margin-top: 2rem;
  flex-wrap: wrap;
}

.btn {
  padding: 0.9rem 2rem;
  border-radius: 30px;
  text-decoration: none;
  font-weight: 600;
  font-size: 1rem;
  transition: all 0.3s ease;
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  border: none;
  cursor: pointer;
}

.btn i {
  font-size: 1.1rem;
}

.btn-primary {
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
  color: white;
  box-shadow: 0 4px 15px rgba(239, 68, 68, 0.4);
}

.btn-primary:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 20px rgba(239, 68, 68, 0.6);
}

.btn-secondary {
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(10px);
  color: white;
  border: 2px solid rgba(255, 255, 255, 0.3);
}

.btn-secondary:hover {
  background: rgba(255, 255, 255, 0.25);
  border-color: rgba(255, 255, 255, 0.5);
  transform: translateY(-3px);
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
